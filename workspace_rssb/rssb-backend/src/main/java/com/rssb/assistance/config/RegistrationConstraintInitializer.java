package com.rssb.assistance.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class RegistrationConstraintInitializer implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationConstraintInitializer.class);

    private final JdbcTemplate jdbcTemplate;

    public RegistrationConstraintInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        refreshAppUserRoleCheckConstraint();
        dropLocalDeviceRecordUniqueConstraint();
        refreshRegistrationCombinationUniqueConstraint();
        refreshSyncStatusCheckConstraint();
    }

    private void refreshAppUserRoleCheckConstraint() {
        String sql = """
                DO $$
                BEGIN
                  IF EXISTS (
                    SELECT 1
                    FROM information_schema.table_constraints tc
                    WHERE tc.table_name = 'app_users'
                      AND tc.constraint_type = 'CHECK'
                      AND tc.constraint_name = 'app_users_role_check'
                  ) THEN
                    ALTER TABLE app_users DROP CONSTRAINT app_users_role_check;
                  END IF;

                  ALTER TABLE app_users
                    ADD CONSTRAINT app_users_role_check
                    CHECK (role IN ('ROLE_USER', 'ROLE_ADMIN', 'ROLE_VISITOR'));
                END $$;
                """;

        jdbcTemplate.execute(sql);
    }

    private void dropLocalDeviceRecordUniqueConstraint() {
        String sql = """
                DO $$
                DECLARE c_name text;
                BEGIN
                  SELECT tc.constraint_name
                    INTO c_name
                  FROM information_schema.table_constraints tc
                  JOIN information_schema.constraint_column_usage ccu
                    ON tc.constraint_name = ccu.constraint_name
                   AND tc.table_schema = ccu.table_schema
                  WHERE tc.table_name = 'person_registrations'
                    AND tc.constraint_type = 'UNIQUE'
                    AND ccu.column_name = 'local_device_record_id'
                  LIMIT 1;

                  IF c_name IS NOT NULL THEN
                    EXECUTE format('ALTER TABLE person_registrations DROP CONSTRAINT %I', c_name);
                  END IF;
                END $$;
                """;

        jdbcTemplate.execute(sql);
    }

    private void refreshRegistrationCombinationUniqueConstraint() {
        String sql = """
                DO $$
                DECLARE c_name text;
                BEGIN
                  SELECT tc.constraint_name
                    INTO c_name
                  FROM information_schema.table_constraints tc
                  JOIN information_schema.constraint_column_usage ccu
                    ON tc.constraint_name = ccu.constraint_name
                   AND tc.table_schema = ccu.table_schema
                  WHERE tc.table_name = 'person_registrations'
                    AND tc.constraint_type = 'UNIQUE'
                    AND ccu.column_name = 'phone_number'
                  LIMIT 1;

                  IF c_name IS NOT NULL THEN
                    EXECUTE format('ALTER TABLE person_registrations DROP CONSTRAINT %I', c_name);
                  END IF;

                  IF NOT EXISTS (
                    SELECT 1
                    FROM pg_indexes
                    WHERE schemaname = current_schema()
                      AND indexname = 'uq_person_registrations_identity_combo'
                  ) THEN
                    EXECUTE '
                      CREATE UNIQUE INDEX uq_person_registrations_identity_combo
                      ON person_registrations (
                        lower(trim(person_name)),
                        phone_number,
                        coalesce(lower(trim(vehicle_number)), ''''),
                        coalesce(parking_location_id, -1)
                      )';
                  END IF;
                EXCEPTION
                  WHEN unique_violation THEN
                    RAISE NOTICE 'Duplicate registration combinations exist. Unique index not added.';
                END $$;
                """;

        try {
            jdbcTemplate.execute(sql);
        } catch (Exception ex) {
            LOGGER.warn("Could not add unique registration combination index", ex);
        }
    }

    private void refreshSyncStatusCheckConstraint() {
        String sql = """
                DO $$
                DECLARE c_name text;
                BEGIN
                  SELECT tc.constraint_name
                    INTO c_name
                  FROM information_schema.table_constraints tc
                  WHERE tc.table_name = 'person_registrations'
                    AND tc.constraint_type = 'CHECK'
                    AND tc.constraint_name = 'person_registrations_sync_status_check'
                  LIMIT 1;

                  IF c_name IS NOT NULL THEN
                    EXECUTE format('ALTER TABLE person_registrations DROP CONSTRAINT %I', c_name);
                  END IF;

                  ALTER TABLE person_registrations
                    ADD CONSTRAINT person_registrations_sync_status_check
                    CHECK (sync_status IN ('PENDING', 'SEARCHING', 'COMPLETE', 'SYNCED', 'FAILED'));
                END $$;
                """;

        jdbcTemplate.execute(sql);
    }
}

package com.rssb.assistance.repository;

import com.rssb.assistance.entity.PersonRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PersonRegistrationRepository
        extends JpaRepository<PersonRegistration, Long>, JpaSpecificationExecutor<PersonRegistration> {
        boolean existsByPhoneNumber(String phoneNumber);

        boolean existsByPhoneNumberAndRegistrationIdNot(String phoneNumber, Long registrationId);

        @Query("""
                select case when count(pr) > 0 then true else false end
                from PersonRegistration pr
                where lower(trim(pr.personName)) = lower(trim(:personName))
                  and pr.phoneNumber = :phoneNumber
                  and coalesce(lower(trim(pr.vehicleNumber)), '') = coalesce(lower(trim(:vehicleNumber)), '')
                  and coalesce(pr.parkingLocation.id, -1) = coalesce(:parkingLocationId, -1)
                  and (:excludeId is null or pr.registrationId <> :excludeId)
                """)
        boolean existsByRegistrationCombination(@Param("personName") String personName,
                                                @Param("phoneNumber") String phoneNumber,
                                                @Param("vehicleNumber") String vehicleNumber,
                                                @Param("parkingLocationId") Long parkingLocationId,
                                                @Param("excludeId") Long excludeId);
}

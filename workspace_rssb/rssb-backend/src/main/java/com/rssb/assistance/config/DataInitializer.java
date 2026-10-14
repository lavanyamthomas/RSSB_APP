package com.rssb.assistance.config;

import com.rssb.assistance.entity.AppUser;
import com.rssb.assistance.entity.CompletedRecord;
import com.rssb.assistance.entity.ParkingLocation;
import com.rssb.assistance.entity.Person;
import com.rssb.assistance.entity.Role;
import com.rssb.assistance.repository.AppUserRepository;
import com.rssb.assistance.repository.CompletedRecordRepository;
import com.rssb.assistance.repository.ParkingLocationRepository;
import com.rssb.assistance.repository.PersonRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Order(2)
public class DataInitializer implements CommandLineRunner {

    private final AppUserRepository appUserRepository;
    private final PersonRepository personRepository;
    private final CompletedRecordRepository completedRecordRepository;
    private final ParkingLocationRepository parkingLocationRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(AppUserRepository appUserRepository,
                           PersonRepository personRepository,
                           CompletedRecordRepository completedRecordRepository,
                           ParkingLocationRepository parkingLocationRepository,
                           PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.personRepository = personRepository;
        this.completedRecordRepository = completedRecordRepository;
        this.parkingLocationRepository = parkingLocationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedUsers();
        seedParkingLocations();
        seedDomainData();
    }

    private void seedUsers() {
        if (!appUserRepository.existsByUsername("admin")) {
            AppUser admin = new AppUser();
            admin.setUsername("admin");
            admin.setFullName("System Admin");
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setRole(Role.ROLE_ADMIN);
            appUserRepository.save(admin);
        }

        if (!appUserRepository.existsByUsername("sewadar")) {
            AppUser user = new AppUser();
            user.setUsername("sewadar");
            user.setFullName("Default Sewadar");
            user.setPassword(passwordEncoder.encode("Sewadar@123"));
            user.setRole(Role.ROLE_USER);
            appUserRepository.save(user);
        }

        if (!appUserRepository.existsByUsername("visitor")) {
            AppUser visitor = new AppUser();
            visitor.setUsername("visitor");
            visitor.setFullName("Default Visitor");
            visitor.setPassword(passwordEncoder.encode("Visitor@123"));
            visitor.setRole(Role.ROLE_VISITOR);
            appUserRepository.save(visitor);
        }
    }

    private void seedParkingLocations() {
        if (parkingLocationRepository.count() == 0) {
            String[][] locations = {
                {"North Gate Parking", "Parking area near the north entrance"},
                {"South Gate Parking", "Parking area near the south entrance"},
                {"Main Hall Parking", "Parking area adjacent to the main hall"},
                {"East Wing Parking", "Overflow parking on the east side"},
                {"VIP Parking", "Reserved parking for special guests"}
            };
            for (String[] loc : locations) {
                ParkingLocation pl = new ParkingLocation();
                pl.setName(loc[0]);
                pl.setDescription(loc[1]);
                parkingLocationRepository.save(pl);
            }
        }
    }

    private void seedDomainData() {
        if (personRepository.count() == 0) {
            Person p1 = new Person();
            p1.setPersonCode("RSSB-2031");
            p1.setFullName("Sevak Rajan");
            p1.setMobile("9876543210");
            p1.setCity("North Gate");
            p1.setGender("male");
            p1.setLocation("north-gate");
            p1.setStatus("Online");
            personRepository.save(p1);

            Person p2 = new Person();
            p2.setPersonCode("RSSB-1887");
            p2.setFullName("Sevika Meera");
            p2.setMobile("9876500000");
            p2.setCity("Main Hall");
            p2.setGender("female");
            p2.setLocation("main-hall");
            p2.setStatus("Last seen 5m ago");
            personRepository.save(p2);
        }

        if (completedRecordRepository.count() == 0) {
            saveRecord("REC-1001", "Registration Completed", "Registration workflow closed", LocalDateTime.now().minusMinutes(35));
            saveRecord("REC-1002", "Search Request Closed", "Search task completed", LocalDateTime.now().minusMinutes(20));
            saveRecord("REC-1003", "Voice Alert Resolved", "Walkie alert acknowledged", LocalDateTime.now().minusMinutes(7));
        }
    }

    private void saveRecord(String code, String title, String description, LocalDateTime completedAt) {
        CompletedRecord record = new CompletedRecord();
        record.setRecordCode(code);
        record.setTitle(title);
        record.setDescription(description);
        record.setCompletedAt(completedAt);
        completedRecordRepository.save(record);
    }
}

package com.rssb.assistance.service;

import com.rssb.assistance.dto.CompletedRecordDto;
import com.rssb.assistance.dto.PersonDto;
import com.rssb.assistance.dto.RegistrationRequest;
import com.rssb.assistance.dto.RegistrationResponse;
import com.rssb.assistance.dto.WalkieTransmitRequest;
import com.rssb.assistance.dto.WalkieTransmitResponse;
import com.rssb.assistance.entity.CompletedRecord;
import com.rssb.assistance.entity.Person;
import com.rssb.assistance.entity.WalkieTransmission;
import com.rssb.assistance.repository.CompletedRecordRepository;
import com.rssb.assistance.repository.PersonRepository;
import com.rssb.assistance.repository.WalkieTransmissionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class RssbDataService {

    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final PersonRepository personRepository;
    private final CompletedRecordRepository completedRecordRepository;
    private final WalkieTransmissionRepository walkieTransmissionRepository;

    public RssbDataService(PersonRepository personRepository,
                           CompletedRecordRepository completedRecordRepository,
                           WalkieTransmissionRepository walkieTransmissionRepository) {
        this.personRepository = personRepository;
        this.completedRecordRepository = completedRecordRepository;
        this.walkieTransmissionRepository = walkieTransmissionRepository;
    }

    public RegistrationResponse createRegistration(RegistrationRequest request) {
        String personCode = "RSSB-" + (2000 + ThreadLocalRandom.current().nextInt(1000, 9999));

        Person person = new Person();
        person.setPersonCode(personCode);
        person.setFullName(request.getFullName());
        person.setMobile(request.getMobile());
        person.setCity(request.getCity());
        person.setGender(request.getGender());
        person.setLocation(normalizeLocation(request.getCity()));
        person.setStatus("Online");
        personRepository.save(person);

        saveCompletedRecord("Registration Completed", "Registration for " + request.getFullName() + " completed");

        return new RegistrationResponse(personCode, "Registration created successfully");
    }

    public Page<PersonDto> searchPeople(String query, String location, String status, Pageable pageable) {
        Specification<Person> spec = Specification.where(null);

        if (query != null && !query.isBlank()) {
            String q = "%" + query.trim().toLowerCase(Locale.ROOT) + "%";
            spec = spec.and((root, cq, cb) -> cb.or(
                    cb.like(cb.lower(root.get("fullName")), q),
                    cb.like(cb.lower(root.get("personCode")), q),
                    cb.like(cb.lower(root.get("mobile")), q)
            ));
        }

        if (location != null && !location.isBlank() && !"all".equalsIgnoreCase(location)) {
            spec = spec.and((root, cq, cb) -> cb.equal(cb.lower(root.get("location")), location.toLowerCase(Locale.ROOT)));
        }

        if (status != null && !status.isBlank()) {
            String s = "%" + status.trim().toLowerCase(Locale.ROOT) + "%";
            spec = spec.and((root, cq, cb) -> cb.like(cb.lower(root.get("status")), s));
        }

        return personRepository.findAll(spec, pageable).map(this::toPersonDto);
    }

    public Page<CompletedRecordDto> getCompletedRecords(LocalDate dateFrom,
                                                        LocalDate dateTo,
                                                        String title,
                                                        Pageable pageable) {
        Specification<CompletedRecord> spec = Specification.where(null);

        if (dateFrom != null) {
            spec = spec.and((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("completedAt"), dateFrom.atStartOfDay()));
        }
        if (dateTo != null) {
            spec = spec.and((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("completedAt"), dateTo.plusDays(1).atStartOfDay().minusNanos(1)));
        }
        if (title != null && !title.isBlank()) {
            String t = "%" + title.trim().toLowerCase(Locale.ROOT) + "%";
            spec = spec.and((root, cq, cb) -> cb.like(cb.lower(root.get("title")), t));
        }

        return completedRecordRepository.findAll(spec, pageable).map(this::toCompletedRecordDto);
    }

    public WalkieTransmitResponse transmit(WalkieTransmitRequest request) {
        LocalDateTime now = LocalDateTime.now();
        String transmissionCode = "TX-" + System.currentTimeMillis();

        WalkieTransmission transmission = new WalkieTransmission();
        transmission.setTransmissionCode(transmissionCode);
        transmission.setChannel(request.getChannel());
        transmission.setMessage(request.getMessage());
        transmission.setStatus("Transmitted");
        transmission.setTransmittedAt(now);
        walkieTransmissionRepository.save(transmission);

        saveCompletedRecord("Voice Alert Resolved", "Walkie message sent to " + request.getChannel());

        return new WalkieTransmitResponse(
                transmissionCode,
                request.getChannel(),
                now.format(DATE_TIME_FORMAT),
                "Transmitted"
        );
    }

    private PersonDto toPersonDto(Person person) {
        return new PersonDto(person.getPersonCode(), person.getFullName(), person.getLocation(), person.getStatus());
    }

    private CompletedRecordDto toCompletedRecordDto(CompletedRecord record) {
        return new CompletedRecordDto(
                record.getRecordCode(),
                record.getTitle(),
                Optional.ofNullable(record.getCompletedAt()).map(dt -> dt.format(DATE_TIME_FORMAT)).orElse("")
        );
    }

    private void saveCompletedRecord(String title, String description) {
        CompletedRecord record = new CompletedRecord();
        record.setRecordCode("REC-" + System.currentTimeMillis());
        record.setTitle(title);
        record.setDescription(description);
        record.setCompletedAt(LocalDateTime.now());
        completedRecordRepository.save(record);
    }

    private String normalizeLocation(String city) {
        if (city == null || city.isBlank()) {
            return "main-hall";
        }
        return city.trim().toLowerCase(Locale.ROOT).replace(' ', '-');
    }
}

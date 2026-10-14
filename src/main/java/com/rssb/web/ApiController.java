package com.rssb.web;

import com.rssb.dto.Dtos.*;
import com.rssb.model.*;
import com.rssb.repo.*;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApiController {
    private static final Set<String> PERSON_SORT = Set.of("id", "name", "location", "status", "createdAt");
    private static final Set<String> RECORD_SORT = Set.of("id", "title", "completedAt");

    private final PersonRepository persons;
    private final CompletedRecordRepository records;
    private final WalkieMessageRepository walkie;

    public ApiController(PersonRepository persons, CompletedRecordRepository records, WalkieMessageRepository walkie) {
        this.persons = persons; this.records = records; this.walkie = walkie;
    }

    @GetMapping("/health")
    public Map<String, String> health() { return Map.of("status", "UP"); }

    @PostMapping("/registrations")
    public ResponseEntity<Person> register(@Valid @RequestBody RegistrationRequest req) {
        Person p = new Person();
        p.setName(req.name());
        p.setPhone(req.phone());
        p.setLocation(req.location());
        return ResponseEntity.status(HttpStatus.CREATED).body(persons.save(p));
    }

    @GetMapping("/persons/search")
    public Page<Person> search(@RequestParam(required = false) String q,
                               @RequestParam(required = false) String location,
                               @RequestParam(required = false) String status,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               @RequestParam(defaultValue = "createdAt") String sortBy,
                               @RequestParam(defaultValue = "desc") String sortDir) {
        Specification<Person> spec = (root, query, cb) -> {
            List<Predicate> ps = new ArrayList<>();
            if (hasText(q)) {
                String like = "%" + q.trim().toLowerCase() + "%";
                ps.add(cb.or(cb.like(cb.lower(root.get("name")), like),
                             cb.like(cb.lower(cb.coalesce(root.<String>get("phone"), "")), like)));
            }
            if (hasText(location)) {
                ps.add(cb.like(cb.lower(cb.coalesce(root.<String>get("location"), "")),
                        "%" + location.trim().toLowerCase() + "%"));
            }
            if (hasText(status)) {
                ps.add(cb.equal(cb.lower(root.get("status")), status.trim().toLowerCase()));
            }
            return cb.and(ps.toArray(new Predicate[0]));
        };
        return persons.findAll(spec, pageable(page, size, sortBy, sortDir, PERSON_SORT, "createdAt"));
    }

    @GetMapping("/records/completed")
    public Page<CompletedRecord> completed(@RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate dateFrom,
                                           @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate dateTo,
                                           @RequestParam(required = false) String title,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size,
                                           @RequestParam(defaultValue = "completedAt") String sortBy,
                                           @RequestParam(defaultValue = "desc") String sortDir) {
        Specification<CompletedRecord> spec = (root, query, cb) -> {
            List<Predicate> ps = new ArrayList<>();
            if (dateFrom != null) ps.add(cb.greaterThanOrEqualTo(root.get("completedAt"), dateFrom));
            if (dateTo != null) ps.add(cb.lessThanOrEqualTo(root.get("completedAt"), dateTo));
            if (hasText(title)) {
                ps.add(cb.like(cb.lower(root.get("title")), "%" + title.trim().toLowerCase() + "%"));
            }
            return cb.and(ps.toArray(new Predicate[0]));
        };
        return records.findAll(spec, pageable(page, size, sortBy, sortDir, RECORD_SORT, "completedAt"));
    }

    @PostMapping("/walkie/transmit")
    public ResponseEntity<WalkieMessage> transmit(@Valid @RequestBody WalkieRequest req, Authentication auth) {
        WalkieMessage m = new WalkieMessage();
        m.setSender(auth.getName());
        m.setChannel(req.channel());
        m.setMessage(req.message());
        return ResponseEntity.status(HttpStatus.CREATED).body(walkie.save(m));
    }

    private static boolean hasText(String s) { return s != null && !s.isBlank(); }

    private static PageRequest pageable(int page, int size, String sortBy, String sortDir,
                                        Set<String> allowed, String defaultSort) {
        String field = allowed.contains(sortBy) ? sortBy : defaultSort;
        Sort.Direction dir = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), Sort.by(dir, field));
    }
}

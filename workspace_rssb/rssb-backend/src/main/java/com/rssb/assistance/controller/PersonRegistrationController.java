package com.rssb.assistance.controller;

import com.rssb.assistance.dto.ParkingLocationDto;
import com.rssb.assistance.dto.PersonRegistrationRequest;
import com.rssb.assistance.dto.PersonRegistrationResponse;
import com.rssb.assistance.dto.PersonRegistrationUpdateRequest;
import com.rssb.assistance.service.PersonRegistrationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/person-registrations")
public class PersonRegistrationController {

    private final PersonRegistrationService service;

    public PersonRegistrationController(PersonRegistrationService service) {
        this.service = service;
    }

    /** Create a new person registration */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PersonRegistrationResponse create(@Valid @RequestBody PersonRegistrationRequest request) {
        return service.create(request);
    }

    /**
     * List registrations with optional filters:
     *   ?completed=true/false
     *   ?syncStatus=PENDING|SYNCED|FAILED
     */
    @GetMapping
    public Page<PersonRegistrationResponse> list(
            @RequestParam(required = false, name = "q") String query,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) String syncStatus,
            @PageableDefault(size = 20) Pageable pageable) {
        if ((query != null && !query.isBlank())
                || (location != null && !location.isBlank())
                || completed != null
                || syncStatus != null) {
            return service.search(query, location, completed, syncStatus, pageable);
        }
        return service.getAll(pageable);
    }

    /** Get a single registration by ID */
    @GetMapping("/{id}")
    public PersonRegistrationResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    /** Update editable fields on a registration */
    @PutMapping("/{id}")
    public PersonRegistrationResponse update(@PathVariable Long id,
                                             @RequestBody PersonRegistrationUpdateRequest request) {
        return service.update(id, request);
    }

    /** Mark a registration as completed */
    @PatchMapping("/{id}/complete")
    public PersonRegistrationResponse complete(@PathVariable Long id) {
        return service.markComplete(id);
    }

    /** Delete a registration */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    /** List all parking locations (for dropdown) */
    @GetMapping("/parking-locations")
    public List<ParkingLocationDto> parkingLocations() {
        return service.getParkingLocations();
    }
}

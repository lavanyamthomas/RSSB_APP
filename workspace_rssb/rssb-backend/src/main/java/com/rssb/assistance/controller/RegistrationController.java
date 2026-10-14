package com.rssb.assistance.controller;

import com.rssb.assistance.dto.RegistrationRequest;
import com.rssb.assistance.dto.RegistrationResponse;
import com.rssb.assistance.service.RssbDataService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/registrations")
public class RegistrationController {

    private final RssbDataService rssbDataService;

    public RegistrationController(RssbDataService rssbDataService) {
        this.rssbDataService = rssbDataService;
    }

    @PostMapping
    public RegistrationResponse create(@Valid @RequestBody RegistrationRequest request) {
        return rssbDataService.createRegistration(request);
    }
}

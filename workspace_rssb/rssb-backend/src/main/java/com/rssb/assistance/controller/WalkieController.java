package com.rssb.assistance.controller;

import com.rssb.assistance.dto.WalkieTransmitRequest;
import com.rssb.assistance.dto.WalkieTransmitResponse;
import com.rssb.assistance.service.RssbDataService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/walkie")
public class WalkieController {

    private final RssbDataService rssbDataService;

    public WalkieController(RssbDataService rssbDataService) {
        this.rssbDataService = rssbDataService;
    }

    @PostMapping("/transmit")
    public WalkieTransmitResponse transmit(@Valid @RequestBody WalkieTransmitRequest request) {
        return rssbDataService.transmit(request);
    }
}

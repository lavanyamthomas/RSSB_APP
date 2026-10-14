package com.rssb.assistance.controller;

import com.rssb.assistance.dto.CompletedRecordsResponse;
import com.rssb.assistance.dto.CompletedRecordDto;
import com.rssb.assistance.service.RssbDataService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/records")
public class RecordsController {

    private final RssbDataService rssbDataService;

    public RecordsController(RssbDataService rssbDataService) {
        this.rssbDataService = rssbDataService;
    }

    @GetMapping("/completed")
    public CompletedRecordsResponse completed(@RequestParam(name = "dateFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
                                              @RequestParam(name = "dateTo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
                                              @RequestParam(name = "title", required = false) String title,
                                              @RequestParam(name = "page", defaultValue = "0") int page,
                                              @RequestParam(name = "size", defaultValue = "10") int size,
                                              @RequestParam(name = "sortBy", defaultValue = "completedAt") String sortBy,
                                              @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir) {
        Sort.Direction direction = Sort.Direction.fromOptionalString(sortDir).orElse(Sort.Direction.DESC);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<CompletedRecordDto> resultPage = rssbDataService.getCompletedRecords(dateFrom, dateTo, title, pageRequest);

        return new CompletedRecordsResponse(
                resultPage.getContent(),
                resultPage.getNumber(),
                resultPage.getSize(),
                resultPage.getTotalElements(),
                resultPage.getTotalPages(),
                sortBy,
                direction.name().toLowerCase()
        );
    }
}

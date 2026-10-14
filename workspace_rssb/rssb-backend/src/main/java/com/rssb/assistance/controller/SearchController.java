package com.rssb.assistance.controller;

import com.rssb.assistance.dto.SearchResponse;
import com.rssb.assistance.service.RssbDataService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.rssb.assistance.dto.PersonDto;

@RestController
@RequestMapping("/api/persons")
public class SearchController {

    private final RssbDataService rssbDataService;

    public SearchController(RssbDataService rssbDataService) {
        this.rssbDataService = rssbDataService;
    }

    @GetMapping("/search")
    public SearchResponse search(@RequestParam(name = "q", required = false) String query,
                                 @RequestParam(name = "location", required = false) String location,
                                 @RequestParam(name = "status", required = false) String status,
                                 @RequestParam(name = "page", defaultValue = "0") int page,
                                 @RequestParam(name = "size", defaultValue = "10") int size,
                                 @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                 @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir) {
        Sort.Direction direction = Sort.Direction.fromOptionalString(sortDir).orElse(Sort.Direction.DESC);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<PersonDto> resultPage = rssbDataService.searchPeople(query, location, status, pageRequest);

        return new SearchResponse(
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

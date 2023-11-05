package com.surf.search.controller;

import com.surf.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;
    @GetMapping("")
    public Mono<String> searchGet(@RequestParam String query){
        Mono<String> searchGet = searchService.searchGet(query);

        return searchGet;

//        ApiResponse apiResponse = ApiResponse.builder()
//                .message("검색 결과")
//                .status(HttpStatus.OK.value())
//                .data(searchGet)
//                .build();
//
//        return ResponseEntity.ok(apiResponse);
    }
}

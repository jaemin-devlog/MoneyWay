package com.example.moneyway.place.controller;

import com.example.moneyway.place.dto.PlaceSearchResultDto;
import com.example.moneyway.place.service.PlaceSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Place Search", description = "장소 통합 검색 API")
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class PlaceSearchController {

    private final PlaceSearchService searchService;

    @Operation(summary = "키워드 기반 통합 검색", description = "음식점, 관광지, 숙소 등을 통합하여 검색합니다.")
    @GetMapping
    public ResponseEntity<List<PlaceSearchResultDto>> search(@RequestParam("keyword") String keyword) {
        return ResponseEntity.ok(searchService.searchPlacesByKeyword(keyword));
    }
}

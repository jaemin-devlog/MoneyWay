package com.example.moneyway.place.controller;

import com.example.moneyway.place.domain.PlaceCategory;
import com.example.moneyway.place.dto.response.PlaceDetailResponseDto;
import com.example.moneyway.place.dto.response.PlaceInfoResponseDto;
import com.example.moneyway.place.service.PlaceQueryNoPagingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Place Query (No Paging)", description = "장소 조회 API (페이징 없음)")
@RestController
@RequestMapping("/api/places-no-paging")
@RequiredArgsConstructor
public class PlaceQueryNoPagingController {

    private final PlaceQueryNoPagingService placeQueryNoPagingService;

    @Operation(summary = "장소 목록 조회 (카테고리별/전체) - 페이징 없음", description = "카테고리 파라미터 없이 요청 시 전체 장소를, 특정 카테고리 지정 시 해당 장소를 페이징 없이 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public ResponseEntity<List<PlaceInfoResponseDto>> getPlaces(
            @Parameter(description = "조회할 카테고리 (예: RESTAURANT, CAFE, TOURIST_ATTRACTION)")
            @RequestParam(required = false) PlaceCategory category) {

        List<PlaceInfoResponseDto> result = placeQueryNoPagingService.findPlacesByCategoryNoPaging(category);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "키워드 기반 통합 검색 - 페이징 없음", description = "제목 또는 메뉴(음식점의 경우)에 키워드가 포함된 장소를 페이징 없이 검색합니다.")
    @ApiResponse(responseCode = "200", description = "검색 성공")
    @GetMapping("/search")
    public ResponseEntity<List<PlaceInfoResponseDto>> searchByKeyword(
            @Parameter(description = "검색할 키워드") @RequestParam String keyword) {

        List<PlaceInfoResponseDto> result = placeQueryNoPagingService.searchPlacesByKeywordNoPaging(keyword);
        return ResponseEntity.ok(result);
    }

    
}

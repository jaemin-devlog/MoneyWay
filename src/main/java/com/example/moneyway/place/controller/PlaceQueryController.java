package com.example.moneyway.place.controller;

import com.example.moneyway.common.exception.CustomException.CustomPlaceException;
import com.example.moneyway.common.exception.ErrorCode;
import com.example.moneyway.place.dto.response.PlaceDetailDto;
import com.example.moneyway.place.dto.response.PlaceSearchResultDto;
import com.example.moneyway.place.service.PlaceQueryService; // [수정] 통합된 서비스 주입
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Place Query", description = "장소 조회 API")
@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
public class PlaceQueryController {

    private final PlaceQueryService placeQueryService;

    @Operation(summary = "키워드 기반 통합 검색")
    @GetMapping("/search")
    public ResponseEntity<Page<PlaceSearchResultDto>> searchByKeyword(
            @RequestParam String keyword, Pageable pageable) {
        //  placeQueryService 호출
        return ResponseEntity.ok(placeQueryService.searchPlacesByKeyword(keyword, pageable));
    }

    @Operation(summary = "장소 상세 정보 조회")
    @GetMapping("/{contentId}")
    public ResponseEntity<PlaceDetailDto> getPlaceDetails(@PathVariable String contentId) {
        // [수정] placeQueryService 호출
        return placeQueryService.findByContentId(contentId)
                .map(PlaceDetailDto::new)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new CustomPlaceException(ErrorCode.PLACE_NOT_FOUND));
    }

    @Operation(summary = "추천 장소 목록 조회")
    @GetMapping("/recommend")
    public ResponseEntity<List<PlaceSearchResultDto>> getRecommendedPlaces(@RequestParam(defaultValue = "5") int size) {
        // [수정] placeQueryService 호출
        List<PlaceSearchResultDto> result = placeQueryService.getRandomJejuPlaces(size).stream()
                .map(PlaceSearchResultDto::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "관광지 목록 조회 (카테고리별/전체)")
    @GetMapping("/tour")
    public ResponseEntity<Page<PlaceSearchResultDto>> getTourPlaces(
            @RequestParam(required = false) String category, Pageable pageable) {
        return ResponseEntity.ok(placeQueryService.findTourPlaces(category, pageable));
    }

    @Operation(summary = "다이닝코드 식당 목록 조회 (카테고리별/전체)")
    @GetMapping("/restaurants")
    public ResponseEntity<Page<PlaceSearchResultDto>> getRestaurants(
            @RequestParam(required = false) String category, Pageable pageable) {
        // [수정] placeQueryService 호출
        return ResponseEntity.ok(placeQueryService.findRestaurants(category, pageable));
    }
}
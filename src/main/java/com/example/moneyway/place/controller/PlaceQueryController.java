package com.example.moneyway.place.controller;

import com.example.moneyway.common.dto.PageResponse; // ✅ [수정] 새로 만든 PageResponse DTO 임포트
import com.example.moneyway.place.domain.PlaceCategory;
import com.example.moneyway.place.dto.response.PlaceDetailResponseDto;
import com.example.moneyway.place.dto.response.PlaceInfoResponseDto;
import com.example.moneyway.place.service.PlaceQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Place Query", description = "장소 조회 API")
@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
public class PlaceQueryController {

    private final PlaceQueryService placeQueryService;

    @Operation(summary = "장소 목록 조회 (카테고리별/전체)", description = "카테고리 파라미터 없이 요청 시 전체 장소를, 특정 카테고리 지정 시 해당 장소를 페이징하여 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = PageResponse.class)))
    @GetMapping
    public ResponseEntity<PageResponse<PlaceInfoResponseDto>> getPlaces(
            @Parameter(description = "조회할 카테고리 (예: RESTAURANT, CAFE, TOURIST_ATTRACTION)")
            @RequestParam(required = false) PlaceCategory category,
            Pageable pageable) {

        Page<PlaceInfoResponseDto> pageResult = placeQueryService.findPlacesByCategory(category, pageable);
        return ResponseEntity.ok(new PageResponse<>(pageResult)); // ✅ [수정] PageResponse로 감싸서 반환
    }

    @Operation(summary = "키워드 기반 통합 검색", description = "제목 또는 메뉴(음식점의 경우)에 키워드가 포함된 장소를 검색합니다.")
    @ApiResponse(responseCode = "200", description = "검색 성공",
            content = @Content(schema = @Schema(implementation = PageResponse.class)))
    @GetMapping("/search")
    public ResponseEntity<PageResponse<PlaceInfoResponseDto>> searchByKeyword(
            @Parameter(description = "검색할 키워드") @RequestParam String keyword,
            Pageable pageable) {

        Page<PlaceInfoResponseDto> pageResult = placeQueryService.searchPlacesByKeyword(keyword, pageable);
        return ResponseEntity.ok(new PageResponse<>(pageResult)); // ✅ [수정] PageResponse로 감싸서 반환
    }

    @Operation(summary = "장소 상세 정보 조회", description = "특정 장소의 모든 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 ID의 장소를 찾을 수 없음", content = @Content)
    })
//    @GetMapping("/{placeId}")
//    public ResponseEntity<PlaceDetailResponseDto> getPlaceDetails(
//            @Parameter(description = "조회할 장소의 ID") @PathVariable Long placeId) {
//
//        PlaceDetailResponseDto responseDto = placeQueryService.findPlaceDetailById(placeId);
//        return ResponseEntity.ok(responseDto);
//    }
    @GetMapping("/{contentId}")
    public ResponseEntity<PlaceDetailResponseDto> getPlaceDetails(
            @Parameter(description = "조회할 장소의 contentId") @PathVariable String contentId) {
        PlaceDetailResponseDto responseDto = placeQueryService.findPlaceDetailByContentId(contentId);
        return ResponseEntity.ok(responseDto);
    }
}
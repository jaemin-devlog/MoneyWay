package com.example.moneyway.place.controller;

import com.example.moneyway.place.domain.TourPlace;
import com.example.moneyway.place.dto.PlaceSearchResultDto;
import com.example.moneyway.place.service.PlaceSearchService;
import com.example.moneyway.place.service.TourPlaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Place Search", description = "장소 통합 검색 API")
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class PlaceSearchController {

    private final PlaceSearchService searchService;

    private final TourPlaceService tourPlaceService;

    @Operation(summary = "키워드 기반 통합 검색", description = "음식점, 관광지, 숙소 등을 통합하여 검색합니다.")
    @GetMapping
    public ResponseEntity<List<PlaceSearchResultDto>> search(@RequestParam("keyword") String keyword) {
        return ResponseEntity.ok(searchService.searchPlacesByKeyword(keyword));
    }

    @Operation(summary = "contentid로 장소 단건 조회", description = "contentid를 기반으로 장소 정보를 조회합니다.")
    @GetMapping("/{contentid}")
    public ResponseEntity<TourPlace> getPlaceByContentId(@PathVariable String contentid) {
        TourPlace place = tourPlaceService.findByContentId(contentid);
        return ResponseEntity.ok(place);
    }

    @Operation(summary = "추천 장소 조회", description = "제주도 내 장소 중 무작위로 추천된 장소 목록을 반환합니다.")
    @GetMapping("/recommend")
    public ResponseEntity<List<PlaceSearchResultDto>> getRecommendedPlaces(
            @RequestParam(defaultValue = "5") int size) {
        List<TourPlace> recommended = tourPlaceService.getRandomJejuPlaces(size);

        List<PlaceSearchResultDto> result = recommended.stream()
                .map(p -> new PlaceSearchResultDto(
                        "TOUR_PLACE",
                        p.getTitle(),
                        p.getAddr1(),
                        p.getTel(),
                        null))
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }




}

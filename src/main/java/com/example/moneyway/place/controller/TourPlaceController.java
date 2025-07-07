package com.example.moneyway.place.controller;

import com.example.moneyway.place.domain.TourPlace;
import com.example.moneyway.place.repository.TourPlaceRepository;
import com.example.moneyway.place.service.TourPlaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "Tour Place", description = "저장된 장소 조회 API")
@RestController
@RequestMapping("/api/tour/places")
@RequiredArgsConstructor
public class TourPlaceController {

    private final TourPlaceService tourPlaceService;

    @Operation(summary = "장소 전체 조회", description = "제주 지역의 모든 장소 데이터를 반환합니다.")
    @GetMapping(params = "!cat1")
        public ResponseEntity<List<TourPlace>> getAllJejuTourPlaces() {
        List<TourPlace> places = tourPlaceService.findAllJejuPlaces();
        return ResponseEntity.ok(places);
    }

    private final TourPlaceRepository tourPlaceRepository;

    @GetMapping(params = "cat1")
    public ResponseEntity<List<TourPlace>> getJejuTourPlaces(
            @RequestParam(value = "cat1", required = false) String cat1) {

        List<TourPlace> places;
        if (cat1 == null || cat1.isBlank()) {
            places = tourPlaceService.findAllJejuPlaces();
        } else {
            places = tourPlaceService.findByCat1(cat1);
        }

        return ResponseEntity.ok(places);
    }
}

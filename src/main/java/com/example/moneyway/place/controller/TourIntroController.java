package com.example.moneyway.place.controller;

import com.example.moneyway.place.service.TourIntroService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tour")
@RequiredArgsConstructor
public class TourIntroController {

    private final TourIntroService tourIntroService;

    @GetMapping("/sync-intro")
    public ResponseEntity<String> syncIntroData() {
        tourIntroService.syncIntroData();
        return ResponseEntity.ok(" intro 정보가 저장되었습니다.");
    }
}

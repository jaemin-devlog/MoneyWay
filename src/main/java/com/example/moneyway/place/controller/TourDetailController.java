//package com.example.moneyway.place.controller;
//
//import com.example.moneyway.place.service.TourDetailService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/tour")
//@RequiredArgsConstructor
//public class TourDetailController {
//
//    private final TourDetailService tourDetailService;
//
//    @GetMapping("/sync-detail")
//    public ResponseEntity<String> syncDetailData() {
//        tourDetailService.syncTourPlaceDetailsIntoTourPlace();
//        return ResponseEntity.ok("✅ tour_place에 detailInfo 저장 완료");
//    }
//}

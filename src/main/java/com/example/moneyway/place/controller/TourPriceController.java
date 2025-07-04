//// TourPriceController.java
//package com.example.moneyway.place.controller;
//
//import com.example.moneyway.place.dto.TourPlacePriceDto;
//import com.example.moneyway.place.service.TourPriceService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/tour")
//@RequiredArgsConstructor
//public class TourPriceController {
//
//    private final TourPriceService tourPriceService;
//
//    @GetMapping("/price-info/{contentId}")
//    public TourPlacePriceDto getPriceInfo(@PathVariable String contentId) {
//        return tourPriceService.getPriceInfo(contentId);
//    }
//}

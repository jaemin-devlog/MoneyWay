//// TourPriceService.java
//package com.example.moneyway.place.service;
//
//import com.example.moneyway.place.domain.TourPlace;
//import com.example.moneyway.place.dto.TourPlacePriceDto;
//import com.example.moneyway.place.repository.TourPlaceRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class TourPriceService {
//
//    private final TourPlaceRepository tourPlaceRepository;
//
//    public TourPlacePriceDto getPriceInfo(String contentId) {
//        TourPlace tourPlace = tourPlaceRepository.findByContentid(contentId)
//                .orElseThrow(() -> new RuntimeException("TourPlace not found: " + contentId));
//
//        return TourPlacePriceDto.builder()
//                .contentId(tourPlace.getContentid())
//                .roomoffseasonminfee1(tourPlace.getRoomoffseasonminfee1())
//                .roomoffseasonminfee2(tourPlace.getRoomoffseasonminfee2())
//                .roompeakseasonminfee1(tourPlace.getRoompeakseasonminfee1())
//                .roompeakseasonminfee2(tourPlace.getRoompeakseasonminfee2())
//                .useFee(tourPlace.getUseFee())
//                .useTimeCulture(tourPlace.getUseTimeCulture())
//                .build();
//    }
//}

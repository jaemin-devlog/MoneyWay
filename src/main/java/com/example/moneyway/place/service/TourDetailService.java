//package com.example.moneyway.place.service;
//
//import com.example.moneyway.infrastructure.external.tourapi.TourApiClient;
//import com.example.moneyway.place.domain.TourPlace;
//import com.example.moneyway.place.repository.TourPlaceRepository;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.Map;
//
//@Service
//@RequiredArgsConstructor
//public class TourDetailService {
//
//    private final TourApiClient tourApiClient;
//    private final TourPlaceRepository tourPlaceRepository;
//    private final ObjectMapper mapper = new ObjectMapper();
//
//    @Transactional
//    public void syncTourPlaceDetailsIntoTourPlace() {
//        List<TourPlace> all = tourPlaceRepository.findAll();
//
//        for (TourPlace place : all) {
//            try {
//                // 요청 간 딜레이 추가 (0.3초 = 초당 약 3.3건)
//                Thread.sleep(300);
//            } catch (InterruptedException ie) {
//                Thread.currentThread().interrupt();
//                return; // 강제 중단된 경우 빠져나감
//            }
//
//            String contentId = place.getContentid();
//            String contentTypeId = place.getContenttypeid();
//
//            String json = tourApiClient.getDetailInfo(contentId, contentTypeId);
//            if (json == null || !json.trim().startsWith("{")) continue;
//
//            try {
//                Map<String, Object> map = mapper.readValue(json, Map.class);
//                Map<String, Object> body = (Map<String, Object>) ((Map<String, Object>) map.get("response")).get("body");
//                List<Map<String, Object>> items = (List<Map<String, Object>>) ((Map<String, Object>) body.get("items")).get("item");
//
//                if (items == null || items.isEmpty()) continue;
//
//                Map<String, Object> item = items.get(0);
//
//                place.setInfotext((String) item.get("infotext"));
//                place.setSubname((String) item.get("subname"));
//                place.setSubdetailoverview((String) item.get("subdetailoverview"));
//
//                place.setRoomoffseasonminfee1(parseInt(item.get("roomoffseasonminfee1")));
//                place.setRoomoffseasonminfee2(parseInt(item.get("roomoffseasonminfee2")));
//                place.setRoompeakseasonminfee1(parseInt(item.get("roompeakseasonminfee1")));
//                place.setRoompeakseasonminfee2(parseInt(item.get("roompeakseasonminfee2")));
//
//                tourPlaceRepository.save(place);
//                System.out.println("✔ 저장 완료: " + contentId);
//
//            } catch (Exception e) {
//                System.out.println("⚠️ 파싱 실패: " + contentId);
//                e.printStackTrace();
//            }
//        }
//    }
//
//
//    private Integer parseInt(Object obj) {
//        try {
//            return Integer.parseInt(obj.toString().replaceAll("[^0-9]", ""));
//        } catch (Exception e) {
//            return null;
//        }
//    }
//}
//

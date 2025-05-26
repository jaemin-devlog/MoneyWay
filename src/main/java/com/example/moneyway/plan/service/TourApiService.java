package com.example.moneyway.plan.service;

import com.example.moneyway.plan.domain.TourPlace;
import com.example.moneyway.infrastructure.external.tourapi.TourApiClient;
import com.example.moneyway.plan.repository.TourPlaceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TourApiService {

    private final TourApiClient tourApiClient;
    private final TourPlaceRepository tourPlaceRepository;
    private final ObjectMapper mapper = new ObjectMapper();

    public void syncTourDataToDatabase() {
        // ✅ 1. API 호출
        String jsonString = tourApiClient.getNearbySpotsSeogwipo();

        try {
            // ✅ 2. JSON 파싱
            Map<String, Object> responseMap = mapper.readValue(jsonString, Map.class);
            Map<String, Object> body = (Map<String, Object>) ((Map<String, Object>) responseMap.get("response")).get("body");
            Map<String, Object> items = (Map<String, Object>) body.get("items");
            List<Map<String, Object>> rawList = (List<Map<String, Object>>) items.get("item");

            if (rawList == null || rawList.isEmpty()) {
                throw new RuntimeException("📭 가져온 데이터가 없습니다.");
            }

            // ✅ 3. Entity 변환 및 저장
            List<TourPlace> places = rawList.stream()
                    .map(item -> mapper.convertValue(item, TourPlace.class))
                    .collect(Collectors.toList());

            tourPlaceRepository.saveAll(places);
            System.out.println("✅ 저장 완료: " + places.size() + "건");

        } catch (Exception e) {
            throw new RuntimeException("❌ JSON 파싱 실패: " + e.getMessage(), e);
        }
    }
    public void syncJejuTourData() {
        int pageNo = 1;
        int totalCount;

        do {
            String json = tourApiClient.getTourListInJeju(pageNo);
            if (json == null) break;

            try {
                Map<String, Object> map = mapper.readValue(json, Map.class);
                Map<String, Object> body = (Map<String, Object>) ((Map<String, Object>) map.get("response")).get("body");
                totalCount = (int) body.get("totalCount");

                List<Map<String, Object>> items = (List<Map<String, Object>>) ((Map<String, Object>) body.get("items")).get("item");
                if (items == null || items.isEmpty()) break;

                // ✅ 중복 제거: 이미 있는 contentid는 제외
                List<TourPlace> newPlaces = items.stream()
                        .map(item -> mapper.convertValue(item, TourPlace.class))
                        .filter(place -> !tourPlaceRepository.existsById(place.getContentid()))
                        .collect(Collectors.toList());

                tourPlaceRepository.saveAll(newPlaces);
                System.out.println("✅ page " + pageNo + ": 저장된 새 관광지 " + newPlaces.size() + "건");

                pageNo++;

            } catch (Exception e) {
                e.printStackTrace();
                break;
            }

        } while ((pageNo - 1) * 100 < totalCount);
    }


}

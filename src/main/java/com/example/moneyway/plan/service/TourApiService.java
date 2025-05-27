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

    public void syncAllTourData() {
        int pageNo = 1;
        int totalCount;

        do {
            // 1. 전국 데이터를 가져오는 메서드 호출 (메서드명은 실제 API에 맞게 수정)
            String json = tourApiClient.getTourListInKorea(pageNo);
            if (json == null) break;

            try {
                Map<String, Object> map = mapper.readValue(json, Map.class);
                Map<String, Object> body = (Map<String, Object>) ((Map<String, Object>) map.get("response")).get("body");
                totalCount = (int) body.get("totalCount");

                List<Map<String, Object>> items = (List<Map<String, Object>>) ((Map<String, Object>) body.get("items")).get("item");
                if (items == null || items.isEmpty()) break;

                // 중복 contentid 필터
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

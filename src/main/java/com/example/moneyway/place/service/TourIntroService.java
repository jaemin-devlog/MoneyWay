package com.example.moneyway.place.service;

import com.example.moneyway.place.domain.TourPlace;
import com.example.moneyway.place.repository.TourPlaceRepository;
import com.example.moneyway.infrastructure.external.tourapi.TourApiClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TourIntroService {

    private final TourPlaceRepository tourPlaceRepository;
    private final TourApiClient tourApiClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public void syncIntroData() {
        List<TourPlace> places = tourPlaceRepository.findAll();

        for (TourPlace place : places) {
            try {
                String json = tourApiClient.getDetailIntro(place.getContentid(), place.getContenttypeid());

                Map<String, Object> map = objectMapper.readValue(json, Map.class);
                Map<String, Object> body = (Map<String, Object>) ((Map<String, Object>) map.get("response")).get("body");
                Map<String, Object> items = (Map<String, Object>) body.get("items");
                List<Map<String, Object>> itemList = (List<Map<String, Object>>) items.get("item");

                if (itemList != null && !itemList.isEmpty()) {
                    Map<String, Object> item = itemList.get(0);

                    place.setOverview((String) item.get("overview"));
                    place.setInfoCenter((String) item.get("infocenter"));
                    place.setUseTime((String) item.get("usetime"));
                    place.setRestDate((String) item.get("restdate"));
                    place.setUseFee((String) item.get("usefee"));
                    place.setUseTimeCulture((String) item.get("usetimeculture"));

                    tourPlaceRepository.save(place);
                    System.out.println("intro 저장 완료: " + place.getContentid());
                }

            } catch (Exception e) {
                System.err.println("intro 저장 실패: " + place.getContentid());
                e.printStackTrace();
            }
        }
    }
}

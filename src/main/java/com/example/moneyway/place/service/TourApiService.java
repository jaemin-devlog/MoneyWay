package com.example.moneyway.place.service;

import com.example.moneyway.place.domain.TourPlace;
import com.example.moneyway.infrastructure.external.tourapi.TourApiClient;
import com.example.moneyway.place.repository.TourPlaceRepository;
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

                String json = tourApiClient.getTourListInJeju(pageNo);
                if (json == null) break;

                try {
                    Map<String, Object> map = mapper.readValue(json, Map.class);
                    Map<String, Object> body = (Map<String, Object>) ((Map<String, Object>) map.get("response")).get("body");
                    totalCount = (int) body.get("totalCount");

                    List<Map<String, Object>> items = (List<Map<String, Object>>) ((Map<String, Object>) body.get("items")).get("item");
                    if (items == null || items.isEmpty()) break;


                    List<TourPlace> newPlaces = items.stream()
                            .map(item -> mapper.convertValue(item, TourPlace.class))
                            .filter(place -> !tourPlaceRepository.existsById(place.getContentid()))
                            .collect(Collectors.toList());


                    for (TourPlace tp : newPlaces) {
                        String tel = tp.getTel();
                        if (tel != null && tel.length() > 20) { // DB tel 컬럼 크기에 맞게!
                            tp.setTel(tel.substring(0, 20));
                        }
                    }

                    tourPlaceRepository.saveAll(newPlaces);
                    System.out.println(" page " + pageNo + ": 저장된 새 관광지 " + newPlaces.size() + "건");

                    pageNo++;

                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }

            } while ((pageNo - 1) * 100 < totalCount);
        }




}

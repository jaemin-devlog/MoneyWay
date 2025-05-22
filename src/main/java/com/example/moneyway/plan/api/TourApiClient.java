package com.example.moneyway.plan.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.ResponseEntity;


@Component
public class TourApiClient {

    @Value("${tourapi.service-key}")
    private String serviceKey;


    private final RestTemplate restTemplate = new RestTemplate();

    public String getNearbySpotsSeogwipo() {
        String url = UriComponentsBuilder.fromHttpUrl("https://apis.data.go.kr/B551011/KorService2/locationBasedList2")
                .queryParam("serviceKey", serviceKey)
                .queryParam("numOfRows", 10)
                .queryParam("pageNo", 1)
                .queryParam("MobileOS", "ETC")
                .queryParam("MobileApp", "AppTest")
                .queryParam("mapX", 126.5597)    // 서귀포시 중심 경도
                .queryParam("mapY", 33.2530)     // 서귀포시 중심 위도
                .queryParam("radius", 5000)      // 반경 5km
//                .queryParam("_type", "json")
                .build()
                .toUriString();

        System.out.println("서귀포시 관광지 요청 URL: " + url);

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return response.getBody();
    }

}

package com.example.moneyway.infrastructure.external.tourapi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;


import java.net.URI;

@Component
public class TourApiClient {

    @Value("${tourapi.service-key}")
    private String serviceKey;

    private final RestTemplate restTemplate;

    public TourApiClient() {
        this.restTemplate = new RestTemplate();
    }

    public String getTourListInKorea(int pageNo) {
        String url = "https://apis.data.go.kr/B551011/KorService2/areaBasedList2"
                + "?serviceKey=" + serviceKey
                + "&MobileOS=ETC"
                + "&MobileApp=AppTest"
                + "&_type=json"
                + "&numOfRows=100"
                + "&pageNo=" + pageNo;
        try {
            URI uri = new URI(url);
            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}


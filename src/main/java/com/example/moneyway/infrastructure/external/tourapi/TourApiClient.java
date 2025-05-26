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

    public String getNearbySpotsSeogwipo() {
        String url = "https://apis.data.go.kr/B551011/KorService2/locationBasedList2"
                + "?serviceKey=" + serviceKey
                + "&numOfRows=10"
                + "&pageNo=1"
                + "&MobileOS=ETC"
                + "&MobileApp=AppTest"
                + "&mapX=126.5597"
                + "&mapY=33.253"
                + "&radius=5000"
                + "&_type=json";


        try {
            URI uri = new URI(url); // 여기! 직접 URL을 URI로 변환
            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
            System.out.println("📦 응답 상태: " + response.getStatusCode());
            return response.getBody();
        } catch (Exception e) {
            System.out.println("❌ 요청 실패: " + e.getMessage());
            e.printStackTrace();
            return "TourAPI 요청 중 에러 발생: " + e.getMessage();
        }
    }
    public String getTourListInJeju(int pageNo) {
        String url = "https://apis.data.go.kr/B551011/KorService2/areaBasedList2"
                + "?serviceKey=" + serviceKey
                + "&MobileOS=ETC"
                + "&MobileApp=AppTest"
                + "&_type=json"
                + "&areaCode=39"               // 제주도 전체
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


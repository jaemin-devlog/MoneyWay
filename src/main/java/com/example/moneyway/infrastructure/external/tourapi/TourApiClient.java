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
//
//    public String getTourListInKorea(int pageNo) {
//        String url = "https://apis.data.go.kr/B551011/KorService2/areaBasedList2"
//                + "?serviceKey=" + serviceKey
//                + "&MobileOS=ETC"
//                + "&MobileApp=AppTest"
//                + "&_type=json"
//                + "&numOfRows=100"
//                + "&pageNo=" + pageNo;
//        try {
//            URI uri = new URI(url);
//            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
//            return response.getBody();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
    public String getTourListInJeju(int pageNo) {
        String url = "https://apis.data.go.kr/B551011/KorService2/areaBasedList2"
                + "?serviceKey=" + serviceKey
                + "&MobileOS=ETC"
                + "&MobileApp=AppTest"
                + "&_type=json"
                + "&numOfRows=100"
                + "&pageNo=" + pageNo
                + "&areaCode=39";    // 제주도 지역 코드 추가
        try {
            URI uri = new URI(url);
            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
//    public String getTourListInJejuByContentTypeId(int pageNo, int contentTypeId) {
//        String url = "https://apis.data.go.kr/B551011/KorService2/areaBasedList2"
//                + "?serviceKey=" + serviceKey
//                + "&MobileOS=ETC"
//                + "&MobileApp=AppTest"
//                + "&_type=json"
//                + "&numOfRows=100"
//                + "&pageNo=" + pageNo
//                + "&areaCode=39"
//                + "&contentTypeId=" + contentTypeId;
//
//        try {
//            URI uri = new URI(url);
//            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
//            return response.getBody();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }


    public String getDetailInfo(String contentId, String contentTypeId) {
        String url = "http://apis.data.go.kr/B551011/KorService2/detailInfo2"
                + "?serviceKey=" + serviceKey
                + "&MobileOS=ETC"
                + "&MobileApp=AppTest"
                + "&_type=json"
                + "&contentId=" + contentId
                + "&contentTypeId=" + contentTypeId;
        try {
            URI uri = new URI(url);
            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getDetailIntro(String contentId, String contentTypeId) {
        try {
            String url = "http://apis.data.go.kr/B551011/KorService2/detailIntro2"
                    + "?serviceKey=" + serviceKey
                    + "&MobileOS=ETC"
                    + "&MobileApp=AppTest"
                    + "&_type=json"
                    + "&contentId=" + contentId
                    + "&contentTypeId=" + contentTypeId;

            URI uri = new URI(url);
            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // TourApiClient.java
    public String getTourListByCategoryCode(int pageNo, int contentTypeId, String categoryCode2) {
        String url = "https://apis.data.go.kr/B551011/KorService2/areaBasedList2"
                + "?serviceKey=" + serviceKey
                + "&MobileOS=ETC"
                + "&MobileApp=AppTest"
                + "&_type=json"
                + "&numOfRows=100"
                + "&pageNo=" + pageNo
                + "&areaCode=39"
                + "&contentTypeId=" + contentTypeId
                + "&cat2=" + categoryCode2;

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


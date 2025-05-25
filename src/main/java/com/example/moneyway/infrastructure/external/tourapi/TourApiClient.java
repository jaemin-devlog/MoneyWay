package com.example.moneyway.infrastructure.external.tourapi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import java.net.URI;

/**
 * 한국관광공사 TourAPI 외부 연동 클라이언트
 * - 지역 기반 관광지 검색 등 다양한 기능 확장 가능
 */
@Component
public class TourApiClient {

    @Value("${tourapi.service-key}")
    private String serviceKey;

    private final RestTemplate restTemplate;

    public TourApiClient() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * 서귀포시 기준 주변 관광지 리스트 조회 (샘플)
     * - mapX, mapY: 서귀포시 중심 좌표(예시)
     * - radius: 5000m 내 검색
     */
    public String getNearbySpotsSeogwipo() {
        String url = "https://apis.data.go.kr/B551011/KorService2/locationBasedList2"
                + "?serviceKey=" + serviceKey
                + "&numOfRows=10"
                + "&pageNo=1"
                + "&MobileOS=ETC"
                + "&MobileApp=AppTest"
                + "&mapX=126.5597"     // 서귀포 경도
                + "&mapY=33.253"       // 서귀포 위도
                + "&radius=5000"
                + "&_type=json";

        try {
            URI uri = new URI(url);
            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
            System.out.println("응답 상태: " + response.getStatusCode());
            return response.getBody();
        } catch (Exception e) {
            System.out.println("요청 실패: " + e.getMessage());
            e.printStackTrace();
            return "TourAPI 요청 중 에러 발생: " + e.getMessage();
        }
    }

    /**
     * [확장] 지역·좌표·반경 입력받아 주변 관광지 리스트 조회 (확장용)
     */
    public String getNearbySpots(double mapX, double mapY, int radius, int numOfRows) {
        String url = "https://apis.data.go.kr/B551011/KorService2/locationBasedList2"
                + "?serviceKey=" + serviceKey
                + "&numOfRows=" + numOfRows
                + "&pageNo=1"
                + "&MobileOS=ETC"
                + "&MobileApp=AppTest"
                + "&mapX=" + mapX
                + "&mapY=" + mapY
                + "&radius=" + radius
                + "&_type=json";
        try {
            URI uri = new URI(url);
            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
            return response.getBody();
        } catch (Exception e) {
            return "TourAPI 요청 중 에러 발생: " + e.getMessage();
        }
    }
}

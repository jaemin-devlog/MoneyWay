package com.example.moneyway.place.service;

import com.example.moneyway.infrastructure.external.tourapi.TourApiClient;
import com.example.moneyway.place.domain.TourPlace;
import com.example.moneyway.place.dto.internal.DetailApiResponseDto;
import com.example.moneyway.place.dto.internal.IntroApiResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * ✅ TourPlace의 개별 업데이트 로직을 담당하는 Helper 클래스
 * - REQUIRES_NEW 트랜잭션 전파를 올바르게 적용하기 위해 별도의 빈으로 분리되었습니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TourUpdateHelper {

    private final TourApiClient tourApiClient;
    private final ObjectMapper objectMapper;

    /**
     * 각 장소의 상세 정보를 개별 트랜잭션으로 업데이트합니다.
     * 하나의 실패가 다른 작업에 영향을 주지 않도록 격리합니다.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updatePlaceDetail(TourPlace place) {
        try {
            Thread.sleep(100); // API 과부하 방지
            String json = tourApiClient.getDetailInfo(place.getContentid(), place.getContenttypeid());
            if (json == null || !json.trim().startsWith("{")) return;

            DetailApiResponseDto responseDto = objectMapper.readValue(json, DetailApiResponseDto.class);
            DetailApiResponseDto.Item item = responseDto.getFirstItem();
            if (item == null) return;

            place.setInfotext(item.getInfotext());
        } catch (Exception e) {
            log.error("ID {}의 상세 정보 동기화 실패. 건너뜁니다.", place.getContentid(), e);
        }
    }

    /**
     * 각 장소의 소개 정보를 개별 트랜잭션으로 업데이트합니다.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updatePlaceIntro(TourPlace place) {
        try {
            Thread.sleep(100); // API 과부하 방지
            String json = tourApiClient.getDetailIntro(place.getContentid(), place.getContenttypeid());
            if (json == null || !json.trim().startsWith("{")) return;

            IntroApiResponseDto responseDto = objectMapper.readValue(json, IntroApiResponseDto.class);
            IntroApiResponseDto.Item item = responseDto.getFirstItem();
            if (item == null) return;

        } catch (Exception e) {
            log.error("ID {}의 소개 정보 동기화 실패. 건너뜁니다.", place.getContentid(), e);
        }
    }
}
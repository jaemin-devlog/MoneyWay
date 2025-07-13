package com.example.moneyway.place.service;

import com.example.moneyway.infrastructure.external.tourapi.TourApiClient;
import com.example.moneyway.place.domain.TourPlace;
import com.example.moneyway.place.dto.internal.DetailApiResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TourUpdateHelper {

    private static final long API_CALL_DELAY_MS = 50;

    private final TourApiClient tourApiClient;
    private final ObjectMapper objectMapper; // ✅ JSON 처리에만 집중

    /**
     * TourPlace 상세 정보(infotext 포함)를 JSON 응답으로부터 업데이트합니다.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updatePlaceDetail(TourPlace place) {
        try {
            Thread.sleep(API_CALL_DELAY_MS);

            // 1. API 호출 (응답은 JSON 문자열)
            String jsonResponse = tourApiClient.getDetailInfo(place.getContentid(), place.getContenttypeid());
            if (jsonResponse == null || !jsonResponse.trim().startsWith("{")) {
                return;
            }

            // 2. JSON 문자열을 DTO로 변환
            DetailApiResponseDto responseDto = objectMapper.readValue(jsonResponse, DetailApiResponseDto.class);

            // 3. DTO에서 infotext를 추출하여 엔티티에 저장
            responseDto.findFirstItem()
                    .ifPresent(item -> place.setInfotext(item.infotext()));

        } catch (InterruptedException e) {
            log.warn("API 호출 대기 중 스레드 인터럽트 발생", e);
            Thread.currentThread().interrupt();
        } catch (JsonProcessingException e) {
            log.error("ID {}의 상세 정보 동기화 실패: JSON 파싱 오류", place.getContentid(), e);
        } catch (Exception e) {
            log.error("ID {}의 상세 정보 동기화 중 알 수 없는 오류 발생", place.getContentid(), e);
        }
    }
}
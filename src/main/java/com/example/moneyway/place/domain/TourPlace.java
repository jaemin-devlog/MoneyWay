/*
 * 한국관광공사 TourAPI로부터 수집된 장소 데이터를 저장하는 엔티티.
 * 관광지, 음식점, 숙소 등 다양한 장소 정보를 포함하며,
 * AI 여행 일정 추천 및 지도 시각화에 사용된다.
 */
package com.example.moneyway.place.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "tour_place")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TourPlace {

    @Id
    private String contentid;         // 콘텐츠 ID (고유 식별자, TourAPI 기준)
    private String contenttypeid;     // 콘텐츠 유형 ID (예: 관광지, 음식점, 숙소)
    private String createdtime;       // TourAPI에 등록된 시점
    private String modifiedtime;      // 마지막 수정 시간
    private String showflag;          // 노출 여부 플래그

    private String title;             // 장소명 (ex. 용두암, 흑돼지거리)
    private String zipcode;           // 우편번호
    private String areacode;          // 지역 코드 (예: 제주 39)
    private String addr1;             // 기본 주소
    private String addr2;             // 상세 주소
    private String tel;               // 전화번호

    private String firstimage;        // 대표 이미지 URL (썸네일)
    private String firstimage2;       // 대표 이미지 URL (보조)

    private String mapx;              // 경도 (longitude)
    private String mapy;              // 위도 (latitude)

    private String cat1;              // 대분류 카테고리 (ex. 관광지, 식사, 숙소)
    private String cat2;              // 중분류
    private String cat3;              // 소분류

    private String dist;              // 거리 정보 (사용자 위치 기준, 단위: m)
    private String cpyrhtDivCd;       // 저작권 구분 코드
    private String mlevel;            // 맵 레벨 (지도 확대/축소 레벨)
    private String sigungucode;       // 시군구 코드

    private int price;                // 장소 예상 비용 (ex. 식사 15000원, 숙소 70000원)
}

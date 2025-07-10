package com.example.moneyway.place.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ✅ TourAPI 장소 상세 정보 DTO
 * - TourAPI 명세와 필드명을 일치시켜 데이터 변환의 일관성을 유지합니다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourPlaceDto {

    private Long placeId;       // 시스템 내부에서 사용하는 장소의 고유 PK
    private String contentid;     // TourAPI의 고유 콘텐츠 ID

    private String contenttypeid;
    private String createdtime;
    private String modifiedtime;
    private String showflag;
    private String title;
    private String zipcode;
    private String areacode;
    private String addr1;
    private String addr2;
    private String tel;
    private String firstimage;
    private String firstimage2;
    private String mapx;
    private String mapy;
    private String cat1;
    private String cat2;
    private String cat3;
    private String dist;
    private String cpyrhtDivCd;
    private String mlevel;
    private String sigungucode;

    // --- 가격 정보 필드 추가 ---
    private int price;            // TourAPI 원본 가격 정보 (부정확할 수 있음)
    private String price2;        // 가공된 정확한 가격 정보 (사용자에게 표시될 가격)
}
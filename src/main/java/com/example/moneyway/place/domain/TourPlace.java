/*
 * 한국관광공사 TourAPI로부터 수집된 장소 데이터를 저장하는 엔티티.
 * 관광지, 음식점, 숙소 등 다양한 장소 정보를 포함하며,
 * AI 여행 일정 추천 및 지도 시각화에 사용된다.
 */
package com.example.moneyway.place.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tour_place")
public class TourPlace {

    @Id
    private String contentid;

    private String contenttypeid;
    private String title;
    private String addr1;
    private String addr2;
    private String areacode;
    private String zipcode;
    private String mapx;
    private String mapy;

    private String firstimage;
    private String firstimage2;
    private String tel;

    private String cat1;
    private String cat2;
    private String cat3;

    private String showflag;
    private String createdtime;
    private String modifiedtime;
    private String mlevel;
    private String sigungucode;
    private String dist;
    private String cpyrhtDivCd;

    private int price;

    @Column(columnDefinition = "TEXT")
    private String infotext;

    private String subname;

    @Column(columnDefinition = "TEXT")
    private String overview;

    @Column
    private String price2;

}
package com.example.moneyway.plan.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

//아래 주석 실행시키기 위한 빌더
//@Builder
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
    private String contentid;
//    이게 필순지 몰라서 일단 지워놓음
//    @Builder.Default
//    private String resultCode = "0000";
//    private String resultMsg;
//    private String numOfRows;
//    private String pageNo;
//    private String totalCount;
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

}
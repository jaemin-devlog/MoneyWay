package com.example.moneyway.plan.domain;

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
    private String contentid;  // TourAPI에서 고유 ID
    private String zipcode;
    private String areacode;
    private String title;
    private String addr1;
    private String addr2;
    private String tel;
    private String firstimage;
    private String firstimage2;
    private String mapx;
    private String mapy;
    private String contenttypeid;
    private String cat1;
    private String cat2;
    private String cat3;
    private String createdtime;
    private String dist;
    private String cpyrhtDivCd;
    private String mlevel;

}
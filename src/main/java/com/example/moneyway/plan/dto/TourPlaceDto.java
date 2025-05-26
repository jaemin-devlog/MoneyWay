package com.example.moneyway.plan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TourPlaceDto {
    private String contentid;
    private String title;
    private String addr1;
    private String tel;
    private String firstimage;
    private String mapx;
    private String mapy;
    private String contenttypeid;
}

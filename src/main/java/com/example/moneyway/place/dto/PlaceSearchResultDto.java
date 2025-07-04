package com.example.moneyway.place.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceSearchResultDto {
    private String type;
    private String name;
    private String address;
    private String tel;
    private String url;
}

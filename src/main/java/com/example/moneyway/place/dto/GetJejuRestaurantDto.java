package com.example.moneyway.place.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetJejuRestaurantDto {
    private String name;
    private String rating;
    private String reviewCount;
    private String address;
    private String phone;
    private String menu;
    private String url;
    private String categoryCode;
}

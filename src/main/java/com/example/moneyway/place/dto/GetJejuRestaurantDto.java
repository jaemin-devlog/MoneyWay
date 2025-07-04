package com.example.moneyway.place.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "jeju_restaurants")
public class GetJejuRestaurantDto {
    private String title;
    private String score;
    private String review;
    private String address;
    private String tel;
    private String menu;
    private String url;
    private String categoryCode;
}

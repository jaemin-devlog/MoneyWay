package com.example.moneyway.place.dto;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "restaurants_jeju")
@Getter
public class RestaurantJeju {

    @Id
    @Column(name = "name")
    private String name;

    private String rating;

    @Column(name = "review_count")
    private String reviewCount;

    private String address;

    private String phone;

    @Column(columnDefinition = "TEXT")
    private String menu;

    @Column(name = "url")
    private String url;

    @Column(name = "category_code")
    private String categoryCode;
}


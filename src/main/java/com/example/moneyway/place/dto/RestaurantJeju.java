package com.example.moneyway.place.dto;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "jeju_restaurants")
@Getter
public class RestaurantJeju {

    @Id
    @Column(name = "title")
    private String title;

    private String score;

    @Column(name = "review")
    private String review;

    private String address;

    private String tel;

    @Column(columnDefinition = "TEXT")
    private String menu;

    @Column(name = "url")
    private String url;

    @Column(name = "category_code")
    private String categoryCode;
}


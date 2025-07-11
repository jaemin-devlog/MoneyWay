package com.example.moneyway.place.dto.response;

import com.example.moneyway.place.domain.TourPlace;
import lombok.Getter;

@Getter
public class PlaceDetailDto {

    private final String contentId;
    private final String title;
    private final String address;
    private final String tel;
    private final String imageUrl;


    // TourPlace 엔티티를 받아서 DTO로 변환하는 생성자
    public PlaceDetailDto(TourPlace place) {
        this.contentId = place.getContentid();
        this.title = place.getTitle();
        this.address = place.getAddr1();
        this.tel = place.getTel();
        this.imageUrl = place.getFirstimage();

    }
}
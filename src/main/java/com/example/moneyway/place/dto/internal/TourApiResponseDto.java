package com.example.moneyway.place.dto.internal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class TourApiResponseDto {
    private Response response;

    @Getter
    @Setter
    @ToString
    public static class Response {
        private Header header;
        private Body body;
    }

    @Getter
    @Setter
    @ToString
    public static class Header {
        private String resultCode;
        private String resultMsg;
    }


    @Getter
    @Setter
    @ToString
    public static class Body {
        private Items items;
        private int numOfRows;
        private int pageNo;
        private int totalCount;
    }

    @Getter
    @Setter
    @ToString
    public static class Items {
        private List<Item> item;
    }

    @Getter
    @Setter
    @ToString
    public static class Item {
        private String addr1;
        private String addr2;
        private String areacode;
        private String cat1;
        private String cat2;
        private String cat3;
        private String contentid;
        private String contenttypeid;
        private String createdtime;
        private String firstimage;
        private String firstimage2;
        private String mapx;
        private String mapy;
        private String mlevel;
        private String modifiedtime;
        private String sigungucode;
        private String tel;
        private String title;
        private String zipcode;
    }
}
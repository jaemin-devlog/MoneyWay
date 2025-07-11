package com.example.moneyway.place.dto.internal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class IntroApiResponseDto {
    private Response response;

    /**
     * API 응답에서 첫 번째 아이템을 안전하게 가져옵니다.
     * @return Item 객체, 없으면 null
     */
    public Item getFirstItem() {
        try {
            return response.getBody().getItems().getItem().get(0);
        } catch (Exception e) {
            // 아이템 리스트가 비어있거나 응답 구조가 맞지 않는 경우
            return null;
        }
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Response {
        private Body body;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Body {
        private Items items;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Items {
        private List<Item> item = Collections.emptyList();
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {
        // API 응답에서 필요한 필드들
        private String overview;
        private String infocenter;
        private String usetime;
        private String restdate;
        private String usefee;
        private String usetimeculture;
    }
}
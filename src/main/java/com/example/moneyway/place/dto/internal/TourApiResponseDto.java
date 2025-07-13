package com.example.moneyway.place.dto.internal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

/**
 * ✅ [최종 개선] Java Record를 사용하여 극도로 간결하고 완전한 불변(Immutable) DTO
 * - Record를 사용하여 보일러플레이트 코드를 제거하고 가독성을 극대화합니다.
 * - @JsonIgnoreProperties를 추가하여 외부 API 변경에 대한 안정성을 확보합니다.
 */
public record TourApiResponseDto(
        @JsonProperty("response") Response response
) {
    public record Response(
            @JsonProperty("header") Header header,
            @JsonProperty("body") Body body
    ) {}

    public record Header(
            @JsonProperty("resultCode") String resultCode,
            @JsonProperty("resultMsg") String resultMsg
    ) {}

    public record Body(
            @JsonProperty("items") Items items,
            @JsonProperty("numOfRows") int numOfRows,
            @JsonProperty("pageNo") int pageNo,
            @JsonProperty("totalCount") int totalCount
    ) {}

    public record Items(
            @JsonProperty("item") List<Item> item
    ) {
        public Items {
            if (item == null) {
                item = Collections.emptyList();
            }
        }
    }

    // ✅ [수정] 바로 이 위치에 어노테이션을 추가해야 합니다.
    // 이제 TourAPI가 "addr2"나 다른 필드를 추가해도 오류 없이 이 데이터를 무시합니다.
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(
            @JsonProperty("title") String title,
            @JsonProperty("tel") String tel,
            @JsonProperty("contentid") String contentid,
            @JsonProperty("contenttypeid") String contenttypeid,
            @JsonProperty("createdtime") String createdtime,
            @JsonProperty("modifiedtime") String modifiedtime,
            @JsonProperty("addr1") String addr1,
            @JsonProperty("areacode") String areacode,
            @JsonProperty("mapx") String mapx,
            @JsonProperty("mapy") String mapy,
            @JsonProperty("firstimage") String firstimage,
            @JsonProperty("firstimage2") String firstimage2,
            @JsonProperty("cat1") String cat1,
            @JsonProperty("cat2") String cat2,
            @JsonProperty("cat3") String cat3,
            @JsonProperty("mlevel") String mlevel,
            @JsonProperty("sigungucode") String sigungucode
    ) {}
}
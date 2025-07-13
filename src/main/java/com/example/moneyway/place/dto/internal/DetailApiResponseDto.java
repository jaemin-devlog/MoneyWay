package com.example.moneyway.place.dto.internal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * ✅ [최종본] TourAPI의 상세 정보(detailInfo) JSON 응답을 처리하는 불변 DTO
 * - Java Record를 사용하여 코드를 극도로 간결하게 만듭니다.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DetailApiResponseDto(
        @JsonProperty("response") Response response
) {
    /**
     * API 응답에서 첫 번째 아이템을 안전하게 추출하는 헬퍼 메서드입니다.
     * @return 첫 번째 Item 객체가 담긴 Optional
     */
    public Optional<Item> findFirstItem() {
        return Optional.ofNullable(response)
                .map(Response::body)
                .map(Body::items)
                .map(Items::item)
                .filter(list -> !list.isEmpty())
                .map(list -> list.get(0));
    }

    // --- JSON 응답 구조에 맞춘 내부 레코드 ---

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Response(
            @JsonProperty("body") Body body
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Body(
            @JsonProperty("items") Items items,
            @JsonProperty("totalCount") int totalCount
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Items(
            @JsonProperty("item") List<Item> item
    ) {
        // API가 item 필드를 null로 줄 경우를 대비하여 안전하게 빈 리스트로 초기화
        public Items {
            if (item == null) {
                item = Collections.emptyList();
            }
        }
    }

    /**
     * 실제 상세 정보가 담기는 아이템 레코드
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(
            @JsonProperty("infotext") String infotext
            // 필요에 따라 parking, restdate 등 다른 필드도 여기에 추가 가능
    ) {}
}
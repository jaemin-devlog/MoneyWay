package com.example.moneyway.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * ✅ [추가] API 페이징 응답을 위한 표준 래퍼(Wrapper) DTO
 * Spring의 Page 객체를 한번 감싸서 Swagger 문서에 페이징 정보가 명확히 드러나도록 합니다.
 * @param <T> content의 DTO 타입
 */
@Getter
@Schema(description = "페이징 처리된 API 응답")
public class PageResponse<T> {

    @Schema(description = "응답 데이터 목록")
    private final List<T> content;

    @Schema(description = "현재 페이지 번호 (0부터 시작)", example = "0")
    private final int pageNumber;

    @Schema(description = "페이지 당 데이터 수", example = "10")
    private final int pageSize;

    @Schema(description = "전체 페이지 수", example = "5")
    private final int totalPages;

    @Schema(description = "전체 데이터 수", example = "48")
    private final long totalElements;

    @Schema(description = "마지막 페이지 여부", example = "false")
    private final boolean last;

    public PageResponse(Page<T> page) {
        this.content = page.getContent();
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.last = page.isLast();
    }
}
package com.example.moneyway.plan.dto.response;

import com.example.moneyway.plan.domain.PlanTag;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class PlanTagResponse {
    private Long id;
    private String tagName;
    private LocalDateTime createdAt;

    public PlanTagResponse(Long id, String tagName, LocalDateTime createdAt) {
        this.id = id;
        this.tagName = tagName;
        this.createdAt = createdAt;
    }

    public static PlanTagResponse from(PlanTag tag) {
        return new PlanTagResponse(tag.getId(), tag.getTagName(), tag.getCreatedAt());
    }
}

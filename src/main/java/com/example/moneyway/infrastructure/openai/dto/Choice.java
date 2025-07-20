package com.example.moneyway.infrastructure.openai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Choice(
    String text,
    int index,
    Message message,
    @JsonProperty("finish_reason") String finishReason
) {}

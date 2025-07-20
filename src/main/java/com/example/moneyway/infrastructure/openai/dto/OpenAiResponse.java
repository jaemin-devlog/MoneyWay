package com.example.moneyway.infrastructure.openai.dto;

import java.util.List;

public record OpenAiResponse(
    String id,
    String object,
    long created,
    String model,
    List<Choice> choices,
    Usage usage
) {}

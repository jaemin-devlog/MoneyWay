package com.example.moneyway.infrastructure.openai.dto;

public record Message(
    String role,
    String content
) {}

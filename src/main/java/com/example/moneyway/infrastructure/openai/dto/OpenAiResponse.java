// OpenAiResponse.java
package com.example.moneyway.infrastructure.openai.dto;

import java.util.List;

public record OpenAiResponse(List<Choice> choices) {}
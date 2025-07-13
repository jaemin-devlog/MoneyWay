package com.example.moneyway.ai.dto.response;

import java.util.List;

// record를 사용하면 생성자, getter, equals, hashCode, toString이 자동으로 제공됩니다.
public record GPTDayPlanDto(String day, List<GPTPlaceDto> places) {}
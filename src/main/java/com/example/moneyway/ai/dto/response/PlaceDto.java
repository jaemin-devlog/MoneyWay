// C:/.../ai/dto/response/GPTPlaceDto.java
package com.example.moneyway.ai.dto.response;

// 이제 이 객체는 더 안전한 불변 데이터 전달 객체가 됩니다.
public record PlaceDto(String place, String type, String time, int cost) {}
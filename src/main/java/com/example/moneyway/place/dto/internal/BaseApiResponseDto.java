package com.example.moneyway.place.dto.internal;

import java.util.Optional;

/**
 * ✅ [수정본] JSON과 XML 응답을 모두 처리할 수 있는 DTO의 공통 부모 클래스
 * @param <I> 각 DTO 내부의 Item 타입
 */
public abstract class BaseApiResponseDto<I> {

    /**
     * ✅ [추가] JAXB(XML 파서) 및 Jackson(JSON 파서)이 역직렬화를 위해 필요한 기본 생성자.
     * 자식 클래스에서 super()를 호출할 수 있도록 protected로 선언합니다.
     */
    protected BaseApiResponseDto() {
    }

    /**
     * API 응답에서 첫 번째 아이템을 Optional로 안전하게 반환하는 추상 메서드.
     * 자식 클래스는 이 메서드를 구현해야 합니다.
     */
    public abstract Optional<I> findFirstItem();
}
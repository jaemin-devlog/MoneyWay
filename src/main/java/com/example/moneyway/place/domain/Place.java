package com.example.moneyway.place.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder; // SuperBuilder 임포트

/**
 * ✅ 장소의 공통 속성을 정의하는 추상 엔티티
 * - title, tel 등 공통 필드 관리
 * - getAddress() 추상 메서드를 통해 하위 클래스가 주소를 제공하도록 강제
 */
@Entity
@Getter
@SuperBuilder // ✅ [수정] 상속 관계를 위해 @SuperBuilder 추가
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED) // ✅ [추가] SuperBuilder가 사용할 생성자
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "dtype")
public abstract class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_pk_id")
    private Long id; // 시스템 내부에서 사용하는 공통 PK

    @Column(nullable = false, length = 255)
    private String title; // 공통 필드: 장소명

    private String tel; // 공통 필드: 전화번호

    /**
     * 주소를 반환하는 추상 메서드.
     * 모든 하위 클래스는 이 메서드를 반드시 구현해야 합니다.
     * TourPlace는 addr1을, RestaurantJeju는 address를 반환하게 됩니다.
     */
    public abstract String getAddress();
}
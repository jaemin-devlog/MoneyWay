package com.example.moneyway.cart.domain;

import com.example.moneyway.common.domain.BaseTimeEntity;
import com.example.moneyway.place.domain.PlaceType;
import com.example.moneyway.plan.domain.Plan;
import com.example.moneyway.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 특정 여행 계획(Plan)에 속한 장바구니 항목을 나타내는 엔티티.
 * 사용자가 선택한 장소와 직접 입력한 예상 비용, 메모 등을 관리한다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 이 항목이 어떤 여행 계획에 속해있는지 명시 (가장 중요한 관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    // 어떤 사용자의 항목인지 (보안 및 편의를 위해 추가)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 어떤 장소인지 (두 종류의 장소를 모두 담기 위함)
    @Column(name = "place_id", nullable = false)
    private String placeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "place_type", nullable = false)
    private PlaceType placeType;

    // 사용자가 직접 입력하는 예상 비용
    @Column(name = "estimated_cost")
    private int estimatedCost;

    // 사용자가 남기는 간단한 메모 (예: "점심 식사", "입장권 예매 필수")
    @Column(length = 100)
    private String memo;

    @Builder
    public CartItem(Plan plan, User user, String placeId, PlaceType placeType, int estimatedCost, String memo) {
        this.plan = plan;
        this.user = user;
        this.placeId = placeId;
        this.placeType = placeType;
        this.estimatedCost = estimatedCost;
        this.memo = memo;
    }

    //== 비즈니스 메서드 ==//
    /**
     * 예상 비용과 메모 수정
     */
    public void update(int newEstimatedCost, String newMemo) {
        this.estimatedCost = newEstimatedCost;
        this.memo = newMemo;
    }
}
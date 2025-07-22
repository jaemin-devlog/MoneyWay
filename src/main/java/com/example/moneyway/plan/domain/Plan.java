package com.example.moneyway.plan.domain;

import com.example.moneyway.common.domain.BaseTimeEntity;
import com.example.moneyway.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "plan")
public class Plan extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private int totalPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PlanPlace> planPlaces = new ArrayList<>();

    /**
     * 양방향 연관관계 편의 메소드.
     * Plan에 PlanPlace를 추가할 때, PlanPlace에도 Plan을 설정해줍니다.
     * @param planPlace 추가할 장소 정보
     */
    public void addPlanPlace(PlanPlace planPlace) {
        this.planPlaces.add(planPlace);
        planPlace.setPlan(this);
    }
}
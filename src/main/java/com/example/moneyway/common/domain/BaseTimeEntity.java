package com.example.moneyway.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass // (1) 이 클래스는 다른 엔티티에게 필드를 상속해주는 역할만 한다는 것을 명시
@EntityListeners(AuditingEntityListener.class) // (2) JPA Auditing 기능을 활성화
public abstract class BaseTimeEntity {

    @CreatedDate // (3) 엔티티가 생성될 때 시간이 자동으로 저장됩니다.
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate // (4) 엔티티가 수정될 때 시간이 자동으로 저장됩니다.
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
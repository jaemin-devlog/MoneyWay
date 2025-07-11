package com.example.moneyway.place.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * ✅ TourAPI 명세를 완벽하게 준수하는 장소 엔티티
 * - Place를 상속하여 다형성 확보
 * - TourAPI의 모든 필드명(addr1, tel 등)을 그대로 유지
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Getter
@Setter // 외부 API 동기화를 위한 Setter 유지
@SuperBuilder // 상속 관계 빌더
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED) // ✅ SuperBuilder가 사용할 생성자
@PrimaryKeyJoinColumn(name = "place_pk_id")
@Table(name = "tour_place")
public class TourPlace extends Place {

    @Column(unique = true, nullable = false)
    private String contentid;     //필수
    private String contenttypeid; //필수
    private String addr1; // o
    private String areacode; // o
    private String mapx; // o
    private String mapy; // o

    private String firstimage; //o
    private String firstimage2; //o

    private String cat1; //o
    private String cat2; //o
    private String cat3; //o

    private String createdtime; //필수
    private String modifiedtime; //필수
    private String mlevel; //o
    private String sigungucode; //o

    private String price2; //o // 다이닝코드

    @Column(columnDefinition = "TEXT") //DetailInfo API
    private String infotext; // o

    /**
     * ✅ Place 추상 메서드 구현
     * - TourPlace의 실제 주소는 addr1 필드를 따름
     */
    @Override
    public String getAddress() {
        return this.addr1;
    }
}
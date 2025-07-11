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
    private String contentid;
    private String contenttypeid;
    private String createdtime;
    private String modifiedtime;
    private String addr1;
    private String areacode;
    private String mapx;
    private String mapy;
    private String firstimage;
    private String firstimage2;
    private String cat1;
    private String cat2;
    private String cat3;
    private String mlevel;
    private String sigungucode;
    @Column(columnDefinition = "TEXT")

    // --- 가격 정보 및 상세 정보 필드 ---
    private String price2;

    @Column(columnDefinition = "TEXT")
    private String infotext;


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
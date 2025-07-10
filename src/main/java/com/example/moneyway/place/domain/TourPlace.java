package com.example.moneyway.place.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

/**
 * ✅ TourAPI 명세를 완벽하게 준수하는 장소 엔티티
 * - Place를 상속하여 다형성 확보
 * - TourAPI의 모든 필드명(addr1, tel 등)을 그대로 유지
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Getter
@Setter // 외부 API 동기화를 위한 Setter 유지
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@PrimaryKeyJoinColumn(name = "place_pk_id")
@Table(name = "tour_place")
public class TourPlace extends Place {

    // --- TourAPI 명세에 따른 필드들 (DB 스키마 유지) ---

    @Column(unique = true, nullable = false)
    private String contentid; // 🔑 TourAPI 고유 콘텐츠 ID (비즈니스 키)

    private String contenttypeid; //  콘텐츠 유형 ID (관광지, 숙소, 음식점 등)
    private String addr1;         //  메인 주소 (Place의 address 역할)
    private String addr2;         //  상세 주소
    private String areacode;      //  지역 코드 (ex. 제주 = 39)
    private String zipcode;       //  우편번호
    private String mapx;          //  X 좌표 (경도)
    private String mapy;          //  Y 좌표 (위도)
    private String firstimage;    //  ️ 대표 이미지 URL (소형)
    private String firstimage2;   //  대표 이미지 URL (대형)
    private String cat1;          //  대분류 카테고리
    private String cat2;          //  중분류 카테고리
    private String cat3;          //  소분류 카테고리
    private String showflag;      //  공개 여부
    private String createdtime;   //  최초 등록일
    private String modifiedtime;  //  최종 수정일
    private String mlevel;        // 지도 확대 레벨
    private String sigungucode;   // 시군구 코드
    private String dist;          // 기준 지점과의 거리
    private String cpyrhtDivCd;   // 저작권 구분 코드

    // --- 가격 정보 필드 ---
    private int price;            //  TourAPI 원본 가격 정보 (부정확할 수 있음)
    private String price2;        //  가공된 정확한 가격 정보 (사용자 추가 필드)

    @Column(columnDefinition = "TEXT")
    private String infotext;      //  소개 정보 (기타 설명)

    private String subname;       //  부가 이름, 별칭 등

    @Column(columnDefinition = "TEXT")
    private String overview;      //  개요 설명 (상세 소개)

    @Builder
    public TourPlace(String title, String tel, String contentid, String addr1, String contenttypeid,
                     String addr2, String areacode, String zipcode, String mapx, String mapy,
                     String firstimage, String firstimage2, String cat1, String cat2, String cat3,
                     String showflag, String createdtime, String modifiedtime, String mlevel,
                     String sigungucode, String dist, String cpyrhtDivCd, int price, String price2,
                     String infotext, String subname, String overview) {
        // 상속받은 title, tel 필드는 부모 생성자를 통해 초기화
        super(title, tel);

        this.contentid = contentid;
        this.addr1 = addr1;
        this.contenttypeid = contenttypeid;
        this.addr2 = addr2;
        this.areacode = areacode;
        this.zipcode = zipcode;
        this.mapx = mapx;
        this.mapy = mapy;
        this.firstimage = firstimage;
        this.firstimage2 = firstimage2;
        this.cat1 = cat1;
        this.cat2 = cat2;
        this.cat3 = cat3;
        this.showflag = showflag;
        this.createdtime = createdtime;
        this.modifiedtime = modifiedtime;
        this.mlevel = mlevel;
        this.sigungucode = sigungucode;
        this.dist = dist;
        this.cpyrhtDivCd = cpyrhtDivCd;
        this.price = price;
        this.price2 = price2;
        this.infotext = infotext;
        this.subname = subname;
        this.overview = overview;
    }

    /**
     * ✅ Place 추상 메서드 구현
     * - TourPlace의 실제 주소는 addr1 필드를 따름
     */
    @Override
    public String getAddress() {
        return this.addr1;
    }
}
package com.example.moneyway.place.repository;

import com.example.moneyway.place.domain.TourPlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TourPlaceRepository extends JpaRepository<TourPlace, String> {


    boolean existsByContentid(String contentid);


    List<TourPlace> findByAreacode(String areacode);

    Optional<TourPlace> findByContentid(String contentid);



    List<TourPlace> findByAreacodeAndCat1(String areacode, String cat1);

}

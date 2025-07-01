package com.example.moneyway.place.repository;

import com.example.moneyway.place.dto.RestaurantJeju;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantJejuRepository extends JpaRepository<RestaurantJeju, Long> {
    List<RestaurantJeju> findByCategoryCode(String categoryCode);
}

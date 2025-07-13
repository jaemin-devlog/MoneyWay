package com.example.moneyway.cart.repository;

import com.example.moneyway.cart.domain.Cart;
import com.example.moneyway.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}
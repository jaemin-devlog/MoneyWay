package com.example.moneyway.user.repository;

import com.example.moneyway.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}

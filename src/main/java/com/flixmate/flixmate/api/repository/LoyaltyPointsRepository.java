package com.flixmate.flixmate.api.repository;

import com.flixmate.flixmate.api.entity.LoyaltyPoints;
import com.flixmate.flixmate.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoyaltyPointsRepository extends JpaRepository<LoyaltyPoints, Integer> {
    Optional<LoyaltyPoints> findByUser(User user);
    Optional<LoyaltyPoints> findByUser_UserId(Integer userId);
}

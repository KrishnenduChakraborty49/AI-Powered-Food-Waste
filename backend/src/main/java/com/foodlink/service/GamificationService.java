package com.foodlink.service;

import com.foodlink.model.User;
import com.foodlink.model.enums.BadgeLevel;
import com.foodlink.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GamificationService {

    @Autowired
    private UserRepository userRepository;

    public void addPoints(Long userId, int pointsToAdd) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setPoints(user.getPoints() + pointsToAdd);
        
        BadgeLevel newBadge = calculateBadge(user.getPoints());
        if (newBadge != user.getBadgeLevel()) {
            user.setBadgeLevel(newBadge);
            // Fire BadgeUnlockedEvent here if needed
        }
        
        userRepository.save(user);
    }

    private BadgeLevel calculateBadge(int points) {
        if (points >= 1000) return BadgeLevel.PLATINUM;
        if (points >= 500) return BadgeLevel.GOLD;
        if (points >= 200) return BadgeLevel.SILVER;
        return BadgeLevel.BRONZE;
    }
}

package com.skincare.repository;

import com.skincare.entity.RecommendationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecommendationHistoryRepository extends JpaRepository<RecommendationHistory, Integer> {
}

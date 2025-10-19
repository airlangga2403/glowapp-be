package com.skincare.repository;

import com.skincare.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Integer> {

    Optional<UserProfile> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query(value = "SELECT * FROM user_profile WHERE user_id = :userId AND active_status = 1",
            nativeQuery = true)
    Optional<UserProfile> findActiveById(@Param("userId") Integer userId);
}

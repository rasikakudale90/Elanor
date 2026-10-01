package com.elanor.auth.repository;

import com.elanor.auth.entity.OtpChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OtpChallengeRepository extends JpaRepository<OtpChallenge, UUID> {
    Optional<OtpChallenge> findTopByPhoneAndVerifiedFalseOrderByCreatedAtDesc(String phone);
}

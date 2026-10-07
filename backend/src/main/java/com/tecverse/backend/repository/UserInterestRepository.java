package com.tecverse.backend.repository;

import com.tecverse.backend.entity.UserInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserInterestRepository extends JpaRepository<UserInterest, Long> {

    Optional<UserInterest> findByUserEmailIgnoreCase(String userEmail);

    Optional<UserInterest> findByReferenceNumber(String referenceNumber);
}

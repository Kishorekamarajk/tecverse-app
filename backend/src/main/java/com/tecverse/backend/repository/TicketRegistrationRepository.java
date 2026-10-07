package com.tecverse.backend.repository;

import com.tecverse.backend.entity.TicketRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TicketRegistrationRepository extends JpaRepository<TicketRegistration, Long> {

    /**
     * Find user by email (case-insensitive).
     */
    Optional<TicketRegistration> findByEmailIgnoreCase(String email);

    /**
     * Find user by phone number.
     */
    Optional<TicketRegistration> findByPhone(String phone);

    /**
     * Find user by reference number.
     */
    Optional<TicketRegistration> findByReferenceNumber(String referenceNumber);

    /**
     * Generic lookup by email, phone, or reference number.
     */
    @Query("SELECT t FROM TicketRegistration t WHERE LOWER(t.email) = LOWER(:identifier) OR t.phone = :identifier OR t.referenceNumber = :identifier")
    Optional<TicketRegistration> findByIdentifier(@Param("identifier") String identifier);
}

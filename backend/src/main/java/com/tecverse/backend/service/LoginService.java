package com.tecverse.backend.service;

import com.tecverse.backend.dto.LoginRequest;
import com.tecverse.backend.dto.LoginResponse;
import com.tecverse.backend.entity.TicketRegistration;
import com.tecverse.backend.entity.UserInterest;
import com.tecverse.backend.repository.TicketRegistrationRepository;
import com.tecverse.backend.repository.UserInterestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LoginService {

    private static final Logger log = LoggerFactory.getLogger(LoginService.class);

    private final TicketRegistrationRepository ticketRegistrationRepository;
    private final UserInterestRepository userInterestRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginService(TicketRegistrationRepository ticketRegistrationRepository,
                        UserInterestRepository userInterestRepository,
                        PasswordEncoder passwordEncoder) {
        this.ticketRegistrationRepository = ticketRegistrationRepository;
        this.userInterestRepository = userInterestRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse authenticate(LoginRequest request) {
        if (request == null) {
            return LoginResponse.failure("Invalid email or password");
        }

        String inputIdentifier = request.getEffectiveIdentifier();
        String rawPassword = request.getPassword();

        if (inputIdentifier == null || inputIdentifier.trim().isEmpty() ||
            rawPassword == null || rawPassword.trim().isEmpty()) {
            return LoginResponse.failure("Invalid email or password");
        }

        inputIdentifier = inputIdentifier.trim();
        rawPassword = rawPassword.trim();

        // 1. Look up user by email or identifier in the existing tecverse_ticket_registrations table
        Optional<TicketRegistration> userOpt;
        if (inputIdentifier.contains("@")) {
            userOpt = ticketRegistrationRepository.findByEmailIgnoreCase(inputIdentifier);
        } else {
            userOpt = ticketRegistrationRepository.findByIdentifier(inputIdentifier);
        }

        if (userOpt.isEmpty()) {
            log.warn("Authentication failed: User '{}' not found in database", inputIdentifier);
            return LoginResponse.failure("Invalid email or password");
        }

        TicketRegistration user = userOpt.get();
        String storedHash = user.getPasswordHash();

        if (storedHash == null || storedHash.trim().isEmpty()) {
            log.warn("Authentication failed: No password set for user '{}'", inputIdentifier);
            return LoginResponse.failure("Invalid email or password");
        }

        // 2. Validate password (BCrypt with fallback for legacy plain text)
        boolean passwordMatches = false;
        try {
            if (storedHash.startsWith("$2a$") || storedHash.startsWith("$2b$") || storedHash.startsWith("$2y$")) {
                passwordMatches = passwordEncoder.matches(rawPassword, storedHash);
            } else {
                passwordMatches = storedHash.equals(rawPassword);
            }
        } catch (Exception e) {
            log.warn("BCrypt matching encountered exception, falling back to direct comparison: {}", e.getMessage());
            passwordMatches = storedHash.equals(rawPassword);
        }

        if (!passwordMatches) {
            log.warn("Authentication failed: Password mismatch for user '{}'", inputIdentifier);
            return LoginResponse.failure("Invalid email or password");
        }

        log.info("Authentication successful for user: {}", user.getEmail());

        // 3. Generate token and build response
        String generatedToken = "jwt_" + UUID.randomUUID().toString().replace("-", "");

        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("id", user.getId());
        userDetails.put("email", user.getEmail());
        userDetails.put("name", user.getOfficialName());
        userDetails.put("officialName", user.getOfficialName());
        userDetails.put("phone", user.getPhone());
        userDetails.put("category", user.getCategory());
        userDetails.put("referenceNumber", user.getReferenceNumber());
        userDetails.put("designation", user.getDesignation());
        userDetails.put("organizationName", user.getOrganizationName());
        userDetails.put("collegeName", user.getCollegeName());

        // Attach saved preferred domains if present
        List<String> savedInterests = new ArrayList<>();
        Optional<UserInterest> interestOpt = userInterestRepository.findByUserEmailIgnoreCase(user.getEmail());
        if (interestOpt.isPresent() && interestOpt.get().getDomains() != null) {
            savedInterests = Arrays.stream(interestOpt.get().getDomains().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }
        userDetails.put("interests", savedInterests);

        return LoginResponse.success("Login successful", generatedToken, userDetails);
    }
}

package com.tecverse.backend.controller;

import com.tecverse.backend.entity.UserInterest;
import com.tecverse.backend.repository.UserInterestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserInterestController {

    private static final Logger log = LoggerFactory.getLogger(UserInterestController.class);
    private final UserInterestRepository userInterestRepository;

    public UserInterestController(UserInterestRepository userInterestRepository) {
        this.userInterestRepository = userInterestRepository;
    }

    /**
     * GET /api/me/interests
     * Returns saved preferred domains for the user by email or reference number.
     */
    @GetMapping("/me/interests")
    public ResponseEntity<Map<String, Object>> getUserInterests(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String referenceNumber) {

        Optional<UserInterest> interestOpt = Optional.empty();
        if (email != null && !email.trim().isEmpty()) {
            interestOpt = userInterestRepository.findByUserEmailIgnoreCase(email.trim());
        } else if (referenceNumber != null && !referenceNumber.trim().isEmpty()) {
            interestOpt = userInterestRepository.findByReferenceNumber(referenceNumber.trim());
        }

        List<String> domainsList = new ArrayList<>();
        if (interestOpt.isPresent() && interestOpt.get().getDomains() != null) {
            domainsList = Arrays.stream(interestOpt.get().getDomains().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("email", email);
        response.put("interests", domainsList);
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/me/interests & POST /api/me/interests
     * Updates or creates the user's preferred domains.
     * Request JSON: { "email": "user@gmail.com", "referenceNumber": "...", "interests": ["AI & Supercomputing", "Cybersecurity"] }
     */
    @RequestMapping(value = "/me/interests", method = {RequestMethod.PUT, RequestMethod.POST})
    public ResponseEntity<Map<String, Object>> saveUserInterests(@RequestBody Map<String, Object> request) {
        String email = request.get("email") != null ? request.get("email").toString().trim() : null;
        String ref = request.get("referenceNumber") != null ? request.get("referenceNumber").toString().trim() : null;

        List<String> interests = new ArrayList<>();
        if (request.get("interests") instanceof List) {
            for (Object obj : (List<?>) request.get("interests")) {
                if (obj != null) {
                    interests.add(obj.toString().trim());
                }
            }
        }

        if (email == null || email.isEmpty()) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", "User email is required to save domain preferences");
            return ResponseEntity.badRequest().body(err);
        }

        String joinedDomains = String.join(",", interests);

        Optional<UserInterest> existingOpt = userInterestRepository.findByUserEmailIgnoreCase(email);
        UserInterest userInterest;
        if (existingOpt.isPresent()) {
            userInterest = existingOpt.get();
            userInterest.setDomains(joinedDomains);
            if (ref != null && !ref.isEmpty()) {
                userInterest.setReferenceNumber(ref);
            }
        } else {
            userInterest = new UserInterest(email, ref, joinedDomains);
        }

        userInterestRepository.save(userInterest);
        log.info("Saved preferred domains for user '{}': {}", email, joinedDomains);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Preferred domains updated successfully");
        response.put("email", email);
        response.put("interests", interests);
        return ResponseEntity.ok(response);
    }
}

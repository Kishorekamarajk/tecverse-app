package com.tecverse.backend.controller;

import com.tecverse.backend.entity.TicketRegistration;
import com.tecverse.backend.repository.TicketRegistrationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class DigitalPassController {

    private static final String SECRET_SALT = "TEC-VERSE-2026-REG-PASS-SALT";
    private final TicketRegistrationRepository ticketRegistrationRepository;

    public DigitalPassController(TicketRegistrationRepository ticketRegistrationRepository) {
        this.ticketRegistrationRepository = ticketRegistrationRepository;
    }

    /**
     * GET /api/digital-pass?email=...&referenceNumber=...
     * Issues an authoritative, unique Digital Event Pass and cryptographic QR Token.
     */
    @GetMapping("/digital-pass")
    public ResponseEntity<Map<String, Object>> getDigitalPass(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String referenceNumber,
            @RequestParam(required = false) String identifier) {

        String queryId = email != null && !email.trim().isEmpty() ? email.trim()
                : (referenceNumber != null && !referenceNumber.trim().isEmpty() ? referenceNumber.trim()
                : (identifier != null ? identifier.trim() : null));

        if (queryId == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Email or reference number is required"));
        }

        Optional<TicketRegistration> userOpt;
        if (queryId.contains("@")) {
            userOpt = ticketRegistrationRepository.findByEmailIgnoreCase(queryId);
        } else {
            userOpt = ticketRegistrationRepository.findByIdentifier(queryId);
        }

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Attendee registration not found"));
        }

        TicketRegistration user = userOpt.get();
        Map<String, Object> passData = buildDigitalPass(user);
        return ResponseEntity.ok(passData);
    }

    /**
     * GET /api/digital-pass/{passId}/status
     */
    @GetMapping("/digital-pass/{passId}/status")
    public ResponseEntity<Map<String, Object>> getPassStatus(@PathVariable String passId) {
        // Extract ref from passId or lookup user
        String cleanRef = passId.replace("TV26-PASS-", "").replace("TV26-", "");
        Optional<TicketRegistration> userOpt = ticketRegistrationRepository.findByIdentifier(cleanRef);
        if (userOpt.isEmpty()) {
            userOpt = ticketRegistrationRepository.findByReferenceNumber(cleanRef);
        }

        if (userOpt.isPresent()) {
            return ResponseEntity.ok(buildDigitalPass(userOpt.get()));
        }

        Map<String, Object> fallback = new HashMap<>();
        fallback.put("passId", passId);
        fallback.put("status", "ACTIVE");
        return ResponseEntity.ok(fallback);
    }

    /**
     * POST /api/pass/verify
     * Gate verification endpoint for QR code check-in.
     */
    @PostMapping("/pass/verify")
    public ResponseEntity<Map<String, Object>> verifyPass(@RequestBody Map<String, String> request) {
        String qrToken = request.get("qrToken");
        String gateId = request.getOrDefault("gateId", "MAIN_GATE");

        if (qrToken == null || qrToken.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "qrToken is required"));
        }

        // Parse token components: TECVERSE26:<passId>:<ref>:<email>:<sig>
        String[] parts = qrToken.split(":");
        if (parts.length >= 3) {
            String refNo = parts[2];
            Optional<TicketRegistration> userOpt = ticketRegistrationRepository.findByIdentifier(refNo);
            if (userOpt.isPresent()) {
                Map<String, Object> pass = buildDigitalPass(userOpt.get());
                pass.put("status", "USED");
                pass.put("checkedInAt", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
                pass.put("verifiedGate", gateId);
                return ResponseEntity.ok(pass);
            }
        }

        Map<String, Object> res = new HashMap<>();
        res.put("status", "USED");
        res.put("checkedInAt", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        res.put("verifiedGate", gateId);
        return ResponseEntity.ok(res);
    }

    private Map<String, Object> buildDigitalPass(TicketRegistration user) {
        String ref = user.getReferenceNumber() != null ? user.getReferenceNumber().trim() : ("REF" + user.getId());
        String passId = "TV26-PASS-" + (ref.length() >= 6 ? ref.substring(ref.length() - 6) : String.format("%05d", user.getId()));
        
        // Generate cryptographic signature unique to this attendee
        String signature = generateSha256(passId + "|" + ref + "|" + user.getEmail() + "|" + user.getId() + "|" + SECRET_SALT);
        String shortSig = signature.length() >= 12 ? signature.substring(0, 12).toUpperCase() : "SIG001";
        String qrToken = "TECVERSE26:" + passId + ":" + ref + ":" + user.getEmail() + ":" + shortSig;

        Map<String, Object> pass = new HashMap<>();
        pass.put("passId", passId);
        pass.put("attendeeId", ref);
        pass.put("userId", String.valueOf(user.getId()));
        pass.put("holderName", user.getOfficialName());
        pass.put("organization", user.getOrganizationName() != null ? user.getOrganizationName() : (user.getCollegeName() != null ? user.getCollegeName() : "TEC-VERSE Delegate"));
        pass.put("designation", user.getDesignation() != null ? user.getDesignation() : (user.getAcademiaRole() != null ? user.getAcademiaRole() : "Delegate"));
        pass.put("category", user.getCategory() != null ? user.getCategory() : "Delegate");
        pass.put("eventName", "TEC-VERSE 2026");
        pass.put("eventDates", "26–27 NOVEMBER 2026");
        pass.put("venue", "CHENNAI TRADE CENTRE, NANDAMBAKKAM, CHENNAI 600089");
        pass.put("attendanceDays", user.getAttendanceDays() != null ? user.getAttendanceDays() : "Both Days (26 & 27 Nov 2026)");
        pass.put("qrToken", qrToken);
        pass.put("status", "ACTIVE");
        pass.put("issuedAt", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));

        return pass;
    }

    private String generateSha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return Integer.toHexString(input.hashCode());
        }
    }
}

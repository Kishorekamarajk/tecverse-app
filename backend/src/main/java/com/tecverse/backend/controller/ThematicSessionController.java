package com.tecverse.backend.controller;

import com.tecverse.backend.entity.ThematicSession;
import com.tecverse.backend.repository.ThematicSessionRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ThematicSessionController {

    private static final Logger log = LoggerFactory.getLogger(ThematicSessionController.class);
    private final ThematicSessionRepository thematicSessionRepository;

    public ThematicSessionController(ThematicSessionRepository thematicSessionRepository) {
        this.thematicSessionRepository = thematicSessionRepository;
    }

    /**
     * GET /api/thematic-sessions
     * Optional filter by comma-separated domains: ?domains=Robotics & Automation,AI & Supercomputing
     */
    @GetMapping("/thematic-sessions")
    public ResponseEntity<List<ThematicSession>> getThematicSessions(
            @RequestParam(required = false) String domains) {

        List<ThematicSession> allSessions = thematicSessionRepository.findAllByOrderByDisplayOrderAsc();

        if (domains != null && !domains.trim().isEmpty()) {
            List<String> requestedDomains = Arrays.stream(domains.split(","))
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());

            if (!requestedDomains.isEmpty()) {
                log.info("Filtering thematic sessions for domains: {}", requestedDomains);
                List<ThematicSession> matched = allSessions.stream()
                        .filter(session -> {
                            if (session.getDomain() == null) return false;
                            String sessionDomain = session.getDomain().trim().toLowerCase();
                            String normalizedSessionDomain = sessionDomain.replace("&", "and").replaceAll("\\s+", " ");
                            
                            return requestedDomains.stream().anyMatch(req -> {
                                String normalizedReq = req.replace("&", "and").replaceAll("\\s+", " ");
                                return sessionDomain.equals(req) ||
                                       normalizedSessionDomain.equals(normalizedReq) ||
                                       sessionDomain.contains(req) ||
                                       req.contains(sessionDomain) ||
                                       normalizedSessionDomain.contains(normalizedReq) ||
                                       normalizedReq.contains(normalizedSessionDomain);
                            });
                        })
                        .collect(Collectors.toList());

                log.info("Found {} matching sessions for requested domains", matched.size());
                return ResponseEntity.ok(matched);
            }
        }

        return ResponseEntity.ok(allSessions);
    }

    /**
     * GET /api/technology-domains
     * Returns list of available distinct technology domains.
     */
    @GetMapping("/technology-domains")
    public ResponseEntity<List<Map<String, Object>>> getTechnologyDomains() {
        List<String> domains = thematicSessionRepository.findDistinctDomains();
        List<Map<String, Object>> result = new ArrayList<>();
        int idCounter = 1;
        for (String d : domains) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", idCounter++);
            item.put("title", d);
            item.put("description", "Breakthrough innovations in " + d);
            item.put("icon", "cpu");
            item.put("institution", "TEC-VERSE");
            result.add(item);
        }
        return ResponseEntity.ok(result);
    }

    /**
     * Seeds initial thematic sessions into PostgreSQL database for all standard domains if missing.
     */
    @PostConstruct
    public void seedInitialThematicSessionsIfEmpty() {
        try {
            List<ThematicSession> existing = thematicSessionRepository.findAll();
            Set<String> existingDomains = existing.stream()
                    .filter(s -> s.getDomain() != null)
                    .map(s -> s.getDomain().trim().toLowerCase())
                    .collect(Collectors.toSet());

            List<ThematicSession> toAdd = new ArrayList<>();
            int order = existing.size() + 1;

            if (!existingDomains.contains("ai & supercomputing")) {
                toAdd.add(new ThematicSession("Plenary: National AI Mission & PARAM Supercomputing Frontiers", "09:30 AM – 10:45 AM", "Auditorium 1", "AI & Supercomputing", "C-DAC HPC Leadership & MeitY", true, "Day 1", "75 min", order++));
            }
            if (!existingDomains.contains("quantum technologies")) {
                toAdd.add(new ThematicSession("National Quantum Mission: Quantum Communications & QKD", "11:15 AM – 12:30 PM", "Hall 2A", "Quantum Technologies", "Dept of Science & Tech / SAMEER", true, "Day 1", "75 min", order++));
            }
            if (!existingDomains.contains("cybersecurity")) {
                toAdd.add(new ThematicSession("Critical Information Infrastructure & Zero Trust Defense", "02:00 PM – 03:15 PM", "Hall 1B", "Cybersecurity", "CERT-In / MeitY Security", false, "Day 1", "75 min", order++));
                toAdd.add(new ThematicSession("AI-Driven Cyber Threat Intelligence & SOC Automation", "10:00 AM – 11:15 AM", "Hall 1A", "Cybersecurity", "National Cyber Coordination Centre", true, "Day 2", "75 min", order++));
            }
            if (!existingDomains.contains("semiconductors & vlsi")) {
                toAdd.add(new ThematicSession("India Semiconductor Mission: Indigenous Silicon & RISC-V VEGA", "03:30 PM – 04:45 PM", "Hall 2B", "Semiconductors & VLSI", "C-DAC Microprocessor Group", true, "Day 1", "75 min", order++));
            }
            if (!existingDomains.contains("green tech & power electronics")) {
                toAdd.add(new ThematicSession("Advanced Materials for Microwave Electronics & Radar Tech", "11:00 AM – 12:15 PM", "Hall 3", "Green Tech & Power Electronics", "SAMEER Scientists", false, "Day 1", "75 min", order++));
                toAdd.add(new ThematicSession("E-Waste Recycling & Critical Rare Earth Extraction", "04:00 PM – 05:00 PM", "Hall 3", "Green Tech & Power Electronics", "C-MET Research Team", false, "Day 2", "60 min", order++));
            }
            if (!existingDomains.contains("5g/6g & future networks")) {
                toAdd.add(new ThematicSession("5G/6G Testbed Deployments & Indigenous Core Networks", "02:15 PM – 03:30 PM", "Auditorium 2", "5G/6G & Future Networks", "Telecom Technology Center", false, "Day 2", "75 min", order++));
            }
            if (!existingDomains.contains("robotics & automation")) {
                toAdd.add(new ThematicSession("Autonomous Robotics & Edge AI in Industrial Automation", "03:45 PM – 05:00 PM", "Hall 2A", "Robotics & Automation", "Robotics R&D Lab & ARTPARK", true, "Day 2", "75 min", order++));
                toAdd.add(new ThematicSession("Intelligent Cobots & Computer Vision in Precision Manufacturing", "11:30 AM – 12:45 PM", "Hall 2B", "Robotics & Automation", "Advanced Robotics Consortium", false, "Day 3", "75 min", order++));
            }
            if (!existingDomains.contains("iot & smart systems")) {
                toAdd.add(new ThematicSession("Smart City IoT Sensor Grids & LPWAN Architecture", "11:30 AM – 12:45 PM", "Hall 1B", "IoT & Smart Systems", "Smart Cities Mission & C-DAC", false, "Day 1", "75 min", order++));
            }
            if (!existingDomains.contains("photonics & sensors")) {
                toAdd.add(new ThematicSession("Integrated Photonics, LiDAR & Optical Sensor Networks", "02:30 PM – 03:45 PM", "Hall 3", "Photonics & Sensors", "SAMEER & CEERI Scientists", false, "Day 2", "75 min", order++));
            }
            if (!existingDomains.contains("advanced materials")) {
                toAdd.add(new ThematicSession("Graphene & 2D Nanomaterials for Next-Gen Energy Storage", "04:00 PM – 05:15 PM", "Hall 1A", "Advanced Materials", "C-MET Advanced Materials Group", false, "Day 2", "75 min", order++));
            }

            if (!toAdd.isEmpty()) {
                thematicSessionRepository.saveAll(toAdd);
                log.info("Successfully seeded {} missing thematic sessions into PostgreSQL database.", toAdd.size());
            }
        } catch (Exception e) {
            log.warn("Could not seed thematic sessions table: {}", e.getMessage());
        }
    }
}

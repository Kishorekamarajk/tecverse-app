package com.tecverse.backend.controller;

import com.tecverse.backend.entity.ImportantDate;
import com.tecverse.backend.repository.ImportantDateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class ImportantDateController {

    private static final Logger log = LoggerFactory.getLogger(ImportantDateController.class);

    @Autowired
    private ImportantDateRepository importantDateRepository;

    @GetMapping({"/api/latest-news", "/api/important-dates", "/api/news"})
    public ResponseEntity<List<ImportantDate>> getImportantDates() {
        List<ImportantDate> dates = importantDateRepository.findAllByOrderByDisplayOrderAsc();
        return ResponseEntity.ok(dates);
    }

    @GetMapping("/api/important-dates/featured")
    public ResponseEntity<List<ImportantDate>> getFeaturedNews() {
        List<ImportantDate> featured = importantDateRepository.findByIsFeaturedTrueOrderByDisplayOrderAsc();
        return ResponseEntity.ok(featured);
    }

    /**
     * Seeds initial standard important dates / news items if table is empty.
     */
    @Bean
    public CommandLineRunner seedImportantDates() {
        return args -> {
            try {
                if (importantDateRepository.count() == 0) {
                    log.info("Seeding initial important dates & latest news for TEC-VERSE 2026...");
                    List<ImportantDate> seedList = Arrays.asList(
                            new ImportantDate(
                                    "TechVerse 2026 Registrations Now Open!",
                                    "26–27 November 2026",
                                    "2 days ago",
                                    "Registration",
                                    "ACTIVE",
                                    "assets/images/news_techverse.jpg",
                                    "Official delegate, researcher, and industry exhibitor registrations are now live for TEC-VERSE 2026 at Chennai Trade Centre.",
                                    1
                            ),
                            new ImportantDate(
                                    "Call for Research Papers: Deadline Extended",
                                    "15 October 2026",
                                    "5 days ago",
                                    "Papers",
                                    "ACTIVE",
                                    "assets/images/conbanner.png",
                                    "Submit your breakthrough research papers in AI, Quantum Technologies, Cybersecurity, Semiconductors, and Robotics.",
                                    2
                            ),
                            new ImportantDate(
                                    "Exhibition Passes & Stall Allocations Live",
                                    "10 November 2026",
                                    "1 week ago",
                                    "Exhibition",
                                    "UPCOMING",
                                    "assets/images/trade.png",
                                    "Standard 3x3m and Premium 6x6m exhibition stalls are now available for technology startups and premier institutions.",
                                    3
                            ),
                            new ImportantDate(
                                    "Acceptance Notification & Camera-Ready Submissions",
                                    "30 October 2026",
                                    "Upcoming",
                                    "Milestone",
                                    "UPCOMING",
                                    "assets/images/about-tec.png",
                                    "Review notifications for plenary sessions, thematic technology tracks, and scientific paper presentations.",
                                    4
                            ),
                            new ImportantDate(
                                    "Grand Inauguration & Conference Opening",
                                    "26 November 2026",
                                    "Upcoming",
                                    "Conference",
                                    "UPCOMING",
                                    "assets/images/chennai.png",
                                    "Inaugural address by MeitY, C-DAC, SAMEER, and C-MET leadership at Hall 1, Chennai Trade Centre.",
                                    5
                            )
                    );
                    importantDateRepository.saveAll(seedList);
                    log.info("Successfully seeded {} important dates into the database.", seedList.size());
                }
            } catch (Exception e) {
                log.warn("Could not seed important dates (DB may be offline): {}", e.getMessage());
            }
        };
    }
}

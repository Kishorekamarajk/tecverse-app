package com.tecverse.backend.controller;

import com.tecverse.backend.entity.ScheduleItem;
import com.tecverse.backend.repository.ScheduleRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ScheduleController {

    private static final Logger log = LoggerFactory.getLogger(ScheduleController.class);
    private final ScheduleRepository scheduleRepository;

    public ScheduleController(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    /**
     * GET /api/schedule
     * Returns all conference schedule sessions ordered by day and time.
     */
    @GetMapping("/schedule")
    public ResponseEntity<List<ScheduleItem>> getAllScheduleItems() {
        List<ScheduleItem> items = scheduleRepository.findAllByOrderByDayNumberAscDisplayOrderAsc();
        return ResponseEntity.ok(items);
    }

    /**
     * GET /api/schedule/day/{dayNumber}
     */
    @GetMapping("/schedule/day/{dayNumber}")
    public ResponseEntity<List<ScheduleItem>> getScheduleByDay(@PathVariable int dayNumber) {
        List<ScheduleItem> items = scheduleRepository.findByDayNumberOrderByDisplayOrderAsc(dayNumber);
        return ResponseEntity.ok(items);
    }

    /**
     * Seeds initial conference schedule in PostgreSQL database if the table is empty.
     */
    @PostConstruct
    public void seedInitialScheduleIfEmpty() {
        try {
            if (scheduleRepository.count() == 0) {
                log.info("Populating initial TEC-VERSE 2026 conference schedule into PostgreSQL database...");
                List<ScheduleItem> scheduleList = new ArrayList<>();

                // Day 1 Sessions (26 Nov 2026)
                scheduleList.add(new ScheduleItem(1, "Registration", "09:00 AM - 10:00 AM", false, "registration", 1));
                scheduleList.add(new ScheduleItem(1, "Arrival of Guests", "10:00 AM - 10:30 AM", false, "welcome", 2));
                scheduleList.add(new ScheduleItem(1, "Inaugural Ceremony (Lamp Lighting)", "10:35 AM - 10:45 AM", true, "ceremony", 3));
                scheduleList.add(new ScheduleItem(1, "Tamil Thai Vazhthu", "10:45 AM - 10:50 AM", false, "ceremony", 4));
                scheduleList.add(new ScheduleItem(1, "C-DAC, SAMEER & C-MET Achievements Video", "10:50 AM - 11:00 AM", false, "general", 5));
                scheduleList.add(new ScheduleItem(1, "Keynote Address by Chief Guest", "11:00 AM - 12:00 PM", true, "keynote", 6));
                scheduleList.add(new ScheduleItem(1, "Product Launch / IoA / MoU", "12:00 PM - 12:45 PM", true, "mou", 7));
                scheduleList.add(new ScheduleItem(1, "Stall Visit by Chief Guest", "01:00 PM - 02:00 PM", false, "stall", 8));
                scheduleList.add(new ScheduleItem(1, "Lunch Break", "02:00 PM - 03:00 PM", false, "lunch", 9));
                scheduleList.add(new ScheduleItem(1, "High Tea", "04:30 PM - 05:00 PM", false, "tea", 10));
                scheduleList.add(new ScheduleItem(1, "Cultural Program", "06:00 PM - 08:00 PM", true, "cultural", 11));
                scheduleList.add(new ScheduleItem(1, "Dinner", "08:00 PM - 09:30 PM", false, "lunch", 12));

                // Day 2 Sessions (27 Nov 2026)
                scheduleList.add(new ScheduleItem(2, "Registration", "09:00 AM - 10:00 AM", false, "registration", 1));
                scheduleList.add(new ScheduleItem(2, "Welcome Address", "10:00 AM - 10:15 AM", true, "welcome", 2));
                scheduleList.add(new ScheduleItem(2, "High Tea", "11:30 AM - 11:45 AM", false, "tea", 3));
                scheduleList.add(new ScheduleItem(2, "Lunch Break", "02:00 PM - 03:00 PM", false, "lunch", 4));
                scheduleList.add(new ScheduleItem(2, "MoU Signing Inauguration", "03:00 PM - 03:10 PM", true, "mou", 5));
                scheduleList.add(new ScheduleItem(2, "Keynote Address by Chief Guest", "03:20 PM - 03:40 PM", true, "keynote", 6));
                scheduleList.add(new ScheduleItem(2, "Stall Visit by College Students", "03:00 PM - 06:30 PM", false, "stall", 7));
                scheduleList.add(new ScheduleItem(2, "High Tea", "04:00 PM - 04:30 PM", false, "tea", 8));

                scheduleRepository.saveAll(scheduleList);
                log.info("Successfully loaded {} schedule items into database.", scheduleList.size());
            }
        } catch (Exception e) {
            log.warn("Could not seed schedule table: {}", e.getMessage());
        }
    }
}

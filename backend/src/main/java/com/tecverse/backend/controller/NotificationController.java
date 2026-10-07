package com.tecverse.backend.controller;

import com.tecverse.backend.entity.Notification;
import com.tecverse.backend.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);

    @Autowired
    private NotificationRepository notificationRepository;

    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications(
            @RequestParam(value = "userRef", required = false) String userRef) {
        List<Notification> list;
        if (userRef != null && !userRef.trim().isEmpty()) {
            list = notificationRepository.findForUserOrBroadcast(userRef.trim());
        } else {
            list = notificationRepository.findAllByOrderByCreatedAtDesc();
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Object>> getUnreadCount() {
        long count = notificationRepository.countByIsReadFalse();
        Map<String, Object> resp = new HashMap<>();
        resp.put("unreadCount", count);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/{id}/read")
    @PutMapping("/{id}/read")
    public ResponseEntity<Map<String, Object>> markAsRead(@PathVariable("id") Long id) {
        Optional<Notification> opt = notificationRepository.findById(id);
        Map<String, Object> resp = new HashMap<>();
        if (opt.isPresent()) {
            Notification notif = opt.get();
            notif.setIsRead(true);
            notif.setUpdatedAt(LocalDateTime.now());
            notificationRepository.save(notif);
            resp.put("success", true);
            resp.put("message", "Notification marked as read");
            return ResponseEntity.ok(resp);
        }
        resp.put("success", false);
        resp.put("message", "Notification not found");
        return ResponseEntity.status(404).body(resp);
    }

    @PostMapping("/read-all")
    @PutMapping("/read-all")
    public ResponseEntity<Map<String, Object>> markAllAsRead() {
        List<Notification> list = notificationRepository.findAll();
        for (Notification notif : list) {
            notif.setIsRead(true);
            notif.setUpdatedAt(LocalDateTime.now());
        }
        notificationRepository.saveAll(list);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("message", "All notifications marked as read");
        return ResponseEntity.ok(resp);
    }

    @PostMapping
    public ResponseEntity<Notification> createNotification(@RequestBody Notification payload) {
        if (payload.getCreatedAt() == null) {
            payload.setCreatedAt(LocalDateTime.now());
        }
        payload.setUpdatedAt(LocalDateTime.now());
        Notification saved = notificationRepository.save(payload);
        return ResponseEntity.ok(saved);
    }

    /**
     * Seeds initial notifications for TEC-VERSE 2026 if table is empty.
     */
    @Bean
    public CommandLineRunner seedNotifications() {
        return args -> {
            try {
                if (notificationRepository.count() == 0) {
                    log.info("Seeding initial notifications for TEC-VERSE 2026...");
                    List<Notification> seedList = Arrays.asList(
                            new Notification(
                                    "TEC-VERSE 2026 Digital Pass Activated",
                                    "Your official Digital Event Pass and QR Credential are ready. Present this pass at Gate 2 of Chennai Trade Centre for fast-track badge collection.",
                                    "ANNOUNCEMENT",
                                    "HIGH",
                                    "/pass"
                            ),
                            new Notification(
                                    "Keynote Schedule & Sessions Live",
                                    "Explore the newly announced plenary tracks by C-DAC, SAMEER, and C-MET. Check the schedule to bookmark your interested domains.",
                                    "SCHEDULE",
                                    "NORMAL",
                                    "/schedule"
                            ),
                            new Notification(
                                    "Venue Directions & Transport Guide",
                                    "The event is hosted at Chennai Trade Centre, Nandambakkam. Free parking and shuttle assistance are available for registered delegates.",
                                    "UPDATE",
                                    "NORMAL",
                                    "/location"
                            ),
                            new Notification(
                                    "Paper Submission Acceptance Notices Sent",
                                    "Authors of submitted research papers can check the acceptance status and camera-ready submission guidelines in the Latest News section.",
                                    "REMINDER",
                                    "NORMAL",
                                    "/news"
                            )
                    );
                    notificationRepository.saveAll(seedList);
                    log.info("Successfully seeded {} notifications.", seedList.size());
                }
            } catch (Exception e) {
                log.warn("Could not seed notifications table (might be auto-created or already managed): {}", e.getMessage());
            }
        };
    }
}

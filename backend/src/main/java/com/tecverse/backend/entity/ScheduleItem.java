package com.tecverse.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tecverse_schedules", indexes = {
    @Index(name = "idx_schedule_day", columnList = "day_number")
})
public class ScheduleItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "day_number", nullable = false)
    private int dayNumber; // 1 = Day 1, 2 = Day 2

    @Column(name = "session_title", nullable = false, length = 255)
    private String sessionTitle;

    @Column(name = "time_label", nullable = false, length = 100)
    private String timeLabel; // e.g. "09:00 AM - 10:00 AM"

    @Column(nullable = false)
    private boolean highlighted;

    @Column(length = 50)
    private String category;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    public ScheduleItem() {
    }

    public ScheduleItem(int dayNumber, String sessionTitle, String timeLabel, boolean highlighted, String category, int displayOrder) {
        this.dayNumber = dayNumber;
        this.sessionTitle = sessionTitle;
        this.timeLabel = timeLabel;
        this.highlighted = highlighted;
        this.category = category;
        this.displayOrder = displayOrder;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(int dayNumber) {
        this.dayNumber = dayNumber;
    }

    public String getSessionTitle() {
        return sessionTitle;
    }

    public void setSessionTitle(String sessionTitle) {
        this.sessionTitle = sessionTitle;
    }

    public String getTimeLabel() {
        return timeLabel;
    }

    public void setTimeLabel(String timeLabel) {
        this.timeLabel = timeLabel;
    }

    public boolean isHighlighted() {
        return highlighted;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }
}

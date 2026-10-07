package com.tecverse.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tecverse_thematic_sessions", indexes = {
    @Index(name = "idx_thematic_domain", columnList = "domain"),
    @Index(name = "idx_thematic_day", columnList = "day_label")
})
public class ThematicSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "time_label", nullable = false, length = 100)
    private String timeLabel;

    @Column(nullable = false, length = 100)
    private String hall;

    @Column(nullable = false, length = 100)
    private String domain;

    @Column(length = 255)
    private String speaker;

    @Column(name = "is_highlight", nullable = false)
    private boolean isHighlight;

    @Column(name = "day_label", nullable = false, length = 50)
    private String dayLabel;

    @Column(length = 50)
    private String duration;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    public ThematicSession() {
    }

    public ThematicSession(String title, String timeLabel, String hall, String domain, String speaker, boolean isHighlight, String dayLabel, String duration, int displayOrder) {
        this.title = title;
        this.timeLabel = timeLabel;
        this.hall = hall;
        this.domain = domain;
        this.speaker = speaker;
        this.isHighlight = isHighlight;
        this.dayLabel = dayLabel;
        this.duration = duration;
        this.displayOrder = displayOrder;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTimeLabel() {
        return timeLabel;
    }

    public void setTimeLabel(String timeLabel) {
        this.timeLabel = timeLabel;
    }

    public String getHall() {
        return hall;
    }

    public void setHall(String hall) {
        this.hall = hall;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getSpeaker() {
        return speaker;
    }

    public void setSpeaker(String speaker) {
        this.speaker = speaker;
    }

    public boolean isHighlight() {
        return isHighlight;
    }

    public void setHighlight(boolean highlight) {
        isHighlight = highlight;
    }

    public String getDayLabel() {
        return dayLabel;
    }

    public void setDayLabel(String dayLabel) {
        this.dayLabel = dayLabel;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }
}

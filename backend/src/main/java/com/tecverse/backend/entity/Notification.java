package com.tecverse.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "category", length = 50)
    private String category = "ANNOUNCEMENT"; // ANNOUNCEMENT, ALERT, REMINDER, SCHEDULE, UPDATE

    @Column(name = "priority", length = 20)
    private String priority = "NORMAL"; // HIGH, NORMAL, LOW

    @Column(name = "action_route", length = 255)
    private String actionRoute; // e.g. /pass, /schedule, /sessions, /location

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(name = "is_broadcast")
    private Boolean isBroadcast = true;

    @Column(name = "user_reference_number", length = 100)
    private String userReferenceNumber;

    @Column(name = "is_read")
    private Boolean isRead = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Notification() {}

    public Notification(String title, String message, String category, String priority, String actionRoute) {
        this.title = title;
        this.message = message;
        this.category = category;
        this.priority = priority;
        this.actionRoute = actionRoute;
        this.isBroadcast = true;
        this.isRead = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Notification(String title, String message, String category, String priority, String actionRoute, String imageUrl) {
        this(title, message, category, priority, actionRoute);
        this.imageUrl = imageUrl;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
        if (this.isBroadcast == null) {
            this.isBroadcast = true;
        }
        if (this.isRead == null) {
            this.isRead = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters & Setters
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getActionRoute() {
        return actionRoute;
    }

    public void setActionRoute(String actionRoute) {
        this.actionRoute = actionRoute;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getIsBroadcast() {
        return isBroadcast;
    }

    public void setIsBroadcast(Boolean broadcast) {
        isBroadcast = broadcast;
    }

    public String getUserReferenceNumber() {
        return userReferenceNumber;
    }

    public void setUserReferenceNumber(String userReferenceNumber) {
        this.userReferenceNumber = userReferenceNumber;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean read) {
        isRead = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

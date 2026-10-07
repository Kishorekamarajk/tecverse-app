package com.tecverse.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tecverse_ticket_registrations")
public class TicketRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reference_number", nullable = false, length = 12)
    private String referenceNumber;

    @Column(name = "official_name", nullable = false, length = 150)
    private String officialName;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(name = "password_hash", length = 60)
    private String passwordHash;

    @Column(nullable = false, length = 40)
    private String category;

    @Column(name = "academia_type", length = 30)
    private String academiaType;

    @Column(name = "college_name", length = 250)
    private String collegeName;

    @Column(name = "college_district", length = 120)
    private String collegeDistrict;

    @Column(name = "college_state", length = 120)
    private String collegeState;

    @Column(name = "university_name", length = 250)
    private String universityName;

    @Column(name = "academia_role", length = 30)
    private String academiaRole;

    @Column(name = "register_number", length = 100)
    private String registerNumber;

    @Column(name = "central_ministry", length = 250)
    private String centralMinistry;

    @Column(name = "state_name", length = 120)
    private String stateName;

    @Column(name = "state_department", length = 250)
    private String stateDepartment;

    @Column(name = "organization_name", length = 250)
    private String organizationName;

    @Column(name = "organization_location", length = 250)
    private String organizationLocation;

    @Column(length = 150)
    private String designation;

    @Column(name = "industry_or_startup", length = 30)
    private String industryOrStartup;

    @Column(name = "citizenship_status", nullable = false, length = 20)
    private String citizenshipStatus;

    @Column(name = "passport_number", length = 50)
    private String passportNumber;

    @Column(name = "passport_valid_until", length = 30)
    private String passportValidUntil;

    @Column(name = "passport_name", length = 150)
    private String passportName;

    @Column(name = "attendance_days", nullable = false, length = 100)
    private String attendanceDays;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public TicketRegistration() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getOfficialName() {
        return officialName;
    }

    public void setOfficialName(String officialName) {
        this.officialName = officialName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAcademiaType() {
        return academiaType;
    }

    public void setAcademiaType(String academiaType) {
        this.academiaType = academiaType;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getCollegeDistrict() {
        return collegeDistrict;
    }

    public void setCollegeDistrict(String collegeDistrict) {
        this.collegeDistrict = collegeDistrict;
    }

    public String getCollegeState() {
        return collegeState;
    }

    public void setCollegeState(String collegeState) {
        this.collegeState = collegeState;
    }

    public String getUniversityName() {
        return universityName;
    }

    public void setUniversityName(String universityName) {
        this.universityName = universityName;
    }

    public String getAcademiaRole() {
        return academiaRole;
    }

    public void setAcademiaRole(String academiaRole) {
        this.academiaRole = academiaRole;
    }

    public String getRegisterNumber() {
        return registerNumber;
    }

    public void setRegisterNumber(String registerNumber) {
        this.registerNumber = registerNumber;
    }

    public String getCentralMinistry() {
        return centralMinistry;
    }

    public void setCentralMinistry(String centralMinistry) {
        this.centralMinistry = centralMinistry;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getStateDepartment() {
        return stateDepartment;
    }

    public void setStateDepartment(String stateDepartment) {
        this.stateDepartment = stateDepartment;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getOrganizationLocation() {
        return organizationLocation;
    }

    public void setOrganizationLocation(String organizationLocation) {
        this.organizationLocation = organizationLocation;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getIndustryOrStartup() {
        return industryOrStartup;
    }

    public void setIndustryOrStartup(String industryOrStartup) {
        this.industryOrStartup = industryOrStartup;
    }

    public String getCitizenshipStatus() {
        return citizenshipStatus;
    }

    public void setCitizenshipStatus(String citizenshipStatus) {
        this.citizenshipStatus = citizenshipStatus;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public String getPassportValidUntil() {
        return passportValidUntil;
    }

    public void setPassportValidUntil(String passportValidUntil) {
        this.passportValidUntil = passportValidUntil;
    }

    public String getPassportName() {
        return passportName;
    }

    public void setPassportName(String passportName) {
        this.passportName = passportName;
    }

    public String getAttendanceDays() {
        return attendanceDays;
    }

    public void setAttendanceDays(String attendanceDays) {
        this.attendanceDays = attendanceDays;
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

package com.rssb.assistance.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "person_registrations")
public class PersonRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long registrationId;

    /** Local offline ID assigned by the client device before sync */
    @Column
    private String localDeviceRecordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parking_location_id")
    private ParkingLocation parkingLocation;

    @Column(nullable = false)
    private String personName;

    @Column(nullable = false)
    private String phoneNumber;

    private String attendantName;

    private String vehicleNumber;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private UserType userType = UserType.VISITOR;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id")
    private Photo photo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registered_by_user_id")
    private AppUser registeredByUser;

    @Column(nullable = false)
    private LocalDateTime registrationTime = LocalDateTime.now();

    @Column(nullable = false)
    private Boolean completedFlag = false;

    private LocalDateTime completionTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "completed_by_user_id")
    private AppUser completedByUser;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SyncStatus syncStatus = SyncStatus.PENDING;

    private LocalDateTime syncStatusUpdatedAt;

    @Column(columnDefinition = "TEXT")
    private String comments;

    private LocalDateTime commentsUpdatedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // --- Getters and Setters ---

    public Long getRegistrationId() { return registrationId; }
    public void setRegistrationId(Long registrationId) { this.registrationId = registrationId; }

    public String getLocalDeviceRecordId() { return localDeviceRecordId; }
    public void setLocalDeviceRecordId(String localDeviceRecordId) { this.localDeviceRecordId = localDeviceRecordId; }

    public ParkingLocation getParkingLocation() { return parkingLocation; }
    public void setParkingLocation(ParkingLocation parkingLocation) { this.parkingLocation = parkingLocation; }

    public String getPersonName() { return personName; }
    public void setPersonName(String personName) { this.personName = personName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAttendantName() { return attendantName; }
    public void setAttendantName(String attendantName) { this.attendantName = attendantName; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public UserType getUserType() { return userType; }
    public void setUserType(UserType userType) { this.userType = userType; }

    public Photo getPhoto() { return photo; }
    public void setPhoto(Photo photo) { this.photo = photo; }

    public AppUser getRegisteredByUser() { return registeredByUser; }
    public void setRegisteredByUser(AppUser registeredByUser) { this.registeredByUser = registeredByUser; }

    public LocalDateTime getRegistrationTime() { return registrationTime; }
    public void setRegistrationTime(LocalDateTime registrationTime) { this.registrationTime = registrationTime; }

    public Boolean getCompletedFlag() { return completedFlag; }
    public void setCompletedFlag(Boolean completedFlag) { this.completedFlag = completedFlag; }

    public LocalDateTime getCompletionTime() { return completionTime; }
    public void setCompletionTime(LocalDateTime completionTime) { this.completionTime = completionTime; }

    public AppUser getCompletedByUser() { return completedByUser; }
    public void setCompletedByUser(AppUser completedByUser) { this.completedByUser = completedByUser; }

    public SyncStatus getSyncStatus() { return syncStatus; }
    public void setSyncStatus(SyncStatus syncStatus) { this.syncStatus = syncStatus; }

    public LocalDateTime getSyncStatusUpdatedAt() { return syncStatusUpdatedAt; }
    public void setSyncStatusUpdatedAt(LocalDateTime syncStatusUpdatedAt) { this.syncStatusUpdatedAt = syncStatusUpdatedAt; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public LocalDateTime getCommentsUpdatedAt() { return commentsUpdatedAt; }
    public void setCommentsUpdatedAt(LocalDateTime commentsUpdatedAt) { this.commentsUpdatedAt = commentsUpdatedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

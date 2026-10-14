package com.rssb.assistance.dto;

import java.time.LocalDateTime;

public class PersonRegistrationResponse {

    private Long registrationId;
    private String localDeviceRecordId;
    private Long parkingLocationId;
    private String parkingLocationName;
    private String personName;
    private String phoneNumber;
    private String attendantName;
    private String vehicleNumber;
    private String userType;
    private Long photoId;
    private String photoFilePath;
    private Long registeredByUserId;
    private String registeredByUsername;
    private LocalDateTime registrationTime;
    private Boolean completedFlag;
    private LocalDateTime completionTime;
    private Long completedByUserId;
    private String completedByUsername;
    private String syncStatus;
    private LocalDateTime syncStatusUpdatedAt;
    private String comments;
    private LocalDateTime commentsUpdatedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getRegistrationId() { return registrationId; }
    public void setRegistrationId(Long registrationId) { this.registrationId = registrationId; }

    public String getLocalDeviceRecordId() { return localDeviceRecordId; }
    public void setLocalDeviceRecordId(String localDeviceRecordId) { this.localDeviceRecordId = localDeviceRecordId; }

    public Long getParkingLocationId() { return parkingLocationId; }
    public void setParkingLocationId(Long parkingLocationId) { this.parkingLocationId = parkingLocationId; }

    public String getParkingLocationName() { return parkingLocationName; }
    public void setParkingLocationName(String parkingLocationName) { this.parkingLocationName = parkingLocationName; }

    public String getPersonName() { return personName; }
    public void setPersonName(String personName) { this.personName = personName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAttendantName() { return attendantName; }
    public void setAttendantName(String attendantName) { this.attendantName = attendantName; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public Long getPhotoId() { return photoId; }
    public void setPhotoId(Long photoId) { this.photoId = photoId; }

    public String getPhotoFilePath() { return photoFilePath; }
    public void setPhotoFilePath(String photoFilePath) { this.photoFilePath = photoFilePath; }

    public Long getRegisteredByUserId() { return registeredByUserId; }
    public void setRegisteredByUserId(Long registeredByUserId) { this.registeredByUserId = registeredByUserId; }

    public String getRegisteredByUsername() { return registeredByUsername; }
    public void setRegisteredByUsername(String registeredByUsername) { this.registeredByUsername = registeredByUsername; }

    public LocalDateTime getRegistrationTime() { return registrationTime; }
    public void setRegistrationTime(LocalDateTime registrationTime) { this.registrationTime = registrationTime; }

    public Boolean getCompletedFlag() { return completedFlag; }
    public void setCompletedFlag(Boolean completedFlag) { this.completedFlag = completedFlag; }

    public LocalDateTime getCompletionTime() { return completionTime; }
    public void setCompletionTime(LocalDateTime completionTime) { this.completionTime = completionTime; }

    public Long getCompletedByUserId() { return completedByUserId; }
    public void setCompletedByUserId(Long completedByUserId) { this.completedByUserId = completedByUserId; }

    public String getCompletedByUsername() { return completedByUsername; }
    public void setCompletedByUsername(String completedByUsername) { this.completedByUsername = completedByUsername; }

    public String getSyncStatus() { return syncStatus; }
    public void setSyncStatus(String syncStatus) { this.syncStatus = syncStatus; }

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

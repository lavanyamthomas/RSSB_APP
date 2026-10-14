package com.rssb.assistance.dto;

public class PersonRegistrationUpdateRequest {

    private String localDeviceRecordId;
    private String personName;
    private String phoneNumber;
    private String attendantName;
    private String vehicleNumber;
    private String userType;
    private Long parkingLocationId;
    private Long photoId;
    private String photoFilePath;
    private String syncStatus;
    private String comments;
    private Boolean completedFlag;

    public String getLocalDeviceRecordId() { return localDeviceRecordId; }
    public void setLocalDeviceRecordId(String localDeviceRecordId) { this.localDeviceRecordId = localDeviceRecordId; }

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

    public Long getParkingLocationId() { return parkingLocationId; }
    public void setParkingLocationId(Long parkingLocationId) { this.parkingLocationId = parkingLocationId; }

    public Long getPhotoId() { return photoId; }
    public void setPhotoId(Long photoId) { this.photoId = photoId; }

    public String getPhotoFilePath() { return photoFilePath; }
    public void setPhotoFilePath(String photoFilePath) { this.photoFilePath = photoFilePath; }

    public String getSyncStatus() { return syncStatus; }
    public void setSyncStatus(String syncStatus) { this.syncStatus = syncStatus; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public Boolean getCompletedFlag() { return completedFlag; }
    public void setCompletedFlag(Boolean completedFlag) { this.completedFlag = completedFlag; }
}

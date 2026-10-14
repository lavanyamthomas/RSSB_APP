package com.rssb.assistance.dto;

import jakarta.validation.constraints.NotBlank;

public class PersonRegistrationRequest {

    /** Optional: local offline ID from the client device */
    private String localDeviceRecordId;

    private Long parkingLocationId;

    @NotBlank(message = "Person name is required")
    private String personName;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    private String attendantName;

    private String vehicleNumber;

    private String userType;

    /** Optional: ID of an already-uploaded Photo record */
    private Long photoId;

    /** File path / URL submitted when creating a new photo inline */
    private String photoFilePath;

    public String getLocalDeviceRecordId() { return localDeviceRecordId; }
    public void setLocalDeviceRecordId(String localDeviceRecordId) { this.localDeviceRecordId = localDeviceRecordId; }

    public Long getParkingLocationId() { return parkingLocationId; }
    public void setParkingLocationId(Long parkingLocationId) { this.parkingLocationId = parkingLocationId; }

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
}

package com.rssb.assistance.service;

import com.rssb.assistance.dto.ParkingLocationDto;
import com.rssb.assistance.dto.PersonRegistrationRequest;
import com.rssb.assistance.dto.PersonRegistrationResponse;
import com.rssb.assistance.dto.PersonRegistrationUpdateRequest;
import com.rssb.assistance.entity.*;
import com.rssb.assistance.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonRegistrationService {

    private final PersonRegistrationRepository registrationRepository;
    private final ParkingLocationRepository parkingLocationRepository;
    private final PhotoRepository photoRepository;
    private final AppUserRepository appUserRepository;

    public PersonRegistrationService(PersonRegistrationRepository registrationRepository,
                                     ParkingLocationRepository parkingLocationRepository,
                                     PhotoRepository photoRepository,
                                     AppUserRepository appUserRepository) {
        this.registrationRepository = registrationRepository;
        this.parkingLocationRepository = parkingLocationRepository;
        this.photoRepository = photoRepository;
        this.appUserRepository = appUserRepository;
    }

    @Transactional
    public PersonRegistrationResponse create(PersonRegistrationRequest request) {
        String localDeviceRecordId = request.getLocalDeviceRecordId() != null
                ? request.getLocalDeviceRecordId().trim()
                : null;
        String personName = request.getPersonName() != null ? request.getPersonName().trim() : null;
        String phoneNumber = request.getPhoneNumber() != null ? request.getPhoneNumber().trim() : null;
        String vehicleNumber = normalizeOptionalValue(request.getVehicleNumber());

        if (personName == null || personName.isBlank()) {
            throw new IllegalArgumentException("Person name is required");
        }
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Phone number is required");
        }

        ensureUniqueRegistrationCombination(personName, phoneNumber, vehicleNumber, request.getParkingLocationId(), null);

        PersonRegistration reg = new PersonRegistration();

        reg.setLocalDeviceRecordId(localDeviceRecordId == null || localDeviceRecordId.isBlank() ? null : localDeviceRecordId);
        reg.setPersonName(personName);
        reg.setPhoneNumber(phoneNumber);
        reg.setAttendantName(request.getAttendantName());
        reg.setVehicleNumber(vehicleNumber);
        reg.setUserType(parseUserType(request.getUserType()));

        if (request.getParkingLocationId() != null) {
            ParkingLocation loc = parkingLocationRepository.findById(request.getParkingLocationId())
                    .orElseThrow(() -> new IllegalArgumentException("Parking location not found: " + request.getParkingLocationId()));
            reg.setParkingLocation(loc);
        }

        if (request.getPhotoId() != null) {
            Photo photo = photoRepository.findById(request.getPhotoId())
                    .orElseThrow(() -> new IllegalArgumentException("Photo not found: " + request.getPhotoId()));
            reg.setPhoto(photo);
        } else if (request.getPhotoFilePath() != null && !request.getPhotoFilePath().isBlank()) {
            Photo newPhoto = new Photo();
            newPhoto.setFilePath(request.getPhotoFilePath());
            reg.setPhoto(photoRepository.save(newPhoto));
        }

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        appUserRepository.findByUsername(currentUsername).ifPresent(reg::setRegisteredByUser);

        reg.setRegistrationTime(LocalDateTime.now());
        reg.setCompletedFlag(false);
        reg.setSyncStatus(SyncStatus.PENDING);
        reg.setSyncStatusUpdatedAt(LocalDateTime.now());

        return toResponse(registrationRepository.save(reg));
    }

    @Transactional(readOnly = true)
    public PersonRegistrationResponse getById(Long id) {
        return registrationRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Registration not found: " + id));
    }

    @Transactional(readOnly = true)
    public Page<PersonRegistrationResponse> getAll(Pageable pageable) {
        return registrationRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<PersonRegistrationResponse> search(String query,
                                                   String location,
                                                   Boolean completedFlag,
                                                   String syncStatus,
                                                   Pageable pageable) {
        Specification<PersonRegistration> spec = Specification.where(null);

        if (query != null && !query.isBlank()) {
            String q = "%" + query.trim().toLowerCase(Locale.ROOT) + "%";
            spec = spec.and((root, cq, cb) -> cb.or(
                    cb.like(cb.lower(root.get("personName")), q),
                    cb.like(cb.lower(root.get("phoneNumber")), q),
                    cb.like(cb.lower(root.get("vehicleNumber")), q),
                    cb.like(cb.lower(root.get("localDeviceRecordId")), q)
            ));
        }

        if (location != null && !location.isBlank() && !"all".equalsIgnoreCase(location)) {
            String normalizedLocation = location.trim().toLowerCase(Locale.ROOT).replace('-', ' ');
            spec = spec.and((root, cq, cb) -> cb.like(
                    cb.lower(root.join("parkingLocation", jakarta.persistence.criteria.JoinType.LEFT).get("name")),
                    "%" + normalizedLocation + "%"
            ));
        }

        if (completedFlag != null) {
            spec = spec.and((root, cq, cb) -> cb.equal(root.get("completedFlag"), completedFlag));
        }
        if (syncStatus != null && !syncStatus.isBlank()) {
            SyncStatus status = parseSyncStatus(syncStatus);
            spec = spec.and((root, cq, cb) -> cb.equal(root.get("syncStatus"), status));
        }

        return registrationRepository.findAll(spec, pageable).map(this::toResponse);
    }

    @Transactional
    public PersonRegistrationResponse update(Long id, PersonRegistrationUpdateRequest request) {
        PersonRegistration reg = registrationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Registration not found: " + id));

        String effectivePersonName = request.getPersonName() != null && !request.getPersonName().isBlank()
            ? request.getPersonName().trim()
            : reg.getPersonName();
        String effectivePhoneNumber = request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()
            ? request.getPhoneNumber().trim()
            : reg.getPhoneNumber();
        String effectiveVehicleNumber = request.getVehicleNumber() != null
            ? normalizeOptionalValue(request.getVehicleNumber())
            : reg.getVehicleNumber();
        Long effectiveParkingLocationId = request.getParkingLocationId() != null
            ? request.getParkingLocationId()
            : (reg.getParkingLocation() != null ? reg.getParkingLocation().getId() : null);

        ensureUniqueRegistrationCombination(effectivePersonName, effectivePhoneNumber, effectiveVehicleNumber, effectiveParkingLocationId, reg.getRegistrationId());

        if (request.getLocalDeviceRecordId() != null) {
            String localDeviceRecordId = request.getLocalDeviceRecordId().trim();
            reg.setLocalDeviceRecordId(localDeviceRecordId.isBlank() ? null : localDeviceRecordId);
        }
        if (request.getPersonName() != null && !request.getPersonName().isBlank()) {
            reg.setPersonName(request.getPersonName());
        }
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
            String phoneNumber = request.getPhoneNumber().trim();
            reg.setPhoneNumber(phoneNumber);
        }
        if (request.getAttendantName() != null) {
            reg.setAttendantName(request.getAttendantName());
        }
        if (request.getVehicleNumber() != null) {
            reg.setVehicleNumber(request.getVehicleNumber());
        }
        if (request.getUserType() != null && !request.getUserType().isBlank()) {
            reg.setUserType(parseUserType(request.getUserType()));
        }
        if (request.getParkingLocationId() != null) {
            ParkingLocation loc = parkingLocationRepository.findById(request.getParkingLocationId())
                    .orElseThrow(() -> new IllegalArgumentException("Parking location not found: " + request.getParkingLocationId()));
            reg.setParkingLocation(loc);
        }
        if (request.getPhotoId() != null) {
            Photo photo = photoRepository.findById(request.getPhotoId())
                    .orElseThrow(() -> new IllegalArgumentException("Photo not found: " + request.getPhotoId()));
            reg.setPhoto(photo);
        } else if (request.getPhotoFilePath() != null && !request.getPhotoFilePath().isBlank()) {
            Photo newPhoto = new Photo();
            newPhoto.setFilePath(request.getPhotoFilePath());
            reg.setPhoto(photoRepository.save(newPhoto));
        }
        if (request.getSyncStatus() != null && !request.getSyncStatus().isBlank()) {
            SyncStatus incomingStatus = parseSyncStatus(request.getSyncStatus());
            if (reg.getSyncStatus() != incomingStatus) {
                reg.setSyncStatus(incomingStatus);
                reg.setSyncStatusUpdatedAt(LocalDateTime.now());
            }
        }
        if (request.getComments() != null) {
            String incomingComments = request.getComments();
            String existingComments = reg.getComments();
            if (!java.util.Objects.equals(existingComments, incomingComments)) {
                reg.setComments(incomingComments);
                reg.setCommentsUpdatedAt(LocalDateTime.now());
            }
        }
        if (request.getCompletedFlag() != null) {
            Boolean wasCompleted = reg.getCompletedFlag();
            reg.setCompletedFlag(request.getCompletedFlag());
            if (Boolean.TRUE.equals(request.getCompletedFlag()) && reg.getCompletionTime() == null) {
                reg.setCompletionTime(LocalDateTime.now());
            }
            if (Boolean.TRUE.equals(request.getCompletedFlag())
                    && (!Boolean.TRUE.equals(wasCompleted) || reg.getCompletedByUser() == null)) {
                String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
                appUserRepository.findByUsername(currentUsername).ifPresent(reg::setCompletedByUser);
            }
        }

        return toResponse(registrationRepository.save(reg));
    }

    private void ensureUniqueRegistrationCombination(String personName,
                                                     String phoneNumber,
                                                     String vehicleNumber,
                                                     Long parkingLocationId,
                                                     Long excludeId) {
        if (registrationRepository.existsByRegistrationCombination(
                personName,
                phoneNumber,
                vehicleNumber,
                parkingLocationId,
                excludeId)) {
            throw new IllegalArgumentException("A registration with the same name, phone number, vehicle number, and parking location already exists.");
        }
    }

    private String normalizeOptionalValue(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isBlank() ? null : trimmed;
    }

    @Transactional
    public void delete(Long id) {
        if (!registrationRepository.existsById(id)) {
            throw new IllegalArgumentException("Registration not found: " + id);
        }
        registrationRepository.deleteById(id);
    }

    @Transactional
    public PersonRegistrationResponse markComplete(Long id) {
        PersonRegistration reg = registrationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Registration not found: " + id));

        reg.setCompletedFlag(true);
        reg.setCompletionTime(LocalDateTime.now());
        reg.setSyncStatus(SyncStatus.COMPLETE);
        reg.setSyncStatusUpdatedAt(LocalDateTime.now());

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        appUserRepository.findByUsername(currentUsername).ifPresent(reg::setCompletedByUser);

        return toResponse(registrationRepository.save(reg));
    }

    public List<ParkingLocationDto> getParkingLocations() {
        return parkingLocationRepository.findAll().stream()
                .map(loc -> new ParkingLocationDto(loc.getId(), loc.getName(), loc.getDescription()))
                .collect(Collectors.toList());
    }

    private PersonRegistrationResponse toResponse(PersonRegistration reg) {
        PersonRegistrationResponse dto = new PersonRegistrationResponse();
        dto.setRegistrationId(reg.getRegistrationId());
        dto.setLocalDeviceRecordId(reg.getLocalDeviceRecordId());
        dto.setPersonName(reg.getPersonName());
        dto.setPhoneNumber(reg.getPhoneNumber());
        dto.setAttendantName(reg.getAttendantName());
        dto.setVehicleNumber(reg.getVehicleNumber());
        dto.setUserType(reg.getUserType() != null ? reg.getUserType().name() : UserType.VISITOR.name());
        dto.setRegistrationTime(reg.getRegistrationTime());
        dto.setCompletedFlag(reg.getCompletedFlag());
        dto.setCompletionTime(reg.getCompletionTime());
        dto.setSyncStatus(toCanonicalStatus(reg.getSyncStatus()));
        dto.setSyncStatusUpdatedAt(reg.getSyncStatusUpdatedAt());
        dto.setComments(reg.getComments());
        dto.setCommentsUpdatedAt(reg.getCommentsUpdatedAt());
        dto.setCreatedAt(reg.getCreatedAt());
        dto.setUpdatedAt(reg.getUpdatedAt());

        if (reg.getParkingLocation() != null) {
            dto.setParkingLocationId(reg.getParkingLocation().getId());
            dto.setParkingLocationName(reg.getParkingLocation().getName());
        }
        if (reg.getPhoto() != null) {
            dto.setPhotoId(reg.getPhoto().getId());
            dto.setPhotoFilePath(reg.getPhoto().getFilePath());
        }
        if (reg.getRegisteredByUser() != null) {
            dto.setRegisteredByUserId(reg.getRegisteredByUser().getId());
            dto.setRegisteredByUsername(reg.getRegisteredByUser().getUsername());
        }
        if (reg.getCompletedByUser() != null) {
            dto.setCompletedByUserId(reg.getCompletedByUser().getId());
            dto.setCompletedByUsername(reg.getCompletedByUser().getUsername());
        }
        return dto;
    }

    private SyncStatus parseSyncStatus(String value) {
        String normalized = value.trim().toUpperCase(Locale.ROOT);
        if ("PEDING".equals(normalized)) {
            return SyncStatus.PENDING;
        }
        if ("SYNCED".equals(normalized)) {
            return SyncStatus.COMPLETE;
        }
        if ("FAILED".equals(normalized)) {
            return SyncStatus.SEARCHING;
        }
        return SyncStatus.valueOf(normalized);
    }

    private String toCanonicalStatus(SyncStatus status) {
        if (status == null) {
            return null;
        }
        if (status == SyncStatus.SYNCED) {
            return SyncStatus.COMPLETE.name();
        }
        if (status == SyncStatus.FAILED) {
            return SyncStatus.SEARCHING.name();
        }
        return status.name();
    }

    private UserType parseUserType(String value) {
        if (value == null || value.isBlank()) {
            return UserType.VISITOR;
        }

        String normalized = value.trim().toUpperCase(Locale.ROOT);
        if ("USER".equals(normalized) || "SEWADAR".equals(normalized)) {
            return UserType.SEWADAR;
        }
        if ("VISITOR".equals(normalized)) {
            return UserType.VISITOR;
        }
        throw new IllegalArgumentException("Invalid user type. Use VISITOR or SEWADAR.");
    }
}

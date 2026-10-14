package com.rssb.assistance.repository;

import com.rssb.assistance.entity.ParkingLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingLocationRepository extends JpaRepository<ParkingLocation, Long> {
}

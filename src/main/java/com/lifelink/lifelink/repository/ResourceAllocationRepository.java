package com.lifelink.lifelink.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lifelink.lifelink.entity.EmergencyNeed;
import com.lifelink.lifelink.entity.Resource;
import com.lifelink.lifelink.entity.ResourceAllocation;

public interface ResourceAllocationRepository
        extends JpaRepository<ResourceAllocation, Long> {

    Optional<ResourceAllocation> findByEmergencyNeedAndResource(
            EmergencyNeed emergencyNeed,
            Resource resource);
}
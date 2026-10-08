package com.lifelink.lifelink.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.lifelink.lifelink.entity.EmergencyNeed;
import com.lifelink.lifelink.entity.Resource;
import com.lifelink.lifelink.entity.ResourceAllocation;

@Service
public class ResourceMatchingService {

    // =========================================================
    // CALCULATE SCORE BREAKDOWN
    // =========================================================

    public MatchScoreBreakdown calculateScoreBreakdown(
            EmergencyNeed need,
            Resource resource) {

        int compatibilityScore = 0;
        int urgencyScore = 0;
        int availabilityScore = 0;
        int locationScore = 0;
        int priorityScore = 0;

        // -----------------------------------------------------
        // 1. NEED-RESOURCE COMPATIBILITY
        // -----------------------------------------------------

        if (isCompatible(need, resource)) {

            compatibilityScore = 30;

        } else {

            return new MatchScoreBreakdown(
                    0,
                    0,
                    0,
                    0,
                    0);
        }

        // -----------------------------------------------------
        // 2. URGENCY
        // -----------------------------------------------------

        if (need.getUrgency() >= 5) {

            urgencyScore = 25;

        } else if (need.getUrgency() >= 3) {

            urgencyScore = 15;

        } else {

            urgencyScore = 5;
        }

        // -----------------------------------------------------
        // 3. RESOURCE AVAILABILITY
        // -----------------------------------------------------

        if ("AVAILABLE".equalsIgnoreCase(
                resource.getAvailabilityStatus())) {

            availabilityScore = 20;

        } else {

            // Resource is not available.
            return new MatchScoreBreakdown(
                    compatibilityScore,
                    urgencyScore,
                    0,
                    0,
                    0);
        }

        // -----------------------------------------------------
        // 4. RESOURCE QUANTITY
        // -----------------------------------------------------

        if (resource.getQuantity() <= 0) {

            // Resource exists but has no usable quantity.
            return new MatchScoreBreakdown(
                    compatibilityScore,
                    urgencyScore,
                    availabilityScore,
                    0,
                    0);
        }

        // -----------------------------------------------------
        // 5. LOCATION MATCH
        // -----------------------------------------------------

        if (need.getLocation() != null
                && resource.getLocation() != null
                && need.getLocation()
                        .equalsIgnoreCase(resource.getLocation())) {

            locationScore = 15;
        }

        // -----------------------------------------------------
        // 6. RESOURCE PRIORITY
        // -----------------------------------------------------

        if (resource.getPriority() >= 5) {

            priorityScore = 10;

        } else if (resource.getPriority() >= 3) {

            priorityScore = 7;

        } else {

            priorityScore = 3;
        }

        // -----------------------------------------------------
        // RETURN COMPLETE SCORE BREAKDOWN
        // -----------------------------------------------------

        return new MatchScoreBreakdown(
                compatibilityScore,
                urgencyScore,
                availabilityScore,
                locationScore,
                priorityScore);
    }

    // =========================================================
    // CALCULATE MATCH SCORE
    // =========================================================

    public int calculateMatchScore(
            EmergencyNeed need,
            Resource resource) {

        MatchScoreBreakdown breakdown =
                calculateScoreBreakdown(
                        need,
                        resource);

        return breakdown.getTotalScore();
    }

    // =========================================================
    // CHECK RESOURCE COMPATIBILITY
    // =========================================================

    public boolean isCompatible(
            EmergencyNeed need,
            Resource resource) {

        String needType = need.getNeedType();
        String resourceType = resource.getType();

        // Prevent null values from causing problems.
        if (needType == null
                || resourceType == null) {

            return false;
        }

        // -----------------------------------------------------
        // MEDICAL → AMBULANCE
        // -----------------------------------------------------

        if ("MEDICAL".equalsIgnoreCase(needType)
                && "AMBULANCE".equalsIgnoreCase(resourceType)) {

            return true;
        }

        // -----------------------------------------------------
        // FOOD → FOOD
        // -----------------------------------------------------

        if ("FOOD".equalsIgnoreCase(needType)
                && "FOOD".equalsIgnoreCase(resourceType)) {

            return true;
        }

        // -----------------------------------------------------
        // WATER → WATER
        // -----------------------------------------------------

        if ("WATER".equalsIgnoreCase(needType)
                && "WATER".equalsIgnoreCase(resourceType)) {

            return true;
        }

        // -----------------------------------------------------
        // SHELTER → SHELTER
        // -----------------------------------------------------

        if ("SHELTER".equalsIgnoreCase(needType)
                && "SHELTER".equalsIgnoreCase(resourceType)) {

            return true;
        }

        // -----------------------------------------------------
        // MEDICINE → MEDICINE
        // -----------------------------------------------------

        if ("MEDICINE".equalsIgnoreCase(needType)
                && "MEDICINE".equalsIgnoreCase(resourceType)) {

            return true;
        }

        // No compatible resource found.
        return false;
    }

    // =========================================================
    // GENERATE EXPLANATION
    // =========================================================

    public String generateRecommendationReason(
            EmergencyNeed need,
            Resource resource) {

        StringBuilder reason =
                new StringBuilder();

        MatchScoreBreakdown breakdown =
                calculateScoreBreakdown(
                        need,
                        resource);

        // -----------------------------------------------------
        // 1. COMPATIBILITY
        // -----------------------------------------------------

        if (breakdown.getCompatibilityScore() > 0) {

            reason.append(
                    "Resource type is compatible with the emergency need; ");
        }

        // -----------------------------------------------------
        // 2. URGENCY
        // -----------------------------------------------------

        if (breakdown.getUrgencyScore() == 25) {

            reason.append(
                    "high urgency; ");

        } else if (breakdown.getUrgencyScore() == 15) {

            reason.append(
                    "moderate urgency; ");

        } else {

            reason.append(
                    "low urgency; ");
        }

        // -----------------------------------------------------
        // 3. AVAILABILITY
        // -----------------------------------------------------

        if (breakdown.getAvailabilityScore() > 0) {

            reason.append(
                    "resource is available; ");

        } else {

            reason.append(
                    "resource is not currently available; ");
        }

        // -----------------------------------------------------
        // 4. LOCATION
        // -----------------------------------------------------

        if (breakdown.getLocationScore() > 0) {

            reason.append(
                    "resource is at the same location; ");

        } else {

            reason.append(
                    "resource is at a different location; ");
        }

        // -----------------------------------------------------
        // 5. RESOURCE PRIORITY
        // -----------------------------------------------------

        if (breakdown.getPriorityScore() == 10) {

            reason.append(
                    "resource has high operational priority; ");

        } else if (breakdown.getPriorityScore() == 7) {

            reason.append(
                    "resource has moderate operational priority; ");

        } else {

            reason.append(
                    "resource has lower operational priority; ");
        }

        // -----------------------------------------------------
        // 6. QUANTITY
        // -----------------------------------------------------

        if (resource.getQuantity() > 0) {

            reason.append(
                    "required quantity is available.");

        } else {

            reason.append(
                    "required quantity is not available.");
        }

        return reason.toString();
    }

    // =========================================================
    // FIND BEST RESOURCE AUTOMATICALLY
    // =========================================================

    public ResourceAllocation findBestResource(
            EmergencyNeed need,
            List<Resource> resources) {

        Resource bestResource =
                resources.stream()

                        // Only consider resources that
                        // have a valid positive score.
                        .filter(resource ->
                                calculateMatchScore(
                                        need,
                                        resource) > 0)

                        // Select the resource with
                        // the highest match score.
                        .max(
                                Comparator.comparingInt(
                                        resource ->
                                                calculateMatchScore(
                                                        need,
                                                        resource)))

                        // No suitable resource.
                        .orElse(null);

        // -----------------------------------------------------
        // NO RESOURCE FOUND
        // -----------------------------------------------------

        if (bestResource == null) {

            return null;
        }

        // -----------------------------------------------------
        // CALCULATE FINAL SCORE BREAKDOWN
        // -----------------------------------------------------

        MatchScoreBreakdown breakdown =
                calculateScoreBreakdown(
                        need,
                        bestResource);

        // -----------------------------------------------------
        // GENERATE EXPLANATION
        // -----------------------------------------------------

        String recommendationReason =
                generateRecommendationReason(
                        need,
                        bestResource);

        // -----------------------------------------------------
        // CREATE RESOURCE ALLOCATION
        // -----------------------------------------------------

        ResourceAllocation allocation =
                new ResourceAllocation();

        allocation.setEmergencyNeed(
                need);

        allocation.setResource(
                bestResource);

        allocation.setMatchScore(
                breakdown.getTotalScore());

        allocation.setRecommendationReason(
                recommendationReason);

        // -----------------------------------------------------
        // HUMAN VERIFICATION
        // -----------------------------------------------------

        allocation.setStatus(
                "PENDING_HUMAN_VERIFICATION");

        return allocation;
    }
}


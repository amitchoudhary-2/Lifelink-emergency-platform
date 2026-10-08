package com.lifelink.lifelink.service;

import org.springframework.stereotype.Service;

import com.lifelink.lifelink.entity.AffectedPerson;
import com.lifelink.lifelink.entity.EmergencyNeed;
import com.lifelink.lifelink.entity.Incident;

@Service
public class EmergencyPriorityService {

    public int calculatePriorityScore(
            Incident incident,
            EmergencyNeed emergencyNeed,
            AffectedPerson affectedPerson) {

        int score = 0;

        // 1. Incident severity: maximum 30 points
        if (incident != null) {
            if (incident.getSeverity() >= 5) {
                score += 30;
            } else if (incident.getSeverity() >= 3) {
                score += 20;
            } else {
                score += 10;
            }
        }

        // 2. Emergency urgency: maximum 30 points
        if (emergencyNeed != null) {
            if (emergencyNeed.getUrgency() >= 5) {
                score += 30;
            } else if (emergencyNeed.getUrgency() >= 3) {
                score += 20;
            } else {
                score += 10;
            }
        }

        // 3. Medical attention requirement: maximum 25 points
        if (affectedPerson != null
                && affectedPerson.isRequiresMedicalAttention()) {

            score += 25;
        }

        // 4. Serious medical condition: maximum 15 points
        if (affectedPerson != null
                && affectedPerson.getCondition() != null) {

            String condition =
                    affectedPerson.getCondition().toLowerCase();

            if (condition.contains("critical")
                    || condition.contains("unconscious")
                    || condition.contains("bleeding")
                    || condition.contains("fracture")
                    || condition.contains("injury")) {

                score += 15;
            }
        }

        // Maximum score = 100
        return Math.min(score, 100);
    }

    public String determinePriorityLevel(int score) {

        if (score >= 80) {
            return "CRITICAL";
        }

        if (score >= 60) {
            return "HIGH";
        }

        if (score >= 40) {
            return "MEDIUM";
        }

        return "LOW";
    }

    public String generatePriorityExplanation(
            Incident incident,
            EmergencyNeed emergencyNeed,
            AffectedPerson affectedPerson) {

        int score =
                calculatePriorityScore(
                        incident,
                        emergencyNeed,
                        affectedPerson);

        StringBuilder explanation =
                new StringBuilder();

        if (incident != null
                && incident.getSeverity() >= 5) {

            explanation.append(
                    "Incident has high severity; ");
        }

        if (emergencyNeed != null
                && emergencyNeed.getUrgency() >= 5) {

            explanation.append(
                    "emergency need has high urgency; ");
        }

        if (affectedPerson != null
                && affectedPerson.isRequiresMedicalAttention()) {

            explanation.append(
                    "medical attention is required; ");
        }

        if (affectedPerson != null
                && affectedPerson.getCondition() != null) {

            String condition =
                    affectedPerson.getCondition().toLowerCase();

            if (condition.contains("critical")
                    || condition.contains("unconscious")
                    || condition.contains("bleeding")
                    || condition.contains("fracture")
                    || condition.contains("injury")) {

                explanation.append(
                        "reported condition indicates increased medical concern; ");
            }
        }

        if (explanation.length() == 0) {
            explanation.append(
                    "Priority is based on the available emergency information; ");
        }

        explanation.append(
                "calculated priority score is ")
                .append(score)
                .append("/100.");

        return explanation.toString();
    }
}
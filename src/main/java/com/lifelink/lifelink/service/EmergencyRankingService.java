package com.lifelink.lifelink.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.lifelink.lifelink.entity.AffectedPerson;
import com.lifelink.lifelink.entity.EmergencyNeed;
import com.lifelink.lifelink.entity.Incident;

@Service
public class EmergencyRankingService {

    private final EmergencyPriorityService emergencyPriorityService;

    public EmergencyRankingService(
            EmergencyPriorityService emergencyPriorityService) {

        this.emergencyPriorityService =
                emergencyPriorityService;
    }

    public List<EmergencyPriorityResult> rankEmergencies(
            List<EmergencyNeed> emergencyNeeds,
            List<Incident> incidents) {

        List<EmergencyPriorityResult> results =
                new ArrayList<>();

        for (EmergencyNeed emergencyNeed : emergencyNeeds) {

            Incident relatedIncident =
                    findRelatedIncident(
                            emergencyNeed,
                            incidents);

            AffectedPerson affectedPerson =
                    emergencyNeed.getAffectedPerson();

            int priorityScore;

            String explanation;

            /*
             * If a matching incident exists,
             * use incident severity + need + person information.
             */
            if (relatedIncident != null) {

                priorityScore =
                        emergencyPriorityService
                                .calculatePriorityScore(
                                        relatedIncident,
                                        emergencyNeed,
                                        affectedPerson);

                explanation =
                        emergencyPriorityService
                                .generatePriorityExplanation(
                                        relatedIncident,
                                        emergencyNeed,
                                        affectedPerson);

            } else {

                /*
                 * If no matching incident exists,
                 * still calculate priority using the
                 * emergency need and affected person.
                 */
                priorityScore = 0;

                if (emergencyNeed != null) {

                    if (emergencyNeed.getUrgency() >= 5) {
                        priorityScore += 30;
                    } else if (emergencyNeed.getUrgency() >= 3) {
                        priorityScore += 20;
                    } else {
                        priorityScore += 10;
                    }
                }

                if (affectedPerson != null
                        && affectedPerson.isRequiresMedicalAttention()) {

                    priorityScore += 25;
                }

                if (affectedPerson != null
                        && affectedPerson.getCondition() != null) {

                    String condition =
                            affectedPerson
                                    .getCondition()
                                    .toLowerCase();

                    if (condition.contains("critical")
                            || condition.contains("unconscious")
                            || condition.contains("bleeding")
                            || condition.contains("fracture")
                            || condition.contains("injury")) {

                        priorityScore += 15;
                    }
                }

                priorityScore =
                        Math.min(priorityScore, 100);

                explanation =
                        "No matching incident was found for this location; "
                        + "priority is calculated from emergency need urgency "
                        + "and affected person information. "
                        + "Calculated priority score is "
                        + priorityScore
                        + "/100.";
            }

            String priorityLevel =
                    emergencyPriorityService
                            .determinePriorityLevel(
                                    priorityScore);

            EmergencyPriorityResult result =
                    new EmergencyPriorityResult(
                            emergencyNeed.getId(),

                            relatedIncident != null
                                    ? relatedIncident.getId()
                                    : null,

                            affectedPerson != null
                                    ? affectedPerson.getId()
                                    : null,

                            priorityScore,
                            priorityLevel,
                            explanation
                    );

            results.add(result);
        }

        /*
         * Highest priority emergencies appear first.
         */
        results.sort(
                Comparator.comparingInt(
                        EmergencyPriorityResult::getPriorityScore)
                        .reversed());

        /*
         * Assign ranking numbers after sorting.
         */
        for (int i = 0; i < results.size(); i++) {

            results.get(i).setRank(i + 1);
        }

        return results;
    }

    private Incident findRelatedIncident(
            EmergencyNeed emergencyNeed,
            List<Incident> incidents) {

        if (emergencyNeed.getLocation() == null) {
            return null;
        }

        return incidents.stream()
                .filter(incident ->
                        incident.getLocation() != null
                                && incident.getLocation()
                                        .equalsIgnoreCase(
                                                emergencyNeed
                                                        .getLocation()))
                .max(
                        Comparator.comparingInt(
                                Incident::getSeverity))
                .orElse(null);
    }

    public static class EmergencyPriorityResult {

        private int rank;
        private Long emergencyNeedId;
        private Long incidentId;
        private Long affectedPersonId;
        private int priorityScore;
        private String priorityLevel;
        private String explanation;

        public EmergencyPriorityResult(
                Long emergencyNeedId,
                Long incidentId,
                Long affectedPersonId,
                int priorityScore,
                String priorityLevel,
                String explanation) {

            this.emergencyNeedId =
                    emergencyNeedId;

            this.incidentId =
                    incidentId;

            this.affectedPersonId =
                    affectedPersonId;

            this.priorityScore =
                    priorityScore;

            this.priorityLevel =
                    priorityLevel;

            this.explanation =
                    explanation;
        }

        public int getRank() {
            return rank;
        }

        public void setRank(int rank) {
            this.rank = rank;
        }

        public Long getEmergencyNeedId() {
            return emergencyNeedId;
        }

        public Long getIncidentId() {
            return incidentId;
        }

        public Long getAffectedPersonId() {
            return affectedPersonId;
        }

        public int getPriorityScore() {
            return priorityScore;
        }

        public String getPriorityLevel() {
            return priorityLevel;
        }

        public String getExplanation() {
            return explanation;
        }
    }
}
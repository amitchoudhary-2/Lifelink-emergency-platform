package com.lifelink.lifelink.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class ResourceAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String recommendationReason;
    private int matchScore;
    private String status;

    @ManyToOne
    @JoinColumn(name = "emergency_need_id")
    private EmergencyNeed emergencyNeed;

    @ManyToOne
    @JoinColumn(name = "resource_id")
    private Resource resource;

    public ResourceAllocation() {
    }

    public ResourceAllocation(
            String recommendationReason,
            int matchScore,
            String status,
            EmergencyNeed emergencyNeed,
            Resource resource) {

        this.recommendationReason = recommendationReason;
        this.matchScore = matchScore;
        this.status = status;
        this.emergencyNeed = emergencyNeed;
        this.resource = resource;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRecommendationReason() {
        return recommendationReason;
    }

    public void setRecommendationReason(String recommendationReason) {
        this.recommendationReason = recommendationReason;
    }

    public int getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(int matchScore) {
        this.matchScore = matchScore;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public EmergencyNeed getEmergencyNeed() {
        return emergencyNeed;
    }

    public void setEmergencyNeed(EmergencyNeed emergencyNeed) {
        this.emergencyNeed = emergencyNeed;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }
}

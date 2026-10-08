package com.lifelink.lifelink.service;

public class MatchScoreBreakdown {

    private int compatibilityScore;
    private int urgencyScore;
    private int availabilityScore;
    private int locationScore;
    private int priorityScore;
    private int totalScore;

    public MatchScoreBreakdown() {
    }

    public MatchScoreBreakdown(
            int compatibilityScore,
            int urgencyScore,
            int availabilityScore,
            int locationScore,
            int priorityScore) {

        this.compatibilityScore = compatibilityScore;
        this.urgencyScore = urgencyScore;
        this.availabilityScore = availabilityScore;
        this.locationScore = locationScore;
        this.priorityScore = priorityScore;

        this.totalScore =
                compatibilityScore
                + urgencyScore
                + availabilityScore
                + locationScore
                + priorityScore;
    }

    public int getCompatibilityScore() {
        return compatibilityScore;
    }

    public void setCompatibilityScore(
            int compatibilityScore) {

        this.compatibilityScore = compatibilityScore;
        recalculateTotal();
    }

    public int getUrgencyScore() {
        return urgencyScore;
    }

    public void setUrgencyScore(
            int urgencyScore) {

        this.urgencyScore = urgencyScore;
        recalculateTotal();
    }

    public int getAvailabilityScore() {
        return availabilityScore;
    }

    public void setAvailabilityScore(
            int availabilityScore) {

        this.availabilityScore = availabilityScore;
        recalculateTotal();
    }

    public int getLocationScore() {
        return locationScore;
    }

    public void setLocationScore(
            int locationScore) {

        this.locationScore = locationScore;
        recalculateTotal();
    }

    public int getPriorityScore() {
        return priorityScore;
    }

    public void setPriorityScore(
            int priorityScore) {

        this.priorityScore = priorityScore;
        recalculateTotal();
    }

    public int getTotalScore() {
        return totalScore;
    }

    private void recalculateTotal() {

        this.totalScore =
                compatibilityScore
                + urgencyScore
                + availabilityScore
                + locationScore
                + priorityScore;
    }
}
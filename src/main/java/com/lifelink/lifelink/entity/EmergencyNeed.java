package com.lifelink.lifelink.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class EmergencyNeed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String needType;
    private String description;
    private int urgency;
    private String status;
    private String location;

    @ManyToOne
    @JoinColumn(name = "affected_person_id")
    private AffectedPerson affectedPerson;

    public EmergencyNeed() {
    }

    public EmergencyNeed(
            String needType,
            String description,
            int urgency,
            String status,
            String location,
            AffectedPerson affectedPerson) {

        this.needType = needType;
        this.description = description;
        this.urgency = urgency;
        this.status = status;
        this.location = location;
        this.affectedPerson = affectedPerson;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNeedType() {
        return needType;
    }

    public void setNeedType(String needType) {
        this.needType = needType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getUrgency() {
        return urgency;
    }

    public void setUrgency(int urgency) {
        this.urgency = urgency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public AffectedPerson getAffectedPerson() {
        return affectedPerson;
    }

    public void setAffectedPerson(AffectedPerson affectedPerson) {
        this.affectedPerson = affectedPerson;
    }
}
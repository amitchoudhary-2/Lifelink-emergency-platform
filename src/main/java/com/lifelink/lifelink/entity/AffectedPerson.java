
package com.lifelink.lifelink.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class AffectedPerson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int age;
    private String gender;
    private String condition;
    private String location;
    private boolean requiresMedicalAttention;

    public AffectedPerson() {
    }

    public AffectedPerson(
            String name,
            int age,
            String gender,
            String condition,
            String location,
            boolean requiresMedicalAttention) {

        this.name = name;
        this.age = age;
        this.gender = gender;
        this.condition = condition;
        this.location = location;
        this.requiresMedicalAttention = requiresMedicalAttention;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isRequiresMedicalAttention() {
        return requiresMedicalAttention;
    }

    public void setRequiresMedicalAttention(boolean requiresMedicalAttention) {
        this.requiresMedicalAttention = requiresMedicalAttention;
    }
}


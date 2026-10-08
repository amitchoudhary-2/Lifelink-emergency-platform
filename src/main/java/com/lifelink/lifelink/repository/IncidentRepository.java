package com.lifelink.lifelink.repository;

import com.lifelink.lifelink.entity.Incident;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncidentRepository extends JpaRepository<Incident, Long> {

}
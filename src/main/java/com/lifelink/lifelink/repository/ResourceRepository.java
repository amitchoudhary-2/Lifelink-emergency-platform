package com.lifelink.lifelink.repository;

import com.lifelink.lifelink.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
}
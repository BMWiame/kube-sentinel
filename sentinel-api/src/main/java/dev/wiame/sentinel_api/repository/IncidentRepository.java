package dev.wiame.sentinel_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.wiame.sentinel_api.entity.IncidentEntity;

public interface IncidentRepository extends JpaRepository<IncidentEntity, Long>{
    
}

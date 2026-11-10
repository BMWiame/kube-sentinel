package dev.wiame.sentinel_api.repository;


import dev.wiame.sentinel_api.entity.SecurityEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<SecurityEventEntity, Long> {}

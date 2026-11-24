package dev.wiame.sentinel_api.repository;


import dev.wiame.sentinel_api.entity.SecurityEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventRepository extends JpaRepository<SecurityEventEntity, Long> {
    List<SecurityEventEntity> findTop50ByOrderByIdDesc();
}

package com.caprae.saasquatch.repository;

import com.caprae.saasquatch.entity.ScrapingLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScrapingLogRepository extends JpaRepository<ScrapingLog, Long> {
    List<ScrapingLog> findTop20ByOrderByCreatedAtDesc();
}

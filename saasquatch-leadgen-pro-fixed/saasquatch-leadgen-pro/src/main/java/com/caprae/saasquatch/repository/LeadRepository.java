package com.caprae.saasquatch.repository;

import com.caprae.saasquatch.entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Long>, JpaSpecificationExecutor<Lead> {

    Optional<Lead> findByDomain(String domain);

    boolean existsByDomain(String domain);

    List<Lead> findByStatus(String status);

    List<Lead> findByIndustry(String industry);

    @Query("SELECT l FROM Lead l WHERE " +
           "(:query IS NULL OR :query = '' OR " +
           "LOWER(l.companyName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(l.domain) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(l.industry) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(l.region) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(l.priority) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(l.techStack) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:industry IS NULL OR :industry = '' OR LOWER(l.industry) = LOWER(:industry)) AND " +
           "(:status IS NULL OR :status = '' OR l.status = :status) AND " +
           "(:minScore IS NULL OR l.dealScore >= :minScore) " +
           "ORDER BY l.dealScore DESC, l.createdAt DESC")
    List<Lead> searchLeads(
            @Param("query") String query,
            @Param("industry") String industry,
            @Param("status") String status,
            @Param("minScore") Integer minScore);

    @Query("SELECT COUNT(l) FROM Lead l")
    long countTotalLeads();

    @Query("SELECT AVG(l.dealScore) FROM Lead l")
    Double getAverageDealScore();

    @Query("SELECT SUM(l.estimatedRevenue) FROM Lead l")
    Double getTotalPipelineRevenue();

    @Query("SELECT l.industry, COUNT(l) FROM Lead l GROUP BY l.industry")
    List<Object[]> countLeadsByIndustry();

    @Query("SELECT l.status, COUNT(l) FROM Lead l GROUP BY l.status")
    List<Object[]> countLeadsByStatus();
}

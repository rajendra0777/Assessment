package com.caprae.saasquatch.repository;

import com.caprae.saasquatch.entity.LeadContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeadContactRepository extends JpaRepository<LeadContact, Long> {
    List<LeadContact> findByLeadId(Long leadId);
}

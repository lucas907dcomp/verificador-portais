package com.hackathon.back.repository;

import com.hackathon.back.model.Portal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortalRepository extends JpaRepository<Portal, Long> {
    Portal findByUrl(String url);
    boolean existsByUrl(String url);
} 
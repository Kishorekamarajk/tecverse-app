package com.tecverse.backend.repository;

import com.tecverse.backend.entity.ThematicSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThematicSessionRepository extends JpaRepository<ThematicSession, Long> {

    List<ThematicSession> findAllByOrderByDisplayOrderAsc();

    @Query("SELECT s FROM ThematicSession s WHERE LOWER(s.domain) IN :domains ORDER BY s.displayOrder ASC")
    List<ThematicSession> findByDomainsInIgnoreCase(@Param("domains") List<String> domains);

    @Query("SELECT DISTINCT s.domain FROM ThematicSession s ORDER BY s.domain ASC")
    List<String> findDistinctDomains();
}

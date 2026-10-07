package com.tecverse.backend.repository;

import com.tecverse.backend.entity.ImportantDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImportantDateRepository extends JpaRepository<ImportantDate, Long> {
    List<ImportantDate> findAllByOrderByDisplayOrderAsc();
    List<ImportantDate> findByIsFeaturedTrueOrderByDisplayOrderAsc();
}

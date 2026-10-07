package com.tecverse.backend.repository;

import com.tecverse.backend.entity.FaqItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FaqItemRepository extends JpaRepository<FaqItem, Long> {

    List<FaqItem> findAllByOrderByDisplayOrderAsc();

    List<FaqItem> findByPageKeyIgnoreCaseOrderByDisplayOrderAsc(String pageKey);

    @Query("SELECT f FROM FaqItem f WHERE " +
           "LOWER(f.question) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(f.answer) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(f.pageKey) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<FaqItem> searchFaq(@Param("q") String query);
}

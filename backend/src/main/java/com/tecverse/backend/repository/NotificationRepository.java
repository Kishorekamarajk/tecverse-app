package com.tecverse.backend.repository;

import com.tecverse.backend.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByOrderByCreatedAtDesc();

    @Query("SELECT n FROM Notification n WHERE n.isBroadcast = true OR n.userReferenceNumber = :userRef ORDER BY n.createdAt DESC")
    List<Notification> findForUserOrBroadcast(@Param("userRef") String userRef);

    long countByIsReadFalse();
}

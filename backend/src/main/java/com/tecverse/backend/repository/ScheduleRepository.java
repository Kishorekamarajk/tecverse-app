package com.tecverse.backend.repository;

import com.tecverse.backend.entity.ScheduleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<ScheduleItem, Long> {

    List<ScheduleItem> findAllByOrderByDayNumberAscDisplayOrderAsc();

    List<ScheduleItem> findByDayNumberOrderByDisplayOrderAsc(int dayNumber);
}

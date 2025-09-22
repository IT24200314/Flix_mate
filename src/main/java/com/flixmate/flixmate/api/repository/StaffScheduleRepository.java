package com.flixmate.flixmate.api.repository;

import com.flixmate.flixmate.api.entity.StaffSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StaffScheduleRepository extends JpaRepository<StaffSchedule, Integer> {
    List<StaffSchedule> findByHallId(Integer hallId);
}

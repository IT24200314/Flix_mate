package com.flixmate.flixmate.api.repository;

import com.flixmate.flixmate.api.entity.Report;
import com.flixmate.flixmate.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Integer> {
    List<Report> findByUser(User user);
}

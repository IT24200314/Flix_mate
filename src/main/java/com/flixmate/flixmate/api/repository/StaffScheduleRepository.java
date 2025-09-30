package com.flixmate.flixmate.api.repository;

import com.flixmate.flixmate.api.entity.StaffSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for StaffSchedule entity
 */
@Repository
public interface StaffScheduleRepository extends JpaRepository<StaffSchedule, Long> {
    
    /**
     * Find schedules by staff name
     */
    List<StaffSchedule> findByStaffName(String staffName);
    
    /**
     * Find schedules by hall ID
     */
        List<StaffSchedule> findByHallId(Integer hallId);
    
    /**
     * Find schedules by date range
     */
    @Query("SELECT s FROM StaffSchedule s WHERE " +
           "CAST(s.startTime AS date) BETWEEN :startDate AND :endDate")
    List<StaffSchedule> findSchedulesByDateRange(@Param("startDate") LocalDate startDate, 
                                                @Param("endDate") LocalDate endDate);
    
    /**
     * Find active schedules for a specific date
     */
    @Query("SELECT s FROM StaffSchedule s WHERE " +
           "CAST(s.startTime AS date) = :date")
    List<StaffSchedule> findSchedulesByDate(@Param("date") LocalDate date);
    
    /**
     * Find schedules for a specific staff member on a date
     */
    @Query("SELECT s FROM StaffSchedule s WHERE " +
           "s.staffName = :staffName AND CAST(s.startTime AS date) = :date")
    List<StaffSchedule> findSchedulesByStaffAndDate(@Param("staffName") String staffName, 
                                                   @Param("date") LocalDate date);
    
    /**
     * Find schedules for a cinema hall on a specific date
     */
    @Query("SELECT s FROM StaffSchedule s WHERE " +
           "s.hallId = :hallId AND CAST(s.startTime AS date) = :date")
    List<StaffSchedule> findSchedulesByHallAndDate(@Param("hallId") Integer hallId, 
                                                  @Param("date") LocalDate date);
    
    /**
     * Find overlapping schedules for a staff member
     */
    @Query("SELECT s FROM StaffSchedule s WHERE " +
           "s.staffName = :staffName AND " +
           "((s.startTime <= :startTime AND s.endTime >= :startTime) OR " +
           "(s.startTime <= :endTime AND s.endTime >= :endTime) OR " +
           "(s.startTime >= :startTime AND s.endTime <= :endTime))")
    List<StaffSchedule> findOverlappingSchedules(@Param("staffName") String staffName, 
                                                @Param("startTime") LocalDateTime startTime, 
                                                @Param("endTime") LocalDateTime endTime);
    
    /**
     * Find schedules by time range
     */
    @Query("SELECT s FROM StaffSchedule s WHERE " +
           "s.startTime <= :endTime AND s.endTime >= :startTime")
    List<StaffSchedule> findSchedulesByTimeRange(@Param("startTime") LocalDateTime startTime, 
                                                 @Param("endTime") LocalDateTime endTime);
    
    /**
     * Count schedules for a staff member
     */
    @Query("SELECT COUNT(s) FROM StaffSchedule s WHERE s.staffName = :staffName")
    Long countSchedulesByStaff(@Param("staffName") String staffName);
    
    /**
     * Find upcoming schedules
     */
    @Query("SELECT s FROM StaffSchedule s WHERE " +
           "s.startTime >= :date " +
           "ORDER BY s.startTime")
    List<StaffSchedule> findUpcomingSchedules(@Param("date") LocalDateTime date);
    
    /**
     * Find schedules for today
     */
    @Query("SELECT s FROM StaffSchedule s WHERE " +
           "CAST(s.startTime AS date) = CAST(:today AS date)")
    List<StaffSchedule> findTodaySchedules(@Param("today") LocalDate today);
    
    /**
     * Find schedules by hall ID and date range
     */
    @Query("SELECT s FROM StaffSchedule s WHERE " +
           "s.hallId = :hallId AND " +
           "CAST(s.startTime AS date) BETWEEN :startDate AND :endDate " +
           "ORDER BY s.startTime")
    List<StaffSchedule> findSchedulesByHallAndDateRange(@Param("hallId") Integer hallId,
                                                       @Param("startDate") LocalDate startDate,
                                                       @Param("endDate") LocalDate endDate);
}
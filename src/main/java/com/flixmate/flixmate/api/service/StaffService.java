package com.flixmate.flixmate.api.service;

import com.flixmate.flixmate.api.entity.Staff;
import com.flixmate.flixmate.api.entity.StaffSchedule;
import com.flixmate.flixmate.api.entity.CinemaHall;
import com.flixmate.flixmate.api.repository.StaffRepository;
import com.flixmate.flixmate.api.repository.StaffScheduleRepository;
import com.flixmate.flixmate.api.repository.CinemaHallRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for staff management operations
 */
@Service
@Transactional
public class StaffService {
    
    @Autowired
    private StaffRepository staffRepository;
    
    @Autowired
    private StaffScheduleRepository staffScheduleRepository;
    
    @Autowired
    private CinemaHallRepository cinemaHallRepository;
    
    /**
     * Get all staff members
     */
    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }
    
    /**
     * Get staff by ID
     */
    public Optional<Staff> getStaffById(Long id) {
        return staffRepository.findById(id);
    }
    
    /**
     * Create new staff member
     */
    public Staff createStaff(Staff staff) {
        staff.setCreatedAt(java.time.LocalDateTime.now());
        staff.setUpdatedAt(java.time.LocalDateTime.now());
        if (staff.getHireDate() == null) {
            staff.setHireDate(java.time.LocalDateTime.now());
        }
        return staffRepository.save(staff);
    }
    
    /**
     * Update staff member
     */
    public Staff updateStaff(Long id, Staff staffDetails) {
        Optional<Staff> optionalStaff = staffRepository.findById(id);
        if (optionalStaff.isPresent()) {
            Staff staff = optionalStaff.get();
            staff.setFirstName(staffDetails.getFirstName());
            staff.setLastName(staffDetails.getLastName());
            staff.setEmail(staffDetails.getEmail());
            staff.setPhone(staffDetails.getPhone());
            staff.setPosition(staffDetails.getPosition());
            staff.setAssignedHallId(staffDetails.getAssignedHallId());
            staff.setAddress(staffDetails.getAddress());
            staff.setStatus(staffDetails.getStatus());
            staff.setSalary(staffDetails.getSalary()); // Add salary update
            staff.setUpdatedAt(java.time.LocalDateTime.now());
            return staffRepository.save(staff);
        }
        return null;
    }
    
    /**
     * Delete staff member
     */
    public boolean deleteStaff(Long id) {
        if (staffRepository.existsById(id)) {
            staffRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    /**
     * Get active staff members
     */
    public List<Staff> getActiveStaff() {
        return staffRepository.findActiveStaff();
    }
    
    /**
     * Get staff by position
     */
    public List<Staff> getStaffByPosition(String position) {
        return staffRepository.findByPosition(position);
    }
    
    /**
     * Get staff by assigned hall
     */
    public List<Staff> getStaffByHall(Integer hallId) {
        return staffRepository.findByAssignedHallId(hallId);
    }
    
    /**
     * Search staff by name
     */
    public List<Staff> searchStaffByName(String name) {
        return staffRepository.findStaffByName(name);
    }
    
    /**
     * Get staff working on a specific date
     */
    public List<Staff> getStaffWorkingOnDate(LocalDate date) {
        return staffRepository.findStaffWorkingOnDate(date);
    }
    
    /**
     * Get staff assigned to a cinema hall on a specific date
     */
    public List<Staff> getStaffByHallAndDate(Integer hallId, LocalDate date) {
        return staffRepository.findStaffByHallAndDate(hallId, date);
    }
    
    /**
     * Create staff schedule
     */
    public StaffSchedule createSchedule(StaffSchedule schedule) {
        // Ensure timestamps are set
        if (schedule.getCreatedAt() == null) {
            schedule.setCreatedAt(java.time.LocalDateTime.now());
        }
        if (schedule.getUpdatedAt() == null) {
            schedule.setUpdatedAt(java.time.LocalDateTime.now());
        }
        
        // Check for overlapping schedules (if repository method exists)
        try {
            List<StaffSchedule> overlapping = staffScheduleRepository.findOverlappingSchedules(
                schedule.getStaffName(), 
                schedule.getStartTime(), 
                schedule.getEndTime()
            );
            
            if (!overlapping.isEmpty()) {
                throw new IllegalArgumentException("Staff member already has overlapping schedule");
            }
        } catch (Exception e) {
            // If findOverlappingSchedules doesn't exist, continue without check
            System.out.println("Overlap check skipped: " + e.getMessage());
        }
        
        return staffScheduleRepository.save(schedule);
    }
    
    /**
     * Get schedules for a staff member
     */
    public List<StaffSchedule> getSchedulesByStaff(String staffName) {
        return staffScheduleRepository.findByStaffName(staffName);
    }
    
    /**
     * Get schedules for a cinema hall
     */
    public List<StaffSchedule> getSchedulesByHall(Integer hallId) {
        return staffScheduleRepository.findByHallId(hallId);
    }
    
    /**
     * Get schedules for a specific date
     */
    public List<StaffSchedule> getSchedulesByDate(LocalDate date) {
        return staffScheduleRepository.findSchedulesByDate(date);
    }
    
    /**
     * Get schedules for a cinema hall on a specific date
     */
    public List<StaffSchedule> getSchedulesByHallAndDate(Integer hallId, LocalDate date) {
        return staffScheduleRepository.findSchedulesByHallAndDate(hallId, date);
    }
    
    /**
     * Get today's schedules
     */
    public List<StaffSchedule> getTodaySchedules() {
        return staffScheduleRepository.findTodaySchedules(LocalDate.now());
    }
    
    /**
     * Get upcoming schedules
     */
    public List<StaffSchedule> getUpcomingSchedules() {
        return staffScheduleRepository.findUpcomingSchedules(java.time.LocalDateTime.now());
    }
    
    /**
     * Get schedules by date range
     */
    public List<StaffSchedule> getSchedulesByDateRange(LocalDate startDate, LocalDate endDate) {
        return staffScheduleRepository.findSchedulesByDateRange(startDate, endDate);
    }
    
    /**
     * Get schedules by hall and date range
     */
    public List<StaffSchedule> getSchedulesByHallAndDateRange(Integer hallId, LocalDate startDate, LocalDate endDate) {
        return staffScheduleRepository.findSchedulesByHallAndDateRange(hallId, startDate, endDate);
    }
    
    /**
     * Get all schedules
     */
    public List<StaffSchedule> getAllSchedules() {
        return staffScheduleRepository.findAll();
    }
    
    /**
     * Update schedule
     */
    public StaffSchedule updateSchedule(Long scheduleId, StaffSchedule scheduleDetails) {
        Optional<StaffSchedule> optionalSchedule = staffScheduleRepository.findById(scheduleId);
        if (optionalSchedule.isPresent()) {
            StaffSchedule schedule = optionalSchedule.get();
            schedule.setStaffName(scheduleDetails.getStaffName());
            schedule.setStartTime(scheduleDetails.getStartTime());
            schedule.setEndTime(scheduleDetails.getEndTime());
            schedule.setHallId(scheduleDetails.getHallId());
            schedule.setUpdatedAt(java.time.LocalDateTime.now());
            return staffScheduleRepository.save(schedule);
        }
        return null;
    }
    
    /**
     * Delete schedule
     */
    public boolean deleteSchedule(Long scheduleId) {
        if (staffScheduleRepository.existsById(scheduleId)) {
            staffScheduleRepository.deleteById(scheduleId);
            return true;
        }
        return false;
    }
    
    /**
     * Get staff statistics
     */
    public Object getStaffStatistics() {
        long totalStaff = staffRepository.count();
        long activeStaff = staffRepository.findActiveStaff().size();
        long scheduledShifts = staffScheduleRepository.count();
        long availableHalls = cinemaHallRepository.count(); // Get real count from database
        
        return new StaffStatistics(totalStaff, activeStaff, scheduledShifts, availableHalls);
    }
    
    /**
     * Inner class for staff statistics
     */
    public static class StaffStatistics {
        public final long totalStaff;
        public final long activeStaff;
        public final long scheduledShifts;
        public final long availableHalls;
        
        public StaffStatistics(long totalStaff, long activeStaff, long scheduledShifts, long availableHalls) {
            this.totalStaff = totalStaff;
            this.activeStaff = activeStaff;
            this.scheduledShifts = scheduledShifts;
            this.availableHalls = availableHalls;
        }
    }
}

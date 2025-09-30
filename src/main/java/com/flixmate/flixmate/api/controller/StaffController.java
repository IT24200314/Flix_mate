package com.flixmate.flixmate.api.controller;

import com.flixmate.flixmate.api.entity.Staff;
import com.flixmate.flixmate.api.entity.StaffSchedule;
import com.flixmate.flixmate.api.service.StaffService;
import com.flixmate.flixmate.api.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for staff management
 */
@RestController
@RequestMapping("/api/staff")
@CrossOrigin(origins = "*")
public class StaffController {
    
    @Autowired
    private StaffService staffService;
    
    @Autowired
    private NotificationService notificationService;
    
    /**
     * Get all staff members
     */
    @GetMapping
    public ResponseEntity<List<Staff>> getAllStaff() {
        List<Staff> staff = staffService.getAllStaff();
        return ResponseEntity.ok(staff);
    }
    
    /**
     * Get staff by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Staff> getStaffById(@PathVariable Long id) {
        Optional<Staff> staff = staffService.getStaffById(id);
        return staff.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Create new staff member
     */
    @PostMapping
    public ResponseEntity<Staff> createStaff(@RequestBody Staff staff) {
        try {
            Staff createdStaff = staffService.createStaff(staff);
            
            // Create notification for new staff member
            notificationService.notifyStaffAdded(createdStaff.getFullName());
            
            return ResponseEntity.ok(createdStaff);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Update staff member
     */
    @PutMapping("/{id}")
    public ResponseEntity<Staff> updateStaff(@PathVariable Long id, @RequestBody Staff staffDetails) {
        Staff updatedStaff = staffService.updateStaff(id, staffDetails);
        if (updatedStaff != null) {
            return ResponseEntity.ok(updatedStaff);
        }
        return ResponseEntity.notFound().build();
    }
    
    /**
     * Delete staff member
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStaff(@PathVariable Long id) {
        boolean deleted = staffService.deleteStaff(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    /**
     * Get active staff members
     */
    @GetMapping("/active")
    public ResponseEntity<List<Staff>> getActiveStaff() {
        List<Staff> activeStaff = staffService.getActiveStaff();
        return ResponseEntity.ok(activeStaff);
    }
    
    /**
     * Get staff by position
     */
    @GetMapping("/position/{position}")
    public ResponseEntity<List<Staff>> getStaffByPosition(@PathVariable String position) {
        List<Staff> staff = staffService.getStaffByPosition(position);
        return ResponseEntity.ok(staff);
    }
    
    /**
     * Get staff by assigned hall
     */
    @GetMapping("/hall/{hallId}")
    public ResponseEntity<List<Staff>> getStaffByHall(@PathVariable Integer hallId) {
        List<Staff> staff = staffService.getStaffByHall(hallId);
        return ResponseEntity.ok(staff);
    }
    
    /**
     * Search staff by name
     */
    @GetMapping("/search")
    public ResponseEntity<List<Staff>> searchStaffByName(@RequestParam String name) {
        List<Staff> staff = staffService.searchStaffByName(name);
        return ResponseEntity.ok(staff);
    }
    
    /**
     * Get staff working on a specific date
     */
    @GetMapping("/working/{date}")
    public ResponseEntity<List<Staff>> getStaffWorkingOnDate(@PathVariable String date) {
        LocalDate workingDate = LocalDate.parse(date);
        List<Staff> staff = staffService.getStaffWorkingOnDate(workingDate);
        return ResponseEntity.ok(staff);
    }
    
    /**
     * Get staff assigned to a cinema hall on a specific date
     */
    @GetMapping("/hall/{hallId}/date/{date}")
    public ResponseEntity<List<Staff>> getStaffByHallAndDate(@PathVariable Integer hallId, @PathVariable String date) {
        LocalDate workingDate = LocalDate.parse(date);
        List<Staff> staff = staffService.getStaffByHallAndDate(hallId, workingDate);
        return ResponseEntity.ok(staff);
    }
    
    /**
     * Get staff statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Object> getStaffStatistics() {
        Object statistics = staffService.getStaffStatistics();
        return ResponseEntity.ok(statistics);
    }
    
    /**
     * Create staff schedule
     */
    @PostMapping("/schedules")
    public ResponseEntity<StaffSchedule> createSchedule(@RequestBody StaffSchedule schedule) {
        try {
            StaffSchedule createdSchedule = staffService.createSchedule(schedule);
            return ResponseEntity.ok(createdSchedule);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get schedules for a staff member
     */
    @GetMapping("/schedules/staff/{staffName}")
    public ResponseEntity<List<StaffSchedule>> getSchedulesByStaff(@PathVariable String staffName) {
        List<StaffSchedule> schedules = staffService.getSchedulesByStaff(staffName);
        return ResponseEntity.ok(schedules);
    }
    
    /**
     * Get schedules for a cinema hall
     */
    @GetMapping("/schedules/hall/{hallId}")
    public ResponseEntity<List<StaffSchedule>> getSchedulesByHall(@PathVariable Integer hallId) {
        List<StaffSchedule> schedules = staffService.getSchedulesByHall(hallId);
        return ResponseEntity.ok(schedules);
    }
    
    /**
     * Get schedules for a specific date
     */
    @GetMapping("/schedules/date/{date}")
    public ResponseEntity<List<StaffSchedule>> getSchedulesByDate(@PathVariable String date) {
        LocalDate scheduleDate = LocalDate.parse(date);
        List<StaffSchedule> schedules = staffService.getSchedulesByDate(scheduleDate);
        return ResponseEntity.ok(schedules);
    }
    
    /**
     * Get schedules for a cinema hall on a specific date
     */
    @GetMapping("/schedules/hall/{hallId}/date/{date}")
    public ResponseEntity<List<StaffSchedule>> getSchedulesByHallAndDate(@PathVariable Integer hallId, @PathVariable String date) {
        LocalDate scheduleDate = LocalDate.parse(date);
        List<StaffSchedule> schedules = staffService.getSchedulesByHallAndDate(hallId, scheduleDate);
        return ResponseEntity.ok(schedules);
    }
    
    /**
     * Get schedules by date range
     */
    @GetMapping("/schedules/range")
    public ResponseEntity<List<StaffSchedule>> getSchedulesByDateRange(@RequestParam String startDate, @RequestParam String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        List<StaffSchedule> schedules = staffService.getSchedulesByDateRange(start, end);
        return ResponseEntity.ok(schedules);
    }
    
    /**
     * Get schedules by hall and date range
     */
    @GetMapping("/schedules/hall/{hallId}/range")
    public ResponseEntity<List<StaffSchedule>> getSchedulesByHallAndDateRange(@PathVariable Integer hallId, @RequestParam String startDate, @RequestParam String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        List<StaffSchedule> schedules = staffService.getSchedulesByHallAndDateRange(hallId, start, end);
        return ResponseEntity.ok(schedules);
    }
    
    /**
     * Get all schedules
     */
    @GetMapping("/schedules")
    public ResponseEntity<List<StaffSchedule>> getAllSchedules() {
        List<StaffSchedule> schedules = staffService.getAllSchedules();
        return ResponseEntity.ok(schedules);
    }
    
    /**
     * Get today's schedules
     */
    @GetMapping("/schedules/today")
    public ResponseEntity<List<StaffSchedule>> getTodaySchedules() {
        List<StaffSchedule> schedules = staffService.getTodaySchedules();
        return ResponseEntity.ok(schedules);
    }
    
    /**
     * Get upcoming schedules
     */
    @GetMapping("/schedules/upcoming")
    public ResponseEntity<List<StaffSchedule>> getUpcomingSchedules() {
        List<StaffSchedule> schedules = staffService.getUpcomingSchedules();
        return ResponseEntity.ok(schedules);
    }
    
    /**
     * Delete schedule
     */
    @DeleteMapping("/schedules/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long scheduleId) {
        boolean deleted = staffService.deleteSchedule(scheduleId);
        if (deleted) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    /**
     * Update schedule
     */
    @PutMapping("/schedules/{scheduleId}")
    public ResponseEntity<StaffSchedule> updateSchedule(@PathVariable Long scheduleId, @RequestBody StaffSchedule scheduleDetails) {
        StaffSchedule updatedSchedule = staffService.updateSchedule(scheduleId, scheduleDetails);
        if (updatedSchedule != null) {
            return ResponseEntity.ok(updatedSchedule);
        }
        return ResponseEntity.notFound().build();
    }
}

package com.flixmate.flixmate.api.repository;

import com.flixmate.flixmate.api.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Staff entity
 */
@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    
    /**
     * Find staff by email
     */
    Optional<Staff> findByEmail(String email);
    
    /**
     * Find staff by position
     */
    List<Staff> findByPosition(String position);
    
    /**
     * Find staff by assigned hall
     */
    List<Staff> findByAssignedHallId(Integer assignedHallId);
    
    /**
     * Find staff by status
     */
    List<Staff> findByStatus(String status);
    
    /**
     * Find active staff members
     */
    @Query("SELECT s FROM Staff s WHERE s.status = 'Active'")
    List<Staff> findActiveStaff();
    
    /**
     * Find staff by cinema hall and date
     * Note: Since we use flat table structure, this method is simplified
     */
    @Query("SELECT s FROM Staff s WHERE s.assignedHallId = :hallId")
    List<Staff> findStaffByHallAndDate(@Param("hallId") Integer hallId, @Param("date") LocalDate date);
    
    /**
     * Find staff working on a specific date
     * Note: Since we use flat table structure, this returns all active staff
     */
    @Query("SELECT s FROM Staff s WHERE s.status = 'Active'")
    List<Staff> findStaffWorkingOnDate(@Param("date") LocalDate date);
    
    /**
     * Count staff by position
     */
    @Query("SELECT s.position, COUNT(s) FROM Staff s GROUP BY s.position")
    List<Object[]> countStaffByPosition();
    
    /**
     * Find staff by name (first name or last name)
     */
    @Query("SELECT s FROM Staff s WHERE " +
           "LOWER(s.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
           "LOWER(s.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Staff> findStaffByName(@Param("name") String name);
}

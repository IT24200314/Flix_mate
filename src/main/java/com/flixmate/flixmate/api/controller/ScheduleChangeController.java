package com.flixmate.flixmate.api.controller;

import com.flixmate.flixmate.api.service.ScheduleChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schedule")
public class ScheduleChangeController {

    @Autowired
    private ScheduleChangeService scheduleChangeService;

    @PutMapping("/{showtimeId}")
    public ResponseEntity<String> updateShowTime(@PathVariable Integer showtimeId,
                                               @RequestParam String newStartTime) {
        scheduleChangeService.updateShowTime(showtimeId, newStartTime);
        return ResponseEntity.ok("Schedule updated and notification sent");
    }
}

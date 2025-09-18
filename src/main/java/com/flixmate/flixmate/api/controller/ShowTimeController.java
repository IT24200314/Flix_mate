package com.flixmate.flixmate.api.controller;

import com.flixmate.flixmate.api.entity.ShowTime;
import com.flixmate.flixmate.api.service.ShowTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/showtimes")
public class ShowTimeController {

    @Autowired
    private ShowTimeService showTimeService;

    @GetMapping
    public ResponseEntity<List<ShowTime>> getAllShowTimes() {
        return ResponseEntity.ok(showTimeService.getAllShowTimes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShowTime> getShowTimeById(@PathVariable Integer id) {
        ShowTime showTime = showTimeService.getShowTimeById(id);
        if (showTime != null) {
            return ResponseEntity.ok(showTime);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<ShowTime>> getShowTimesByMovie(@PathVariable Integer movieId) {
        return ResponseEntity.ok(showTimeService.getShowTimesByMovie(movieId));
    }
}

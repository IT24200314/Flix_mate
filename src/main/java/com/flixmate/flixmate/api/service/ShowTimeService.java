package com.flixmate.flixmate.api.service;

import com.flixmate.flixmate.api.entity.ShowTime;
import com.flixmate.flixmate.api.repository.ShowTimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShowTimeService {

    @Autowired
    private ShowTimeRepository showTimeRepository;

    public List<ShowTime> getAllShowTimes() {
        return showTimeRepository.findAll();
    }

    public ShowTime getShowTimeById(Integer id) {
        return showTimeRepository.findById(id).orElse(null);
    }

    public List<ShowTime> getShowTimesByMovie(Integer movieId) {
        return showTimeRepository.findByMovie_MovieId(movieId);
    }
}

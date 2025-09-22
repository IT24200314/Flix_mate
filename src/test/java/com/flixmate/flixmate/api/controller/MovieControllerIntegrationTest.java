package com.flixmate.flixmate.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flixmate.flixmate.api.entity.Movie;
import com.flixmate.flixmate.api.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
public class MovieControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MovieRepository movieRepository;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        objectMapper = new ObjectMapper();
        
        // Clean up test data
        movieRepository.deleteAll();
    }

    @Test
    void testGetAllMovies() throws Exception {
        // Create test movie
        Movie movie = new Movie();
        movie.setTitle("Test Movie");
        movie.setDescription("Test Description");
        movie.setReleaseYear(2025);
        movie.setGenre("Action");
        movie.setDuration(120);
        movie.setLanguage("English");
        movie.setDirector("Test Director");
        movie.setIsActive(true);
        movieRepository.save(movie);

        mockMvc.perform(get("/api/movies")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("Test Movie"))
                .andExpect(jsonPath("$[0].description").value("Test Description"))
                .andExpect(jsonPath("$[0].releaseYear").value(2025))
                .andExpect(jsonPath("$[0].genre").value("Action"));
    }

    @Test
    void testGetMovieById() throws Exception {
        // Create test movie
        Movie movie = new Movie();
        movie.setTitle("Test Movie");
        movie.setDescription("Test Description");
        movie.setReleaseYear(2025);
        movie.setGenre("Action");
        movie.setDuration(120);
        movie.setLanguage("English");
        movie.setDirector("Test Director");
        movie.setIsActive(true);
        Movie savedMovie = movieRepository.save(movie);

        mockMvc.perform(get("/api/movies/{id}", savedMovie.getMovieId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.movieId").value(savedMovie.getMovieId()))
                .andExpect(jsonPath("$.title").value("Test Movie"));
    }

    @Test
    void testGetMovieByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/movies/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAddMovie() throws Exception {
        Movie movie = new Movie();
        movie.setTitle("New Movie");
        movie.setDescription("New Description");
        movie.setReleaseYear(2025);
        movie.setGenre("Comedy");
        movie.setDuration(90);
        movie.setLanguage("English");
        movie.setDirector("New Director");
        movie.setIsActive(true);

        mockMvc.perform(post("/api/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movie)))
                .andExpect(status().isOk())
                .andExpect(content().string("Movie added successfully"));

        // Verify movie was saved
        assert movieRepository.findAll().size() == 1;
        Movie savedMovie = movieRepository.findAll().get(0);
        assert savedMovie.getTitle().equals("New Movie");
        assert savedMovie.getGenre().equals("Comedy");
    }

    @Test
    @WithMockUser(roles = "USER")
    void testAddMovieUnauthorized() throws Exception {
        Movie movie = new Movie();
        movie.setTitle("New Movie");
        movie.setDescription("New Description");
        movie.setReleaseYear(2025);
        movie.setGenre("Comedy");

        mockMvc.perform(post("/api/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movie)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateMovie() throws Exception {
        // Create test movie
        Movie movie = new Movie();
        movie.setTitle("Original Title");
        movie.setDescription("Original Description");
        movie.setReleaseYear(2025);
        movie.setGenre("Action");
        movie.setDuration(120);
        movie.setLanguage("English");
        movie.setDirector("Original Director");
        movie.setIsActive(true);
        Movie savedMovie = movieRepository.save(movie);

        // Update movie
        movie.setTitle("Updated Title");
        movie.setDescription("Updated Description");

        mockMvc.perform(put("/api/movies/{id}", savedMovie.getMovieId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movie)))
                .andExpect(status().isOk())
                .andExpect(content().string("Movie updated successfully"));

        // Verify movie was updated
        Movie updatedMovie = movieRepository.findById(savedMovie.getMovieId()).orElse(null);
        assert updatedMovie != null;
        assert updatedMovie.getTitle().equals("Updated Title");
        assert updatedMovie.getDescription().equals("Updated Description");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteMovie() throws Exception {
        // Create test movie
        Movie movie = new Movie();
        movie.setTitle("Movie to Delete");
        movie.setDescription("Description");
        movie.setReleaseYear(2025);
        movie.setGenre("Action");
        movie.setDuration(120);
        movie.setLanguage("English");
        movie.setDirector("Director");
        movie.setIsActive(true);
        Movie savedMovie = movieRepository.save(movie);

        mockMvc.perform(delete("/api/movies/{id}", savedMovie.getMovieId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Movie deleted successfully"));

        // Verify movie was deleted
        assert !movieRepository.existsById(savedMovie.getMovieId());
    }

    @Test
    void testGetAllMoviesWithErrorHandling() throws Exception {
        // Test with invalid endpoint to verify error handling
        mockMvc.perform(get("/api/movies/invalid")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}

package com.vijaymovie.api.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/movies")
public class MovieController {
	
	@Value("${tmdb.api.key}")
    private String apiKey;

    @Value("${tmdb.base.url}")
    private String baseUrl;

    @Value("${tmdb.region:IN}")
    private String region;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    @GetMapping("/now-playing")
    public ResponseEntity<List<String>> getNowPlaying() {
        String url = String.format("%s/movie/now_playing?api_key=%s&language=en-US&region=%s",
                baseUrl, apiKey, region);

        String json = restTemplate.getForObject(url, String.class);
        List<String> movies = new ArrayList<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);
            for (JsonNode movie : root.path("results")) {
                String title = movie.path("title").asText();
                double rating = movie.path("vote_average").asDouble();
                String language = movie.path("original_language").asText();
                String releasedate = movie.path("release_date").asText();
                if(language.equalsIgnoreCase("en")) {
                	language = "English";
                }else if(language.equalsIgnoreCase("hi")) {
                	language = "Hindi";
                }else if(language.equalsIgnoreCase("ta")) {
                	language = "Tamil";
                }else if(language.equalsIgnoreCase("te")) {
                	language = "Telugu";
                }
                movies.add("movie name : "+title + ",  rating : (" + rating + ")" + ", language : "+  language + ", release date : "+releasedate);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(List.of("Error parsing movie data"));
        }

        return ResponseEntity.ok(movies);
    }

}

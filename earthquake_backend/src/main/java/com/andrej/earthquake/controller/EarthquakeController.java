package com.andrej.earthquake.controller;

import com.andrej.earthquake.model.Earthquake;
import com.andrej.earthquake.service.EarthquakeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/earthquakes")
@CrossOrigin(origins = "http://localhost:5173")
public class EarthquakeController {

    private final EarthquakeService earthquakeService;

    public EarthquakeController(EarthquakeService earthquakeService) {
        this.earthquakeService = earthquakeService;
    }

    @PostMapping("/fetch")
    public ResponseEntity<List<Earthquake>> fetchAndStore() {
        List<Earthquake> earthquakes = earthquakeService.fetchAndStore();
        return ResponseEntity.ok(earthquakes);
    }

    @GetMapping
    public ResponseEntity<List<Earthquake>> getAll(
            @RequestParam(required = false) Double minMagnitude,
            @RequestParam(required = false) String after) {
        List<Earthquake> earthquakes;
        if (minMagnitude != null || after != null) {
            earthquakes = earthquakeService.getFiltered(minMagnitude, after);
        } else {
            earthquakes = earthquakeService.getAll();
        }
        return ResponseEntity.ok(earthquakes);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        earthquakeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

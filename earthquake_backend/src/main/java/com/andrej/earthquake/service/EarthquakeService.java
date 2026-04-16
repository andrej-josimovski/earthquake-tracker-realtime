package com.andrej.earthquake.service;

import com.andrej.earthquake.exception.*;
import com.andrej.earthquake.model.Earthquake;
import com.andrej.earthquake.repository.EarthquakeRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EarthquakeService {

    private static final String USGS_URL =
            "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson";

    private final EarthquakeRepository repository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public EarthquakeService(EarthquakeRepository repository, RestTemplate restTemplate) {
        this.repository = repository;
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public List<Earthquake> fetchAndStore() {
        try {
            //fetching raw JSON from usgs
            String response;
            try {
                response = restTemplate.getForObject(USGS_URL, String.class);
            } catch (RestClientException e) {
                throw new ApiUnavailableException();
            }

            if (response == null || response.isBlank()) {
                throw new InvalidGeoJsonException("USGS API returned an empty response.");
            }

            JsonNode features;
            try {
                JsonNode root = objectMapper.readTree(response);
                features = root.get("features");
            } catch (Exception e) {
                throw new InvalidGeoJsonException("Failed to parse GeoJSON response.");
            }

            if (features == null || !features.isArray()) {
                throw new InvalidGeoJsonException("'features' field is missing or not an array.");
            }

            List<Earthquake> earthquakesToSave = new ArrayList<>();

            for (JsonNode feature : features) {
                JsonNode props = feature.get("properties");

                //skipame ako nema
                if (props == null) continue;

                JsonNode magNode = props.get("mag");
                JsonNode placeNode = props.get("place");
                JsonNode timeNode = props.get("time");
                JsonNode titleNode = props.get("title");
                JsonNode magTypeNode = props.get("magType");

                //skip na nulls
                if (magNode == null || magNode.isNull()) continue;
                if (timeNode == null || timeNode.isNull()) continue;

                Double magnitude = magNode.asDouble();
                String place = placeNode != null && !placeNode.isNull() ? placeNode.asText() : "Unknown";
                String title = titleNode != null && !titleNode.isNull() ? titleNode.asText() : "Unknown";
                String magType = magTypeNode != null && !magTypeNode.isNull() ? magTypeNode.asText() : "Unknown";
                Instant time = Instant.ofEpochMilli(timeNode.asLong());

                earthquakesToSave.add(new Earthquake(magnitude, magType, place, title, time));
            }

            try {
                repository.deleteAll();
                repository.saveAll(earthquakesToSave);
            } catch (DataAccessException e) {
                throw new DatabaseException(e.getMessage());
            }

            return earthquakesToSave;

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch earthquake data: " + e.getMessage(), e);
        }
    }

    public List<Earthquake> getAll() {
        return repository.findAll();
    }

    public List<Earthquake> getFiltered(Double minMagnitude, String after) {
        List<Earthquake> earthquakeList = repository.findAll();
        Instant afterInstant = null;
        if (after != null) {
            try {
                afterInstant = LocalDateTime.parse(after).toInstant(ZoneOffset.UTC);
            } catch (Exception e) {
                throw new InvalidDateFormatException();
            }
        }
        final Instant afterFinal = afterInstant;
        return earthquakeList.stream()
                .filter(e -> {
                    if (minMagnitude != null && e.getMagnitude() != null) {
                        if (e.getMagnitude() <= minMagnitude) return false;
                    }
                    if (afterFinal != null && e.getTime() != null) {
                        return !e.getTime().isBefore(afterFinal);
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new EarthquakeNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
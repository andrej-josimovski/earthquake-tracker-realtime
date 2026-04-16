package com.andrej.earthquake.service;

import com.andrej.earthquake.exception.EarthquakeNotFoundException;
import com.andrej.earthquake.exception.InvalidDateFormatException;
import com.andrej.earthquake.model.Earthquake;
import com.andrej.earthquake.repository.EarthquakeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.client.RestTemplate;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(classes = EarthquakeServiceIntegrationTest.TestConfig.class)
class EarthquakeServiceIntegrationTest {

    @Autowired
    private EarthquakeService earthquakeService;

    @Autowired
    private EarthquakeRepository repository;

    @Autowired
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        reset(repository);
    }

    @Test
    void fetchAndStore_parsesGeoJsonAndPersistsEarthquakes() {
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
        server.expect(requestTo("https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson"))
                .andRespond(withSuccess("""
                        {
                          "features": [
                            {
                              "properties": {
                                "mag": 4.2,
                                "place": "10km S of Test City",
                                "time": 1713225600000,
                                "title": "M 4.2 - Test City",
                                "magType": "mb"
                              }
                            }
                          ]
                        }
                        """, MediaType.APPLICATION_JSON));

        List<Earthquake> earthquakes = earthquakeService.fetchAndStore();

        assertEquals(1, earthquakes.size());
        Earthquake earthquake = earthquakes.get(0);
        assertEquals(4.2, earthquake.getMagnitude());
        assertEquals("mb", earthquake.getMagType());
        assertEquals("10km S of Test City", earthquake.getPlace());
        assertEquals("M 4.2 - Test City", earthquake.getTitle());
        assertEquals(Instant.ofEpochMilli(1713225600000L), earthquake.getTime());
        verify(repository).deleteAll();
        verify(repository).saveAll(earthquakes);
        server.verify();
    }

    @Test
    void getAll_returnsAllEarthquakesFromRepository() {
        List<Earthquake> storedEarthquakes = List.of(
                new Earthquake(2.5, "ml", "Place A", "Title A", Instant.parse("2024-01-01T00:00:00Z")),
                new Earthquake(5.1, "mb", "Place B", "Title B", Instant.parse("2024-01-02T00:00:00Z"))
        );
        when(repository.findAll()).thenReturn(storedEarthquakes);

        List<Earthquake> result = earthquakeService.getAll();

        assertSame(storedEarthquakes, result);
        verify(repository).findAll();
    }

    @Test
    void getFiltered_appliesMagnitudeAndAfterFilters() {
        List<Earthquake> storedEarthquakes = List.of(
                new Earthquake(2.5, "ml", "Old Place", "Old", Instant.parse("2024-01-01T00:00:00Z")),
                new Earthquake(4.0, "mb", "Too Small", "Too Small", Instant.parse("2024-01-03T00:00:00Z")),
                new Earthquake(5.1, "mb", "Kept Place", "Kept", Instant.parse("2024-01-04T00:00:00Z"))
        );
        when(repository.findAll()).thenReturn(storedEarthquakes);

        List<Earthquake> result = earthquakeService.getFiltered(4.0, "2024-01-02T00:00:00");

        assertEquals(1, result.size());
        assertEquals("Kept Place", result.get(0).getPlace());
        verify(repository).findAll();
    }

    @Test
    void getFiltered_rejectsInvalidDateFormat() {
        when(repository.findAll()).thenReturn(List.of());

        assertThrows(InvalidDateFormatException.class, () -> earthquakeService.getFiltered(null, "2024/01/02"));
        verify(repository).findAll();
    }

    @Test
    void deleteById_deletesExistingEarthquake() {
        when(repository.existsById(7L)).thenReturn(true);

        earthquakeService.deleteById(7L);

        verify(repository).existsById(7L);
        verify(repository).deleteById(7L);
    }

    @Test
    void deleteById_throwsWhenEarthquakeDoesNotExist() {
        when(repository.existsById(7L)).thenReturn(false);

        assertThrows(EarthquakeNotFoundException.class, () -> earthquakeService.deleteById(7L));
        verify(repository).existsById(7L);
    }

    @Configuration
    static class TestConfig {

        @Bean
        EarthquakeRepository earthquakeRepository() {
            return mock(EarthquakeRepository.class);
        }

        @Bean
        RestTemplate restTemplate() {
            return new RestTemplate();
        }

        @Bean
        EarthquakeService earthquakeService(EarthquakeRepository earthquakeRepository, RestTemplate restTemplate) {
            return new EarthquakeService(earthquakeRepository, restTemplate);
        }
    }
}


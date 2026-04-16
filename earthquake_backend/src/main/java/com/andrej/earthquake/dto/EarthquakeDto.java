package com.andrej.earthquake.dto;

import java.time.Instant;

public class EarthquakeDto {

    private Double magnitude;
    private String magType;
    private String place;
    private String title;
    private Instant time;

    public EarthquakeDto() {}

    public Double getMagnitude() { return magnitude; }
    public void setMagnitude(Double magnitude) { this.magnitude = magnitude; }

    public String getMagType() { return magType; }
    public void setMagType(String magType) { this.magType = magType; }

    public String getPlace() { return place; }
    public void setPlace(String place) { this.place = place; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Instant getTime() { return time; }
    public void setTime(Instant time) { this.time = time; }
}
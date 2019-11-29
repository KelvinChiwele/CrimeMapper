package com.techart.crimemapper.models;

/**
 * Object for a Report
 */

public class Report {
    private String userUrl;
    private String subject;
    private String icon;
    private String place;
    private Double latitude;
    private Double longitude;
    private String particularOfOffence;
    private String modeOfSubmission;
    private String date;
    private String status;
    private String station;

    public Report() {  }

    public String getUserUrl() {
        return userUrl;
    }

    public void setUserUrl(String userUrl) {
        this.userUrl = userUrl;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getParticularOfOffence() {
        return particularOfOffence;
    }

    public void setParticularOfOffence(String particularOfOffence) {
        this.particularOfOffence = particularOfOffence;
    }

    public String getModeOfSubmission() {
        return modeOfSubmission;
    }

    public void setModeOfSubmission(String modeOfSubmission) {
        this.modeOfSubmission = modeOfSubmission;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }
}

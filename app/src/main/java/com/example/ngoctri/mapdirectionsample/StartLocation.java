package com.example.ngoctri.mapdirectionsample;

class StartLocation {
    private Double lat;
    private Double lng;

    public StartLocation() {
    }

    public StartLocation(Double lat, Double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}

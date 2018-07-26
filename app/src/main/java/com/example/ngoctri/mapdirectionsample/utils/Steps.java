package com.example.ngoctri.mapdirectionsample.utils;

public class Steps {
    private EndLocation end_location;
    private StartLocation start_location;
    private String maneuver;
    private String html_instructions;

    public Steps() {
    }

    public Steps(EndLocation end_location, StartLocation start_location, String maneuver, String html_instructions) {
        this.end_location = end_location;
        this.start_location = start_location;
        this.maneuver = maneuver;
        this.html_instructions = html_instructions;
    }

    public EndLocation getEnd_location() {
        return end_location;
    }

    public void setEnd_location(EndLocation end_location) {
        this.end_location = end_location;
    }

    public StartLocation getStart_location() {
        return start_location;
    }

    public void setStart_location(StartLocation start_location) {
        this.start_location = start_location;
    }

    public String getManeuver() {
        return maneuver;
    }

    public void setManeuver(String maneuver) {
        this.maneuver = maneuver;
    }

    public String getHtml_instructions() {
        return html_instructions;
    }

    public void setHtml_instructions(String html_instructions) {
        this.html_instructions = html_instructions;
    }
}

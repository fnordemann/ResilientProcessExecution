package org.example.eureka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Metadata {

    @JsonProperty
    private String urlanalysis;
    @JsonProperty
    private String cost;
    @JsonProperty
    private String accuracy;
    @JsonProperty
    private String location;
    @JsonProperty("management.port")
    private String management_port;
    @JsonProperty
    private String time;
    @JsonProperty
    private String type;
    @JsonProperty
    private String urlstatus;
    @JsonProperty
    private String urllog;
    @JsonProperty
    private String urlgps;

    public Metadata() {
    }

    public Metadata(String urlanalysis, String cost, String accuracy, String location, String management_port, String time, String type, String urlstatus, String urllog, String urlgps) {
        this.urlanalysis = urlanalysis;
        this.cost = cost;
        this.accuracy = accuracy;
        this.location = location;
        this.management_port = management_port;
        this.time = time;
        this.type = type;
        this.urlstatus = urlstatus;
        this.urllog = urllog;
        this.urlgps = urlgps;
    }


    public String getUrlanalysis() {
        return urlanalysis;
    }

    public void setUrlanalysis(String urlanalysis) {
        this.urlanalysis = urlanalysis;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getManagement_port() {
        return management_port;
    }

    public void setManagement_port(String management_port) {
        this.management_port = management_port;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrlstatus() {
        return urlstatus;
    }

    public void setUrlstatus(String urlstatus) {
        this.urlstatus = urlstatus;
    }

    public String getUrllog() {
        return urllog;
    }

    public void setUrllog(String urllog) {
        this.urllog = urllog;
    }

    public String getUrlgps() {
        return urlgps;
    }

    public void setUrlgps(String urlgps) {
        this.urlgps = urlgps;
    }

    @Override
    public String toString() {
        return "[urlanalysis = " + urlanalysis + ", urlstatus = " + urlstatus + ", urllog = " + urllog + ", cost = " + cost + ", accuracy = " + accuracy + ", location = " + location + ", management.port = " + management_port + ", time = " + time + ", type = " + type + "]";
    }
}
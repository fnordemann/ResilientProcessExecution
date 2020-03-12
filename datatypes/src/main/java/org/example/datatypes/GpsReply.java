package org.example.datatypes;

// Message format
public class GpsReply {

    private String taskId;
    private double latOffset;
    private double longOffset;

    public GpsReply() {
    }

    public GpsReply(String taskId, double latOffset, double longOffset) {
        this.taskId = taskId;
        this.latOffset = latOffset;
        this.longOffset = longOffset;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public double getLatOffset() {
        return latOffset;
    }

    public void setLatOffset(double latOffset) {
        this.latOffset = latOffset;
    }

    public double getLongOffset() {
        return longOffset;
    }

    public void setLongOffset(double longOffset) {
        this.longOffset = longOffset;
    }

}

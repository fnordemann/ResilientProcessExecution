package org.example.datatypes;

public class ProcessStart {

    private String taskId;

    private double dMinAccuracy;

    private double dCostLimit;

    private double dAccuracyWeight;

    private double dCostWeight;

    private double dTimeWeight;

    public ProcessStart() {
    }

    public ProcessStart(String taskId, double dMinAccuracy, double dCostLimit, double dAccuracyWeight, double dCostWeight, double dTimeWeight) {
        this.taskId = taskId;
        this.dMinAccuracy = dMinAccuracy;
        this.dCostLimit = dCostLimit;
        this.dAccuracyWeight = dAccuracyWeight;
        this.dCostWeight = dCostWeight;
        this.dTimeWeight = dTimeWeight;
    }

    public String getTaskId() {
        return taskId;
    }

    public double getdMinAccuracy() {
        return dMinAccuracy;
    }

    public void setdMinAccuracy(double dMinAccuracy) {
        this.dMinAccuracy = dMinAccuracy;
    }

    public double getdCostLimit() {
        return dCostLimit;
    }

    public void setdCostLimit(double dCostLimit) {
        this.dCostLimit = dCostLimit;
    }

    public double getdAccuracyWeight() {
        return dAccuracyWeight;
    }

    public void setdAccuracyWeight(double dAccuracyWeight) {
        this.dAccuracyWeight = dAccuracyWeight;
    }

    public double getdCostWeight() {
        return dCostWeight;
    }

    public void setdCostWeight(double dCostWeight) {
        this.dCostWeight = dCostWeight;
    }

    public double getdTimeWeight() {
        return dTimeWeight;
    }

    public void setdTimeWeight(double dTimeWeight) {
        this.dTimeWeight = dTimeWeight;
    }
}

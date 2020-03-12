package org.example.datatypes;

public class Neighbor {

    private String neighborAddress;
    //private String neighborConnectivity;
    //private String neighborLocation;

    public Neighbor() {
    }

    public Neighbor(String neighborAddress) {
        this.neighborAddress = neighborAddress;
    }

    public String getNeighborAddress() {
        return neighborAddress;
    }

    public void setNeighborAddress(String neighborAddress) {
        this.neighborAddress = neighborAddress;
    }

    /*
    public Neighbor(String neighborAddress, String neighborConnectivity, String neighborLocation) {
        this.neighborAddress = neighborAddress;
        this.neighborConnectivity = neighborConnectivity;
        this.neighborLocation = neighborLocation;
    }

    public String getNeighborConnectivity() {
        return neighborConnectivity;
    }

    public void setNeighborConnectivity(String neighborConnectivity) {
        this.neighborConnectivity = neighborConnectivity;
    }

    public String getNeighborLocation() {
        return neighborLocation;
    }

    public void setNeighborLocation(String neighborLocation) {
        this.neighborLocation = neighborLocation;
    }
    */
}

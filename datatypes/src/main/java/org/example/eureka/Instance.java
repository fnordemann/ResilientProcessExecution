package org.example.eureka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Instance {
    @JsonProperty
    private String app;
    @JsonProperty
    private String hostName;
    @JsonProperty
    private Metadata metadata;
    @JsonProperty
    private String instanceId;
    @JsonProperty
    private Port port;
    @JsonProperty
    private String vipAddress;
    @JsonProperty
    private String ipAddr;
    @JsonProperty
    private String status;

    public Instance() {
    }

    public Instance(String instanceId) {
        this.instanceId = instanceId;
    }

    public Instance(String app, String hostName, Metadata metadata, String instanceId, Port port, String vipAddress, String ipAddr, String status) {
        this.app = app;
        this.hostName = hostName;
        this.metadata = metadata;
        this.instanceId = instanceId;
        this.port = port;
        this.vipAddress = vipAddress;
        this.ipAddr = ipAddr;
        this.status = status;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public Port getPort() {
        return port;
    }

    public void setPort(Port port) {
        this.port = port;
    }

    public String getVipAddress() {
        return vipAddress;
    }

    public void setVipAddress(String vipAddress) {
        this.vipAddress = vipAddress;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "[app = " + app + ", hostName = " + hostName + ", metadata = " + metadata + ", instanceId = " + instanceId + ", port = " + port + ", vipAddress = " + vipAddress + ", ipAddr = " + ipAddr + ", status = " + status + "]";
    }
}
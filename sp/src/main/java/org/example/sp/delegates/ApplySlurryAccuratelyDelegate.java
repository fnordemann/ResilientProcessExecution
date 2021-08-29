package org.example.sp.delegates;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.example.datatypes.GpsReply;
import org.example.datatypes.GpsRequest;
import org.example.eureka.Instance;
import org.example.eureka.Metadata;
import org.example.sp.functions.ServiceDecisionWSM;
import org.example.sp.functions.ServiceDiscovery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Component
public class ApplySlurryAccuratelyDelegate implements JavaDelegate {

    @Autowired
    private DiscoveryClient discoveryClient;

    // Camunda variables
    private ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    private RuntimeService runtimeService = processEngine.getRuntimeService();
    private final static Logger LOGGER = Logger.getLogger("APPLY-SLURRY-ACCURATELY");

    int debug = 0;
    private ServiceDecisionWSM serviceDecisionWSM = new ServiceDecisionWSM();
    private ServiceDiscovery serviceDiscovery = new ServiceDiscovery();

    public void execute(DelegateExecution execution) throws Exception {
        // Setup process variables
        String taskId = "0000";

        // Fetch taskId
        try {
            taskId = execution.getVariable("taskId").toString();
        } catch (Exception e) {
            taskId = "0000";
            if (debug == 1)
                LOGGER.info("No taskId provided. Using taskId " + taskId + ".");
        }

        // Do work
        LOGGER.info("Correcting GPS.");

        // Prepare service search
        boolean searchService = true;
        Instance serviceInstance = null;
        Map<String, String> metadataMap;
        String serviceId = "gps-service";
        String[] criteria = {"accuracy", "cost", "time"};
        double[] criteriaWeights = new double[]{0.7, 0.2, 0.1};

        LOGGER.info("Fetching available services for serviceId " + serviceId + "...");
        List<Instance> instanceList = serviceDiscovery.findServices("gps-service");

        LOGGER.info("Available services: " + instanceList.size());
        for (int i = 0; i < instanceList.size(); i++) {
            serviceInstance = instanceList.get(i);
            System.out.println("--- Service number " + i + " ---");
            System.out.println("Host: " + serviceInstance.getHostName());
            System.out.println("InstanceId: " + serviceInstance.getInstanceId());
            System.out.println("Metadata: " + serviceInstance.getMetadata().toString());
        }

        // Search for service until an appropriate is found
        while (searchService) {
            // Instances found?
            if (instanceList.size() > 0) {
                // Select by using decision matrix
                serviceInstance = serviceDecisionWSM.selectServiceRestBased(instanceList, criteria, criteriaWeights);

                if (serviceInstance != null) {
                    // Call chosen instance
                    Metadata metadata = serviceInstance.getMetadata();
                    String serviceString = metadata.getUrlanalysis();
                    String urlString = "http://" + serviceInstance.getHostName() + ":" + serviceInstance.getMetadata().getManagement_port() + serviceString;

                    // URI available?
                    if (serviceString != null && serviceString != "") {

                        // URI absolute?
                        URI serviceUri = null;
                        try {
                            serviceUri = new URI(urlString);
                        } catch (Exception e) {
                            LOGGER.info("URI Exception: " + e.toString());
                        }
                        if (serviceUri.isAbsolute()) {

                            // POST example
                            GpsRequest gpsRequest = new GpsRequest();
                            gpsRequest.setTaskId(taskId);

                            LOGGER.info("Going to call service at " + serviceUri.toString());
                            //restTemplate = new RestTemplate();
                            try {
                                ObjectMapper objectMapper = new ObjectMapper();
                                RestTemplate restTemplate = new RestTemplate();
                                HttpHeaders headers = new HttpHeaders();
                                headers.setContentType(MediaType.APPLICATION_JSON);

                                HttpEntity<GpsRequest> requestEntity = new HttpEntity<GpsRequest>(gpsRequest, headers);
                                ResponseEntity<GpsReply> responseEntity = restTemplate.postForEntity(serviceUri, requestEntity, GpsReply.class);
                                GpsReply gpsReply = responseEntity.getBody();
                                LOGGER.info("GPS correction for taskId " + gpsReply.getTaskId() + " resulted in offsets: " + gpsReply.getLatOffset() + " / " + gpsReply.getLongOffset());
                                searchService = false;
                            } catch (HttpServerErrorException e) {
                                LOGGER.info("Could not call service " + serviceInstance.getInstanceId() + ": HTTP 500");
                                instanceList.remove(serviceInstance);
                            } catch (Exception e) {
                                LOGGER.info("Could not call service " + serviceInstance.getInstanceId() + "!");
                                LOGGER.info(e.toString());
                                instanceList.remove(serviceInstance);
                            }
                        } else {
                            LOGGER.info("URI not absolute: " + serviceUri.toString());
                            instanceList.remove(serviceInstance);
                        }
                    } else {
                        LOGGER.info("No valid service address available! urlString: " + urlString);
                        instanceList.remove(serviceInstance);
                    }
                } else {
                    LOGGER.info("Received no service instance from service dictionary!");
                }
            } else {
                LOGGER.info("No services for serviceId " + serviceId + " available!");
                instanceList.clear();
                searchService = false;
            }
        }
    }
}

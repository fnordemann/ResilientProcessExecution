package org.example.sp.delegates;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.example.datatypes.AnalysisReply;
import org.example.datatypes.AnalysisRequest;
import org.example.eureka.Instance;
import org.example.eureka.Metadata;
import org.example.sp.functions.ServiceDecisionGraph;
import org.example.sp.functions.ServiceSearch;
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
public class AnalyzeSlurryIngredientsDelegate implements JavaDelegate {

    @Autowired
    private DiscoveryClient discoveryClient;

    // Camunda variables
    private ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    private RuntimeService runtimeService = processEngine.getRuntimeService();
    private final static Logger LOGGER = Logger.getLogger("ANALYZE-SLURRY");

    private ServiceSearch serviceSearch = new ServiceSearch();

    int debug = 0;


    public void execute(DelegateExecution execution) throws Exception {

        // Variables
        String taskId;
        double dMinAccuracy;
        double dCostLimit;
        double dAccuracyWeight;
        double dCostWeight;
        double dTimeWeight;

        ServiceDecisionGraph serviceDecisionGraph = new ServiceDecisionGraph();

        // Fetch taskId
        try {
            taskId = execution.getVariable("taskId").toString();
        } catch (Exception e) {
            taskId = "0000";
            if (debug == 1)
                LOGGER.info("No taskId provided. Using taskId " + taskId + ".");
        }

        // Fetch dMinAccuracy
        try {
            dMinAccuracy = Double.parseDouble(execution.getVariable("dMinAccuracy").toString());
        } catch (Exception e) {
            dMinAccuracy = 0.3;
            if (debug == 1)
                LOGGER.info("Could not fetch dMinAccuracy. Using dMinAccuracy of 0.3");
        }

        // Fetch dCostLimit
        try {
            dCostLimit = Double.parseDouble(execution.getVariable("dCostLimit").toString());
        } catch (Exception e) {
            dCostLimit = 2.0;
            if (debug == 1)
                LOGGER.info("Could not fetch dCostLimit. Using dCostLimit of 2.0");
        }

        // Fetch dAccuracyWeight
        try {
            dAccuracyWeight = Double.parseDouble(execution.getVariable("dAccuracyWeight").toString());
        } catch (Exception e) {
            dAccuracyWeight = 0.5;
            if (debug == 1)
                LOGGER.info("Could not fetch dAccuracyWeight. Using dAccuracyWeight of 0.5");
        }

        // Fetch dCostWeight
        try {
            dCostWeight = Double.parseDouble(execution.getVariable("dCostWeight").toString());
        } catch (Exception e) {
            dCostWeight = 0.3;
            if (debug == 1)
                LOGGER.info("Could not fetch dCostWeight. Using dCostWeight of 0.3");
        }

        // Fetch dTimeWeight
        try {
            dTimeWeight = Double.parseDouble(execution.getVariable("dTimeWeight").toString());
        } catch (Exception e) {
            dTimeWeight = 0.2;
            if (debug == 1)
                LOGGER.info("Could not fetch dTimeWeight. Using dTimeWeight of 0.2");
        }


        // Do work
        LOGGER.info("Analyzing slurry.");

        // Prepare service search
        boolean searchService = true;
        Instance serviceInstance = null;
        Map<String, String> metadataMap;
        String serviceId = "ingredients-service";

        LOGGER.info("Fetching available services for serviceId " + serviceId + "...");
        List<Instance> instanceList = serviceSearch.findServices(serviceId);

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
            // Update graph for precision farming segment
            serviceDecisionGraph.updateGraph("precision-farming", serviceSearch.findServices("precision-farming-service"), dAccuracyWeight, dCostWeight, dTimeWeight);
            // Update graph for slurry analysis segment
            serviceDecisionGraph.updateGraph("slurry-analysis", instanceList, dAccuracyWeight, dCostWeight, dTimeWeight);
            // Update graph for position correction segment
            serviceDecisionGraph.updateGraph("position-sensing", serviceSearch.findServices("gps-service"), dAccuracyWeight, dCostWeight, dTimeWeight);
            serviceDecisionGraph.printGraph();

            String sChosenId = serviceDecisionGraph.selectServiceGraphBased("G1", "S'", dMinAccuracy, dCostLimit, "slurry-analysis");
            serviceInstance = serviceDecisionGraph.getInstanceForId(instanceList, sChosenId);

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
                        AnalysisRequest analysisRequest = new AnalysisRequest();
                        analysisRequest.setTaskId(taskId);

                        LOGGER.info("Going to call service at " + serviceUri.toString());
                        //restTemplate = new RestTemplate();
                        try {
                            ObjectMapper objectMapper = new ObjectMapper();
                            RestTemplate restTemplate = new RestTemplate();
                            HttpHeaders headers = new HttpHeaders();
                            headers.setContentType(MediaType.APPLICATION_JSON);

                            HttpEntity<AnalysisRequest> requestEntity = new HttpEntity<AnalysisRequest>(analysisRequest, headers);
                            ResponseEntity<AnalysisReply> responseEntity = restTemplate.postForEntity(serviceUri, requestEntity, AnalysisReply.class);
                            AnalysisReply analysisReply = responseEntity.getBody();
                            LOGGER.info("Analysis for taskId " + analysisReply.getTaskId() + " resulted in nitrogen: " + analysisReply.getNitrogen());
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
                // No path found! Process fails!
                LOGGER.warning("Process fails. Process instance is deleted. Please restart proof-of-concept.");
                LOGGER.warning("Variables:");
                LOGGER.warning("\tsChosenId: " + sChosenId);
                searchService = false;
                runtimeService.deleteProcessInstance(execution.getProcessInstanceId(), sChosenId);
                System.exit(-1);
            }
        }
    }
}
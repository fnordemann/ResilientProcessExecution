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
import org.example.sp.functions.ServiceDecision;
import org.example.sp.functions.ServiceDecisionGraph;
import org.example.sp.functions.ServiceSearch;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
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

    int debug = 0;
    private ServiceDecisionGraph serviceDecisionGraph = new ServiceDecisionGraph();
    private ServiceSearch serviceSearch = new ServiceSearch();

    double dCostLimit = 2.0;


    public void execute(DelegateExecution execution) throws Exception {

        // Variables
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
        LOGGER.info("Analyzing slurry.");

        searchAndDecideRestBased(taskId);

    }

    // Search and decide based on Eureka REST calls
    private void searchAndDecideRestBased(String taskId) {
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
            // Instances found?
            if (instanceList.size() > 0) {
                // Select by using a multi-criteria graph
                // Update graph for slurry analysis segment
                //serviceDecisionGraph.updateGraph(instanceList);
                // Update graph for position correction segment
                serviceDecisionGraph.updateGraph(serviceSearch.findServices("gps-service"));
                serviceDecisionGraph.printGraph();

                serviceInstance = serviceDecisionGraph.selectServiceGraphBased(instanceList, "G1", "S'", dCostLimit, "slurry-analysis");

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
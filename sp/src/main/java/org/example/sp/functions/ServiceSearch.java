package org.example.sp.functions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RuntimeService;
import org.example.datatypes.Neighbor;
import org.example.eureka.Instance;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

// Class to search for Eureka services / applications
public class ServiceSearch {

    // Camunda variables
    private ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    private RuntimeService runtimeService = processEngine.getRuntimeService();
    private final static Logger LOGGER = Logger.getLogger("SERVICE-SEARCH");
    int debug = 0;

    public ServiceSearch() {
        // For test env
        addTestEnvNeighbors();
    }

    // Find services from different Eureka servers
    public List<Instance> findServices(String serviceId) {

        // Variables
        int debug = 0;
        ObjectMapper objectMapper = new XmlMapper();
        RestTemplate restTemplate = new RestTemplate();
        List<Instance> instanceList = new ArrayList<Instance>();
        List<String> serviceIdList = new ArrayList<String>();
        String serviceUri = null;
        String serviceTypeUri = null;
        String eurekaServerUri = null;
        String appString = "eureka/apps/";
        List<String> serverList;

        // Configure headers to use XML
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_XML));
        HttpEntity<String> requestEntity = new HttpEntity<String>("parameters", headers);

        // Prepare to search
        instanceList.clear();

        // Update list of servers according to neighbor table
        serverList = updateServerList();

        // Search over all available Eureka servers
        for (int numServers = 0; numServers < serverList.size(); numServers++) {
            eurekaServerUri = serverList.get(numServers) + appString;
            serviceTypeUri = eurekaServerUri + serviceId;
            serviceIdList.clear();
            String responseStrBody = null;

            // Query Eureka-REST-API for services
            try {
                ResponseEntity<String> responseEntityStr = restTemplate.exchange(serviceTypeUri, HttpMethod.GET, requestEntity, String.class);
                //ResponseEntity<Application> responseEntityApp = restTemplate.exchange(serviceTypeUri, HttpMethod.GET, requestEntity, Application.class);
                responseStrBody = responseEntityStr.getBody();
            } catch (HttpClientErrorException httpClientErrorException) {
                if (debug == 2)
                    LOGGER.info("Server " + serverList.get(numServers) + " not reachable or has no services available: " + httpClientErrorException.toString());
            } catch (Exception e) {
                LOGGER.info("Exception: " + e.toString());
            }

            // Found services on current server?
            if (responseStrBody != null) {
                try {
                    // Fetch instanceIds from result
                    DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    InputSource is = new InputSource();
                    is.setCharacterStream(new StringReader(responseStrBody));
                    Document doc = db.parse(is);

                    // Search for instances
                    NodeList instanceNodes = doc.getElementsByTagName("instance");
                    if (instanceNodes.getLength() > 0) {
                        if (debug == 2)
                            LOGGER.info("Found instance/s:");
                        for (int i = 0; i < instanceNodes.getLength(); i++) {
                            Node node = instanceNodes.item(i);

                            // Create instance POJO
                            StringWriter stringWriter = new StringWriter();
                            try {
                                Transformer t = TransformerFactory.newInstance().newTransformer();
                                t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                                t.setOutputProperty(OutputKeys.INDENT, "yes");
                                t.transform(new DOMSource(node), new StreamResult(stringWriter));
                                Instance serivceInstance = objectMapper.readValue(stringWriter.toString(), Instance.class);
                                if (debug == 2)
                                    LOGGER.info("serviceInstance " + i + " of server " + serverList.get(numServers) + " : " + serivceInstance.toString());
                                // Add to list
                                instanceList.add(serivceInstance);
                            } catch (TransformerException te) {
                                LOGGER.info("Transformer Exception while creating instance POJO");
                            } catch (JsonProcessingException jp) {
                                LOGGER.info("JSON processing failed while creating instance POJO");
                            }
                        }
                    }
                } catch (Exception e) {
                    LOGGER.info("Exception: " + e.toString());
                }
            }
        }

        // Return list of services
        return instanceList;
    }

    // Get the current list of neighbors
    public List<String> updateServerList() {
        String neighborServiceUrl = "http://localhost:8031/neighbortable/";
        List<String> serverList = new ArrayList<String>();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            ResponseEntity<Neighbor[]> responseEntity = restTemplate.getForEntity(neighborServiceUrl, Neighbor[].class);
            Neighbor[] neighborArray = responseEntity.getBody();
            for (int i = 0; i < neighborArray.length; i++) {
                serverList.add(neighborArray[i].getNeighborAddress());
            }
            if (debug == 1)
                LOGGER.info("Current neighbors of SP (Eureka server list): " + serverList.toString());
        } catch (HttpServerErrorException e) {
            LOGGER.info("Could not call neighbor service!");
        } catch (Exception e) {
            LOGGER.info("Could not call neighbor service!");
            LOGGER.info(e.toString());
        }
        return serverList;
    }

    // For running proof-of-concept on a local system (no MANET in place)
    public void addTestEnvNeighbors() {
        String neighborServiceUrl = "http://localhost:8031/testenvsp/";

        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getForObject(neighborServiceUrl, String.class);
            if (debug > 0)
                LOGGER.info("Added TestEnvNeighbors to SP.");
        } catch (Exception e) {
            LOGGER.info("Could not add TestEnvNeighbors to SP!");
            LOGGER.info(e.toString());
        }
    }
}

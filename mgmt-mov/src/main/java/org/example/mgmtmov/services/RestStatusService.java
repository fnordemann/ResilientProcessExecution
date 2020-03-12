package org.example.mgmtmov.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.MessageCorrelationResult;
import org.example.datatypes.ProcessStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@RestController
public class RestStatusService {
    // Variables
    private static List<ProcessStatus> processStatusList = new ArrayList<ProcessStatus>();
    private ObjectMapper objectMapper = new ObjectMapper();
    private final static Logger LOGGER = Logger.getLogger("REST-STATUS");
    private ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    private RuntimeService runtimeService = processEngine.getRuntimeService();

    //@Autowired
    //private DiscoveryClient discoveryClient;

    // Constructor
    public RestStatusService() {
    }

    // Catch and consume posted status
    @RequestMapping(value = "/status", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public Response postStatus(@RequestBody ProcessStatus givenProcessStatus) {
        String jsonString = "empty";

        // Mandatory properties available?
        if (givenProcessStatus.getTaskId() == null) {
            LOGGER.info("Mandatory fields missing.");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        // Mandatory properties filled?
        if (givenProcessStatus.getTaskId().isEmpty()) {
            LOGGER.info("Mandatory fields missing.");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        // add to list
        this.processStatusList.add(givenProcessStatus);
        LOGGER.info("Received status " + givenProcessStatus.getTaskStatus() + " of taskId "
                + givenProcessStatus.getTaskId());

        // map object to JSON for Camunda transfer
        try {
            jsonString = objectMapper.writeValueAsString(givenProcessStatus);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // create message event to inform running BPMN activity
        List<MessageCorrelationResult> results = runtimeService.createMessageCorrelation("ProcessStatus")
                //.setVariable("ProcessStatus", givenProcessStatus).correlateAllWithResult();
                .setVariable("ProcessStatus", jsonString).correlateAllWithResult();

        LOGGER.info("Triggered message event ProcessStatus " + results.size() + " times.");

        // return object as success message
        if (this.processStatusList.isEmpty()) {
            return Response.ok(jsonString, MediaType.APPLICATION_JSON).build();
        } else {
            // return Response.ok().build();
            return Response.ok(jsonString, MediaType.APPLICATION_JSON).build();
        }
    }

    @RequestMapping(value = "/status", method = RequestMethod.POST, consumes = "text/plain", produces = "text/plain")
    @ResponseBody
    public Response postStatus(@RequestBody String textString) {
        LOGGER.info("Received text message: " + textString);

        // replace by given
        ProcessStatus givenProcessStatus = new ProcessStatus("MyTask", Integer.parseInt(textString), 0.0, 0.0, 1);

        List<MessageCorrelationResult> results = runtimeService.createMessageCorrelation("ProcessStatus")
                .setVariable("ProcessStatus", givenProcessStatus).correlateAllWithResult();
        if (results.size() > 0)
            LOGGER.info("Triggered message event ProcessStatus " + results.size() + " times.");

        return Response.ok(textString, MediaType.TEXT_PLAIN).build();

        /*
         * List<ProcessInstance> processInstanceList =
         * runtimeService.createProcessInstanceQuery()
         * .processDefinitionKey("MgmtKey").list();
         * LOGGER.info("processInstanceList-Size: " + processInstanceList.size());
         *
         * if (processInstanceList.size() > 0) { ProcessInstance processInstance =
         * processInstanceList.get(0);
         *
         * }
         */
        // runtimeService.createMessageCorrelation("ProcessStatus").processInstanceBusinessKey("100").correlateAll();
        /*
         * Execution execution2 =
         * runtimeService.createExecutionQuery().messageEventSubscriptionName(
         * "ProcessStatus") .singleResult(); if (execution2 != null) {
         * LOGGER.info("execution2-ToString: " + execution2.toString()); }
         */
    }


    // Return last status
    @RequestMapping(value = "/status", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Response getLastStatus() {
        String json = "empty";

        if (this.processStatusList.isEmpty()) {
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        } else {
            try {
                json = objectMapper.writeValueAsString(processStatusList.get(processStatusList.size() - 1));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }
}

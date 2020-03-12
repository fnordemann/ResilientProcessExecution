package org.example.mgmt.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.MessageCorrelationResult;
import org.example.datatypes.ProcessStatus;
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
    int debug = 0;

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

        if (debug > 0)
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
                .setVariable("ProcessStatus", jsonString).correlateAllWithResult();

        if (debug > 0)
            LOGGER.info("Triggered message event ProcessStatus " + results.size() + " time(s).");

        // return object as success message
        if (this.processStatusList.isEmpty()) {
            return Response.ok(jsonString, MediaType.APPLICATION_JSON).build();
        } else {
            // return Response.ok().build();
            return Response.ok(jsonString, MediaType.APPLICATION_JSON).build();
        }
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

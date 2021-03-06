package org.example.sp.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.MessageCorrelationResult;
import org.example.datatypes.ProcessStart;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@RestController
public class RestStart {
    // Variables
    private ProcessStart latestProcessStart = null;
    private ObjectMapper objectMapper = new ObjectMapper();
    private final static Logger LOGGER = Logger.getLogger("REST-START");
    private ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    private RuntimeService runtimeService = processEngine.getRuntimeService();

    // Constructor
    public RestStart() {
    }

    // Catch and consume posted status
    @RequestMapping(value = "/start", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public Response postStatus(@RequestBody ProcessStart givenProcessStart) {
        String jsonString = "empty";

        // Mandatory properties available?
        if (givenProcessStart.getTaskId() == null) {
            LOGGER.info("Mandatory fields missing.");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        // Mandatory properties filled?
        if (givenProcessStart.getTaskId().isEmpty()) {
            LOGGER.info("Mandatory fields missing.");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        LOGGER.info("");
        LOGGER.info("---------");
        LOGGER.info("Slurry process started with taskId "
                + givenProcessStart.getTaskId() + ".");
        LOGGER.info("---------");

        // map object to JSON for Camunda transfer
        try {
            jsonString = objectMapper.writeValueAsString(givenProcessStart);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // create message event to initiate BPMN process
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("taskId", givenProcessStart.getTaskId());
        runtimeService.startProcessInstanceByMessage("ProcessStart", variables);

        latestProcessStart = givenProcessStart;

        // return object as success message
        return Response.ok(jsonString, MediaType.APPLICATION_JSON).build();
    }

    @RequestMapping(value = "/start", method = RequestMethod.POST, consumes = "text/plain", produces = "text/plain")
    @ResponseBody
    public Response postStatus(@RequestBody String textString) {
        LOGGER.info("Received text message: " + textString);

        // replace by given
        ProcessStart givenProcessStart = new ProcessStart(textString);

        List<MessageCorrelationResult> results = runtimeService.createMessageCorrelation("ProcessStart")
                .setVariable("ProcessStart", givenProcessStart).correlateAllWithResult();
        if (results.size() > 0)
            LOGGER.info("Triggered message event ProcessStart " + results.size() + " times.");

        latestProcessStart = givenProcessStart;

        return Response.ok(textString, MediaType.TEXT_PLAIN).build();
    }


    // Return last status
    @RequestMapping(value = "/start", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Response getLastStatus() {
        String json = "empty";

        if (latestProcessStart != null) {
            try {
                json = objectMapper.writeValueAsString(this.latestProcessStart);
                return Response.ok(json, MediaType.APPLICATION_JSON).build();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }
}

package org.example.mgmt.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.MessageCorrelationResult;
import org.example.datatypes.ProcessLog;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@RestController
public class RestLogService {
    // Variables
    private static List<ProcessLog> processLogList = new ArrayList<ProcessLog>();
    private ObjectMapper objectMapper = new ObjectMapper();
    private final static Logger LOGGER = Logger.getLogger("REST-LOG");
    private ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    private RuntimeService runtimeService = processEngine.getRuntimeService();
    int debug = 0;

    // Constructor
    public RestLogService() {
    }

    // Catch and consume posted log
    @RequestMapping(value = "/log", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public Response postStatus(@RequestBody ProcessLog givenProcessLog) {
        String jsonString = "empty";

        // Mandatory properties available?
        if (givenProcessLog.getTaskId() == null) {
            LOGGER.info("Mandatory fields missing.");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        // Mandatory properties filled?
        if (givenProcessLog.getTaskId().isEmpty()) {
            LOGGER.info("Mandatory fields missing.");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (debug > 0)
            LOGGER.info("Received status " + givenProcessLog.getTaskStatus() + " of taskId "
                    + givenProcessLog.getTaskId());

        // map object to JSON for Camunda transfer
        try {
            jsonString = objectMapper.writeValueAsString(givenProcessLog);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // create message event to inform running BPMN activity
        List<MessageCorrelationResult> results = runtimeService.createMessageCorrelation("ProcessLog")
                //.setVariable("ProcessLog", givenProcessStatus).correlateAllWithResult();
                .setVariable("ProcessLog", jsonString).correlateAllWithResult();

        if (debug > 0)
            LOGGER.info("Triggered message event ProcessLog " + results.size() + " times.");

        // return object as success message
        return Response.ok(jsonString, MediaType.APPLICATION_JSON).build();
    }

    // Return last log
    @RequestMapping(value = "/log", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Response getLastLog() {
        String json = "empty";

        if (this.processLogList.isEmpty()) {
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        } else {
            try {
                json = objectMapper.writeValueAsString(processLogList.get(processLogList.size() - 1));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }
}

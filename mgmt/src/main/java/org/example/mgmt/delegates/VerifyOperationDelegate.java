package org.example.mgmt.delegates;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.example.datatypes.ProcessStatus;

import java.util.logging.Logger;

public class VerifyOperationDelegate implements JavaDelegate {

    // Camunda variables
    private ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    private RuntimeService runtimeService = processEngine.getRuntimeService();
    private final static Logger LOGGER = Logger.getLogger("CONTROL-SLURRY-APPLICATION");

    public void execute(DelegateExecution execution) throws Exception {

        // Get process variables
        boolean bTaskFinished = (Boolean) execution.getVariable("bTaskFinished");
        String sProcessStatusJson = execution.getVariable("ProcessStatus").toString();
        LOGGER.info("Received status message from SP: " + sProcessStatusJson);

        // Create object from JSON
        ObjectMapper objectMapper = new ObjectMapper();
        ProcessStatus processStatus = null;
        try {
            processStatus = objectMapper.readValue(sProcessStatusJson, ProcessStatus.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // Do work
        if (processStatus != null) {
            LOGGER.info("Checking slurry application.");
            LOGGER.info("Process status: taskId " + processStatus.getTaskId() + " with status "
                    + processStatus.getTaskStatus() + ".");

            if (processStatus.getTaskStatus() == 4) {
                bTaskFinished = true;
                LOGGER.info("Finished slurry application. Waiting for task log from SP.");
            } else {
                LOGGER.info("Continuing slurry application.");
            }
        } else {
            LOGGER.warning("ProcessStatus-object is null!");
        }

        // Set process variables
        execution.setVariable("bTaskFinished", bTaskFinished);
    }
}

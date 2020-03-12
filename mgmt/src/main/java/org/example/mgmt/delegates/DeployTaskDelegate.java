package org.example.mgmt.delegates;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.logging.Logger;

@Component
public class DeployTaskDelegate implements JavaDelegate {

    @Autowired
    private DiscoveryClient discoveryClient;

    // Setup process variables
    boolean bTaskFinished = false;

    // Camunda variables
    private ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    private RuntimeService runtimeService = processEngine.getRuntimeService();
    private final static Logger LOGGER = Logger.getLogger("DEPLOY-TASK");

    public void execute(DelegateExecution execution) throws Exception {

        // Do work
        LOGGER.info("Deploying task.");

        // Set process variables
        execution.setVariable("bTaskFinished", bTaskFinished);
    }
}

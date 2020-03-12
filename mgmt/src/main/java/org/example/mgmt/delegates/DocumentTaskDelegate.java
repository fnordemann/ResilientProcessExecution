package org.example.mgmt.delegates;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.util.logging.Logger;

public class DocumentTaskDelegate implements JavaDelegate {

    private final static Logger LOGGER = Logger.getLogger("DOCUMENT-TASK");

    public void execute(DelegateExecution execution) throws Exception {
        // Do work
        LOGGER.info("Received log from SP.");
        LOGGER.info("Documenting task.");
        LOGGER.info("Process done.");
    }
}

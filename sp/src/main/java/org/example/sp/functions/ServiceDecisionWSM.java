package org.example.sp.functions;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RuntimeService;
import org.example.eureka.Instance;
import org.example.eureka.Metadata;
import org.springframework.cloud.client.ServiceInstance;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

// Class to decide on one of multiple services
public class ServiceDecisionWSM {

    // Camunda variables
    private ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    private RuntimeService runtimeService = processEngine.getRuntimeService();
    private final static Logger LOGGER = Logger.getLogger("SERVICE-DECISION-WSM");

    public ServiceDecisionWSM() {
    }

    // Selection based on Eureka instances
    public ServiceInstance selectService(List<ServiceInstance> serviceInstanceList, String[] criteria, double[] criteriaWeights) {

        // Instances found?
        if (serviceInstanceList.size() > 0) {

            // Decide on instance
            int services = serviceInstanceList.size();
            double[][] weightedDecisionMatrix = new double[criteria.length + 1][services + 1];
            Map<String, String> metadataMap;

            // Fill matrix
            // Fill weights
            for (int i = 0; i < criteria.length; i++) {
                weightedDecisionMatrix[i][0] = criteriaWeights[i];
            }
            // Fill criteria per service and calculate service rating
            for (int j = 1; j < services + 1; j++) {
                ServiceInstance serviceInstance = serviceInstanceList.get(j - 1);
                metadataMap = serviceInstance.getMetadata();
                double serviceRating = 0.0;
                for (int i = 0; i < criteria.length; i++) {
                    Double dCriteriaValue = Double.parseDouble(metadataMap.get(criteria[i]));
                    if(criteria[i].equals("accuracy")){
                        dCriteriaValue = 1 - dCriteriaValue;
                    }
                    weightedDecisionMatrix[i][j] = dCriteriaValue;
                    serviceRating = serviceRating + weightedDecisionMatrix[i][j] * weightedDecisionMatrix[i][0];
                }
                weightedDecisionMatrix[criteria.length][j] = serviceRating;

            }

            // Print matrix
            LOGGER.info("Decision matrix: ");
            for (int j = 0; j < criteria.length + 1; j++) {
                for (int i = 0; i < services + 1; i++) {
                    System.out.print(weightedDecisionMatrix[j][i] + " ");
                }
            }
            System.out.print("\n");

            // Pick the best rated service
            double currentRating = weightedDecisionMatrix[criteria.length][1];
            int highestRated = 1;
            for (int i = 2; i < services + 1; i++) {
                if (currentRating > weightedDecisionMatrix[criteria.length][i]) {
                    highestRated = i;
                }
            }
            ServiceInstance serviceInstance = null;
            serviceInstance = serviceInstanceList.get(highestRated - 1);

            return serviceInstance;

        } else {
            LOGGER.info("No services instances provided!\n");
            serviceInstanceList.clear();
            return null;
        }
    }

    // Selection based on Eureka-REST call results
    public Instance selectServiceRestBased(List<Instance> serviceInstanceList, String[] criteria, double[] criteriaWeights) {

        // Instances found?
        if (serviceInstanceList.size() > 0) {

            // Decide on instance
            int services = serviceInstanceList.size();
            double[][] weightedDecisionMatrix = new double[criteria.length + 1][services + 1];
            Map<String, String> metadataMap = null;

            // Fill matrix
            // Fill weights
            for (int i = 0; i < criteria.length; i++) {
                weightedDecisionMatrix[i][0] = criteriaWeights[i];
            }
            // Fill criteria per service and calculate service rating
            for (int j = 1; j < services + 1; j++) {
                Instance instance = serviceInstanceList.get(j - 1);
                // Helper to get metadata from POJO
                Metadata metadata = instance.getMetadata();
                metadataMap = new HashMap<String, String>();
                metadataMap.put("accuracy", metadata.getAccuracy());
                metadataMap.put("cost", metadata.getCost());
                metadataMap.put("time", metadata.getTime());
                double serviceRating = 0.0;
                for (int i = 0; i < criteria.length; i++) {
                    Double dCriteriaValue = Double.parseDouble(metadataMap.get(criteria[i]));
                    if(criteria[i].equals("accuracy")){
                        dCriteriaValue = 1 - dCriteriaValue;
                    }
                    weightedDecisionMatrix[i][j] = dCriteriaValue;
                    serviceRating = serviceRating + weightedDecisionMatrix[i][j] * weightedDecisionMatrix[i][0];
                }
                metadataMap.clear();
                weightedDecisionMatrix[criteria.length][j] = serviceRating;
            }

            // Print matrix
            LOGGER.info("Decision matrix: ");
            LOGGER.info("Weight | Service0 Service1 Service2... (last line: summarization of criteria values for a service )");
            DecimalFormatSymbols customFormat = new DecimalFormatSymbols(Locale.getDefault());
            customFormat.setDecimalSeparator('.');
            customFormat.setGroupingSeparator(',');
            DecimalFormat df = new DecimalFormat("#0.0", customFormat);
            for (int j = 0; j < criteria.length + 1; j++) {
                for (int i = 0; i < services + 1; i++) {
                    System.out.print(df.format(weightedDecisionMatrix[j][i]) + " ");
                    //System.out.print(weightedDecisionMatrix[j][i] + " ");
                }
                System.out.print("\n");
                try {
                    if (criteria.length > 1 && j == criteria.length - 1) {
                        System.out.println("***");
                    }
                } catch (Exception e) {
                    // nothing to do
                }
            }


            // Pick the best rated service
            double currentRating = weightedDecisionMatrix[criteria.length][1];
            int highestRated = 1;
            for (int i = 2; i < services + 1; i++) {
                if (currentRating > weightedDecisionMatrix[criteria.length][i]) {
                    highestRated = i;
                }
            }
            Instance instance = null;
            instance = serviceInstanceList.get(highestRated - 1);

            return instance;

        } else {
            LOGGER.info("No services instances provided!");
            serviceInstanceList.clear();
            return null;
        }
    }
}

package org.example.sp.functions;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RuntimeService;
import org.example.eureka.Instance;
import org.example.eureka.Metadata;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.*;
import org.jgrapht.graph.EdgeReversedGraph;
import org.springframework.cloud.client.ServiceInstance;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.logging.Logger;

import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
//import org.jgrapht.io.ExportException;

// Class to decide on one of multiple services
public class ServiceDecision {

    // Camunda variables
    private ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    private RuntimeService runtimeService = processEngine.getRuntimeService();
    private final static Logger LOGGER = Logger.getLogger("SERVICE-DECISION");

    public ServiceDecision() {
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
                    weightedDecisionMatrix[i][j] = Double.parseDouble(metadataMap.get(criteria[i]));
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
                if (currentRating < weightedDecisionMatrix[criteria.length][i]) {
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
                    weightedDecisionMatrix[i][j] = Double.parseDouble(metadataMap.get(criteria[i]));
                    serviceRating = serviceRating + weightedDecisionMatrix[i][j] * weightedDecisionMatrix[i][0];
                }
                metadataMap.clear();
                weightedDecisionMatrix[criteria.length][j] = serviceRating;
            }

            // Print matrix
            LOGGER.info("Decision matrix: ");
            LOGGER.info("Weight Service0 Service1 Service2... (last line: summarization of criteria values for a service )");
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
            }


            // Pick the best rated service
            double currentRating = weightedDecisionMatrix[criteria.length][1];
            int highestRated = 1;
            for (int i = 2; i < services + 1; i++) {
                if (currentRating < weightedDecisionMatrix[criteria.length][i]) {
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

    public void testGraph(){
        //final Graph<String, DefaultWeightedEdge> directedGraph = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        Graph<String, DefaultWeightedEdge> directedGraph = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);

        long startTime;
        long elapsedTime;

        // graph variables
        String sP = "S";
        String sP_ = "S'";
        String PF = "PF";
        String PFL = "PF(L)";
        String noPF = "noPF";
        String G1 = "G1";
        String LAB = "LAB";
        String NIRS = "NIRS";
        String REF = "REF";
        String REFL = "REF(L)";
        String G2 = "G2";
        String GPS = "GPS";
        String CELL = "CELL";
        String LOC = "LOC";

        // add vertices
        directedGraph.addVertex(sP);
        directedGraph.addVertex(sP_);
        directedGraph.addVertex(PF);
        directedGraph.addVertex(PFL);
        directedGraph.addVertex(noPF);
        directedGraph.addVertex(G1);
        directedGraph.addVertex(LAB);
        directedGraph.addVertex(NIRS);
        directedGraph.addVertex(REF);
        directedGraph.addVertex(REFL);
        directedGraph.addVertex(G2);
        directedGraph.addVertex(GPS);
        directedGraph.addVertex(CELL);
        directedGraph.addVertex(LOC);


        // create graph and add joint weights from design time analysis
        // S to G1
        DefaultWeightedEdge eSPF = directedGraph.addEdge(sP, PF);
        double dWeight = 0;
        directedGraph.setEdgeWeight(eSPF, dWeight);
        DefaultWeightedEdge ePF = directedGraph.addEdge(PF, G1);
        dWeight = 0.45;
        directedGraph.setEdgeWeight(ePF, dWeight);
        DefaultWeightedEdge eSPFL = directedGraph.addEdge(sP, PFL);
        dWeight = 0;
        directedGraph.setEdgeWeight(eSPFL, dWeight);
        DefaultWeightedEdge ePFL = directedGraph.addEdge(PFL, G1);
        dWeight = 0.29;
        directedGraph.setEdgeWeight(ePFL, dWeight);
        DefaultWeightedEdge eSnoPF = directedGraph.addEdge(sP, noPF);
        dWeight = 0;
        directedGraph.setEdgeWeight(eSnoPF, dWeight);
        DefaultWeightedEdge eNoPF = directedGraph.addEdge(noPF, sP_);
        dWeight = 0.43;
        directedGraph.setEdgeWeight(eNoPF, dWeight);

        // G1 to G2
        DefaultWeightedEdge eG1LAB = directedGraph.addEdge(G1, LAB);
        dWeight = 0;
        directedGraph.setEdgeWeight(eG1LAB, dWeight);
        DefaultWeightedEdge eLAB = directedGraph.addEdge(LAB, G2);
        dWeight = 0.5;
        directedGraph.setEdgeWeight(eLAB, dWeight);
        DefaultWeightedEdge eG1NIRS = directedGraph.addEdge(G1, NIRS);
        dWeight = 0;
        directedGraph.setEdgeWeight(eG1NIRS, dWeight);
        DefaultWeightedEdge eNIRS = directedGraph.addEdge(NIRS, G2);
        dWeight = 0.29;
        directedGraph.setEdgeWeight(eNIRS, dWeight);
        DefaultWeightedEdge eG1REF = directedGraph.addEdge(G1, REF);
        dWeight = 0;
        directedGraph.setEdgeWeight(eG1REF, dWeight);
        DefaultWeightedEdge eREF = directedGraph.addEdge(REF, G2);
        dWeight = 0.35;
        directedGraph.setEdgeWeight(eREF, dWeight);
        DefaultWeightedEdge eG1REFL = directedGraph.addEdge(G1, REFL);
        dWeight = 0;
        directedGraph.setEdgeWeight(eG1REFL, dWeight);
        DefaultWeightedEdge eREFL = directedGraph.addEdge(REFL, G2);
        dWeight = 0.4;
        directedGraph.setEdgeWeight(eREFL, dWeight);

        // G2 to S'
        DefaultWeightedEdge eG2GPS = directedGraph.addEdge(G2, GPS);
        dWeight = 0;
        directedGraph.setEdgeWeight(eG2GPS, dWeight);
        DefaultWeightedEdge eGPS = directedGraph.addEdge(GPS, sP_);
        dWeight = 0.28;
        directedGraph.setEdgeWeight(eGPS, dWeight);
        DefaultWeightedEdge eG2CELL = directedGraph.addEdge(G2, CELL);
        dWeight = 0;
        directedGraph.setEdgeWeight(eG2CELL, dWeight);
        DefaultWeightedEdge eCELL = directedGraph.addEdge(CELL, sP_);
        dWeight = 0.14;
        directedGraph.setEdgeWeight(eCELL, dWeight);
        DefaultWeightedEdge eG2LOC = directedGraph.addEdge(G2, LOC);
        dWeight = 0;
        directedGraph.setEdgeWeight(eG2LOC, dWeight);
        DefaultWeightedEdge eLOC = directedGraph.addEdge(LOC, sP_);
        dWeight = 0.23;
        directedGraph.setEdgeWeight(eLOC, dWeight);





        System.out.println("Shortest path from i to c:");
        System.out.println("DijkstraShortestPath:");
        DijkstraShortestPath<String, DefaultWeightedEdge> dijkstraAlg =
                new DijkstraShortestPath<>(directedGraph);
        startTime = System.nanoTime();
        //ShortestPathAlgorithm.SingleSourcePaths<String, DefaultWeightedEdge> iPaths = dijkstraAlg.getPaths(sP);
        GraphPath graphPath = dijkstraAlg.getPath(sP, sP_);

        System.out.println("ShortestPath String: " + graphPath.toString());
        System.out.println("ShortestPath Weight: " + graphPath.getWeight());
        System.out.println("ShortestPath EdgeList: " + graphPath.getEdgeList().toString());
        System.out.println("ShortestPath VertexList: " + graphPath.getVertexList().toString());
        System.out.println("ShortestPath Graph: " + graphPath.getGraph().toString());

        elapsedTime = System.nanoTime() - startTime;
        System.out.println("Total execution time for DijkstraShortestPath: "
                + elapsedTime/1000 + " \u00B5s / " + elapsedTime/1000000 + " ms");
        startTime = System.nanoTime();
        //System.out.println(iPaths.getPath(sP_));
        //System.out.println("Weight: " + iPaths.getPath(sP_).getWeight() + "\n");



        YenShortestPathIterator yenShortestPathIterator = new YenShortestPathIterator(directedGraph, sP, sP_);
        System.out.println("YenShortestPathIterator: " + yenShortestPathIterator.toString());
        System.out.println("YenShortestPathIterator: " + yenShortestPathIterator.next().toString());
        System.out.println("YenShortestPathIterator: " + yenShortestPathIterator.next().toString());
        System.out.println("YenShortestPathIterator: " + yenShortestPathIterator.next().toString());

        YenKShortestPath<String, DefaultWeightedEdge> yenKShortestPath = new YenKShortestPath<>(directedGraph);
        List<GraphPath<String, DefaultWeightedEdge>> yenKGraphPaths = yenKShortestPath.getPaths(sP, sP_, 99);
        System.out.println("yenKShortestPath-size: " + yenKGraphPaths.size());
        System.out.println("yenKShortestPath-0: " + yenKGraphPaths.get(0));
        System.out.println("yenKShortestPath-0-List: " + yenKGraphPaths.get(0).getVertexList().toString());
        System.out.println("yenKShortestPath-1: " + yenKGraphPaths.get(1));
        System.out.println("yenKShortestPath-1-List: " + yenKGraphPaths.get(1).getVertexList().toString());


        List<String> vertexList = new ArrayList<String>();
        vertexList.add(sP);
        vertexList.add(sP_);
        vertexList.add(G1);
        vertexList.add(G2);
        vertexList.add(PF);
        vertexList.add(PFL);
        vertexList.add(noPF);
        vertexList.add(LAB);
        vertexList.add(NIRS);
        vertexList.add(REF);
        vertexList.add(REFL);
        vertexList.add(GPS);
        vertexList.add(CELL);
        vertexList.add(LOC);



        Graph<String, DefaultWeightedEdge> revGraph = new EdgeReversedGraph<>(directedGraph);
        Graph<String, DefaultWeightedEdge> directedCostGraph = new EdgeReversedGraph<>(revGraph);
        //To reduce the memory complexity
        revGraph = null;



        directedCostGraph.setEdgeWeight(PF, G1, 0.8);
        directedCostGraph.setEdgeWeight(PFL, G1, 0.1);
        directedCostGraph.setEdgeWeight(noPF, sP_, 0.1);

        directedCostGraph.setEdgeWeight(LAB, G2, 0.9);
        directedCostGraph.setEdgeWeight(NIRS, G2, 0.4);
        directedCostGraph.setEdgeWeight(REF, G2, 0.1);
        directedCostGraph.setEdgeWeight(REFL, G2, 0.1);

        directedCostGraph.setEdgeWeight(GPS, sP_, 0.1);
        directedCostGraph.setEdgeWeight(CELL, sP_, 0.3);
        directedCostGraph.setEdgeWeight(LOC, sP_, 0.7);


        List<String> pathVertexList = graphPath.getVertexList();


        for (int i = 0; i < vertexList.size(); i++) {
            for (int j = 0; j < pathVertexList.size(); j++) {
                if(vertexList.get(i).equals(pathVertexList.get(j))) {
                    try {
                        System.out.println("Removing " + vertexList.get(i) + " from vertexList. vertexList: " + vertexList.toString());
                        vertexList.remove(i);
                    } catch (Exception e) {
                        System.out.println("Exception while removing from list");
                    }
                }
            }
        }



        for (int i = 0; i < vertexList.size(); i++) {
            try {
                System.out.println("Removing " + vertexList.get(i) + " from graph.");
                directedCostGraph.removeVertex(vertexList.get(i));
            } catch (Exception e){
                System.out.println("Exception while removing from graph.");
            }
        }





        System.out.println("BellmanFordShortestPath:");
        BellmanFordShortestPath<String, DefaultWeightedEdge> bellmanFordAlg =
                new BellmanFordShortestPath<>(directedCostGraph);
        startTime = System.nanoTime();
        //ShortestPathAlgorithm.SingleSourcePaths<String, DefaultWeightedEdge> bPaths = bellmanFordAlg.getPaths(sP);
        elapsedTime = System.nanoTime() - startTime;
        graphPath = bellmanFordAlg.getPath(sP, sP_);
        System.out.println("ShortestPath: " + graphPath.toString());
        System.out.println("ShortestPath: " + graphPath.getWeight());
        elapsedTime = System.nanoTime() - startTime;
        System.out.println("Total execution time for BellmanFordShortestPath: "
                + elapsedTime/1000 + " \u00B5s / " + elapsedTime/1000000 + " ms");
        //System.out.println(bPaths.getPath(sP_));
        //System.out.println("Weight: " + bPaths.getPath(sP_).getWeight() + "\n");


        System.out.println("MultiCriteriaGraph: " + directedGraph.toString());

    }
}

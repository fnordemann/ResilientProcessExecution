package org.example.sp.functions;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RuntimeService;
import org.example.eureka.Instance;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.shortestpath.YenKShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

public class ServiceDecisionGraph {

    // Camunda variables
    private ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    private RuntimeService runtimeService = processEngine.getRuntimeService();
    private final static Logger LOGGER = Logger.getLogger("SERVICE-DECISION-GRAPH-BASED");

    // Graph variables
    private Graph<String, DefaultWeightedEdge> directedGraph = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);

    final String sP = "S";
    final String sP_ = "S'";
    final String PF = "PF";
    final String PFL = "PF(L)";
    final String noPF = "noPF";
    final String G1 = "G1";
    final String LAB = "LAB";
    final String NIRS = "NIRS";
    final String REF = "REF";
    final String REFL = "REF(L)";
    final String G2 = "G2";
    final String GPS = "GPS";
    final String CELL = "CELL";
    final String LOC = "LOC";
    List<String> vertexList = new ArrayList<String>();
    List<String> preFarmingList = new ArrayList<String>();
    List<String> analysisList = new ArrayList<String>();
    List<String> corrGpsList = new ArrayList<String>();

    final double dAccuracyWeight = 0.5;
    final double dCostWeight = 0.3;
    final double dTimeWeight = 0.2;

    final double dAccuracyMin = 0.3;

    double dCostPF = 0.8;
    double dCostPFL = 0.4;
    double dCostNoPF = 0.1;
    double dCostLAB = 0.9;
    double dCostNIRS = 0.4;
    double dCostREF = 0.1;
    double dCostREFL = 0.1;
    double dCostGPS = 0.1;
    double dCostCELL = 0.3;
    double dCostLOC = 0.7;

    long startTime;
    long elapsedTime;


    public ServiceDecisionGraph() {
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

        // add joint weights from design time analysis
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
        /* Not setting this edge since accuracy of noPF is smaller than required (0.2 < 0.3)
        DefaultWeightedEdge eNoPF = directedGraph.addEdge(noPF, sP_);
        dWeight = 0.43;
        directedGraph.setEdgeWeight(eNoPF, dWeight);
         */

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

        // create vertex lists
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

        preFarmingList.add(PF);
        preFarmingList.add(PFL);

        analysisList.add(LAB);
        analysisList.add(NIRS);
        analysisList.add(REF);
        analysisList.add(REFL);

        corrGpsList.add(CELL);
        corrGpsList.add(LOC);
    }

    public void updateGraph(List<Instance> instanceList) {
        // Preparing list for unavailable vertices
        List<String> unavailableVertexList = new ArrayList<String>();
        if (instanceList.size() > 0) {
            //String sProcessSegment = instanceList.get(1).getInstanceId();
            String sProcessSegment = instanceList.get(0).getApp();

            if (sProcessSegment.equals("PRECISION-FARMING-SERVICE")) {
                unavailableVertexList = preFarmingList;
                System.out.println("Updating precision farming segment in graph...");
            } else if (sProcessSegment.equals("INGREDIENTS-SERVICE")) {
                unavailableVertexList = analysisList;
                System.out.println("Updating slurry analysis segment in graph...");
            } else if (sProcessSegment.equals("GPS-SERVICE")) {
                System.out.println("Updating position correction segment in graph...");
                unavailableVertexList = corrGpsList;
            }
        }

        // Updating the graph weights
        for (int i = 0; i < instanceList.size(); i++) {
            String sId = instanceList.get(i).getMetadata().getType();
            System.out.print("Found vertex id: " + sId);
            // Calc joint weight for graph
            double dAccuracy = Double.parseDouble(instanceList.get(i).getMetadata().getAccuracy());
            double dCost = Double.parseDouble(instanceList.get(i).getMetadata().getCost());
            double dTime = Double.parseDouble(instanceList.get(i).getMetadata().getTime());
            double dWeight = (1 - dAccuracy) * dAccuracyWeight + dCost * dCostWeight + dTime * dTimeWeight;
            dWeight = Double.parseDouble(String.format(Locale.ENGLISH, "%1.2f", dWeight));

            // Update edge weight or remove vertex since i) it is not available or ii) min accuracy is not provided
            if(sId != null) {
                switch (sId) {
                    case PF:
                        if(dAccuracy >= dAccuracyMin) {
                            directedGraph.setEdgeWeight(PF, G1, dWeight);
                            unavailableVertexList.remove(PF);
                            dCostPF = dCost;
                            System.out.println("\tUpdated edge " + directedGraph.getEdge(PF, G1) + " with weight " + directedGraph.getEdgeWeight(directedGraph.getEdge(PF, G1)));
                        }
                        break;
                    case PFL:
                        if(dAccuracy >= dAccuracyMin) {
                            directedGraph.setEdgeWeight(PFL, G1, dWeight);
                            unavailableVertexList.remove(PFL);
                            dCostPFL = dCost;
                            System.out.println("\tUpdated edge " + directedGraph.getEdge(PFL, G1) + " with weight " + directedGraph.getEdgeWeight(directedGraph.getEdge(PFL, G1)));
                        }
                        break;
                    case noPF:
                        if(dAccuracy >= dAccuracyMin) {
                            directedGraph.setEdgeWeight(noPF, sP_, dWeight);
                            dCostNoPF = dCost;
                            System.out.println("\tUpdated edge " + directedGraph.getEdge(noPF, sP_) + " with weight " + directedGraph.getEdgeWeight(directedGraph.getEdge(noPF, sP_)));
                        }
                        break;
                    case LAB:
                        if(dAccuracy >= dAccuracyMin) {
                            directedGraph.setEdgeWeight(LAB, G2, dWeight);
                            unavailableVertexList.remove(LAB);
                            dCostLAB = dCost;
                            System.out.println("\tUpdated edge " + directedGraph.getEdge(LAB, G2) + " with weight " + directedGraph.getEdgeWeight(directedGraph.getEdge(LAB, G2)));
                        }
                        break;
                    case NIRS:
                        if(dAccuracy >= dAccuracyMin) {
                            directedGraph.setEdgeWeight(NIRS, G2, dWeight);
                            unavailableVertexList.remove(NIRS);
                            dCostNIRS = dCost;
                            System.out.println("\tUpdated edge " + directedGraph.getEdge(NIRS, G2) + " with weight " + directedGraph.getEdgeWeight(directedGraph.getEdge(NIRS, G2)));
                        }
                        break;
                    case REF:
                        if(dAccuracy >= dAccuracyMin) {
                            directedGraph.setEdgeWeight(REF, G2, dWeight);
                            unavailableVertexList.remove(REF);
                            dCostREF = dCost;
                            System.out.println("\tUpdated edge " + directedGraph.getEdge(REF, G2) + " with weight " + directedGraph.getEdgeWeight(directedGraph.getEdge(REF, G2)));
                        }
                        break;
                    case REFL:
                        if(dAccuracy >= dAccuracyMin) {
                            directedGraph.setEdgeWeight(REFL, G2, dWeight);
                            unavailableVertexList.remove(REFL);
                            dCostREFL = dCost;
                            System.out.println("\tUpdated edge " + directedGraph.getEdge(REFL, G2) + " with weight " + directedGraph.getEdgeWeight(directedGraph.getEdge(REFL, G2)));
                        }
                        break;
                    case GPS:
                        if(dAccuracy >= dAccuracyMin) {
                            directedGraph.setEdgeWeight(GPS, sP_, dWeight);
                            dCostGPS = dCost;
                            System.out.println("\tUpdated edge " + directedGraph.getEdge(GPS, sP_) + " with weight " + directedGraph.getEdgeWeight(directedGraph.getEdge(GPS, sP_)));
                        }
                        break;
                    case CELL:
                        if(dAccuracy >= dAccuracyMin) {
                            directedGraph.setEdgeWeight(CELL, sP_, dWeight);
                            unavailableVertexList.remove(CELL);
                            dCostCELL = dCost;
                            System.out.println("\tUpdated edge " + directedGraph.getEdge(CELL, sP_) + " with weight " + directedGraph.getEdgeWeight(directedGraph.getEdge(CELL, sP_)));
                        }
                        break;
                    case LOC:
                        if(dAccuracy >= dAccuracyMin) {
                            directedGraph.setEdgeWeight(LOC, sP_, dWeight);
                            unavailableVertexList.remove(LOC);
                            dCostLOC = dCost;
                            System.out.println("\tUpdated edge " + directedGraph.getEdge(LOC, sP_) + " with weight " + directedGraph.getEdgeWeight(directedGraph.getEdge(LOC, sP_)));
                        }
                        break;
                    default:
                        System.out.println("\tCould not find a vertex for id " + sId);
                        break;
                }
            }
        }

        // Remove unavailable services from graph
        removeFromGraph(unavailableVertexList);
    }

    public void removeFromGraph(List<String> removalList) {
        for (int i = 0; i < removalList.size(); i++) {
            directedGraph.removeVertex(removalList.get(i));
            System.out.println("Removed vertex " + removalList.get(i) + " from graph: unavailable or failing minimum accuracy.");
        }
        //System.out.println("New graph after removal method: " + directedGraph.toString());
    }

    public double calcTotalCost(List<String> pathVertexList) {
        double dTotalCost = 0.0;

        for (int i = 0; i < pathVertexList.size(); i++) {
            switch (pathVertexList.get(i)) {
                case PF:
                    dTotalCost += dCostPF;
                    break;
                case PFL:
                    dTotalCost += dCostPFL;
                    break;
                case noPF:
                    dTotalCost += dCostNoPF;
                    break;
                case LAB:
                    dTotalCost += dCostLAB;
                    break;
                case NIRS:
                    dTotalCost += dCostNIRS;
                    break;
                case REF:
                    dTotalCost += dCostREF;
                    break;
                case REFL:
                    dTotalCost += dCostREFL;
                    break;
                case GPS:
                    dTotalCost += dCostGPS;
                    break;
                case CELL:
                    dTotalCost += dCostCELL;
                    break;
                case LOC:
                    dTotalCost += dCostLOC;
                    break;
                case G1:
                    break;
                case G2:
                    break;
                case sP:
                    break;
                case sP_:
                    break;
                default:
                    System.out.println("Could not find a cost for vertex " + pathVertexList.get(i));
                    break;
            }
        }
        System.out.println("Path with vertex list: " + pathVertexList);
        System.out.println("Total cost for path: " + dTotalCost);

        return dTotalCost;
    }

    public List<GraphPath<String, DefaultWeightedEdge>> kShortestPaths(String sStartVertex, String sEndVertex) {
        /* Yen iterator
        YenShortestPathIterator yenShortestPathIterator = new YenShortestPathIterator(directedGraph, sP, sP_);
        System.out.println("YenShortestPathIterator: " + yenShortestPathIterator.toString());
        System.out.println("YenShortestPathIterator: " + yenShortestPathIterator.next().toString());
        System.out.println("YenShortestPathIterator: " + yenShortestPathIterator.next().toString());
        System.out.println("YenShortestPathIterator: " + yenShortestPathIterator.next().toString());
         */

        // Calc shortest paths
        YenKShortestPath<String, DefaultWeightedEdge> yenKShortestPath = new YenKShortestPath<>(directedGraph);
        List<GraphPath<String, DefaultWeightedEdge>> yenKGraphPaths = yenKShortestPath.getPaths(sStartVertex, sEndVertex, 99);

        System.out.println("--- k-shortest-paths analysis ---");
        System.out.println("Number of Shortest Paths (" + sStartVertex + " to " + sEndVertex + ") found: " + yenKGraphPaths.size());
        if (yenKGraphPaths.size() > 1) {
            System.out.println("\tShowing the first two paths...");
            System.out.println("\tyenKShortestPath-#0: " + yenKGraphPaths.get(0));
            System.out.println("\tyenKShortestPath-#0-VertexList: " + yenKGraphPaths.get(0).getVertexList().toString());
            System.out.println("\tyenKShortestPath-#0-Weight: " + yenKGraphPaths.get(0).getWeight());
            System.out.println("\tyenKShortestPath-#1: " + yenKGraphPaths.get(1));
            System.out.println("\tyenKShortestPath-#1-VertexList: " + yenKGraphPaths.get(1).getVertexList().toString());
            System.out.println("\tyenKShortestPath-#1-Weight: " + yenKGraphPaths.get(1).getWeight());
        } else if (yenKGraphPaths.size() > 0) {
            System.out.println("\tyenKShortestPath-#0: " + yenKGraphPaths.get(0));
            System.out.println("\tyenKShortestPath-#0-VertexList: " + yenKGraphPaths.get(0).getVertexList().toString());
            System.out.println("\tyenKShortestPath-#0-Weight: " + yenKGraphPaths.get(0).getWeight());
        } else {
            System.out.println("\tNo paths found! Process unable to proceed!");
        }

        return yenKGraphPaths;
    }

    public GraphPath<String, DefaultWeightedEdge> shortestPath(String sStartVertex, String sEndVertex) {
        // Calc shortest path
        DijkstraShortestPath<String, DefaultWeightedEdge> dijkstraAlg = new DijkstraShortestPath<>(directedGraph);
        startTime = System.nanoTime();
        GraphPath graphPath = dijkstraAlg.getPath(sStartVertex, sEndVertex);

        System.out.println("ShortestPath String: " + graphPath.toString());
        System.out.println("ShortestPath Weight: " + graphPath.getWeight());
        System.out.println("ShortestPath EdgeList: " + graphPath.getEdgeList().toString());
        System.out.println("ShortestPath VertexList: " + graphPath.getVertexList().toString());
        System.out.println("ShortestPath Graph: " + graphPath.getGraph().toString());

        elapsedTime = System.nanoTime() - startTime;
        System.out.println("Total execution time for DijkstraShortestPath: "
                + elapsedTime / 1000 + " \u00B5s / " + elapsedTime / 1000000 + " ms");

        if (graphPath.getVertexList().size() < 1) {
            System.out.println("No paths found! Process unable to proceed!");
        }

        return graphPath;
    }

    public Instance selectServiceGraphBased(List<Instance> instanceList, String sStartVertex, String sEndVertex, double dCostLimit, String sSlurrySegment){
        String sChosenId = null;
        Instance serviceInstance = null;

        // Calc paths and check for total cost limit
        List<GraphPath<String, DefaultWeightedEdge>> graphPathsList = this.kShortestPaths(sStartVertex, sEndVertex);
        double dTotalCost = dCostLimit;
        int iChosenPath = -1;
        for(int i = 0; i < graphPathsList.size(); i++){
            dTotalCost = this.calcTotalCost(graphPathsList.get(i).getVertexList());
            if(dTotalCost < dCostLimit){
                System.out.println("\tPath selected: #" + i);
                System.out.println("\tPath: " + graphPathsList.get(i).toString());
                System.out.println("\tPath vertex list: " + graphPathsList.get(i).getVertexList());
                iChosenPath = i;
                break;
            }
        }

        // Find instance for chosen id
        if(iChosenPath > -1) {
            List<String> pathVertexList = graphPathsList.get(iChosenPath).getVertexList();
            for(int i = 0; i < pathVertexList.size(); i++){
                String sPathVertex = pathVertexList.get(i);
                switch(sSlurrySegment){
                    case "precision-farming":
                        if(sPathVertex.equals("PF")) {
                            sChosenId = "PF";
                        } else if(sPathVertex.equals("PF(L)")){
                            sChosenId = "PF(L)";
                        }
                        else if(sPathVertex.equals("noPF")){
                            sChosenId = "noPF";
                        }
                        break;
                    case "slurry-analysis":
                        if(sPathVertex.equals("LAB")) {
                            sChosenId = "LAB";
                        } else if(sPathVertex.equals("NIRS")){
                            sChosenId = "NIRS";
                        }
                        else if(sPathVertex.equals("REF")){
                            sChosenId = "REF";
                        }
                        else if(sPathVertex.equals("REF(L)")){
                            sChosenId = "REF(L)";
                        }
                        break;
                    case "position-correction":
                        if(sPathVertex.equals("GPS")) {
                            sChosenId = "GPS";
                        } else if(sPathVertex.equals("CELL")){
                            sChosenId = "CELL";
                        }
                        else if(sPathVertex.equals("LOC")){
                            sChosenId = "LOC";
                        }
                        break;
                    default:
                        System.out.println("Missing process segment.");
                        break;
                }
            }

            // Get instance for id
            for(int i = 0; i < instanceList.size(); i++){
                if(instanceList.get(i).getMetadata().getType().equals(sChosenId)) {
                    serviceInstance = instanceList.get(i);
                    break;
                }
            }
        } else{
            System.out.println("Did not find a path with a total cost < " + dCostLimit);
            System.out.println("Process can not be continued!");
        }

        return serviceInstance;
    }

    public void printGraph() {
        // Print graph with weights
        System.out.println("Current graph: " + directedGraph.toString());
        System.out.println("--- Precising Farming Segment ---");
        if (directedGraph.getEdge(PF, G1) != null)
            System.out.println("\tEdge " + directedGraph.getEdge(PF, G1).toString() + " with weight " + directedGraph.getEdgeWeight(directedGraph.getEdge(PF, G1)));
        if (directedGraph.getEdge(PFL, G1) != null)
            System.out.println("\tEdge " + directedGraph.getEdge(PFL, G1).toString() + " with weight " + directedGraph.getEdgeWeight(directedGraph.getEdge(PFL, G1)));
        if (directedGraph.getEdge(noPF, sP_) != null)
            System.out.println("\tEdge " + directedGraph.getEdge(noPF, sP_).toString() + " with weight " + directedGraph.getEdgeWeight(directedGraph.getEdge(noPF, sP_)));
        System.out.println("--- Slurry Analysis Segment ---");
        if (directedGraph.getEdge(LAB, G2) != null)
            System.out.println("\tEdge " + directedGraph.getEdge(LAB, G2).toString() + " with weight " + directedGraph.getEdgeWeight(directedGraph.getEdge(LAB, G2)));
        if (directedGraph.getEdge(NIRS, G2) != null)
            System.out.println("\tEdge " + directedGraph.getEdge(NIRS, G2).toString() + " with weight " + directedGraph.getEdgeWeight(directedGraph.getEdge(NIRS, G2)));
        if (directedGraph.getEdge(REF, G2) != null)
            System.out.println("\tEdge " + directedGraph.getEdge(REF, G2).toString() + " with weight " + directedGraph.getEdgeWeight(directedGraph.getEdge(REF, G2)));
        if (directedGraph.getEdge(REFL, G2) != null)
            System.out.println("\tEdge " + directedGraph.getEdge(REFL, G2).toString() + " with weight " + directedGraph.getEdgeWeight(directedGraph.getEdge(REFL, G2)));
        System.out.println("--- Position Correction Segment ---");
        if (directedGraph.getEdge(GPS, sP_) != null)
            System.out.println("\tEdge " + directedGraph.getEdge(GPS, sP_).toString() + " with weight " + directedGraph.getEdgeWeight(directedGraph.getEdge(GPS, sP_)));
        if (directedGraph.getEdge(CELL, sP_) != null)
            System.out.println("\tEdge " + directedGraph.getEdge(CELL, sP_).toString() + " with weight " + directedGraph.getEdgeWeight(directedGraph.getEdge(CELL, sP_)));
        if (directedGraph.getEdge(LOC, sP_) != null)
            System.out.println("\tEdge " + directedGraph.getEdge(LOC, sP_).toString() + " with weight " + directedGraph.getEdgeWeight(directedGraph.getEdge(LOC, sP_)));
    }


    public void testGraph() {

        /*
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
                + elapsedTime / 1000 + " \u00B5s / " + elapsedTime / 1000000 + " ms");
        //System.out.println(bPaths.getPath(sP_));
        //System.out.println("Weight: " + bPaths.getPath(sP_).getWeight() + "\n");


        System.out.println("MultiCriteriaGraph: " + directedGraph.toString());

         */

    }

    public void removeVerticesFromGraph() {
        /*
        List<String> pathVertexList = graphPath.getVertexList();


        // Remove vertices from list
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

        // Remove vertices from graph
        for (int i = 0; i < vertexList.size(); i++) {
            try {
                System.out.println("Removing " + vertexList.get(i) + " from graph.");
                directedCostGraph.removeVertex(vertexList.get(i));
            } catch (Exception e){
                System.out.println("Exception while removing from graph.");
            }
        }

         */
    }

}

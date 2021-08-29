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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

public class ServiceDecisionGraph {

    // Camunda variables
    private ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    private RuntimeService runtimeService = processEngine.getRuntimeService();
    private final static Logger LOGGER = Logger.getLogger("SERVICE-DECISION-GRAPH");

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

    /*
    List<String> vertexList = new ArrayList<String>();
    List<String> preFarmingList = new ArrayList<String>();
    List<String> analysisList = new ArrayList<String>();
    List<String> corrGpsList = new ArrayList<String>();
    */

    double dAccPF = 0.9;
    double dAccPFL = 0.7;
    double dAccNoPF = 0.2;
    double dAccLAB = 0.9;
    double dAccNIRS = 0.7;
    double dAccREF = 0.4;
    double dAccREFL = 0.3;
    double dAccGPS = 0.5;
    double dAccCELL = 0.9;
    double dAccLOC = 1.0;

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

    DecimalFormat df = new DecimalFormat("###.##", new DecimalFormatSymbols(Locale.US));


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

        // create vertex lists
        /*
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

         */
    }

    public void updateGraph(String sProcessSegment, List<Instance> instanceList, double dAccuracyWeight, double dCostWeight, double dTimeWeight) {
        this.updateGraph(sProcessSegment, instanceList, 0.0, dAccuracyWeight, dCostWeight, dTimeWeight);
    }

    public void updateGraph(String sProcessSegment, List<Instance> instanceList, double dMinAccuracy, double dAccuracyWeight, double dCostWeight, double dTimeWeight) {
        // Preparing list for unavailable vertices
        List<String> unavailableVertexList = new ArrayList<String>();

            if (sProcessSegment.equals("precision-farming")) {
                unavailableVertexList.add(PF);
                unavailableVertexList.add(PFL);
                System.out.println("Updating precision farming segment in graph...");
            } else if (sProcessSegment.equals("slurry-analysis")) {
                unavailableVertexList.add(LAB);
                unavailableVertexList.add(NIRS);
                unavailableVertexList.add(REF);
                unavailableVertexList.add(REFL);
                System.out.println("Updating slurry analysis segment in graph...");
            } else if (sProcessSegment.equals("position-sensing")) {
                System.out.println("Updating position correction segment in graph...");
                unavailableVertexList.add(CELL);
                unavailableVertexList.add(LOC);
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

            // Update edge weight or remove vertex since i) it is not available or ii) min accuracy is not provided
            if (dAccuracy >= dMinAccuracy) {
                if (sId != null) {
                    switch (sId) {
                        case PF:
                            directedGraph.setEdgeWeight(PF, G1, dWeight);
                            unavailableVertexList.remove(PF);
                            dAccPF = dAccuracy;
                            dCostPF = dCost;
                            System.out.println("\tUpdated edge " + directedGraph.getEdge(PF, G1) + " with weight " + df.format(directedGraph.getEdgeWeight(directedGraph.getEdge(PF, G1))));
                            break;
                        case PFL:
                            directedGraph.setEdgeWeight(PFL, G1, dWeight);
                            unavailableVertexList.remove(PFL);
                            dAccPFL = dAccuracy;
                            dCostPFL = dCost;
                            System.out.println("\tUpdated edge " + directedGraph.getEdge(PFL, G1) + " with weight " + df.format(directedGraph.getEdgeWeight(directedGraph.getEdge(PFL, G1))));
                            break;
                        case noPF:
                            directedGraph.setEdgeWeight(noPF, sP_, dWeight);
                            dAccNoPF = dAccuracy;
                            dCostNoPF = dCost;
                            System.out.println("\tUpdated edge " + directedGraph.getEdge(noPF, sP_) + " with weight " + df.format(directedGraph.getEdgeWeight(directedGraph.getEdge(noPF, sP_))));
                            break;
                        case LAB:
                            directedGraph.setEdgeWeight(LAB, G2, dWeight);
                            unavailableVertexList.remove(LAB);
                            dAccLAB = dAccuracy;
                            dCostLAB = dCost;
                            System.out.println("\tUpdated edge " + directedGraph.getEdge(LAB, G2) + " with weight " + df.format(directedGraph.getEdgeWeight(directedGraph.getEdge(LAB, G2))));
                            break;
                        case NIRS:
                            directedGraph.setEdgeWeight(NIRS, G2, dWeight);
                            unavailableVertexList.remove(NIRS);
                            dAccNIRS = dAccuracy;
                            dCostNIRS = dCost;
                            System.out.println("\tUpdated edge " + directedGraph.getEdge(NIRS, G2) + " with weight " + df.format(directedGraph.getEdgeWeight(directedGraph.getEdge(NIRS, G2))));
                            break;
                        case REF:
                            directedGraph.setEdgeWeight(REF, G2, dWeight);
                            unavailableVertexList.remove(REF);
                            dAccREF = dAccuracy;
                            dCostREF = dCost;
                            System.out.println("\tUpdated edge " + directedGraph.getEdge(REF, G2) + " with weight " + df.format(directedGraph.getEdgeWeight(directedGraph.getEdge(REF, G2))));
                            break;
                        case REFL:
                            directedGraph.setEdgeWeight(REFL, G2, dWeight);
                            unavailableVertexList.remove(REFL);
                            dAccREFL = dAccuracy;
                            dCostREFL = dCost;
                            System.out.println("\tUpdated edge " + directedGraph.getEdge(REFL, G2) + " with weight " + df.format(directedGraph.getEdgeWeight(directedGraph.getEdge(REFL, G2))));
                            break;
                        case GPS:
                            directedGraph.setEdgeWeight(GPS, sP_, dWeight);
                            dAccGPS = dAccuracy;
                            dCostGPS = dCost;
                            System.out.println("\tUpdated edge " + directedGraph.getEdge(GPS, sP_) + " with weight " + df.format(directedGraph.getEdgeWeight(directedGraph.getEdge(GPS, sP_))));
                            break;
                        case CELL:
                            directedGraph.setEdgeWeight(CELL, sP_, dWeight);
                            unavailableVertexList.remove(CELL);
                            dAccCELL = dAccuracy;
                            dCostCELL = dCost;
                            System.out.println("\tUpdated edge " + directedGraph.getEdge(CELL, sP_) + " with weight " + df.format(directedGraph.getEdgeWeight(directedGraph.getEdge(CELL, sP_))));
                            break;
                        case LOC:
                            directedGraph.setEdgeWeight(LOC, sP_, dWeight);
                            unavailableVertexList.remove(LOC);
                            dAccLOC = dAccuracy;
                            dCostLOC = dCost;
                            System.out.println("\tUpdated edge " + directedGraph.getEdge(LOC, sP_) + " with weight " + df.format(directedGraph.getEdgeWeight(directedGraph.getEdge(LOC, sP_))));
                            break;
                        default:
                            System.out.println("\tCould not find a vertex for id " + sId);
                            break;
                    }
                }
            }
        }

        // Remove unavailable / unqualified services from graph
        removeFromGraph(unavailableVertexList);

        // Check noPF for min accucary
        if (directedGraph.containsVertex(noPF)) {
            if (dAccNoPF < dMinAccuracy) {
                directedGraph.removeVertex(noPF);
                System.out.println("Removed vertex noPF from graph: failing minimum accuracy " + dAccNoPF + " < " + dMinAccuracy + " (dMinAccuracy)");
            } else {
                double dNewWeight = (1 - 0.2) * dAccuracyWeight + 0.1 * dCostWeight + 0.0 * dTimeWeight;
                directedGraph.setEdgeWeight(noPF, sP_, dNewWeight);
            }
        }
        // Check GPS for min accucary
        if (directedGraph.containsVertex(GPS)) {
            if (dAccGPS < dMinAccuracy) {
                directedGraph.removeVertex(GPS);
                System.out.println("Removed vertex noPF from graph: failing minimum accuracy " + dAccGPS + " < " + dMinAccuracy + " (dMinAccuracy)");
            } else {
                double dNewWeight = (1 - 0.5) * dAccuracyWeight + 0.1 * dCostWeight + 0.0 * dTimeWeight;
                directedGraph.setEdgeWeight(GPS, sP_, dNewWeight);
            }
        }
    }

    public void removeFromGraph(List<String> removalList) {
        for (int i = 0; i < removalList.size(); i++) {
            directedGraph.removeVertex(removalList.get(i));
            System.out.println("Removed vertex " + removalList.get(i) + " from graph: unavailable or failing minimum accuracy.");
        }
        //System.out.println("New graph after removal method: " + directedGraph.toString());
    }

    public double getMinAccuracyOfPath(List<String> pathVertexList) {
        double dMinAccuracyOfPath = 99.9;

        for (int i = 0; i < pathVertexList.size(); i++) {
            switch (pathVertexList.get(i)) {
                case PF:
                    if (dMinAccuracyOfPath > dAccPF)
                        dMinAccuracyOfPath = dAccPF;
                    break;
                case PFL:
                    if (dMinAccuracyOfPath > dAccPFL)
                        dMinAccuracyOfPath = dAccPFL;
                    break;
                case noPF:
                    if (dMinAccuracyOfPath > dAccNoPF)
                        dMinAccuracyOfPath = dAccNoPF;
                    break;
                case LAB:
                    if (dMinAccuracyOfPath > dAccLAB)
                        dMinAccuracyOfPath = dAccLAB;
                    break;
                case NIRS:
                    if (dMinAccuracyOfPath > dAccNIRS)
                        dMinAccuracyOfPath = dAccNIRS;
                    break;
                case REF:
                    if (dMinAccuracyOfPath > dAccREF)
                        dMinAccuracyOfPath = dAccREF;
                    break;
                case REFL:
                    if (dMinAccuracyOfPath > dAccREFL)
                        dMinAccuracyOfPath = dAccREFL;
                    break;
                case GPS:
                    if (dMinAccuracyOfPath > dAccGPS)
                        dMinAccuracyOfPath = dAccGPS;
                    break;
                case CELL:
                    if (dMinAccuracyOfPath > dAccCELL)
                        dMinAccuracyOfPath = dAccCELL;
                    break;
                case LOC:
                    if (dMinAccuracyOfPath > dAccLOC)
                        dMinAccuracyOfPath = dAccLOC;
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

        /*
        List<DefaultWeightedEdge> defaultWeightedEdgeList = graphPath.getEdgeList();
        for (int i = 0; i < defaultWeightedEdgeList.size(); i++) {
            double dCurrWeight = directedGraph.getEdgeWeight(defaultWeightedEdgeList.get(i));
            if (dCurrWeight < dMinAccuracyOfPath)
                dMinAccuracyOfPath = dCurrWeight;
        }
         */

        return dMinAccuracyOfPath;
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
        System.out.println("Number of shortest paths (" + sStartVertex + " to " + sEndVertex + ") found: " + yenKGraphPaths.size());

        int iLimiter = 3;
        for (int i = 0; i < yenKGraphPaths.size(); i++) {
            if (i < iLimiter) {
                System.out.println("\tyenKShortestPath-#" + i + ": " + yenKGraphPaths.get(i));
                System.out.println("\tyenKShortestPath-#" + i + "-VertexList: " + yenKGraphPaths.get(i).getVertexList().toString());
                System.out.println("\tyenKShortestPath-#" + i + "-Weight: " + df.format(yenKGraphPaths.get(i).getWeight()));
            } else {
                System.out.println("\tOmitting remaining paths...");
                break;
            }
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

    public String selectServiceGraphBased(String sStartVertex, String sEndVertex, double dMinAccuracy,
                                          double dCostLimit, String sSlurrySegment) {
        String sChosenId = "no-path";

        // Calc paths and check for total cost limit
        List<GraphPath<String, DefaultWeightedEdge>> graphPathsList = this.kShortestPaths(sStartVertex, sEndVertex);
        double dMinAccuracyOfPath;
        double dTotalCost;
        int iChosenPath = -1;
        for (int i = 0; i < graphPathsList.size(); i++) {
            System.out.println("Checking minimum accuracy (min defined: " + dMinAccuracy + ") and total cost (max defined: " + dCostLimit + ") of path #" + i + " ...");
            dMinAccuracyOfPath = this.getMinAccuracyOfPath(graphPathsList.get(i).getVertexList());
            System.out.println("\tMinimum accuracy: " + df.format(dMinAccuracyOfPath));
            dTotalCost = this.calcTotalCost(graphPathsList.get(i).getVertexList());
            System.out.println("\tTotal cost: " + df.format(dTotalCost));
            if (dMinAccuracyOfPath >= dMinAccuracy && dTotalCost <= dCostLimit) {
                System.out.println("Path selected: #" + i);
                System.out.println("\tPath edge list:" + graphPathsList.get(i).getEdgeList().toString());
                System.out.println("\tPath vertex list: " + graphPathsList.get(i).getVertexList());
                System.out.println("\tMinimum accuracy " + df.format(dMinAccuracyOfPath) + " >= " + dMinAccuracy + " (dMinAccuracy)");
                System.out.println("\tPath cost " + df.format(dTotalCost) + " <= " + dCostLimit + " (dCostLimit)");
                iChosenPath = i;
                break;
            }
        }

        // Find id for chosen path
        if (iChosenPath > -1) {
            List<String> pathVertexList = graphPathsList.get(iChosenPath).getVertexList();
            for (int i = 0; i < pathVertexList.size(); i++) {
                String sPathVertex = pathVertexList.get(i);
                switch (sSlurrySegment) {
                    case "precision-farming":
                        if (sPathVertex.equals("PF")) {
                            sChosenId = "PF";
                        } else if (sPathVertex.equals("PF(L)")) {
                            sChosenId = "PF(L)";
                        } else if (sPathVertex.equals("noPF")) {
                            sChosenId = "noPF";
                        }
                        break;
                    case "slurry-analysis":
                        if (sPathVertex.equals("LAB")) {
                            sChosenId = "LAB";
                        } else if (sPathVertex.equals("NIRS")) {
                            sChosenId = "NIRS";
                        } else if (sPathVertex.equals("REF")) {
                            sChosenId = "REF";
                        } else if (sPathVertex.equals("REF(L)")) {
                            sChosenId = "REF(L)";
                        }
                        break;
                    case "position-correction":
                        if (sPathVertex.equals("GPS")) {
                            sChosenId = "GPS";
                        } else if (sPathVertex.equals("CELL")) {
                            sChosenId = "CELL";
                        } else if (sPathVertex.equals("LOC")) {
                            sChosenId = "LOC";
                        }
                        break;
                    default:
                        System.out.println("Missing process segment.");
                        break;
                }
            }
        } else {
            System.out.println("No path qualified for a minimum accuracy >= " + dMinAccuracy + " and a total cost <= " + dCostLimit + "!");
        }

        return sChosenId;
    }

    public Instance getInstanceForId(List<Instance> instanceList, String sChosenId) {
        Instance instance = null;

        // Get instance for id
        for (int i = 0; i < instanceList.size(); i++) {
            if (instanceList.get(i).getMetadata().getType().equals(sChosenId)) {
                instance = instanceList.get(i);
                break;
            }
        }

        return instance;
    }

    public void printGraph() {
        if (directedGraph != null) {
            // Print graph with weights
            System.out.println("Current graph: " + directedGraph.toString());
            System.out.println("--- Precising farming segment ---");
            if (directedGraph.getEdge(PF, G1) != null)
                System.out.println("\tEdge " + directedGraph.getEdge(PF, G1).toString() + " with weight " + df.format(directedGraph.getEdgeWeight(directedGraph.getEdge(PF, G1))));
            if (directedGraph.getEdge(PFL, G1) != null)
                System.out.println("\tEdge " + directedGraph.getEdge(PFL, G1).toString() + " with weight " + df.format(directedGraph.getEdgeWeight(directedGraph.getEdge(PFL, G1))));
            if (directedGraph.getEdge(noPF, sP_) != null)
                System.out.println("\tEdge " + directedGraph.getEdge(noPF, sP_).toString() + " with weight " + df.format(directedGraph.getEdgeWeight(directedGraph.getEdge(noPF, sP_))));
            System.out.println("--- Slurry analysis segment ---");
            if (directedGraph.getEdge(LAB, G2) != null)
                System.out.println("\tEdge " + directedGraph.getEdge(LAB, G2).toString() + " with weight " + df.format(directedGraph.getEdgeWeight(directedGraph.getEdge(LAB, G2))));
            if (directedGraph.getEdge(NIRS, G2) != null)
                System.out.println("\tEdge " + directedGraph.getEdge(NIRS, G2).toString() + " with weight " + df.format(directedGraph.getEdgeWeight(directedGraph.getEdge(NIRS, G2))));
            if (directedGraph.getEdge(REF, G2) != null)
                System.out.println("\tEdge " + directedGraph.getEdge(REF, G2).toString() + " with weight " + df.format(directedGraph.getEdgeWeight(directedGraph.getEdge(REF, G2))));
            if (directedGraph.getEdge(REFL, G2) != null)
                System.out.println("\tEdge " + directedGraph.getEdge(REFL, G2).toString() + " with weight " + df.format(directedGraph.getEdgeWeight(directedGraph.getEdge(REFL, G2))));
            System.out.println("--- Position correction segment ---");
            if (directedGraph.getEdge(GPS, sP_) != null)
                System.out.println("\tEdge " + directedGraph.getEdge(GPS, sP_).toString() + " with weight " + df.format(directedGraph.getEdgeWeight(directedGraph.getEdge(GPS, sP_))));
            if (directedGraph.getEdge(CELL, sP_) != null)
                System.out.println("\tEdge " + directedGraph.getEdge(CELL, sP_).toString() + " with weight " + df.format(directedGraph.getEdgeWeight(directedGraph.getEdge(CELL, sP_))));
            if (directedGraph.getEdge(LOC, sP_) != null)
                System.out.println("\tEdge " + directedGraph.getEdge(LOC, sP_).toString() + " with weight " + df.format(directedGraph.getEdgeWeight(directedGraph.getEdge(LOC, sP_))));
        } else {
            System.out.println("Graph is null!");
        }
    }

    /*
    public void removeVerticesFromGraph() {
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


    }
    */
}

package org.example.neighbor;

import org.example.datatypes.Neighbor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

// Controller class implementing the neighbor table service.
@RestController
public class NeighborController {

    // Variables
    private List<Neighbor> neighborList = new ArrayList<Neighbor>();
    private Neighbor neighbor;

    // Return neighbor table
    @RequestMapping(value = "/neighbortable", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public List<Neighbor> getNeighbors() {
        return this.neighborList;
    }

    // Add new neighbor
    @RequestMapping(value = "/neighbortable/add", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public Neighbor addNeighbor(@RequestBody Neighbor newNeighbor) {
        for (int i = 0; i < neighborList.size(); i++) {
            if (neighborList.get(i).getNeighborAddress().equals(newNeighbor.getNeighborAddress())) {
                return new Neighbor();
            }
        }

        this.neighborList.add(newNeighbor);
        return newNeighbor;
    }

    // Dell a neighbor
    @RequestMapping(value = "/neighbortable/del", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public Neighbor delNeighbor(@RequestBody Neighbor pastNeighbor) {
        for (int i = 0; i < neighborList.size(); i++) {
            if (neighborList.get(i).getNeighborAddress().equals(pastNeighbor.getNeighborAddress())) {
                neighborList.remove(i);
                return pastNeighbor;
            }
        }
        return new Neighbor();
    }

    // Neighbor nodes for SP in test env
    @RequestMapping(value = "/testenvsp", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public void testNeighborsSp() {
        List<String> testNeighborList = new ArrayList<>();
        testNeighborList.add("http://localhost:8020/");
        testNeighborList.add("http://localhost:8030/");
        testNeighborList.add("http://localhost:8040/");
        testNeighborList.add("http://localhost:8050/");

        // Check for duplicates
        for (int j = 0; j < testNeighborList.size(); j++) {
            boolean duplicate = false;
            for (int i = 0; i < neighborList.size(); i++) {
                if (neighborList.get(i).getNeighborAddress().equals(testNeighborList.get(j))) {
                    duplicate = true;
                }
            }
            if (!duplicate) {
                neighborList.add(new Neighbor(testNeighborList.get(j)));
            }
        }
    }

    // Neighbor nodes for MGMT in test env
    @RequestMapping(value = "/testenvmgmt", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public void testNeighborsMgmt() {
        List<String> testNeighborList = new ArrayList<>();
        testNeighborList.add("http://localhost:8020/");
        testNeighborList.add("http://localhost:8030/");

        // Check for duplicates
        for (int j = 0; j < testNeighborList.size(); j++) {
            boolean duplicate = false;
            for (int i = 0; i < neighborList.size(); i++) {
                if (neighborList.get(i).getNeighborAddress().equals(testNeighborList.get(j))) {
                    duplicate = true;
                }
            }
            if (!duplicate) {
                neighborList.add(new Neighbor(testNeighborList.get(j)));
            }
        }
    }
}

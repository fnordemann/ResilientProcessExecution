package org.example.loc;

import org.example.datatypes.GpsReply;
import org.example.datatypes.GpsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Controller class implementing a simple service
@RestController
public class LocController {

    @Autowired
    private DiscoveryClient discoveryClient;

    // Variables
    private List<GpsReply> gpsReplyList = new ArrayList<GpsReply>();
    private GpsReply gpsReply;

    // Return latest element for GET
    @RequestMapping(value = "/gps", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public GpsReply getReply() {
        if (this.gpsReplyList.size() > 0) {
            return this.gpsReplyList.get(this.gpsReplyList.size() - 1);
        } else {
            return null;
        }
    }

    /*
     * @RequestMapping(value = "/greeting", method=RequestMethod.GET, produces =
     * "application/json")
     *
     * @ResponseBody public List<Greeting2> getGreeting() { return
     * this.greetingList; //return
     * this.greetingList.get(this.greetingList.size()-1); }
     */

    // Handle incoming request
    @RequestMapping(value = "/gps", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public GpsReply postRequest(@RequestBody GpsRequest gpsRequest) {
        GpsReply gpsReply = gpsCorrection(gpsRequest);
        this.gpsReplyList.add(gpsReply);
        return gpsReply;
    }

    // Provide GPS offset
    private GpsReply gpsCorrection(GpsRequest gpsRequest) {
        GpsReply gpsReply = new GpsReply();

        if (gpsRequest.getTaskId().contentEquals("")) {
            gpsReply.setTaskId("0000");
        } else {
            gpsReply.setTaskId(gpsRequest.getTaskId());
        }

        // Here, a correction offset would be provided. We use random values for illustration.
        Random r = new Random();
        double max = 0.007;
        double min = 0.07;
        gpsReply.setLatOffset(min + max * r.nextDouble());
        gpsReply.setLongOffset(min + max * r.nextDouble());

        return gpsReply;
    }
}

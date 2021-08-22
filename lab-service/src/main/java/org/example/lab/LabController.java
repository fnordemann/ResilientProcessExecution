package org.example.lab;

import org.example.datatypes.AnalysisReply;
import org.example.datatypes.AnalysisRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Controller class implementing a simple service
@RestController
public class LabController {

    @Autowired
    private DiscoveryClient discoveryClient;

    // Variables
    private List<AnalysisReply> analysisReplyList = new ArrayList<AnalysisReply>();
    private AnalysisReply analysisReply;

    // Return latest element for GET
    @RequestMapping(value = "/lab", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public AnalysisReply getReply() {
        if (this.analysisReplyList.size() > 0) {
            return this.analysisReplyList.get(this.analysisReplyList.size() - 1);
        } else {
            return null;
        }
    }

    // Handle incoming request
    @RequestMapping(value = "/lab", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public AnalysisReply postRequest(@RequestBody AnalysisRequest analysisRequest) {
        AnalysisReply analysisReply = calcIngredients(analysisRequest);
        this.analysisReplyList.add(analysisReply);
        return analysisReply;
    }

    // Calculate slurry application value
    private AnalysisReply calcIngredients(AnalysisRequest analysisRequest) {
        AnalysisReply analysisReply = new AnalysisReply();

        if (analysisRequest.getTaskId().contentEquals("")) {
            analysisReply.setTaskId("0000");
        } else {
            analysisReply.setTaskId(analysisRequest.getTaskId());
        }

        // Here, a lab would analyze the slurry. We use random values for illustration.
        Random r = new Random();
        int max = 50;
        int min = 1;
        analysisReply.setNitrogen(min + max * r.nextDouble());
        analysisReply.setPhosphor(min + max * r.nextDouble());
        analysisReply.setAmmonium(min + max * r.nextDouble());
        analysisReply.setPotassium(min + max * r.nextDouble());

        return analysisReply;
    }
}

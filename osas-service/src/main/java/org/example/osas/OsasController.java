package org.example.osas;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.example.datatypes.AnalysisReply;
import org.example.datatypes.AnalysisRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;

// Controller class implementing a simple service
@RestController
public class OsasController {

	@Autowired
    private DiscoveryClient discoveryClient;
	
	// Variables
	private List<AnalysisReply> analysisReplyList = new ArrayList<AnalysisReply>();
	private AnalysisReply analysisReply;

	// Return latest element for GET
	@RequestMapping(value = "/analysis", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public AnalysisReply getReply() {
		if (this.analysisReplyList.size() > 0) {
			return this.analysisReplyList.get(this.analysisReplyList.size() - 1);
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
	@RequestMapping(value = "/analysis", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public AnalysisReply postRequest(@RequestBody AnalysisRequest analysisRequest) {
		AnalysisReply analysisReply = calcIngredients(analysisRequest);
		this.analysisReplyList.add(analysisReply);
		return analysisReply;
	}

	// Calculate slurry application value
	private AnalysisReply calcIngredients(AnalysisRequest analysisRequest) {
		AnalysisReply analysisReply = new AnalysisReply();
		
		if(analysisRequest.getTaskId().contentEquals("")) {
			analysisReply.setTaskId("0000");
		}
		else {
			analysisReply.setTaskId(analysisRequest.getTaskId());
		}
		
		// Here, a nirs would analyze the slurry. We use random values for illustration.
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

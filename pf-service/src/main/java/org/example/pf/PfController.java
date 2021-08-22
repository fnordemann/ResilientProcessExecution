package org.example.pf;

import java.util.ArrayList;
import java.util.List;

import org.example.datatypes.AppMapReply;
import org.example.datatypes.AppMapRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;

// Controller class implementing a simple service
@RestController
public class PfController {

	@Autowired
    private DiscoveryClient discoveryClient;
	
	// Variables
	private List<AppMapReply> appMapReplyList = new ArrayList<AppMapReply>();
	private AppMapReply appMapReply;

	// Return latest element for GET
	@RequestMapping(value = "/analysis", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public AppMapReply getReply() {
		if (this.appMapReplyList.size() > 0) {
			return this.appMapReplyList.get(this.appMapReplyList.size() - 1);
		} else {
			return null;
		}
	}

	// Handle incoming request
	@RequestMapping(value = "/analysis", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public AppMapReply postRequest(@RequestBody AppMapRequest appMapRequest) {
		AppMapReply appMapReply = calcAppMap(appMapRequest);
		this.appMapReplyList.add(appMapReply);
		return appMapReply;
	}

	// Calculate slurry app map
	private AppMapReply calcAppMap(AppMapRequest appMapRequest) {
		AppMapReply appMapReply = new AppMapReply();
		
		if(appMapRequest.getTaskId().contentEquals("")) {
			appMapReply.setTaskId("0000");
		}
		else {
			appMapReply.setTaskId(appMapRequest.getTaskId());
		}
		
		// Here, an app map would be created. We use a string value for illustration.
		appMapReply.setAppMap("AppMap-Placeholder");

		return appMapReply;
	}

}

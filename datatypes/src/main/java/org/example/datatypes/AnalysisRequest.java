package org.example.datatypes;

//Message format
public class AnalysisRequest {

	private String taskId;

	public AnalysisRequest() {
		this.taskId = "0000";
	}
	
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getTaskId() {
		return taskId;
	}
}

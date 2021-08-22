package org.example.datatypes;

//Message format
public class AppMapRequest {

	private String taskId;

	public AppMapRequest() {
		this.taskId = "0000";
	}
	
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getTaskId() {
		return taskId;
	}
}

package org.example.datatypes;

//Message format
public class GpsRequest {

	private String taskId;

	public GpsRequest() {
		this.taskId = "0000";
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getTaskId() {
		return taskId;
	}
}

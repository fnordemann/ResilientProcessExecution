package org.example.datatypes;

// Message format
public class AppMapReply {

	private String taskId;
	private String appMap;

	public AppMapReply() {
	}

	public AppMapReply(String taskId) {
		this.taskId = taskId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getAppMap() {
		return appMap;
	}

	public void setAppMap(String appMap) {
		this.appMap = appMap;
	}
}

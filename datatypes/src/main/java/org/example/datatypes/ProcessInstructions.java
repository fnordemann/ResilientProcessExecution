package org.example.datatypes;

public class ProcessInstructions {

	private String taskId;
	private String taskInstructions;

	public ProcessInstructions(){
	}

	public ProcessInstructions(String taskId, String taskInstructions) {
		this.taskId = taskId;
		this.taskInstructions = taskInstructions;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getTaskInstructions() {
		return taskInstructions;
	}

	public void setTaskInstructions(String taskInstructions) {
		this.taskInstructions = taskInstructions;
	}
}

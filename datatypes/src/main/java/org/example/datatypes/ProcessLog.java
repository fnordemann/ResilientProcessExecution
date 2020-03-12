package org.example.datatypes;

public class ProcessLog {

	private String taskId;
	private int taskStatus;
	private double taskTotalRuntime;
	private double taskCompletion;
	
	public ProcessLog(){
	}

	public ProcessLog(String taskId, int taskStatus, double taskTotalRuntime, double taskCompletion) {
		this.taskId = taskId;
		this.taskStatus = taskStatus;
		this.taskTotalRuntime = taskTotalRuntime;
		this.taskCompletion = taskCompletion;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public int getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(int taskStatus) {
		this.taskStatus = taskStatus;
	}

	public double getTaskTotalRuntime() {
		return taskTotalRuntime;
	}

	public void setTaskTotalRuntime(double taskTotalRuntime) {
		this.taskTotalRuntime = taskTotalRuntime;
	}

	public double getTaskCompletion() {
		return taskCompletion;
	}

	public void setTaskCompletion(double taskCompletion) {
		this.taskCompletion = taskCompletion;
	}
}

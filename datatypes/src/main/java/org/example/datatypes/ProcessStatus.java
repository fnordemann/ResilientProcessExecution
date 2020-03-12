package org.example.datatypes;

public class ProcessStatus {

	private String taskId;
	private int taskStatus;
	private double taskRuntime;
	private double taskCompletion;
	private int seq;
	
	public ProcessStatus(){
	}
	
	public ProcessStatus(String taskId, int taskStatus, double taskRuntime, double taskCompletion, int seq){
		this.taskId = taskId;
		this.taskStatus = taskStatus;
		this.taskRuntime = taskRuntime;
		this.taskCompletion = taskCompletion;
		this.seq = seq;
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

	public double getTaskRuntime() {
		return taskRuntime;
	}

	public void setTaskRuntime(double taskRuntime) {
		this.taskRuntime = taskRuntime;
	}

	public double getTaskCompletion() {
		return taskCompletion;
	}

	public void setTaskCompletion(double taskCompletion) {
		this.taskCompletion = taskCompletion;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

}

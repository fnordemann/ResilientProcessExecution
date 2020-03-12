package org.example.datatypes;

// Message format
public class AnalysisReply {

	private String taskId;
	private double nitrogen;
	private double phosphor;
	private double ammonium;
	private double potassium;

	public AnalysisReply() {
	}

	public AnalysisReply(String taskId, double nitrogen, double phosphor, double ammonium, double potassium) {
		this.taskId = taskId;
		this.nitrogen = nitrogen;
		this.phosphor = phosphor;
		this.ammonium = ammonium;
		this.potassium = potassium;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public double getNitrogen() {
		return nitrogen;
	}

	public void setNitrogen(double nitrogen) {
		this.nitrogen = nitrogen;
	}

	public double getPhosphor() {
		return phosphor;
	}

	public void setPhosphor(double phosphor) {
		this.phosphor = phosphor;
	}

	public double getAmmonium() {
		return ammonium;
	}

	public void setAmmonium(double ammonium) {
		this.ammonium = ammonium;
	}

	public double getPotassium() {
		return potassium;
	}

	public void setPotassium(double potassium) {
		this.potassium = potassium;
	}

}

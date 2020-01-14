package com.eddocg.alerts;

public class Worker {
	private String workerName;
	private String email;
	private String address;

	public String getWorkerName() {
		return workerName;
	}
	public void setWorkerName(String workerName) {
		this.workerName = workerName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "Worker [workerName=" + workerName + ", email=" + email + ", address=" + address + "]";
	}

}
package com.hogdev.thehoggesweb.beans;

public class DeviceBean {

	String deviceID;
	boolean on = false;

	public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	public boolean isOn() {
		return on;
	}

	public void setOn(boolean on) {
		this.on = on;
	}

}

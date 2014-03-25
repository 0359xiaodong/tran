package com.renyu.nj_tran.model;

import java.io.Serializable;

public class CurrentBusModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	int currentLevel=0;
    long uploadTime=0;
    int busId=0;
    double busLong=0;
    double busLat=0;
    int reloadTime=0;
    double busSpeed=0;
    double Distance=0;
	public int getCurrentLevel() {
		return currentLevel;
	}
	public void setCurrentLevel(int currentLevel) {
		this.currentLevel = currentLevel;
	}
	public long getUploadTime() {
		return uploadTime;
	}
	public void setUploadTime(long uploadTime) {
		this.uploadTime = uploadTime;
	}
	public int getBusId() {
		return busId;
	}
	public void setBusId(int busId) {
		this.busId = busId;
	}
	public double getBusLong() {
		return busLong;
	}
	public void setBusLong(double busLong) {
		this.busLong = busLong;
	}
	public double getBusLat() {
		return busLat;
	}
	public void setBusLat(double busLat) {
		this.busLat = busLat;
	}
	public int getReloadTime() {
		return reloadTime;
	}
	public void setReloadTime(int reloadTime) {
		this.reloadTime = reloadTime;
	}
	public double getBusSpeed() {
		return busSpeed;
	}
	public void setBusSpeed(double busSpeed) {
		this.busSpeed = busSpeed;
	}
	public double getDistance() {
		return Distance;
	}
	public void setDistance(double distance) {
		Distance = distance;
	}
}

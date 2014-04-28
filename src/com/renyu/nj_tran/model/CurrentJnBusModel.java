package com.renyu.nj_tran.model;

import java.io.Serializable;

public class CurrentJnBusModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	int stationNo=0;
	String dis="";
	String stationName="";
	public int getStationNo() {
		return stationNo;
	}
	public void setStationNo(int stationNo) {
		this.stationNo = stationNo;
	}
	public String getDis() {
		return dis;
	}
	public void setDis(String dis) {
		this.dis = dis;
	}
	public String getStationName() {
		return stationName;
	}
	public void setStationName(String stationName) {
		this.stationName = stationName;
	}
	
}

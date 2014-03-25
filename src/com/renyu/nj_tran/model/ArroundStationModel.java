package com.renyu.nj_tran.model;

import java.io.Serializable;
import java.util.ArrayList;

public class ArroundStationModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	double map_long=0;
	double map_lat=0;
	double gps_long=0;
	double gps_lat=0;
    int distance=0;
    int stationId=0;
    String name="";
    ArrayList<String> lids_list=null;
    
	public double getMap_long() {
		return map_long;
	}
	public void setMap_long(double map_long) {
		this.map_long = map_long;
	}
	public double getMap_lat() {
		return map_lat;
	}
	public void setMap_lat(double map_lat) {
		this.map_lat = map_lat;
	}
	public double getGps_long() {
		return gps_long;
	}
	public void setGps_long(double gps_long) {
		this.gps_long = gps_long;
	}
	public double getGps_lat() {
		return gps_lat;
	}
	public void setGps_lat(double gps_lat) {
		this.gps_lat = gps_lat;
	}
	public int getDistance() {
		return distance;
	}
	public void setDistance(int distance) {
		this.distance = distance;
	}
	public int getStationId() {
		return stationId;
	}
	public void setStationId(int stationId) {
		this.stationId = stationId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<String> getLids_list() {
		return lids_list;
	}
	public void setLids_list(ArrayList<String> lids_list) {
		this.lids_list = lids_list;
	}
    
}

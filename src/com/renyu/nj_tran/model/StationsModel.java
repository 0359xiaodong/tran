package com.renyu.nj_tran.model;

import java.io.Serializable;

public class StationsModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int id=0;
	String station_name="";
	double station_lat=0;
	double station_long=0;
	double gps_station_lat=0;
	double gps_station_long=0;
	double map_station_lat=0;
	double map_station_long=0;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getStation_name() {
		return station_name;
	}
	public void setStation_name(String station_name) {
		this.station_name = station_name;
	}
	public double getStation_lat() {
		return station_lat;
	}
	public void setStation_lat(double station_lat) {
		this.station_lat = station_lat;
	}
	public double getStation_long() {
		return station_long;
	}
	public void setStation_long(double station_long) {
		this.station_long = station_long;
	}
	public double getGps_station_lat() {
		return gps_station_lat;
	}
	public void setGps_station_lat(double gps_station_lat) {
		this.gps_station_lat = gps_station_lat;
	}
	public double getGps_station_long() {
		return gps_station_long;
	}
	public void setGps_station_long(double gps_station_long) {
		this.gps_station_long = gps_station_long;
	}
	public double getMap_station_lat() {
		return map_station_lat;
	}
	public void setMap_station_lat(double map_station_lat) {
		this.map_station_lat = map_station_lat;
	}
	public double getMap_station_long() {
		return map_station_long;
	}
	public void setMap_station_long(double map_station_long) {
		this.map_station_long = map_station_long;
	}
}

package com.renyu.nj_tran.model;

import java.io.Serializable;

public class StationByIdModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int id=0;
	int level=0;
	int station_id=0;
	String name="";
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getStation_id() {
		return station_id;
	}
	public void setStation_id(int station_id) {
		this.station_id = station_id;
	}
	
}

package com.renyu.nj_tran.model;

import java.io.Serializable;

public class BusLineModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String line_code="";
	String line_name="";
	String start_from="";
	String end_location="";
	String start_time="";
	String end_time="";
	String path_info="";
	String piao="";
	int id=0;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getLine_code() {
		return line_code;
	}
	public void setLine_code(String line_code) {
		this.line_code = line_code;
	}
	public String getLine_name() {
		return line_name;
	}
	public void setLine_name(String line_name) {
		this.line_name = line_name;
	}
	public String getStart_from() {
		return start_from;
	}
	public void setStart_from(String start_from) {
		this.start_from = start_from;
	}
	public String getEnd_location() {
		return end_location;
	}
	public void setEnd_location(String end_location) {
		this.end_location = end_location;
	}
	public String getStart_time() {
		return start_time;
	}
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}
	public String getEnd_time() {
		return end_time;
	}
	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}
	public String getPath_info() {
		return path_info;
	}
	public void setPath_info(String path_info) {
		this.path_info = path_info;
	}
	public String getPiao() {
		return piao;
	}
	public void setPiao(String piao) {
		this.piao = piao;
	}
}

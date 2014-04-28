package com.renyu.nj_tran.model;

import java.io.Serializable;

public class JNBusLineModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	int sno=0;
	String sn="";
	long log=0;
	long lat=0;
	int si=0;
	public int getSno() {
		return sno;
	}
	public void setSno(int sno) {
		this.sno = sno;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public long getLog() {
		return log;
	}
	public void setLog(long log) {
		this.log = log;
	}
	public long getLat() {
		return lat;
	}
	public void setLat(long lat) {
		this.lat = lat;
	}
	public int getSi() {
		return si;
	}
	public void setSi(int si) {
		this.si = si;
	}
	
}

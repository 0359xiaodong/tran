package com.renyu.nj_tran.commons;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import com.renyu.nj_tran.model.BusLineModel;
import com.renyu.nj_tran.model.StationByIdModel;
import com.renyu.nj_tran.model.StationsModel;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Conn extends SQLiteOpenHelper {
	final static String DATABASE_NAME="onekilo";
	final static int DATABASE_VERSION=1;
	
	final static String _ID="_id";

	Context context=null;
	
	static Conn conn=null;
	

	public static Conn getInstance(Context context) {
		if(conn==null) {
			conn=new Conn(context);
		}
		return conn;
	}

	private Conn(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
		this.context=context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 根据路线获取公交线路信息
	 * @param lineName
	 */
	public ArrayList<BusLineModel> getTranInfoDirect(String lineName, boolean isDirect) {
		synchronized (this) {
			ArrayList<BusLineModel> modelList=new ArrayList<BusLineModel>();
			File file=new File("/data/data/"+context.getPackageName()+"/iso2014.db");
			SQLiteDatabase db=SQLiteDatabase.openOrCreateDatabase(file.getPath(), null); 
			Cursor cs=null;
			if(isDirect) {
				cs=db.query("buslines", null, "line_name=?", new String[]{lineName}, null, null, null);
			}
			else {
				cs=db.query("buslines", null, "line_name like '%"+lineName+"%'", new String[]{}, null, null, null);
			}
			cs.moveToFirst();
			for(int i=0;i<cs.getCount();i++) {
				cs.moveToPosition(i);
				BusLineModel model=new BusLineModel();
				model.setEnd_location(cs.getString(cs.getColumnIndex("end_location")));
				model.setEnd_time(cs.getString(cs.getColumnIndex("end_time")));
				model.setLine_code(cs.getString(cs.getColumnIndex("line_code")));
				model.setLine_name(cs.getString(cs.getColumnIndex("line_name")));
				model.setPath_info(cs.getString(cs.getColumnIndex("pathinfo")));
				model.setPiao(cs.getString(cs.getColumnIndex("piao")));
				model.setStart_from(cs.getString(cs.getColumnIndex("start_from")));
				model.setStart_time(cs.getString(cs.getColumnIndex("start_time")));
				model.setId(cs.getInt(cs.getColumnIndex("id")));
				modelList.add(model);
			}
			cs.close();
			db.close();
			return modelList;
		}
	}
	
	/**
	 * 获取江宁公交线路信息
	 * @param lineName
	 * @param isDirect
	 * @return
	 */
	public ArrayList<BusLineModel> getJNTranInfoDirect(String lineName, boolean isDirect) {
		synchronized (this) {
			ArrayList<BusLineModel> modelList=new ArrayList<BusLineModel>();
			File file=new File("/data/data/"+context.getPackageName()+"/jn.db");
			SQLiteDatabase db=SQLiteDatabase.openOrCreateDatabase(file.getPath(), null); 
			Cursor cs=null;
			if(isDirect) {
				cs=db.query("jndirection", null, "lineName=?", new String[]{lineName}, null, null, null);
			}
			else {
				cs=db.query("jndirection", null, "lineName like '%"+lineName+"%'", new String[]{}, null, null, null);
			}
			cs.moveToFirst();
			for(int i=0;i<cs.getCount();i++) {
				cs.moveToPosition(i);
				BusLineModel model=new BusLineModel();
				model.setEnd_location(cs.getString(cs.getColumnIndex("eStation")));
				model.setEnd_time(cs.getString(cs.getColumnIndex("eTime")));
				model.setLine_code(cs.getString(cs.getColumnIndex("inDown")));
				model.setLine_name(cs.getString(cs.getColumnIndex("lineName")));
				model.setPath_info("");
				model.setPiao("");
				model.setStart_from(cs.getString(cs.getColumnIndex("sStation")));
				model.setStart_time(cs.getString(cs.getColumnIndex("sTime")));
				model.setId(cs.getInt(cs.getColumnIndex("lineId")));
				modelList.add(model);
			}
			cs.close();
			db.close();
			return modelList;
		}
	}
	
	/**
	 * 根据公交站台获取站台信息
	 * @param lineName
	 */
	public ArrayList<StationsModel> getStationInfo(String str) {
		synchronized (this) {
			ArrayList<StationsModel> modelList=new ArrayList<StationsModel>();
			File file=new File("/data/data/"+context.getPackageName()+"/iso2014.db");
			SQLiteDatabase db=SQLiteDatabase.openOrCreateDatabase(file.getPath(), null); 
			Cursor cs=db.rawQuery("select * from stations where station_name like '%"+str+"%'", new String[]{});
			cs.moveToFirst();
			for(int i=0;i<cs.getCount();i++) {
				cs.moveToPosition(i);
				StationsModel model=new StationsModel();
				model.setGps_station_lat(cs.getDouble(cs.getColumnIndex("gps_station_lat")));
				model.setGps_station_long(cs.getDouble(cs.getColumnIndex("gps_station_long")));
				model.setId(cs.getInt(cs.getColumnIndex("id")));
				model.setMap_station_lat(cs.getDouble(cs.getColumnIndex("map_station_lat")));
				model.setMap_station_long(cs.getDouble(cs.getColumnIndex("map_station_long")));
				model.setStation_lat(cs.getDouble(cs.getColumnIndex("station_lat")));
				model.setStation_long(cs.getDouble(cs.getColumnIndex("station_long")));
				model.setStation_name(cs.getString(cs.getColumnIndex("station_name")));
				modelList.add(model);
			}
			cs.close();
			db.close();
			return modelList;
		}
	}
	
	/**
	 * 根据江宁公交站台获取站台信息
	 * @param lineName
	 */
	public ArrayList<StationsModel> getJNStationInfo(String str) {
		synchronized (this) {
			ArrayList<StationsModel> modelList=new ArrayList<StationsModel>();
			File file=new File("/data/data/"+context.getPackageName()+"/jn.db");
			SQLiteDatabase db=SQLiteDatabase.openOrCreateDatabase(file.getPath(), null); 
			Cursor cs=db.rawQuery("select * from jnstation where station like '%"+str+"%'", new String[]{});
			cs.moveToFirst();
			for(int i=0;i<cs.getCount();i++) {
				cs.moveToPosition(i);
				StationsModel model=new StationsModel();
				model.setGps_station_lat(cs.getDouble(cs.getColumnIndex("lat")));
				model.setGps_station_long(cs.getDouble(cs.getColumnIndex("log")));
				model.setId(cs.getInt(cs.getColumnIndex("lineId")));
				model.setMap_station_lat(cs.getDouble(cs.getColumnIndex("lat")));
				model.setMap_station_long(cs.getDouble(cs.getColumnIndex("log")));
				model.setStation_lat(cs.getDouble(cs.getColumnIndex("lat")));
				model.setStation_long(cs.getDouble(cs.getColumnIndex("log")));
				model.setStation_name(cs.getString(cs.getColumnIndex("station")));
				modelList.add(model);
			}
			cs.close();
			db.close();
			return modelList;
		}
	}
	
	public HashMap<String, StationsModel> getStationsModelList(LinkedList<StationByIdModel> model_list_old) {
		synchronized (this) {
			HashMap<String, StationsModel> map=new HashMap<String, StationsModel>();
			String search_temp="";
			for(int i=0;i<model_list_old.size();i++) {
				search_temp+=model_list_old.get(i).getStation_id()+",";
			}
			if(!search_temp.equals("")) {
				File file=new File("/data/data/"+context.getPackageName()+"/iso2014.db");
				SQLiteDatabase db=SQLiteDatabase.openOrCreateDatabase(file.getPath(), null); 
				Cursor cs=db.rawQuery("select * from stations where id in("+search_temp.substring(0, search_temp.length()-1)+")", new String[]{});
				cs.moveToFirst();
				for(int i=0;i<cs.getCount();i++) {
					cs.moveToPosition(i);
					StationsModel model=new StationsModel();
					model.setGps_station_lat(cs.getDouble(cs.getColumnIndex("gps_station_lat")));
					model.setGps_station_long(cs.getDouble(cs.getColumnIndex("gps_station_long")));
					model.setId(cs.getInt(cs.getColumnIndex("id")));
					model.setMap_station_lat(cs.getDouble(cs.getColumnIndex("map_station_lat")));
					model.setMap_station_long(cs.getDouble(cs.getColumnIndex("map_station_long")));
					model.setStation_lat(cs.getDouble(cs.getColumnIndex("station_lat")));
					model.setStation_long(cs.getDouble(cs.getColumnIndex("station_long")));
					model.setStation_name(cs.getString(cs.getColumnIndex("station_name")));
					map.put(""+cs.getInt(cs.getColumnIndex("id")), model);
				}
				cs.close();
				db.close();
			}			
			return map;
		}
	}
	
	public HashMap<String, ArrayList<BusLineModel>> getLineMap(ArrayList<Integer> lines) {
		synchronized (this) {
			HashMap<String, ArrayList<BusLineModel>> map=new HashMap<String, ArrayList<BusLineModel>>();
			String temp="";
			for(int i=0;i<lines.size();i++) {
				temp+=""+lines.get(i)+",";
			}
			File file=new File("/data/data/"+context.getPackageName()+"/iso2014.db");
			SQLiteDatabase db=SQLiteDatabase.openOrCreateDatabase(file.getPath(), null); 
			Cursor cs=db.rawQuery("select * from buslines where id in("+temp.substring(0, temp.length()-1)+")", new String[]{});
			cs.moveToFirst();
			for(int i=0;i<cs.getCount();i++) {
				cs.moveToPosition(i);
				ArrayList<BusLineModel> modelList=null;
				if(map.containsKey(cs.getString(cs.getColumnIndex("line_name")))) {
					modelList=map.get(cs.getString(cs.getColumnIndex("line_name")));
				}
				else {
					modelList=new ArrayList<BusLineModel>();
				}
				BusLineModel model=new BusLineModel();
				model.setEnd_location(cs.getString(cs.getColumnIndex("end_location")));
				model.setEnd_time(cs.getString(cs.getColumnIndex("end_time")));
				model.setLine_code(cs.getString(cs.getColumnIndex("line_code")));
				model.setLine_name(cs.getString(cs.getColumnIndex("line_name")));
				model.setPath_info(cs.getString(cs.getColumnIndex("pathinfo")));
				model.setPiao(cs.getString(cs.getColumnIndex("piao")));
				model.setStart_from(cs.getString(cs.getColumnIndex("start_from")));
				model.setStart_time(cs.getString(cs.getColumnIndex("start_time")));
				model.setId(cs.getInt(cs.getColumnIndex("id")));
				modelList.add(model);
				map.put(cs.getString(cs.getColumnIndex("line_name")), modelList);
			}
			cs.close();
			db.close();
			return map;
		}
	}
	
	/**
	 * 得到相关公交车id
	 * @param lineName
	 * @param startName
	 * @param endName
	 * @return
	 */
	public int getLineId(String lineName, String startName, String endName) {
		synchronized (this) {
			File file=new File("/data/data/"+context.getPackageName()+"/iso2014.db");
			SQLiteDatabase db=SQLiteDatabase.openOrCreateDatabase(file.getPath(), null); 
			Cursor cs=db.rawQuery("select * from buslines where line_name='"+lineName+"'and (start_from='"+startName+"' or end_location='"+endName+"')", new String[]{});
			cs.moveToFirst();
			if(cs.getCount()>0) {
				int line_id=-1;
				for(int i=0;i<cs.getCount();i++) {
					cs.moveToPosition(i);
					line_id=cs.getInt(cs.getColumnIndex("id"));
				}
				cs.close();
				db.close();
				return line_id;
			}
			else {
				cs.close();
				db.close();
				return -1;
			}
		}
	}
	
	/**
	 * 判断是否为江宁公交
	 * @param lineName
	 * @return
	 */
	public boolean isJN(String lineName) {
		synchronized (this) {
			boolean flag=false;
			File file=new File("/data/data/"+context.getPackageName()+"/jn.db");
			SQLiteDatabase db=SQLiteDatabase.openOrCreateDatabase(file.getPath(), null); 
			Cursor cs=db.query("jnbus", new String[]{"lineName"}, null, null, null, null, null);
			cs.moveToFirst();
			for(int i=0;i<cs.getCount();i++) {
				cs.moveToPosition(i);
				if(cs.getString(cs.getColumnIndex("lineName")).equals(lineName)) {
					flag=true;
					break;
				}
			}
			cs.close();
			db.close();
			return flag;
		}
	}
	
	/**
	 * 判断是否为江宁公交
	 * @param lineName
	 * @return
	 */
	public boolean isJN(int lineId) {
		synchronized (this) {
			boolean flag=false;
			File file=new File("/data/data/"+context.getPackageName()+"/jn.db");
			SQLiteDatabase db=SQLiteDatabase.openOrCreateDatabase(file.getPath(), null); 
			Cursor cs=db.query("jnbus", new String[]{"lineId"}, null, null, null, null, null);
			cs.moveToFirst();
			for(int i=0;i<cs.getCount();i++) {
				cs.moveToPosition(i);
				if(cs.getInt(cs.getColumnIndex("lineId"))==lineId) {
					flag=true;
					break;
				}
			}
			cs.close();
			db.close();
			return flag;
		}
	}
	
	/**
	 * 获取江宁公交车在本站位置
	 * @param stationName
	 * @param inDown
	 * @return
	 */
	public int getStationId(String stationName, int inDown, int lineId) {
		synchronized (this) {
			int id=-1;
			File file=new File("/data/data/"+context.getPackageName()+"/jn.db");
			SQLiteDatabase db=SQLiteDatabase.openOrCreateDatabase(file.getPath(), null); 
			Cursor cs=db.query("jnstation", null, "station=? and inDown=? and lineId=?", new String[]{stationName, ""+inDown, ""+lineId}, null, null, null);
			cs.moveToFirst();
			if(cs.getCount()>0) {
				cs.moveToPosition(0);
				id=cs.getInt(cs.getColumnIndex("dicId"));
			}
			cs.close();
			db.close();
			return id;
		}		
	}
	
	/**
	 * 获取本地站点信息
	 * @param inDown
	 * @param lineId
	 * @return
	 */
	public LinkedList<StationByIdModel> getOfflineStationModel(int inDown, int lineId) {
		synchronized (this) {
			LinkedList<StationByIdModel> modelList=new LinkedList<StationByIdModel>();
			File file=new File("/data/data/"+context.getPackageName()+"/jn.db");
			SQLiteDatabase db=SQLiteDatabase.openOrCreateDatabase(file.getPath(), null); 
			Cursor cs=db.query("jnstation", null, "inDown=? and lineId=?", new String[]{""+inDown, ""+lineId}, null, null, null);
			cs.moveToFirst();
			for(int i=0;i<cs.getCount();i++) {
				cs.moveToPosition(i);
				StationByIdModel model=new StationByIdModel();
				model.setId(cs.getInt(cs.getColumnIndex("dicId")));
				model.setName(cs.getString(cs.getColumnIndex("station")));
				modelList.add(model);
			}
			return modelList;
		}
	}
	
}

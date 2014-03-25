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
	public ArrayList<BusLineModel> getTranInfo(String lineName) {
		synchronized (this) {
			ArrayList<BusLineModel> modelList=new ArrayList<BusLineModel>();
			File file=new File("/data/data/"+context.getPackageName()+"/iso2014.db");
			SQLiteDatabase db=SQLiteDatabase.openOrCreateDatabase(file.getPath(), null); 
			Cursor cs=db.query("buslines", null, "line_name=?", new String[]{lineName}, null, null, null);
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
	
}

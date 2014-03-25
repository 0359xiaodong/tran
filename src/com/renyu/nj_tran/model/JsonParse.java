package com.renyu.nj_tran.model;

import java.util.ArrayList;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonParse {
	
	public static ArrayList<ArroundStationModel> getArroundStationModelList(String str) {
		ArrayList<ArroundStationModel> modelList=null;
		try {
			JSONObject obj=new JSONObject(str);
			JSONObject message_obj=obj.getJSONObject("message");
			if(message_obj.getString("code").equals("0")) {
				modelList=new ArrayList<ArroundStationModel>();
				JSONArray data_array=obj.getJSONArray("data");
				for(int i=0;i<data_array.length();i++) {
					ArroundStationModel model=new ArroundStationModel();
					model.setDistance(data_array.getJSONObject(i).getInt("distance"));
					model.setGps_lat(data_array.getJSONObject(i).getDouble("gps_lat"));
					model.setGps_long(data_array.getJSONObject(i).getDouble("gps_long"));
					model.setMap_lat(data_array.getJSONObject(i).getDouble("map_lat"));
					model.setMap_long(data_array.getJSONObject(i).getDouble("map_long"));
					model.setName(data_array.getJSONObject(i).getString("name"));
					model.setStationId(data_array.getJSONObject(i).getInt("id"));
					JSONArray lids_array=data_array.getJSONObject(i).getJSONArray("lids");
					ArrayList<String> lids_list=new ArrayList<String>();
					for(int j=0;j<lids_array.length();j++) {
						if(lids_list.contains(lids_array.getString(j))) {
							continue;
						}
						lids_list.add(lids_array.getString(j));
					}
					model.setLids_list(lids_list);
					modelList.add(model);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return modelList;
	}
	
	public static LinkedList<StationByIdModel> getGetStationByIdModelList(String str) {
		LinkedList<StationByIdModel> modelList=null;
		try {
			JSONObject obj=new JSONObject(str);
			JSONObject message_obj=obj.getJSONObject("message");
			if(message_obj.getString("code").equals("0")) {
				modelList=new LinkedList<StationByIdModel>();
				JSONArray data_array=obj.getJSONArray("data");
				for(int i=0;i<data_array.length();i++) {
					StationByIdModel model=new StationByIdModel();
					model.setId(data_array.getJSONObject(i).getInt("id"));
					model.setLevel(data_array.getJSONObject(i).getInt("level"));
					model.setStation_id(data_array.getJSONObject(i).getInt("station_id"));
					modelList.add(model);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return modelList;
	}
	
	public static ArrayList<CurrentBusModel> getCurrentBusList(String str) {
		ArrayList<CurrentBusModel> modelList=null;
		try {
			JSONObject obj=new JSONObject(str);
			JSONObject message_obj=obj.getJSONObject("message");
			if(message_obj.getString("code").equals("0")) {
				modelList=new ArrayList<CurrentBusModel>();
				JSONArray data_array=obj.getJSONArray("data");
				for(int i=0;i<data_array.length();i++) {
					CurrentBusModel model=new CurrentBusModel();
					model.setBusId(data_array.getJSONObject(i).getInt("busId"));
					model.setBusLat(data_array.getJSONObject(i).getDouble("busLat"));
					model.setBusLong(data_array.getJSONObject(i).getDouble("busLong"));
					model.setBusSpeed(data_array.getJSONObject(i).getDouble("busSpeed"));
					model.setCurrentLevel(data_array.getJSONObject(i).getInt("currentLevel"));
					model.setDistance(data_array.getJSONObject(i).getDouble("Distance"));
					model.setReloadTime(data_array.getJSONObject(i).getInt("reloadTime"));
					model.setUploadTime(data_array.getJSONObject(i).getLong("uploadTime"));
					modelList.add(model);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return modelList;
	}
	
	public static ArrayList<Integer> getStationDetailLines(String str) {
		ArrayList<Integer> modelList=null;
		try {
			JSONObject obj=new JSONObject(str);
			JSONObject message_obj=obj.getJSONObject("message");
			if(message_obj.getString("code").equals("0")) {
				modelList=new ArrayList<Integer>();
				JSONObject data_obj=obj.getJSONObject("data");
				JSONArray line_array=new JSONArray(data_obj.getString("line"));
				for(int i=0;i<line_array.length();i++) {
					JSONObject array_obj=line_array.getJSONObject(i);
					modelList.add(array_obj.getInt("line_id"));
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return modelList;
	}

}

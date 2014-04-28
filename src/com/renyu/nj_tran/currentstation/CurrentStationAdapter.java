package com.renyu.nj_tran.currentstation;

import java.util.ArrayList;

import com.renyu.nj_tran.R;
import com.renyu.nj_tran.busresult.ResultActivity;
import com.renyu.nj_tran.busresult.ResultJnActivity;
import com.renyu.nj_tran.commons.CommonUtils;
import com.renyu.nj_tran.commons.Conn;
import com.renyu.nj_tran.model.ArroundStationModel;
import com.renyu.nj_tran.model.BusLineModel;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class CurrentStationAdapter extends BaseAdapter {
	
	ArrayList<ArroundStationModel> modelLists=null;
	Context context=null;
	
	public CurrentStationAdapter(Context context, ArrayList<ArroundStationModel> modelLists) {
		this.context=context;
		this.modelLists=modelLists;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return modelLists.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return modelLists.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final int pos_=position;
		CurrentStationHolder holder=null;
		if(convertView==null) {
			convertView=LayoutInflater.from(context).inflate(R.layout.adapter_arroundstation, null);
			holder=new CurrentStationHolder();
			holder.a_s_name=(TextView) convertView.findViewById(R.id.a_s_name);
			holder.a_s_libs_layout=(LinearLayout) convertView.findViewById(R.id.a_s_libs_layout);
			holder.a_s_distance=(TextView) convertView.findViewById(R.id.a_s_distance);
			convertView.setTag(holder);
		}
		else {
			holder=(CurrentStationHolder) convertView.getTag();
		}
		holder.a_s_name.setText(modelLists.get(position).getName());
		holder.a_s_distance.setText("距离当前位置约"+modelLists.get(position).getDistance()+"米");
		holder.a_s_libs_layout.removeAllViews();
		LinearLayout currentLayout=null;
		for(int j=0;j<modelLists.get(position).getLids_list().size();j++) {
			final int k=j;
			if(j%3==0) {
				View view_child_layout=LayoutInflater.from(context).inflate(R.layout.view_arroundstation_layout, null);
				LinearLayout child_layout=(LinearLayout) view_child_layout.findViewById(R.id.child_layout);
				currentLayout=child_layout;
				holder.a_s_libs_layout.addView(currentLayout);
			}
			View view_child_text=LayoutInflater.from(context).inflate(R.layout.view_arroundstation_textview, null);
			view_child_text.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					ArrayList<BusLineModel> busLineModelList=null;
					if(CommonUtils.isJnFromNJDB(modelLists.get(pos_).getLids_list().get(k))!=null) {
						String result=CommonUtils.isJnFromNJDB(modelLists.get(pos_).getLids_list().get(k));
						busLineModelList=Conn.getInstance(context.getApplicationContext()).getJNTranInfoDirect(result, true);
					}
					else {
						busLineModelList=Conn.getInstance(context.getApplicationContext()).getTranInfoDirect(modelLists.get(pos_).getLids_list().get(k), true);
					}	
					String[] array=new String[busLineModelList.size()];
					for(int i=0;i<busLineModelList.size();i++) {
						array[i]=busLineModelList.get(i).getLine_name()+" "+busLineModelList.get(i).getStart_from()+"-->"+busLineModelList.get(i).getEnd_location();
					}
					final ArrayList<BusLineModel> busLineModelListTemp=busLineModelList;
					new AlertDialog.Builder(context).setTitle("请您选择该车次线路").setItems(array, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							Intent intent=null;
							Bundle bundle=new Bundle();
							if(CommonUtils.isJnFromNJDB(modelLists.get(pos_).getLids_list().get(k))!=null) {
								intent=new Intent(context, ResultJnActivity.class);
								bundle.putString("lineName", busLineModelListTemp.get(which).getLine_name());
								if(modelLists.get(pos_).getName().indexOf("[")!=-1) {
									bundle.putString("stationName", modelLists.get(pos_).getName().substring(0, modelLists.get(pos_).getName().indexOf("[")));
								}
								else if(modelLists.get(pos_).getName().indexOf("(")!=-1) {
									bundle.putString("stationName", modelLists.get(pos_).getName().substring(0, modelLists.get(pos_).getName().indexOf("(")));	
								}
								else {
									bundle.putString("stationName", modelLists.get(pos_).getName());
								}
								bundle.putInt("lineId", busLineModelListTemp.get(which).getId());
								bundle.putString("inDown", busLineModelListTemp.get(which).getLine_code());								
							}
							else {
								intent=new Intent(context, ResultActivity.class);
								bundle.putString("lineName", busLineModelListTemp.get(which).getLine_name());
								bundle.putString("stationName", modelLists.get(pos_).getName());
								bundle.putInt("lineId", busLineModelListTemp.get(which).getId());
							}
							intent.putExtras(bundle);
							context.startActivity(intent);
						}}).show();
				}
			});
			TextView child_text=(TextView) view_child_text.findViewById(R.id.child_text);
			child_text.setText(modelLists.get(position).getLids_list().get(j));
			LayoutParams params=new LayoutParams((int) CommonUtils.getDisplayParams(context)*80, LayoutParams.WRAP_CONTENT);
			params.leftMargin=10;
			params.rightMargin=10;
			params.topMargin=6;
			params.bottomMargin=6;
			currentLayout.addView(view_child_text, params);
		}
		return convertView;
	}

}

class CurrentStationHolder {
	TextView a_s_name=null;
	TextView a_s_distance=null;
	LinearLayout a_s_libs_layout=null;
}

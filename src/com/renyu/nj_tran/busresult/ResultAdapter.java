package com.renyu.nj_tran.busresult;

import java.util.ArrayList;
import java.util.LinkedList;

import com.renyu.nj_tran.R;
import com.renyu.nj_tran.model.CurrentBusModel;
import com.renyu.nj_tran.model.StationsModel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ResultAdapter extends BaseAdapter {
	
	Context context=null;
	LinkedList<StationsModel> modelListNew=null;
	ArrayList<CurrentBusModel> modelListBus=null;
	String stationName="";
	int stationPos=-1;
	
	public ResultAdapter(Context context, LinkedList<StationsModel> modelListNew, ArrayList<CurrentBusModel> modelListBus, String stationName) {
		this.context=context;
		this.modelListNew=modelListNew;
		this.modelListBus=modelListBus;
		this.stationName=stationName;
	}
	
	public void getStationPos() {
		for(int i=0;i<modelListNew.size();i++) {
			if(modelListNew.get(i).getStation_name().equals(stationName)) {
				stationPos=i;
			}
		}		
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return modelListNew.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return modelListNew.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ResultAdapterHolder holder=null;
		if(convertView==null) {
			convertView=LayoutInflater.from(context).inflate(R.layout.adapter_result, null, false);
			holder=new ResultAdapterHolder();
			holder.result_name=(TextView) convertView.findViewById(R.id.result_name);
			holder.result_extra=(TextView) convertView.findViewById(R.id.result_extra);
			holder.result_extra_layout=(RelativeLayout) convertView.findViewById(R.id.result_extra_layout);
			holder.result_me=(ImageView) convertView.findViewById(R.id.result_me);
			holder.icon_start=(TextView) convertView.findViewById(R.id.icon_start);
			holder.icon_stop=(TextView) convertView.findViewById(R.id.icon_stop);
			convertView.setTag(holder);
		}
		else {
			holder=(ResultAdapterHolder) convertView.getTag();
		}
		holder.result_name.setText(modelListNew.get(position).getStation_name());
		if(stationPos<position&&stationPos!=-1) {
			holder.result_name.setTextColor(context.getResources().getColor(R.color.after_station_color));
		}
		else {
			holder.result_name.setTextColor(context.getResources().getColor(R.color.normal_station_color));
		}
		ArrayList<CurrentBusModel> busModelList=isContains(position+1);
		if(busModelList!=null) {			
			String showInfo="";
			holder.result_extra_layout.setVisibility(View.VISIBLE);
			for(int i=0;i<busModelList.size();i++) {
				showInfo+="距离下一站"+modelListNew.get(position).getStation_name()+"约"+busModelList.get(i).getDistance()+"米";
				if(i!=busModelList.size()-1) {
					showInfo+="\n";
				}
			}		
			holder.result_extra.setText(showInfo);
		}
		else {
			holder.result_extra_layout.setVisibility(View.GONE);
		}
		if(stationName.equals(modelListNew.get(position).getStation_name())) {
			holder.result_me.setVisibility(View.VISIBLE);
		}
		else {
			holder.result_me.setVisibility(View.INVISIBLE);
		}
		if(position==0) {
			holder.icon_start.setVisibility(View.VISIBLE);
		}
		else {
			holder.icon_start.setVisibility(View.GONE);
		}
		if(position==modelListNew.size()-1) {
			holder.icon_stop.setVisibility(View.VISIBLE);
		}
		else {
			holder.icon_stop.setVisibility(View.GONE);
		}
		return convertView;
	}
	
	private ArrayList<CurrentBusModel> isContains(int position) {
		ArrayList<CurrentBusModel> modelList=null;
		for(int i=0;i<modelListBus.size();i++) {
			if(modelListBus.get(i).getCurrentLevel()==position) {
				if(modelList==null) {
					modelList=new ArrayList<CurrentBusModel>();
				}
				modelList.add(modelListBus.get(i));
			}
		}
		return modelList;
	}

}

class ResultAdapterHolder {
	TextView result_name=null;
	TextView result_extra=null;
	RelativeLayout result_extra_layout=null;
	ImageView result_me=null;
	TextView icon_start=null;
	TextView icon_stop=null;
}

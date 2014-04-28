package com.renyu.nj_tran.busresult;

import java.util.ArrayList;
import java.util.LinkedList;

import com.renyu.nj_tran.R;
import com.renyu.nj_tran.model.CurrentJnBusModel;
import com.renyu.nj_tran.model.StationByIdModel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ResultJnAdapter extends BaseAdapter {
	
	Context context=null;
	LinkedList<StationByIdModel> modelListsStation=null;
	ArrayList<CurrentJnBusModel> modelListBus=null;
	int stationNo=0;
	String stationName="";
	
	public ResultJnAdapter(Context context, LinkedList<StationByIdModel> modelListsStation, ArrayList<CurrentJnBusModel> modelListBus, int stationNo, String stationName) {
		this.context=context;
		this.modelListsStation=modelListsStation;
		this.modelListBus=modelListBus;
		this.stationNo=stationNo;
		this.stationName=stationName;
	}
	
	public void setStation(int stationNo, String stationName) {
		this.stationNo=stationNo;
		this.stationName=stationName;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return modelListsStation.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return modelListsStation.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ResultJnAdapterHolder holder=null;
		if(convertView==null) {
			convertView=LayoutInflater.from(context).inflate(R.layout.adapter_result, null);
			holder=new ResultJnAdapterHolder();
			holder.result_name=(TextView) convertView.findViewById(R.id.result_name);
			holder.result_extra=(TextView) convertView.findViewById(R.id.result_extra);
			holder.result_extra_layout=(RelativeLayout) convertView.findViewById(R.id.result_extra_layout);
			holder.result_me=(ImageView) convertView.findViewById(R.id.result_me);
			holder.icon_start=(TextView) convertView.findViewById(R.id.icon_start);
			holder.icon_stop=(TextView) convertView.findViewById(R.id.icon_stop);
			convertView.setTag(holder);
		}
		else {
			holder=(ResultJnAdapterHolder) convertView.getTag();
		}
		holder.result_name.setText(modelListsStation.get(position).getName());
		if(stationNo<(position+1)&&stationNo!=0) {
			holder.result_name.setTextColor(context.getResources().getColor(R.color.after_station_color));
		}
		else {
			holder.result_name.setTextColor(context.getResources().getColor(R.color.normal_station_color));
		}
		ArrayList<CurrentJnBusModel> showExtraLists=new ArrayList<CurrentJnBusModel>();
		for(int i=0;i<modelListBus.size();i++) {
			if(modelListBus.get(i).getStationNo()==0) {
				
			}
			else if((stationNo-(modelListBus.get(i).getStationNo()-1))==modelListsStation.get(position).getId()) {
				showExtraLists.add(modelListBus.get(i));
			}
		}
		if(showExtraLists.size()>0) {
			String str="";
			for(int i=0;i<showExtraLists.size();i++) {
				str+="离"+(stationName.equals("")?"终点":stationName)+"站还有"+showExtraLists.get(i).getStationNo()+"站路"+showExtraLists.get(i).getDis();
				if(i!=showExtraLists.size()-1) {
					str+="\n";
				}
			}
			holder.result_extra.setText(str);
			holder.result_extra_layout.setVisibility(View.VISIBLE);
		}
		else {
			holder.result_extra.setText("");
			holder.result_extra_layout.setVisibility(View.GONE);
		}
		if(stationName.equals(modelListsStation.get(position).getName())) {
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
		if(position==modelListsStation.size()-1) {
			holder.icon_stop.setVisibility(View.VISIBLE);
		}
		else {
			holder.icon_stop.setVisibility(View.GONE);
		}
		return convertView;
	}

}

class ResultJnAdapterHolder {
	TextView result_name=null;
	TextView result_extra=null;
	RelativeLayout result_extra_layout=null;
	ImageView result_me=null;
	TextView icon_start=null;
	TextView icon_stop=null;
}

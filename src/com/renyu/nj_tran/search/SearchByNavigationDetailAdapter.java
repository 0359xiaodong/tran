package com.renyu.nj_tran.search;

import java.util.ArrayList;

import com.renyu.nj_tran.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SearchByNavigationDetailAdapter extends BaseAdapter {
	
	ArrayList<String> str_route=null;
	Context context=null;
	
	public SearchByNavigationDetailAdapter(Context context, ArrayList<String> str_route) {
		this.context=context;
		this.str_route=str_route;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return str_route.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return str_route.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		SearchByNavigationDetailHolder holder=null;
		if(convertView==null) {
			convertView=LayoutInflater.from(context).inflate(R.layout.adapter_searchbynavigationdetail, null);
			holder=new SearchByNavigationDetailHolder();
			holder.navigation_detail_bottom_show=(ImageView) convertView.findViewById(R.id.navigation_detail_bottom_show);
			holder.navigation_detail_top_show=(ImageView) convertView.findViewById(R.id.navigation_detail_top_show);
			holder.navigation_detail_name=(TextView) convertView.findViewById(R.id.navigation_detail_name);
			holder.navigation_detail_type=(ImageView) convertView.findViewById(R.id.navigation_detail_type);
			convertView.setTag(holder);
		}
		else {
			holder=(SearchByNavigationDetailHolder) convertView.getTag();
		}
		if(str_route.get(position).split("&")[0].equals("1")) {
			holder.navigation_detail_type.setImageResource(R.drawable.common_topbar_route_foot_normal);
		}
		else if(str_route.get(position).split("&")[0].equals("2")) {
			holder.navigation_detail_type.setImageResource(R.drawable.common_topbar_route_bus_normal);
		}
		holder.navigation_detail_name.setText(str_route.get(position).split("&")[1]);
		if(position==0) {
			holder.navigation_detail_top_show.setVisibility(View.VISIBLE);
			holder.navigation_detail_bottom_show.setVisibility(View.GONE);
		}
		else if(position==str_route.size()-1) {
			holder.navigation_detail_top_show.setVisibility(View.GONE);
			holder.navigation_detail_bottom_show.setVisibility(View.VISIBLE);
		}
		else {
			holder.navigation_detail_top_show.setVisibility(View.GONE);
			holder.navigation_detail_bottom_show.setVisibility(View.GONE);
		}
		return convertView;
	}

}

class SearchByNavigationDetailHolder {
	ImageView navigation_detail_top_show=null;
	ImageView navigation_detail_type=null;
	TextView navigation_detail_name=null;
	ImageView navigation_detail_bottom_show=null;
}

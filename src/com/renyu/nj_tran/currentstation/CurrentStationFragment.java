package com.renyu.nj_tran.currentstation;

import java.util.ArrayList;
import java.util.HashMap;

import org.mariotaku.refreshnow.widget.OnRefreshListener;
import org.mariotaku.refreshnow.widget.RefreshMode;
import org.mariotaku.refreshnow.widget.RefreshNowConfig;
import org.mariotaku.refreshnow.widget.RefreshNowListView;

import com.renyu.nj_tran.R;
import com.renyu.nj_tran.commons.CommonUtils;
import com.renyu.nj_tran.model.ArroundStationModel;
import com.renyu.nj_tran.model.JsonParse;
import com.renyu.nj_tran.service.GPSService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class CurrentStationFragment extends Fragment implements OnRefreshListener {
	
	View view=null;
	RefreshNowListView tab_mainbody_list=null;
	CurrentStationAdapter adapter=null;
	TextView tab_mainbody_location=null;
	
	LinearLayout line_right=null;
	TextView title_right=null;
	TextView title_name=null;
	ProgressBar title_pb=null;
	
	ArrayList<ArroundStationModel> modelList=null;
	double geoLat=0;
	double geoLng=0;
	String desp="";
	//是否正在刷新
	boolean isRefresh=false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if(view==null) {
			modelList=new ArrayList<ArroundStationModel>();
			
			view=LayoutInflater.from(getActivity()).inflate(R.layout.fragment_currentstation, null);
			
			title_name=(TextView) view.findViewById(R.id.title_name);
			title_name.setText("周围站点详情");
			title_right=(TextView) view.findViewById(R.id.title_right);
			title_right.setText("地图");
			title_right.setOnClickListener(new TextView.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent=new Intent(getActivity(), CurrentStationMapActivity.class);
					Bundle bundle=new Bundle();
					bundle.putDouble("geoLat", geoLat);
					bundle.putDouble("geoLng", geoLng);
					bundle.putString("desp", desp);
					bundle.putSerializable("modelList", modelList);
					intent.putExtras(bundle);
					startActivity(intent);
				}});
			line_right=(LinearLayout) view.findViewById(R.id.line_right);
			title_pb=(ProgressBar) view.findViewById(R.id.title_pb);
			
			tab_mainbody_list=(RefreshNowListView) view.findViewById(R.id.tab_mainbody_list);
			tab_mainbody_list.setRefreshMode(RefreshMode.START);
			tab_mainbody_list.setOnRefreshListener(this);
			tab_mainbody_list.setRefreshIndicatorView(view.findViewById(R.id.tab_mainbody_progress));
			RefreshNowConfig.Builder cb = new RefreshNowConfig.Builder(getActivity());
			cb.maxOverScrollDistance(150);
			cb.minPullDivisor(1);
			cb.extraPullDivisor(1);
			tab_mainbody_list.setConfig(cb.build());
			adapter=new CurrentStationAdapter(getActivity(), modelList);
			tab_mainbody_list.setAdapter(adapter);
			tab_mainbody_location=(TextView) view.findViewById(R.id.tab_mainbody_location);
			
			IntentFilter filter=new IntentFilter();
			filter.addAction(GPSService.GPSACTION);
			getActivity().registerReceiver(receiver, filter);
			
			Intent intent=new Intent(getActivity(), GPSService.class);
			getActivity().startService(intent);
			title_pb.setVisibility(View.VISIBLE);
		}
		ViewGroup parent=(ViewGroup) view.getParent();  
		if(parent!=null) {  
			parent.removeView(view);  
		} 
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}
	
	/**
	 * 获取周围车站信息
	 * @param geoLng
	 * @param geoLat
	 */
	public void getArroundStation(final double geoLng, final double geoLat) {
		isRefresh=true;
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				title_pb.setVisibility(View.INVISIBLE);
				isRefresh=false;
				tab_mainbody_list.setRefreshComplete();
				modelList.clear();
				if(msg.obj!=null) {
					ArrayList<ArroundStationModel> modelListTemp=JsonParse.getArroundStationModelList(msg.obj.toString());
					if(modelListTemp!=null) {						
						modelList.addAll(modelListTemp);
						adapter.notifyDataSetChanged();

						title_right.setVisibility(View.VISIBLE);
						line_right.setVisibility(View.VISIBLE);
					}					
				}
				else {
					Toast.makeText(getActivity(), "获取周围站点失败", 3000).show();
				}
				adapter.notifyDataSetChanged();
			}
		};
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				HashMap<String, String> map=new HashMap<String, String>();
				Message m=new Message();
				m.obj=CommonUtils.getWebData(map, "http://trafficomm.jstv.com/smartBus/Module=BusHelper/Controller=BusInfo/Action=GetArroundStation/range=500/lat="+geoLat+"/long="+geoLng+"/mid=1/key=078f2995225995848592c458d414f810");
				handler.sendMessage(m);
			}
		}).start();
	}
	
	BroadcastReceiver receiver=new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals(GPSService.GPSACTION)) {
				tab_mainbody_location.setText(intent.getExtras().getString("desc"));
				getArroundStation(intent.getExtras().getDouble("geoLng"), intent.getExtras().getDouble("geoLat"));
				geoLat=intent.getExtras().getDouble("geoLat");
				geoLng=intent.getExtras().getDouble("geoLng");
				desp=intent.getExtras().getString("desc");
			}
		}};
	
		public void onDestroy() {
			super.onDestroy();
			getActivity().unregisterReceiver(receiver);
		}

		@Override
		public void onRefreshComplete() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onRefreshStart(RefreshMode mode) {
			// TODO Auto-generated method stub
			if(!isRefresh&&geoLat!=0&&geoLng!=0) {
				Intent intent=new Intent(getActivity(), GPSService.class);
				getActivity().startService(intent);
				title_pb.setVisibility(View.VISIBLE);	
				title_right.setVisibility(View.GONE);
				line_right.setVisibility(View.GONE);	
				tab_mainbody_location.setText("正在定位中。。。");
			}
			else {
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						tab_mainbody_list.setRefreshComplete();
					}
				}, 100);
			}
		};
}

package com.renyu.nj_tran.busresult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import org.mariotaku.refreshnow.widget.OnRefreshListener;
import org.mariotaku.refreshnow.widget.RefreshMode;
import org.mariotaku.refreshnow.widget.RefreshNowConfig;
import org.mariotaku.refreshnow.widget.RefreshNowListView;

import com.baidu.mobstat.StatService;
import com.renyu.nj_tran.R;
import com.renyu.nj_tran.commons.CommonUtils;
import com.renyu.nj_tran.commons.Conn;
import com.renyu.nj_tran.model.CurrentBusModel;
import com.renyu.nj_tran.model.StationByIdModel;
import com.renyu.nj_tran.model.JsonParse;
import com.renyu.nj_tran.model.StationsModel;
import com.renyu.nj_tran.search.SearchByRotateMapActivity;
import com.renyu.nj_tran.search.SearchByStationNameActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ResultActivity extends Activity implements OnRefreshListener {
	
	LinearLayout line_left=null;
	TextView title_name=null;
	ImageView title_left=null;
	LinearLayout line_right=null;
	TextView title_right=null;
	ProgressBar title_pb=null;
	
	RefreshNowListView result_list=null;
	ResultAdapter adapter=null;
	
	LinkedList<StationsModel> modelListStation=null;
	ArrayList<CurrentBusModel> modelListBus=null;	
	String destination="";
	//是否需要提示
	boolean isNeedTip=false;
	//是否已经找到当前车站position
	boolean isGetCurrentPosition=false;
	public final static String actionRefresh="mapRefresh";
	
	Handler handler_main=new Handler();
	
	Runnable runnable_main=new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(!isBusRefresh) {
				GetCurrentBus(destination);
			}
		}
	};
	
	//是否正在刷新当前车辆信息
	boolean isBusRefresh=false;
	//是否正在刷新
	boolean isRefresh=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_result);
		
		modelListStation=new LinkedList<StationsModel>();
		modelListBus=new ArrayList<CurrentBusModel>();
		
		init();
		
		getstationbylid(getIntent().getExtras().getInt("lineId"));
		
	}
	
	private void init() {
		line_left=(LinearLayout) findViewById(R.id.line_left);
		line_left.setVisibility(View.VISIBLE);
		title_name=(TextView) findViewById(R.id.title_name);
		title_name.setText(getIntent().getExtras().getString("lineName")+"实时动态");
		title_left=(ImageView) findViewById(R.id.title_left);
		title_left.setImageResource(R.drawable.icon_back);
		title_left.setVisibility(View.VISIBLE);
		title_left.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});
		title_right=(TextView) findViewById(R.id.title_right);
		title_right.setText("地图");
		title_right.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(ResultActivity.this, SearchByRotateMapActivity.class);
				Bundle bundle=new Bundle();
				bundle.putSerializable("modelListStation", modelListStation);
				bundle.putSerializable("modelListBus", modelListBus);
				bundle.putString("lineName", getIntent().getExtras().getString("lineName"));
				intent.putExtras(bundle);
				startActivity(intent);
			}});
		line_right=(LinearLayout) findViewById(R.id.line_right);
		title_pb=(ProgressBar) findViewById(R.id.title_pb);
		
		result_list=(RefreshNowListView) findViewById(R.id.result_list);
		result_list.setRefreshMode(RefreshMode.START);
		result_list.setOnRefreshListener(this);
		result_list.setRefreshIndicatorView(findViewById(R.id.result_list_progress));
		RefreshNowConfig.Builder cb = new RefreshNowConfig.Builder(this);
		cb.maxOverScrollDistance(150);
		cb.minPullDivisor(1);
		cb.extraPullDivisor(1);
		result_list.setConfig(cb.build());
		adapter=new ResultAdapter(ResultActivity.this, modelListStation, modelListBus, getIntent().getExtras().getString("stationName"));
		result_list.setAdapter(adapter);
		result_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(ResultActivity.this, SearchByStationNameActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("sid", ""+modelListStation.get(position).getId());
				bundle.putString("stationName", ""+modelListStation.get(position).getStation_name());
				bundle.putDouble("lat", modelListStation.get(position).getMap_station_lat());
				bundle.putDouble("long", modelListStation.get(position).getMap_station_long());
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}
	
	/**
	 * 获取当前公交线路
	 * @param id
	 */
	public void getstationbylid(final int id) {
		isRefresh=true;
		title_pb.setVisibility(View.VISIBLE);
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				title_pb.setVisibility(View.INVISIBLE);
				isRefresh=false;
				result_list.setRefreshComplete();
				if(msg.obj!=null) {
					LinkedList<StationByIdModel> modelListOld=JsonParse.getGetStationByIdModelList(msg.obj.toString());
					if(modelListOld!=null) {
						HashMap<String, StationsModel> map=Conn.getInstance(getApplicationContext()).getStationsModelList(modelListOld);	
						if(map.size()==0) {
							Toast.makeText(ResultActivity.this, "暂无相关公交站台信息", 3000).show();
						}
						else {
							for(int i=0;i<modelListOld.size();i++) {
								if(map.get(""+modelListOld.get(i).getStation_id())!=null) {
									modelListStation.add(map.get(""+modelListOld.get(i).getStation_id()));						
								}
							}
							if(!isGetCurrentPosition) {
								adapter.getStationPos();
								isGetCurrentPosition=true;
							}
							adapter.notifyDataSetChanged();
							destination=modelListStation.get(modelListStation.size()-1).getStation_name();
							handler_main.post(runnable_main);
						}
						line_right.setVisibility(View.VISIBLE);
						title_right.setVisibility(View.VISIBLE);
					}					
				}
				else {
					Toast.makeText(ResultActivity.this, "获取公交站台信息失败", 3000).show();
				}
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				HashMap<String, String> map=new HashMap<String, String>();
				Message m=new Message();
				m.obj=CommonUtils.getWebData(map, "http://trafficomm.jstv.com/smartBus/Module=BusHelper/Controller=BusInfo/Action=getstationbylid/lid="+id+"/key="+CommonUtils.changeOldValue(""+id, "getstationbylid"));
				handler.sendMessage(m);
			}}).start();
	}
	
	/**
	 * 获取汽车动态信息
	 */
	public void GetCurrentBus(final String destination) {
		isBusRefresh=true;
		title_pb.setVisibility(View.VISIBLE);
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				title_pb.setVisibility(View.INVISIBLE);
				if(msg.obj!=null) {
					modelListBus.clear();
					ArrayList<CurrentBusModel> modelListBusTemp=JsonParse.getCurrentBusList(msg.obj.toString());
					if(modelListBusTemp!=null&&modelListBusTemp.size()>0) {
						modelListBus.addAll(JsonParse.getCurrentBusList(msg.obj.toString()));
						adapter.notifyDataSetChanged();
						
						isNeedTip=false;
						
						//发送地图刷新广播
						Intent intent=new Intent();
						intent.setAction(actionRefresh);
						Bundle bundle=new Bundle();
						bundle.putSerializable("modelListBus", modelListBus);
						intent.putExtras(bundle);
						sendBroadcast(intent);
					}
					else {
						if(!isNeedTip) {
							Toast.makeText(ResultActivity.this, "暂未获取到汽车位置信息", 3000).show();
							isNeedTip=true;
						}
					}
				}
				isBusRefresh=false;
				handler_main.postDelayed(runnable_main, 10000);
			}
		};
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				HashMap<String, String> map=new HashMap<String, String>();
				Message m=new Message();
				System.out.println("http://trafficomm.jstv.com/smartBus/Module=BusHelper/Controller=BusInfo/Action=GetCurrentBus/LineName=/StationName="+Uri.encode(getIntent().getExtras().getString("stationName"))+"/LineName="+getIntent().getExtras().getString("lineName")+"/Destination="+Uri.encode(destination)+"/key=07e1e5b97fc0f50b1bd842ceb1666973");
				m.obj=CommonUtils.getWebData(map, "http://trafficomm.jstv.com/smartBus/Module=BusHelper/Controller=BusInfo/Action=GetCurrentBus/LineName=/StationName="+Uri.encode(getIntent().getExtras().getString("stationName"))+"/LineName="+getIntent().getExtras().getString("lineName")+"/Destination="+Uri.encode(destination)+"/key=07e1e5b97fc0f50b1bd842ceb1666973");
				handler.sendMessage(m);
			}
		}).start();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		handler_main.removeCallbacks(runnable_main);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		StatService.onResume(this);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		StatService.onPause(this);
	}

	@Override
	public void onRefreshComplete() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRefreshStart(RefreshMode mode) {
		// TODO Auto-generated method stub
		if(modelListStation.size()==0) {
			getstationbylid(getIntent().getExtras().getInt("lineId"));
		}
		else {
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					result_list.setRefreshComplete();
				}
			}, 100);
		}
	}
}

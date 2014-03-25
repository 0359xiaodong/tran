package com.renyu.nj_tran.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.mariotaku.refreshnow.widget.OnRefreshListener;
import org.mariotaku.refreshnow.widget.RefreshMode;
import org.mariotaku.refreshnow.widget.RefreshNowConfig;
import org.mariotaku.refreshnow.widget.RefreshNowListView;

import com.baidu.mobstat.StatService;
import com.renyu.nj_tran.R;
import com.renyu.nj_tran.busresult.ResultActivity;
import com.renyu.nj_tran.commons.CommonUtils;
import com.renyu.nj_tran.commons.Conn;
import com.renyu.nj_tran.model.BusLineModel;
import com.renyu.nj_tran.model.JsonParse;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SearchByStationNameActivity extends Activity implements OnRefreshListener {
		
	LinearLayout line_left=null;
	ImageView title_left=null;
	LinearLayout line_right=null;
	TextView title_right=null;
	TextView title_name=null;
	ProgressBar title_pb=null;
	
	RefreshNowListView searchbystation_list=null;
	SimpleAdapter adapter=null;
	
	List<HashMap<String, Object>> lists=null;
	
	//是否正在刷新
	boolean isRefresh=false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_serchbystationname);
		
		lists=new ArrayList<HashMap<String, Object>>();
		
		init();
		getStationDetail();
	}
	
	private void init() {
		title_name=(TextView) findViewById(R.id.title_name);
		title_name.setText("站台详情");
		title_left=(ImageView) findViewById(R.id.title_left);
		title_left.setImageResource(R.drawable.icon_back);
		title_left.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});
		line_left=(LinearLayout) findViewById(R.id.line_left);
		title_right=(TextView) findViewById(R.id.title_right);
		title_right.setText("地图");
		title_right.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(SearchByStationNameActivity.this, SearchByStationNameMapActivity.class);
				Bundle bundle=new Bundle();
				bundle.putDouble("geoLat", getIntent().getExtras().getDouble("lat"));
				bundle.putDouble("geoLng", getIntent().getExtras().getDouble("long"));
				bundle.putString("desp", getIntent().getExtras().getString("stationName"));
				intent.putExtras(bundle);
				startActivity(intent);
			}});
		line_right=(LinearLayout) findViewById(R.id.line_right);
		title_left.setVisibility(View.VISIBLE);
		line_left.setVisibility(View.VISIBLE);
		title_right.setVisibility(View.VISIBLE);
		line_right.setVisibility(View.VISIBLE);
		title_pb=(ProgressBar) findViewById(R.id.title_pb);
		
		searchbystation_list=(RefreshNowListView) findViewById(R.id.searchbystation_list);
		searchbystation_list.setRefreshMode(RefreshMode.START);
		searchbystation_list.setOnRefreshListener(this);
		searchbystation_list.setRefreshIndicatorView(findViewById(R.id.searchbystation_progress));
		RefreshNowConfig.Builder cb = new RefreshNowConfig.Builder(this);
		cb.maxOverScrollDistance(150);
		cb.minPullDivisor(1);
		cb.extraPullDivisor(1);
		searchbystation_list.setConfig(cb.build());
		searchbystation_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				final ArrayList<BusLineModel> modelList=(ArrayList<BusLineModel>) lists.get(position).get("s_s_list");
				String[] lines_array=new String[modelList.size()];
				for(int i=0;i<modelList.size();i++) {
					lines_array[i]=modelList.get(i).getStart_from()+"-->"+modelList.get(i).getEnd_location();
				}
				new AlertDialog.Builder(SearchByStationNameActivity.this).setTitle("请您选择以下线路").setItems(lines_array, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub						
						Intent intent=new Intent(SearchByStationNameActivity.this, ResultActivity.class);
						Bundle bundle=new Bundle();
						bundle.putString("lineName", modelList.get(which).getLine_name());
						bundle.putString("stationName", getIntent().getExtras().getString("stationName"));
						bundle.putInt("lineId", modelList.get(which).getId());
						intent.putExtras(bundle);
						startActivity(intent);
					}}).show();
			}
		});
		adapter=new SimpleAdapter(SearchByStationNameActivity.this, lists, R.layout.adapter_searchstationbyname, new String[]{"s_s_name", "s_s_linename", "s_s_time", "s_s_piao"}, new int[]{R.id.s_s_name, R.id.s_s_linename, R.id.s_s_time, R.id.s_s_piao});
		searchbystation_list.setAdapter(adapter);	
	}
	
	private void getStationDetail() {
		isRefresh=true;
		title_pb.setVisibility(View.VISIBLE);
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				isRefresh=false;
				searchbystation_list.setRefreshComplete();
				title_pb.setVisibility(View.INVISIBLE);
				if(msg.obj!=null) {
					ArrayList<Integer> lines=JsonParse.getStationDetailLines(msg.obj.toString());
					if(lines!=null) {
						lists.clear();
						HashMap<String, ArrayList<BusLineModel>> mapModels=Conn.getInstance(SearchByStationNameActivity.this).getLineMap(lines);
						Iterator<Entry<String, ArrayList<BusLineModel>>> it=mapModels.entrySet().iterator();
						while(it.hasNext()) {
							Entry<String, ArrayList<BusLineModel>> entry=it.next();
							ArrayList<BusLineModel> modelList=entry.getValue();
							
							HashMap<String, Object> map=new HashMap<String, Object>();
							map.put("s_s_name", modelList.get(0).getLine_name());
							map.put("s_s_time", "运营时间："+modelList.get(0).getStart_time()+"-"+modelList.get(0).getEnd_time());
							map.put("s_s_piao", "票价："+modelList.get(0).getPiao());
							map.put("s_s_linename", modelList.get(0).getStart_from()+"-"+modelList.get(0).getEnd_location()+(modelList.size()>1?"  (双向)":"(单向)"));
							map.put("s_s_list", modelList);
							lists.add(map);
						}
						adapter.notifyDataSetChanged();
					}					
				}
				else {
					Toast.makeText(SearchByStationNameActivity.this, "暂未获取到该站点相关信息", 3000).show();
				}
			}
		};
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				HashMap<String, String> map=new HashMap<String, String>();
				Message m=new Message();
				String key=CommonUtils.changeOldValue(getIntent().getExtras().getString("sid"), "StationDetail");
				m.obj=CommonUtils.getWebData(map, "http://q.i5025.com/index.php?app=api&mod=Info&act=StationDetail&mid=1&sid="+getIntent().getExtras().getString("sid")+"&sname=&lat=&long=&DeviceId=&"+key.substring(1));
				handler.sendMessage(m);
			}
		}).start();
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
		if(lists.size()==0) {
			getStationDetail();
		}
		else {
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					searchbystation_list.setRefreshComplete();
				}
			}, 100);
		}
	}
}

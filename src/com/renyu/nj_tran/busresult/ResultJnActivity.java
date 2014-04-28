package com.renyu.nj_tran.busresult;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import org.mariotaku.refreshnow.widget.OnRefreshListener;
import org.mariotaku.refreshnow.widget.RefreshMode;
import org.mariotaku.refreshnow.widget.RefreshNowConfig;
import org.mariotaku.refreshnow.widget.RefreshNowListView;

import com.baidu.mobstat.StatService;
import com.renyu.nj_tran.R;
import com.renyu.nj_tran.TranApplication;
import com.renyu.nj_tran.commons.CommonUtils;
import com.renyu.nj_tran.commons.Conn;
import com.renyu.nj_tran.model.CurrentJnBusModel;
import com.renyu.nj_tran.model.JsonParse;
import com.renyu.nj_tran.model.StationByIdModel;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ResultJnActivity extends Activity implements OnRefreshListener {
	
	LinkedList<StationByIdModel> modelListStation=null;
	ArrayList<CurrentJnBusModel> modelListBus=null;
	
	LinearLayout line_left=null;
	TextView title_name=null;
	ImageView title_left=null;
	ProgressBar title_pb=null;
	
	RefreshNowListView result_list=null;
	ResultJnAdapter adapter=null;
	
	//当前选择位置
	int stationNo=-1;
	String stationName="";
	//是否需要提示
	boolean isNeedTip=false;
	//是否正在刷新当前车辆信息
	boolean isBusRefresh=false;
	//是否正在刷新
	boolean isRefresh=false;
	//android接口是否正常
	boolean isInterfaceOK=true;
	
	Handler handler_main=new Handler();
	
	Runnable runnable_main=new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(!isBusRefresh) {
				if(isInterfaceOK) {
					GetJnCurrentBus();
				}
				else {
					if(stationNo==-1) {
						GetJnCurrentBusOffline(modelListStation.get(modelListStation.size()-1).getName(), 
								modelListStation.get(0).getName()+"→"+modelListStation.get(modelListStation.size()-1).getName(), 
								stationNo,
								Integer.parseInt(((TranApplication) getApplicationContext()).jn_offline_maps.get(getIntent().getExtras().getString("lineName"))), 
								(Integer.parseInt(getIntent().getExtras().getString("inDown"))+1), 
								getIntent().getExtras().getString("lineName"));
					}
					else {
						GetJnCurrentBusOffline(stationName, 
								modelListStation.get(0).getName()+"→"+modelListStation.get(modelListStation.size()-1).getName(), 
								stationNo, 
								Integer.parseInt(((TranApplication) getApplicationContext()).jn_offline_maps.get(getIntent().getExtras().getString("lineName"))), 
								(Integer.parseInt(getIntent().getExtras().getString("inDown"))+1), 
								getIntent().getExtras().getString("lineName"));
					}
				}				
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_result);
		
		modelListStation=new LinkedList<StationByIdModel>();
		modelListBus=new ArrayList<CurrentJnBusModel>(); 
		stationName=getIntent().getExtras().getString("stationName")==null?"":getIntent().getExtras().getString("stationName");
		if(!stationName.equals("")) {
			stationNo=Conn.getInstance(ResultJnActivity.this).getStationId(stationName, Integer.parseInt(getIntent().getExtras().getString("inDown")), getIntent().getExtras().getInt("lineId"));
		}
		
		init();
		
		getJnstationbylid();
	}
	
	private void init() {
		line_left=(LinearLayout) findViewById(R.id.line_left);
		line_left.setVisibility(View.VISIBLE);
		title_name=(TextView) findViewById(R.id.title_name);
		title_name.setText(getIntent().getExtras().getString("lineName")+"线路实时动态");
		title_left=(ImageView) findViewById(R.id.title_left);
		title_left.setImageResource(R.drawable.icon_back);
		title_left.setVisibility(View.VISIBLE);
		title_left.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});
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
		adapter=new ResultJnAdapter(ResultJnActivity.this, modelListStation, modelListBus, stationNo, stationName);
		result_list.setAdapter(adapter);
	}
	
	private void getJnstationbylid() {
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
					LinkedList<StationByIdModel> modelListsTemp=JsonParse.getGetJNStationByIdModelList(msg.obj.toString());
					if(modelListsTemp!=null&&modelListsTemp.size()>0) {
						modelListStation.clear();
						modelListStation.addAll(modelListsTemp);
						adapter.notifyDataSetChanged();
						
						if(stationNo==-1) {
							stationNo=modelListStation.get(modelListStation.size()-1).getId();
							stationName="";
						}
						adapter.setStation(stationNo, stationName);
						GetJnCurrentBus();
					}
					else {
						isInterfaceOK=false;
						System.out.println("加载离线");
						modelListStation.clear();
						modelListStation.addAll(Conn.getInstance(ResultJnActivity.this).getOfflineStationModel(Integer.parseInt(getIntent().getExtras().getString("inDown")), getIntent().getExtras().getInt("lineId")));
						adapter.notifyDataSetChanged();
						
						//缓存的请求id为空得时候要获取一遍
						if(((TranApplication) getApplicationContext()).jn_offline_maps==null) {
							GetJnBusLineOffline();
						}
						else {
							getParam();
						}
					}
				}
				else {
					Toast.makeText(ResultJnActivity.this, "获取公交站台信息失败", 3000).show();
				}
			}
		};
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message m=new Message();
				HashMap<String, String> map=new HashMap<String, String>();
				m.obj=CommonUtils.getWebData(map, "http://112.2.33.3:7106/BusAndroid/android.do?command=toSta&lineId="+getIntent().getExtras().getInt("lineId")+"&inDown="+(Integer.parseInt(getIntent().getExtras().getString("inDown"))+1));
				handler.sendMessage(m);
			}
			
		}).start();
	}
	
	private void GetJnCurrentBus() {
		isBusRefresh=true;
		title_pb.setVisibility(View.VISIBLE);
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				title_pb.setVisibility(View.INVISIBLE);
				if(msg.obj!=null) {
					ArrayList<CurrentJnBusModel> modelListBusTemp=JsonParse.getCurrentJnBusList(msg.obj.toString());
					if(modelListBusTemp!=null&&modelListBusTemp.size()>0) {
						modelListBus.clear();
						modelListBus.addAll(modelListBusTemp);
						adapter.notifyDataSetChanged();
						handler_main.postDelayed(runnable_main, 10000);
					}
					else {
						Toast.makeText(ResultJnActivity.this, "暂未获取到汽车位置信息", 3000).show();
					}
				}
				isBusRefresh=false;
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message m=new Message();
				HashMap<String, String> map=new HashMap<String, String>();
				m.obj=CommonUtils.getWebData(map, "http://112.2.33.3:7106/BusAndroid/android.do?command=toDis&lineId="+getIntent().getExtras().getInt("lineId")+"&inDown="+(Integer.parseInt(getIntent().getExtras().getString("inDown"))+1)+"&stationNo="+modelListStation.get(modelListStation.size()-1).getId());
				handler.sendMessage(m);
			}
			
		}).start();
	}
	
	private void GetJnCurrentBusOffline(final String stationName, final String strInfo, final int stationNo, final int lineId, final int inDown, final String lineName) {
		isBusRefresh=true;
		title_pb.setVisibility(View.VISIBLE);
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				title_pb.setVisibility(View.INVISIBLE);
				if(!msg.obj.toString().equals("")) {
					if(msg.obj.toString().indexOf("线路上暂无车辆")!=-1) {
						Toast.makeText(ResultJnActivity.this, "暂未获取到汽车位置信息", 3000).show();
						return;
					}
					int start=msg.obj.toString().indexOf("<span");
					int end=msg.obj.toString().lastIndexOf("<br/>")+"<br/>".length();
					String result=msg.obj.toString().substring(start, end);
					String[] results=result.split("<br/>");
					ArrayList<CurrentJnBusModel> modelLists=new ArrayList<CurrentJnBusModel>();
					for(int i=0;i<results.length;i++) {
						CurrentJnBusModel model=new CurrentJnBusModel();
						//System.out.println(results[i]);
						
						//获取stationNo
						int strStart1=results[i].indexOf("距本站<span class=\"red\">");
						int strEnd1=results[i].substring(strStart1).indexOf("</span>");
						model.setStationNo(Integer.parseInt(results[i].substring(strStart1).substring("距本站<span class=\"red\">".length(), strEnd1)));
						
						//获取dis
						int strStart2=results[i].lastIndexOf(")");
						model.setDis(results[i].substring(strStart2+1));
						
						//获取stationName
						int strStart3=results[i].indexOf("(")+1;
						int strEnd3=results[i].lastIndexOf(")");
						model.setStationName(results[i].substring(strStart3, strEnd3).substring(
								results[i].substring(strStart3, strEnd3).indexOf(">")+1, results[i].substring(strStart3, strEnd3).lastIndexOf("<")));
					
						modelLists.add(model);
					}
					if(modelLists!=null&&modelLists.size()>0) {
						modelListBus.clear();
						modelListBus.addAll(modelLists);
						adapter.notifyDataSetChanged();
					}
					isBusRefresh=false;
					handler_main.postDelayed(runnable_main, 10000);
				}
				else {
					Toast.makeText(ResultJnActivity.this, "暂未获取到汽车位置信息", 3000).show();
				}
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message m=new Message();
				m.obj="";
				try {
					//System.out.println("http://112.2.33.3:7106/wap/line.do?command=toDiss2&stationNo="+stationNo+"&stationName="+Uri.encode(stationName)+"&lineId="+lineId+"&inDown="+inDown+"&lineName="+lineName+"&strInfo="+Uri.encode(strInfo));					
					Source source=new Source(new URL("http://112.2.33.3:7106/wap/line.do?command=toDiss2&stationNo="+stationNo+"&stationName="+Uri.encode(stationName)+"&lineId="+lineId+"&inDown="+inDown+"&lineName="+lineName+"&strInfo="+Uri.encode(strInfo)));
					List<Element> trList=source.getAllElements(HTMLElementName.DIV);
					for(int i=0;i<trList.size();i++) {
						Element ele=trList.get(i);						
						if(ele.getContent().toString().indexOf("stationNo="+stationNo)!=-1) {
							if(ele.getAllElementsByClass("cmode").size()==0&&ele.getAllElementsByClass("swap").size()==0) {
								m.obj=ele.getContent().toString();
							}
						}	
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				handler.sendMessage(m);
			}}).start();
	}
	
	private void GetJnBusLineOffline() {
		
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.what==1) {
					getParam();
				}				
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message m=new Message();
				try {
					Source source=new Source(new URL("http://112.2.33.3:7106/wap/line.do?command=toLn"));
					List<Element> trList=source.getAllElements(HTMLElementName.SELECT);
					String result=trList.get(0).getContent().toString();
					String[] results=result.split("\"");
					HashMap<String, String> maps=new HashMap<String, String>();
					for(int i=0;i<results.length;i++) {
						if(results[i].indexOf("|")!=-1) {
							maps.put(results[i].substring(results[i].indexOf("|")+1), results[i].substring(0, results[i].indexOf("|")));
						}
					}
					((TranApplication) getApplicationContext()).jn_offline_maps=maps;
					m.what=1;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					m.what=0;
				}
				handler.sendMessage(m);
			}}).start();
	}
	
	private void getParam() {
		if(!((TranApplication) getApplicationContext()).jn_offline_maps.containsKey(getIntent().getExtras().getString("lineName"))) {
			Toast.makeText(ResultJnActivity.this, "暂未获取到汽车位置信息", 3000).show();
			return;
		}
		if(stationNo==-1) {
			stationNo=modelListStation.get(modelListStation.size()-1).getId();
			GetJnCurrentBusOffline(modelListStation.get(modelListStation.size()-1).getName(), 
					modelListStation.get(0).getName()+"→"+modelListStation.get(modelListStation.size()-1).getName(), 
					stationNo,
					Integer.parseInt(((TranApplication) getApplicationContext()).jn_offline_maps.get(getIntent().getExtras().getString("lineName"))), 
					(Integer.parseInt(getIntent().getExtras().getString("inDown"))+1), 
					getIntent().getExtras().getString("lineName"));
		}
		else {
			GetJnCurrentBusOffline(stationName, 
					modelListStation.get(0).getName()+"→"+modelListStation.get(modelListStation.size()-1).getName(), 
					stationNo, 
					Integer.parseInt(((TranApplication) getApplicationContext()).jn_offline_maps.get(getIntent().getExtras().getString("lineName"))), 
					(Integer.parseInt(getIntent().getExtras().getString("inDown"))+1), 
					getIntent().getExtras().getString("lineName"));
		}
		adapter.setStation(stationNo, stationName);
	}

	@Override
	public void onRefreshComplete() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRefreshStart(RefreshMode mode) {
		// TODO Auto-generated method stub
		if(modelListStation.size()==0) {
			getJnstationbylid();
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
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		handler_main.removeCallbacks(runnable_main);
	}

}

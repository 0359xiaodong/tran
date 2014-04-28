package com.renyu.nj_tran.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import com.qq.wx.voice.recognizer.VoiceRecognizer;
import com.qq.wx.voice.recognizer.VoiceRecognizerListener;
import com.qq.wx.voice.recognizer.VoiceRecognizerResult;
import com.qq.wx.voice.recognizer.VoiceRecordState;
import com.qq.wx.voice.recognizer.VoiceRecognizerResult.Word;
import com.renyu.nj_tran.R;
import com.renyu.nj_tran.TranApplication;
import com.renyu.nj_tran.busresult.ResultActivity;
import com.renyu.nj_tran.busresult.ResultJnActivity;
import com.renyu.nj_tran.commons.CommonUtils;
import com.renyu.nj_tran.commons.Conn;
import com.renyu.nj_tran.model.BusLineModel;
import com.renyu.nj_tran.model.StationsModel;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

public class SearchFragment extends Fragment implements OnEditorActionListener, VoiceRecognizerListener {
	
	View view=null;
	
	LinearLayout line_right=null;
	TextView title_right=null;
	TextView title_name=null;
	
	ViewPager search_viewpager=null;
	PagerAdapter adapter=null;
	PagerTabStrip search_viewpager_strip=null;
	ImageView icon_search_voice=null;
	RelativeLayout icon_search_voicenum_layout=null;
	ImageView icon_search_voicenum=null;
	EditText icon_search_edit=null;
	ListView icon_search_listview=null;
	SimpleAdapter result_adapter=null;
	ArrayList<HashMap<String, Object>> lists=null;
	
	TextView nav_start=null;
	TextView nav_end=null;
	ImageView imagebtn_navsearch_switch=null;
	
	ArrayList<View> views=null;
	//搜索起始坐标
	String start_pos="";
	String end_pos="";
	//当前位置
	int current_pos=0;

	//表示目前所处的状态 0:空闲状态，可进行识别； 1：正在进行录音; 2：处于语音识别; 3：处于取消状态
	private int mRecoState = 0;
	
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
			lists=new ArrayList<HashMap<String, Object>>();			
			views=new ArrayList<View>();
			
			view=LayoutInflater.from(getActivity()).inflate(R.layout.fragment_search, null);
			
			title_name=(TextView) view.findViewById(R.id.title_name);
			title_name.setText("信息检索");	
			title_right=(TextView) view.findViewById(R.id.title_right);
			title_right.setText("提交");
			title_right.setOnClickListener(new TextView.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(current_pos==0) {
						if(icon_search_edit.getText().toString().equals("")||icon_search_edit.getText().toString().equals("路")) {
							Toast.makeText(getActivity(), "请您输入正确的查询数据", 2000).show();
							return;
						}
						loadDBData(icon_search_edit.getText().toString());
					}
					else {
						if(!start_pos.equals("")&&!end_pos.equals("")) {
							searchRoute();
						}
					}
				}});
			line_right=(LinearLayout) view.findViewById(R.id.line_right);
			title_right.setVisibility(View.VISIBLE);
			line_right.setVisibility(View.VISIBLE);
			
			search_viewpager_strip=(PagerTabStrip) view.findViewById(R.id.search_viewpager_strip);
			search_viewpager=(ViewPager) view.findViewById(R.id.search_viewpager);
			for(int i=0;i<2;i++) {
				loadViewPagerView(i);
			}
			adapter=new PagerAdapter() {
				
				@Override
				public boolean isViewFromObject(View arg0, Object arg1) {
					// TODO Auto-generated method stub
					return arg0==arg1;
				}
				
				@Override
				public int getCount() {
					// TODO Auto-generated method stub
					return views.size();
				}
				
				@Override
				public Object instantiateItem(View container, int position) {
					// TODO Auto-generated method stub
					View view=views.get(position);
					((ViewPager) container).addView(view);
					return view;
				}
				
				@Override
				public void destroyItem(View container, int position,
						Object object) {
					// TODO Auto-generated method stub
					View view=views.get(position);
					((ViewPager) container).removeView(view);
				}
				
				@Override
				public CharSequence getPageTitle(int position) {
					// TODO Auto-generated method stub
					if(position==0) {
						return "线路站点查询";
					}
					else if(position==1) {
						return "路线规划";
					}
					return super.getPageTitle(position);
				}
			};
			search_viewpager_strip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
			search_viewpager_strip.setTextColor(Color.parseColor("#50335e"));
			search_viewpager_strip.setPadding(0, 20, 0, 15);
			search_viewpager_strip.setTabIndicatorColor(Color.parseColor("#50335e"));
			search_viewpager_strip.setDrawFullUnderline(true);
			search_viewpager_strip.setTextSpacing(50);
			search_viewpager_strip.setDrawFullUnderline(true);
			search_viewpager.setAdapter(adapter);
			search_viewpager.setOnPageChangeListener(new OnPageChangeListener() {
				
				@Override
				public void onPageSelected(int arg0) {
					// TODO Auto-generated method stub
					current_pos=arg0;
				}
				
				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onPageScrollStateChanged(int arg0) {
					// TODO Auto-generated method stub
					
				}
			});
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
	
	private void loadViewPagerView(int pos) {
		View view=null;
		switch(pos) {
		case 0:
			view=LayoutInflater.from(getActivity()).inflate(R.layout.view_search, null);
			icon_search_voicenum_layout=(RelativeLayout) view.findViewById(R.id.icon_search_voicenum_layout);
			icon_search_voicenum=(ImageView) view.findViewById(R.id.icon_search_voicenum);
			icon_search_voice=(ImageView) view.findViewById(R.id.icon_search_voice);
			icon_search_voice.setOnClickListener(new ImageView.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//判断当前是否调用语音
					if(mRecoState==0) {
						VoiceRecognizer.shareInstance().setSilentTime(1000);
						VoiceRecognizer.shareInstance().setListener(SearchFragment.this);						
						int result=VoiceRecognizer.shareInstance().init(getActivity(), "1e005feff384d776f0ddbbec76a20576948524f0b795d8cf");
						if(result!=0) {
							Toast.makeText(getActivity(), "语音引擎初始化失败", 2000).show();
						}
						else {
							if(0==VoiceRecognizer.shareInstance().start()) {
								mRecoState=1;
								icon_search_voicenum_layout.setVisibility(View.VISIBLE);
							}
							else {
								Toast.makeText(getActivity(), "语音引擎启动失败", 2000).show();
							}
						}
					}
					else if(mRecoState==1) {
						VoiceRecognizer.shareInstance().stop();
					}
				}});
			icon_search_edit=(EditText) view.findViewById(R.id.icon_search_edit);
			icon_search_edit.setOnEditorActionListener(this);
			icon_search_edit.addTextChangedListener(tw);
			icon_search_listview=(ListView) view.findViewById(R.id.icon_search_listview);
			result_adapter=new SimpleAdapter(getActivity(), lists, R.layout.adapter_searchpoi, new String[]{"name"}, new int[]{R.id.search_poi_text});
			icon_search_listview.setAdapter(result_adapter);
			break;
		case 1:
			view=LayoutInflater.from(getActivity()).inflate(R.layout.view_navigation, null);
			nav_start=(TextView) view.findViewById(R.id.nav_start);
			if(!((TranApplication) getActivity().getApplicationContext()).currentLatLng.equals("")) {
				nav_start.setText("我的位置");
				start_pos=((TranApplication) getActivity().getApplicationContext()).currentLatLng;
			}
			nav_start.setOnClickListener(new TextView.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent=new Intent(getActivity(), SearchPoiActivity.class);
					Bundle bundle=new Bundle();
					bundle.putString("title", "起点搜索");
					intent.putExtras(bundle);
					getParentFragment().startActivityForResult(intent, 201);
				}});
			nav_end=(TextView) view.findViewById(R.id.nav_end);
			nav_end.setOnClickListener(new TextView.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent=new Intent(getActivity(), SearchPoiActivity.class);
					Bundle bundle=new Bundle();
					bundle.putString("title", "终点搜索");
					intent.putExtras(bundle);
					getParentFragment().startActivityForResult(intent, 202);
				}});
			imagebtn_navsearch_switch=(ImageView) view.findViewById(R.id.imagebtn_navsearch_switch);
			imagebtn_navsearch_switch.setOnClickListener(new ImageView.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String nav_start_title=nav_start.getText().toString();
					String nav_end_title=nav_end.getText().toString();
					nav_start.setText(nav_end_title);
					nav_end.setText(nav_start_title);
					String start_pos_=start_pos;
					String end_pos_=end_pos;
					start_pos=end_pos_;
					end_pos=start_pos_;
				}});
			break;
		}
		views.add(view);
	}
	
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		// TODO Auto-generated method stub
		if(actionId==EditorInfo.IME_ACTION_SEARCH) {
			InputMethodManager imm=(InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(icon_search_edit.getWindowToken(), 0); 
			if(icon_search_edit.getText().toString().equals("")||icon_search_edit.getText().toString().equals("路")) {
				Toast.makeText(getActivity(), "请您输入正确的查询数据", 2000).show();
				return false;
			}
			loadDBData(icon_search_edit.getText().toString());
		}
		return false;
	}
	
	/**
	 * 加载结果
	 * @param result
	 */
	private void loadDBData(String result) {
		//全数字直接精确查询线路
		if(CommonUtils.isNumeric(result)) {
			//区分江宁与市区
			if(Conn.getInstance(getActivity()).isJN(result)) {
				choiceBusLine(Conn.getInstance(getActivity()).getJNTranInfoDirect(result, true));
			}
			else {
				choiceBusLine(Conn.getInstance(getActivity()).getTranInfoDirect(result+"路", true));
			}
		}
		//含数字直接模糊查询线路
		else if(CommonUtils.isContainNumeric(result)) {
			if(Conn.getInstance(getActivity()).isJN(result)) {
				//如果江宁公交包含，则同样还需搜索一遍市区，以防止线路名称冲突
				ArrayList<BusLineModel> busLineModels=new ArrayList<BusLineModel>();
				busLineModels.addAll(Conn.getInstance(getActivity()).getJNTranInfoDirect(result, false));
				busLineModels.addAll(Conn.getInstance(getActivity()).getTranInfoDirect(result, false));
				choiceBusLine(busLineModels);
			}
			else {
				choiceBusLine(Conn.getInstance(getActivity()).getTranInfoDirect(result, false));
			}
		}
		//如果不含数字，除部分线路以全汉字命名，其余都是在查询站点
		else {
			//部分江宁线路以汉字命名，南京部分线路中也含有'线'命名的线路
			ArrayList<BusLineModel> busLineModels=new ArrayList<BusLineModel>();
			busLineModels.addAll(Conn.getInstance(getActivity()).getJNTranInfoDirect(result, false));
			if(busLineModels.size()==0) {
				busLineModels.addAll(Conn.getInstance(getActivity()).getTranInfoDirect(result, false));
			}			
			if(busLineModels.size()>0) {
				choiceBusLine(busLineModels);
			}
			else {
				//模糊查询站点
				ArrayList<StationsModel> stationsModels=new ArrayList<StationsModel>();
				stationsModels.addAll(Conn.getInstance(getActivity()).getJNStationInfo(result));
				stationsModels.addAll(Conn.getInstance(getActivity()).getStationInfo(result));
				choiceStation(stationsModels);
			}			
		}
	}
	
	/**
	 * 选择公交线路
	 * @param busLineModelList
	 */
	private void choiceBusLine(final ArrayList<BusLineModel> busLineModelList) {
		String[] array=new String[busLineModelList.size()];
		for(int i=0;i<busLineModelList.size();i++) {
			array[i]=busLineModelList.get(i).getLine_name()+" "+busLineModelList.get(i).getStart_from()+"-->"+busLineModelList.get(i).getEnd_location();
		}
		new AlertDialog.Builder(getActivity()).setTitle("请您选择该车次线路").setItems(array, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Intent intent=null;
				if(Conn.getInstance(getActivity()).isJN(busLineModelList.get(which).getId())) {
					intent=new Intent(getActivity(), ResultJnActivity.class);
				}
				else {
					intent=new Intent(getActivity(), ResultActivity.class);
				}
				Bundle bundle=new Bundle();
				bundle.putString("lineName", busLineModelList.get(which).getLine_name());
				bundle.putString("stationName", "");
				bundle.putInt("lineId", busLineModelList.get(which).getId());
				bundle.putString("inDown", busLineModelList.get(which).getLine_code());
				intent.putExtras(bundle);
				getActivity().startActivity(intent);
			}}).show();
	}
	
	private void choiceStation(final ArrayList<StationsModel> modelListStation) {
		String array[]=new String[modelListStation.size()];
		for(int i=0;i<modelListStation.size();i++) {
			array[i]=modelListStation.get(i).getStation_name();
		}
		new AlertDialog.Builder(getActivity()).setTitle("请您选择线路").setItems(array, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(getActivity(), SearchByStationNameActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("sid", ""+modelListStation.get(which).getId());
				bundle.putString("stationName", ""+modelListStation.get(which).getStation_name());
				bundle.putDouble("lat", modelListStation.get(which).getMap_station_lat());
				bundle.putDouble("long", modelListStation.get(which).getMap_station_long());
				intent.putExtras(bundle);
				getActivity().startActivity(intent);
			}
		}).show();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(resultCode==Activity.RESULT_OK) {
			if(requestCode==201) {
				nav_start.setText(data.getExtras().getString("name"));
				start_pos=data.getExtras().getString("pos");
				if(!start_pos.equals("")&&!end_pos.equals("")) {
					searchRoute();
				}
			}
			else if(requestCode==202) {
				nav_end.setText(data.getExtras().getString("name"));
				end_pos=data.getExtras().getString("pos");
				if(!start_pos.equals("")&&!end_pos.equals("")) {
					searchRoute();
				}
			}
		}		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void searchRoute() {
		Intent intent=new Intent(getActivity(), SearchByNavigationActivity.class);
		Bundle bundle=new Bundle();
		bundle.putString("start", start_pos);
		bundle.putString("end", end_pos);
		intent.putExtras(bundle);
		getActivity().startActivity(intent);
	}
	
	TextWatcher tw=new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			if(!s.toString().equals("")) {
				lists.clear();
				//全数字直接精确查询线路
				if(CommonUtils.isNumeric(s.toString())) {		
					ArrayList<BusLineModel> busLineModelList=null;
					//区分江宁与市区
					if(Conn.getInstance(getActivity()).isJN(s.toString())) {
						busLineModelList=Conn.getInstance(getActivity()).getJNTranInfoDirect(s.toString(), true);
					}
					else {
						//仅市区线路
						busLineModelList=Conn.getInstance(getActivity()).getTranInfoDirect(s.toString()+"路", true);
					}
					final ArrayList<BusLineModel> busLineModelList_temp=busLineModelList;
					String[] array=new String[busLineModelList.size()];
					for(int i=0;i<busLineModelList.size();i++) {
						HashMap<String, Object> map=new HashMap<String, Object>();
						array[i]=busLineModelList.get(i).getLine_name()+" "+busLineModelList.get(i).getStart_from()+"-->"+busLineModelList.get(i).getEnd_location();
						map.put("name", array[i]);
						lists.add(map);
					}
					icon_search_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							// TODO Auto-generated method stub
							Intent intent=null;
							if(Conn.getInstance(getActivity()).isJN(busLineModelList_temp.get(position).getId())) {
								intent=new Intent(getActivity(), ResultJnActivity.class);
							}
							else {
								intent=new Intent(getActivity(), ResultActivity.class);
							}
							Bundle bundle=new Bundle();
							bundle.putString("lineName", busLineModelList_temp.get(position).getLine_name());
							bundle.putString("stationName", "");
							bundle.putInt("lineId", busLineModelList_temp.get(position).getId());
							bundle.putString("inDown", busLineModelList_temp.get(position).getLine_code());
							intent.putExtras(bundle);
							getActivity().startActivity(intent);
						}
					});
				}
				//含数字直接模糊查询线路
				else if(CommonUtils.isContainNumeric(s.toString())) {
					ArrayList<BusLineModel> busLineModelList=null;
					if(Conn.getInstance(getActivity()).isJN(s.toString())) {
						//如果江宁公交包含，则同样还需搜索一遍市区，以防止线路名称冲突
						ArrayList<BusLineModel> busLineModels=new ArrayList<BusLineModel>();
						busLineModels.addAll(Conn.getInstance(getActivity()).getJNTranInfoDirect(s.toString(), false));
						busLineModels.addAll(Conn.getInstance(getActivity()).getTranInfoDirect(s.toString(), false));
						busLineModelList=busLineModels;
					}
					else {
						busLineModelList=Conn.getInstance(getActivity()).getTranInfoDirect(s.toString(), false);
					}					
					final ArrayList<BusLineModel> busLineModelList_temp=busLineModelList;
					String[] array=new String[busLineModelList.size()];
					for(int i=0;i<busLineModelList.size();i++) {
						HashMap<String, Object> map=new HashMap<String, Object>();
						array[i]=busLineModelList.get(i).getLine_name()+" "+busLineModelList.get(i).getStart_from()+"-->"+busLineModelList.get(i).getEnd_location();
						map.put("name", array[i]);
						lists.add(map);
					}
					icon_search_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							// TODO Auto-generated method stub
							Intent intent=null;
							if(Conn.getInstance(getActivity()).isJN(busLineModelList_temp.get(position).getId())) {
								intent=new Intent(getActivity(), ResultJnActivity.class);
							}
							else {
								intent=new Intent(getActivity(), ResultActivity.class);
							}
							Bundle bundle=new Bundle();
							bundle.putString("lineName", busLineModelList_temp.get(position).getLine_name());
							bundle.putString("stationName", "");
							bundle.putInt("lineId", busLineModelList_temp.get(position).getId());
							bundle.putString("inDown", busLineModelList_temp.get(position).getLine_code());
							intent.putExtras(bundle);
							getActivity().startActivity(intent);
						}
					});
				}
				//如果不含数字，除部分线路以全汉字命名，其余都是在查询站点
				else {
					//部分江宁线路以汉字命名，南京部分线路中也含有'线'命名的线路
					final ArrayList<BusLineModel> busLineModelList=new ArrayList<BusLineModel>();
					busLineModelList.addAll(Conn.getInstance(getActivity()).getJNTranInfoDirect(s.toString(), false));
					if(busLineModelList.size()==0) {
						busLineModelList.addAll(Conn.getInstance(getActivity()).getTranInfoDirect(s.toString(), false));
					}					
					if(busLineModelList.size()>0) {						
						String[] array=new String[busLineModelList.size()];
						for(int i=0;i<busLineModelList.size();i++) {
							HashMap<String, Object> map=new HashMap<String, Object>();
							array[i]=busLineModelList.get(i).getLine_name()+" "+busLineModelList.get(i).getStart_from()+"-->"+busLineModelList.get(i).getEnd_location();
							map.put("name", array[i]);
							lists.add(map);
						}
						icon_search_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								// TODO Auto-generated method stub
								Intent intent=null;
								if(Conn.getInstance(getActivity()).isJN(busLineModelList.get(position).getId())) {
									intent=new Intent(getActivity(), ResultJnActivity.class);
								}
								else {
									intent=new Intent(getActivity(), ResultActivity.class);
								}
								Bundle bundle=new Bundle();
								bundle.putString("lineName", busLineModelList.get(position).getLine_name());
								bundle.putString("stationName", "");
								bundle.putInt("lineId", busLineModelList.get(position).getId());
								bundle.putString("inDown", busLineModelList.get(position).getLine_code());
								intent.putExtras(bundle);
								getActivity().startActivity(intent);
							}
						});
					}
					else {
						//模糊查询站点
						final ArrayList<StationsModel> modelListStation=new ArrayList<StationsModel>();
						modelListStation.addAll(Conn.getInstance(getActivity()).getJNStationInfo(s.toString()));
						modelListStation.addAll(Conn.getInstance(getActivity()).getStationInfo(s.toString()));
						
						String array[]=new String[modelListStation.size()];
						for(int i=0;i<modelListStation.size();i++) {
							HashMap<String, Object> map=new HashMap<String, Object>();
							array[i]=modelListStation.get(i).getStation_name();
							map.put("name", array[i]);
							lists.add(map);
						}
						icon_search_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								// TODO Auto-generated method stub
								Intent intent=new Intent(getActivity(), SearchByStationNameActivity.class);
								Bundle bundle=new Bundle();
								bundle.putString("sid", ""+modelListStation.get(position).getId());
								bundle.putString("stationName", ""+modelListStation.get(position).getStation_name());
								bundle.putDouble("lat", modelListStation.get(position).getMap_station_lat());
								bundle.putDouble("long", modelListStation.get(position).getMap_station_long());
								intent.putExtras(bundle);
								getActivity().startActivity(intent);
							}
						});
					}
					
					
										
				}
				result_adapter.notifyDataSetChanged();
			}
		}
	};

	@Override
	public void onGetError(int errorCode) {
		// TODO Auto-generated method stub
		cancelTask();
	}

	@Override
	public void onGetResult(VoiceRecognizerResult result) {
		// TODO Auto-generated method stub
		cancelTask();
		if(result!=null&&result.words!=null) {
			int wordSize=result.words.size();
			StringBuilder results=new StringBuilder();
			for (int i=0;i<wordSize;++i) {
				Word word=(Word) result.words.get(i);
				if (word!=null&&word.text!=null) {
					results.append(word.text.replace(" ", ""));
				}
			}
			icon_search_edit.setText(results.toString());
		}
		mRecoState=0;
	}

	@Override
	public void onGetVoiceRecordState(VoiceRecordState state) {
		// TODO Auto-generated method stub
		if (state == VoiceRecordState.Start) {
			Toast.makeText(getActivity(), "语音已开启，请说话…", 2000).show();
		} else if (state == VoiceRecordState.Complete) {
			Toast.makeText(getActivity(), "识别中...", 2000).show();
			mRecoState=2;
			startTask();
		} else if (state == VoiceRecordState.Canceling) {
			mRecoState=3;
			Toast.makeText(getActivity(), "正在取消", 2000).show();
		} else if (state == VoiceRecordState.Canceled) {
			Toast.makeText(getActivity(), "点击开始说话", 2000).show();
			mRecoState=0;
		}
	}

	private final int mMicNum=8;
	@Override
	public void onVolumeChanged(int volume) {
		// TODO Auto-generated method stub
		int index=volume;
		if (index<0) {
			index=0;
		} else if (index>=mMicNum) {
			index=mMicNum-1;
		}
		if (1==mRecoState) {
			switch (index) {
			case 0:
				icon_search_voicenum.setImageResource(R.drawable.recog001);
				break;
			case 1:
				icon_search_voicenum.setImageResource(R.drawable.recog002);
				break;
			case 2:
				icon_search_voicenum.setImageResource(R.drawable.recog003);
				break;
			case 3:
				icon_search_voicenum.setImageResource(R.drawable.recog004);
				break;
			case 4:
				icon_search_voicenum.setImageResource(R.drawable.recog005);
				break;
			case 5:
				icon_search_voicenum.setImageResource(R.drawable.recog006);
				break;
			case 6:
				icon_search_voicenum.setImageResource(R.drawable.recog007);
				break;
			case 7:
				icon_search_voicenum.setImageResource(R.drawable.recog008);
				break;
			default:
				icon_search_voicenum.setImageResource(R.drawable.recogstart);
			}
		}
	
	}
	
	private Handler refresh_handler=new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			System.out.println(msg.what);
			switch(msg.what&7) {
			case 0:
				icon_search_voicenum.setImageResource(R.drawable.recowait001);
				break;
			case 1:
				icon_search_voicenum.setImageResource(R.drawable.recowait002);
				break;
			case 2:
				icon_search_voicenum.setImageResource(R.drawable.recowait003);
				break;
			case 3:
				icon_search_voicenum.setImageResource(R.drawable.recowait004);
				break;
			case 4:
				icon_search_voicenum.setImageResource(R.drawable.recowait005);
				break;
			case 5:
				icon_search_voicenum.setImageResource(R.drawable.recowait006);
				break;
			case 6:
				icon_search_voicenum.setImageResource(R.drawable.recowait007);
				break;
			default:
				icon_search_voicenum.setImageResource(R.drawable.recogstart);
			}
		}
	};
	
	int btnIndex=0;
	Timer frameTimer=null;
	TimerTask frameTask=null;
	private void startTask() {
		frameTimer=new Timer(false);
		frameTask=new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message m=new Message();
				m.what=btnIndex;
				refresh_handler.sendMessage(m);
				btnIndex++;
			}
		};
		frameTimer.schedule(frameTask, 200, 100);
	}
	
	private void cancelTask() {
		icon_search_voicenum_layout.setVisibility(View.GONE);
		icon_search_voicenum.setImageResource(R.drawable.recogstart);
		if(frameTimer!=null) {
			frameTimer.cancel();
		}
		if(frameTask!=null) {
			frameTask.cancel();
		}
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		VoiceRecognizer.shareInstance().destroy();
	}
}

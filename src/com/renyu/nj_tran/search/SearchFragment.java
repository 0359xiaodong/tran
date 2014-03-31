package com.renyu.nj_tran.search;

import java.util.ArrayList;
import java.util.HashMap;

import com.renyu.nj_tran.R;
import com.renyu.nj_tran.TranApplication;
import com.renyu.nj_tran.busresult.ResultActivity;
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
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

public class SearchFragment extends Fragment implements OnEditorActionListener {
	
	View view=null;
	
	LinearLayout line_right=null;
	TextView title_right=null;
	TextView title_name=null;
	
	ViewPager search_viewpager=null;
	PagerAdapter adapter=null;
	PagerTabStrip search_viewpager_strip=null;
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
							Toast.makeText(getActivity(), "请您输入正确的查询数据", 3000).show();
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
				Toast.makeText(getActivity(), "请您输入正确的查询数据", 3000).show();
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
		//判断是不是都是数字，如果都是数字，则为查询线路
		if(CommonUtils.isNumeric(result)) {
			choiceBusLine(Conn.getInstance(getActivity()).getTranInfo(result+"路"));
		}
		//判断是不是含有数字，如果是，则为包含“路”字的查询线路
		else if(CommonUtils.isContainNumeric(result)) {
			choiceBusLine(Conn.getInstance(getActivity()).getTranInfo(result));
		}
		//如果不含数字，那就是在查询站点
		else {
			choiceStation(Conn.getInstance(getActivity()).getStationInfo(result));
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
				Intent intent=new Intent(getActivity(), ResultActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("lineName", busLineModelList.get(which).getLine_name());
				bundle.putString("stationName", "");
				bundle.putInt("lineId", busLineModelList.get(which).getId());
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
				//判断是不是都是数字，如果都是数字，则为查询线路
				if(CommonUtils.isNumeric(s.toString())) {					
					final ArrayList<BusLineModel> busLineModelList=Conn.getInstance(getActivity()).getTranInfo(s.toString()+"路");					
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
							Intent intent=new Intent(getActivity(), ResultActivity.class);
							Bundle bundle=new Bundle();
							bundle.putString("lineName", busLineModelList.get(position).getLine_name());
							bundle.putString("stationName", "");
							bundle.putInt("lineId", busLineModelList.get(position).getId());
							intent.putExtras(bundle);
							getActivity().startActivity(intent);
						}
					});
				}
				//判断是不是含有数字，如果是，则为包含“路”字的查询线路
				else if(CommonUtils.isContainNumeric(s.toString())) {
					final ArrayList<BusLineModel> busLineModelList=Conn.getInstance(getActivity()).getTranInfo(s.toString());					
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
							Intent intent=new Intent(getActivity(), ResultActivity.class);
							Bundle bundle=new Bundle();
							bundle.putString("lineName", busLineModelList.get(position).getLine_name());
							bundle.putString("stationName", "");
							bundle.putInt("lineId", busLineModelList.get(position).getId());
							intent.putExtras(bundle);
							getActivity().startActivity(intent);
						}
					});
				}
				//如果不含数字，那就是在查询站点
				else {
					final ArrayList<StationsModel> modelListStation=Conn.getInstance(getActivity()).getStationInfo(s.toString());
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
				result_adapter.notifyDataSetChanged();
			}
		}
	};
}

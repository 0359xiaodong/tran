package com.renyu.nj_tran.search;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnMapLoadedListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.overlay.BusRouteOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.BusStep;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteBusWalkItem;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.BusRouteQuery;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.WalkRouteResult;
import com.amap.api.services.route.WalkStep;
import com.baidu.mobstat.StatService;
import com.renyu.nj_tran.R;
import com.renyu.nj_tran.busresult.ResultActivity;
import com.renyu.nj_tran.commons.CommonUtils;
import com.renyu.nj_tran.commons.Conn;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class SearchByNavigationActivity extends Activity implements OnRouteSearchListener, OnMapLoadedListener, OnMarkerClickListener {
	
	LinearLayout line_left=null;
	TextView title_name=null;
	ImageView title_left=null;
	LinearLayout line_right=null;
	TextView title_right=null;
	ProgressBar title_pb=null;
	
	private ViewPager navigation_mapsearch_viewpager=null;
	PagerAdapter adapter=null;
	private MapView navigation_mapsearchview;
	private AMap aMap;
	LinearLayout navigation_mapsearch_indicator=null;
	RouteSearch routeSearch=null;
	ArrayList<View> views=null;
	ArrayList<ImageView> image_views=null;
	
	//查询结果
	BusRouteResult result_=null;
	//规划集合
	LinkedList<BusPath> busPaths=null;
	boolean isLoading=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		MapsInitializer.sdcardDir=CommonUtils.getSdCacheDir(SearchByNavigationActivity.this);
		
		setContentView(R.layout.activiity_searchbynavigation);
		
		views=new ArrayList<View>();
		image_views=new ArrayList<ImageView>();
		busPaths=new LinkedList<BusPath>();
		
		init(savedInstanceState);
	}
	
	public void init(Bundle savedInstanceState) {
		line_left=(LinearLayout) findViewById(R.id.line_left);
		line_left.setVisibility(View.VISIBLE);
		title_name=(TextView) findViewById(R.id.title_name);
		title_name.setText("路线规划结果");
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
		title_right.setText("刷新");
		title_right.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!isLoading) {
					searchRouteResult();
				}				
			}});
		title_right.setVisibility(View.VISIBLE);
		line_right=(LinearLayout) findViewById(R.id.line_right);
		line_right.setVisibility(View.VISIBLE);
		title_pb=(ProgressBar) findViewById(R.id.title_pb);
		
		navigation_mapsearch_indicator=(LinearLayout) findViewById(R.id.navigation_mapsearch_indicator);
		navigation_mapsearch_viewpager=(ViewPager) findViewById(R.id.navigation_mapsearch_viewpager);
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
			public void destroyItem(View container, int position, Object object) {
				// TODO Auto-generated method stub
				View view=views.get(position);
				((ViewPager) container).removeView(view);
			}
			
			@Override  
			public int getItemPosition(Object object) {  
				return POSITION_NONE;  
			}
		};
		navigation_mapsearch_viewpager.setAdapter(adapter);
		navigation_mapsearch_viewpager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				BusPath busPath=busPaths.get(arg0);
				showMapRoute(result_, busPath);
				for(int i=0;i<image_views.size();i++) {
					if(i==arg0) {
						image_views.get(i).setImageResource(R.drawable.jiaodian);
					}
					else {
						image_views.get(i).setImageResource(R.drawable.jiaodian_white);
					}
				}
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
		navigation_mapsearchview=(MapView) findViewById(R.id.navigation_mapsearchview);
		navigation_mapsearchview.onCreate(savedInstanceState);
		if(aMap==null) {
			aMap=navigation_mapsearchview.getMap();
			aMap.setOnMapLoadedListener(this);
			aMap.setOnMarkerClickListener(this);
		}
		changeCamera();
		routeSearch = new RouteSearch(this);
		routeSearch.setRouteSearchListener(this);
	}
	
	private void changeCamera() {
		aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(32.041804,118.78418), 17, 0, 0)));
	}
	
	private void searchRouteResult() {
		isLoading=true;
		title_pb.setVisibility(View.VISIBLE);
		String start=getIntent().getExtras().getString("start");
		String end=getIntent().getExtras().getString("end");
		RouteSearch.FromAndTo fromAndTo=new RouteSearch.FromAndTo(new LatLonPoint(Double.parseDouble(start.split("&")[0]), Double.parseDouble(start.split("&")[1])), 
				new LatLonPoint(Double.parseDouble(end.split("&")[0]), Double.parseDouble(end.split("&")[1])));
		BusRouteQuery query=new BusRouteQuery(fromAndTo, RouteSearch.BusDefault, "025", 1);
		routeSearch.calculateBusRouteAsyn(query);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		navigation_mapsearchview.onResume();
		StatService.onResume(this);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		navigation_mapsearchview.onPause();
		StatService.onPause(this);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		navigation_mapsearchview.onDestroy();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		navigation_mapsearchview.onSaveInstanceState(outState);
	}

	@Override
	public void onBusRouteSearched(BusRouteResult result, int rCode) {
		// TODO Auto-generated method stub
		title_pb.setVisibility(View.INVISIBLE);
		isLoading=false;
		if(rCode==0) {
			if(result!=null&&result.getPaths()!=null&&result.getPaths().size()>0) {
				navigation_mapsearch_indicator.removeAllViews();
				image_views.clear();
				views.clear();
				for(int i=0;i<result.getPaths().size();i++) {
					String title="";
					BusPath busPath=result.getPaths().get(i);
					if(i==0) {
						result_=result;
						showMapRoute(result_, busPath);
					}
					busPaths.add(busPath);
					for(int j=0;j<busPath.getSteps().size();j++) {
						BusStep step=busPath.getSteps().get(j);
						if(step.getBusLine()!=null) {
							if(title.equals("")) {
								title=step.getBusLine().getBusLineName().substring(0, step.getBusLine().getBusLineName().indexOf("("));
							}
							else {
								title+=" -> "+step.getBusLine().getBusLineName().substring(0, step.getBusLine().getBusLineName().indexOf("("));
							}											
						}
					}
					LayoutParams params=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);				
					ImageView image=new ImageView(SearchByNavigationActivity.this);
					image.setPadding(3, 3, 3, 3);
					if(i==0) {
						image.setImageResource(R.drawable.jiaodian);
					}
					else {
						image.setImageResource(R.drawable.jiaodian_white);
					}
					navigation_mapsearch_indicator.addView(image, params);
					image_views.add(image);
					views.add(loadNavigationMapView(title, busPath));
				}
				adapter.notifyDataSetChanged();
				if(views.size()>0) {
					navigation_mapsearch_viewpager.setCurrentItem(0);
				}				
			}
			else {
				Toast.makeText(SearchByNavigationActivity.this, "暂未搜索到相关信息", 3000).show();
			}
		}
		else {
			Toast.makeText(SearchByNavigationActivity.this, "暂未搜索到相关信息", 3000).show();
		}
	}
	
	private View loadNavigationMapView(final String title, final BusPath busPath) {
		View view=LayoutInflater.from(SearchByNavigationActivity.this).inflate(R.layout.view_navigation_map, null);
		TextView navigation_map_more=(TextView) view.findViewById(R.id.navigation_map_more);
		navigation_map_more.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ArrayList<String> str_route=new ArrayList<String>();
				for(int j=0;j<busPath.getSteps().size();j++) {
					BusStep step=busPath.getSteps().get(j);
					RouteBusWalkItem walkitem=step.getWalk();
					if(walkitem!=null) {
						List<WalkStep> walkStepList=walkitem.getSteps();
						for(int i=0;i<walkStepList.size();i++) {
							System.out.println(walkStepList.get(i).getInstruction());
							str_route.add("1&"+walkStepList.get(i).getInstruction());
						}
					}
					
					if(step.getBusLine()!=null) {
						System.out.println(step.getBusLine().getBusLineName()+" "+step.getBusLine().getDepartureBusStation().getBusStationName()+" "+step.getBusLine().getArrivalBusStation().getBusStationName());
						str_route.add("2&乘坐"+step.getBusLine().getBusLineName()+"在"+step.getBusLine().getDepartureBusStation().getBusStationName()+"站上车，经过"+(step.getBusLine().getPassStationNum()+1)+"站，至"+step.getBusLine().getArrivalBusStation().getBusStationName()+"站下车");
					}
				}
				Intent intent=new Intent(SearchByNavigationActivity.this, SearchByNavigationDetailActivity.class);
				Bundle bundle=new Bundle();
				bundle.putStringArrayList("routedetail", str_route);
				bundle.putString("title", title);
				bundle.putString("duration", CommonUtils.getBusDuration(busPath.getDuration())); 
				bundle.putString("distance", CommonUtils.getBusDistance(busPath.getDistance()));
				bundle.putString("walkdistance", "步行"+CommonUtils.getBusDistance(busPath.getWalkDistance()));
				intent.putExtras(bundle);
				startActivity(intent);
			}
			
		});
		TextView navigation_map_title=(TextView) view.findViewById(R.id.navigation_map_title);
		navigation_map_title.setText(title);
		TextView navigation_map_time=(TextView) view.findViewById(R.id.navigation_map_time);
		navigation_map_time.setText(CommonUtils.getBusDuration(busPath.getDuration()));
		TextView navigation_map_distance=(TextView) view.findViewById(R.id.navigation_map_distance);
		navigation_map_distance.setText(CommonUtils.getBusDistance(busPath.getDistance()));
		TextView navigation_map_walkdistance=(TextView) view.findViewById(R.id.navigation_map_walkdistance);
		navigation_map_walkdistance.setText("步行"+CommonUtils.getBusDistance(busPath.getWalkDistance()));
		LinearLayout navigation_map_bus_line_layout=(LinearLayout) view.findViewById(R.id.navigation_map_bus_line_layout);
		for(int j=0;j<busPath.getSteps().size();j++) {
			BusStep step=busPath.getSteps().get(j);
			if(step.getBusLine()!=null) {
				LayoutParams params=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				params.topMargin=3;
				TextView textview=new TextView(SearchByNavigationActivity.this);
				textview.setText(Html.fromHtml("乘坐<font color='red'>"+step.getBusLine().getBusLineName()+"</font><br>在<font color='blue'>"+step.getBusLine().getDepartureBusStation().getBusStationName()+"</font>站上车，经过<font color='blue'>"+(step.getBusLine().getPassStationNum()+1)+"</font>站，至<font color='blue'>"+step.getBusLine().getArrivalBusStation().getBusStationName()+"</font>站下车"));
				textview.setTextColor(Color.GRAY);
				textview.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				final String name=step.getBusLine().getBusLineName();
				textview.setOnClickListener(new TextView.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						findDetail(name);
					}});
				navigation_map_bus_line_layout.addView(textview, params);
				TextView textview_line=new TextView(SearchByNavigationActivity.this);
				textview_line.setBackgroundColor(Color.BLACK);
				LayoutParams params_line=new LayoutParams(LayoutParams.MATCH_PARENT, 1);
				params_line.topMargin=8;
				params_line.bottomMargin=8;
				navigation_map_bus_line_layout.addView(textview_line, params_line);			
			}
		}
		return view;
	}
	
	private void showMapRoute(BusRouteResult result, BusPath busPath) {
		aMap.clear();// 清理地图上的所有覆盖物
		BusRouteOverlay routeOverlay=new BusRouteOverlay(this, aMap, busPath, result.getStartPos(), result.getTargetPos());
		routeOverlay.removeFromMap();
		routeOverlay.addToMap();
		routeOverlay.zoomToSpan();
	}

	@Override
	public void onDriveRouteSearched(DriveRouteResult arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onWalkRouteSearched(WalkRouteResult arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMapLoaded() {
		// TODO Auto-generated method stub
		searchRouteResult();
	}

	@Override
	public boolean onMarkerClick(Marker arg0) {
		// TODO Auto-generated method stub
		findDetail(arg0.getTitle());
		
		return false;
	}
	
	private void findDetail(String title) {
		int luIndex=title.indexOf("路");
		if(luIndex==-1) {
			Toast.makeText(SearchByNavigationActivity.this, "暂未查找到相关公交信息", 3000).show();
			return;
		}
		String lu=title.substring(0, luIndex+1);
		
		int _leftIndex=title.indexOf("(");
		int _rightIndex=title.length();
		String _name=title.substring(_leftIndex+1, _rightIndex-1);
		String startName=_name.split("--")[0];
		String endName=_name.split("--")[1];
		
		int lineId=Conn.getInstance(getApplicationContext()).getLineId(lu, startName, endName);
		
		Intent intent=new Intent(SearchByNavigationActivity.this, ResultActivity.class);
		Bundle bundle=new Bundle();
		bundle.putString("lineName", lu);
		bundle.putString("stationName", "");
		bundle.putInt("lineId", lineId);
		intent.putExtras(bundle);
		startActivity(intent);
	}
	
}

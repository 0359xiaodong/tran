package com.renyu.nj_tran.search;

import java.util.ArrayList;
import java.util.List;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.AMap.CancelableCallback;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnCameraChangeListener;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMapLoadedListener;
import com.amap.api.maps.AMap.OnMapLongClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.AMap.OnMarkerDragListener;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.LatLngBounds.Builder;
import com.amap.api.maps.overlay.BusLineOverlay;
import com.amap.api.services.busline.BusLineItem;
import com.amap.api.services.busline.BusLineQuery;
import com.amap.api.services.busline.BusLineResult;
import com.amap.api.services.busline.BusLineSearch;
import com.amap.api.services.busline.BusLineQuery.SearchType;
import com.amap.api.services.busline.BusLineSearch.OnBusLineSearchListener;
import com.amap.api.services.busline.BusStationResult;
import com.amap.api.services.busline.BusStationSearch.OnBusStationSearchListener;
import com.baidu.mobstat.StatService;
import com.renyu.nj_tran.R;
import com.renyu.nj_tran.busresult.ResultActivity;
import com.renyu.nj_tran.commons.CommonUtils;
import com.renyu.nj_tran.commons.MapConverter;
import com.renyu.nj_tran.commons.Point;
import com.renyu.nj_tran.model.CurrentBusModel;
import com.renyu.nj_tran.model.StationsModel;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SearchByRotateMapActivity extends Activity implements 
	OnMapLoadedListener, 
	CancelableCallback, 
	OnMapClickListener, 
	OnMapLongClickListener, 
	OnCameraChangeListener,
	OnMarkerClickListener,
	OnInfoWindowClickListener, 
	OnMarkerDragListener,
	OnClickListener, 
	InfoWindowAdapter, 
	OnBusLineSearchListener,
	OnBusStationSearchListener {
	
	LinearLayout line_left=null;
	TextView title_name=null;
	ImageView title_left=null;
	
	private AMap aMap;
	MapView mapsearchview=null;
	BusLineQuery busLineQuery=null;
	TextView mapsearch_tip=null;
	
	ArrayList<StationsModel> modelListStation=null;
	ArrayList<CurrentBusModel> modelListBus=null;
	
	//允许广播刷新
	boolean isAllowRefresh=false;
	//高德地图搜索完成结果
	List<BusLineItem> lineItems=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		MapsInitializer.sdcardDir=CommonUtils.getSdCacheDir(SearchByRotateMapActivity.this);
		
		setContentView(R.layout.activity_mapsearch);
		
		modelListStation=(ArrayList<StationsModel>) getIntent().getExtras().getSerializable("modelListStation");
		modelListBus=(ArrayList<CurrentBusModel>) getIntent().getExtras().getSerializable("modelListBus");
		
		init(savedInstanceState);
	}
	
	private void init(Bundle savedInstanceState) {
		
		line_left=(LinearLayout) findViewById(R.id.line_left);
		line_left.setVisibility(View.VISIBLE);
		title_name=(TextView) findViewById(R.id.title_name);
		title_name.setText(getIntent().getExtras().getString("lineName")+"实时动态地图");
		title_left=(ImageView) findViewById(R.id.title_left);
		title_left.setImageResource(R.drawable.icon_back);
		title_left.setVisibility(View.VISIBLE);
		title_left.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});
		mapsearch_tip=(TextView) findViewById(R.id.mapsearch_tip);
		mapsearch_tip.setText("由于GPS定位存在偏差，公交车位置显示会有误差\n由此给您带来的不便敬请谅解");
		mapsearch_tip.setVisibility(View.VISIBLE);
		mapsearchview=(MapView) findViewById(R.id.mapsearchview);
		mapsearchview.onCreate(savedInstanceState);// 此方法必须重写
		if(aMap==null) {
			aMap=mapsearchview.getMap();
			
			aMap.setOnMapClickListener(this);// 对amap添加单击地图事件监听器
			aMap.setOnMapLongClickListener(this);// 对amap添加长按地图事件监听器
			aMap.setOnCameraChangeListener(this);// 对amap添加移动地图事件监听器
			aMap.setOnMapLoadedListener(this);// 设置amap加载成功事件监听器
			aMap.setOnMarkerDragListener(this);// 设置marker可拖拽事件监听器
			aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
			aMap.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听器
			aMap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式	
			changeCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(32.041722, 117.784158), 17, 0, 0)), this, false);		
		}
		
		IntentFilter filter=new IntentFilter();
		filter.addAction(ResultActivity.actionRefresh);
		registerReceiver(receiver, filter);
	}
	
	BroadcastReceiver receiver =new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals(ResultActivity.actionRefresh)&&isAllowRefresh) {
				SearchByRotateMapActivity.this.modelListBus=(ArrayList<CurrentBusModel>) intent.getExtras().getSerializable("modelListBus");
				aMap.clear();
				if(lineItems!=null&&lineItems.size()>0) {
					BusLineOverlay busLineOverlay=new BusLineOverlay(SearchByRotateMapActivity.this, aMap, lineItems.get(0));
					busLineOverlay.removeFromMap();
					busLineOverlay.addToMap();					
				}
				else {
					loadStaion(false);
				}
				loadBus();
			}
		}};
	
	
	private void changeCamera(CameraUpdate update, CancelableCallback callback, boolean isAnimate) {
		if(isAnimate) {
			aMap.animateCamera(update, 500, callback);
		}
		else {
			aMap.moveCamera(update);
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mapsearchview.onResume();
		StatService.onResume(this);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		mapsearchview.onSaveInstanceState(outState);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mapsearchview.onPause();
		StatService.onPause(this);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mapsearchview.onDestroy();
		unregisterReceiver(receiver);
	}

	@Override
	public void onCancel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFinish() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCameraChange(CameraPosition arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCameraChangeFinish(CameraPosition arg0) {
		// TODO Auto-generated method stub
		LatLng finishLatLng=arg0.target;
		System.out.println("onCameraChangeFinish:"+finishLatLng.latitude+" "+finishLatLng.longitude);
	}

	@Override
	public void onMapLongClick(LatLng arg0) {
		// TODO Auto-generated method stub
		System.out.println("onMapLongClick:"+arg0.latitude+" "+arg0.longitude);
	}

	@Override
	public void onMapClick(LatLng arg0) {
		// TODO Auto-generated method stub
		System.out.println("onMapClick:"+arg0.latitude+" "+arg0.longitude);
	}

	@Override
	public void onMapLoaded() {
		// TODO Auto-generated method stub
		searchBusLineName();
	}
	
	private void loadStaion(boolean isInclude) {
		if(isInclude) {
			Builder builders=new LatLngBounds.Builder();
			for(int i=0;i<modelListStation.size();i++) {
				if(i==0) {
					addMarker(new LatLng(modelListStation.get(i).getMap_station_lat(), modelListStation.get(i).getMap_station_long()), modelListStation.get(i).getStation_name(), R.drawable.amap_start, i);
				}
				else if(i==modelListStation.size()-1) {
					addMarker(new LatLng(modelListStation.get(i).getMap_station_lat(), modelListStation.get(i).getMap_station_long()), modelListStation.get(i).getStation_name(), R.drawable.amap_end, i);
				}
				else {
					addMarker(new LatLng(modelListStation.get(i).getMap_station_lat(), modelListStation.get(i).getMap_station_long()), modelListStation.get(i).getStation_name(), R.drawable.amap_bus, i);
				}				
				builders.include(new LatLng(modelListStation.get(i).getMap_station_lat(), modelListStation.get(i).getMap_station_long()));
			}
			aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builders.build(), 20));
		}
		else {
			for(int i=0;i<modelListStation.size();i++) {
				if(i==0) {
					addMarker(new LatLng(modelListStation.get(i).getMap_station_lat(), modelListStation.get(i).getMap_station_long()), modelListStation.get(i).getStation_name(), R.drawable.amap_start, i);
				}
				else if(i==modelListStation.size()-1) {
					addMarker(new LatLng(modelListStation.get(i).getMap_station_lat(), modelListStation.get(i).getMap_station_long()), modelListStation.get(i).getStation_name(), R.drawable.amap_end, i);
				}
				else {
					addMarker(new LatLng(modelListStation.get(i).getMap_station_lat(), modelListStation.get(i).getMap_station_long()), modelListStation.get(i).getStation_name(), R.drawable.amap_bus, i);
				}				
			}
		}
	}
	
	private void loadBus() {
		for(int i=0;i<modelListBus.size();i++) {
			MapConverter convert=new MapConverter();
			Point point=convert.getEncryPoint(modelListBus.get(i).getBusLong(), modelListBus.get(i).getBusLat());
			addMarker(new LatLng(point.getY(), point.getX()), "", R.drawable.icon_bus_pos, -1);

			//String result=CommonUtils.transform(modelListBus.get(i).getBusLat(), modelListBus.get(i).getBusLong());
			//addMarker(new LatLng(Double.parseDouble(result.split("-")[0]), Double.parseDouble(result.split("-")[1])), "", R.drawable.icon_bus_pos, -1);
		}
		isAllowRefresh=true;
	}
	
	private void addMarker(LatLng latlng, String stationName, int pic, int pos) {
		MarkerOptions markerOption = new MarkerOptions();
		markerOption.position(latlng);
		if(pos==-1) {
			markerOption.title("");
		}
		else {
			markerOption.title("("+(pos+1)+")"+stationName);
		}
		markerOption.snippet("");
		markerOption.perspective(true);
		markerOption.draggable(false);
		markerOption.icon(BitmapDescriptorFactory.fromResource(pic));
		aMap.addMarker(markerOption);			
	}
	
	private void searchBusLineName() {
		busLineQuery=new BusLineQuery(getIntent().getExtras().getString("lineName"), SearchType.BY_LINE_NAME, "025");// 第一个参数表示公交线路名，第二个参数表示公交线路查询，第三个参数表示所在城市名或者城市区号
		busLineQuery.setPageSize(100);// 设置每页返回多少条数据
		busLineQuery.setPageNumber(0);// 设置查询第几页，第一页从0开始算起
		BusLineSearch busLineSearch=new BusLineSearch(this, busLineQuery);// 设置条件
		busLineSearch.setOnBusLineSearchListener(this);// 设置查询结果的监听
		busLineSearch.searchBusLineAsyn();// 异步查询公交线路名称
	}
	
	private void searchBusLineId(String lineId) {
		busLineQuery=new BusLineQuery(lineId, SearchType.BY_LINE_ID, "025");// 第一个参数表示公交线路id，第二个参数表示公交线路id查询，第三个参数表示所在城市名或者城市区号
		BusLineSearch busLineSearch=new BusLineSearch(SearchByRotateMapActivity.this, busLineQuery);
		busLineSearch.setOnBusLineSearchListener(SearchByRotateMapActivity.this);
		busLineSearch.searchBusLineAsyn();// 异步查询公交线路id
	}
	
	@Override
	public View getInfoContents(Marker arg0) {
		// TODO Auto-generated method stub
		View infoContent=getLayoutInflater().inflate(R.layout.custom_info_contents_view, null);
		render(arg0, infoContent);
		return infoContent;
	}

	@Override
	public View getInfoWindow(Marker arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMarkerDrag(Marker arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMarkerDragEnd(Marker arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMarkerDragStart(Marker arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onInfoWindowClick(Marker arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onMarkerClick(Marker arg0) {
		// TODO Auto-generated method stub
		return false;
	}	
	
	/**
	 * 自定义infowinfow窗口
	 */
	public void render(Marker marker, View view) {
		if(CommonUtils.convertNull(marker.getTitle()).equals("")&&CommonUtils.convertNull(marker.getSnippet()).equals("")) {
			view.setVisibility(View.GONE);
		}
		String title=marker.getTitle();
		TextView titleUi=(TextView) view.findViewById(R.id.title);
		titleUi.setText(title);
		if (CommonUtils.convertNull(title).equals("")) {
			titleUi.setVisibility(View.GONE);
		} else {
			titleUi.setVisibility(View.VISIBLE);
		}
		String snippet=marker.getSnippet();
		TextView snippetUi=(TextView) view.findViewById(R.id.snippet);
		snippetUi.setText(snippet);
		if (CommonUtils.convertNull(snippet).equals("")) {
			snippetUi.setVisibility(View.GONE);
		} else {
			snippetUi.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onBusStationSearched(BusStationResult arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onBusLineSearched(BusLineResult arg0, int arg1) {
		// TODO Auto-generated method stub
		if(arg1==0) {
			if (arg0!=null&&arg0.getQuery()!=null&&arg0.getQuery().equals(busLineQuery)) {
				if(arg0.getQuery().getCategory()==SearchType.BY_LINE_NAME) {
					if (arg0.getPageCount()>0&&arg0.getBusLines()!=null&&arg0.getBusLines().size()>0) {
						List<BusLineItem> lineItems=arg0.getBusLines();
						boolean isFind=false;
						for(int i=0;i<lineItems.size();i++) {
							
							int luIndex=lineItems.get(i).getBusLineName().indexOf("路");
							String lu=lineItems.get(i).getBusLineName().substring(0, luIndex+1);
							
							int _leftIndex=lineItems.get(i).getBusLineName().indexOf("(");
							int _rightIndex=lineItems.get(i).getBusLineName().length();
							String _name=lineItems.get(i).getBusLineName().substring(_leftIndex+1, _rightIndex-1);
							String startName=_name.split("--")[0];
							String endName=_name.split("--")[1];
							
							System.out.println(lineItems.get(i).getBusLineName()+" "+lineItems.get(i).getBusLineId());	
							if(lu.equals(getIntent().getExtras().getString("lineName"))&&
									(startName.equals(modelListStation.get(0).getStation_name())||endName.equals(modelListStation.get(modelListStation.size()-1).getStation_name()))) {
								isFind=true;
								searchBusLineId(lineItems.get(i).getBusLineId());
							}
						}
						if(!isFind) {
							loadStaion(true);
							loadBus();
						}
					}
					else {
						loadStaion(true);
						loadBus();
					}
				}
				else if (arg0.getQuery().getCategory()==SearchType.BY_LINE_ID) {
					aMap.clear();// 清理地图上的marker
					lineItems=arg0.getBusLines();
					if(lineItems!=null&&lineItems.size()>0) {
						BusLineOverlay busLineOverlay=new BusLineOverlay(this, aMap, lineItems.get(0));
						busLineOverlay.removeFromMap();
						busLineOverlay.addToMap();
						busLineOverlay.zoomToSpan();
						loadBus();
					}
					else {
						loadStaion(true);
						loadBus();
					}
				}
			}			
		}
	}
	
}
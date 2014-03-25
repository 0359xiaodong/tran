package com.renyu.nj_tran.search;

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
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.baidu.mobstat.StatService;
import com.renyu.nj_tran.R;
import com.renyu.nj_tran.commons.CommonUtils;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SearchByStationNameMapActivity extends Activity implements 
	OnMapLoadedListener, 
	CancelableCallback, 
	OnMapClickListener, 
	OnMapLongClickListener, 
	OnCameraChangeListener,
	OnMarkerClickListener,
	OnInfoWindowClickListener, 
	OnMarkerDragListener,
	OnClickListener, 
	InfoWindowAdapter {
	
	LinearLayout line_left=null;
	TextView title_name=null;
	ImageView title_left=null;
	
	private AMap aMap;
	MapView mapsearchview=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		MapsInitializer.sdcardDir=CommonUtils.getSdCacheDir(SearchByStationNameMapActivity.this);
		
		setContentView(R.layout.activity_mapsearch);
		
		init(savedInstanceState);
	}
	
	private void init(Bundle savedInstanceState) {
		line_left=(LinearLayout) findViewById(R.id.line_left);
		line_left.setVisibility(View.VISIBLE);
		title_name=(TextView) findViewById(R.id.title_name);
		title_name.setText(getIntent().getExtras().getString("desp")+"位置地图");
		title_left=(ImageView) findViewById(R.id.title_left);
		title_left.setImageResource(R.drawable.icon_back);
		title_left.setVisibility(View.VISIBLE);
		title_left.setOnClickListener(new ImageView.OnClickListener() {
	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});
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
			
			addCurrentLocation(new LatLng(getIntent().getExtras().getDouble("geoLat"), getIntent().getExtras().getDouble("geoLng")), getIntent().getExtras().getString("desp"));
			changeCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(getIntent().getExtras().getDouble("geoLat"), getIntent().getExtras().getDouble("geoLng")), 17, 0, 0)), this, false);
		}
	}
	
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
		
	}
	
	@Override
	public void onMapLongClick(LatLng arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onMapClick(LatLng arg0) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void onMapLoaded() {
		// TODO Auto-generated method stub
		
	}
		
	/**
	 * 添加当前位置坐标	
	 * @param latLng
	 * @param desp
	 */
	private void addCurrentLocation(LatLng latLng, String desp) {
		
	//	ArrayList<BitmapDescriptor> giflist = new ArrayList<BitmapDescriptor>();
	//	giflist.add(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
	//	giflist.add(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
	//	giflist.add(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
	//	Marker me=aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).position(latLng).icons(giflist).perspective(true).draggable(true).period(50));
	//	me.showInfoWindow();
		
		MarkerOptions markerOption = new MarkerOptions();
		markerOption.position(latLng);
		markerOption.perspective(true);
		markerOption.draggable(false);
		View view=LayoutInflater.from(SearchByStationNameMapActivity.this).inflate(R.layout.custom_busstaiton_view, null);
		TextView station_name=(TextView) view.findViewById(R.id.station_name);
		station_name.setText(desp);
		markerOption.icon(BitmapDescriptorFactory.fromBitmap(CommonUtils.convertViewToBitmap(view)));
		aMap.addMarker(markerOption);
		
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

}

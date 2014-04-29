package com.renyu.nj_tran.currentstation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnCameraChangeListener;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapLoadedListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.AMap.CancelableCallback;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.LatLngBounds.Builder;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.baidu.mobstat.StatService;
import com.renyu.nj_tran.R;
import com.renyu.nj_tran.busresult.ResultActivity;
import com.renyu.nj_tran.commons.CommonUtils;
import com.renyu.nj_tran.commons.Conn;
import com.renyu.nj_tran.model.ArroundStationModel;
import com.renyu.nj_tran.model.BusLineModel;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CurrentStationMapActivity extends Activity implements 
	OnMapLoadedListener, 
	CancelableCallback, 
	OnCameraChangeListener,
	OnMarkerClickListener,
	OnInfoWindowClickListener, 
	OnClickListener, 
	InfoWindowAdapter {

	LinearLayout line_left=null;
	TextView title_name=null;
	ImageView title_left=null;
	
	private AMap aMap;
	MapView mapsearchview=null;
	
	ArrayList<ArroundStationModel> modelList=null;
	List<HashMap<String, Object>> lists=null;
	//我的位置
	LatLng myLatLng=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		MapsInitializer.sdcardDir=CommonUtils.getSdCacheDir(CurrentStationMapActivity.this);
		
		setContentView(R.layout.activity_mapsearch);
		
		modelList=new ArrayList<ArroundStationModel>();
		lists=new ArrayList<HashMap<String, Object>>();
		
		init(savedInstanceState);
	}
	
	private void init(Bundle savedInstanceState) {
		line_left=(LinearLayout) findViewById(R.id.line_left);
		line_left.setVisibility(View.VISIBLE);
		title_name=(TextView) findViewById(R.id.title_name);
		title_name.setText("周围站点地图");
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
			
			aMap.setOnCameraChangeListener(this);// 对amap添加移动地图事件监听器
			aMap.setOnMapLoadedListener(this);// 设置amap加载成功事件监听器
			aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
			aMap.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听器
			aMap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式
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
		LatLng finishLatLng=arg0.target;
		System.out.println(finishLatLng.latitude+" "+finishLatLng.longitude);
	}

	@Override
	public void onMapLoaded() {
		// TODO Auto-generated method stub

		if(getIntent().getExtras().getDouble("geoLat")!=0&&getIntent().getExtras().getDouble("geoLng")!=0) {
			addCurrentLocation(new LatLng(getIntent().getExtras().getDouble("geoLat"), getIntent().getExtras().getDouble("geoLng")), getIntent().getExtras().getString("desp"));
			changeCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(getIntent().getExtras().getDouble("geoLat"), getIntent().getExtras().getDouble("geoLng")), 17, 0, 0)), this, false);
		}
		else {
			changeCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(32.041804,118.78418), 17, 0, 0)), this, false);
		}
	}
	
	/**
	 * 添加当前位置坐标	
	 * @param latLng
	 * @param desp
	 */
	private void addCurrentLocation(LatLng latLng, String desp) {
		
//		ArrayList<BitmapDescriptor> giflist = new ArrayList<BitmapDescriptor>();
//		giflist.add(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
//		giflist.add(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//		giflist.add(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
//		Marker me=aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).position(latLng).icons(giflist).perspective(true).draggable(true).period(50));
//		me.showInfoWindow();
		
		MarkerOptions markerOption=new MarkerOptions();
		markerOption.anchor(0.5f, 0.5f);
		markerOption.position(latLng);
		markerOption.perspective(true);
		markerOption.draggable(true);
		markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_my));
		aMap.addMarker(markerOption);
		
		getArroundStation(latLng.longitude, latLng.latitude);
	}
	
	/**
	 * 获取周围车站信息
	 * @param geoLng
	 * @param geoLat
	 */
	public void getArroundStation(final double geoLng, final double geoLat) {
		myLatLng=new LatLng(geoLat, geoLng);
		modelList.addAll((ArrayList<ArroundStationModel>) getIntent().getExtras().getSerializable("modelList"));
		Builder builders=new LatLngBounds.Builder().include(myLatLng);
		for(int i=0;i<modelList.size();i++) {
			if(modelList.get(i).getLids_list().size()==0) {
				continue;
			}
			HashMap<String, Object> map=new HashMap<String, Object>();
			map.put("a_s_name", modelList.get(i).getName());
			String libs_="";
			for(int j=0;j<modelList.get(i).getLids_list().size();j++) {
				libs_+=modelList.get(i).getLids_list().get(j)+"/";
			}
			map.put("a_s_libs", libs_.substring(0, libs_.length()-1));
			lists.add(map);
			
			MarkerOptions markerOption = new MarkerOptions();
			markerOption.anchor(0.5f, 0.5f);
			markerOption.position(new LatLng(modelList.get(i).getMap_lat(), modelList.get(i).getMap_long()));
			markerOption.title(modelList.get(i).getName());
			markerOption.snippet(libs_.substring(0, libs_.length()-1));
			markerOption.perspective(true);
			markerOption.draggable(false);
			View view=LayoutInflater.from(CurrentStationMapActivity.this).inflate(R.layout.custom_busstaiton_view, null);
			TextView station_name=(TextView) view.findViewById(R.id.station_name);
			station_name.setText(modelList.get(i).getName());
			markerOption.icon(BitmapDescriptorFactory.fromBitmap(CommonUtils.convertViewToBitmap(view)));
			aMap.addMarker(markerOption);
			
			builders.include(new LatLng(modelList.get(i).getMap_lat(), modelList.get(i).getMap_long()));
		}
		aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builders.build(), 20));
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
	public void onInfoWindowClick(Marker arg0) {
		// TODO Auto-generated method stub
		if(arg0.getTitle()==null) {
			return ;
		}
		int position=0;
		for(int i=0;i<modelList.size();i++) {
			if(modelList.get(i).getName().equals(arg0.getTitle())) {
				position=i;
			}
		}
		final String a_s_libs_obj=lists.get(position).get("a_s_libs").toString();		
		final String stationName=lists.get(position).get("a_s_name").toString();
		new AlertDialog.Builder(CurrentStationMapActivity.this).setTitle("请您选择查询车次").setItems(a_s_libs_obj.split("/"), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				final ArrayList<BusLineModel> busLineModelList=Conn.getInstance(getApplicationContext()).getTranInfoDirect(a_s_libs_obj.split("/")[which], true);
				String[] array=new String[busLineModelList.size()];
				for(int i=0;i<busLineModelList.size();i++) {
					array[i]=busLineModelList.get(i).getLine_name()+" "+busLineModelList.get(i).getStart_from()+"-->"+busLineModelList.get(i).getEnd_location();
				}
				new AlertDialog.Builder(CurrentStationMapActivity.this).setTitle("请您选择该车次线路").setItems(array, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Intent intent=new Intent(CurrentStationMapActivity.this, ResultActivity.class);
						Bundle bundle=new Bundle();
						bundle.putString("lineName", busLineModelList.get(which).getLine_name());
						bundle.putString("stationName", stationName);
						bundle.putInt("lineId", busLineModelList.get(which).getId());
						intent.putExtras(bundle);
						startActivity(intent);
					}}).show();
			}}).show();
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

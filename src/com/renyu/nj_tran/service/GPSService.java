package com.renyu.nj_tran.service;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.renyu.nj_tran.TranApplication;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;

public class GPSService extends Service implements AMapLocationListener {	
	
	public final static String GPSACTION="gpsaction";

	private LocationManagerProxy mAMapLocManager=null;
	private AMapLocation aMapLocation;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		mAMapLocManager=LocationManagerProxy.getInstance(this);
		mAMapLocManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork, 5000, 10, this);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

	
	
	private void stopLocation() {
		if (mAMapLocManager!=null) {
			mAMapLocManager.removeUpdates(this);
			mAMapLocManager.destory();
		}
		mAMapLocManager=null;
	}
	
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLocationChanged(AMapLocation location) {
		// TODO Auto-generated method stub
		if (location != null) {
			this.aMapLocation=location;// 判断超时机制
			Double geoLat=location.getLatitude();
			Double geoLng=location.getLongitude();
			String desc="";
			Bundle locBundle=location.getExtras();
			if (locBundle!=null) {
				desc=locBundle.getString("desc");
			}
			stopLocation();
			
			((TranApplication) getApplicationContext()).currentLatLng=geoLat+"&"+geoLng;
			
			//发送广播刷新UI
			Intent intent=new Intent();
			Bundle bundle=new Bundle();
			bundle.putString("desc", desc);
			bundle.putDouble("geoLng", geoLng);
			bundle.putDouble("geoLat", geoLat);
			intent.putExtras(bundle);
			intent.setAction(GPSACTION);
			sendBroadcast(intent);
			stopSelf();
		}
	}

}

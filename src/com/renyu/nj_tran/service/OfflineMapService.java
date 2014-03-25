package com.renyu.nj_tran.service;

import com.amap.api.maps.offlinemap.OfflineMapManager;
import com.amap.api.maps.offlinemap.OfflineMapStatus;
import com.amap.api.maps.offlinemap.OfflineMapManager.OfflineMapDownloadListener;
import com.renyu.nj_tran.commons.CommonUtils;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

public class OfflineMapService extends Service implements OfflineMapDownloadListener {

	private OfflineMapManager amapManager=null;
	
	//是否正在下载
	public static boolean isDownload=false;
	//离线地图下载广播
	public final static String DOWNLOADBROADCAST="downloadbroadcast";
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		amapManager=new OfflineMapManager(this, this);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		if(intent==null) {
			return super.onStartCommand(intent, flags, startId);
		}
		if(intent.getExtras().getString("type").equals("start")) {
			try {
				boolean start=amapManager.downloadByCityName("南京");
				if (!start) {
					Toast.makeText(OfflineMapService.this, "下载离线地图数据失败", 3000).show();
					isDownload=false;
				} else {
					Toast.makeText(OfflineMapService.this, "开始下载离线地图数据", 3000).show();
					isDownload=true;
				}
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(OfflineMapService.this, "开启下载失败，请检查网络是否开启！", 3000).show();
				isDownload=false;
			}
		}
		else if(intent.getExtras().getString("type").equals("pause")) {
			amapManager.pause();
			Toast.makeText(OfflineMapService.this, "暂停下载离线地图数据", 3000).show();
			isDownload=false;
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDownload(int arg0, int arg1, String arg2) {
		// TODO Auto-generated method stub
		Intent intent=new Intent();
		intent.setAction(DOWNLOADBROADCAST);
		Bundle bundle=new Bundle();
		bundle.putInt("state", arg0);
		switch (arg0) {
		case OfflineMapStatus.SUCCESS:
			isDownload=false;
			CommonUtils.setOfflineVersion(OfflineMapService.this, Integer.parseInt(amapManager.getItemByCityName("南京").getVersion()));
			System.out.println("下载成功");
			break;
		case OfflineMapStatus.LOADING:
			System.out.println("正在下载");
			bundle.putInt("num", arg1);
			break;
		case OfflineMapStatus.UNZIP:
			isDownload=false;
			CommonUtils.setOfflineVersion(OfflineMapService.this, Integer.parseInt(amapManager.getItemByCityName("南京").getVersion()));
			System.out.println("正在解压");
			break;
		case OfflineMapStatus.WAITING:
			System.out.println("正在等待");
			break;
		case OfflineMapStatus.PAUSE:
			System.out.println("暂停下载");
			break;
		case OfflineMapStatus.STOP:
			System.out.println("停止下载");
			break;
		case OfflineMapStatus.ERROR:
			System.out.println("下载错误");
			break;
		default:
			break;
		}
		intent.putExtras(bundle);
		sendBroadcast(intent);
	}

}

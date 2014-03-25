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
	
	//�Ƿ���������
	public static boolean isDownload=false;
	//���ߵ�ͼ���ع㲥
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
				boolean start=amapManager.downloadByCityName("�Ͼ�");
				if (!start) {
					Toast.makeText(OfflineMapService.this, "�������ߵ�ͼ����ʧ��", 3000).show();
					isDownload=false;
				} else {
					Toast.makeText(OfflineMapService.this, "��ʼ�������ߵ�ͼ����", 3000).show();
					isDownload=true;
				}
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(OfflineMapService.this, "��������ʧ�ܣ����������Ƿ�����", 3000).show();
				isDownload=false;
			}
		}
		else if(intent.getExtras().getString("type").equals("pause")) {
			amapManager.pause();
			Toast.makeText(OfflineMapService.this, "��ͣ�������ߵ�ͼ����", 3000).show();
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
			CommonUtils.setOfflineVersion(OfflineMapService.this, Integer.parseInt(amapManager.getItemByCityName("�Ͼ�").getVersion()));
			System.out.println("���سɹ�");
			break;
		case OfflineMapStatus.LOADING:
			System.out.println("��������");
			bundle.putInt("num", arg1);
			break;
		case OfflineMapStatus.UNZIP:
			isDownload=false;
			CommonUtils.setOfflineVersion(OfflineMapService.this, Integer.parseInt(amapManager.getItemByCityName("�Ͼ�").getVersion()));
			System.out.println("���ڽ�ѹ");
			break;
		case OfflineMapStatus.WAITING:
			System.out.println("���ڵȴ�");
			break;
		case OfflineMapStatus.PAUSE:
			System.out.println("��ͣ����");
			break;
		case OfflineMapStatus.STOP:
			System.out.println("ֹͣ����");
			break;
		case OfflineMapStatus.ERROR:
			System.out.println("���ش���");
			break;
		default:
			break;
		}
		intent.putExtras(bundle);
		sendBroadcast(intent);
	}

}

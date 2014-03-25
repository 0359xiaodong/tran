package com.renyu.nj_tran.setting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.offlinemap.OfflineMapStatus;
import com.renyu.nj_tran.R;
import com.renyu.nj_tran.commons.CommonUtils;
import com.renyu.nj_tran.service.OfflineMapService;
import com.renyu.nj_tran.service.UpdateService;

public class SettingFragment extends Fragment {
	
	View view=null;
	
	TextView title_name=null;
	
	LinearLayout setting_offline=null;
	TextView download_state=null;
	LinearLayout setting_update=null;
	LinearLayout setting_about=null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		// 设置应用单独的地图存储目录，在下载离线地图或初始化地图时设置
		if(CommonUtils.getSdCacheDir(getActivity())==null) {
			return;
		}
		MapsInitializer.sdcardDir=CommonUtils.getSdCacheDir(getActivity());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if(view==null) {
			view=LayoutInflater.from(getActivity()).inflate(R.layout.fragment_setting, null);
			
			title_name=(TextView) view.findViewById(R.id.title_name);
			title_name.setText("个人设置");	
			
			setting_offline=(LinearLayout) view.findViewById(R.id.setting_offline);
			setting_offline.setOnClickListener(new LinearLayout.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(CommonUtils.getOfflineVersion(getActivity())==0) {
						if(OfflineMapService.isDownload) {
							controllOfflineMapDownload("pause");
						}
						else {
							if(CommonUtils.checkNetworkInfo(getActivity())==1) {
								new AlertDialog.Builder(getActivity()).setTitle("提示").setMessage("您当前正在使用蜂窝网络，下载离线地图会消耗您的手机流量，是否继续下载？").setPositiveButton("确定", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										controllOfflineMapDownload("start");	
									}}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
											
									}}).show();
							}
							else if(CommonUtils.checkNetworkInfo(getActivity())==2) {
								controllOfflineMapDownload("start");								
							}
							else if(CommonUtils.checkNetworkInfo(getActivity())==3) {
								Toast.makeText(getActivity(), "请您先打开网络连接，再尝试下载", 3000).show();
							}
						}
						
						
					}
				}});
			
			download_state=(TextView) view.findViewById(R.id.download_state);
			if(CommonUtils.getOfflineVersion(getActivity())==0) {
				download_state.setText("未下载");
			}
			else {
				download_state.setText("已下载");
			}
			IntentFilter filter=new IntentFilter();
			filter.addAction(OfflineMapService.DOWNLOADBROADCAST);
			getActivity().registerReceiver(receiver, filter);
			
			setting_update=(LinearLayout) view.findViewById(R.id.setting_update);
			setting_update.setOnClickListener(new LinearLayout.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent=new Intent(getActivity(), UpdateService.class);
					Bundle bundle=new Bundle();
					bundle.putString("from", "setting");
					intent.putExtras(bundle);
					getActivity().startService(intent);
				}});
			
			setting_about=(LinearLayout) view.findViewById(R.id.setting_about);
			setting_about.setOnClickListener(new LinearLayout.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent=new Intent(getActivity(), AboutActivity.class);
					getActivity().startActivity(intent);
				}});
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
	
	BroadcastReceiver receiver=new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals(OfflineMapService.DOWNLOADBROADCAST)) {
				switch(intent.getExtras().getInt("state")) {
				case OfflineMapStatus.SUCCESS:
					download_state.setText("已下载");
					break;
				case OfflineMapStatus.LOADING:
					download_state.setText("已下载"+intent.getExtras().getInt("num")+"%");
					break;
				case OfflineMapStatus.UNZIP:
					download_state.setText("正在解压");
					break;
				case OfflineMapStatus.WAITING:
					download_state.setText("正在等待");
					break;
				case OfflineMapStatus.PAUSE:
					download_state.setText("暂停");
					break;
				case OfflineMapStatus.STOP:
					download_state.setText("停止");
					break;
				case OfflineMapStatus.ERROR:
					download_state.setText("未下载");
					break;
				default:
					break; 
				}
			}
		}};
	
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(receiver);
	};
	
	public void controllOfflineMapDownload(String state) {
		Intent intent=new Intent(getActivity(), OfflineMapService.class);
		Bundle bundle=new Bundle();
		bundle.putString("type", state);
		intent.putExtras(bundle);
		getActivity().startService(intent);
	}
}

package com.renyu.nj_tran.service;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import com.renyu.nj_tran.TranApplication;
import com.renyu.nj_tran.commons.CommonUtils;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.WindowManager;
import android.widget.Toast;

public class UpdateService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		if(intent!=null) {
			checkUpdate(intent.getExtras().getString("from"));
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	public void checkUpdate(final String from) {
		if(from.equals("setting")) {
			Toast.makeText(UpdateService.this, "正在检测新版本", 3000).show();
		}		
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.obj!=null) {
					String result="";
					try {
						result=new String(msg.obj.toString().getBytes("iso-8859-1"), "utf-8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(result.equals("")) {
						if(from.equals("setting")) {
							Toast.makeText(UpdateService.this, "您目前使用的是最新版本", 3000).show();
						}					
						return ;
					} 
					final String downloadresult=result;
					//在连接cmcc等网络时会产生异常
					try {
						int versionCode=Integer.parseInt(result.split("-")[0]);
						if(versionCode>CommonUtils.getVersionCode(UpdateService.this)) {
							
							AlertDialog.Builder builder=new AlertDialog.Builder(UpdateService.this);
					        builder.setTitle("升级提醒");
					        builder.setMessage(result.split("-")[1]);
					        builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									Intent intent=new Intent(UpdateService.this, DownloadService.class);
									Bundle bundle=new Bundle();
									bundle.putString("download_url", downloadresult.split("-")[2]);
									bundle.putString("download_version", downloadresult.split("-")[0]);
									bundle.putString("download_id", TranApplication.UPDATE);
									intent.putExtras(bundle);
									startService(intent);
								}});
					        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									
								}});
							final AlertDialog dialog=builder.create(); 
					        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
					        dialog.setCanceledOnTouchOutside(false); 
					        dialog.show(); 
						}
						else {
							if(from.equals("setting")) {
								Toast.makeText(UpdateService.this, "您目前使用的是最新版本", 3000).show();
							}					
						}
					} catch(Exception e) {
						e.printStackTrace();
					}
					
				}				
				stopSelf();
			}
		};
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message m=new Message();
				HashMap<String, String> map=new HashMap<String, String>();
				m.obj=CommonUtils.getWebData(map, "http://nanjing.sinaapp.com/main.php");
				handler.sendMessage(m);
			}
		}).start();
	}
	
}

package com.renyu.nj_tran.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import com.renyu.nj_tran.R;
import com.renyu.nj_tran.TranApplication;
import com.renyu.nj_tran.commons.CommonUtils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;
import android.widget.Toast;

public class DownloadService extends Service {
	
	static DownloadService instance=null;	
	
	RemoteViews views=null;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		
		if(intent==null) {
			return super.onStartCommand(intent, flags, startId);
		}
		if(((TranApplication) getApplication()).getAllTask().contains(intent.getExtras().getString("download_id"))) {
			return super.onStartCommand(intent, flags, startId);
		}
		else {
			((TranApplication) getApplication()).getAllTask().add(intent.getExtras().getString("download_id"));
		}
		String download_url=intent.getExtras().getString("download_url");
		String download_name=getResources().getString(R.string.app_name);
		String download_id=intent.getExtras().getString("download_id");
		String download_version=intent.getExtras().getString("download_version");
		
		NotificationManager manager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		Notification no=new Notification();
		no.flags=Notification.FLAG_AUTO_CANCEL;
		no.icon=R.drawable.ic_launcher;
		no.when=System.currentTimeMillis();
		no.tickerText=download_name+"下载提示";
		views=new RemoteViews(getApplication().getPackageName(), R.layout.barstyle);
		no.contentView=views;
		no.contentView.setProgressBar(R.id.downloadbarpb, 100, 0, false);
		no.contentView.setTextViewText(R.id.downloadbartest1, download_name+"准备下载中");
		no.contentView.setTextViewText(R.id.downloadbartest2, "0%");
		no.contentIntent=PendingIntent.getActivity(DownloadService.this, 0, new Intent(DownloadService.this, NotificationActivity.class), 0);
		manager.notify(Integer.parseInt(download_id), no);
		downloadFile(download_url, download_version, download_id, download_name, no, manager);
	
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	public void downloadFile(final String url_, final String download_version, final String download_id, final String download_name, final Notification no, final NotificationManager manager) {
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch(msg.what) {
				case 1:
					views.setProgressBar(R.id.downloadbarpb, 100, Integer.parseInt(msg.obj.toString()), false);
					views.setTextViewText(R.id.downloadbartest2, msg.obj.toString()+"%");
					views.setTextViewText(R.id.downloadbartest1, download_name+"正在下载中");
					no.contentView=views;
					manager.notify(Integer.parseInt(download_id), no);
					break;
				case 2:
					no.defaults=Notification.DEFAULT_SOUND;
					manager.cancel(Integer.parseInt(download_id));
					stopSelf();
					Toast.makeText(DownloadService.this, "下载完成", 3000).show();
					//安装软件
					Intent intent=new Intent();
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setAction(Intent.ACTION_VIEW);
					String type="application/vnd.android.package-archive";
					intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory().getPath()+"/nj_tran/"+(download_id+download_version)+".apk")), type);
					startActivity(intent);
					break;
				case 3:
					manager.cancel(Integer.parseInt(download_id));
					stopSelf();
					Toast.makeText(DownloadService.this, "下载失败，请稍后再试", 3000).show();
					break;
				}
				((TranApplication) getApplication()).getAllTask().remove(download_id);
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				File file=new File(Environment.getExternalStorageDirectory().getPath()+"/nj_tran/"+(download_id+download_version)+".apk");
				int completeSize=0;
				if(file.exists()) {
					FileInputStream fis;
					try {
						fis = new FileInputStream(file);
						completeSize=fis.available();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}					
				}				
				long fileSize=init(url_);
				HttpURLConnection conn=null;
				RandomAccessFile raf=null;
				InputStream is=null;
				
				try {
					URL u = new URL(url_);
					conn=(HttpURLConnection) u.openConnection();
					conn.setConnectTimeout(10000);
					conn.setRequestMethod("GET");
					//文件已经下载完毕或者已经存在
					if(completeSize==fileSize) {
						if(CommonUtils.checkAPKState(DownloadService.this, file.getPath())) {
							Message m=new Message();
							m.what=2;
							handler.sendMessage(m);
							return;
						}
						else {
							completeSize=0;
						}
					}
					else if(completeSize>fileSize) {
						file.delete();
						completeSize=0;
					}

					//conn.setRequestProperty("Range", "bytes="+completeSize+"-"+(fileSize-1));
					// 设置范围，格式为Range：bytes x-y;
					conn.setRequestProperty("User-Agent", "NetFox");
					String sProperty = "bytes=" + completeSize + "-" + fileSize;
					conn.setRequestProperty("RANGE", sProperty);
					
					File file_new=new File(Environment.getExternalStorageDirectory().getPath()+"/nj_tran/"+(download_id+download_version)+".apk");
	            	raf=new RandomAccessFile(file_new, "rw");
	            	raf.seek(completeSize);
	            	is=conn.getInputStream();
	            	byte[] b=new byte[1024];
	            	long total=completeSize;
	            	int count=0;
	            	//记录上一次下载的百分比
					int downloadPercent=0;
					while((count=is.read(b, 0, 1024))!=-1) {
	            		raf.write(b, 0, count);
	            		total+=count;
						//System.out.println(total+"  "+fileSize);
						int percent=(int) (total*100/fileSize);
						if(percent-downloadPercent>5) {
							Message m=new Message();
							m.what=1;
							m.obj=percent;
							handler.sendMessage(m);
							downloadPercent=percent;
						}
	            	}
					if(CommonUtils.checkAPKState(DownloadService.this, file_new.getPath())) {
						Message m=new Message();
						m.what=2;
						handler.sendMessage(m);
					}
					else {
						Message m=new Message();
						m.what=3;
						handler.sendMessage(m);
					}
				
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Message m=new Message();
					m.what=3;
					handler.sendMessage(m);
				} finally {
					try {
						if(is!=null) {
							is.close();
						}
						if(raf!=null) {
							raf.close();
						}
						if(conn!=null) {
							conn.disconnect();
						}						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Message m=new Message();
						m.what=3;
						handler.sendMessage(m);
					}
				}
			
			}}).start();
	}
	
	private long init(String url_) {
		long fileSize=0;
		try {
			URL url = new URL(url_);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(5000);
			connection.setRequestMethod("GET");
			fileSize = connection.getContentLength();
			connection.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileSize;
	}
	
}

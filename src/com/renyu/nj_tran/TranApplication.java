package com.renyu.nj_tran;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.renyu.nj_tran.commons.CommonUtils;

import android.app.Application;

public class TranApplication extends Application {
	
	public boolean appOpen=false;
	public String currentLatLng="";
	public HashMap<String, String> jn_offline_maps=null;
	//��ǰ��������
	public ArrayList<String> tasks=null;
	//����notification
	public static final String UPDATE="100";
	//�Ƽ�notification
	public static final String adv1="200"; 

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		JPushInterface.setDebugMode(true); 
        JPushInterface.init(this); 
        JPushInterface.setAliasAndTags(getApplicationContext(), CommonUtils.getImei(getApplicationContext()), null, new TagAliasCallback() {

			@Override
			public void gotResult(int arg0, String arg1, Set<String> arg2) {
				// TODO Auto-generated method stub
				if(arg0==0) {
					System.out.println("�����������óɹ�");
				}
			}});
        
        CommonUtils.getSdCacheDir(getApplicationContext());
		
		//�״����п������ݿ��ļ�
		CommonUtils.copyDbFile(getApplicationContext());
		
		tasks=new ArrayList<String>();
	}
	
	public ArrayList<String> getAllTask() {
		return tasks;
	}
}

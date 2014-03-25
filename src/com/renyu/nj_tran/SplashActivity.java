package com.renyu.nj_tran;

import com.baidu.mobstat.StatService;
import com.renyu.nj_tran.homepage.HomePageActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

public class SplashActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		if(!((TranApplication) getApplication()).appOpen) {
			Intent intent=new Intent(SplashActivity.this, HomePageActivity.class);
			startActivity(intent);
		}
		finish();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		StatService.onResume(this);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		StatService.onPause(this);
	}

}

package com.renyu.nj_tran.homepage;

import com.baidu.mobstat.StatService;
import com.renyu.nj_tran.R;
import com.renyu.nj_tran.TranApplication;
import com.renyu.nj_tran.service.UpdateService;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.LinearLayout;

public class HomePageActivity extends FragmentActivity {
	
	LinearLayout splash_view=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_homepage);
		
		splash_view=(LinearLayout) findViewById(R.id.splash_view);
		AlphaAnimation animation=new AlphaAnimation(1, 0);
		animation.setDuration(3000);
		animation.setFillAfter(true);
		animation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				
			}
		});
		splash_view.startAnimation(animation);
		
		((TranApplication) getApplication()).appOpen=true;
		
		Intent intent=new Intent(HomePageActivity.this, UpdateService.class);
		Bundle bundle=new Bundle();
		bundle.putString("from", "index");
		intent.putExtras(bundle);
		startService(intent);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		((TranApplication) getApplication()).appOpen=false;
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

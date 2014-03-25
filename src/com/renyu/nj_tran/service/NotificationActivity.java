package com.renyu.nj_tran.service;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class NotificationActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		finish();
	}

}

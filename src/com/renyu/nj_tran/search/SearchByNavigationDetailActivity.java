package com.renyu.nj_tran.search;

import java.util.ArrayList;

import com.baidu.mobstat.StatService;
import com.renyu.nj_tran.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

public class SearchByNavigationDetailActivity extends Activity {

	LinearLayout line_left=null;
	TextView title_name=null;
	ImageView title_left=null;
	
	ListView searchbynavigationdetail_listview=null;
	SearchByNavigationDetailAdapter adapter=null;
	
	ArrayList<String> str_route=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_searchbynavigationdetail);
		
		str_route=new ArrayList<String>();
		
		init();
	}
	
	private void init() {
		line_left=(LinearLayout) findViewById(R.id.line_left);
		line_left.setVisibility(View.VISIBLE);
		title_name=(TextView) findViewById(R.id.title_name);
		title_name.setText("路线规划结果");
		title_left=(ImageView) findViewById(R.id.title_left);
		title_left.setImageResource(R.drawable.icon_back);
		title_left.setVisibility(View.VISIBLE);
		title_left.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});
		
		searchbynavigationdetail_listview=(ListView) findViewById(R.id.searchbynavigationdetail_listview);
		str_route=getIntent().getExtras().getStringArrayList("routedetail");
		adapter=new SearchByNavigationDetailAdapter(SearchByNavigationDetailActivity.this, str_route);
		searchbynavigationdetail_listview.setAdapter(adapter);
		
		TextView navigation_map_title=(TextView) findViewById(R.id.navigation_map_title);
		navigation_map_title.setText(getIntent().getExtras().getString("title"));
		TextView navigation_map_time=(TextView) findViewById(R.id.navigation_map_time);
		navigation_map_time.setText(getIntent().getExtras().getString("duration"));
		TextView navigation_map_distance=(TextView) findViewById(R.id.navigation_map_distance);
		navigation_map_distance.setText(getIntent().getExtras().getString("distance"));
		TextView navigation_map_walkdistance=(TextView) findViewById(R.id.navigation_map_walkdistance);
		navigation_map_walkdistance.setText(getIntent().getExtras().getString("walkdistance"));
		TextView navigation_map_more=(TextView) findViewById(R.id.navigation_map_more);
		navigation_map_more.setVisibility(View.GONE);
		ScrollView navigation_map_bus_line_scroll=(ScrollView) findViewById(R.id.navigation_map_bus_line_scroll);
		navigation_map_bus_line_scroll.setVisibility(View.GONE);
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

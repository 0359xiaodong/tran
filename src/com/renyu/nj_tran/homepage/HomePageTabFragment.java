package com.renyu.nj_tran.homepage;

import java.util.LinkedList;

import com.renyu.nj_tran.R;
import com.renyu.nj_tran.currentstation.CurrentStationFragment;
import com.renyu.nj_tran.search.SearchFragment;
import com.renyu.nj_tran.setting.SettingFragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

public class HomePageTabFragment extends Fragment {
	
	View view=null;
	
	FragmentTabHost homepage_tabhost=null;

	private int iconArray[]={R.drawable.tab_icon_1, R.drawable.tab_icon_3, R.drawable.tab_icon_2};
	private String titleArray[]={"周围站点", "信息检索", "个人设置"};
	
	//背景布局组
	LinkedList<View> nav_layouts=null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if(view==null) {
			view=LayoutInflater.from(getActivity()).inflate(R.layout.fragment_homepage, null);
			homepage_tabhost=(FragmentTabHost) view.findViewById(R.id.homepage_tabhost);
			nav_layouts=new LinkedList<View>();
			setupTabView();	
		}
		ViewGroup parent=(ViewGroup) view.getParent();  
		if(parent!=null) {  
			parent.removeView(view);  
		}
		return view;
	}
	
	private void setupTabView() {
		homepage_tabhost.setup(getActivity(), getChildFragmentManager(), R.id.homepage_realtabcontent);
		homepage_tabhost.getTabWidget().setDividerDrawable(null);
		homepage_tabhost.setOnTabChangedListener(new OnTabChangeListener() {
			
			@Override
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub
				for(int i=0;i<nav_layouts.size();i++) {
					nav_layouts.get(i).setBackgroundColor(Color.parseColor("#ee675b"));
				}
				if(tabId.equals("周围站点")) {
					nav_layouts.get(0).setBackgroundColor(Color.parseColor("#e84c3d"));
				}
				else if(tabId.equals("信息检索")) {
					nav_layouts.get(1).setBackgroundColor(Color.parseColor("#e84c3d"));
				}
				else if(tabId.equals("个人设置")) {
					nav_layouts.get(2).setBackgroundColor(Color.parseColor("#e84c3d"));
				}
			}
		});		
		for(int i=0;i<3;i++) {
			TabHost.TabSpec tabSpec=homepage_tabhost.newTabSpec(titleArray[i]).setIndicator(getTabItemView(iconArray[i], titleArray[i]));
			switch(i) {
			case 0:
				homepage_tabhost.addTab(tabSpec, CurrentStationFragment.class, null);
				break;
			case 1:
				homepage_tabhost.addTab(tabSpec, SearchFragment.class, null);
				break;
			case 2:
				homepage_tabhost.addTab(tabSpec, SettingFragment.class, null);
				break;
			}
		}
		homepage_tabhost.setCurrentTab(0);
	}
	
	private View getTabItemView(int image_res, String title) {
		View view=LayoutInflater.from(getActivity()).inflate(R.layout.fragment_homepage_tab_bottom_nav, null);
		if(title.equals("周围站点")) {
			view.setBackgroundColor(Color.parseColor("#e43525"));
		}
		else {
			view.setBackgroundColor(Color.parseColor("#ed5c4f"));
		}
		nav_layouts.add(view);
		ImageView nav_icon=(ImageView) view.findViewById(R.id.nav_icon);
		nav_icon.setImageResource(image_res);
		TextView nav_title=(TextView) view.findViewById(R.id.nav_title);
		nav_title.setText(title);
		return view;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==Activity.RESULT_OK&&(requestCode==201||requestCode==202)) {
			Fragment f=getChildFragmentManager().findFragmentByTag("信息检索");
			f.onActivityResult(requestCode, resultCode, data);
		}		
	}
	
}

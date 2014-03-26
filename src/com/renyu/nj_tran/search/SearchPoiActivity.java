package com.renyu.nj_tran.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.baidu.mobstat.StatService;
import com.renyu.nj_tran.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class SearchPoiActivity extends Activity implements OnPoiSearchListener {
	
	PoiSearch.Query searchQuery=null;
	
	LinearLayout line_left=null;
	TextView title_name=null;
	ImageView title_left=null;
	LinearLayout line_right=null;
	TextView title_right=null;
	ProgressBar title_pb=null;
	
	EditText searchpoi_edit=null;
	ListView searchpoi_listview=null;
	SimpleAdapter adapter=null;
	ArrayList<HashMap<String, Object>> lists=null;
	
	//当前搜索关键字
	String currentSearchKey="";
	//当前查询关键字
	String currentFindKey="";
	boolean isSearching=false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_searchpoi);
		
		lists=new ArrayList<HashMap<String, Object>>();
		
		init();
	}
	
	private void init() {
		line_left=(LinearLayout) findViewById(R.id.line_left);
		line_left.setVisibility(View.VISIBLE);
		title_name=(TextView) findViewById(R.id.title_name);
		title_name.setText(getIntent().getExtras().getString("title"));
		title_left=(ImageView) findViewById(R.id.title_left);
		title_left.setImageResource(R.drawable.icon_back);
		title_left.setVisibility(View.VISIBLE);
		title_left.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});
		title_right=(TextView) findViewById(R.id.title_right);
		title_right.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!isSearching) {
					searchResult(searchpoi_edit.getText().toString());
				}				
			}});
		title_right.setText("提交");
		title_right.setVisibility(View.VISIBLE);
		line_right=(LinearLayout) findViewById(R.id.line_right);
		line_right.setVisibility(View.VISIBLE);
		title_pb=(ProgressBar) findViewById(R.id.title_pb);
		
		searchpoi_listview=(ListView) findViewById(R.id.searchpoi_listview);
		adapter=new SimpleAdapter(SearchPoiActivity.this, lists, R.layout.adapter_searchpoi, new String[]{"name"}, new int[]{R.id.search_poi_text});
		searchpoi_listview.setAdapter(adapter);
		searchpoi_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent=getIntent();
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				Bundle bundle=new Bundle();
				bundle.putString("name", lists.get(position).get("name").toString());
				LatLonPoint point=(LatLonPoint) lists.get(position).get("latlng");
				bundle.putString("pos", point.getLatitude()+"&"+point.getLongitude());
				intent.putExtras(bundle);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
		searchpoi_edit=(EditText) findViewById(R.id.searchpoi_edit);
		searchpoi_edit.addTextChangedListener(tw);
	}
	
	TextWatcher tw=new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			if(currentSearchKey.equals(s.toString())) {
				return;
			}
			currentSearchKey=s.toString();
			searchResult(currentSearchKey);
		}
	};

	private void searchResult(String str) {
		title_pb.setVisibility(View.VISIBLE);
		isSearching=true;
		currentFindKey=str;
		searchQuery=new PoiSearch.Query(str, "", "025");
		searchQuery.setPageNum(0);
		searchQuery.setPageSize(20);
		PoiSearch poiSearch=new PoiSearch(SearchPoiActivity.this, searchQuery);
		poiSearch.setOnPoiSearchListener(SearchPoiActivity.this);
		poiSearch.searchPOIAsyn();
	}
	
	@Override
	public void onPoiItemDetailSearched(PoiItemDetail arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPoiSearched(PoiResult result, int rCode) {
		// TODO Auto-generated method stub
		lists.clear();
		if(rCode==0) {
			if(result!=null&&result.getQuery()!=null&&result.getPois()!=null&&result.getPois().size()>0) {
				List<PoiItem> poiItems=result.getPois();
				for(int i=0;i<poiItems.size();i++) {
					HashMap<String, Object> map=new HashMap<String, Object>();
					map.put("name", poiItems.get(i).getTitle());
					map.put("latlng", poiItems.get(i).getLatLonPoint());
					lists.add(map);
				}
			}
		}
		isSearching=false;
		title_pb.setVisibility(View.INVISIBLE);
		adapter.notifyDataSetChanged();
		//允许用户多次输入，搜索最后一次输入的信息
		if(!currentFindKey.equals(currentSearchKey)) {
			searchResult(currentSearchKey);
		}
		InputMethodManager imm=(InputMethodManager)getSystemService(SearchPoiActivity.this.INPUT_METHOD_SERVICE); 
		imm.hideSoftInputFromWindow(searchpoi_edit.getWindowToken(), 0); 
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

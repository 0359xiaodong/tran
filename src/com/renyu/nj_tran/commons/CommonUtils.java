package com.renyu.nj_tran.commons;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.MeasureSpec;

public class CommonUtils {

	public static String changeOldValue(String paramString1, String paramString2) {
	    return "/key="+string2MD5(new StringBuilder(String.valueOf(string2MD5(paramString1))).append("#").append(paramString2).toString());
	}
	
	/*** 
     * MD5加码 生成32位md5码 
     */  
    public static String string2MD5(String inStr) {  
        MessageDigest md5 = null;  
        try{  
            md5 = MessageDigest.getInstance("MD5");  
        }catch (Exception e){  
            System.out.println(e.toString());  
            e.printStackTrace();  
            return "";  
        }  
        char[] charArray = inStr.toCharArray();  
        byte[] byteArray = new byte[charArray.length];    
        for (int i = 0; i < charArray.length; i++)  
            byteArray[i] = (byte) charArray[i];  
        byte[] md5Bytes = md5.digest(byteArray);  
        StringBuffer hexValue = new StringBuffer();  
        for (int i = 0; i < md5Bytes.length; i++){  
            int val = ((int) md5Bytes[i]) & 0xff;  
            if (val < 16)  
                hexValue.append("0");  
            hexValue.append(Integer.toHexString(val));  
        }  
        return hexValue.toString();    
    } 
    
    public static String getWebData(HashMap<String, String> map, String url) {
		ArrayList<NameValuePair> params=new ArrayList<NameValuePair>();
		Iterator<Entry<String, String>> it=map.entrySet().iterator();
		while(it.hasNext()) {
			Entry<String, String> entry=it.next();
			params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
		HttpPost post=new HttpPost(url);
		try {
			post.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));//设置post参数 并设置编码格式
			post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
			DefaultHttpClient client=new DefaultHttpClient();
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
			HttpResponse resp=client.execute(post);
			if(resp.getStatusLine().getStatusCode()==200) {
				return EntityUtils.toString(resp.getEntity());
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;
	}
    
    public static String convertToTime(long time) {
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date=new Date(time);
		return df.format(date);
	}
    
    /**
	 * 将内置数据复制进入
	 * @param sqliteName
	 */
	public static void copyDbFile(Context context) {
		File file=new File("/data/data/"+context.getPackageName()+"/iso2014.db");
		if(!file.exists()) {
			CommonUtils.copyAssetsFile("iso2014.db", file.getPath(), context);
		}
	}
	
	/**
     * 通过assets复制文件
     * @param oldName
     * @param newPath
     * @param context
     */
    public static void copyAssetsFile(String oldName, String newPath, Context context) {
    	AssetManager manager=context.getAssets();
    	try {
    		int bytesum=0; 
    		int byteread=0; 
			InputStream inStream=manager.open(oldName);
			FileOutputStream fs=new FileOutputStream(newPath); 
			byte[] buffer=new byte[1444]; 
			while ((byteread = inStream.read(buffer))!=-1) { 
				bytesum+=byteread; 
				fs.write(buffer, 0, byteread); 
			} 
			inStream.close(); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /**
     * 获取时间差值
     * @param time
     * @return
     */
    public static String extraTime(long time) {
    	long currentTime=System.currentTimeMillis()/1000;
    	if(currentTime-time<60) {
    		return (currentTime-time)+"秒前";
    	}
    	else if(currentTime-time>=60&&currentTime-time<3600) {
    		return (currentTime-time)/60+"分"+((currentTime-time)-(currentTime-time)/60*60)+"秒前";
    	}
    	return "1小时前";
    }
    
    /**
     * double 保留1位小数
     */
    public static String m1(double f) {
    	DecimalFormat df=new DecimalFormat(".##");
    	if(f<1) {
    		return "0";
    	}
    	return df.format(f);
    }
    
    /**
     * view转bitmap
     * @param view
     * @return
     */
    public static Bitmap convertViewToBitmap(View view){
    	view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
    	view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
    	view.buildDrawingCache();
    	return view.getDrawingCache();
    }
    
	public static String convertNull(String returnValue) {
        try {
            returnValue = (returnValue==null||(returnValue!=null&&returnValue.equals("null")))?"":returnValue;
        } catch (Exception e) {
            returnValue = "";
        }
        return returnValue;
    }
	
	/**
	 * 获取imei
	 * @param context
	 * @return
	 */
	public static String getImei(Context context) {
		String imei="";
		try {
			TelephonyManager telephonyManager=(TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			imei=telephonyManager.getDeviceId();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return imei;
	}
	
	/**
	 * 设置离线地图的版本
	 * @param context
	 * @param version
	 */
	public static void setOfflineVersion(Context context, int version) {
		SharedPreferences sp=context.getSharedPreferences("tran", Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor=sp.edit();
		editor.putInt("version", version);
		editor.commit();
	}
	
	/**
	 * 获取离线地图版本
	 * @param context
	 * @return
	 */
	public static int getOfflineVersion(Context context) {
		SharedPreferences sp=context.getSharedPreferences("tran", Activity.MODE_PRIVATE);
		return sp.getInt("version", 0);
	}

	/**
	 * 获取map缓存和读取目录
	 * 
	 * @param context
	 * @return
	 */
	public static String getSdCacheDir(Context context) {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File fExternalStorageDirectory=Environment.getExternalStorageDirectory();
			File autonaviDir=new File(fExternalStorageDirectory, "nj_tran");
			if (!autonaviDir.exists()) {
				autonaviDir.mkdir();
			}
			return autonaviDir.toString() + "/";
		} else {
			return null;
		}
	}
	
	/**
	 * 获取版本号
	 * @param context
	 * @return
	 */
	public static int getVersionCode(Context context) {  
	    try {  
	        PackageInfo pi=context.getPackageManager().getPackageInfo(context.getPackageName(), 0);  
	        return pi.versionCode;  
	    } catch (Exception e) {  
	        // TODO Auto-generated catch block  
	        e.printStackTrace();  
	        return 0;  
	    }  
	}
	
	/**
	 * 检测apk包状态
	 * @param context
	 * @param path
	 * @return
	 */
	public static boolean checkAPKState(Context context, String path) {
		PackageInfo pi=null;
		try {
			PackageManager pm=context.getPackageManager();
			pi=pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
			if(pi==null) {
				File file=new File(path);
				if(file.exists()) {
					file.delete();
				}				
			}
			return pi==null?false:true;
		} catch(Exception e) {
			if(pi==null) {
				File file=new File(path);
				if(file.exists()) {
					file.delete();
				}				
			}
			return false;
		}
	}
	
	/**
	 * 获取屏幕密度信息
	 * @param context
	 * @return
	 */
	public static float getDisplayParams(Context context) {
		DisplayMetrics dm=new DisplayMetrics();
		dm=context.getApplicationContext().getResources().getDisplayMetrics();
		return dm.density;
	}
	
	/**
	 * 判断当前网络状态
	 * @param context
	 * @return
	 */
	public static int checkNetworkInfo(Context context) {
		ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		State mobile=conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		if(mobile==State.CONNECTED) {
			return 1;
		}
		State wifi=conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		if(wifi==State.CONNECTED) {
			return 2;
		}
		return 3;
	}
	
	/**
	 * 判断字符串是否均为数字
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str){
		for (int i=str.length();--i>=0;){   
			if (!Character.isDigit(str.charAt(i))){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 判断字符串是否含有数字
	 * @param str
	 * @return
	 */
	public static boolean isContainNumeric(String str) {
		for (int i=str.length();--i>=0;){   
			if (Character.isDigit(str.charAt(i))){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 返回公交车行驶所需时间
	 * @param time
	 * @return
	 */
	public static String getBusDuration(long time) {
		if(time<60) {
			return time+"秒";
		}
		return time/60+"分钟";
	}
	
	/**
	 * 返回步行/公交车行驶距离
	 * @param dis
	 * @return
	 */
	public static String getBusDistance(float dis) {
		if(dis<1000) {
			return dis+"m";
		}
		else {
			return m1(((double)dis)/1000)+"km";
		}
	}
}

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
     * MD5���� ����32λmd5�� 
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
			post.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));//����post���� �����ñ����ʽ
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
	 * ���������ݸ��ƽ���
	 * @param sqliteName
	 */
	public static void copyDbFile(Context context) {
		//�Ͼ����ݿ�
		File file_iso2014=new File("/data/data/"+context.getPackageName()+"/iso2014.db");
		if(!file_iso2014.exists()) {
			CommonUtils.copyAssetsFile("iso2014.db", file_iso2014.getPath(), context);
		}
		//�������ݿ�
		File file_BusDB=new File("/data/data/"+context.getPackageName()+"/jn.db");
		if(!file_BusDB.exists()) {
			CommonUtils.copyAssetsFile("jn.db", file_BusDB.getPath(), context);
		}
	}
	
	/**
     * ͨ��assets�����ļ�
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
     * ��ȡʱ���ֵ
     * @param time
     * @return
     */
    public static String extraTime(long time) {
    	long currentTime=System.currentTimeMillis()/1000;
    	if(currentTime-time<60) {
    		return (currentTime-time)+"��ǰ";
    	}
    	else if(currentTime-time>=60&&currentTime-time<3600) {
    		return (currentTime-time)/60+"��"+((currentTime-time)-(currentTime-time)/60*60)+"��ǰ";
    	}
    	return "1Сʱǰ";
    }
    
    /**
     * double ����1λС��
     */
    public static String m1(double f) {
    	DecimalFormat df=new DecimalFormat(".##");
    	if(f<1) {
    		return "0";
    	}
    	return df.format(f);
    }
    
    /**
     * viewתbitmap
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
	 * ��ȡimei
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
	 * �������ߵ�ͼ�İ汾
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
	 * ��ȡ���ߵ�ͼ�汾
	 * @param context
	 * @return
	 */
	public static int getOfflineVersion(Context context) {
		SharedPreferences sp=context.getSharedPreferences("tran", Activity.MODE_PRIVATE);
		return sp.getInt("version", 0);
	}

	/**
	 * ��ȡmap����Ͷ�ȡĿ¼
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
	 * ��ȡ�汾��
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
	 * ���apk��״̬
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
	 * ��ȡ��Ļ�ܶ���Ϣ
	 * @param context
	 * @return
	 */
	public static float getDisplayParams(Context context) {
		DisplayMetrics dm=new DisplayMetrics();
		dm=context.getApplicationContext().getResources().getDisplayMetrics();
		return dm.density;
	}
	
	/**
	 * �жϵ�ǰ����״̬
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
	 * �ж��ַ����Ƿ��Ϊ����
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
	 * �ж��ַ����Ƿ�������
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
	 * ���ع�������ʻ����ʱ��
	 * @param time
	 * @return
	 */
	public static String getBusDuration(long time) {
		if(time<60) {
			return time+"��";
		}
		return time/60+"����";
	}
	
	/**
	 * ���ز���/��������ʻ����
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
	
	public static HashMap<String, String> lineNameMap=null;
	/**
	 * ���ؽ������ݿ���վ������
	 * @param lineName
	 * @return
	 */
	public static String isJnFromNJDB(String lineName) {
		if(lineNameMap==null) {
			HashMap<String, String> map=new HashMap<String, String>();
			map.put("101·", "101"); map.put("101����", "101"); map.put("101·��վ�쳵", "101");
			map.put("102·", "102"); map.put("103·", "103"); map.put("104·", "104");
			map.put("105·", "105"); map.put("106·", "106"); map.put("119·", "119");
			map.put("137·", "137"); map.put("137����", "137"); map.put("148·", "148");
			map.put("164·", "164"); map.put("180·", "180"); map.put("190·", "190");
			map.put("191·", "191"); map.put("192·", "192"); map.put("821·", "821");
			map.put("827", "827"); map.put("D6", "D6"); map.put("D8", "D8");
			map.put("������", "����"); map.put("������", "����"); map.put("������", "����");
			map.put("������", "����"); map.put("������", "����"); map.put("������", "����");
			map.put("��ͭ��", "��ͭ"); map.put("������", "����"); map.put("������", "����");
			map.put("������", "����"); map.put("����", "����"); map.put("������", "����");
			map.put("��ˮ��", "��ˮ"); map.put("��ũ��", "��ũ"); map.put("�Ƚ���", "�Ƚ�");
			map.put("�Ȱ���", "�Ȱ�"); map.put("�Ȱ���", "�Ȱ�"); map.put("������1·", "������1");
			map.put("�����", "���"); map.put("������", "����"); map.put("����", "��");
			map.put("�����", "���"); map.put("��½��", "��½"); map.put("������", "����");
			map.put("������", "����"); map.put("������", "����"); map.put("������", "����");
			map.put("������", "����"); map.put("»����", "»��"); map.put("½����", "½��");
			map.put("½����", "½��"); map.put("½����", "½��"); map.put("������", "����");
			map.put("�尲��", "�尲"); map.put("����1·", "��1"); map.put("����10·", "��10");
			map.put("����12·", "��12"); map.put("����15·", "��15"); map.put("����16·", "��16");
			map.put("����17·", "��17"); map.put("����19·", "��19"); map.put("����2·", "��2");
			map.put("����20·", "��20"); map.put("����21·", "��21"); map.put("����22·", "��22");
			map.put("����23·", "��23"); map.put("����27·", "��27"); map.put("����29·", "��29");
			map.put("����3·", "��3"); map.put("����30·", "��30"); map.put("����31·", "��31");
			map.put("����4·", "��4"); map.put("����6·", "��6"); map.put("����8·", "��8");
			map.put("ʤ����", "ʤ��"); map.put("������", "����"); map.put("������", "����");
			map.put("�����", "���"); map.put("�ܶ���", "�ܶ�"); map.put("�ܺ���", "�ܺ�");
			map.put("�ܺ���", "�ܺ�"); map.put("��ʯ��", "��ʯ"); 
			lineNameMap=map;
		}
		if(lineNameMap.containsKey(lineName)) {
			return lineNameMap.get(lineName);
		}
		return null;
	}
}

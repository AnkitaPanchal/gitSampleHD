package com.filemanager.sdexplorer;

import android.content.Context;
import android.preference.Preference;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.security.MessageDigest;

public class CustomAdmobPreference extends Preference 
{
	public CustomAdmobPreference(Context context, AttributeSet attrs, int defStyle) {super    (context, attrs, defStyle);}
	public CustomAdmobPreference(Context context, AttributeSet attrs) {super(context, attrs);}
	public CustomAdmobPreference(Context context) {super(context);}
	private String deviceId_md5;
	public static  boolean isInit=false;
	@Override
	protected View onCreateView(ViewGroup parent) 
	{
		

		// this will create the linear layout defined in ads_layout.xml
		View view = super.onCreateView(parent);
		
		 AdView mAdView = (AdView)view.findViewById(R.id.adView);
		AdRequest adRequest;
		if (BuildConfig.DEBUG)
		{
			try
			{
				String android_id = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
				deviceId_md5 = md5(android_id).toUpperCase();
				Log.e("Device ID: ",""+deviceId_md5);
			}
			catch (Exception e)
			{

			}

			adRequest = new AdRequest.Builder()
					.addTestDevice(deviceId_md5)
					.build();
			boolean isTestDevice = adRequest.isTestDevice(getContext());
			Log.v("Admob:", "is Admob Test Device ? " + deviceId_md5 + " " + isTestDevice); //to confirm it worked
		}
		else
		{
			adRequest = new AdRequest.Builder().build();
		}

		mAdView.loadAd(adRequest);



		return view;    
	}
	public static final String md5(final String s)
	{
		try
		{
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest
					.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++) {
				String h = Integer.toHexString(0xFF & messageDigest[i]);
				while (h.length() < 2)
					h = "0" + h;
				hexString.append(h);
			}
			return hexString.toString();

		} catch (Exception e)
		{
			Log.e("md5 Exception: ",e.toString());
		}
		return "";
	}
}

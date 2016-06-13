package com.filemanager.sdexplorer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.filemanager.sdexplorer.data.FM__ConstantData;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.security.MessageDigest;

public class FM_Full_Image extends Activity implements View.OnClickListener{

	private Bitmap myBitmap=null;
	private String strfile_path;
	private ImageButton imgbtninfo;
private String deviceId_md5;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);


		setContentView(R.layout.fm_full_image);


		try
		{

			AdView mAdView = (AdView) this.findViewById(R.id.adView);
			AdRequest adRequest;
			if (BuildConfig.DEBUG)
			{
				try
				{
					String android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
					deviceId_md5 = md5(android_id).toUpperCase();
					Log.e("Device ID: ",""+deviceId_md5);
				}
				catch (Exception e)
				{

				}

				adRequest = new AdRequest.Builder()

						.addTestDevice(deviceId_md5)
						.build();
				boolean isTestDevice = adRequest.isTestDevice(this);
				Log.v("Admob:", "is Admob Test Device ? " + deviceId_md5 + " " + isTestDevice); //to confirm it worked
			}
			else
			{
				adRequest = new AdRequest.Builder().build();
			}
			mAdView.loadAd(adRequest);

		}
		catch (Exception e)
		{
			Log.e("adview Error",""+e.toString());
		}
		try {

			//initialise Analytics
		 FM__ConstantData.initialiseAnalytics(FM__ConstantData.Google_Analytic_ID,this);
			//add Google Analytics view
			String str_name=this.getClass().getSimpleName();
			Log.e("str_name", str_name);
			FM__ConstantData.AnalyticsView("Full Image Activity");

		} catch (Exception e) {
			// TODO: handle exception

			Log.e("AnalyticsView", e.toString());

		}
		imgbtninfo=(ImageButton)findViewById(R.id.imgbtninfo);
		imgbtninfo.setOnClickListener(this);

		Intent i = getIntent();
		if(i == null) return;
		Uri u = i.getData();

		Log.e("URI",""+u.toString());

		if(u == null) return;
		strfile_path = u.getPath();
		if(strfile_path == null) return;


		String FILENAME=strfile_path.substring(strfile_path.lastIndexOf('/')+1);
		TextView txtheadingname=(TextView)findViewById(R.id.txtheadingname);
		txtheadingname.setText(FILENAME);



		TouchImageView myImage = (TouchImageView) findViewById(R.id.image);

		try
		{
			myImage.setImageURI(u);
			myImage.setMinZoom(0.2f);
		}

		catch (OutOfMemoryError e)
		{
			Log.e("OUT OF MEMORY",""+e.toString());
		}

	}
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{


			case R.id.imgbtninfo:

				Intent authIntent = new Intent(FM_Full_Image.this,AboutUS.class);
				startActivity(authIntent);
				break;
		}


	}

	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		if(myBitmap!=null)
		{
			myBitmap.recycle();
			myBitmap=null;
		}

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

package com.filemanager.sdexplorer;

import com.filemanager.sdexplorer.data.FM__ConstantData;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class AboutUS extends PreferenceActivity
{
	private String APP_LINK;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preference);

		Resources res = getResources();
		APP_LINK="https://play.google.com/store/apps/details?id=" + getPackageName();

		try {

			//initialise Analytics
		 FM__ConstantData.initialiseAnalytics(FM__ConstantData.Google_Analytic_ID,this);
			//add Google Analytics view
			String str_name=this.getClass().getSimpleName();
			Log.e("str_name", str_name);
			FM__ConstantData.AnalyticsView("About Us");

		} catch (Exception e) {
			// TODO: handle exception

			Log.e("AnalyticsView", e.toString());

		}
		Preference mailTo = (Preference) findPreference("contactus");

		Preference shareapps = (Preference) findPreference("shareapps");
		Preference RateThisapp = (Preference) findPreference("RateThisapp");
		Preference MoreApps = (Preference) findPreference("MoreApps");
		MoreApps.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(Intent.ACTION_VIEW);
				//				intent.setData(Uri.parse("market://search?q=pub:iWallpapers"));
				intent.setData(Uri.parse("http://play.google.com/store/search?q=pub:AppAspect+Technologies+Pvt.+Ltd."));
				startActivity(intent);
				return false;
			}
		});
		mailTo.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				// TODO Auto-generated method stub

				
				try {

					Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto","info@appaspecttechnologies.com", null));
					emailIntent.putExtra(Intent.EXTRA_SUBJECT,"Contact from - "+getString(R.string.app_name));
					startActivity(Intent.createChooser(emailIntent, "Send email..."));

				} catch (Exception e) {
					// TODO: handle exception
				}
				
//				Intent sendIntent = new Intent(Intent.ACTION_SEND);
//
//				//Mime type of the attachment (or) u can use sendIntent.setType("*/*")
//				sendIntent.setType("text/html");
//
//				String aEmailList[] = {"info@appaspect.com"};
//				sendIntent.putExtra(Intent.EXTRA_EMAIL,aEmailList);
//
//				//Subject for the message or Email
//				sendIntent.putExtra(Intent.EXTRA_SUBJECT,"Contact from - "+getString(R.string.app_name));
//
//				//Body for the message or Email
//				String body="";
//				sendIntent.putExtra(Intent.EXTRA_TEXT ,body);
//
//				startActivity(Intent.createChooser(sendIntent, "Send email..."));
				return false;
			}
		});

		shareapps.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				// TODO Auto-generated method stub

				LayoutInflater li = LayoutInflater.from(AboutUS.this);
				View promptsView = li.inflate(R.layout.edit, null);

				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AboutUS.this);
				// set prompts.xml to alertdialog builder

				alertDialogBuilder.setTitle(getString(R.string.app_name));
				alertDialogBuilder.setView(promptsView);

				final EditText edtText = (EditText) promptsView.findViewById(R.id.edtName);
				String body=getString(R.string.Share_App_Body_top)+" "+getString(R.string.app_name)+" "+
						getString(R.string.Share_App_Body_middle)+ " "+APP_LINK+" "+
						getString(R.string.Share_App_Body_bottom);
				edtText.setText(body);

				// set dialog message
				alertDialogBuilder
				.setCancelable(false)
				.setPositiveButton(getString(R.string.Send),
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) 
					{								
						// get user input and set it to result
						// edit text
						String finalString=(edtText.getText().toString());

						Intent email = new Intent(Intent.ACTION_SEND);
						email.setType("text/plain");
						email.putExtra(Intent.EXTRA_EMAIL,  "" );
						email.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						email.putExtra(Intent.EXTRA_SUBJECT, " "+getString(R.string.app_name)+" "+getString(R.string.Share_Andoird)+""+getString(R.string.Share_Application));

						//						String body=getString(R.string.Share_App_Body_top)+" "+getString(R.string.app_name)+" "+
						//						getString(R.string.Share_App_Body_middle)+ " "+APP_LINK+" "+
						//						getString(R.string.Share_App_Body_bottom);

						email.putExtra(Intent.EXTRA_TEXT,finalString);

						try
						{
							startActivity(Intent.createChooser(email, "Send Message..."));
						}
						catch (android.content.ActivityNotFoundException ex) 
						{

						}
					}
				})
				.setNegativeButton(getString(R.string.Cancel),
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
					}
				});

				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();
				// show it
				alertDialog.show();
				return false;
			}
		});




		RateThisapp.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				// TODO Auto-generated method stub

				try {
					//Uri marketUri = Uri.parse( String.format( "market://details?id=%s", "com.appaspect.compass.hd" ) );
					Uri marketUri = Uri.parse( String.format( "market://details?id=%s", AboutUS.this.getPackageName() ) );
					Intent marketIntent = new Intent( Intent.ACTION_VIEW ).setData( marketUri );
					startActivity( marketIntent );


				} catch (Exception e) {
					// TODO: handle exception

				}
				return false;
			}
		});
		//Get Event on Preference
		//		Preference myPref = (Preference) findPreference("proversion");
		//		myPref.setOnPreferenceClickListener(new OnPreferenceClickListener() 
		//		{
		//			public boolean onPreferenceClick(Preference preference) 
		//			{
		//				Toast.makeText(SettingsActivity.this,"Pro Version Clicked",Toast.LENGTH_SHORT).show();
		//				return true;
		//			}
		//		});

		//		Preference myPref = (Preference) findPreference("aboutus");
		//		myPref.setOnPreferenceClickListener(new OnPreferenceClickListener() 
		//		{
		//			public boolean onPreferenceClick(Preference preference) 
		//			{
		//								Intent intent=new Intent(SettingsActivity.this,AboutUS.class);
		//								startActivity(intent);
		//								Toast.makeText(SettingsActivity.this,"aboutus Clicked",Toast.LENGTH_SHORT).show();
		//				return true;
		//			}
		//		});

		//		Preference deleteAll = (Preference) findPreference("deleteAll");
		//		deleteAll.setOnPreferenceClickListener(new OnPreferenceClickListener() 
		//		{
		//			public boolean onPreferenceClick(Preference preference) 
		//			{
		//				deleteAllRecords();
		//				return true;
		//			}
		//		});

	}

	//	private void deleteAllRecords()
	//	{
	//		AlertDialog alertDialog =new AlertDialog.Builder(this) 
	//		//set message, title, and icon
	//		.setTitle(getResources().getString(R.string.delete_all_log)) 
	//		.setMessage(getResources().getString(R.string.Delete_Confirm)) 
	//
	//		.setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() 
	//		{
	//			public void onClick(DialogInterface dialog, int whichButton) 
	//			{
	//				//your deleting code
	//				deleteFiles();
	//				ConstantData.deleteFlag=true;
	//				dialog.dismiss();
	//			}   
	//		})
	//
	//		.setNegativeButton(getString(R.string.No), new DialogInterface.OnClickListener() 
	//		{
	//			public void onClick(DialogInterface dialog, int which) 
	//			{
	//				dialog.dismiss();
	//			}
	//		})
	//		.create();
	//		alertDialog.show();
	//	}

	//	protected void deleteFiles() 
	//	{
	//		//		To Delete Data from the System Call Log
	//		//		getContentResolver().delete(android.provider.CallLog.Calls.CONTENT_URI, null, null) ;
	//
	//		SharedPreferences preferences = getSharedPreferences(MainActivity.SharedPrefName, Context.MODE_PRIVATE);
	//		ArrayList<CallLogDetail> callLogDetails=null;
	//		callLogDetails=new ArrayList<CallLogDetail>();
	//
	//		Gson gson=new Gson();
	//		String json=gson.toJson(callLogDetails);
	//		Editor edit = preferences.edit();
	//		edit.putString(MainActivity.LOG_DETAILS_ARRAY,json);
	//		edit.commit();
	//	} 
}
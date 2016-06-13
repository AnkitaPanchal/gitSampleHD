package com.filemanager.sdexplorer.data;

import java.io.File;
import java.util.HashMap;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

public class FM__ConstantData {

	
	// new id use latter 23-july-14
			public static String Google_Analytic_ID="UA-57275460-12";

			
			//Google analytics
				public static enum TrackerName {
					APP_TRACKER, // Tracker used only in this app.
					GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
					ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
				}

				public static Tracker tracker;
				public static HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();
				
	public static String Admob_INTERSTITIAL_UNIT_ID="ca-app-pub-3762189980353472/2034226906";
	public static String aApp_link="http://goo.gl/QfovYQ";
	public static String PRO_link="https://play.google.com/store/apps/details?id=com.appaspect.tech.filemanager.pro";
	public static int infocounter=0;
	public static int  screenwidth;
	public static int screenheight;
	public static int backindexvalue=0;
	public static File homedirpath;
	public static File rootdirpath;
	public static String str_homedirpath;
	public static String str_homedirpath_up;
	public static String str_rootdirpath;
	public static boolean backflag=false;
	
	public static String srt_file_path_for_copy;
	public static String srt_file_name_for_copy;
	public static String srt_file_type_for_copy;
	public static final String dbName="filemanager.db";
	public static final String Table_Bookmark="Bookmark";
	
	public static final String ID="id";
	public static final String bookmark_name="bookmark_name";
	public static final String bookmark_path="bookmark_path";
	public static final String bookmark_flag="bookmark_flag";
	public static final String bookmark_file_type="bookmark_file_type";
	
	public static SQLiteDatabase db=null;
	public static FM_Bookmark_MySqlOpenHelper sqlfm_bookmark=null;
	
	public static String[] fm_bookmarkcols= new String[]{FM__ConstantData.ID,FM__ConstantData.bookmark_name,FM__ConstantData.bookmark_path,FM__ConstantData.bookmark_flag,FM__ConstantData.bookmark_file_type};
	

	public synchronized static Tracker initialiseAnalytics(String PROPERTY_ID, Context m_this) {
		if (!FM__ConstantData.mTrackers.containsKey(FM__ConstantData.TrackerName.APP_TRACKER)) {

			GoogleAnalytics analytics = GoogleAnalytics.getInstance(m_this);
			analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
			analytics.setDryRun(false);
			FM__ConstantData.tracker = analytics.newTracker(PROPERTY_ID);
			FM__ConstantData.mTrackers.put(TrackerName.APP_TRACKER, FM__ConstantData.tracker);

		}

		return FM__ConstantData.mTrackers.get(TrackerName.APP_TRACKER);
	}

	public static void AnalyticsView(String classname) {

		//		tracker.enableAutoActivityTracking(true);
		FM__ConstantData.tracker.setScreenName(classname);
		FM__ConstantData.tracker.send(new HitBuilders.AppViewBuilder().build());


	}
	
	
}

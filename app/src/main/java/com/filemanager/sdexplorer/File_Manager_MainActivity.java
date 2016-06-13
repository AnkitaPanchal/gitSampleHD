package com.filemanager.sdexplorer;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.filemanager.sdexplorer.data.ExtractManager;
import com.filemanager.sdexplorer.data.FM_Bookmark_MySqlOpenHelper;
import com.filemanager.sdexplorer.data.FM__ConstantData;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;
import java.security.MessageDigest;
import java.util.ArrayList;
//import com.sbstrm.appirater.Appirater;


public class File_Manager_MainActivity extends Activity 

{

	private File currentDir;
	private FileArrayAdapter adapter;
	private ImageButton imgbtninfo;
	private ListView lstdata;

	public static final String ACTION_WIDGET = "com.nexes.manager.Main.ACTION_WIDGET";

	public static final String PREFS_NAME = "ManagerPrefsFile";	//user preference file name
	public static final String PREFS_HIDDEN = "hidden";
	public static final String PREFS_COLOR = "color";
	public static final String PREFS_THUMBNAIL = "thumbnail";
	public static final String PREFS_SORT = "sort";
	public static final String PREFS_STORAGE = "sdcard space";

	private static final int MENU_MKDIR =   0x00;			//option menu id
	private static final int MENU_SETTING = 0x01;			//option menu id
	private static final int MENU_SEARCH =  0x02;			//option menu id
	private static final int MENU_SPACE =   0x03;			//option menu id
	private static final int MENU_QUIT = 	0x04;			//option menu id
	private static final int SEARCH_B = 	0x09;

	private static final int D_MENU_DELETE = 0x05;			//context menu id
	private static final int D_MENU_RENAME = 0x06;			//context menu id
	private static final int D_MENU_COPY =   0x07;			//context menu id
	private static final int D_MENU_PASTE =  0x08;			//context menu id
	private static final int D_MENU_ZIP = 	 0x0e;			//context menu id
	private static final int D_MENU_UNZIP =  0x0f;			//context menu id
	private static final int D_MENU_MOVE = 	 0x30;			//context menu id
	private static final int F_MENU_MOVE = 	 0x20;			//context menu id
	private static final int F_MENU_DELETE = 0x0a;			//context menu id
	private static final int F_MENU_RENAME = 0x0b;			//context menu id
	private static final int F_MENU_ATTACH = 0x0c;			//context menu id
	private static final int F_MENU_COPY =   0x0d;			//context menu id
	private static final int SETTING_REQ = 	 0x10;			//request code for intent

	public static FileManager mFileMag;
	public static EventHandler mHandler;
	public static EventHandler.TableRow mTable;

	public static SharedPreferences mSettings;
	public static Editor editor;
	private boolean mReturnIntent = false;
	public static boolean mHoldingFile = false;
	public boolean mHoldingZip = false;
	private boolean mUseBackKey = true;
	private String mCopiedTarget;
	private String mZippedTarget;
	private String mSelectedListItem;				//item from context menu
	private TextView  mPathLabel;
	private LruCache<String, Bitmap> bitmapCache;
	private int cacheSize,mainselectpos=0;
	private int memClass;
	private int width;
	private int height;
	private File direct_sd;
	private int index;
	private ConnectivityManager connMgr_fmmain;
	private InterstitialAd interstitialAd;
	/** The log tag. */
	private static final String LOG_TAG = "InterstitialSample";
	
	public static ArrayList<String> arrylist_next_folder;
	public static boolean flag_str_homedirpath=false;
	public static TextView txtmessage;
	public static Activity mainacActivity;
	public static LinearLayout llpaste;
	private String deviceId_md5;
	private static Parcelable mListViewScrollPos = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file__manager__main);

		mainacActivity=this;
		arrylist_next_folder= new  ArrayList<String>();

		 llpaste=(LinearLayout)findViewById(R.id.llpaste);
			llpaste.setVisibility(LinearLayout.GONE);
			

//		    AdRequest adRequest = new AdRequest.Builder()
//		        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//		        .addTestDevice("829B1CAB0DBEBF29479B0DDFB320328E")
//					.addTestDevice("E6B3B159DE871B7F3618B3F61A8C8294")
//		        .build();
//		    adView.loadAd(adRequest);


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




		    
		    try {
				//Appirater.appLaunched(this);
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			 try {

					//initialise Analytics
				 FM__ConstantData.initialiseAnalytics(FM__ConstantData.Google_Analytic_ID,this);
					//add Google Analytics view
					String str_name=this.getClass().getSimpleName();
					Log.e("str_name", str_name);
					FM__ConstantData.AnalyticsView("Main Activity");

				} catch (Exception e) {
					// TODO: handle exception

					Log.e("AnalyticsView", e.toString());

				}
			 
		FM__ConstantData.homedirpath=android.os.Environment.getExternalStorageDirectory();
		FM__ConstantData.rootdirpath=android.os.Environment.getRootDirectory();

		FM__ConstantData.str_homedirpath_up=FM__ConstantData.homedirpath.toString();
		FM__ConstantData.str_homedirpath=FM__ConstantData.homedirpath.toString();
		FM__ConstantData.str_rootdirpath=FM__ConstantData.rootdirpath.toString();


		///DataBase Creation SRAudio
		FM__ConstantData.db=openOrCreateDatabase(FM__ConstantData.dbName,SQLiteDatabase.CREATE_IF_NECESSARY,null);
		FM__ConstantData.sqlfm_bookmark = new FM_Bookmark_MySqlOpenHelper(File_Manager_MainActivity.this, FM__ConstantData.dbName, null, 1);
		FM__ConstantData.sqlfm_bookmark.onOpen(FM__ConstantData.db);

		String facebooksql="CREATE TABLE if not exists "+ FM__ConstantData.Table_Bookmark+"("+FM__ConstantData.ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+FM__ConstantData.bookmark_name+" TEXT NOT NULL,"+FM__ConstantData.bookmark_path+" TEXT NOT NULL,"+FM__ConstantData.bookmark_flag+" TEXT,"+FM__ConstantData.bookmark_file_type+" TEXT);";
		FM__ConstantData.sqlfm_bookmark.create_table(facebooksql);
		FM__ConstantData.db.close();


		
		connMgr_fmmain = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

		
		direct_sd=new File(android.os.Environment.getExternalStorageDirectory()+"/File Manager/","cache_img");

		Log.e("	FM_homedirpath", 	FM__ConstantData.str_homedirpath);
		Log.e("FM__rootdirpath", 	FM__ConstantData.str_rootdirpath);



		if(!direct_sd.exists())
		{
			if(direct_sd.mkdir()) 
			{
				//directory is created;
			}

		}
		
		Display display = getWindowManager().getDefaultDisplay(); 
		width = display.getWidth();
		height= display.getHeight();

		FM__ConstantData.screenwidth=width;
		FM__ConstantData.screenheight=height;

		Log.e("width", ""+width);
		Log.e("height", ""+height);

		System.gc();
		Runtime.getRuntime().gc();

		// Create a Message object
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		memClass = am.getMemoryClass();

		Log.e("memClass", ""+memClass);
		cacheSize = 1024 * 1024 * memClass / 10;
		bitmapCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getHeight() * value.getRowBytes();
			}
		};

		lstdata=(ListView)findViewById(R.id.lstdata);
		lstdata.setSelected(true);
		txtmessage=(TextView)findViewById(R.id.txtmessage);
		txtmessage.setVisibility(TextView.GONE);
		imgbtninfo=(ImageButton)findViewById(R.id.imgbtninfo);
		imgbtninfo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub


				Intent authIntent = new Intent(File_Manager_MainActivity.this,AboutUS.class);
				startActivity(authIntent);
			}
		});

		mPathLabel = (TextView)findViewById(R.id.txtheadingname);
		mPathLabel.setText("path:"+FM__ConstantData.str_homedirpath);



		/*read settings*/
		mSettings = getSharedPreferences(PREFS_NAME, 0);
		
		editor = mSettings.edit();
		boolean hide = mSettings.getBoolean(PREFS_HIDDEN, true);
		boolean thumb = mSettings.getBoolean(PREFS_THUMBNAIL, true);
		int space = mSettings.getInt(PREFS_STORAGE, View.VISIBLE);
		int color = mSettings.getInt(PREFS_COLOR, -1);
		int sort = mSettings.getInt(PREFS_SORT, 1);

		mFileMag = new FileManager();
		mFileMag.setShowHiddenFiles(hide);
		mFileMag.setSortType(sort);

		if (savedInstanceState != null)
		{
			mHandler = new EventHandler(File_Manager_MainActivity.this, mFileMag);
		}

		else
			mHandler = new EventHandler(File_Manager_MainActivity.this, mFileMag);

		mHandler.setTextColor(color);
		mHandler.setShowThumbnails(thumb);
		mTable = mHandler.new TableRow();

		/*sets the ListAdapter for our ListActivity and
		 *gives our EventHandler class the same adapter
		 */
		mHandler.setListAdapter(mTable);

		lstdata.setAdapter(mTable);

		mTable.notifyDataSetChanged();
		/* register context menu for our list view */
		//registerForContextMenu(lstdata);




		mPathLabel.setText(FM__ConstantData.str_homedirpath);

		mHandler.setUpdateLabels(mPathLabel);

		/* setup buttons */
		int[] img_button_id = {R.id.imgbtnfav,R.id.imgbtnpaste, R.id.imgbtnhome, 
				R.id.imgbtnback, R.id.imgbtnup, 
				R.id.imgbtncreate,R.id.imgbtncreate_file, R.id.imgbtnnext,R.id.multiselect_button,
				R.id.imgbtnrefresh,R.id.imgbtnsearch,R.id.imgbtnsort_ascending,R.id.imgbtnsort_descending,R.id.imgbtnsort_size};
//		,R.id.imgbtnsort_type};

		int[] button_id = {R.id.hidden_copy,
				R.id.hidden_delete, R.id.hidden_move};

		ImageButton[] bimg = new ImageButton[img_button_id.length];
		Button[] bt = new Button[button_id.length];

		for(int i = 0; i < img_button_id.length; i++) 
		{
			bimg[i] = (ImageButton)findViewById(img_button_id[i]);
			bimg[i].setOnClickListener(mHandler);

			if(i < 3) 
			{
				bt[i] = (Button)findViewById(button_id[i]);
				bt[i].setOnClickListener(mHandler);
			}
		}

		Intent intent = getIntent();

		if(intent.getAction().equals(Intent.ACTION_GET_CONTENT)) {
			bimg[5].setVisibility(View.GONE);
			mReturnIntent = true;

		} else if (intent.getAction().equals(ACTION_WIDGET)) {
			Log.e("MAIN", "Widget action, string = " + intent.getExtras().getString("folder"));
			mHandler.updateDirectory(mFileMag.getNextDir(intent.getExtras().getString("folder"), true));

		}

		lstdata.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0,  View view, int position, long id)
			{

				mainselectpos= position;

				if (!FM__ConstantData.backflag) 
				{
					FM__ConstantData.backindexvalue=0;
					FM__ConstantData.backindexvalue= position;
					FM__ConstantData.backflag=true;
				}
				final String item = mHandler.getData(position);

				boolean multiSelect = mHandler.isMultiSelected();
				
				
				File file = new File(mFileMag.getCurrentDir() + "/" + item);
				String item_ext = null;

				try {
					item_ext = item.substring(item.lastIndexOf("."), item.length());

				} catch(IndexOutOfBoundsException e) {	
					item_ext = ""; 
				}

				/*
				 * If the user has multi-select on, we just need to record the file
				 * not make an intent for it.
				 */
				if(multiSelect) 
				{
					mTable.addMultiPosition(position, file.getPath());

				} 
				else
				{
					
					if (file.isDirectory()) 
					{
						if(file.canRead()) 
						{
							//mHandler.stopThumbnailThread();

							String strcurrnt_dir=mFileMag.getCurrentDir();

							if(strcurrnt_dir.equalsIgnoreCase(FM__ConstantData.str_homedirpath))
							{


								arrylist_next_folder= new  ArrayList<String>();
								EventHandler.next_folder_index=0;
								flag_str_homedirpath=true;
							}


							else if(strcurrnt_dir.equalsIgnoreCase("/"))
							{
								flag_str_homedirpath=false;
								arrylist_next_folder= new  ArrayList<String>();
								EventHandler.next_folder_index=0;

							}



							mHandler.updateDirectory(mFileMag.getNextDir(item, false));
							mPathLabel.setText(mFileMag.getCurrentDir());


							if(flag_str_homedirpath)
							{
								strcurrnt_dir=mFileMag.getCurrentDir();

								if(strcurrnt_dir.equalsIgnoreCase(FM__ConstantData.str_homedirpath))
								{

								}
								else
								{
									arrylist_next_folder.add(item);
									EventHandler.next_folder_index=EventHandler.next_folder_index+1;
								}

							}
							/*set back button switch to true 
							 * (this will be better implemented later)
							 */
							if(!mUseBackKey)
								mUseBackKey = true;

						} else {
							Toast.makeText(File_Manager_MainActivity.this, mainacActivity.getResources().getString(R.string.Cannot_read_folder_due_to_permissions), 
									Toast.LENGTH_SHORT).show();
						}
					}

					/*video file selected--add more video formats*/
					else if(item_ext.equalsIgnoreCase(".m4v") || 
							item_ext.equalsIgnoreCase(".3gp") ||
							item_ext.equalsIgnoreCase(".wmv") || 
							item_ext.equalsIgnoreCase(".mp4") || 
							item_ext.equalsIgnoreCase(".ogg") ||
							item_ext.equalsIgnoreCase(".wav") ||
							item_ext.equalsIgnoreCase(".mov"))
					{


						String mimetypedata = getMimeType(file.getAbsolutePath());
						
						if(mimetypedata!=null)
						{
							Log.e("mimetypedata", mimetypedata);
						}
						
						if (file.exists()) {
							if(mReturnIntent) 
							{
								returnIntentResults(file);

							}
							else
							{
								try
								{
									getIntent().putExtra("fromVitamioInitActivity", false);


									Intent intent = new Intent(File_Manager_MainActivity.this, FM_Video_Open.class);
									intent.setData(Uri.fromFile(file));
									try
									{
										startActivity(intent);

									} catch(ActivityNotFoundException e)
									{
										Log.e("Exception",""+e.toString());
									}
								}
								catch (Exception e)
								{
									Log.e("Exception",""+e.toString());
								}


							}
						}
					}
					/*music file selected--add more audio formats*/
					else if (item_ext.equalsIgnoreCase(".mp3") || 
							item_ext.equalsIgnoreCase(".m4a")||
							item_ext.equalsIgnoreCase(".amr")||
							item_ext.equalsIgnoreCase(".midi")||
							item_ext.equalsIgnoreCase(".mp4")) 
					{

						if (file.exists())
						{
							if(mReturnIntent) 
							{
								returnIntentResults(file);
							}
							else
							{
								Intent intent = new Intent(File_Manager_MainActivity.this,FM_Audio_Open.class);
								intent.setData(Uri.fromFile(file));
								try
								{
									startActivity(intent);

								} catch(ActivityNotFoundException e)
								{

								}

							}
						}
					}

					/*photo file selected*/
					else if(item_ext.equalsIgnoreCase(".jpeg") || 
							item_ext.equalsIgnoreCase(".jpg")  ||
							item_ext.equalsIgnoreCase(".png")  ||
							item_ext.equalsIgnoreCase(".gif")  || 
							item_ext.equalsIgnoreCase(".bmp")  || 
							item_ext.equalsIgnoreCase(".webp")  || 
							item_ext.equalsIgnoreCase(".psd")  || 
							item_ext.equalsIgnoreCase(".tiff")) {

						if (file.exists()) {
							if(mReturnIntent) {
								returnIntentResults(file);

							}
							else
							{
								
								
								
								Intent picIntent = new Intent(File_Manager_MainActivity.this,FM_Full_Image.class);
								picIntent.setData(Uri.fromFile(file));
								startActivity(picIntent);

							}
						}
					}

					/*video file selected--add more video formats*/
					else if(item_ext.equalsIgnoreCase(".m4v") || 
							item_ext.equalsIgnoreCase(".3gp") ||
							item_ext.equalsIgnoreCase(".wmv") || 
							item_ext.equalsIgnoreCase(".mp4") || 
							item_ext.equalsIgnoreCase(".ogg") ||
							item_ext.equalsIgnoreCase(".wav")
							||
							item_ext.equalsIgnoreCase(".mov")) {

						if (file.exists()) {
							if(mReturnIntent) 
							{
								returnIntentResults(file);

							}
							else
							{
								try
								{



									Intent intent = new Intent(File_Manager_MainActivity.this, FM_Video_Open.class);
									intent.setData(Uri.fromFile(file));
									try
									{
										startActivity(intent);

									} catch(ActivityNotFoundException e)
									{
										Log.e("Exception",""+e.toString());
									}
								}
								catch (Exception e)
								{
									Log.e("Exception",""+e.toString());
								}




							}
						}
					}

					//					/*zip file */

					/*pdf file selected*/
					else if(item_ext.equalsIgnoreCase(".zip")) 
					{

						new ExtractManager(File_Manager_MainActivity.this).extract(file.getAbsoluteFile(), mFileMag.getCurrentDir());
						
					}


					
					
					/*pdf file selected*/
					else if(item_ext.equalsIgnoreCase(".pdf")) 
					{

						if(file.exists()) {
							if(mReturnIntent) {
								returnIntentResults(file);

							} else {
								Intent pdfIntent = new Intent();
								pdfIntent.setAction(android.content.Intent.ACTION_VIEW);
								pdfIntent.setDataAndType(Uri.fromFile(file), 
										"application/pdf");

								try {
									startActivity(pdfIntent);
								} catch (ActivityNotFoundException e) {
									Toast.makeText(File_Manager_MainActivity.this, mainacActivity.getResources().getString(R.string.Sorry_could_not_find_a_pdf_viewer), 
											Toast.LENGTH_SHORT).show();
								}
							}
						}
					}

					else if(item_ext.equalsIgnoreCase(".doc")||item_ext.equalsIgnoreCase(".docx")) 
					{

						if(file.exists()) {
							if(mReturnIntent) {
								returnIntentResults(file);

							} else 
							
							{
//								Intent intent = new Intent(File_Manager_MainActivity.this,FM_Doc_File_Open.class);
//								Bundle bndldata= new Bundle();
//								
//								bndldata.putString("strfile_path", file.getAbsolutePath());
//								intent.putExtras(bndldata);
//								startActivity(intent);
								
								Intent pdfIntent = new Intent();
								pdfIntent.setAction(android.content.Intent.ACTION_VIEW);
								pdfIntent.setDataAndType(Uri.fromFile(file), 
										"application/msword");


								try {
									startActivity(pdfIntent);
								} catch (ActivityNotFoundException e) {
									Toast.makeText(File_Manager_MainActivity.this, mainacActivity.getResources().getString(R.string.Sorry_could_not_find_a_Document_viewer), 
											Toast.LENGTH_SHORT).show();
								}
							}
						}
					}
					
					else if(item_ext.equalsIgnoreCase(".xls")||item_ext.equalsIgnoreCase(".xlsx")) 
					{

						if(file.exists()) {
							if(mReturnIntent) {
								returnIntentResults(file);

							} else {
								Intent pdfIntent = new Intent();
								pdfIntent.setAction(android.content.Intent.ACTION_VIEW);
								pdfIntent.setDataAndType(Uri.fromFile(file), 
										"application/vnd.ms-excel");

								try {
									startActivity(pdfIntent);
								} catch (ActivityNotFoundException e) {
									Toast.makeText(File_Manager_MainActivity.this, mainacActivity.getResources().getString(R.string.Sorry_could_not_find_a_Document_viewer), 
											Toast.LENGTH_SHORT).show();
								}
							}
						}
					}
					
					else if(item_ext.equalsIgnoreCase(".ppt")||item_ext.equalsIgnoreCase(".pptx")) 
					{

						if(file.exists()) {
							if(mReturnIntent) {
								returnIntentResults(file);

							} else {
								Intent pdfIntent = new Intent();
								pdfIntent.setAction(android.content.Intent.ACTION_VIEW);
								pdfIntent.setDataAndType(Uri.fromFile(file), 
										"application/vnd.ms-powerpoint");

								try {
									startActivity(pdfIntent);
								} catch (ActivityNotFoundException e) {
									Toast.makeText(File_Manager_MainActivity.this, mainacActivity.getResources().getString(R.string.Sorry_could_not_find_a_Document_viewer),  
											Toast.LENGTH_SHORT).show();
								}
							}
						}
					}

					
					// jar folder
					else if(item_ext.equalsIgnoreCase(".jar")) 
					{

						if(file.exists()) {
							if(mReturnIntent) {
								returnIntentResults(file);

							} else {
								Intent pdfIntent = new Intent();
								pdfIntent.setAction(android.content.Intent.ACTION_VIEW);
								pdfIntent.setDataAndType(Uri.fromFile(file), 
										"application/java-archive");

								try {
									startActivity(pdfIntent);
								} catch (ActivityNotFoundException e) {
									Toast.makeText(File_Manager_MainActivity.this, mainacActivity.getResources().getString(R.string.Sorry_could_not_find_a_Document_viewer),  
											Toast.LENGTH_SHORT).show();
								}
							}
						}
					}


					
					/*Android application file*/
					else if(item_ext.equalsIgnoreCase(".apk")){

						if(file.exists()) {
							if(mReturnIntent) {
								returnIntentResults(file);

							} else {
								Intent apkIntent = new Intent();
								apkIntent.setAction(android.content.Intent.ACTION_VIEW);
								apkIntent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
								startActivity(apkIntent);
							}
						}
					}

					
					
					/* HTML file */
					else if(item_ext.equalsIgnoreCase(".html")) {

						if(file.exists()) {
							if(mReturnIntent) {
								returnIntentResults(file);

							} else {
								
								
//								Intent intent = new Intent(File_Manager_MainActivity.this,FM_Doc_File_Open.class);
//								Bundle bndldata= new Bundle();
//								
//								bndldata.putString("strfile_path", file.getAbsolutePath());
//								intent.putExtras(bndldata);
//								startActivity(intent);
								
								Intent htmlIntent = new Intent();
								htmlIntent.setAction(android.content.Intent.ACTION_VIEW);
								htmlIntent.setDataAndType(Uri.fromFile(file), "text/html");

								try {
									startActivity(htmlIntent);
								} catch(ActivityNotFoundException e) {
									Toast.makeText(File_Manager_MainActivity.this, mainacActivity.getResources().getString(R.string.Sorry_could_not_find_a_HTML_viewer), 
											Toast.LENGTH_SHORT).show();
								}
							}
						}
					}

					
					/* php file*/
					else if(item_ext.equalsIgnoreCase(".php")) 
					{

						if(file.exists()) {
							if(mReturnIntent) {
								returnIntentResults(file);

							} else {
								Intent txtIntent = new Intent();
								txtIntent.setAction(android.content.Intent.ACTION_VIEW);
								txtIntent.setDataAndType(Uri.fromFile(file), "text/php");

								try {
									startActivity(txtIntent);
								} catch(ActivityNotFoundException e) {
									txtIntent.setType("text/*");
									startActivity(txtIntent);
								}
							}
						}
					}

					
					
					/* xml file*/
					else if(item_ext.equalsIgnoreCase(".xml")) 
					{

						if(file.exists()) {
							if(mReturnIntent) {
								returnIntentResults(file);

							} else {
								Intent txtIntent = new Intent();
								txtIntent.setAction(android.content.Intent.ACTION_VIEW);
								txtIntent.setDataAndType(Uri.fromFile(file), "text/xml");

								try {
									startActivity(txtIntent);
								} catch(ActivityNotFoundException e) {
									txtIntent.setType("text/*");
									startActivity(txtIntent);
								}
							}
						}
					}
					
					
					/* css file*/
					else if(item_ext.equalsIgnoreCase(".css")) 
					{

						if(file.exists()) {
							if(mReturnIntent) {
								returnIntentResults(file);

							} else {
								Intent txtIntent = new Intent();
								txtIntent.setAction(android.content.Intent.ACTION_VIEW);
								txtIntent.setDataAndType(Uri.fromFile(file), "text/plain");

								try {
									startActivity(txtIntent);
								} catch(ActivityNotFoundException e) {
									txtIntent.setType("text/*");
									startActivity(txtIntent);
								}
							}
						}
					}
					
					/* rft file*/
					else if(item_ext.equalsIgnoreCase(".rft")) 
					{

						if(file.exists()) {
							if(mReturnIntent) {
								returnIntentResults(file);

							} else {
								Intent txtIntent = new Intent();
								txtIntent.setAction(android.content.Intent.ACTION_VIEW);
								txtIntent.setDataAndType(Uri.fromFile(file), "text/plain");

								try {
									startActivity(txtIntent);
								} catch(ActivityNotFoundException e) {
									txtIntent.setType("text/*");
									startActivity(txtIntent);
								}
							}
						}
					}
					
					/* java file*/
					else if(item_ext.equalsIgnoreCase(".java")) 
					{

						if(file.exists()) {
							if(mReturnIntent) {
								returnIntentResults(file);

							} else {
								Intent txtIntent = new Intent();
								txtIntent.setAction(android.content.Intent.ACTION_VIEW);
								txtIntent.setDataAndType(Uri.fromFile(file), "text/plain");

								try {
									startActivity(txtIntent);
								} catch(ActivityNotFoundException e) {
									txtIntent.setType("text/*");
									startActivity(txtIntent);
								}
							}
						}
					}
					
					/* text file*/
					else if(item_ext.equalsIgnoreCase(".txt")) 
					{

						if(file.exists()) 
						{
							if(mReturnIntent)
							{
								returnIntentResults(file);

							} else 
							{
								
								Intent intent = new Intent(File_Manager_MainActivity.this,EditorActivity.class);
								intent.setData(Uri.fromFile(file));

								try {
									startActivity(intent);
//									startActivity(txtIntent);
								} catch(ActivityNotFoundException e) 
								{
//									txtIntent.setType("text/*");
//									startActivity(txtIntent);
								}
							}
						}
					}

					/* generic intent */
					else {
						if(file.exists()) {
							if(mReturnIntent) {
								returnIntentResults(file);

							} else {
								Intent generic = new Intent();
								generic.setAction(android.content.Intent.ACTION_VIEW);
								generic.setDataAndType(Uri.fromFile(file), "text/plain");

								try {
									startActivity(generic);
								} catch(ActivityNotFoundException e) {
									Toast.makeText(File_Manager_MainActivity.this, mainacActivity.getResources().getString(R.string.Sorry_could_not_find_anything) +
											" " +mainacActivity.getResources().getString(R.string.to_open)+" " + file.getName(), 
											Toast.LENGTH_SHORT).show();
								}
							}
						}
					}
				}




				connMgr_fmmain = (ConnectivityManager)File_Manager_MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);

				if (connMgr_fmmain.getActiveNetworkInfo() != null && connMgr_fmmain.getActiveNetworkInfo().isAvailable() && connMgr_fmmain.getActiveNetworkInfo().isConnected())
				{


					Log.e("Constant infocounter", FM__ConstantData.infocounter+"");


					if(FM__ConstantData.infocounter==0 || FM__ConstantData.infocounter==5)
					{


						try {

							// Create an ad.
							interstitialAd = new InterstitialAd(File_Manager_MainActivity.this);
							interstitialAd.setAdUnitId(FM__ConstantData.Admob_INTERSTITIAL_UNIT_ID);







							//AdView mAdView = (AdView) findViewById(R.id.adView);
							AdRequest adRequest;
							if (BuildConfig.DEBUG)
							{
								try
								{
									String android_id = Settings.Secure.getString(File_Manager_MainActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
									deviceId_md5 = md5(android_id).toUpperCase();
									Log.e("Device ID: ",""+deviceId_md5);
								}
								catch (Exception e)
								{

								}

								adRequest = new AdRequest.Builder()

										//AppAspect Nexus Tablet 4.4
										.addTestDevice("829B1CAB0DBEBF29479B0DDFB320328E")

										//AppAspect Samsung Tablet 3.2
										.addTestDevice("0B3B8773DC0B8378AE15FCC9D5A11A44")

										//Hims Android 4.2 Grandd
										.addTestDevice("F0690C43495E1DA80D5F21753AD4367F")

										//Device ma'am
										.addTestDevice("CF85E258C590EC7A688E84ED182F2B3A")
										// Dara's Device ID:
										.addTestDevice("D1AD87B0ADB9776B045B528774F7E791")
										.addTestDevice(deviceId_md5)
										.build();
								boolean isTestDevice = adRequest.isTestDevice(File_Manager_MainActivity.this);
								Log.v("Admob:", "is Admob Test Device ? " + deviceId_md5 + " " + isTestDevice); //to confirm it worked
							}
							else
							{
								adRequest = new AdRequest.Builder().build();
							}



							// Load the interstitial ad.
							interstitialAd.loadAd(adRequest);




							// Set the AdListener.
							interstitialAd.setAdListener(new AdListener() {
								@Override
								public void onAdLoaded() {
									Log.d(LOG_TAG, "onAdLoaded");


									if (interstitialAd.isLoaded()) {
										interstitialAd.show();
									} else {
										Log.d(LOG_TAG, "Interstitial ad was not ready to be shown.");
									}

									// Check the logcat output for your hashed device ID to get test ads on a physical device.

									//   Toast.makeText(InterstitialSample.this, "onAdLoaded", Toast.LENGTH_SHORT).show();

									// Change the button text and enable the button.
									//  showButton.setText("Show Interstitial");
									//   showButton.setEnabled(true);
								}

								@Override
								public void onAdFailedToLoad(int errorCode) {
									String message = String.format("onAdFailedToLoad (%s)", getErrorReason(errorCode));
									//  Log.d(LOG_TAG, message);
									//   Toast.makeText(InterstitialSample.this, message, Toast.LENGTH_SHORT).show();

									// Change the button text and disable the button.
									//   showButton.setText("Ad Failed to Load");
									//   showButton.setEnabled(false);
								}
							});


						} catch (Exception e) {
							// TODO: handle exception
						}

						FM__ConstantData.infocounter=FM__ConstantData.infocounter+1;

					}
					else
					{
						FM__ConstantData.infocounter=FM__ConstantData.infocounter+1;
					}






				}
			}
		});


		lstdata.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub

				String srt_file_type=null;
				String srt_file_mime_type=null;
				String selected_item = mHandler.getData(position);

				String srt_file_path= mFileMag.getCurrentDir() +"/"+ selected_item;
				File file = new File(mFileMag.getCurrentDir() + "/" + selected_item);

				String item_ext = null;

				try {
					item_ext = selected_item.substring(selected_item.lastIndexOf(".")+1, selected_item.length());

					/*music file selected--add more Video formats*/
					 if(item_ext.equalsIgnoreCase("m4v") || 
							item_ext.equalsIgnoreCase("3gp") ||
							item_ext.equalsIgnoreCase("wmv") || 
							item_ext.equalsIgnoreCase("mp4") || 
							item_ext.equalsIgnoreCase("ogg") ||
							item_ext.equalsIgnoreCase("wav")
							 ||
							 item_ext.equalsIgnoreCase(".mov"))
					 {
						 srt_file_mime_type="video/"+item_ext;
						
					}
					 /*music file selected--add more audio formats*/
						else if (item_ext.equalsIgnoreCase("mp3") || 
								item_ext.equalsIgnoreCase("m4a")||
								item_ext.equalsIgnoreCase("amr")||
								item_ext.equalsIgnoreCase("midi")||
								item_ext.equalsIgnoreCase("mp4")) 
						{
							 srt_file_mime_type="audio/"+item_ext;
						}
					 /*photo file selected*/
						else if(item_ext.equalsIgnoreCase("jpeg") || 
								item_ext.equalsIgnoreCase("jpg")  ||
								item_ext.equalsIgnoreCase("png")  ||
								item_ext.equalsIgnoreCase("gif")  || 
								item_ext.equalsIgnoreCase("bmp")  || 
								item_ext.equalsIgnoreCase("webp")  || 
								item_ext.equalsIgnoreCase("psd")  || 
								item_ext.equalsIgnoreCase("tiff")) 
						{
							 srt_file_mime_type="image/"+item_ext;
						}
						else if(item_ext.equalsIgnoreCase(".zip")) 
						{
							 srt_file_mime_type="application/zip";
						}
						else if(item_ext.equalsIgnoreCase(".pdf")) 
						{
							 srt_file_mime_type="application/pdf";
						}
						else if(item_ext.equalsIgnoreCase(".doc")||item_ext.equalsIgnoreCase(".docx")) 
						{
							srt_file_mime_type="application/msword";
						}
						else if(item_ext.equalsIgnoreCase(".xls")||item_ext.equalsIgnoreCase(".xlsx")) 
						{
							srt_file_mime_type="application/vnd.ms-excel";
						}
						else if(item_ext.equalsIgnoreCase(".ppt")||item_ext.equalsIgnoreCase(".pptx")) 
						{
							srt_file_mime_type="application/vnd.ms-excel";
						}
						else if(item_ext.equalsIgnoreCase(".jar")) 
						{
							srt_file_mime_type="application/java-archive";
						}
						else if(item_ext.equalsIgnoreCase(".apk"))
						{
							srt_file_mime_type="application/vnd.android.package-archive";
						}
						else if(item_ext.equalsIgnoreCase(".html")) 
						{
							srt_file_mime_type="text/html";
							
						}
						else if(item_ext.equalsIgnoreCase(".php")) 
						{
							srt_file_mime_type="text/*";
						}
						else if(item_ext.equalsIgnoreCase(".xml")) 
						{
							srt_file_mime_type="text/*";
						}
						else if(item_ext.equalsIgnoreCase(".css")) 
						{
							srt_file_mime_type="text/*";
						}
						else if(item_ext.equalsIgnoreCase(".rft")) 
						{
							srt_file_mime_type="text/*";
						}
						else if(item_ext.equalsIgnoreCase(".java")) 
						{
							srt_file_mime_type="text/*";
						}
						else if(item_ext.equalsIgnoreCase(".txt")) 
						{
							srt_file_mime_type="text/*";
						}
						else
						{
							srt_file_mime_type="text/*";
						}
					 
				} catch(IndexOutOfBoundsException e) 
				{	
					item_ext = ""; 
					srt_file_mime_type="text/*";
				}

				if(file.exists())
				{
					if(file.isDirectory())
					{
						srt_file_type="Directory";
					}
					else
					{
						if(item_ext.equalsIgnoreCase("zip"))
						{
							srt_file_type="zip";
						}
						else
						{
							srt_file_type=item_ext;
							//srt_file_type="file";
						}
					}
				}
				Log.e("srt_file_path", srt_file_path);
				Log.e("srt_file_type", srt_file_type);
				
				if(srt_file_type==null)
				{
					Toast.makeText(File_Manager_MainActivity.this, mainacActivity.getResources().getString(R.string.Sorry_could_not_find_anything) +
							" " +mainacActivity.getResources().getString(R.string.to_open)+" " + file.getName(), 
							Toast.LENGTH_SHORT).show();
					
				}
				else
				{
					Intent intdata= new Intent(File_Manager_MainActivity.this, FM_FileOperations_List.class);
					Bundle bdl_data= new Bundle();
					bdl_data.putString("srt_file_path", srt_file_path);
					bdl_data.putString("srt_file_name", selected_item);
					bdl_data.putString("srt_file_type", srt_file_type);
					bdl_data.putString("srt_file_mime_type", srt_file_mime_type);
					

					intdata.putExtras(bdl_data);
					startActivity(intdata);
				}
				

				return false;
			}


		});
	}
	public Context getDialogContext() {
		Context context;
		if (getParent() != null) context = getParent();
		else context = this;
		return context;
	}


	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// Save the ListView position
		mListViewScrollPos =lstdata.onSaveInstanceState();
	}
	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
//
//		int index = lstdata.getFirstVisiblePosition();
//		View v = lstdata.getChildAt(0);
//		int top = (v == null) ? 0 : (v.getTop() - lstdata.getPaddingTop());
//
//		lstdata.setSelectionFromTop(index, top);
//
//		if (mListViewScrollPos != null)
//		{
//			lstdata.onRestoreInstanceState(mListViewScrollPos);
//		}






		if(mPathLabel != null)
			mPathLabel.setText(mFileMag.getCurrentDir());

		File_Manager_MainActivity.mHandler.updateDirectory(File_Manager_MainActivity.mFileMag.getrefresh(File_Manager_MainActivity.mFileMag.getCurrentDir())) ;
		
		
		if(FM__ConstantData.srt_file_path_for_copy==null || FM__ConstantData.srt_file_path_for_copy=="")
		{
			 llpaste=(LinearLayout)findViewById(R.id.llpaste);
			llpaste.setVisibility(LinearLayout.GONE);
		}
		else
		{
			llpaste=(LinearLayout)findViewById(R.id.llpaste);
			llpaste.setVisibility(LinearLayout.VISIBLE);
		}
		
		
		if(mHandler.multi_select_flag)
		{
			
			ImageButton imgbtn_multiselect_button = (ImageButton)findViewById(R.id.multiselect_button);
			imgbtn_multiselect_button.setBackgroundResource(R.drawable.multi_select);
			//multi_select_flag = false;

		} else 
		{
			
			ImageButton imgbtn_multiselect_button = (ImageButton)findViewById(R.id.multiselect_button);
			imgbtn_multiselect_button.setBackgroundResource(R.drawable.multi_select_);
			
		}


	}




	private void returnIntentResults(File data) {
		mReturnIntent = false;

		Intent ret = new Intent();
		ret.setData(Uri.fromFile(data));
		setResult(RESULT_OK, ret);

		finish();
	}
	@Override
	public boolean onKeyDown(int keycode, KeyEvent event)
	{
		String current = mFileMag.getCurrentDir();
		Log.e("current", current);

		String s_Main_Remove=current;
		String s_Main=current.substring(current.lastIndexOf("/"));
		s_Main_Remove=current.replace(s_Main,"");
		if(s_Main_Remove.equalsIgnoreCase(FM__ConstantData.str_homedirpath))
		{
			mHandler.updateDirectory(mFileMag.getPreviousDir());
			if(mPathLabel != null)
				mPathLabel.setText(mFileMag.getCurrentDir());
			current=FM__ConstantData.str_homedirpath;
			FM__ConstantData.backflag=false;
		}




		if(keycode == KeyEvent.KEYCODE_SEARCH)
		{
			showDialog(SEARCH_B);

			return true;

		}

		else if(keycode == KeyEvent.KEYCODE_BACK && current.equals("/")) 
		{

			mHandler.updateDirectory(mFileMag.setHomeDir(FM__ConstantData.str_homedirpath));

			mPathLabel.setText(mFileMag.getCurrentDir());

			lstdata.setAdapter(mTable);

			mTable.notifyDataSetChanged();
			//mTable.notifyDataSetChanged();
			Log.e("else", "else");
			//	lstdata.setSelectionFromTop(15, 0);
			lstdata.setSelected(true);
			lstdata.setSelection(FM__ConstantData.backindexvalue);
			lstdata.requestFocus();	
			mUseBackKey = true;
			return false;

		}

		else if(keycode == KeyEvent.KEYCODE_BACK && !current.equals(FM__ConstantData.str_homedirpath))
		{
			if(mHandler.isMultiSelected())
			{

				mTable.killMultiSelect_paste(true);
				if(mHandler.multi_select_flag)
				{

					ImageButton imgbtn_multiselect_button = (ImageButton)findViewById(R.id.multiselect_button);
					imgbtn_multiselect_button.setBackgroundResource(R.drawable.multi_select);
					//multi_select_flag = false;

				} else
				{

					ImageButton imgbtn_multiselect_button = (ImageButton)findViewById(R.id.multiselect_button);
					imgbtn_multiselect_button.setBackgroundResource(R.drawable.multi_select_);

				}
			}
			else
			{

				if(FM__ConstantData.str_homedirpath_up.equalsIgnoreCase("/"))
				{

				}
				else
				{
					mHandler.updateDirectory(mFileMag.getPreviousDir());




					if(mPathLabel != null)
						mPathLabel.setText(mFileMag.getCurrentDir());

				}

			}
			return true;

		}

		else if(keycode == KeyEvent.KEYCODE_BACK && mUseBackKey && current.equals(FM__ConstantData.str_homedirpath))
		{

			lstdata.setAdapter(mTable);

			mTable.notifyDataSetChanged();
			if(current.equals(FM__ConstantData.str_homedirpath))
			{
				lstdata.setSelected(true);
				lstdata.setSelection(FM__ConstantData.backindexvalue);
				lstdata.requestFocus();
			}

			Toast.makeText(File_Manager_MainActivity.this, mainacActivity.getResources().getString(R.string.Press_back_again_to_quit), Toast.LENGTH_SHORT).show();

				mTable.killMultiSelect_paste(true);

				if(mHandler.multi_select_flag)
				{

					ImageButton imgbtn_multiselect_button = (ImageButton)findViewById(R.id.multiselect_button);
					imgbtn_multiselect_button.setBackgroundResource(R.drawable.multi_select);

				} else
				{

					ImageButton imgbtn_multiselect_button = (ImageButton)findViewById(R.id.multiselect_button);
					imgbtn_multiselect_button.setBackgroundResource(R.drawable.multi_select_);

				}

			mUseBackKey = false;
			mPathLabel.setText(mFileMag.getCurrentDir());

			return false;

		}
		else if(keycode == KeyEvent.KEYCODE_BACK && !mUseBackKey && current.equals(FM__ConstantData.str_homedirpath)) 
		{
			
			if (connMgr_fmmain.getActiveNetworkInfo() != null && connMgr_fmmain.getActiveNetworkInfo().isAvailable() && connMgr_fmmain.getActiveNetworkInfo().isConnected()) 
			{
				
			}
			finish();

			return false;
		}

		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contextmenu, menu);


		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo info) {

		super.onCreateContextMenu(menu, v, info);

		//IconContextMenu cm = new IconContextMenu(context, R.menu.contextmenu);




		//		boolean multi_data = mHandler.hasMultiSelectData();
		//		AdapterContextMenuInfo _info = (AdapterContextMenuInfo)info;
		//		mSelectedListItem = mHandler.getData(_info.position);
		//
		//		try {
		//			menu.clear();
		//			menu.setHeaderIcon(R.drawable.folder);
		//			menu.setHeaderTitle("Share Menu.");
		//			MenuInflater inflater = getMenuInflater();
		//
		//			inflater.inflate(R.layout.contextmenu_listview, menu);
		//
		//		} catch (Exception e) {
		//			// TODO: handle exception
		//			Log.e("menu", e.getMessage());
		//		}


		//		/* is it a directory and is multi-select turned off */
		//		if(mFileMag.isDirectory(mSelectedListItem) && !mHandler.isMultiSelected()) {
		//			menu.setHeaderTitle("Folder operations");
		//			menu.add(0, D_MENU_DELETE, 0, "Delete Folder").setIcon(R.drawable.folder);
		//			menu.add(0, D_MENU_RENAME, 0, "Rename Folder");
		//			menu.add(0, D_MENU_COPY, 0, "Copy Folder");
		//			menu.add(0, D_MENU_MOVE, 0, "Move(Cut) Folder");
		//			menu.add(0, D_MENU_ZIP, 0, "Zip Folder");
		//			menu.add(0, D_MENU_PASTE, 0, "Paste into folder").setEnabled(mHoldingFile || 
		//					multi_data);
		//			menu.add(0, D_MENU_UNZIP, 0, "Extract here").setEnabled(mHoldingZip);
		//
		//			/* is it a file and is multi-select turned off */
		//		} else if(!mFileMag.isDirectory(mSelectedListItem) && !mHandler.isMultiSelected()) {
		//			menu.setHeaderTitle("File Operations");
		//			menu.add(0, F_MENU_DELETE, 0, "Delete File");
		//			menu.add(0, F_MENU_RENAME, 0, "Rename File");
		//			menu.add(0, F_MENU_COPY, 0, "Copy File");
		//			menu.add(0, F_MENU_MOVE, 0, "Move(Cut) File");
		//			menu.add(0, F_MENU_ATTACH, 0, "Email File");
		//		}	
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		
		
		
		
		System.gc();
		Runtime.getRuntime().gc();
		
	}
	public static String getMimeType(String url)
	{
		String type = null;
		String extension = MimeTypeMap.getFileExtensionFromUrl(url);
		if (extension != null) {
			MimeTypeMap mime = MimeTypeMap.getSingleton();
			type = mime.getMimeTypeFromExtension(extension);
		}
		return type;
	}

	
	/** Called when the Load Interstitial button is clicked. */
	  public void loadInterstitial(View unusedView) {
	    // Disable the show button until the new ad is loaded.
	//    showButton.setText("Loading Interstitial...");
	 //   showButton.setEnabled(false);

	    // Check the logcat output for your hashed device ID to get test ads on a physical device.
	    AdRequest adRequest = new AdRequest.Builder()
	        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
	        .addTestDevice("INSERT_YOUR_HASHED_DEVICE_ID_HERE")
				.addTestDevice("E6B3B159DE871B7F3618B3F61A8C8294")
	        .build();

	    // Load the interstitial ad.
	    interstitialAd.loadAd(adRequest);
	  }

	  /** Called when the Show Interstitial button is clicked. */
	  public void showInterstitial(View unusedView) {
	    // Disable the show button until another interstitial is loaded.
	//    showButton.setText("Interstitial Not Ready");
	 //   showButton.setEnabled(false);

	    if (interstitialAd.isLoaded()) {
	      interstitialAd.show();
	    } else {
	      Log.d(LOG_TAG, "Interstitial ad was not ready to be shown.");
	    }
	  }

	  /** Gets a string error reason from an error code. */
	  private String getErrorReason(int errorCode) {
	    String errorReason = "";
	    switch(errorCode) {
	      case AdRequest.ERROR_CODE_INTERNAL_ERROR:
	        errorReason = "Internal error";
	        break;
	      case AdRequest.ERROR_CODE_INVALID_REQUEST:
	        errorReason = "Invalid request";
	        break;
	      case AdRequest.ERROR_CODE_NETWORK_ERROR:
	        errorReason = "Network Error";
	        break;
	      case AdRequest.ERROR_CODE_NO_FILL:
	        errorReason = "No fill";
	        break;
	    }
	    return errorReason;
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



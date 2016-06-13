package com.filemanager.sdexplorer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.filemanager.sdexplorer.data.ExtractManager;
import com.filemanager.sdexplorer.data.FM_Bookmark_Data;
import com.filemanager.sdexplorer.data.FM__Bookmark_list_Adapter;
import com.filemanager.sdexplorer.data.FM__ConstantData;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.File;
import java.security.MessageDigest;
import java.util.ArrayList;

public class FM_bookmarkList extends Activity {



	private ListView lstbookmark;
	private ArrayList<FM_Bookmark_Data> favuserlist;

	private String str_bookmark_name="";
	private String filename,item_ext;
	private boolean bdata=false;
	private ConnectivityManager connMgr_info;
	private ImageButton imgbtnback;
	private boolean mReturnIntent = false;
	private TextView txtinfoname;
	private String deviceId_md5;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fm_bookmarklist);

		lstbookmark=(ListView)findViewById(R.id.lstbookmark);

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



		connMgr_info = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

		try {

			//initialise Analytics
		 FM__ConstantData.initialiseAnalytics(FM__ConstantData.Google_Analytic_ID,this);
			//add Google Analytics view
			String str_name=this.getClass().getSimpleName();
			Log.e("str_name", str_name);
			FM__ConstantData.AnalyticsView("Bookmark List Activity");

		} catch (Exception e) {
			// TODO: handle exception

			Log.e("AnalyticsView", e.toString());

		}
		
		
		txtinfoname=(TextView)findViewById(R.id.txtinfoname);

		txtinfoname.setText(getResources().getString(R.string.bookmarklist));

		imgbtnback=(ImageButton)findViewById(R.id.imgbtnback);	
		imgbtnback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				finish();
			}
		});


		//		Intent intent = getIntent();
		//
		//		if(intent.getAction().equals(Intent.ACTION_GET_CONTENT))
		//		{
		//		
		//			mReturnIntent = true;
		//
		//		} 

		favuserlist=new ArrayList<FM_Bookmark_Data>();


		FM__ConstantData.sqlfm_bookmark.Read();


		favuserlist	=FM__ConstantData.sqlfm_bookmark.getAllBookmarkListData();

		FM__ConstantData.db.close();

		FM__Bookmark_list_Adapter adapter = new FM__Bookmark_list_Adapter(FM_bookmarkList.this, favuserlist);
		lstbookmark.setAdapter(adapter);

		lstbookmark.setOnItemClickListener(new OnItemClickListener() 
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub


				item_ext="."+favuserlist.get(position).getBookmark_file_type();

				if(item_ext!=null)
				{


					File file= new File(favuserlist.get(position).getBookmark_path());
					if (file.isDirectory()) 
					{
						if(file.canRead()) 
						{

							File_Manager_MainActivity.arrylist_next_folder= new ArrayList<String>();

							EventHandler.next_folder_index=0;
							File_Manager_MainActivity.mHandler.updateDirectory(File_Manager_MainActivity.mFileMag.setHomeDir(favuserlist.get(position).getBookmark_path()));
							finish();
							//						if(mPathLabel != null)
							//							mPathLabel.setText(mFileMang.getCurrentDir());

						} else
						{
							Toast.makeText(FM_bookmarkList.this, getResources().getString(R.string.Cannot_read_folder_due_to_permissions), 
									Toast.LENGTH_SHORT).show();
						}
					}

					/*video file selected--add more video formats*/
					else if(item_ext.equalsIgnoreCase(".m4v") || 
							item_ext.equalsIgnoreCase(".3gp") ||
							item_ext.equalsIgnoreCase(".wmv") || 
							item_ext.equalsIgnoreCase(".mp4") || 
							item_ext.equalsIgnoreCase(".ogg") ||
							item_ext.equalsIgnoreCase(".wav")) {


						String mimetypedata = getMimeType(file.getAbsolutePath());
						Log.e("mimetypedata", mimetypedata);
						if (file.exists()) {
							if(mReturnIntent) 
							{
								returnIntentResults(file);

							} else {
								Intent movieIntent = new Intent();
								movieIntent.setAction(android.content.Intent.ACTION_VIEW);
								movieIntent.setDataAndType(Uri.fromFile(file), "video/*");
								startActivity(movieIntent);
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

						if (file.exists()) {
							if(mReturnIntent) 
							{
								returnIntentResults(file);
							} else
							{
								Intent i = new Intent();
								i.setAction(android.content.Intent.ACTION_VIEW);
								i.setDataAndType(Uri.fromFile(file), "audio/*");
								startActivity(i);
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

							} else {



								Intent picIntent = new Intent(FM_bookmarkList.this,FM_Full_Image.class);
								//							Bundle bdata= new Bundle();
								//							bdata.putString("file_path", file.getAbsolutePath());
								//							picIntent.putExtras(bdata);
								picIntent.setData(Uri.fromFile(file));
								startActivity(picIntent);


								//							Intent picIntent = new Intent();
								//							picIntent.setAction(android.content.Intent.ACTION_VIEW);
								//							picIntent.setDataAndType(Uri.fromFile(file), "image/*");
								//							startActivity(picIntent);
							}
						}
					}

					/*video file selected--add more video formats*/
					else if(item_ext.equalsIgnoreCase(".m4v") || 
							item_ext.equalsIgnoreCase(".3gp") ||
							item_ext.equalsIgnoreCase(".wmv") || 
							item_ext.equalsIgnoreCase(".mp4") || 
							item_ext.equalsIgnoreCase(".ogg") ||
							item_ext.equalsIgnoreCase(".wav")) {

						if (file.exists()) {
							if(mReturnIntent) 
							{
								returnIntentResults(file);

							}
							else
							{
								Intent movieIntent = new Intent();
								movieIntent.setAction(android.content.Intent.ACTION_VIEW);
								movieIntent.setDataAndType(Uri.fromFile(file), "video/*");
								startActivity(movieIntent);
							}
						}
					}

					//					/*zip file */

					/*pdf file selected*/
					else if(item_ext.equalsIgnoreCase(".zip")) 
					{

						new ExtractManager(FM_bookmarkList.this).extract(file.getAbsoluteFile(), File_Manager_MainActivity.mFileMag.getCurrentDir());

					}


					//					else if(item_ext.equalsIgnoreCase(".zip")) {
					//
					//						if(mReturnIntent) {
					//							returnIntentResults(file);
					//
					//						} else {
					//							AlertDialog.Builder builder = new AlertDialog.Builder(FM_bookmarkList.this);
					//							AlertDialog alert;
					//							mZippedTarget = mFileMag.getCurrentDir() + "/" + item;
					//							CharSequence[] option = {"Extract here", "Extract to..."};
					//
					//							builder.setTitle("Extract");
					//							builder.setItems(option, new DialogInterface.OnClickListener() {
					//
					//								public void onClick(DialogInterface dialog, int which) {
					//									switch(which) {
					//									case 0:
					//										String dir = mFileMag.getCurrentDir();
					//										mHandler.unZipFile(item, dir + "/");
					//										break;
					//
					//									case 1:
					//										//										mDetailLabel.setText("Holding " + item + 
					//										//												" to extract");
					//										mHoldingZip = true;
					//										break;
					//									}
					//								}
					//							});
					//
					//							alert = builder.create();
					//							alert.show();
					//						}
					//					}

					//					/* gzip files, this will be implemented later */
					//					else if(item_ext.equalsIgnoreCase(".gzip") ||
					//							item_ext.equalsIgnoreCase(".gz")) {
					//
					//						if(mReturnIntent) {
					//							returnIntentResults(file);
					//
					//						} else {
					//							//TODO:
					//						}
					//					}

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
									Toast.makeText(FM_bookmarkList.this, getResources().getString(R.string.Sorry_could_not_find_a_pdf_viewer), 
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
								//							Intent intent = new Intent(FM_bookmarkList.this,FM_Doc_File_Open.class);
								//							Bundle bndldata= new Bundle();
								//							
								//							bndldata.putString("strfile_path", file.getAbsolutePath());
								//							intent.putExtras(bndldata);
								//							startActivity(intent);

								Intent pdfIntent = new Intent();
								pdfIntent.setAction(android.content.Intent.ACTION_VIEW);
								pdfIntent.setDataAndType(Uri.fromFile(file), 
										"application/msword");

								try {
									startActivity(pdfIntent);
								} catch (ActivityNotFoundException e) {
									Toast.makeText(FM_bookmarkList.this, getResources().getString(R.string.Sorry_could_not_find_a_Document_viewer), 
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
									Toast.makeText(FM_bookmarkList.this, getResources().getString(R.string.Sorry_could_not_find_a_Document_viewer), 
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
									Toast.makeText(FM_bookmarkList.this, getResources().getString(R.string.Sorry_could_not_find_a_Document_viewer),  
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
									Toast.makeText(FM_bookmarkList.this, getResources().getString(R.string.Sorry_could_not_find_a_Document_viewer),  
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


								//							Intent intent = new Intent(FM_bookmarkList.this,FM_Doc_File_Open.class);
								//							Bundle bndldata= new Bundle();
								//							
								//							bndldata.putString("strfile_path", file.getAbsolutePath());
								//							intent.putExtras(bndldata);
								//							startActivity(intent);

								Intent htmlIntent = new Intent();
								htmlIntent.setAction(android.content.Intent.ACTION_VIEW);
								htmlIntent.setDataAndType(Uri.fromFile(file), "text/html");

								try {
									startActivity(htmlIntent);
								} catch(ActivityNotFoundException e) {
									Toast.makeText(FM_bookmarkList.this, getResources().getString(R.string.Sorry_could_not_find_a_HTML_viewer), 
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

								Intent intent = new Intent(FM_bookmarkList.this,EditorActivity.class);
								intent.setData(Uri.fromFile(file));
								//startActivity(intent);

								//							Intent txtIntent = new Intent();
								//							txtIntent.setAction(android.content.Intent.ACTION_VIEW);
								//							txtIntent.setDataAndType(Uri.fromFile(file), "text/plain");

								try {
									startActivity(intent);
									//								startActivity(txtIntent);
								} catch(ActivityNotFoundException e) 
								{
									//								txtIntent.setType("text/*");
									//								startActivity(txtIntent);
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
									Toast.makeText(FM_bookmarkList.this, getResources().getString(R.string.Sorry_could_not_find_anything) +
											" " +getResources().getString(R.string.to_open)+" " + file.getName(), 
											Toast.LENGTH_SHORT).show();
								}
							}
						}
					}

				
				// FM_bookmarkList.mHandler.updateDirectory(FM_bookmarkList.mFileMag.getNextDir(favuserlist.get(position).getBookmark_path(), true));
				}
			}

		});

		lstbookmark.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub

				openDialog_for_bookmark(favuserlist.get(position).getBookmark_path(),favuserlist.get(position).getBookmark_name());

				return false;
			}

		});


	}
	private void returnIntentResults(File data) {
		mReturnIntent = false;

		Intent ret = new Intent();
		ret.setData(Uri.fromFile(data));
		setResult(RESULT_OK, ret);

		finish();
	}
	private Context getDialogContext() {
		Context context;
		if (getParent() != null) context = getParent();
		else context = this;
		return context;
	}
	protected void openDialog_for_bookmark(String strfilepath, String filename)
	{
		final String strfile=strfilepath;
		str_bookmark_name=filename;
		Log.e("str_bookmark_name", str_bookmark_name+"");
		try 
		{


			final CharSequence[] options= {getString(R.string.Edit_Bookmark_Name),getString(R.string.Remove_Bookmark)};

			AlertDialog.Builder alt_bld = new AlertDialog.Builder(getDialogContext());
			alt_bld.setIcon(R.drawable.icon);
			alt_bld.setTitle(getString(R.string.Select_option));
			alt_bld.setSingleChoiceItems(options, -1, new DialogInterface.OnClickListener() 
			{



				public void onClick(DialogInterface dialog, int item) 
				{
					dialog.dismiss();

					if(item==0)
					{


						AlertDialog.Builder alert_edit = new AlertDialog.Builder(getDialogContext());

						alert_edit.setTitle(getResources().getString(R.string.Edit_Bookmark_Name));
						alert_edit.setMessage(strfile);

						// Set an EditText view to get user input 
						final EditText input = new EditText(getDialogContext());
						input.setText(str_bookmark_name);
						alert_edit.setView(input);

						alert_edit.setPositiveButton(getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
							private Integer keyid;
							private String[] colsvalues;

							public void onClick(DialogInterface dialog, int whichButton) {

								str_bookmark_name = input.getText().toString();						
								//str_bookmark_name = input_editbox.getText().toString();

								str_bookmark_name=str_bookmark_name.trim();

								Log.e("temp", str_bookmark_name+"");

								if(str_bookmark_name==null)
								{
									Toast.makeText(FM_bookmarkList.this, getResources().getString(R.string.Please_enter_the_name), Toast.LENGTH_SHORT).show();
								}
								else if(str_bookmark_name.equalsIgnoreCase(""))
								{
									Toast.makeText(FM_bookmarkList.this, getResources().getString(R.string.Please_enter_the_name), Toast.LENGTH_SHORT).show();
								}
								else if(str_bookmark_name.equalsIgnoreCase(" "))
								{
									Toast.makeText(FM_bookmarkList.this, getResources().getString(R.string.Please_enter_the_name), Toast.LENGTH_SHORT).show();
								}
								else
								{
									favuserlist=new ArrayList<FM_Bookmark_Data>();


									FM__ConstantData.sqlfm_bookmark.Read();


									favuserlist	=FM__ConstantData.sqlfm_bookmark.getAllBookmarkListData();

									FM__ConstantData.db.close();

									if(!favuserlist.isEmpty())
									{
										for (int j = 0; j < favuserlist.size(); j++) 
										{

											if(favuserlist.get(j).getBookmark_path().equalsIgnoreCase(strfile))
											{

												bdata=true ;
												keyid=Integer.valueOf(favuserlist.get(j).getId());
												colsvalues= new String[]{String.valueOf(favuserlist.get(j).getId()),
														str_bookmark_name,
														favuserlist.get(j).getBookmark_path(),
														String.valueOf(favuserlist.get(j).isBookmark_flag())};

											}
										}
									}

									if(bdata)
									{
										String KEY_ID = "id";
										//colsvalues= new String[]{UserList.get(position).getId(),UserList.get(position).getUserName(),"NULL",UserList.get(position).getMysecret(),UserList.get(position).getPath()};
										FM__ConstantData.sqlfm_bookmark.Write();
										FM__ConstantData.sqlfm_bookmark.update(FM__ConstantData.Table_Bookmark, FM__ConstantData.fm_bookmarkcols,colsvalues,
												KEY_ID + " = ?",
												new String[] { String.valueOf(keyid) });
										FM__ConstantData.db.close();
										Log.e("bdata",""+bdata);

										Toast.makeText(FM_bookmarkList.this, getResources().getString(R.string.Bookmark_name_updated), Toast.LENGTH_SHORT).show();
									}
									else
									{
										FM__ConstantData.sqlfm_bookmark.Write();

										FM__ConstantData.db.execSQL("INSERT INTO "+FM__ConstantData.Table_Bookmark +" VALUES(NULL,'"+str_bookmark_name+"','"+strfile+"','"+false+"');");
										FM__ConstantData.db.close();
										Toast.makeText(FM_bookmarkList.this, getResources().getString(R.string.Bookmark_Added), Toast.LENGTH_SHORT).show();

									}


								}




								favuserlist=new ArrayList<FM_Bookmark_Data>();

								FM__ConstantData.sqlfm_bookmark.Read();


								favuserlist	=FM__ConstantData.sqlfm_bookmark.getAllBookmarkListData();

								FM__ConstantData.db.close();

								FM__Bookmark_list_Adapter adapter = new FM__Bookmark_list_Adapter(FM_bookmarkList.this, favuserlist);
								lstbookmark.setAdapter(adapter);


							}
						});

						alert_edit.setNegativeButton(getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								// Canceled.
							}
						});

						alert_edit.show();



					}
					else if(item==1)
					{

						AlertDialog alertDialog = new AlertDialog.Builder(getDialogContext()).create();
						alertDialog.setTitle(getResources().getString(R.string.Bookmark_Confimation));

						alertDialog.setMessage(getResources().getString(R.string.Are_you_sure_you_want_Bookmark));

						alertDialog.setButton3(getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
							private Integer keyid;
							private String[] colsvalues;

							public void onClick(DialogInterface dialog, int which) {

								favuserlist=new ArrayList<FM_Bookmark_Data>();


								FM__ConstantData.sqlfm_bookmark.Read();


								favuserlist	=FM__ConstantData.sqlfm_bookmark.getAllBookmarkListData();

								FM__ConstantData.db.close();

								if(!favuserlist.isEmpty())
								{
									for (int j = 0; j < favuserlist.size(); j++) 
									{

										if(favuserlist.get(j).getBookmark_path().equalsIgnoreCase(strfile))
										{

											bdata=true ;
											keyid=Integer.valueOf(favuserlist.get(j).getId());


										}
									}
								}

								if(bdata)
								{
									String KEY_ID = "id";
									//colsvalues= new String[]{UserList.get(position).getId(),UserList.get(position).getUserName(),"NULL",UserList.get(position).getMysecret(),UserList.get(position).getPath()};
									FM__ConstantData.sqlfm_bookmark.Write();
									FM__ConstantData.sqlfm_bookmark.delete(FM__ConstantData.Table_Bookmark,
											KEY_ID + " = ?",
											new String[] { String.valueOf(keyid) });
									FM__ConstantData.db.close();
									Log.e("bdata",""+bdata);

									Toast.makeText(FM_bookmarkList.this, getResources().getString(R.string.Remove_Bookmark_Successfully), Toast.LENGTH_SHORT).show();
								}



								favuserlist=new ArrayList<FM_Bookmark_Data>();


								FM__ConstantData.sqlfm_bookmark.Read();


								favuserlist	=FM__ConstantData.sqlfm_bookmark.getAllBookmarkListData();

								FM__ConstantData.db.close();

								FM__Bookmark_list_Adapter adapter = new FM__Bookmark_list_Adapter(FM_bookmarkList.this, favuserlist);
								lstbookmark.setAdapter(adapter);


							} }); 

						alertDialog.setButton2(getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {

								//here you can add functions
								//	SVR_Recording_Video_List_Option.this.finish();
							} }); 
						alertDialog.setIcon(R.drawable.icon);
						alertDialog.show();




					}




				}
			});
			AlertDialog alert = alt_bld.create();
			alert.show();
		}
		catch (Exception e) 
		{
			Log.e("Error playing file:- ",""+e.toString());
		}


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
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();

		finish();
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

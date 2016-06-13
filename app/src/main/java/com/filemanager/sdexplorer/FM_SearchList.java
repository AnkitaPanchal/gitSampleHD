package com.filemanager.sdexplorer;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.filemanager.sdexplorer.data.ExtractManager;
import com.filemanager.sdexplorer.data.FM__ConstantData;
import com.filemanager.sdexplorer.data.FM__Search_list_Adapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.File;
import java.security.MessageDigest;
import java.util.ArrayList;

public class FM_SearchList extends Activity
{
	private ImageButton imgbtnback;
	private TextView txtinfoname;
	private ArrayList<String> file_list;
	private CharSequence[] names;
	private String search_name;
	private ListView lstsearch;
	private boolean mReturnIntent = false;
	String item_ext="";
	private String deviceId_md5;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.fm_serchlist);

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

			//initialise Analytics
		 FM__ConstantData.initialiseAnalytics(FM__ConstantData.Google_Analytic_ID,this);
			//add Google Analytics view
			String str_name=this.getClass().getSimpleName();
			Log.e("str_name", str_name);
			FM__ConstantData.AnalyticsView("SearchList Activity");

		} catch (Exception e) {
			// TODO: handle exception

			Log.e("AnalyticsView", e.toString());

		}
	    
		Intent intdata=getIntent();

		Bundle bndle_data=intdata.getExtras();
		file_list=	bndle_data.getStringArrayList("file_list_search");
		names=bndle_data.getCharSequenceArray("file_names_search");
		search_name=	bndle_data.getString("Search_name");
		Log.e("search_name", search_name);


		for (int i = 0; i < file_list.size(); i++) 
		{

			Log.e("file"+i, file_list.get(i));
		}

		for (int i = 0; i < names.length; i++) 
		{

			Log.e("names"+i, names[i]+"");
		}
		lstsearch=(ListView)findViewById(R.id.lstsearch);
		imgbtnback=(ImageButton)findViewById(R.id.imgbtnback);	
		txtinfoname=(TextView)findViewById(R.id.txtinfoname);	
		txtinfoname.setText(getResources().getString(R.string.Search)+" : "+search_name);

		imgbtnback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				finish();
			}
		});
		FM__Search_list_Adapter adapter = new FM__Search_list_Adapter(FM_SearchList.this, file_list,names);
		lstsearch.setAdapter(adapter);

		lstsearch.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub

				File file= new File(file_list.get(position));

				if(file != null && file.isFile())
				{
					String str_filepath = file.toString();
					item_ext=str_filepath.substring(str_filepath.lastIndexOf("."), str_filepath.length());
					Log.e("item_ext", item_ext);
				}

				if (file.isDirectory()) 
				{
					if(file.canRead()) 
					{

						File_Manager_MainActivity.arrylist_next_folder= new ArrayList<String>();

						EventHandler.next_folder_index=0;
						File_Manager_MainActivity.mHandler.updateDirectory(File_Manager_MainActivity.mFileMag.setHomeDir(file_list.get(position)));
						finish();
						//						if(mPathLabel != null)
						//							mPathLabel.setText(mFileMang.getCurrentDir());

					} else
					{
						Toast.makeText(FM_SearchList.this, getResources().getString(R.string.Cannot_read_folder_due_to_permissions), 
								Toast.LENGTH_SHORT).show();
					}
				}
				else
				{

					/*video file selected--add more video formats*/
					if(item_ext.equalsIgnoreCase(".m4v") || 
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



								Intent picIntent = new Intent(FM_SearchList.this,FM_Full_Image.class);
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

						new ExtractManager(FM_SearchList.this).extract(file.getAbsoluteFile(), File_Manager_MainActivity.mFileMag.getCurrentDir());

					}


					//					else if(item_ext.equalsIgnoreCase(".zip")) {
					//
					//						if(mReturnIntent) {
					//							returnIntentResults(file);
					//
					//						} else {
					//							AlertDialog.Builder builder = new AlertDialog.Builder(FM_SearchList.this);
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
									Toast.makeText(FM_SearchList.this, getResources().getString(R.string.Sorry_could_not_find_a_pdf_viewer), 
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
								//							Intent intent = new Intent(FM_SearchList.this,FM_Doc_File_Open.class);
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
									Toast.makeText(FM_SearchList.this, getResources().getString(R.string.Sorry_could_not_find_a_Document_viewer), 
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
									Toast.makeText(FM_SearchList.this, getResources().getString(R.string.Sorry_could_not_find_a_Document_viewer), 
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
									Toast.makeText(FM_SearchList.this, getResources().getString(R.string.Sorry_could_not_find_a_Document_viewer),  
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
									Toast.makeText(FM_SearchList.this, getResources().getString(R.string.Sorry_could_not_find_a_Document_viewer),  
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


								//							Intent intent = new Intent(FM_SearchList.this,FM_Doc_File_Open.class);
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
									Toast.makeText(FM_SearchList.this, getResources().getString(R.string.Sorry_could_not_find_a_HTML_viewer), 
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

								Intent intent = new Intent(FM_SearchList.this,EditorActivity.class);
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
									Toast.makeText(FM_SearchList.this, getResources().getString(R.string.Sorry_could_not_find_anything) +
											" " +getResources().getString(R.string.to_open)+" " + file.getName(), 
											Toast.LENGTH_SHORT).show();
								}
							}
						}
					}


				}



				// FM_SearchList.mHandler.updateDirectory(FM_SearchList.mFileMag.getNextDir(favuserlist.get(position).getBookmark_path(), true));


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

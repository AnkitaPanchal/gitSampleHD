package com.filemanager.sdexplorer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.filemanager.sdexplorer.data.FM__ConstantData;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.MessageDigest;

public class EditorActivity extends Activity //implements OnItemSelectedListener 
{
	//	Spinner s1;
	private EditText box;
	private Dialog dialog2;
	private String FILENAME = "Untitled" ;
	private String string="";
	private String deviceId_md5;
	private boolean isFromEmail=false;

	// openFlag
	// openFlag=false (file not Opened with Intent)
	// openFlag=false (file Opened With Intent.)
	private boolean openFlag;
	private String openFilePath,PATH_NAME;
	//	String menu[] = { "Menu", "Save", "mail", "Quit", "Clear all", "new",
	//	"open" };

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editorpage);

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
			FM__ConstantData.AnalyticsView("Editor Activity");

		} catch (Exception e) {
			// TODO: handle exception

			Log.e("AnalyticsView", e.toString());

		}
	    
		//		s1 = (Spinner) findViewById(R.id.spinner1);
		box = (EditText) findViewById(R.id.editText1);

		
		Intent i = getIntent();
		PATH_NAME=i.getStringExtra("PATH_NAME");
		
		if(PATH_NAME!=null)
		{
			openFilePath=PATH_NAME;
			Log.e("openFilePath", openFilePath);
		}
		
		TextView txtheadingname=(TextView)findViewById(R.id.txtheadingname);
		if(openFilePath!=null)
		{

			txtheadingname.setText(FILENAME);
		}
		else
		{
			txtheadingname.setText("");
		}
		
		ImageButton imgbtninfo=(ImageButton)findViewById(R.id.imgbtninfo);
		imgbtninfo.setBackgroundResource(android.R.drawable.ic_menu_save);

		imgbtninfo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// TODO Auto-generated method stub


				string = box.getText().toString();
				if(openFlag)
				{
					File file= new File(openFilePath);
					try 
					{
						OutputStream os = new FileOutputStream(file);
						os.write(string.getBytes());
						os.close();
						MediaScannerConnection.scanFile(EditorActivity.this,
								new String[] { file.toString() }, null,
								new MediaScannerConnection.OnScanCompletedListener() {
							public void onScanCompleted(String path, Uri uri) 
							{
								Log.i("ExternalStorage", "Scanned " + path+ ":");
								Log.i("ExternalStorage", "-> uri=" + uri);
							}
						});

						if (file.exists()) 
						{
							Toast t = Toast.makeText(EditorActivity.this, getResources().getString(R.string.File_has_been_Saved),Toast.LENGTH_SHORT);
							t.show();
//							if(clearTextFlag)
//								box.setText(" ");
							finish();
						}
						else 
						{
							Toast t1 = Toast.makeText(EditorActivity.this, getResources().getString(R.string.Could_not_save_the_file),Toast.LENGTH_SHORT);
							t1.show();
						}
					}
					catch (IOException e) 
					{
						// Unable to create file, likely because external storage is
						// not currently mounted.
						Log.w("ExternalStorage", "Error writing " + file, e);
					}
				}
				else
				{
					calldialog2();	
				}
				
			
				//finish();
			
			}
		});
		
		
		//		s1.setAdapter(new ArrayAdapter<String>(this,
		//				android.R.layout.simple_spinner_dropdown_item, menu));
		//		s1.setOnItemSelectedListener(this);

		//		Start Intent to Open text file(.txt)
		
		
		
		if(i == null) return;
		Uri u = i.getData();
		if(u == null) return;
//		openFilePath = u.getEncodedPath();
		openFilePath = Uri.decode(u.getEncodedPath());
		if(openFilePath == null) return;

		try
		{
			openFlag=true;
			FILENAME=openFilePath.substring(openFilePath.lastIndexOf('/')+1);
			if(openFilePath.contains("@"))
			{
				openFlag=false;
				isFromEmail=true;
				openFilePath=Environment.getExternalStorageDirectory().getPath();
			}

			readFile(getContentResolver().openInputStream(u));
		}
		catch (Exception e) 
		{
			Log.e("File from uri.", e.toString());
			e.printStackTrace();
		}

		
		if(openFilePath!=null)
		{

			txtheadingname.setText(FILENAME);
		}
		else
		{
			txtheadingname.setText("");
		}

//		Button button_save=(Button)findViewById(R.id.button_save);
//
//		Button button_cancel=(Button)findViewById(R.id.button_cancel);
//
//		button_save.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//
//
//				string = box.getText().toString();
//				if(openFlag)
//				{
//					File file= new File(openFilePath);
//					try 
//					{
//						OutputStream os = new FileOutputStream(file);
//						os.write(string.getBytes());
//						os.close();
//						MediaScannerConnection.scanFile(EditorActivity.this,
//								new String[] { file.toString() }, null,
//								new MediaScannerConnection.OnScanCompletedListener() {
//							public void onScanCompleted(String path, Uri uri) 
//							{
//								Log.i("ExternalStorage", "Scanned " + path+ ":");
//								Log.i("ExternalStorage", "-> uri=" + uri);
//							}
//						});
//
//						if (file.exists()) 
//						{
//							Toast t = Toast.makeText(EditorActivity.this, getResources().getString(R.string.File_has_been_Saved),Toast.LENGTH_SHORT);
//							t.show();
////							if(clearTextFlag)
////								box.setText(" ");
//							finish();
//						}
//						else 
//						{
//							Toast t1 = Toast.makeText(EditorActivity.this, getResources().getString(R.string.Could_not_save_the_file),Toast.LENGTH_SHORT);
//							t1.show();
//						}
//					}
//					catch (IOException e) 
//					{
//						// Unable to create file, likely because external storage is
//						// not currently mounted.
//						Log.w("ExternalStorage", "Error writing " + file, e);
//					}
//				}
//				else
//				{
//					calldialog2();	
//				}
//				
//			
//				finish();
//			}
//		});
//
//		button_cancel.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//
//				finish();
//			}
//		});

	}




	//  # Added By #Hims
	//	Flag to Show Clear Text After File Save. 
	public void checkandsave() 
	{
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;

		File file=null;
		if(openFlag)
		{
			file= new File(openFilePath, FILENAME+".txt");
			try 
			{
				OutputStream os = new FileOutputStream(file);
				os.write(string.getBytes());
				os.close();
				MediaScannerConnection.scanFile(this,
						new String[] { file.toString() }, null,
						new MediaScannerConnection.OnScanCompletedListener() {
					public void onScanCompleted(String path, Uri uri) 
					{
						Log.i("ExternalStorage", "Scanned " + path+ ":");
						Log.i("ExternalStorage", "-> uri=" + uri);
					}
				});

				if (file.exists()) 
				{


					Toast t = Toast.makeText(this, getResources().getString(R.string.File_has_been_Saved),Toast.LENGTH_SHORT);
					t.show();
					//					if(clearTextFlag)
					//						box.setText(" ");
					dialog2.cancel();
					finish();
				}
				else 
				{
					Toast t1 = Toast.makeText(this, getResources().getString(R.string.Could_not_save_the_file),Toast.LENGTH_SHORT);
					t1.show();
				}
			}
			catch (IOException e) 
			{
				// Unable to create file, likely because external storage is
				// not currently mounted.
				Log.w("ExternalStorage", "Error writing " + file, e);
			}
		}
		else
		{
			String state = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(state)) 
			{
				// We can read and write the media
				mExternalStorageAvailable = mExternalStorageWriteable = true;
			}
			else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) 
			{
				// We can only read the media
				mExternalStorageAvailable = true;
				mExternalStorageWriteable = false;
			} 
			else 
			{
				mExternalStorageAvailable = mExternalStorageWriteable = false;
			}

//			if (mExternalStorageAvailable == mExternalStorageWriteable && mExternalStorageAvailable == true) 
//			{
//				File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//				// Make sure the Target directory exists.
//				path.mkdirs();
//
//				file = new File(path, FILENAME+".txt");
//			}
			file = new File(openFilePath, FILENAME+".txt");
		}

		try 
		{
			if(file!=null)
			{
				OutputStream os = new FileOutputStream(file);
				os.write(string.getBytes());
				os.close();
				MediaScannerConnection.scanFile(this,
						new String[] { file.toString() }, null,
						new MediaScannerConnection.OnScanCompletedListener() {
					public void onScanCompleted(String path, Uri uri) 
					{
						Log.i("ExternalStorage", "Scanned " + path
								+ ":");
						Log.i("ExternalStorage", "-> uri=" + uri);
					}
				});

				if (file.exists()) 
				{
					Toast t = Toast.makeText(this, getResources().getString(R.string.File_has_been_Saved),Toast.LENGTH_SHORT);
					t.show();
					//					if(clearTextFlag)
					//						box.setText(" ");
					dialog2.cancel();
					finish();
				}
				else 
				{
					Toast t1 = Toast.makeText(this, getResources().getString(R.string.Could_not_save_the_file),Toast.LENGTH_SHORT);
					t1.show();
				}
			}
		}
		catch (IOException e) 
		{
			// Unable to create file, likely because external storage is
			// not currently mounted.
			Log.w("ExternalStorage", "Error writing " + file, e);
		}
	}

	public void checkandopen()
	{
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();
		String content="";
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else {

			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
		if (mExternalStorageAvailable == mExternalStorageWriteable && mExternalStorageAvailable == true) 
		{
			File path = Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
			File file = new File(path, FILENAME+".txt");
			if(file.exists()) try {
				InputStream istream = new FileInputStream(file);
				InputStreamReader inputreader = new InputStreamReader(istream);
				BufferedReader bufferedreader = new BufferedReader(inputreader);
				String line;
				while((line = bufferedreader.readLine()) != null) {
					content += line + "\n";
				}
				istream.close();

			} catch (IOException e) {

				e.printStackTrace();

			}
		} 
		else 
		{

		}
		box.setText(content);
	}

	//	@Override
	//	public boolean onCreateOptionsMenu(Menu menu) {
	//		// Inflate the menu; this adds items to the action bar if it is present.
	//		getMenuInflater().inflate(R.menu.editorpage, menu);
	//		return true;
	//	}

	//	public void calldialog1() 
	//	{
	//		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	//		LayoutInflater inflater = this.getLayoutInflater();
	//		builder.setView(inflater.inflate(R.layout.dialog_layout, null))
	//		.setPositiveButton("Save",
	//				new DialogInterface.OnClickListener() {
	//
	//			@Override
	//			public void onClick(DialogInterface dialog,
	//					int which) {
	//				// TODO Auto-generated method stub
	//
	//				//Clear Text Flag set to True.
	//				calldialog2(true);
	//			}
	//		})
	//		.setNeutralButton("Dont save",
	//				new DialogInterface.OnClickListener() {
	//
	//			@Override
	//			public void onClick(DialogInterface dialog,
	//					int which) {
	//				dialog.cancel();
	//				box.setText(" ");
	//			}
	//		})
	//		.setNegativeButton("Cancel",
	//				new DialogInterface.OnClickListener() {
	//
	//			@Override
	//			public void onClick(DialogInterface dialog,
	//					int which) {
	//				// TODO Auto-generated method stub
	//				dialog.cancel();
	//			}
	//		}); 
	//		AlertDialog dialog = builder.create();
	//		dialog.show();
	//	}

	//  # Added By #Hims
	//	Flag to Show Clear Text After File Save. 
	public void calldialog2()
	{
		dialog2= new Dialog(this);
		dialog2.setContentView(R.layout.text_editor_dialog2);

		final EditText namebox = (EditText) dialog2.findViewById(R.id.filename);
		final Button confirm = (Button) dialog2.findViewById(R.id.b1);
		final Button Cancel = (Button) dialog2.findViewById(R.id.b2);
		confirm.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub

				if(isFromEmail)
				{
					Toast.makeText(EditorActivity.this,"File Path:"+openFilePath,Toast.LENGTH_LONG).show();
				}

				FILENAME = namebox.getText().toString();
				if(FILENAME.equals(""))
				{
					FILENAME = "Untitled";
				}
				checkandsave();
			}
		});
		Cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog2.cancel();
			}
		});
		dialog2.setTitle("Save");
		dialog2.show();

	}
	public void calldialog3()
	{
		final Dialog dialog2= new Dialog(this);
		dialog2.setContentView(R.layout.text_editor_dialog2);


		final EditText namebox = (EditText) dialog2.findViewById(R.id.filename);
		final Button confirm = (Button) dialog2.findViewById(R.id.b1);
		final Button Cancel = (Button) dialog2.findViewById(R.id.b2);
		confirm.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FILENAME = namebox.getText().toString();
				if(FILENAME.equals(""))
				{
					FILENAME = "Untitled";
				}
				checkandsave();
			}
		});
		Cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog2.cancel();

			}
		});
		dialog2.setTitle("Save");
		dialog2.show();

		finish();
	}

	public void calldialog4()
	{	
		checkandopen();
		final Dialog dialog2= new Dialog(this);
		dialog2.setContentView(R.layout.text_editor_dialog2);

		final EditText namebox = (EditText) dialog2.findViewById(R.id.filename);
		final Button confirm = (Button) dialog2.findViewById(R.id.b1);
		final Button Cancel = (Button) dialog2.findViewById(R.id.b2);
		confirm.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FILENAME = namebox.getText().toString();

				checkandopen();

			}
		});
		Cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog2.cancel();

			}
		});
		dialog2.setTitle("Open");
		dialog2.show();
	}


	//	@Override
	//	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
	//			long arg3) 
	//	{
	//		int pos = s1.getSelectedItemPosition();
	//		// TODO Auto-generated method stub
	//		switch (pos) {
	//		case 0:
	//			break;
	//		case 1:
	//			string = box.getText().toString();
	//			calldialog2();
	//			break;
	//		case 2:
	//			break;
	//		case 3:
	//			calldialog3();
	//			break;
	//		case 4:
	//			box.setText(" ");
	//			break;
	//		case 5:
	//			calldialog1();
	//
	//		box.setText(" ");
	//		break;
	//		case 6:calldialog4();
	//
	//		break;
	//		}
	//	}

	//	@Override
	//	public void onNothingSelected(AdapterView<?> arg0) {
	//		// TODO Auto-generated method stub
	//
	//		// TODO Auto-generated method stub
	//
	//	}

	private void readFile(InputStream inputStream) 
	{
		try 
		{
			if(inputStream!=null)
			{
				String result="";

				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				int len = -1;
				byte[] buff = new byte[1024];
				while ((len = inputStream.read(buff)) != -1) 
				{
					outputStream.write(buff, 0, len);
				}
				byte[] bs = outputStream.toByteArray();
				result = new String(bs);

				box.setText(result);

				//				result = new String(bs,"UTF-8");
				//				Log.e("Result:- ", result);
			}
		}

		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
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

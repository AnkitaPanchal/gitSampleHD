package com.filemanager.sdexplorer;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.filemanager.sdexplorer.data.CompressManager;
import com.filemanager.sdexplorer.data.ExtractManager;
import com.filemanager.sdexplorer.data.FM__ConstantData;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FM_FileOperations_List extends Activity{

	private int width;
	private int height;
	private LinearLayout llmain,llextract,llbookmark,lldetails,llrename;
	private LinearLayout llshare,lldelete,llcopy,llmove;
	private final int KB = 1024;
	private final int MG = KB * KB;
	private final int GB = MG * KB;   
	private String srt_file_path,srt_file_name,srt_file_type,srt_file_mime_type;
	private Bundle bundle_data;
	private Intent intdata;
	private ImageView imgmain,imgextract;
	private TextView txtmain,txtextract;
	protected Dialog dialog_box;
	protected ConnectivityManager connMgr_fm_operation_list;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.fileoperations);

		connMgr_fm_operation_list = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
		intdata=getIntent();
		bundle_data= intdata.getExtras();

		srt_file_path= bundle_data.getString("srt_file_path");
		srt_file_name= bundle_data.getString("srt_file_name");
		srt_file_type= bundle_data.getString("srt_file_type");
		srt_file_mime_type= bundle_data.getString("srt_file_mime_type");
		if(srt_file_name==null)
		{
			srt_file_name="";
		}
//		Log.e("srt_file_type", srt_file_type);
//		Log.e("srt_file_path", srt_file_path);
		
		llmain=(LinearLayout)findViewById(R.id.llmain);
		llextract=(LinearLayout)findViewById(R.id.llextract);
		llbookmark=(LinearLayout)findViewById(R.id.llbookmark);
		lldetails=(LinearLayout)findViewById(R.id.lldetails);
		llrename=(LinearLayout)findViewById(R.id.llrename);
		lldelete=(LinearLayout)findViewById(R.id.lldelete);
		llcopy=(LinearLayout)findViewById(R.id.llcopy);
		llmove=(LinearLayout)findViewById(R.id.llmove);
		llshare=(LinearLayout)findViewById(R.id.llshare);
		
		imgextract=(ImageView)findViewById(R.id.imgextract);
		imgmain=(ImageView)findViewById(R.id.imgmain);
		txtmain=(TextView)findViewById(R.id.txtmain);
		txtextract=(TextView)findViewById(R.id.txtextract);

		if(srt_file_type.equalsIgnoreCase("Directory"))
		{
			imgmain.setImageResource(R.drawable.folder);
			txtextract.setText(getResources().getString(R.string.Compress_here));
			imgextract.setImageResource(R.drawable.compress);
			llshare.setVisibility(LinearLayout.GONE);
		}
		else if(srt_file_type.equalsIgnoreCase("zip"))
		{
			imgmain.setImageResource(R.drawable.zip);
		}
		else
		{
			imgmain.setImageResource(R.drawable.file);
			txtextract.setText(getResources().getString(R.string.Compress_here));
			imgextract.setImageResource(R.drawable.compress);
		}

		if(srt_file_path!=null)
		{
			txtmain.setText(srt_file_path);
		}

		llshare.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			
				if (connMgr_fm_operation_list.getActiveNetworkInfo() != null && connMgr_fm_operation_list.getActiveNetworkInfo().isAvailable() && connMgr_fm_operation_list.getActiveNetworkInfo().isConnected()) 
				{

				String	srt_file_path_share = srt_file_path.replaceAll(" ", "%20");
				
					Intent sendIntent = new Intent(Intent.ACTION_SEND);
					//Mime type of the attachment (or) u can use sendIntent.setType("*/*")
//					sendIntent.setType("*/*");
					sendIntent.setType(srt_file_mime_type);
										
//					sendIntent.putExtra(Intent.EXTRA_EMAIL,  "" );
					sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					sendIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name) +" "+ srt_file_name);
					sendIntent.putExtra(Intent.EXTRA_TEXT,   ""+srt_file_name);
					//Subject for the message or Email
					//			sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.Email_Subject));
					//Full Path to the attachment
					//sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:/"+srt_file_path));
					sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(srt_file_path)));
					//Use a chooser to decide whether email or mms

					try
					{

						FM_FileOperations_List.this.startActivity(Intent.createChooser(sendIntent, "Send email..."));

					}
					catch (android.content.ActivityNotFoundException ex) 
					{
						Toast.makeText(FM_FileOperations_List.this, getResources().getString(R.string.There_are_no_email_clients_installed_), Toast.LENGTH_SHORT).show();
					}

				}
				else
				{
					Toast.makeText(FM_FileOperations_List.this, getResources().getString(R.string.No_Internet_Connection), Toast.LENGTH_SHORT).show();
				}
				
				finish();
			}
		});
		
		llmove.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				File_Manager_MainActivity.mHoldingFile = true;
				File_Manager_MainActivity.mHandler.setDeleteAfterCopy(true);
				FM__ConstantData.srt_file_path_for_copy=srt_file_path;
				FM__ConstantData.srt_file_name_for_copy=srt_file_name;
				FM__ConstantData.srt_file_type_for_copy=srt_file_type;
				finish();
			}
		});
		llcopy.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				File_Manager_MainActivity.mHoldingFile = true;
				
				File_Manager_MainActivity.mHandler.setDeleteAfterCopy(false);
				FM__ConstantData.srt_file_path_for_copy=srt_file_path;
				FM__ConstantData.srt_file_name_for_copy=srt_file_name;
				FM__ConstantData.srt_file_type_for_copy=srt_file_type;
				finish();
			}
		});
		lldelete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				final File file = new File(srt_file_path);
				finish();
				
				if(file.canRead() && file.canWrite())
				{
					
					
					
					
					AlertDialog.Builder alert_edit = new AlertDialog.Builder(File_Manager_MainActivity.mainacActivity);

					alert_edit.setTitle(getResources().getString(R.string.Delete_Confimation));

					alert_edit.setMessage(getResources().getString(R.string.Are_you_sure_you_want_delete)+"  "+srt_file_name);
				
					alert_edit.setIcon(R.drawable.delete);
					// Set an EditText view to get user input 
					

					alert_edit.setPositiveButton(getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
						private Integer keyid;
						private String[] colsvalues;
					

						public void onClick(DialogInterface dialog, int whichButton) {
							
							
									
							//str_bookmark_name = input_editbox.getText().toString();
							
							
								if(srt_file_type.equalsIgnoreCase("Directory"))
								{
									File_Manager_MainActivity.mHandler.deleteFile(srt_file_path);
									Toast.makeText(File_Manager_MainActivity.mainacActivity, getResources().getString(R.string.Deleted_Successfully), Toast.LENGTH_SHORT).show();
								}
								else
								{
									
									File_Manager_MainActivity.mHandler.deleteFile(srt_file_path);
									Toast.makeText(File_Manager_MainActivity.mainacActivity, getResources().getString(R.string.Deleted_Successfully), Toast.LENGTH_SHORT).show();
								}
								
							
							 File_Manager_MainActivity.mHandler.updateDirectory(File_Manager_MainActivity.mFileMag.getrefresh(File_Manager_MainActivity.mFileMag.getCurrentDir())) ;
							
						}
					});

					alert_edit.setNegativeButton(getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							// Canceled.
						}
					});

					alert_edit.show();
				}
				else
				{
					
				}
				
				
			}
		});

//		llmain.setLayoutParams(new LinearLayout.LayoutParams(
//				FM__ConstantData.screenwidth,   // *** this param has no effect, regardless of the value I set here ***
//				LinearLayout.LayoutParams.FILL_PARENT
//				));

		llrename.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				final File file = new File(srt_file_path);
				finish();
				
				if(file.canRead() && file.canWrite())
				{
					
					final String srt_file_path_new=srt_file_path.substring(0,srt_file_path.lastIndexOf("/"));
					Log.e("srt_file_path_new", srt_file_path_new);
					
					
					
					
					AlertDialog.Builder alert_edit = new AlertDialog.Builder(File_Manager_MainActivity.mainacActivity);

					alert_edit.setTitle(getResources().getString(R.string.Rename));
					alert_edit.setMessage(getResources().getString(R.string.New_Name));
					alert_edit.setIcon(R.drawable.rename);
					// Set an EditText view to get user input 
					final EditText input = new EditText(getDialogContext());
					input.setText(srt_file_name);
					alert_edit.setView(input);

					alert_edit.setPositiveButton(getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
						private Integer keyid;
						private String[] colsvalues;
					

						public void onClick(DialogInterface dialog, int whichButton) {
							
							
							srt_file_name = input.getText().toString();						
							//str_bookmark_name = input_editbox.getText().toString();
							
							srt_file_name=srt_file_name.trim();
							
							Log.e("temp", srt_file_name+"");
							
							if(srt_file_name==null)
							{
								Toast.makeText(File_Manager_MainActivity.mainacActivity, getResources().getString(R.string.Please_enter_the_name), Toast.LENGTH_SHORT).show();
							}
							else if(srt_file_name.equalsIgnoreCase(""))
							{
								Toast.makeText(File_Manager_MainActivity.mainacActivity, getResources().getString(R.string.Please_enter_the_name), Toast.LENGTH_SHORT).show();
							}
							else if(srt_file_name.equalsIgnoreCase(" "))
							{
								Toast.makeText(File_Manager_MainActivity.mainacActivity, getResources().getString(R.string.Please_enter_the_name), Toast.LENGTH_SHORT).show();
							}
							else
							{
								if(srt_file_type.equalsIgnoreCase("Directory"))
								{
									File new_file = new File(srt_file_path_new,srt_file_name);
									file.renameTo(new_file);
								}
								else
								{
									File new_file = new File(srt_file_path_new,srt_file_name);
									file.renameTo(new_file);
								}
								
							}
							 File_Manager_MainActivity.mHandler.updateDirectory(File_Manager_MainActivity.mFileMag.getrefresh(File_Manager_MainActivity.mFileMag.getCurrentDir())) ;
							
						}
					});

					alert_edit.setNegativeButton(getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							// Canceled.
						}
					});

					alert_edit.show();
				}
				else
				{
					
				}
				
				
				
			}
			
		});
		
		lldetails.setOnClickListener(new OnClickListener() {

			private String display_size;

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				finish();
				
				dialog_box = new Dialog(File_Manager_MainActivity.mainacActivity);
				dialog_box.setContentView(R.layout.file_details);
				dialog_box.setTitle(R.string.Details_Folder);
				//dialog_box.setCancelable(false);

				ImageView imgmain = (ImageView)dialog_box.findViewById(R.id.imgmain);
				imgmain.setImageResource(R.drawable.search);

				if(srt_file_type.equalsIgnoreCase("Directory"))
				{
					imgmain.setImageResource(R.drawable.folder);
					
				}
				else if(srt_file_type.equalsIgnoreCase("zip"))
				{
					imgmain.setImageResource(R.drawable.zip);
				}
				else
				{
					imgmain.setImageResource(R.drawable.file);
					
				}
				TextView txtmain = (TextView)dialog_box.findViewById(R.id.txtmain);
				txtmain.setText(srt_file_name);
				
				TextView    txtname= (TextView)dialog_box.findViewById(R.id.txtname);
				TextView	txtpath= (TextView)dialog_box.findViewById(R.id.txtpath);
				TextView	txtsize = (TextView)dialog_box.findViewById(R.id.txtsize);
				
				TextView	txtitems = (TextView)dialog_box.findViewById(R.id.txtitems);
				TextView	txtpermission = (TextView)dialog_box.findViewById(R.id.txtpermission);
				TextView	txtmodified= (TextView)dialog_box.findViewById(R.id.txtmodified);

				File file = new File(srt_file_path);
				int num_items = 0;
	    		String[] list = file.list();
	    		
	    		if(list != null)
	    			num_items = list.length;
	    		
	    		String dateString = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a").format(new Date(file.lastModified()));
	    		
				txtmodified.setText(dateString+"");
				txtname.setText(file.getName());
				
				txtpath.setText(file.getAbsolutePath());
				txtitems.setText(num_items+"");
				
				
				String permission = getFilePermissions(file);
				
				txtpermission.setText(permission);
				if(file.isFile())
	    		{
	    			double size = file.length();
	        		if (size > GB)
	    				display_size = String.format("%.2f Gb ", (double)size / GB);
	    			else if (size < GB && size > MG)
	    				display_size = String.format("%.2f Mb ", (double)size / MG);
	    			else if (size < MG && size > KB)
	    				display_size = String.format("%.2f Kb ", (double)size/ KB);
	    			else
	    				display_size = String.format("%.2f bytes ", (double)size);
	        		
	        		if(file.isHidden())
	        		{
	        			txtsize.setText(R.string.hidden+" | " + display_size +" | "+ permission);
	        		}
	        			
	        		else
	        		{
	        			txtsize.setText(display_size +" | "+ permission);
	        		}
	        			
	        		
	    		} else {
	    			
	    			if(file.isHidden())
	    				txtsize.setText("(hidden) | " + num_items + " "+R.string.items+" | " + permission);
	    			else
	    				txtsize.setText(num_items + " "+R.string.items+" | " + permission);
	    		}
				
				Button btnok = (Button)dialog_box.findViewById(R.id.btnok);
				

				btnok.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						


						dialog_box.dismiss();
					}
				});

				
				dialog_box.show();

			}
		});
		
		llextract.setOnClickListener(new OnClickListener() 
		{

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				finish();

				if(srt_file_type.equalsIgnoreCase("zip"))
				{
					File file = new File(srt_file_path);

					try {
						new ExtractManager(File_Manager_MainActivity.mainacActivity,true).extract(file.getAbsoluteFile(), File_Manager_MainActivity.mFileMag.getCurrentDir());
					} catch (Exception e) {
						// TODO: handle exception
						Toast.makeText(File_Manager_MainActivity.mainacActivity,
								R.string.extracting_error, Toast.LENGTH_SHORT).show();
					}
				}
				else
				{
					File file = new File(srt_file_path);

					try {

						new CompressManager(File_Manager_MainActivity.mainacActivity,true)
						.compress(file.getAbsoluteFile(),
								file.getName() + ".zip");

					} catch (Exception e) {
						// TODO: handle exception
						Toast.makeText(File_Manager_MainActivity.mainacActivity,
								R.string.compressing_error, Toast.LENGTH_SHORT).show();
					}

				}

				//


			}
		});

		llbookmark.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub



				Intent intdata= new Intent(FM_FileOperations_List.this, FM_FileOperations.class);
				bundle_data.putString("operations", "bookmark");
				bundle_data.putString("srt_file_type",srt_file_type);
				intdata.putExtras(bundle_data);
				startActivity(intdata);
				finish();

			}


		});
	}
	public  Context getDialogContext() {
		Context context;
		if (getParent() != null) context = getParent();
		else context = this;
		return context;
	}
	public String getFilePermissions(File file) {
		String per = "-";
	    		
		if(file.isDirectory())
			per += "d";
		if(file.canRead())
			per += "r";
		if(file.canWrite())
			per += "w";
		
		return per;
	}
}

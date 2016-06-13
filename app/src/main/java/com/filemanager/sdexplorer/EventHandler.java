/*
    Open Manager, an open source file manager for the Android system
    Copyright (C) 2009, 2010, 2011  Joe Berria <nexesdevelopment@gmail.com>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.filemanager.sdexplorer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.filemanager.sdexplorer.data.DrawableThreadLoader;
import com.filemanager.sdexplorer.data.FM_Bookmark_Data;
import com.filemanager.sdexplorer.data.FM_Video_BitmapManager;
import com.filemanager.sdexplorer.data.FM__ConstantData;
import com.filemanager.sdexplorer.data.ImageThreadLoader;

/**
 * This class sits between the Main activity and the FileManager class.
 * To keep the FileManager class modular, this class exists to handle
 * UI events and communicate that information to the FileManger class
 *
 * This class is responsible for the buttons onClick method. If one needs
 * to change the functionality of the buttons found from the Main activity
 * or add button logic, this is the class that will need to be edited.
 *
 * This class is responsible for handling the information that is displayed
 * from the list view (the files and folder) with a a nested class TableRow.
 * The TableRow class is responsible for displaying which icon is shown for each
 * entry. For example a folder will display the folder icon, a Word doc will
 * display a word icon and so on. If more icons are to be added, the TableRow
 * class must be updated to display those changes.
 *
 * @author Joe Berria
 */
public class EventHandler implements OnClickListener {
	/*
	 * Unique types to control which file operation gets
	 * performed in the background
	 */
	private static final int SEARCH_TYPE =		0x00;
	private static final int COPY_TYPE =		0x01;
	private static final int UNZIP_TYPE =		0x02;
	private static final int UNZIPTO_TYPE =		0x03;
	private static final int ZIP_TYPE =			0x04;
	private static final int DELETE_TYPE = 		0x05;
	private static final int MANAGE_DIALOG =	 0x06;

	private final Context mContext;
	private final Activity myacActivity;
	private final FileManager mFileMang;
	//private ThumbnailCreator mThumbnail;
	private TableRow mDelegate;
	public static int next_folder_index=0;
	public static boolean flag_next_folder= false;
	public boolean multi_select_flag = false;
	private boolean delete_after_copy = false;
	private boolean thumbnail_flag = true;
	private int mColor = Color.WHITE;

	//the list used to feed info into the array adapter and when multi-select is on
	private ArrayList<String> mDataSource, mMultiSelectData;
	private TextView mPathLabel;
	private TextView mInfoLabel;
	ImageThreadLoader imageLoader;
	DrawableThreadLoader drawableLoader;
	Dialog dialog_box;
	public static String destination_path_file=null;
	private ArrayList<FM_Bookmark_Data> favuserlist;
	private ConnectivityManager connMgr_info;
	/**
	 * Creates an EventHandler object. This object is used to communicate
	 * most work from the Main activity to the FileManager class.
	 *
	 * @param context	The context of the main activity e.g  Main
	 * @param manager	The FileManager object that was instantiated from Main
	 */
	public EventHandler(Activity context, final FileManager manager) {
		mContext = context;
		myacActivity=context;
		mFileMang = manager;
		imageLoader = new ImageThreadLoader(context);
		drawableLoader = new DrawableThreadLoader(context);
		mDataSource = new ArrayList<String>(mFileMang.setHomeDir
				(Environment.getExternalStorageDirectory().getPath()));

		connMgr_info = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
	}

	/**
	 * This constructor is called if the user has changed the screen orientation
	 * and does not want the directory to be reset to home.
	 *
	 * @param context	The context of the main activity e.g  Main
	 * @param manager	The FileManager object that was instantiated from Main
	 * @param location	The first directory to display to the user
	 */
	public EventHandler(Activity context, final FileManager manager, String location) {
		mContext = context;
		myacActivity=context;
		mFileMang = manager;
		imageLoader = new ImageThreadLoader(context);
		mDataSource = new ArrayList<String>(mFileMang.getNextDir(location, true));
		connMgr_info = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
	}

	/**
	 * This method is called from the Main activity and this has the same
	 * reference to the same object so when changes are made here or there
	 * they will display in the same way.
	 *
	 * @param adapter	The TableRow object
	 */
	public void setListAdapter(TableRow adapter) {
		mDelegate = adapter;
	}

	/**
	 * This method is called from the Main activity and is passed
	 * the TextView that should be updated as the directory changes
	 * so the user knows which folder they are in.
	 *
	 * @param path	The label to update as the directory changes
	 * @param label	the label to update information
	 */
	public void setUpdateLabels(TextView path) {
		mPathLabel = path;

	}
	public void setUpdateLabels(TextView path, TextView label) {
		mPathLabel = path;
		mInfoLabel = label;
	}

	/**
	 *
	 * @param color
	 */
	public void setTextColor(int color) {
		mColor = color;
	}

	/**
	 * Set this true and thumbnails will be used as the icon for image files. False will
	 * show a default image.
	 *
	 * @param show
	 */
	public void setShowThumbnails(boolean show) {
		thumbnail_flag = show;
	}

	/**
	 * If you want to move a file (cut/paste) and not just copy/paste use this method to
	 * tell the file manager to delete the old reference of the file.
	 *
	 * @param delete true if you want to move a file, false to copy the file
	 */
	public void setDeleteAfterCopy(boolean delete) {
		delete_after_copy = delete;
	}

	/**
	 * Indicates whether the user wants to select
	 * multiple files or folders at a time.
	 * <br><br>
	 * false by default
	 *
	 * @return	true if the user has turned on multi selection
	 */
	public boolean isMultiSelected() {
		return multi_select_flag;
	}

	/**
	 * Use this method to determine if the user has selected multiple files/folders
	 *
	 * @return	returns true if the user is holding multiple objects (multi-select)
	 */
	public boolean hasMultiSelectData() {
		return (mMultiSelectData != null && mMultiSelectData.size() > 0);
	}

	/**
	 * Will search for a file then display all files with the
	 * search parameter in its name
	 *
	 * @param name	the name to search for
	 */
	public void searchForFile(String name) {
		new BackgroundWork(SEARCH_TYPE).execute(name);
	}

	/**
	 * Will delete the file name that is passed on a background
	 * thread.
	 *
	 * @param name
	 */
	public void deleteFile(String name) {
		new BackgroundWork(DELETE_TYPE).execute(name);
	}

	/**
	 * Will copy a file or folder to another location.
	 *
	 * @param oldLocation	from location
	 * @param newLocation	to location
	 * @param temp
	 */
	public void copyFile(String oldLocation, String newLocation) {
		String[] data = {oldLocation, newLocation};

		new BackgroundWork(COPY_TYPE).execute(data);
	}

	/**
	 *
	 * @param newLocation
	 */
	public void copyFileMultiSelect(String newLocation) {
		String[] data;
		int index = 1;

		if (mMultiSelectData.size() > 0) {
			data = new String[mMultiSelectData.size() + 1];
			data[0] = newLocation;

			for(String s : mMultiSelectData)
				data[index++] = s;

			new BackgroundWork(COPY_TYPE).execute(data);
		}
	}

	/**
	 * This will extract a zip file to the same directory.
	 *
	 * @param file	the zip file name
	 * @param path	the path were the zip file will be extracted (the current directory)
	 */
	public void unZipFile(String file, String path) {
		new BackgroundWork(UNZIP_TYPE).execute(file, path);
	}

	/**
	 * This method will take a zip file and extract it to another
	 * location
	 *
	 * @param name		the name of the of the new file (the dir name is used)
	 * @param newDir	the dir where to extract to
	 * @param oldDir	the dir where the zip file is
	 */
	public void unZipFileToDir(String name, String newDir, String oldDir) {
		new BackgroundWork(UNZIPTO_TYPE).execute(name, newDir, oldDir);
	}

	/**
	 * Creates a zip file
	 *
	 * @param zipPath	the path to the directory you want to zip
	 */
	public void zipFile(String zipPath) {
		new BackgroundWork(ZIP_TYPE).execute(zipPath);
	}

	/**
	 * this will stop our background thread that creates thumbnail icons
	 * if the thread is running. this should be stopped when ever
	 * we leave the folder the image files are in.
	 */
	//	public void stopThumbnailThread() {
	//		if (mThumbnail != null) {
	//			mThumbnail.setCancelThumbnails(true);
	//			mThumbnail = null;
	//		}
	//	}

	/**
	 *  This method, handles the button presses of the top buttons found
	 *  in the Main activity.
	 */
	@Override
	public void onClick(View v)
	{

		switch(v.getId()) {


		case R.id.imgbtnpaste:


			if(multi_select_flag)
			{

				ImageButton imgbtn_multiselect_button =
						(ImageButton)((Activity) mContext).findViewById(R.id.multiselect_button);
				imgbtn_multiselect_button.setBackgroundResource(R.drawable.multi_select);


				//multi_select_flag = false;

			}
			else
			{
				ImageButton imgbtn_multiselect_button =
						(ImageButton)((Activity) mContext).findViewById(R.id.multiselect_button);


				imgbtn_multiselect_button.setBackgroundResource(R.drawable.multi_select_);
				multi_select_flag = true;

			}
			boolean multi_select = hasMultiSelectData();

			if(multi_select)
			{
				copyFileMultiSelect(mFileMang.getCurrentDir());

			} else if(File_Manager_MainActivity.mHoldingFile && FM__ConstantData.srt_file_path_for_copy.length() > 1) {

				copyFile(FM__ConstantData.srt_file_path_for_copy, mFileMang.getCurrentDir());

			}


			updateDirectory(mFileMang.getrefresh(mFileMang.getCurrentDir())) ;
			File_Manager_MainActivity.llpaste.setVisibility(LinearLayout.GONE);
			File_Manager_MainActivity.mHoldingFile = false;

			break;
		case R.id.imgbtnup:




			String str_currentdir=mFileMang.getCurrentDir();


			Log.e("str_currentdir", str_currentdir);



			if(str_currentdir.equalsIgnoreCase("/"))
			{
				break;
			}
			else
			{

				if(str_currentdir.equalsIgnoreCase(FM__ConstantData.str_homedirpath))
				{

					File_Manager_MainActivity.arrylist_next_folder= new  ArrayList<String>();
					EventHandler.next_folder_index=0;
					FM__ConstantData.str_homedirpath_up=FM__ConstantData.str_homedirpath;

					FM__ConstantData.str_homedirpath_up=FM__ConstantData.str_homedirpath_up.substring(0, FM__ConstantData.str_homedirpath_up.lastIndexOf("/"));
					updateDirectory(mFileMang.setHomeDir(FM__ConstantData.str_homedirpath_up));

					if(mPathLabel != null)
						mPathLabel.setText(mFileMang.getCurrentDir());
					break;
				}
				else
				{

					if(FM__ConstantData.str_homedirpath_up.equalsIgnoreCase(FM__ConstantData.str_homedirpath))
					{
						updateDirectory(mFileMang.setHomeDir(FM__ConstantData.str_homedirpath));
						if(mPathLabel != null)
							mPathLabel.setText(mFileMang.getCurrentDir());
						break;
					}
					else if(FM__ConstantData.str_homedirpath_up.equalsIgnoreCase("/"))
					{
						break;
					}
					else
					{


						if(FM__ConstantData.str_homedirpath_up.indexOf('/')==FM__ConstantData.str_homedirpath_up.lastIndexOf('/'))
						{

							updateDirectory(mFileMang.setHomeDir("/"));

							if(mPathLabel != null)
								mPathLabel.setText(mFileMang.getCurrentDir());
							break;
						}
						else
						{

							FM__ConstantData.str_homedirpath_up=FM__ConstantData.str_homedirpath_up.substring(0, FM__ConstantData.str_homedirpath_up.lastIndexOf("/"));
							updateDirectory(mFileMang.setHomeDir(FM__ConstantData.str_homedirpath_up));

							if(mPathLabel != null)
								mPathLabel.setText(mFileMang.getCurrentDir());
							break;
						}

					}

				}
			}




			//	stopThumbnailThread();
			//updateDirectory(mFileMang.setHomeDir("/"));



		case R.id.imgbtnhome:

			//						if(multi_select_flag) {
			//							mDelegate.killMultiSelect(true);
			//							Toast.makeText(mContext, "Multi-select is now off",
			//										   Toast.LENGTH_SHORT).show();
			//						}

			//	stopThumbnailThread();
			FM__ConstantData.str_homedirpath_up=FM__ConstantData.str_homedirpath;
			File_Manager_MainActivity.arrylist_next_folder= new ArrayList<String>();

			EventHandler.next_folder_index=0;
			updateDirectory(mFileMang.setHomeDir(FM__ConstantData.str_homedirpath));
			if(mPathLabel != null)
				mPathLabel.setText(mFileMang.getCurrentDir());
			break;


		case R.id.imgbtnsearch:

			dialog_box = new Dialog(myacActivity);
			dialog_box.setContentView(R.layout.input_layout);
			dialog_box.setTitle(myacActivity.getResources().getString(R.string.Search));
			//	dialog_box.setCancelable(false);

			ImageView searchIcon = (ImageView)dialog_box.findViewById(R.id.input_icon);
			searchIcon.setImageResource(R.drawable.search);

			TextView search_label = (TextView)dialog_box.findViewById(R.id.input_label);
			search_label.setText(myacActivity.getResources().getString(R.string.Search_for_a_file));
			final EditText search_input = (EditText)dialog_box.findViewById(R.id.input_inputText);

			Button search_button = (Button)dialog_box.findViewById(R.id.input_create_b);
			Button cancel_button = (Button)dialog_box.findViewById(R.id.input_cancel_b);
			search_button.setText(myacActivity.getResources().getString(R.string.Search));

			search_button.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					String temp = search_input.getText().toString();

					if (temp.length() > 0)
						searchForFile(temp);
					dialog_box.dismiss();
				}
			});

			cancel_button.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dialog_box.dismiss();
				}
			});
			dialog_box.show();
			break;
		case R.id.imgbtnback:

			//						if(multi_select_flag) {
			//							mDelegate.killMultiSelect(true);
			//							Toast.makeText(mContext, "Multi-select is now off",
			//										   Toast.LENGTH_SHORT).show();
			//						}

			//	stopThumbnailThread();
			flag_next_folder=false;
			String str_crnt_dir=mFileMang.getCurrentDir();

			if(str_crnt_dir.equalsIgnoreCase("/"))
			{

			}
			else if(str_crnt_dir.equalsIgnoreCase("/mnt"))
			{

			}

			else if(str_crnt_dir.equalsIgnoreCase(FM__ConstantData.str_homedirpath))
			{

			}
			else
			{
				if(next_folder_index>0)
				{

					updateDirectory(mFileMang.getPreviousDir());
					if(mPathLabel != null)
						mPathLabel.setText(mFileMang.getCurrentDir());

					next_folder_index=next_folder_index-1;
				}
				else
				{
					next_folder_index=0;
				}

			}


			break;


		case R.id.imgbtnnext:


			str_crnt_dir=mFileMang.getCurrentDir();

			if(str_crnt_dir.equalsIgnoreCase("/"))
			{

			}
			else if(str_crnt_dir.equalsIgnoreCase("/mnt"))
			{

			}

			else if(str_crnt_dir.equalsIgnoreCase(FM__ConstantData.str_homedirpath))
			{


				if(next_folder_index < File_Manager_MainActivity.arrylist_next_folder.size())
				{
					updateDirectory(mFileMang.getNextDir(File_Manager_MainActivity.arrylist_next_folder.get(next_folder_index), false));

					if(mPathLabel != null)
						mPathLabel.setText(mFileMang.getCurrentDir());
					next_folder_index=next_folder_index+1;
				}
			}
			else
			{
				if(next_folder_index< File_Manager_MainActivity.arrylist_next_folder.size())
				{
					updateDirectory(mFileMang.getNextDir(File_Manager_MainActivity.arrylist_next_folder.get(next_folder_index), false));

					if(mPathLabel != null)
						mPathLabel.setText(mFileMang.getCurrentDir());
					next_folder_index=next_folder_index+1;
				}
			}


			break;



		case R.id.imgbtnfav:


			FM__ConstantData.sqlfm_bookmark.Read();


			favuserlist	=FM__ConstantData.sqlfm_bookmark.getAllBookmarkListData();

			FM__ConstantData.db.close();

			if(favuserlist.size()==0)
			{
				Toast.makeText(mContext, myacActivity.getResources().getString(R.string.No_Bookmark_Added), Toast.LENGTH_LONG).show();
			}
			else
			{
				Intent info = new Intent(mContext, FM_bookmarkList.class);

				//info.putExtra("PATH_NAME", mFileMang.getCurrentDir());
				mContext.startActivity(info);
			}



			break;
		case R.id.imgbtnsort_ascending:

			File_Manager_MainActivity.editor.putInt(File_Manager_MainActivity.PREFS_SORT, 1);
			File_Manager_MainActivity.editor.commit();
			mFileMang.setSortType(1);
			updateDirectory(mFileMang.getrefresh(mFileMang.getCurrentDir())) ;
			//Toast.makeText(mContext, myacActivity.getResources().getString(R.string.Sort_Completed), Toast.LENGTH_LONG).show();

			break;

		case R.id.imgbtnsort_descending:

			File_Manager_MainActivity.editor.putInt(File_Manager_MainActivity.PREFS_SORT,4);
			File_Manager_MainActivity.editor.commit();
			mFileMang.setSortType(4);
			updateDirectory(mFileMang.getrefresh(mFileMang.getCurrentDir())) ;
			//Toast.makeText(mContext, myacActivity.getResources().getString(R.string.Sort_Completed), Toast.LENGTH_LONG).show();

			break;

		case R.id.imgbtnsort_size:

			File_Manager_MainActivity.editor.putInt(File_Manager_MainActivity.PREFS_SORT, 3);
			File_Manager_MainActivity.editor.commit();
			mFileMang.setSortType(3);
			updateDirectory(mFileMang.getrefresh(mFileMang.getCurrentDir())) ;
			//Toast.makeText(mContext, myacActivity.getResources().getString(R.string.Sort_Completed), Toast.LENGTH_LONG).show();

			break;


//		case R.id.imgbtnsort_type:
//
//			File_Manager_MainActivity.editor.putInt(File_Manager_MainActivity.PREFS_SORT, 2);
//			File_Manager_MainActivity.editor.commit();
//			mFileMang.setSortType(2);
//			updateDirectory(mFileMang.getrefresh(mFileMang.getCurrentDir())) ;
//			//Toast.makeText(mContext, myacActivity.getResources().getString(R.string.Sort_Completed), Toast.LENGTH_LONG).show();
//
//			break;
		case R.id.imgbtnrefresh:



			updateDirectory(mFileMang.getrefresh(mFileMang.getCurrentDir())) ;
			//Toast.makeText(mContext, myacActivity.getResources().getString(R.string.Refresh), Toast.LENGTH_LONG).show();
			break;

			//		case R.id.imgbtnbuypro:
			//
			//			if (connMgr_info.getActiveNetworkInfo() != null && connMgr_info.getActiveNetworkInfo().isAvailable() && connMgr_info.getActiveNetworkInfo().isConnected())
			//			{
			//
			//				Intent authIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(myacActivity.getResources().getString(R.string.Buy_Pro_link)));
			//				myacActivity.startActivity(authIntent);
			//			}
			//			else
			//			{
			//				Toast.makeText(myacActivity, myacActivity.getResources().getString(R.string.No_Internet_Connection), Toast.LENGTH_LONG).show();
			//			}
			//			break;
		case R.id.imgbtncreate:

			dialog_box = new Dialog(myacActivity);
			dialog_box.setContentView(R.layout.input_layout);
			dialog_box.setTitle(myacActivity.getResources().getString(R.string.Create_New_Directory));
			//dialog_box.setCancelable(false);

			ImageView icon = (ImageView)dialog_box.findViewById(R.id.input_icon);
			icon.setImageResource(R.drawable.folder);

			TextView label = (TextView)dialog_box.findViewById(R.id.input_label);
			label.setText(mFileMang.getCurrentDir());
			final EditText input = (EditText)dialog_box.findViewById(R.id.input_inputText);

			Button cancel = (Button)dialog_box.findViewById(R.id.input_cancel_b);
			Button create = (Button)dialog_box.findViewById(R.id.input_create_b);

			create.setOnClickListener(new OnClickListener() {
				public void onClick (View v) {
					if (input.getText().length() > 1) {
						if (mFileMang.createDir(mFileMang.getCurrentDir() + "/", input.getText().toString()) == 0)
							Toast.makeText(mContext,
									myacActivity.getResources().getString(R.string.Folder)+ input.getText().toString() + " "+myacActivity.getResources().getString(R.string.Created),
									Toast.LENGTH_LONG).show();
						else
							Toast.makeText(mContext, myacActivity.getResources().getString(R.string.New_folder_was_not_created), Toast.LENGTH_SHORT).show();
					}

					dialog_box.dismiss();
					String temp = mFileMang.getCurrentDir();
					updateDirectory(mFileMang.getNextDir(temp, true));
				}
			});
			cancel.setOnClickListener(new OnClickListener() {
				public void onClick (View v) {
					dialog_box.dismiss();

				}
			});


			dialog_box.show();




			break;
			//					case R.id.info_button:
			//						Intent info = new Intent(mContext, DirectoryInfo.class);
			//						info.putExtra("PATH_NAME", mFileMang.getCurrentDir());
			//						mContext.startActivity(info);
			//						break;
			//
			//					case R.id.help_button:
			//						Intent help = new Intent(mContext, HelpManager.class);
			//						mContext.startActivity(help);
			//						break;
			//
			//					case R.id.manage_button:
			//						display_dialog(MANAGE_DIALOG);
			//						break;
			//
		case R.id.imgbtncreate_file:


			Intent info = new Intent(mContext,EditorActivity.class);

			info.putExtra("PATH_NAME", mFileMang.getCurrentDir());
			mContext.startActivity(info);
			break;
		case R.id.multiselect_button:


			if(multi_select_flag)
			{

				ImageButton imgbtn_multiselect_button =
						(ImageButton)((Activity) mContext).findViewById(R.id.multiselect_button);
				imgbtn_multiselect_button.setBackgroundResource(R.drawable.multi_select_);
				mDelegate.killMultiSelect(true);

				//multi_select_flag = false;

			}
			else
			{
				if(mMultiSelectData != null && !mMultiSelectData.isEmpty())
					mMultiSelectData.clear();

				ImageButton imgbtn_multiselect_button =
						(ImageButton)((Activity) mContext).findViewById(R.id.multiselect_button);

				LinearLayout hidden_lay =
						(LinearLayout)((Activity) mContext).findViewById(R.id.hidden_buttons);
				imgbtn_multiselect_button.setBackgroundResource(R.drawable.multi_select);
				multi_select_flag = true;
				hidden_lay.setVisibility(LinearLayout.VISIBLE);
			}
			break;

			/*
			 * three hidden buttons for multiselect
			 */

			//		case R.id.hidden_attach:
			//			/* check if user selected objects before going further */
			//			if(mMultiSelectData == null || mMultiSelectData.isEmpty()) {
			//				mDelegate.killMultiSelect(true);
			//				break;
			//			}
			//
			//			ArrayList<Uri> uris = new ArrayList<Uri>();
			//			int length = mMultiSelectData.size();
			//			Intent mail_int = new Intent();
			//
			//			mail_int.setAction(android.content.Intent.ACTION_SEND_MULTIPLE);
			//			mail_int.setType("application/mail");
			//			mail_int.putExtra(Intent.EXTRA_BCC, "");
			//			mail_int.putExtra(Intent.EXTRA_SUBJECT, " ");
			//
			//			for(int i = 0; i < length; i++) {
			//				File file = new File(mMultiSelectData.get(i));
			//				uris.add(Uri.fromFile(file));
			//			}
			//
			//			mail_int.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
			//			mContext.startActivity(Intent.createChooser(mail_int,
			//					"Email using..."));
			//
			//			mDelegate.killMultiSelect(true);
			//			break;

		case R.id.hidden_move:
		case R.id.hidden_copy:
			/* check if user selected objects before going further */


			if(multi_select_flag)
			{

				ImageButton imgbtn_multiselect_button =
						(ImageButton)((Activity) mContext).findViewById(R.id.multiselect_button);
				imgbtn_multiselect_button.setBackgroundResource(R.drawable.multi_select_);


				//multi_select_flag = false;

			}
			else
			{
				ImageButton imgbtn_multiselect_button =
						(ImageButton)((Activity) mContext).findViewById(R.id.multiselect_button);


				imgbtn_multiselect_button.setBackgroundResource(R.drawable.multi_select);


			}
			if(mMultiSelectData == null || mMultiSelectData.isEmpty())
			{

				File_Manager_MainActivity.llpaste.setVisibility(LinearLayout.VISIBLE);


				//copyFileMultiSelect(mFileMang.getCurrentDir());
				mDelegate.killMultiSelect_paste(false);
				updateDirectory(mFileMang.getrefresh(mFileMang.getCurrentDir())) ;
				break;
			}
			else
			{

				File_Manager_MainActivity.llpaste.setVisibility(LinearLayout.VISIBLE);


				//copyFileMultiSelect(mFileMang.getCurrentDir());
				mDelegate.killMultiSelect_paste(false);
				updateDirectory(mFileMang.getrefresh(mFileMang.getCurrentDir())) ;
			}


			if(v.getId() == R.id.hidden_move)
				delete_after_copy = true;

			//mInfoLabel.setText("Holding " + mMultiSelectData.size() + " file(s)");

			//mDelegate.killMultiSelect(false);
			break;

		case R.id.hidden_delete:
			/* check if user selected objects before going further */

			if(multi_select_flag)
			{

				ImageButton imgbtn_multiselect_button =
						(ImageButton)((Activity) mContext).findViewById(R.id.multiselect_button);
				imgbtn_multiselect_button.setBackgroundResource(R.drawable.multi_select_);


				//multi_select_flag = false;

			}
			else
			{
				ImageButton imgbtn_multiselect_button =
						(ImageButton)((Activity) mContext).findViewById(R.id.multiselect_button);


				imgbtn_multiselect_button.setBackgroundResource(R.drawable.multi_select);


			}
			if(mMultiSelectData == null || mMultiSelectData.isEmpty())
			{
				mDelegate.killMultiSelect(true);
				break;
			}

			final String[] data = new String[mMultiSelectData.size()];
			int at = 0;

			for(String string : mMultiSelectData)
				data[at++] = string;

			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setMessage("Are you sure you want to delete " +
					data.length + " files? This cannot be " +
					"undone.");
			builder.setCancelable(false);
			builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					new BackgroundWork(DELETE_TYPE).execute(data);
					mDelegate.killMultiSelect(true);
				}
			});
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mDelegate.killMultiSelect(true);
					dialog.cancel();
				}
			});

			builder.create().show();
			break;
		}
	}

	/**
	 * will return the data in the ArrayList that holds the dir contents.
	 *
	 * @param position	the indext of the arraylist holding the dir content
	 * @return the data in the arraylist at position (position)
	 */
	public String getData(int position) {

		if(position > mDataSource.size() - 1 || position < 0)
			return null;

		return mDataSource.get(position);
	}

	/**
	 * called to update the file contents as the user navigates there
	 * phones file system.
	 *
	 * @param content	an ArrayList of the file/folders in the current directory.
	 */
	public void updateDirectory(ArrayList<String> content) {
		if(!mDataSource.isEmpty())
			mDataSource.clear();

		for(String data : content)
			mDataSource.add(data);


		if(mDataSource.size()==0)
		{
			File_Manager_MainActivity.txtmessage.setVisibility(TextView.VISIBLE);
		}
		else
		{
			File_Manager_MainActivity.txtmessage.setVisibility(TextView.GONE);
		}


		mDelegate.notifyDataSetChanged();
	}

	/**
	 * This private method is used to display options the user can select when
	 * the tool box button is pressed. The WIFI option is commented out as it doesn't
	 * seem to fit with the overall idea of the application. However to display it, just
	 * uncomment the below code and the code in the AndroidManifest.xml file.
	 */
	private void display_dialog(int type) {
		AlertDialog.Builder builder;
		AlertDialog dialog;

		switch(type) {
		//    		case MANAGE_DIALOG:
		//    			//un-comment WIFI Info here and in the manifest file
		//    	    	//to display WIFI info. Also uncomment and change case number below
		//    	    	CharSequence[] options = {"Process Info", /*"Wifi Info",*/ "Application backup"};
		//
		//    	    	builder = new AlertDialog.Builder(mContext);
		//    	    	builder.setTitle("Tool Box");
		//    	    	builder.setIcon(R.drawable.toolbox);
		//    	    	builder.setItems(options, new DialogInterface.OnClickListener() {
		//
		//    				public void onClick(DialogInterface dialog, int index) {
		//    					Intent i;
		//
		//    					switch(index) {
		//    						case 0:
		//    							i = new Intent(mContext, ProcessManager.class);
		//    							mContext.startActivity(i);
		//    							break;
		//    	/*
		//    						case 1:
		//    							i = new Intent(context, WirelessManager.class);
		//    							context.startActivity(i);
		//    							break;
		//    	*/
		//    						case 1:
		//    							i = new Intent(mContext, ApplicationBackup.class);
		//    							mContext.startActivity(i);
		//    							break;
		//    					}
		//    				}
		//    			});
		//    	    	dialog = builder.create();
		//    	    	dialog.show();
		//    			break;
		}
	}

	private static class ViewHolder {
		TextView topView,textsize;
		TextView bottomView;
		ImageView icon;
		ImageView mSelect;	//multi-select check mark icon
	}


	/**
	 * A nested class to handle displaying a custom view in the ListView that
	 * is used in the Main activity. If any icons are to be added, they must
	 * be implemented in the getView method. This class is instantiated once in Main
	 * and has no reason to be instantiated again.
	 *
	 * @author Joe Berria
	 */
	public class TableRow extends ArrayAdapter<String> {
		private final int KB = 1024;
		private final int MG = KB * KB;
		private final int GB = MG * KB;
		private String display_size;
		private ArrayList<Integer> positions;
		private LinearLayout hidden_layout;

		public TableRow() {
			super(mContext, R.layout.tablerow, mDataSource);
		}

		public void addMultiPosition(int index, String path) {
			if(positions == null)
				positions = new ArrayList<Integer>();

			if(mMultiSelectData == null) {
				positions.add(index);
				add_multiSelect_file(path);

			} else if(mMultiSelectData.contains(path)) {
				if(positions.contains(index))
					positions.remove(new Integer(index));

				mMultiSelectData.remove(path);

			} else {
				positions.add(index);
				add_multiSelect_file(path);
			}

			notifyDataSetChanged();
		}

		/**
		 * This will turn off multi-select and hide the multi-select buttons at the
		 * bottom of the view.
		 *
		 * @param clearData if this is true any files/folders the user selected for multi-select
		 * 					will be cleared. If false, the data will be kept for later use. Note:
		 * 					multi-select copy and move will usually be the only one to pass false,
		 * 					so we can later paste it to another folder.
		 */

		public void killMultiSelect_paste(boolean clearData) {
			hidden_layout = (LinearLayout)((Activity)mContext).findViewById(R.id.hidden_buttons);
			hidden_layout.setVisibility(LinearLayout.GONE);
			multi_select_flag = false;

			if(positions != null && !positions.isEmpty())
				positions.clear();

			notifyDataSetChanged();

		}
		public void killMultiSelect(boolean clearData) {
			hidden_layout = (LinearLayout)((Activity)mContext).findViewById(R.id.hidden_buttons);
			hidden_layout.setVisibility(LinearLayout.GONE);
			multi_select_flag = false;

			if(positions != null && !positions.isEmpty())
				positions.clear();

			if(clearData)
				if(mMultiSelectData != null && !mMultiSelectData.isEmpty())
					mMultiSelectData.clear();

			notifyDataSetChanged();
		}

		public String getFileDate_Time(File file) {
			String strdatetime = "";

			long datetime=file.lastModified();
			Date date=new Date(datetime);
			SimpleDateFormat df1 = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
			SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yy");
			strdatetime = df1.format(date);
			return strdatetime;
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

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder mViewHolder;
			int num_items = 0;
			String temp = mFileMang.getCurrentDir();
			File file = new File(temp + "/" + mDataSource.get(position));
			String[] list = file.list();

			if(list != null)
			{
				num_items = list.length;
			}


			if(convertView == null)
			{
				LayoutInflater inflater = (LayoutInflater) mContext.
						getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.tablerow, parent, false);

				mViewHolder = new ViewHolder();
				mViewHolder.topView = (TextView)convertView.findViewById(R.id.top_view);
				mViewHolder.textsize= (TextView)convertView.findViewById(R.id.textsize);
				mViewHolder.bottomView = (TextView)convertView.findViewById(R.id.bottom_view);
				mViewHolder.icon = (ImageView)convertView.findViewById(R.id.row_image);
				mViewHolder.mSelect = (ImageView)convertView.findViewById(R.id.multiselect_icon);

				convertView.setTag(mViewHolder);

			}
			else
			{
				mViewHolder = (ViewHolder)convertView.getTag();
			}

			if (positions != null && positions.contains(position))
				mViewHolder.mSelect.setVisibility(ImageView.VISIBLE);
			else
				mViewHolder.mSelect.setVisibility(ImageView.GONE);

			//			mViewHolder.topView.setTextColor(mColor);
			//			mViewHolder.textsize.setTextColor(mColor);
			//			mViewHolder.bottomView.setTextColor(mColor);

			//			if(mThumbnail == null)
			//				mThumbnail = new ThumbnailCreator(52, 52,mContext);

			if(file != null && file.isFile())
			{
				String ext = file.toString();
				String sub_ext = ext.substring(ext.lastIndexOf(".") + 1);

				/* This series of else if statements will determine which
				 * icon is displayed
				 */
				if (sub_ext.equalsIgnoreCase("pdf"))
				{
					mViewHolder.icon.setImageResource(R.drawable.pdf);

				}
				//audio files
				else if (sub_ext.equalsIgnoreCase("mp3"))
				{

					mViewHolder.icon.setImageResource(R.drawable.mp3);

				}
				else if (sub_ext.equalsIgnoreCase("wma") )
				{

					mViewHolder.icon.setImageResource(R.drawable.wma);

				}
				else if (sub_ext.equalsIgnoreCase("caf") )
				{

					mViewHolder.icon.setImageResource(R.drawable.caf);

				}
				else if (sub_ext.equalsIgnoreCase("ogg") )
				{

					mViewHolder.icon.setImageResource(R.drawable.ogg);

				}
				else if (sub_ext.equalsIgnoreCase("wav"))
				{

					mViewHolder.icon.setImageResource(R.drawable.wav_);

				}

				else if (sub_ext.equalsIgnoreCase("midi"))
				{

					mViewHolder.icon.setImageResource(R.drawable.midi);

				}
				else if (sub_ext.equalsIgnoreCase("mid"))
				{

					mViewHolder.icon.setImageResource(R.drawable.mid);

				}
				else if (sub_ext.equalsIgnoreCase("mid"))
				{

					mViewHolder.icon.setImageResource(R.drawable.mid);

				}
				else if (sub_ext.equalsIgnoreCase("mid"))
				{

					mViewHolder.icon.setImageResource(R.drawable.mid);

				}
				else if (sub_ext.equalsIgnoreCase("mid"))
				{

					mViewHolder.icon.setImageResource(R.drawable.mid);

				}
				else if (sub_ext.equalsIgnoreCase("mid"))
				{

					mViewHolder.icon.setImageResource(R.drawable.mid);

				}
				else if (sub_ext.equalsIgnoreCase("m4a"))
				{

					mViewHolder.icon.setImageResource(R.drawable.m4a);

				}

				else if (sub_ext.equalsIgnoreCase("m4p"))
				{

					mViewHolder.icon.setImageResource(R.drawable.m4p);

				}
				else if (sub_ext.equalsIgnoreCase("amr") )
				{

					mViewHolder.icon.setImageResource(R.drawable.amr);

				}

				//audio files
				else if (sub_ext.equalsIgnoreCase("png") ||
						sub_ext.equalsIgnoreCase("jpg") ||
						sub_ext.equalsIgnoreCase("jpeg")||
						sub_ext.equalsIgnoreCase("gif") ||
						sub_ext.equalsIgnoreCase("bmp") ||
						sub_ext.equalsIgnoreCase("webp") ||
						sub_ext.equalsIgnoreCase("psd") ||
						sub_ext.equalsIgnoreCase("tiff"))
				{

					if(thumbnail_flag && file.length() != 0)
					{
						mViewHolder.icon.setTag(file.getAbsolutePath());

						imageLoader.displayImage(file.getAbsolutePath(),myacActivity, mViewHolder.icon);
						// FM_BitmapManager.INSTANCE.loadBitmap(file.getAbsolutePath(),  mViewHolder.icon, 50, 50);
						//						Bitmap thumb = mThumbnail.isBitmapCached(file.getPath());
						//
						//						if (thumb == null) {
						//							final Handler handle = new Handler(new Handler.Callback() {
						//								public boolean handleMessage(Message msg) {
						//									notifyDataSetChanged();
						//
						//									return true;
						//								}
						//							});
						//
						//							mThumbnail.createNewThumbnail(mDataSource, mFileMang.getCurrentDir(), handle);
						//
						//							if (!mThumbnail.isAlive())
						//								mThumbnail.start();
						//
						//						} else {
						//							mViewHolder.icon.setImageBitmap(thumb);
						//						}

					}
					else
					{

						if(sub_ext.equalsIgnoreCase("png"))
						{
							mViewHolder.icon.setImageResource(R.drawable.png);
						}
						else if(sub_ext.equalsIgnoreCase("jpg"))
						{
							mViewHolder.icon.setImageResource(R.drawable.jpg);
						}
						else if(sub_ext.equalsIgnoreCase("jpeg"))
						{
							mViewHolder.icon.setImageResource(R.drawable.jpg);
						}
						else if(sub_ext.equalsIgnoreCase("gif"))
						{
							mViewHolder.icon.setImageResource(R.drawable.gif);
						}
						else if(sub_ext.equalsIgnoreCase("bmp"))
						{
							mViewHolder.icon.setImageResource(R.drawable.bmp);
						}
						else if(sub_ext.equalsIgnoreCase("webp"))
						{
							mViewHolder.icon.setImageResource(R.drawable.webp);
						}
						else if(sub_ext.equalsIgnoreCase("tiff"))
						{
							mViewHolder.icon.setImageResource(R.drawable.tiff);
						}
						else if(sub_ext.equalsIgnoreCase("psd"))
						{
							mViewHolder.icon.setImageResource(R.drawable.tiff);
						}


					}

				}
				//video files
				else if(sub_ext.equalsIgnoreCase("avi") ||
						sub_ext.equalsIgnoreCase("3gp") ||
						sub_ext.equalsIgnoreCase("mp4") ||
						sub_ext.equalsIgnoreCase("mov") ||
						sub_ext.equalsIgnoreCase("m4v") ||
						sub_ext.equalsIgnoreCase("mkv") ||
						sub_ext.equalsIgnoreCase("webm") ||
						sub_ext.equalsIgnoreCase("wmv"))
				{

					//					Bitmap bMap=null;
					//
					//					try {
					//
					//						//drawableLoader.displayImage(file.getAbsolutePath(),myacActivity, mViewHolder.icon);
					//					//bMap = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MediaStore.Video.Thumbnails.MICRO_KIND);
					//					FM_Video_BitmapManager.INSTANCE.loadBitmap(file.getAbsolutePath(), mViewHolder.icon, 100, 100);
					//					} catch (Exception e) {
					//						// TODO: handle exception
					//
					//					}
					//
					//
					//					if(bMap!=null)
					//					{
					//						mViewHolder.icon.setImageBitmap(bMap);
					//					}
					//					else
					//					{
					//						if(sub_ext.equalsIgnoreCase("avi"))
					//						{
					//							mViewHolder.icon.setImageResource(R.drawable.avi);
					//						}
					//						else if(sub_ext.equalsIgnoreCase("3gp"))
					//						{
					//							mViewHolder.icon.setImageResource(R.drawable.a_3gp);
					//						}
					//						else if(sub_ext.equalsIgnoreCase("mp4"))
					//						{
					//							 mViewHolder.icon.setImageResource(R.drawable.mp4);
					//						}
					//						else if(sub_ext.equalsIgnoreCase("mov"))
					//						{
					//							mViewHolder.icon.setImageResource(R.drawable.mov);
					//						}
					//						else if(sub_ext.equalsIgnoreCase("m4v"))
					//						{
					//							mViewHolder.icon.setImageResource(R.drawable.m4v);
					//						}
					//						else if(sub_ext.equalsIgnoreCase("mkv"))
					//						{
					//							mViewHolder.icon.setImageResource(R.drawable.mkv);
					//						}
					//						else if(sub_ext.equalsIgnoreCase("webm"))
					//						{
					//							mViewHolder.icon.setImageResource(R.drawable.webm);
					//						}
					//						else if(sub_ext.equalsIgnoreCase("wmv"))
					//						{
					//							mViewHolder.icon.setImageResource(R.drawable.wmv);
					//						}
					//
					//
					//					}


					if(sub_ext.equalsIgnoreCase("avi"))
					{
						mViewHolder.icon.setImageResource(R.drawable.avi);
					}
					else if(sub_ext.equalsIgnoreCase("3gp"))
					{
						mViewHolder.icon.setImageResource(R.drawable.a_3gp);
					}
					else if(sub_ext.equalsIgnoreCase("mp4"))
					{
						mViewHolder.icon.setImageResource(R.drawable.mp4);
					}
					else if(sub_ext.equalsIgnoreCase("mov"))
					{
						mViewHolder.icon.setImageResource(R.drawable.mov);
					}
					else if(sub_ext.equalsIgnoreCase("m4v"))
					{
						mViewHolder.icon.setImageResource(R.drawable.m4v);
					}
					else if(sub_ext.equalsIgnoreCase("mkv"))
					{
						mViewHolder.icon.setImageResource(R.drawable.mkv);
					}
					else if(sub_ext.equalsIgnoreCase("webm"))
					{
						mViewHolder.icon.setImageResource(R.drawable.webm);
					}
					else if(sub_ext.equalsIgnoreCase("wmv"))
					{
						mViewHolder.icon.setImageResource(R.drawable.wmv);
					}


					try {

						//drawableLoader.displayImage(file.getAbsolutePath(),myacActivity, mViewHolder.icon);
						//bMap = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MediaStore.Video.Thumbnails.MICRO_KIND);
						FM_Video_BitmapManager.INSTANCE.loadBitmap(file.getAbsolutePath(), mViewHolder.icon, 100, 100);

					} catch (Exception e) {
						// TODO: handle exception


					}




				}
				// text file
				else if(sub_ext.equalsIgnoreCase("txt")) {
					mViewHolder.icon.setImageResource(R.drawable.txt);

				}

				// zip folder
				else if (sub_ext.equalsIgnoreCase("zip")  ||
						sub_ext.equalsIgnoreCase("gzip") ||
						sub_ext.equalsIgnoreCase("gz")) {

					mViewHolder.icon.setImageResource(R.drawable.zip);

				}
				// rar folder
				else if (sub_ext.equalsIgnoreCase("rar"))
				{
					mViewHolder.icon.setImageResource(R.drawable.rar);

				}

				// system files
				else if(sub_ext.equalsIgnoreCase("conf"))
				{
					mViewHolder.icon.setImageResource(R.drawable.conf);

				}
				else if(sub_ext.equalsIgnoreCase("jar"))
				{

					mViewHolder.icon.setImageResource(R.drawable.jar);

				}
				else if(sub_ext.equalsIgnoreCase("ttf"))
				{

					mViewHolder.icon.setImageResource(R.drawable.ttf);

				}

				// ms office files
				else if(sub_ext.equalsIgnoreCase("doc") ||
						sub_ext.equalsIgnoreCase("docx")) {

					mViewHolder.icon.setImageResource(R.drawable.doc);

				} else if(sub_ext.equalsIgnoreCase("xls") ||
						sub_ext.equalsIgnoreCase("xlsx")) {

					mViewHolder.icon.setImageResource(R.drawable.xls);

				} else if(sub_ext.equalsIgnoreCase("ppt") ||
						sub_ext.equalsIgnoreCase("pptx")) {

					mViewHolder.icon.setImageResource(R.drawable.ppt);

				}
				// document files
				else if(sub_ext.equalsIgnoreCase("java")) {
					mViewHolder.icon.setImageResource(R.drawable.java);

				}
				else if(sub_ext.equalsIgnoreCase("class")) {
					mViewHolder.icon.setImageResource(R.drawable.a_class);

				}

				else if(sub_ext.equalsIgnoreCase("apk"))
				{


					String filePath = file.getPath();
					mViewHolder.icon.setTag(filePath);
					drawableLoader.displayImage(filePath,myacActivity, mViewHolder.icon);

					//					PackageInfo packageInfo = mContext.getPackageManager().getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
					//					if(packageInfo != null)
					//					{
					//
					//						ApplicationInfo appInfo = packageInfo.applicationInfo;
					//						if (Build.VERSION.SDK_INT >= 8) {
					//							appInfo.sourceDir = filePath;
					//							appInfo.publicSourceDir = filePath;
					//						}
					//						Drawable icon = appInfo.loadIcon(mContext.getPackageManager());
					//						Bitmap bmpIcon = ((BitmapDrawable) icon).getBitmap();
					//
					//						mViewHolder.icon.setImageBitmap(bmpIcon);
					//					}
					//					else
					//					{
					//						mViewHolder.icon.setImageResource(R.drawable.apk);
					//					}



				}

				else if(sub_ext.equalsIgnoreCase("css")) {
					mViewHolder.icon.setImageResource(R.drawable.css);

				}
				else if(sub_ext.equalsIgnoreCase("php")) {
					mViewHolder.icon.setImageResource(R.drawable.php);

				}
				else if(sub_ext.equalsIgnoreCase("dll")) {
					mViewHolder.icon.setImageResource(R.drawable.dll);

				}
				else if(sub_ext.equalsIgnoreCase("html")) {
					mViewHolder.icon.setImageResource(R.drawable.html);

				}
				else if(sub_ext.equalsIgnoreCase("rtf")) {
					mViewHolder.icon.setImageResource(R.drawable.rtf);

				}
				else if(sub_ext.equalsIgnoreCase("xml")) {
					mViewHolder.icon.setImageResource(R.drawable.xml);

				}
				else if(sub_ext.equalsIgnoreCase("webdoc")) {
					mViewHolder.icon.setImageResource(R.drawable.webdoc);

				}
				else
				{
					mViewHolder.icon.setImageResource(R.drawable.unknown);
				}

			}
			else if (file != null && file.isDirectory())
			{
				if (file.canRead() && file.list().length > 0)
				{
					mViewHolder.icon.setImageResource(R.drawable.folder);
				}
				else
				{
					mViewHolder.icon.setImageResource(R.drawable.empty_folder);
				}

			}

			String permission = getFilePermissions(file);
			String date_Time = getFileDate_Time(file);

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
					mViewHolder.textsize.setText( display_size );
					mViewHolder.bottomView.setText(date_Time);
				}
				else
				{
					mViewHolder.textsize.setText(display_size );
					mViewHolder.bottomView.setText(date_Time);

				}

			}
			else
			{
				if(file.isHidden())
				{
					mViewHolder.textsize.setText(num_items + " "+mContext.getResources().getString(R.string.items) );
					mViewHolder.bottomView.setText( date_Time);
				}
				else
				{
					mViewHolder.textsize.setText(num_items + " "+mContext.getResources().getString(R.string.items));
					mViewHolder.bottomView.setText( date_Time);
				}
			}

			mViewHolder.topView.setText(file.getName());





			return convertView;
		}

		private void add_multiSelect_file(String src) {
			if(mMultiSelectData == null)
				mMultiSelectData = new ArrayList<String>();

			mMultiSelectData.add(src);
		}
	}

	/**
	 * A private inner class of EventHandler used to perform time extensive
	 * operations. So the user does not think the the application has hung,
	 * operations such as copy/past, search, unzip and zip will all be performed
	 * in the background. This class extends AsyncTask in order to give the user
	 * a progress dialog to show that the app is working properly.
	 *
	 * (note): this class will eventually be changed from using AsyncTask to using
	 * Handlers and messages to perform background operations.
	 *
	 * @author Joe Berria
	 */
	private class BackgroundWork extends AsyncTask<String, Void, ArrayList<String>> {
		private String file_name;
		private ProgressDialog pr_dialog;
		private int type;
		private int copy_rtn;

		private BackgroundWork(int type) {
			this.type = type;
		}

		/**
		 * This is done on the EDT thread. this is called before
		 * doInBackground is called
		 */
		@Override
		protected void onPreExecute() {

			switch(type) {
			case SEARCH_TYPE:
				pr_dialog = ProgressDialog.show(mContext, mContext.getResources().getString(R.string.Searching),
						mContext.getResources().getString(R.string.Searching_current_file_system),
						true, true);
				break;

			case COPY_TYPE:
				pr_dialog = ProgressDialog.show(mContext, mContext.getResources().getString(R.string.Copying),
						mContext.getResources().getString(R.string.Copying_file),
						true, false);
				break;

			case UNZIP_TYPE:
				pr_dialog = ProgressDialog.show(mContext, mContext.getResources().getString(R.string.Unzipping),
						mContext.getResources().getString(R.string.Unpacking_zip_file_please_wait),
						true, false);
				break;

			case UNZIPTO_TYPE:
				pr_dialog = ProgressDialog.show(mContext, mContext.getResources().getString(R.string.Unzipping),
						mContext.getResources().getString(R.string.Unpacking_zip_file_please_wait),
						true, false);
				break;

			case ZIP_TYPE:
				pr_dialog = ProgressDialog.show(mContext, mContext.getResources().getString(R.string.Zipping),
						mContext.getResources().getString(R.string.Zipping_folder),
						true, false);
				break;

			case DELETE_TYPE:
				pr_dialog = ProgressDialog.show(mContext, mContext.getResources().getString(R.string.Deleting),
						mContext.getResources().getString(R.string.Deleting_files),
						true, false);
				break;
			}
		}

		/**
		 * background thread here
		 */
		@Override
		protected ArrayList<String> doInBackground(String... params) {

			switch(type) {
			case SEARCH_TYPE:
				file_name = params[0];
				ArrayList<String> found = mFileMang.searchInDirectory(mFileMang.getCurrentDir(),
						file_name);
				return found;

			case COPY_TYPE:
				int len = params.length;

				if(mMultiSelectData != null && !mMultiSelectData.isEmpty()) {
					for(int i = 1; i < len; i++)
					{
						copy_rtn = mFileMang.copyToDirectory(params[i], params[0]);

						if(delete_after_copy)
							mFileMang.deleteTarget(params[i]);
					}
				} else {
					copy_rtn = mFileMang.copyToDirectory(params[0], params[1]);

					if(delete_after_copy)
						mFileMang.deleteTarget(params[0]);
				}

				delete_after_copy = false;
				return null;

			case UNZIP_TYPE:
				mFileMang.extractZipFiles(params[0], params[1]);
				return null;

			case UNZIPTO_TYPE:
				mFileMang.extractZipFilesFromDir(params[0], params[1], params[2]);
				return null;

			case ZIP_TYPE:
				mFileMang.createZipFile(params[0]);
				return null;

			case DELETE_TYPE:
				int size = params.length;

				for(int i = 0; i < size; i++)
					mFileMang.deleteTarget(params[i]);

				return null;
			}
			return null;
		}

		/**
		 * This is called when the background thread is finished. Like onPreExecute, anything
		 * here will be done on the EDT thread.
		 */
		@Override
		protected void onPostExecute(final ArrayList<String> file) {
			final CharSequence[] names;
			int len = file != null ? file.size() : 0;

			switch(type) {
			case SEARCH_TYPE:
				if(len == 0) {

					Toast.makeText(mContext, mContext.getResources().getString(R.string.Could_not_find) +" " + file_name,
							Toast.LENGTH_SHORT).show();

				} else {

					names = new CharSequence[len];

					for (int i = 0; i < len; i++) {
						String entry = file.get(i);
						names[i] = entry.substring(entry.lastIndexOf("/") + 1, entry.length());
					}

					Intent info = new Intent(mContext, FM_SearchList.class);
					Bundle bundle= new Bundle();
					bundle.putStringArrayList("file_list_search", file);
					bundle.putCharSequenceArray("file_names_search", names);
					bundle.putString("Search_name",file_name);
					info.putExtras(bundle);
					//info.putExtra("PATH_NAME", mFileMang.getCurrentDir());
					mContext.startActivity(info);

					//					AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
					//					builder.setTitle(mContext.getResources().getString(R.string.Found) +" " + len + " "+mContext.getResources().getString(R.string.files));
					//					builder.setItems(names, new DialogInterface.OnClickListener() {
					//
					//						public void onClick(DialogInterface dialog, int position) {
					//							String path = file.get(position);
					//							updateDirectory(mFileMang.getNextDir(path.
					//									substring(0, path.lastIndexOf("/")), true));
					//						}
					//					});
					//
					//					AlertDialog dialog = builder.create();
					//					dialog.show();
				}

				pr_dialog.dismiss();
				break;

			case COPY_TYPE:
				if(mMultiSelectData != null && !mMultiSelectData.isEmpty()) {
					multi_select_flag = false;
					mMultiSelectData.clear();
				}

				if(copy_rtn == 0)
					Toast.makeText(mContext,  mContext.getResources().getString(R.string.File_successfully_copied_and_pasted),
							Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(mContext, mContext.getResources().getString(R.string.Copy_pasted_failed), Toast.LENGTH_SHORT).show();

				FM__ConstantData.srt_file_path_for_copy=null;
				EventHandler.destination_path_file=null;
				mDelegate.killMultiSelect(true);
				updateDirectory(mFileMang.getNextDir(mFileMang.getCurrentDir(), true));
				pr_dialog.dismiss();
				//mInfoLabel.setText("");
				break;

			case UNZIP_TYPE:
				updateDirectory(mFileMang.getNextDir(mFileMang.getCurrentDir(), true));
				pr_dialog.dismiss();
				break;

			case UNZIPTO_TYPE:
				updateDirectory(mFileMang.getNextDir(mFileMang.getCurrentDir(), true));
				pr_dialog.dismiss();
				break;

			case ZIP_TYPE:
				updateDirectory(mFileMang.getNextDir(mFileMang.getCurrentDir(), true));
				pr_dialog.dismiss();
				break;

			case DELETE_TYPE:
				if(mMultiSelectData != null && !mMultiSelectData.isEmpty()) {
					mMultiSelectData.clear();
					multi_select_flag = false;
				}

				mDelegate.killMultiSelect(true);
				updateDirectory(mFileMang.getNextDir(mFileMang.getCurrentDir(), true));
				pr_dialog.dismiss();
				//mInfoLabel.setText("");
				break;
			}
		}
	}
}

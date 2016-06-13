package com.filemanager.sdexplorer.data;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.filemanager.sdexplorer.FM_SearchList;
import com.filemanager.sdexplorer.FM_bookmarkList;
import com.filemanager.sdexplorer.R;

public class FM__Search_list_Adapter extends BaseAdapter{


	private Activity activity;
	private ArrayList<String> file_list_adpt;
	private CharSequence[] names_adpt;
	private LayoutInflater inflater=null;
	private ImageThreadLoader imageLoader;
	private DrawableThreadLoader	drawableLoader;
	private boolean thumbnail_flag = true;
	private String sub_ext;

	public FM__Search_list_Adapter(FM_SearchList fm_SearchList,
			ArrayList<String> file, CharSequence[] names) {
		
		activity=fm_SearchList;
		file_list_adpt=file;
		names_adpt=names;
		imageLoader = new ImageThreadLoader(activity);
		drawableLoader = new DrawableThreadLoader(activity);
		inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// TODO Auto-generated constructor stub
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return this.file_list_adpt.size();
	}


	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}


	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public class ViewHolder{
		public TextView username;
		public TextView message;
		public ImageView image;
		public ImageButton imgaddbtn;
	}
	public View getView(final int position, View convertView, ViewGroup parent) 
	{
		View v=convertView;
		if(v==null)
		{
			v = inflater.inflate(R.layout.itemlist_bookmark, null);
		} 



		ImageView image=(ImageView)v.findViewById(R.id.imgbookmarkicon);
		TextView txtbookmarkname=(TextView)v.findViewById(R.id.txtbookmarkname);
		TextView txtbookmarkpath=(TextView)v.findViewById(R.id.txtbookmarkpath);
		txtbookmarkpath.setSelected(true);
		
		image.setTag(""+position);
		image.setImageResource(R.drawable.folder);
		txtbookmarkname.setText(names_adpt[position]);
		txtbookmarkpath.setText(file_list_adpt.get(position));

		File file = new File(file_list_adpt.get(position));
		
		
		
		if(file != null && file.isFile())
		{
			String str_filepath = file.toString();
			sub_ext=str_filepath.substring(str_filepath.lastIndexOf(".")+1, str_filepath.length());
			Log.e("item_ext", sub_ext);
		}
		
		if (file.isDirectory()) 
		{
			if(file.canRead()) 
			{
				
				image.setImageResource(R.drawable.folder);
			} else
			{
				image.setImageResource(R.drawable.folder);
			}
		}
		else if (sub_ext.equalsIgnoreCase("pdf")) 
		{
			image.setImageResource(R.drawable.pdf);

		} 
		//audio files
		else if (sub_ext.equalsIgnoreCase("mp3"))
		{

			image.setImageResource(R.drawable.mp3);

		}
		else if (sub_ext.equalsIgnoreCase("wma") )
		{

			image.setImageResource(R.drawable.wma);

		}
		else if (sub_ext.equalsIgnoreCase("caf") )
		{

			image.setImageResource(R.drawable.caf);

		}
		else if (sub_ext.equalsIgnoreCase("ogg") )
		{

			image.setImageResource(R.drawable.ogg);

		}
		else if (sub_ext.equalsIgnoreCase("wav"))
		{

			image.setImageResource(R.drawable.wav_);

		}

		else if (sub_ext.equalsIgnoreCase("midi"))
		{

			image.setImageResource(R.drawable.midi);

		}
		else if (sub_ext.equalsIgnoreCase("mid"))
		{

			image.setImageResource(R.drawable.mid);

		}
		else if (sub_ext.equalsIgnoreCase("mid"))
		{

			image.setImageResource(R.drawable.mid);

		}
		else if (sub_ext.equalsIgnoreCase("mid"))
		{

			image.setImageResource(R.drawable.mid);

		}
		else if (sub_ext.equalsIgnoreCase("mid"))
		{

			image.setImageResource(R.drawable.mid);

		}
		else if (sub_ext.equalsIgnoreCase("mid"))
		{

			image.setImageResource(R.drawable.mid);

		}
		else if (sub_ext.equalsIgnoreCase("m4a"))
		{

			image.setImageResource(R.drawable.m4a);

		}

		else if (sub_ext.equalsIgnoreCase("m4p"))
		{

			image.setImageResource(R.drawable.m4p);

		}
		else if (sub_ext.equalsIgnoreCase("amr") )
		{

			image.setImageResource(R.drawable.amr);

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
				image.setTag(file_list_adpt.get(position));

				imageLoader.displayImage(file_list_adpt.get(position),activity, image);
				// FM_BitmapManager.INSTANCE.loadBitmap(file.getAbsolutePath(),  image, 50, 50);
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
				//							image.setImageBitmap(thumb);
				//						}

			} 
			else 
			{

				if(sub_ext.equalsIgnoreCase("png"))
				{
					image.setImageResource(R.drawable.png);
				}
				else if(sub_ext.equalsIgnoreCase("jpg"))
				{
					image.setImageResource(R.drawable.jpg);
				}
				else if(sub_ext.equalsIgnoreCase("jpeg"))
				{
					image.setImageResource(R.drawable.jpg);
				}
				else if(sub_ext.equalsIgnoreCase("gif"))
				{
					image.setImageResource(R.drawable.gif);
				}
				else if(sub_ext.equalsIgnoreCase("bmp"))
				{
					image.setImageResource(R.drawable.bmp);
				}
				else if(sub_ext.equalsIgnoreCase("webp"))
				{
					image.setImageResource(R.drawable.webp);
				}
				else if(sub_ext.equalsIgnoreCase("tiff"))
				{
					image.setImageResource(R.drawable.tiff);
				}
				else if(sub_ext.equalsIgnoreCase("psd"))
				{
					image.setImageResource(R.drawable.tiff);
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
			Bitmap bMap=null;
			try {

				bMap = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MediaStore.Video.Thumbnails.MICRO_KIND);

			} catch (Exception e) {
				// TODO: handle exception

			}


			if(bMap!=null)
			{
				image.setImageBitmap(bMap);
			}
			else
			{
				if(sub_ext.equalsIgnoreCase("avi"))
				{
					image.setImageResource(R.drawable.avi);
				}
				else if(sub_ext.equalsIgnoreCase("3gp"))
				{
					image.setImageResource(R.drawable.a_3gp);
				}
				else if(sub_ext.equalsIgnoreCase("mp4"))
				{
					image.setImageResource(R.drawable.mp4);
				}
				else if(sub_ext.equalsIgnoreCase("mov"))
				{
					image.setImageResource(R.drawable.mov);
				}
				else if(sub_ext.equalsIgnoreCase("m4v"))
				{
					image.setImageResource(R.drawable.m4v);
				}
				else if(sub_ext.equalsIgnoreCase("mkv"))
				{
					image.setImageResource(R.drawable.mkv);
				}
				else if(sub_ext.equalsIgnoreCase("webm"))
				{
					image.setImageResource(R.drawable.webm);
				}
				else if(sub_ext.equalsIgnoreCase("wmv"))
				{
					image.setImageResource(R.drawable.wmv);
				}


			}


		} 
		// text file
		else if(sub_ext.equalsIgnoreCase("txt")) {
			image.setImageResource(R.drawable.txt);

		}

		// zip folder
		else if (sub_ext.equalsIgnoreCase("zip")  || 
				sub_ext.equalsIgnoreCase("gzip") ||
				sub_ext.equalsIgnoreCase("gz")) {

			image.setImageResource(R.drawable.zip);

		} 
		// rar folder
		else if (sub_ext.equalsIgnoreCase("rar")) 
		{
			image.setImageResource(R.drawable.rar);

		}

		// system files
		else if(sub_ext.equalsIgnoreCase("conf"))
		{
			image.setImageResource(R.drawable.conf);

		}
		else if(sub_ext.equalsIgnoreCase("jar"))
		{

			image.setImageResource(R.drawable.jar);

		} 
		else if(sub_ext.equalsIgnoreCase("ttf"))
		{

			image.setImageResource(R.drawable.ttf);

		} 

		// ms office files
		else if(sub_ext.equalsIgnoreCase("doc") || 
				sub_ext.equalsIgnoreCase("docx")) {

			image.setImageResource(R.drawable.doc);

		} else if(sub_ext.equalsIgnoreCase("xls") || 
				sub_ext.equalsIgnoreCase("xlsx")) {

			image.setImageResource(R.drawable.xls);

		} else if(sub_ext.equalsIgnoreCase("ppt") ||
				sub_ext.equalsIgnoreCase("pptx")) {

			image.setImageResource(R.drawable.ppt);   	

		}
		// document files
		else if(sub_ext.equalsIgnoreCase("java")) {
			image.setImageResource(R.drawable.java);

		}
		else if(sub_ext.equalsIgnoreCase("class")) {
			image.setImageResource(R.drawable.a_class);

		}

		else if(sub_ext.equalsIgnoreCase("apk")) 
		{


			String filePath = file.getPath();
			image.setTag(filePath);
			drawableLoader.displayImage(filePath,activity, image);

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
			//						image.setImageBitmap(bmpIcon);
			//					}
			//					else
			//					{
			//						image.setImageResource(R.drawable.apk);
			//					}



		}

		else if(sub_ext.equalsIgnoreCase("css")) {
			image.setImageResource(R.drawable.css);  

		} 
		else if(sub_ext.equalsIgnoreCase("php")) {
			image.setImageResource(R.drawable.php);

		}
		else if(sub_ext.equalsIgnoreCase("dll")) {
			image.setImageResource(R.drawable.dll);  

		} 
		else if(sub_ext.equalsIgnoreCase("html")) {
			image.setImageResource(R.drawable.html);  

		}
		else if(sub_ext.equalsIgnoreCase("rtf")) {
			image.setImageResource(R.drawable.rtf);

		}
		else if(sub_ext.equalsIgnoreCase("xml")) {
			image.setImageResource(R.drawable.xml);

		}
		else if(sub_ext.equalsIgnoreCase("webdoc")) {
			image.setImageResource(R.drawable.webdoc);

		}
		else 
		{
			image.setImageResource(R.drawable.unknown);
		}






		return v;
	}


}


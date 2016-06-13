package com.filemanager.sdexplorer.data;

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

public enum FM_Video_BitmapManager {  
	INSTANCE;  

	private final Map<String, SoftReference<Bitmap>> cache;  
	public final ExecutorService pool;  
	private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());  
	private Bitmap placeholder;  

	FM_Video_BitmapManager() {  
		cache = new HashMap<String, SoftReference<Bitmap>>();  
		pool = Executors.newFixedThreadPool(5);  
	}  

	public void setPlaceholder(Bitmap bmp) {  
		placeholder = bmp;  
	}  

	public Bitmap getBitmapFromCache(String url) {  
		if (cache.containsKey(url)) {  
			return cache.get(url).get();  
		}  

		return null;  
	}  

	public void queueJob(final String url, final ImageView imageView,  
			final int width, final int height) {  
		/* Create handler in UI thread. */  

		final Handler handler = new Handler() {  
			@Override  
			public void handleMessage(Message msg) {  
				String tag = imageViews.get(imageView);  

				if (tag != null && tag.equals(url)) {  
					if (msg.obj != null) {  
						imageView.setImageBitmap((Bitmap) msg.obj);  
					} else {  
						imageView.setImageBitmap(placeholder);  
						Log.d(null, "fail " + url);  
					}  
				}  
			}  
		};  

		pool.submit(new Runnable() {  

			public void run() {  
				final Bitmap bmp = downloadBitmap(url, width, height);  
				Message message = Message.obtain();  
				message.obj = bmp;  
				Log.d(null, "Item downloaded: " + url);  

				handler.sendMessage(message);  
			}  
		});  
	}  

	public void loadBitmap(final String url, final ImageView imageView,  
			final int width, final int height) {  
		imageViews.put(imageView, url);  
		Bitmap bitmap = getBitmapFromCache(url);  

		// check in UI thread, so no concurrency issues  
		if (bitmap != null) {  
			Log.d(null, "Item loaded from cache: " + url);  
			imageView.setImageBitmap(bitmap);  
		} else {  
			imageView.setImageBitmap(placeholder);  
			queueJob(url, imageView, width, height);  
		}  
	}  

	private Bitmap downloadBitmap(String url, int width, int height) {  

		Log.e("url", url);
//		Bitmap bitmap = BitmapFactory.decodeFile(url) ; 
//		bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);  
		
		Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(url, MediaStore.Video.Thumbnails.MICRO_KIND);
		cache.put(url, new SoftReference<Bitmap>(bitmap));  
		
		return bitmap;  



	}  
}  
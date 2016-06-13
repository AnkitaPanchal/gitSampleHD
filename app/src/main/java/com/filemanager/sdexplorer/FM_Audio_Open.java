package com.filemanager.sdexplorer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.Timer;
import java.util.TimerTask;


public class FM_Audio_Open extends Activity implements View.OnClickListener{



	public  MediaPlayer mPlayer;
	private SeekBar seekBar;
	public ImageView btnStart,btnStop,btnPause;
	private Handler mHandler;
	private boolean isPlayiing=true;
	public Uri u;
	public int duration;
	private ImageButton imgbtninfo;
	private ImageView imgThumnail;
	private Bitmap bitmap;
	private TextView txtTotalTime,txtRemainTime;
	private String deviceId_md5;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.fm_audio_open);


		Intent i = getIntent();
		if(i == null) return;
		 u = i.getData();

		if(u == null) return;

		String	strfile_path = u.getPath();

		if(strfile_path == null) return;

		String FILENAME=strfile_path.substring(strfile_path.lastIndexOf('/')+1);
		TextView txtheadingname=(TextView)findViewById(R.id.txtheadingname);
		txtheadingname.setText(FILENAME);

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





		seekBar = (SeekBar) findViewById(R.id.seekbar);
		btnPause=(ImageView)findViewById(R.id.btnpause);
		btnStart=(ImageView)findViewById(R.id.btnstart);
		btnStop=(ImageView)findViewById(R.id.btnstop);
		imgbtninfo=(ImageButton)findViewById(R.id.imgbtninfo);
		imgThumnail=(ImageView)findViewById(R.id.imgthumnail);
		txtRemainTime=(TextView)findViewById(R.id.txtremaningtimeaudio);
		txtTotalTime=(TextView)findViewById(R.id.txttotaltimeaudio);

		btnPause.setOnClickListener(this);
		btnStart.setOnClickListener(this);
		btnStop.setOnClickListener(this);
		imgbtninfo.setOnClickListener(this);

		PlayAudio();
		mPlayer.start();
		duration=mPlayer.getDuration();
		try
		{
			seekBar.setMax(duration);
		}
		catch (Exception e)
		{

		}
		try
		{
			MediaMetadataRetriever mmr = new MediaMetadataRetriever();
			byte[] rawArt;
			BitmapFactory.Options bfo=new BitmapFactory.Options();
			mmr.setDataSource(getApplicationContext(), u);
			rawArt = mmr.getEmbeddedPicture();

			if (null != rawArt)
				bitmap = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, bfo);
				imgThumnail.setImageBitmap(bitmap);







		}
		catch (Exception e)
		{
			Log.e("exception",e.toString());
		}



	}
//	public String getThumbnailPath(Uri uri) {
//		String[] proj = { MediaStore.Images.Media.DATA };
//
//		// This method was deprecated in API level 11
//		// Cursor cursor = managedQuery(contentUri, proj, null, null, null);
//
//		CursorLoader cursorLoader = new CursorLoader(this, uri, proj, null, null, null);
//		Cursor cursor = cursorLoader.loadInBackground();
//
//		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
//
//
//		cursor.moveToFirst();
//		long imageId = cursor.getLong(column_index);
//		//cursor.close();
//		String result="";
//		cursor = MediaStore.Images.Thumbnails.queryMiniThumbnail(this.getContentResolver(), imageId,
//				MediaStore.Images.Thumbnails.MINI_KIND, null);
//		if (cursor != null && cursor.getCount() > 0) {
//			cursor.moveToFirst();
//			result = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA));
//			cursor.close();
//		}
//		return result;
//	}
//	public Bitmap getThumbnailBitmap(Uri uri){
//		String[] proj = { MediaStore.Images.Media.DATA };
//
//		// This method was deprecated in API level 11
//		// Cursor cursor = managedQuery(contentUri, proj, null, null, null);
//
//		CursorLoader cursorLoader = new CursorLoader(this, uri, proj, null, null, null);
//		Cursor cursor = cursorLoader.loadInBackground();
//
//		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
//
//		cursor.moveToFirst();
//		long imageId = cursor.getLong(column_index);
//		//cursor.close();
//
//		Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(
//				getContentResolver(), imageId,
//				MediaStore.Images.Thumbnails.MINI_KIND,
//				(BitmapFactory.Options) null );
//
//		return bitmap;
//	}



	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btnstart:
								btnPause.setVisibility(View.VISIBLE);
								btnStart.setVisibility(View.GONE);
								mPlayer.start();
								break;

			case R.id.btnstop:
								btnStart.setVisibility(View.VISIBLE);
								btnPause.setVisibility(View.GONE);
								mPlayer.stop();
								PlayAudio();
								break;

			case R.id.btnpause:
								btnStart.setVisibility(View.VISIBLE);
								btnPause.setVisibility(View.GONE);
								mPlayer.pause();
								break;

			case R.id.imgbtninfo:

								Intent authIntent = new Intent(FM_Audio_Open.this,AboutUS.class);
								startActivity(authIntent);
								break;
		}


	}

	void PlayAudio()
	{
		mPlayer=null;
		mPlayer = new MediaPlayer();
		mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);




		try
		{
			mPlayer.setDataSource(getApplicationContext(), u);

			final Handler mHandler = new Handler();
			Timer timer = new Timer();
			timer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {

							txtRemainTime.post(new Runnable()
							{
								@Override
								public void run()
								{
									try
									{
//											if(videoView.getCurrentPosition()!=0 )
//											{
										long time = mPlayer.getCurrentPosition();
										txtRemainTime.setText(""+getTimeString(time));
//											}

									}
									catch (Exception e)
									{}

								}
							});

						}
					});
				}
			}, 0, 1000);

		}
		catch (IllegalArgumentException e)
		{
			Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
		}
		catch (SecurityException e)
		{
			Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
		}
		catch (IllegalStateException e)
		{
			Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		try
		{
			mPlayer.prepare();
		}
		catch (IllegalStateException e)
		{
			Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
		}
		catch (IOException e)
		{
			Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
		}
		catch (Exception e)
		{
			Log.e("cant pay Audio",e.toString());
		}
//
		mHandler = new Handler();

		this.runOnUiThread(new Runnable()
		{

			@Override
			public void run()
			{
				if(mPlayer != null)
				{
					long timeTotal = mPlayer.getDuration();
					txtTotalTime.setText(""+getTimeString(timeTotal));
					int mCurrentPosition = mPlayer.getCurrentPosition() ;
					seekBar.setProgress((int)mCurrentPosition);

				}
				mHandler.postDelayed(this, 100);
			}
		});


		 Runnable updateSeekBarTime = new Runnable() {

			public void run() {
				 int timeElapsed = mPlayer.getCurrentPosition();
				seekBar.setProgress((int) timeElapsed);
				mHandler.postDelayed(this, 100);
			}
		};


		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
		{

			@Override
			public void onStopTrackingTouch(SeekBar seekBar)
			{

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if(mPlayer != null && fromUser){
					mPlayer.seekTo(progress);
					seekBar.setProgress(progress);

				}
			}
		});
	}

	@Override
	protected void onResume()
	{
		super.onResume();
//		mPlayer.stop();
//		mPlayer=null;
	}

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		mPlayer.stop();
		mPlayer=null;
	}

	private String getTimeString(long millis) {
		StringBuffer buf = new StringBuffer();

		int hours = (int) (millis / (1000 * 60 * 60));
		int minutes = (int) ((millis % (1000 * 60 * 60)) / (1000 * 60));
		int seconds = (int) (((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000);

		buf
				.append(String.format("%02d", hours))
				.append(":")
				.append(String.format("%02d", minutes))
				.append(":")
				.append(String.format("%02d", seconds));

		return buf.toString();
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

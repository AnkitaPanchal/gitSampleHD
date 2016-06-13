package com.filemanager.sdexplorer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class FM_Video_Open extends Activity implements View.OnClickListener,View.OnTouchListener{



	public org.videolan.libvlc.media.VideoView videoView;
	private SeekBar seekBar;
	public ImageView btnStart,btnStop,btnPause;
	private Handler mHandler;
	private boolean isPlayiing=true;
	public Uri u;
	private String	strfile_path;
	private ImageButton imgbtninfo;
	private boolean isStop=false;
	private FrameLayout fmLayout;
	private TextView txtTotalTime,txtRemainTime;

@Override
protected void onCreate(Bundle savedInstanceState)
{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.fm_video_open);


	restart();
	videoView.start();

		}

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{

		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if (event.getAction() == MotionEvent.ACTION_DOWN)
		{
			fmLayout.setVisibility(View.VISIBLE);
		}
		if (event.getAction() == MotionEvent.ACTION_UP)
		{
			Runnable mRunnable;
			Handler mHandler=new Handler();

			mRunnable=new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					fmLayout.setVisibility(View.GONE);
				}
			};
			mHandler.postDelayed(mRunnable,3*1000);


		}
		return super.onTouchEvent(event);
	}

	public void restart()
	{


		Intent i = getIntent();
		if(i == null) return;
		u = i.getData();

		if(u == null) return;

		strfile_path = u.getPath();
		if(strfile_path == null) return;

		String FILENAME=strfile_path.substring(strfile_path.lastIndexOf('/')+1);
		TextView txtheadingname=(TextView)findViewById(R.id.txtheadingname);
		txtheadingname.setText(FILENAME);

		fmLayout=(FrameLayout)findViewById(R.id.fmlayout);
		seekBar = (SeekBar) findViewById(R.id.seekbarvideo);
		btnPause=(ImageView)findViewById(R.id.btnpausevideo);
		btnStart=(ImageView)findViewById(R.id.btnstartvideo);
		btnStop=(ImageView)findViewById(R.id.btnstopvideo);
		videoView=(org.videolan.libvlc.media.VideoView)findViewById(R.id.video);
		imgbtninfo=(ImageButton)findViewById(R.id.imgbtninfo);
		txtRemainTime=(TextView)findViewById(R.id.txtremaningtime);
		txtTotalTime=(TextView)findViewById(R.id.txttotaltime);

		btnPause.setOnClickListener(this);
		btnStart.setOnClickListener(this);
		btnStop.setOnClickListener(this);
		imgbtninfo.setOnClickListener(this);

		PlayVideo();


		long duration=videoView.getDuration();
		try
		{
			seekBar.setMax((int)duration);

		}
		catch (Exception e)
		{

		}
	}

	@Override
public void onClick(View v)
		{
		switch (v.getId())
		{
		case R.id.btnstartvideo:

			if(isStop)
			{
				videoView.stopPlayback();
				videoView.resume();
				PlayVideo();
				isStop=false;
			}
			else
			{
				videoView.start();
			}


			btnPause.setVisibility(View.VISIBLE);
			btnStart.setVisibility(View.GONE);

		break;

		case R.id.btnstopvideo:
			isStop=true;
			videoView.stopPlayback();
			btnStart.setVisibility(View.VISIBLE);
			btnPause.setVisibility(View.GONE);



		break;

		case R.id.btnpausevideo:

			videoView.pause();
			btnStart.setVisibility(View.VISIBLE);
			btnPause.setVisibility(View.GONE);

		break;

			case R.id.imgbtninfo:

				Intent authIntent = new Intent(FM_Video_Open.this,AboutUS.class);
			startActivity(authIntent);
			break;
		}


		}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

	}

	void PlayVideo()
		{

		try
		{
			videoView.setVideoURI(u);



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
												long time = videoView.getCurrentPosition();
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
		catch (Exception e)
		{
		Log.e("cant pay Audio",e.toString());
		}

		mHandler = new Handler();
			this.runOnUiThread(new Runnable()
			{

				@Override
				public void run()
				{
					if(videoView != null)
					{
						long timeTotal = videoView.getDuration();
						txtTotalTime.setText(""+getTimeString(timeTotal));
						long mCurrentPosition = videoView.getCurrentPosition() ;
						seekBar.setProgress((int) mCurrentPosition);
						seekBar.setMax((int)videoView.getDuration());
					}
					mHandler.postDelayed(this, 1000);
				}
			});


			Runnable updateSeekBarTime = new Runnable() {

				public void run() {
					long timeElapsed = videoView.getCurrentPosition();
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
		if(videoView != null && fromUser){
			videoView.seekTo(progress);
			seekBar.setProgress(progress);
		}
		}
		});
		}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		videoView.stopPlayback();
		videoView=null;
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
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		if (null == videoView) return;
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
			getWindow().getDecorView().invalidate();

			float height = getWidthInPx(this);
			float width = getHeightInPx(this);


			videoView.getHolder().setFixedSize((int) height, (int) width);
			videoView.getLayoutParams().height = (int) width;
			videoView.getLayoutParams().width = (int) height;
		}
		else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
		{
			final WindowManager.LayoutParams attrs = getWindow().getAttributes();
			attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
			getWindow().setAttributes(attrs);
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
			float width = getWidthInPx(this);
			float height = dip2px(this, 200.f);
			videoView.getHolder().setFixedSize((int) height, (int) width);
			videoView.getLayoutParams().height = (int) height;
			videoView.getLayoutParams().width = (int) width;
		}
	}
	public static final float getHeightInPx(Context context) {
		final float height = context.getResources().getDisplayMetrics().heightPixels;
		return height;
	}

	public static final float getWidthInPx(Context context) {
		final float width = context.getResources().getDisplayMetrics().widthPixels;
		return width;
	}

	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
}


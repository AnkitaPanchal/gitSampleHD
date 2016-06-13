package com.filemanager.sdexplorer;

import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class FM_Doc_File_Open extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.fm_doc_file_open);

		Intent intdata = getIntent();
		Bundle bdldata=intdata.getExtras();
		String strfile_path=bdldata.getString("strfile_path");
		
		Log.e("strfile_path", strfile_path);
		
		WebView urlWebView = (WebView)findViewById(R.id.containWebView);
		urlWebView.setWebViewClient(new AppWebViewClients());
		urlWebView.getSettings().setJavaScriptEnabled(true);
		urlWebView.getSettings().setUseWideViewPort(true);
//		urlWebView.loadUrl("http://docs.google.com/gview?embedded=true&url="
//				+ "http://www.appaspect.com/hardik.doc"); 
//		urlWebView.loadUrl("http://docs.google.com/viewer?embedded=true&url="
//				+ getClass().getClassLoader().getResource(strfile_path)); 
		String str_url=Uri.parse(strfile_path).toString();
		Log.e("str_url", str_url+"");
		try 
		{
//			urlWebView.loadUrl("http://docs.google.com/viewer?embedded=true&url="
//				+ "file://"+strfile_path); 
			urlWebView.loadUrl("file://"+strfile_path);
//		URL urlstr= getClass().getClassLoader().getResource(strfile_path);
//		Log.e("urlstr", urlstr+"");
		}
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("Exception", e.toString());
		}

	}
	public class AppWebViewClients extends WebViewClient {



		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// TODO Auto-generated method stub
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			super.onPageFinished(view, url);

		}
	}
}

package com.filemanager.sdexplorer;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.filemanager.sdexplorer.data.FM_Bookmark_Data;
import com.filemanager.sdexplorer.data.FM__ConstantData;

public class FM_FileOperations extends Activity{
	
	private ArrayList<FM_Bookmark_Data> favuserlist;
	private boolean bdata;
	private Integer keyid;
	private String str_bookmark_name,srt_file_type;
	private String[] colsvalues;
	private Dialog dialog_box;
	private String srt_file_operations,srt_file_path;
	private Intent intdata;
	private Bundle bundle_data;
	private TextView main_label, search_label,input_name;
	private Button search_button, cancel_button ;
	private EditText input_editbox;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.operation_layout);
		
		
		intdata=getIntent();
		bundle_data= intdata.getExtras();
		
		srt_file_path= bundle_data.getString("srt_file_path");
		srt_file_operations= bundle_data.getString("operations");
		str_bookmark_name= bundle_data.getString("srt_file_name");
		srt_file_type= bundle_data.getString("srt_file_type");
		
		Log.e("srt_file_type", srt_file_type);
		
		 main_label = (TextView)findViewById(R.id.main_label);
		 search_label = (TextView)findViewById(R.id.input_label);
		 search_button = (Button)findViewById(R.id.input_create_b);
		 cancel_button = (Button)findViewById(R.id.input_cancel_b);
		input_editbox = (EditText)findViewById(R.id.edtboxinput);
		input_editbox.setText(str_bookmark_name);
		input_name = (TextView)findViewById(R.id.input_name);
		
		if(srt_file_operations.equalsIgnoreCase("bookmark"))
		{
			main_label.setText(getResources().getString(R.string.Add_Bookmark));
			
			
			input_name.setText(getResources().getString(R.string.Name));
			
			search_label.setText(srt_file_path);
			
					
		
			search_button.setText(getResources().getString(R.string.Bookmark));
			
			search_button.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					
					str_bookmark_name = input_editbox.getText().toString();
					
					str_bookmark_name=str_bookmark_name.trim();
					
					Log.e("temp", str_bookmark_name+"");
					
					if(str_bookmark_name==null)
					{
						Toast.makeText(FM_FileOperations.this, getResources().getString(R.string.Please_enter_the_name), Toast.LENGTH_SHORT).show();
					}
					else if(str_bookmark_name.equalsIgnoreCase(""))
					{
						Toast.makeText(FM_FileOperations.this, getResources().getString(R.string.Please_enter_the_name), Toast.LENGTH_SHORT).show();
					}
					else if(str_bookmark_name.equalsIgnoreCase(" "))
					{
						Toast.makeText(FM_FileOperations.this, getResources().getString(R.string.Please_enter_the_name), Toast.LENGTH_SHORT).show();
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
								
								if(favuserlist.get(j).getBookmark_path().equalsIgnoreCase(srt_file_path))
								{
									
										bdata=true ;
										keyid=Integer.valueOf(favuserlist.get(j).getId());
										colsvalues= new String[]{String.valueOf(favuserlist.get(j).getId()),
												favuserlist.get(j).getBookmark_name(),
												favuserlist.get(j).getBookmark_path(),
												String.valueOf(favuserlist.get(j).isBookmark_flag()),
												srt_file_type};
									
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
								
								Toast.makeText(FM_FileOperations.this, getResources().getString(R.string.Bookmark_Already_done), Toast.LENGTH_SHORT).show();
							}
							else
							{
								FM__ConstantData.sqlfm_bookmark.Write();
								
								FM__ConstantData.db.execSQL("INSERT INTO "+FM__ConstantData.Table_Bookmark +" VALUES(NULL,'"+str_bookmark_name+"','"+srt_file_path+"','"+false+"','"+srt_file_type+"');");
								FM__ConstantData.db.close();
								Toast.makeText(FM_FileOperations.this, getResources().getString(R.string.Bookmark_Added), Toast.LENGTH_SHORT).show();
								
							}
							
							finish();
					}
					
					
					
					
				}
			});
			
			cancel_button.setOnClickListener(new OnClickListener() {
				public void onClick(View v) { 
					
					finish();
					}
			});
		}
		
		
		
		
		
	}

}

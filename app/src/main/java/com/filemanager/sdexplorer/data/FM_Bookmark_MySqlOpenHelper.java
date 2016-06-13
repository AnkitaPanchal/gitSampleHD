package com.filemanager.sdexplorer.data;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class FM_Bookmark_MySqlOpenHelper extends SQLiteOpenHelper{

	private Context context;
	private String database_name;
	private int database_version;
	private	boolean bdata;
	private ArrayList<FM_Bookmark_Data> QueList = new ArrayList<FM_Bookmark_Data>();
	
	public FM_Bookmark_MySqlOpenHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		this.context = context;
		this.database_name = name;
		this.database_version = version;
	}
	public FM_Bookmark_MySqlOpenHelper Read(){
		FM__ConstantData.sqlfm_bookmark =  new FM_Bookmark_MySqlOpenHelper(context, database_name, null, database_version);
		FM__ConstantData.db = FM__ConstantData.sqlfm_bookmark.getWritableDatabase();

		return this;
	}
	public FM_Bookmark_MySqlOpenHelper Write(){
		FM__ConstantData.sqlfm_bookmark =  new FM_Bookmark_MySqlOpenHelper(context, database_name, null, database_version);
		FM__ConstantData.db = FM__ConstantData.sqlfm_bookmark.getReadableDatabase();

		return this;
	}
	@Override
	public void onOpen(SQLiteDatabase db) {
	    super.onOpen(db);
	    if (!db.isReadOnly()) {
	        // Enable foreign key constraints
	        db.execSQL("PRAGMA foreign_keys=ON;");
	    }
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		FM__ConstantData.db = db;
	}
	public void create_table(String sqlquery){
		FM__ConstantData.db.execSQL(sqlquery);
	}

	//INSERT DataBase
	public void insert(String table_name,String[] cols ,String[] values){

		ContentValues val = new ContentValues();

		for (int j = 0; j < values.length; j++) {
			val.put(cols[j], values[j]);	
		}			
		FM__ConstantData.db.insert(table_name, null, val);
	}

	//UPDATE DataBase
	public void update(String table_name,String cols[], String values[], String where, String whereArgs[]){

		ContentValues val = new ContentValues();

		for (int j = 0; j < values.length; j++) {
			val.put(cols[j], values[j]);	
		}

		FM__ConstantData.db.update(table_name, val, where, whereArgs);
	}

	public void delete(String table_name,String where,String whereArgs[])
	{
		FM__ConstantData.db.delete(table_name, where, whereArgs);

		//db.delete(employeeTable,colID+"=?", new String [] {String.valueOf(emp.getID())});


	}

	//SELECT DataBase

	//	public ArrayList<Object> select(boolean isDistinct, String table_name, String[] cols, String where, String selectionArgs[],String groupBy, String having,
	//			String orderby,String limit){
	//	ArrayList<Object> _list = new ArrayList<Object>();
	//	ArrayList<Object> _data = null;
	//	Cursor c = Constant.db.query(isDistinct, table_name, cols, where, selectionArgs, groupBy, having, orderby, limit);
	//		c.moveToFirst();
	//		if(c != null){
	//			if(c.getCount() >0){
	//				while (!c.isAfterLast()) {
	//					_data = new ArrayList<Object>();
	//					int col_count = c.getColumnCount();	
	//					
	//					for (int i = 0; i < col_count; i++) {						
	//						_data.add(c.getString(i));												
	//					}
	//					c.moveToNext();
	//					_list.add(_data);
	//					
	//				}
	//				
	//			}
	//			
	//		}
	//	
	//	return _list;
	//}

	public ArrayList<FM_Bookmark_Data> getAllBookmarkListData() {

		// Select All Query
		String selectQuery = "SELECT  * FROM "+FM__ConstantData.Table_Bookmark ;

		FM__ConstantData.db = this.getWritableDatabase();
		Cursor cursor = FM__ConstantData.db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				FM_Bookmark_Data _UserClass = new FM_Bookmark_Data();

				// _UserClass =new UserClass();
				_UserClass.setId(Integer.parseInt(cursor.getString(0)));
				_UserClass.setBookmark_name(cursor.getString(1));
				_UserClass.setBookmark_path(cursor.getString(2));
				_UserClass.setBookmark_flag(Boolean.getBoolean(cursor.getString(3)));
				_UserClass.setBookmark_file_type(cursor.getString(4));
				
				
				
				QueList.add(_UserClass);
			} while (cursor.moveToNext());
		}
		cursor.close();
		FM__ConstantData.db.close();
		// return user list
		return QueList;


	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + FM__ConstantData.Table_Bookmark);

		// Create tables again
		onCreate(db);
		db.close();

	}
	

	

}


//http://www.androidhive.info/2011/11/android-sqlite-database-tutorial/

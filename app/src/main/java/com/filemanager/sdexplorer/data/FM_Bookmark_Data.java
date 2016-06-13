package com.filemanager.sdexplorer.data;

public class FM_Bookmark_Data {
	
	private int id;
	private String bookmark_name;
	private String bookmark_path;
	private boolean bookmark_flag;
	private String bookmark_file_type;
	
	public String getBookmark_file_type() {
		return bookmark_file_type;
	}
	public void setBookmark_file_type(String bookmark_file_type) {
		this.bookmark_file_type = bookmark_file_type;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getBookmark_name() {
		return bookmark_name;
	}
	public void setBookmark_name(String bookmark_name) {
		this.bookmark_name = bookmark_name;
	}
	public String getBookmark_path() {
		return bookmark_path;
	}
	public void setBookmark_path(String bookmark_path) {
		this.bookmark_path = bookmark_path;
	}
	public boolean isBookmark_flag() {
		return bookmark_flag;
	}
	public void setBookmark_flag(boolean bookmark_flag) {
		this.bookmark_flag = bookmark_flag;
	}
	
	

}

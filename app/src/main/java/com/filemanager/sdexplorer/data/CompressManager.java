package com.filemanager.sdexplorer.data;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.filemanager.sdexplorer.File_Manager_MainActivity;
import com.filemanager.sdexplorer.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CompressManager {
    /**
     * TAG for log messages.
     */
    static final String TAG = "CompressManager";

    private static final int BUFFER_SIZE = 1024;
    private Activity activity;
    private ProgressDialog progressDialog;
    private int fileCount;
    private String fileOut;
    private  boolean flag_extract;

    public CompressManager(File_Manager_MainActivity activity) {
        this.activity = activity;
    }

    public CompressManager(Context dialogContext) {
		// TODO Auto-generated constructor stub
    	this.activity = (Activity)dialogContext;
	}
    public CompressManager(Context dialogContext,boolean flag) {
		// TODO Auto-generated constructor stub
		
		this.activity = (Activity)dialogContext;
		this.flag_extract=flag;
	}
	public void compress(File f, String out) {
        List <File>list = new ArrayList<File>();
        list.add(f);
        compress(list, out);
    }    

    public void compress(List<File> list, String out) {
        if (list.isEmpty()){
            Log.v(TAG, "couldn't compress empty file list");
            return;
        }
        this.fileOut = list.get(0).getParent()+File.separator+out;
        fileCount=0;
        for (File f: list){
            fileCount += FileUtils.getFileCount(f);
        }
        new CompressTask().execute(list);
    }

    private class CompressTask extends AsyncTask<Object, Void, Integer> {
        private static final int success = 0;
        private static final int error = 1;
        private ZipOutputStream zos;

        /**
         * count of compressed file to update the progress bar
         */
        private int isCompressed = 0;

        /**
         * Recursively compress file or directory
         * @returns 0 if successful, error value otherwise.
         */
        private void compressFile(File file, String path) throws IOException {
            if (!file.isDirectory()){
                byte[] buf = new byte[BUFFER_SIZE];
                int len;
                FileInputStream in = new FileInputStream(file);
                zos.putNextEntry(new ZipEntry(path + "/" + file.getName()));
                while ((len = in.read(buf)) > 0) {
                    zos.write(buf, 0, len);
                }
                in.close();
                return;
            }
            if (file.list() == null){
                return;
            }
            for (String fileName: file.list()){
                File f = new File(file.getAbsolutePath()+File.separator+fileName);
                compressFile(f, path + File.separator + file.getName());
                try{
                isCompressed++;
                progressDialog.setProgress((isCompressed * 100)/ fileCount);
                }catch (Exception e) {
					// TODO: handle exception
				}
            }
        }

        @Override
        protected void onPreExecute() {
            FileOutputStream out = null;
            progressDialog = new ProgressDialog(activity);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMessage(activity.getResources().getString(R.string.compressing));
            progressDialog.show();
            progressDialog.setProgress(0);
            try {
                out = new FileOutputStream(new File(fileOut));
                zos = new ZipOutputStream(new BufferedOutputStream(out));
            } catch (FileNotFoundException e) {
                Log.e(TAG, "error while creating ZipOutputStream");
            }
        }

        @Override
        protected Integer doInBackground(Object... params) {
            if (zos == null){
                return error;
            }
            List<File> list = (List<File>) params[0]; 
            for (File file:list){
                try {
                    compressFile(file, "");
                    try{
                    isCompressed++;
                    progressDialog.setProgress((isCompressed * 100)/ fileCount);
                    }
                    catch (Exception e) {
						// TODO: handle exception
					}
                } catch (IOException e) {
                    Log.e(TAG, "Error while compressing", e);
                    return error;
                }
            }
            return success;
        }

        @Override
        protected void onPostExecute(Integer result) {
            try {
            	
            	if(zos!=null)
            	{
            		zos.flush();
                    zos.close();
            	}
                
            } catch (IOException e) {
                Log.e(TAG, "error while closing zos", e);
            }
            progressDialog.cancel();
            if (result == error){
                Toast.makeText(activity, activity.getResources().getString(R.string.compressing_error), Toast.LENGTH_SHORT).show();
            } else if (result == success){
                Toast.makeText(activity, activity.getResources().getString(R.string.compressing_success), Toast.LENGTH_SHORT).show();
            }
            File_Manager_MainActivity.mHandler.updateDirectory(File_Manager_MainActivity.mFileMag.getrefresh(File_Manager_MainActivity.mFileMag.getCurrentDir())) ;
           
//            if(flag_extract)
//            {
//            	activity.finish();
//            }
           // activity.refreshList();
        }
    }
}

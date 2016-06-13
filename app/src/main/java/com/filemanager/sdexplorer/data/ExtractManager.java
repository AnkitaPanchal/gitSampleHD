package com.filemanager.sdexplorer.data;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.filemanager.sdexplorer.FM_FileOperations_List;
import com.filemanager.sdexplorer.File_Manager_MainActivity;
import com.filemanager.sdexplorer.R;

public class ExtractManager {
    /**
     * TAG for log messages.
     */
    static final String TAG = "ExtractManager";
 
    private static final int BUFFER_SIZE = 1024;
    
    private Activity activity;
    private ProgressDialog progressDialog;
    private  boolean flag_extract;
    public ExtractManager(File_Manager_MainActivity activity) {
        this.activity = activity;
    }

    public ExtractManager(FM_FileOperations_List fm_FileOperations) {
		// TODO Auto-generated constructor stub
    	 this.activity = fm_FileOperations;
	}

	public ExtractManager(Context dialogContext) {
		// TODO Auto-generated constructor stub
		
		this.activity = (Activity)dialogContext;
	}

	public ExtractManager(Context dialogContext,boolean flag) {
		// TODO Auto-generated constructor stub
		
		this.activity = (Activity)dialogContext;
		this.flag_extract=flag;
	}
	public void extract(File f, String destinationPath) {
            new ExtractTask().execute(f, destinationPath);
    }
	
    private class ExtractTask extends AsyncTask<Object, Void, Integer> {
        private static final int success = 0;
        private static final int error = 1;

        /**
         * count of extracted files to update the progress bar
         */
        private int isExtracted = 0;

        /**
         * Recursively extract file or directory
         */
        public boolean extract(File archive, String destinationPath) {
            try {
                ZipFile zipfile = new ZipFile(archive);
                int fileCount = zipfile.size();
                for (Enumeration e = zipfile.entries(); e.hasMoreElements();) {
                    ZipEntry entry = (ZipEntry) e.nextElement();
                    unzipEntry(zipfile, entry, destinationPath);
                    isExtracted++;
                    progressDialog.setProgress((isExtracted * 100)/ fileCount);
                }
                return true;
            } catch (Exception e) {
                Log.e(TAG, "Error while extracting file " + archive, e);
                return false;
            }
        }        
        
        private void createDir(File dir) {
            if (dir.exists()) {
                return;
            }
            Log.i(TAG, "Creating dir " + dir.getName());
            if (!dir.mkdirs()) {
                throw new RuntimeException("Can not create dir " + dir);
            }
        }        
        
        private void unzipEntry(ZipFile zipfile, ZipEntry entry,
                                String outputDir) throws IOException {
            if (entry.isDirectory()) {
                createDir(new File(outputDir, entry.getName()));
                return;
            }
            File outputFile = new File(outputDir, entry.getName());
            if (!outputFile.getParentFile().exists()) {
                createDir(outputFile.getParentFile());
            }
            Log.i(TAG, "Extracting: " + entry);
            BufferedInputStream inputStream = new BufferedInputStream(zipfile.getInputStream(entry));
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
            try {
                int len;
                byte buf[] = new byte[BUFFER_SIZE];
                while ((len = inputStream.read(buf)) > 0) {
                    outputStream.write(buf, 0, len);
                }
            } finally {
                outputStream.close();
                inputStream.close();
            }
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMessage(activity.getResources().getString(R.string.extracting));
            progressDialog.show();
            progressDialog.setProgress(0);
            isExtracted = 0;
        }

        @Override
        protected Integer doInBackground(Object... params) {
            File f= (File) params[0];
            String destination = (String) params[1];
            boolean result = extract(f, destination);
            return result ? success : error;
        }

        @Override
        protected void onPostExecute(Integer result) {
           
            if (result == error)
            {
                Toast.makeText(activity, activity.getResources().getString(R.string.extracting_error), Toast.LENGTH_SHORT).show();
            } else if (result == success)
            {
                Toast.makeText(activity, activity.getResources().getString(R.string.extracting_success), Toast.LENGTH_SHORT).show();
            }
            File_Manager_MainActivity.mHandler.updateDirectory(File_Manager_MainActivity.mFileMag.getrefresh(File_Manager_MainActivity.mFileMag.getCurrentDir())) ;
            
           
            progressDialog.cancel();
            
//            if(flag_extract)
//            {
//            	activity.finish();
//            }
            
            //activity.refreshList();
        }
    }
}

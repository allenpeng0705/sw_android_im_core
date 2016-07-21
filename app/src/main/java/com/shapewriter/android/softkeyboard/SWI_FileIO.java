package com.shapewriter.android.softkeyboard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.Context;
import android.util.Log;

public class SWI_FileIO {
	
	/**
	 * Write the resource named by rawResourceId to file "/data/data/package name/files/filename",
	 * If the file exist, it is overwritten.
	 * @param context: globe context
	 * @param filename: file name
	 * @param rawResourceId: resource id saved in /res/raw
	 */
	public static void write(Context context, String filename, int rawResourceId){
		try {
			FileOutputStream out = context.openFileOutput(filename, Context.MODE_WORLD_READABLE);
			InputStream stream = context.getResources().openRawResource(rawResourceId);
			int length = stream.available();
			byte[] data = new byte[length];
			stream.read(data, 0, length);
			out.write(data);
			out.close();
		} catch (Exception e) {
			Log.e("chen", "Exception in write in SWI_FileIO. " + e.toString() + " writing " + filename);
		}
	}
	
	public static InputStream readAsInputStream(String directory, String filename){
		File file = new File(directory, filename);
		if (file.exists()){
			try {
				return new FileInputStream(file);
			} catch (FileNotFoundException e) {
				Log.e("chen", "FileNotFoundException in readAsInputStream in SWI_FileIO. " + e.toString()
						+ ", directory = " + directory + ", file name = " + filename);
			}
		}
		return null;
	}
	
	public static byte [] readAsByteArray(String directory, String filename){
		File file = new File(directory, filename);
		if (file.exists()){
			try {
				FileInputStream in = new FileInputStream(file);
				int length = (int)in.available();
	        	byte[] buf = new byte[length];
	        	in.read(buf, 0, length);
	        	in.close();
	        	return buf;
			} catch (Exception e) {
				Log.e("chen", "Exception in readAsByteArray in SWI_FileIO. " + e.toString()
						+ ", directory = " + directory + ", file name = " + filename);
			}
		}
		return null;
	}
	
	/**
	 * Read four files data as one byte array. The data of file1 is in former, and
	 * the data of file4 is in latter. 
	 * @param directory: The directory of file1, file2, file3 and file4 saved, 
	 * 					 files should be saved in same directory.
	 * @param filename1: The name of file1.
	 * @param filename2: The name of file2.
	 * @param filename3: The name of file3.
	 * @param filename4: The name of file4.
	 * 
	 * @return The content of file1, file2, file3 and file4.
	 */
	public static byte [] readAsByteArray(String directory, String filename1, String filename2,
			String filename3, String filename4){
		File file1 = new File(directory, filename1);
		File file2 = new File(directory, filename2);
		File file3 = new File(directory, filename3);
		File file4 = new File(directory, filename4);
		
		if(file1.exists() && file2.exists() && file3.exists() && file4.exists()){
			try {
				FileInputStream in1 = new FileInputStream(file1);
				int length1 = (int)in1.available();
				FileInputStream in2 = new FileInputStream(file2);
				int length2 = (int)in2.available();
				FileInputStream in3 = new FileInputStream(file3);
				int length3 = (int)in3.available();
				FileInputStream in4 = new FileInputStream(file4);
				int length4 = (int)in4.available();
				
				
				byte[] buf = new byte[length1 + length2 + length3 + length4];
				in1.read(buf, 0, length1);
				in2.read(buf, length1, length2);
				in3.read(buf, length1 + length2, length3);
				in4.read(buf, length1 + length2 + length3, length4);
	        	in1.close();
	        	in2.close();
	        	in3.close();
	        	in4.close();
	        	return buf;
			}
	        catch (Exception e) {
				Log.e("chen", "Exception in readAsByteArray in SWI_FileIO. " + e.toString()
						+ ", directory = " + directory + ", file1 name = " + filename1 + 
						",file2 name = " + filename2);
			}
		}
		return null;
	}
}

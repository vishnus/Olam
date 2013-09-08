/*
	Vishnu S - http://vishnus.name
	Last updated: September 2013
	
	Class to do database operations
	
	License: MIT License
*/

package com.olam;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper{

	private static String DB_PATH = "/data/data/com.olam/databases/";
	private static String DB_NAME = "enml.db";
	
	private SQLiteDatabase enmlDb;
	
	private final Context olamContext;
	
	private static final String TAG = "OLAM LOGGER in DBH";
	
	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, 1);
		this.olamContext = context;
		Log.v(TAG, "DB Helper created");
	}
	
	public void createDatabase() throws IOException {
		
		boolean dbExist = checkDatabase();
		
		if(dbExist) { 
			Log.v(TAG, "Database Exists");
		}
		else {
			this.getReadableDatabase();
			try {
				Log.v(TAG, "Going to copy the database");
				copyDataBase();
				Log.v(TAG, "Copyied the database");
			}
			catch (IOException e) {
				Log.v(TAG, "ERROR COPYING DB");
				throw new Error("Error Copying Database!");
			}
		}
		
	}

	private boolean checkDatabase() {
		SQLiteDatabase checkDb = null;
		Log.v(TAG, "Checking if DB exists");
		try {
			String myPath = DB_PATH+DB_NAME;
			checkDb = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);			
		}
		catch(SQLiteException e) {
			
		}
		
		if(checkDb != null) {
			checkDb.close();
		}
		
		return checkDb != null ? true : false;
				
	}
	
	private void copyDataBase() throws IOException{
		
		String  outFileName = DB_PATH+DB_NAME;
		
		InputStream myInput = olamContext.getAssets().open(DB_NAME);
		OutputStream myOutput = new FileOutputStream(outFileName);
		
		byte[] buffer = new byte[1024];
		int length;
		
		while((length = myInput.read(buffer))>0) {
			myOutput.write(buffer, 0, length);
		}
		
		myOutput.flush();
		myOutput.close();
		myInput.close();		
	}
	
	public void openDatabase() {
		String myPath = DB_PATH + DB_NAME; 
		enmlDb = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
		Log.v(TAG, "Database Opened");
	}
	
	public Map<String, String> getSimilarStems(String stem) {
		Map<String, String> results = new LinkedHashMap<String, String>();
		
		Cursor res = enmlDb.rawQuery("SELECT word, _id FROM words_en WHERE stems LIKE '%"+stem+" %' OR stems LIKE '"+stem+"' ORDER BY LENGTH(word) LIMIT 10", null);

		while(res.moveToNext()) {
			String id = res.getString(res.getColumnIndex("_id"));
			String word = res.getString(res.getColumnIndex("word"));			
			results.put(id, word);			
		}
		return results;
	}
	
	public Map<String, ArrayList<Map<String, String>>> getDefinitions(String[] ids) {
		Cursor res = enmlDb.rawQuery("SELECT word, id_en,type FROM relations_en_ml LEFT JOIN words_ml ON (words_ml._id = relations_en_ml.id_ml) WHERE id_en IN (" + TextUtils.join(",", ids) + ") ORDER BY LENGTH(word)", null);


		Map<String, ArrayList<Map<String, String>>> results = new HashMap<String, ArrayList<Map<String, String>>>();

		while(res.moveToNext()) {
			String id_en = res.getString(res.getColumnIndex("id_en"));
			if(!results.containsKey( id_en )) {
				results.put(id_en, new ArrayList<Map<String, String>>());
			}
			
			Map<String, String> item = new HashMap<String, String>();
			
			item.put(res.getString(res.getColumnIndex("word")), res.getString(res.getColumnIndex("type")));
			

			results.get(id_en).add(item);
		}
		Log.v(TAG, "Search Returned");
		return results;	
	}
	
	@Override
	public synchronized void close() {
		if(enmlDb != null) {
			enmlDb.close();
			super.close();
		}
	}
	
	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

}

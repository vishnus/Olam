/*
	Vishnu S - http://vishnus.name
	Last updated: September 2013
	
	Main Activity class
	
	License: MIT License
*/

package com.olam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.olam.R.drawable;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView.OnEditorActionListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.PopupWindow;
import android.widget.TextView;

public class MainSearch extends Activity {
	
	private static final String TAG = "OLAM LOGGER in MAIN";
	
	ProgressDialog pDialog;
	LinearLayout resLayout;
	LinearLayout layout;
	
    
	LayoutParams params;
	PopupWindow popUp;
	
	ImageView logo;
	TextView appDesc;
	TextView appDescEn;	
	TextView aboutTitle;
	TextView olamAuthor;
	TextView just_a_new_line;
	TextView olamUrl;
	
	boolean click = true;
	
	EditText editText;
	
	Typeface tf;
	DatabaseHelper dbHelper;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_search);
		
		resLayout = (LinearLayout) findViewById(R.id.resultsLayout);
		tf  = Typeface.createFromAsset(getBaseContext().getAssets(),"Meera.ttf");
		dbHelper = new DatabaseHelper(this);		
		ImageView button = (ImageView) findViewById(R.id.imageButton);
		
		
		//Creating Olam database if not exist from DB file in Assets. 
		try {
			Log.v(TAG, "Copying the database");
			dbHelper.createDatabase();			
		}
		catch (IOException ioe) {
			Log.v(TAG, "Unable to create database");
		}
		
		//Opening the database for operation with search button
		try {
			dbHelper.openDatabase();
		
			button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					editText = (EditText) findViewById(R.id.editText1);
					String searchString = editText.getText().toString();
					
					if(searchString != null && !searchString.isEmpty()) {	
						resLayout.removeAllViews();					
						new doSearch().execute();
					}
				}
			});
			
		}
		catch (SQLiteException sqle) {
			throw sqle;
		}	
		
		//Opening the database for operation with keyboard GO button
		try {
			dbHelper.openDatabase();
			editText = (EditText) findViewById(R.id.editText1);
			editText.setOnEditorActionListener(new OnEditorActionListener() {        
			    @Override
			    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			        if(actionId==EditorInfo.IME_ACTION_DONE){
			            //do something
			        	String searchString = editText.getText().toString();
			        	
						if(searchString != null && !searchString.isEmpty()) {	
							resLayout.removeAllViews();					
							new doSearch().execute();
						}
			        }
			    return false;
			    }
			});
		}
		catch (SQLiteException sqle) {
			throw sqle;
		}
		
		
		// About info popup 
		layout = new LinearLayout(this);
		logo = new ImageView(this);
		popUp = new PopupWindow(this);
		aboutTitle = new TextView(this);
		appDesc = new TextView(this);
		appDescEn = new TextView(this);
        olamAuthor = new TextView(this);
        just_a_new_line = new TextView(this);
        olamUrl = new TextView(this);
        
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(Color.WHITE);
        
        aboutTitle.setText(R.string.title);        
        aboutTitle.setTextSize(30);
        aboutTitle.setTypeface(tf,Typeface.BOLD);        
        aboutTitle.setGravity(Gravity.CENTER_HORIZONTAL);
        
        appDesc.setText(R.string.app_description);        
        appDesc.setTextSize(15);
        appDesc.setTypeface(tf);        
        appDesc.setGravity(Gravity.CENTER_HORIZONTAL);
                
        appDescEn.setText(R.string.app_description_english);        
        appDescEn.setTextSize(11);             
        appDescEn.setGravity(Gravity.CENTER_HORIZONTAL);
        
        olamAuthor.setText(Html.fromHtml(getResources().getString(R.string.olam_author)));        
        olamAuthor.setTextSize(11);                
        olamAuthor.setMovementMethod(LinkMovementMethod.getInstance());
        olamAuthor.setGravity(Gravity.CENTER_HORIZONTAL);
        
        just_a_new_line.setText(R.string.just_a_new_line);        
        just_a_new_line.setTextSize(10);                
        just_a_new_line.setMovementMethod(LinkMovementMethod.getInstance());
        just_a_new_line.setGravity(Gravity.LEFT);
        
        olamUrl.setText(Html.fromHtml(getResources().getString(R.string.olam_url)));        
        olamUrl.setTextSize(13);        
        olamUrl.setMovementMethod(LinkMovementMethod.getInstance());
        olamUrl.setGravity(Gravity.CENTER_HORIZONTAL);
        
        layout.addView(aboutTitle);
        layout.addView(appDesc);
        layout.addView(appDescEn);
        layout.addView(olamAuthor);
        layout.addView(just_a_new_line);
        layout.addView(olamUrl);
        layout.setBackgroundResource(drawable.popupbox);
        
        popUp.setBackgroundDrawable(new BitmapDrawable());        
        popUp.setOutsideTouchable(true);       
        popUp.setContentView(layout);
        
		ImageView infoButton = (ImageView) findViewById(R.id.infoButton);
		infoButton.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				if(click){
					popUp.showAtLocation(v, Gravity.CENTER, 10, 10);
					popUp.update(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					click = false;
				}
				else {
					popUp.dismiss();
					click = true;
				}
				                  			
			}
		});
		
		//about window end
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    // Override back button
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	        if (popUp.isShowing()) {
	            popUp.dismiss();
	            return false;
	        }
	    }
	    return super.onKeyDown(keyCode, event);
	} 
	
	//searching done as an async task
	
	class doSearch extends AsyncTask<String, String, String> {

		String searchString;
		Map<String, String> matches;
		Map<String, ArrayList<Map<String, String>>> results = new HashMap<String, ArrayList<Map<String, String>>>();
		
		@Override
	    protected void onPreExecute() {
			super.onPreExecute();
	        pDialog = new ProgressDialog(MainSearch.this);
	        pDialog.setMessage(Html.fromHtml("<b>Searching..</b>"));
	        pDialog.setIndeterminate(false);
	        pDialog.setCancelable(false);
	        pDialog.show();
		}
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			EditText editText = (EditText) findViewById(R.id.editText1);
			searchString = editText.getText().toString();
			
			Log.v(TAG, searchString);
			if(searchString != null && !searchString.isEmpty()) {				
				Stemmer stem = new Stemmer();		    	
		    	String stems = stem.completeStem(searchString.toLowerCase());
		    	Log.v("Stemmer Tag", stems);		    	
		    	matches = dbHelper.getSimilarStems(stems);		    	
		    	
		    	// 
		    	if(!matches.isEmpty()) {
		    		
		    		String[] resultIDs = new String[matches.size()];
		    		int i=0;
		    		   						
			    	for (Map.Entry<String, String> entry : matches.entrySet()) {		    	    	
			    	    	String resultID = entry.getKey();
			    	        String word = entry.getValue();		    	        
			    	        resultIDs[i] = resultID;
			    	        i++;			    	        
			    	}
			    	
			    	results = dbHelper.getDefinitions(resultIDs);    
			    	
		    		Log.v("List Map Tag", "List Map created");

		    		//results = dbHelper.searchDatabase(stems);				
		    		Log.v(TAG, "Search Results Got back");				
		    	}
			}
			return null;
		}
		
	    @Override
		protected void onPostExecute(String file_url) {
	    	pDialog.dismiss();	    
	    	Log.v(TAG, "Dismissed Progress Dialog");
	    	renderResults();
	    }
	    
	    protected void renderResults(){
	    	
	    	int i=1;
	    	
	    	if(matches.isEmpty()) {
	    		TextView sorryMsg = new TextView(MainSearch.this);
	    		TextView addUrl = new TextView(MainSearch.this);
	    		
	    		sorryMsg.setText(R.string.sorry_not_found);
	    		sorryMsg.setPadding(30, 15, 30, 0);
	    		sorryMsg.setGravity(Gravity.CENTER);
	    		sorryMsg.setTypeface(tf, Typeface.BOLD);
	    		sorryMsg.setTextSize(18);
				resLayout.addView(sorryMsg);
				
				addUrl.setText(Html.fromHtml("<a href=\"http://olam.in/Add/\">http://olam.in/Add/</a>"));
				addUrl.setPadding(30, 15, 30, 0);
				addUrl.setGravity(Gravity.CENTER);
				addUrl.setTypeface(null, Typeface.BOLD);
				addUrl.setTextSize(18);
				addUrl.setMovementMethod(LinkMovementMethod.getInstance());
				resLayout.addView(addUrl);
	    	}
	    	else {
	    			
		    	    for (Map.Entry<String, String> entry : matches.entrySet()) {	    	        
		    	    	String wordID = entry.getKey();
		    	        String word = entry.getValue();
		    	        
		    	        TextView title = new TextView(MainSearch.this);	    	
		    			title.setText(i+". "+word);			
		    			title.setPadding(50, 20, 0, 0);
		    			title.setTextSize(16);			
		    			title.setTypeface(null, Typeface.BOLD);
		    			resLayout.addView(title);
		    	        i++;
		    	        int nounFlag = 0;     
		    	        int verbFlag = 0;
		    	        int adjFlag = 0;
		    			
		    	        ArrayList<Map<String, String>> res = results.get(wordID);		    	        
		    	        
		    	        for(Map<String,String> mapIt : res) {
		    	        	
		    	        	 
				    	    LinearLayout nounLayout;
				    			nounLayout = new LinearLayout(MainSearch.this);
				    			nounLayout.setOrientation(LinearLayout.VERTICAL);
				    			nounLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				    		
				    		LinearLayout verbLayout;
				        		verbLayout = new LinearLayout(MainSearch.this);	    	        				
				        		verbLayout.setOrientation(LinearLayout.VERTICAL);
				        		verbLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				        	
				        	LinearLayout adjLayout;
				        		adjLayout = new LinearLayout(MainSearch.this);	    	        				
				        		adjLayout.setOrientation(LinearLayout.VERTICAL);
				        		adjLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				        	
				        	LinearLayout generalLayout;
				        		generalLayout = new LinearLayout(MainSearch.this);	    	        				
				        		generalLayout.setOrientation(LinearLayout.VERTICAL);
				        		generalLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		    	        	
		    	        	for (Map.Entry<String, String> resultMap : mapIt.entrySet()) {
		    	        		String meaning = resultMap.getKey();
		    	        		String type = resultMap.getValue(); 
		    	        		
		    	        		
		    	        		
		    	        		if(type.equals("n")) //if equals a noun
		    	        		{
		    	        			Log.v(TAG, "Noun");
		    	        			TextView temp = new TextView(MainSearch.this);
		    	        			if(nounFlag==0) {       				
		    	        				
		    	        				TextView typeView = new TextView(MainSearch.this);
		    	        				typeView.setText(R.string.noun);
		    	        				typeView.setTypeface(tf, Typeface.ITALIC);
		    	        				typeView.setTextSize(15);
		    	        				typeView.setTextColor(Color.GRAY);
		    	        				typeView.setPadding(90, 15, 15, 0);
		    	        				nounLayout.addView(typeView);
		    	        				
					    				meaning = "• "+meaning;
					    				temp.setText(meaning);
					    				temp.setPadding(100, 15, 15, 0);					    				
					    				temp.setTypeface(tf);
					    				temp.setTextSize(19);
					    				nounLayout.addView(temp);
					    				nounFlag=1;
		    	        			}
		    	        			else {
		    	        				meaning = "• "+meaning;
					    				temp.setText(meaning);
					    				temp.setPadding(100, 15, 15, 0);					    				
					    				temp.setTypeface(tf);
					    				temp.setTextSize(19);
					    				nounLayout.addView(temp);
		    	        			}
		    	        		}
		    	        		else if(type.equals("v")) //if equals a verb
		    	        		{
		    	        			Log.v(TAG,"Verb");		
		    	        			TextView temp = new TextView(MainSearch.this);
		    	        			
		    	        			if(verbFlag==0) {
		    	        				TextView typeView = new TextView(MainSearch.this);
		    	        				typeView.setText(R.string.verb);
		    	        				typeView.setTypeface(tf, Typeface.ITALIC);
		    	        				typeView.setTextColor(Color.GRAY);
		    	        				typeView.setTextSize(15);
		    	        				typeView.setPadding(90, 15, 15, 0);
		    	        				verbLayout.addView(typeView);
		    	        				
		    	        				meaning = "• "+meaning;
					    				temp.setText(meaning);
					    				temp.setPadding(100, 15, 15, 0);					    				
					    				temp.setTypeface(tf);
					    				temp.setTextSize(19);
					    				verbLayout.addView(temp);
					    				verbFlag=1;
		    	        			}
		    	        			else {
		    	        				meaning = "• "+meaning;
					    				temp.setText(meaning);
					    				temp.setPadding(100, 15, 15, 0);					    				
					    				temp.setTypeface(tf);
					    				temp.setTextSize(19);
					    				verbLayout.addView(temp);
		    	        			}
		    	        		}
		    	        		else if(type.equals("a")) //if equals an adjective
		    	        		{
		    	        			Log.v(TAG,"Adjective");		
		    	        			TextView temp = new TextView(MainSearch.this);
		    	        			
		    	        			if(adjFlag==0) {
		    	        				TextView typeView = new TextView(MainSearch.this);
		    	        				typeView.setText(R.string.adjective);
		    	        				typeView.setTypeface(tf, Typeface.ITALIC);
		    	        				typeView.setTextColor(Color.GRAY);
		    	        				typeView.setTextSize(15);
		    	        				typeView.setPadding(90, 15, 15, 0);
		    	        				adjLayout.addView(typeView);
		    	        				
		    	        				meaning = "• "+meaning;
					    				temp.setText(meaning);
					    				temp.setPadding(100, 15, 15, 0);					    				
					    				temp.setTypeface(tf);
					    				temp.setTextSize(19);
					    				adjLayout.addView(temp);
					    				adjFlag=1;
		    	        			}
		    	        			else {
		    	        				meaning = "• "+meaning;
					    				temp.setText(meaning);
					    				temp.setPadding(100, 15, 15, 0);					    				
					    				temp.setTypeface(tf);
					    				temp.setTextSize(19);
					    				adjLayout.addView(temp);
		    	        			}
		    	        		} 
		    	        		else { //if equals everything else
		    	        			TextView temp = new TextView(MainSearch.this);
		    	        			meaning = "• "+meaning;
				    				temp.setText(meaning);
				    				temp.setPadding(100, 15, 15, 0);					    				
				    				temp.setTypeface(tf);
				    				temp.setTextSize(19);
				    				generalLayout.addView(temp);
		    	        		}
		    	        		
		    	        		resLayout.addView(nounLayout);
		    	        		resLayout.addView(verbLayout);
		    	        		resLayout.addView(adjLayout);
		    	        		resLayout.addView(generalLayout);
		    	        		
		    	        		
		    	        	}
		    	        	
		    	        	
		    	        }
		    	    }
		    	
	    	}

	    }
	}

}
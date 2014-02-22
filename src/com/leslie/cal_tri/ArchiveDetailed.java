package com.leslie.cal_tri;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.widget.TextView;

public class ArchiveDetailed extends Activity {

	private TextView date;
	private TextView name;
	private TextView activityType;
	private TextView distance;
	private TextView distanceType;
	private TextView time;
	private TextView notes;
	private TextView intensity;
	private DBCalTri databaseHelper;
	private String id;
	private String dateFromEntry;
	private int secondsFromEntry;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.archive_detailed);			

		date = (TextView) findViewById(R.id.single_date);
		name = (TextView) findViewById(R.id.single_name);
		activityType = (TextView) findViewById(R.id.single_activity);
		distance = (TextView) findViewById(R.id.single_distance);
		distanceType = (TextView) findViewById(R.id.single_miles);
		time = (TextView) findViewById(R.id.single_time);
		notes = (TextView) findViewById(R.id.single_notes);
		intensity = (TextView) findViewById(R.id.single_intensity);

		Bundle extras = getIntent().getExtras();
		id = extras.getString("id");
		
		databaseHelper = new DBCalTri(this);
		databaseHelper.open();
		Cursor entry = databaseHelper.fetchEntry(Long.valueOf(id));
		
		dateFromEntry = entry.getString(entry.getColumnIndex("date"));
		SimpleDateFormat formatReceived = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat titleFormat = new SimpleDateFormat("EEEE dd MMMM yyyy");
		
		try {
			String reformattedDate = titleFormat.format(formatReceived.parse(dateFromEntry));
			date.setText(reformattedDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		
		//conversion and formatting of total seconds from database
		//to hours:minutes:seconds
		secondsFromEntry = Integer.parseInt(entry.getString(entry.getColumnIndex("time")));	
		String timeString = getTime(secondsFromEntry);
		
		name.setText(entry.getString(entry.getColumnIndex("comments")));
		activityType.setText(entry.getString(entry.getColumnIndex("activity_type")));
		distance.setText(entry.getString(entry.getColumnIndex("distance")));
		time.setText(timeString);
		intensity.setText("Intensity " + entry.getString(entry.getColumnIndex("intensity")));
		notes.setText(entry.getString(entry.getColumnIndex("notes")));
		
		if (notes.getText().equals("")){
			notes.setText("No session notes.");
		}
		
	}

	
	private String getTime(int totalSeconds){
		int hours = secondsFromEntry / 3600;
		int minutes = (secondsFromEntry % 3600) / 60;
		int seconds = secondsFromEntry % 60;
		String hoursStr = String.valueOf(hours);
		String minsStr = String.valueOf(minutes);
		String secStr = String.valueOf(seconds);	
		if(minsStr.length()==1){
			minsStr = "0" + minsStr;
		}
		if (secStr.length()==1){
			secStr = "0" + secStr;
		}
		return hoursStr + ":" + minsStr + ":" + secStr;
	}

}

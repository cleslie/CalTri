package com.leslie.cal_tri;

import java.lang.reflect.Method;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Record extends Activity {

	private DatePicker datePicker;
	private Button save;
	private Spinner activityType;
	private TextView tvDistance;
	private EditText distance;
	private TextView tvName;
	private EditText name;
	private EditText hours;
	private EditText mins;
	private EditText secs;
	private EditText notes;
	private SeekBar intensity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// no title bar must be called before adding any content
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.record);
		
		//Assignemnt of views and buttons
		datePicker = (DatePicker) findViewById(R.id.pickerDate);
		name = (EditText) findViewById(R.id.etName);
		activityType = (Spinner) findViewById(R.id.spiActivities);
		distance = (EditText) findViewById(R.id.etDistance);
		hours = (EditText) findViewById(R.id.etHours);
		mins = (EditText) findViewById(R.id.etMins);
		secs = (EditText) findViewById(R.id.etSecs);
		notes = (EditText) findViewById(R.id.etNotes);
		intensity = (SeekBar) findViewById(R.id.seekerIntensity);
		save = (Button) findViewById(R.id.bsave);

		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= 11) {
			// do not display calender display on newer APIs
			// take up too much space on screen and unnecessary because
			// the vast majority of entries will be the current date
			try {
				Method m = datePicker.getClass().getMethod("setCalendarViewShown", boolean.class);
				m.invoke(datePicker, false);
			} catch (Exception e) {
				// pass
			}
		}
		
		//Give spinner activities (swim, cycle, run)
		populateSpinner(activityType);

		save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Checking Name and Distance are entered (required fields) 
				if (checkNameDate()) {
					boolean didItWork = true;
					
					//Getting information to save
					try {
						
						//Get date
						String dateStr = parseDate();
						
						//Get user input from fields
						String activityStr = activityType.getSelectedItem().toString();
						Float flDistance = Float.valueOf(distance.getText().toString());
						int totalSecs = combineTime();
						String commentsStr = name.getText().toString();
						String notesStr = notes.getText().toString();
						int seekInt = intensity.getProgress();

						//Write record to db
						DBCalTri databaseHelper = new DBCalTri(Record.this);
						databaseHelper.open();
						databaseHelper.createEntry(dateStr, activityStr, flDistance,commentsStr, notesStr, totalSecs, seekInt);
						databaseHelper.close();

					} catch (Exception e) {
						// Error adding entry
						didItWork = false;
						Dialog d = new Dialog(Record.this);
						d.setTitle("Error adding entry.");
						d.show();
					} finally {
						if (didItWork) {
							// Successfully added entry
							Dialog d = new Dialog(Record.this);
							d.setTitle("Session has been saved");
							d.show();
						}
					}
					// Change screen to training log after new entry
					Intent intentArchive = new Intent(Record.this, Archive.class);
					startActivity(intentArchive);
					finish();
								
				} 
			}

		});

	}

	private void populateSpinner(Spinner spin) {
		//activities array == swim, cycle, run
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.activities_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin.setAdapter(adapter);
	}

	//returns a total of time entered in H M S boxes in seconds
	public int combineTime() {
		int total = 0;
		if (!isEmpty(hours)) {
			total += 60 * 60 * Integer.parseInt(hours.getText().toString());
		}
		if (!isEmpty(mins)) {
			total += 60 * Integer.parseInt(mins.getText().toString());
		}
		if (!isEmpty(mins)) {
			total += Integer.parseInt(secs.getText().toString());
		}
		return total;
	}
	
	private String parseDate(){
		//Date formatting and validation for database
		String day = Integer.toString(datePicker.getDayOfMonth());
		String month = Integer.toString(datePicker.getMonth() + 1);
		String year = Integer.toString(datePicker.getYear());
		
		// adding leading zeros if day/month are single digits
		if (month.length() == 1) {
			month = "0" + month;
		}
		if (day.length() == 1) {
			day = "0" + day;
		}
		// Date format for database: YYYY-MM-DD
		return year + "-" + month + "-" + day;
	}

	// Checks if given edittext eText is empty, returns true if empty
	private boolean isEmpty(EditText eText) {
		if (eText.getText().toString().trim().length() > 0) {
			return false;
		} else {
			return true;
		}
	}
	
	private boolean checkNameDate(){
		if (isEmpty(distance) || isEmpty(name)) {
			Toast completeFieldsToast = Toast.makeText(getApplicationContext(), "Name and Distance are required fields", Toast.LENGTH_LONG);
			completeFieldsToast.show();
			
			if (isEmpty(distance)){
				tvDistance.setTextColor(Color.RED);
			}		
			if (isEmpty(name)){
				tvName.setTextColor(Color.RED);
			}
			return false;
			
		} else {
			return true;
		}
	}
}

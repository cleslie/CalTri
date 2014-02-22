package com.leslie.cal_tri;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import au.com.bytecode.opencsv.*;

public class EmailArchive extends Activity {
	
	private Button sendToEmail;
	private Button saveDataToCSV;
	private Button deleteSavedData;
	private TextView fileExistsNotification;
	private DBCalTri databaseHelper;
	private List<String[]> currentData;
	public final String ADDRESS_FILE = (Environment.getExternalStorageDirectory() + "/TrainingCalTri.csv");
	private Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.email_archive);
		sendToEmail = (Button) findViewById(R.id.btnEmail);
		saveDataToCSV = (Button) findViewById(R.id.btnSaveCSV);
		deleteSavedData = (Button) findViewById(R.id.btnDeleteCSV);
		fileExistsNotification = (TextView) findViewById(R.id.tvTrainingFileExists);
		
		//Check for SD Card
		isSDMountedToast(isSDPresent);
		
		//Start trying to save data if a file does not already exist
		if (!fileExistNotification()){
			writeTrainingToSDCard();
		}
		
		//Send training log intent
		sendToEmail.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//Log.e("boolean writesucces is: ", String.valueOf(writeSuccessful)); 
				if (fileExistNotification()){
					Intent sendIntent = new Intent(Intent.ACTION_SEND);
			        sendIntent.setType("text/plain");
			        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Triathlon Training Log - CalTri");
			        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+ADDRESS_FILE));
			        startActivity(Intent.createChooser(sendIntent, "Email:"));
				}
			}
		});
		
		saveDataToCSV.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
				isSDMountedToast(isSDPresent);
				writeTrainingToSDCard();
				fileExistNotification();
			}
		});
		
		deleteSavedData.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
				if (deleteFileFromSD()){
					Toast deleteSuccessMessage = Toast.makeText(getApplicationContext(), "Saved data successfully deleted.", Toast.LENGTH_LONG);
					deleteSuccessMessage.show();
					fileExistNotification();
				}
				
			}
		});
		
		
	}

	@SuppressWarnings("deprecation")
	public void writeTrainingToSDCard() {		
		AlertDialog dialogWrite = new AlertDialog.Builder(EmailArchive.this).create();
		dialogWrite.setTitle("Save to SD Card");
		dialogWrite.setMessage("Save training log to SD card? (Required for email - will overwrite previously saved data)");
		dialogWrite.setButton("Ok", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {
				//Get data from database
				databaseHelper = new DBCalTri(EmailArchive.this);
				databaseHelper.open();
				currentData = databaseHelper.getAllEntriesArray();
				databaseHelper.close();
				
				//Convert data to .csv format for email
				CSVWriter writer;
				try {
					writer = new CSVWriter(new FileWriter(ADDRESS_FILE));
					writer.writeAll(currentData);
					writer.close();
					Toast saveSuccessMessage = Toast.makeText(getApplicationContext(), "Save Successful", Toast.LENGTH_SHORT);
					saveSuccessMessage.show();
					fileExistNotification();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		dialogWrite.setButton2("Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
		
		//Show save option dialog
		dialogWrite.show();
	}
	
	public boolean deleteFileFromSD(){
		boolean isDeleted = false;
		File trainingCSV = new File(ADDRESS_FILE);
		if (!isSDPresent){
			Toast noSDNotification = Toast.makeText(getApplicationContext(), "No SD card found. Attempting to email or save log may cause application to crash.", Toast.LENGTH_LONG);
			noSDNotification.show();
		} else if (!trainingCSV.exists()){
			Toast noFileNotification = Toast.makeText(getApplicationContext(), "No saved file found.", Toast.LENGTH_LONG);
			noFileNotification.show();
		} else {
			isDeleted = trainingCSV.delete();
		}
		
		return isDeleted;
	}
	
	public void isSDMountedToast(Boolean SDPresent){
		if (SDPresent){
			// SD Card present, continue writing (do nothing)
			Toast SDNotification = Toast.makeText(getApplicationContext(), "SD card found.", Toast.LENGTH_LONG);
			SDNotification.show();
		} else {		
			//no SD card - needs handled better, ie. restart activity
			Toast noSDNotification = Toast.makeText(getApplicationContext(), "No SD card found. Attempting to email or save log may cause application to crash.", Toast.LENGTH_LONG);
			noSDNotification.show();
		}
	}
	
	//Checks if training file exists, displays notification with information about the file if it does
	public boolean fileExistNotification(){
		File trainingCSV = new File(ADDRESS_FILE);
		if (trainingCSV.exists()){
			Date lastMod = new Date(trainingCSV.lastModified());
			fileExistsNotification.setText("Training File Found. Last modified: " + lastMod.toString());
			fileExistsNotification.setTextColor(Color.parseColor("#66CD00"));
			return true;
		} else {
			fileExistsNotification.setText("File not found.");
			fileExistsNotification.setTextColor(Color.RED);
			return false;
		}
	}

}

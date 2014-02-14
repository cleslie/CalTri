package com.leslie.cal_tri;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
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
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import au.com.bytecode.opencsv.*;

public class EmailArchive extends Activity {
	
	private Button sendToEmail;
	private Button sendToDrive;
	private Button saveDataToCSV;
	private Button deleteSavedData;
	private TextView fileExistsNotification;
	private DBCalTri databaseHelper;
	private List<String[]> currentData;
	public final String ADDRESS_FILE = (Environment
			.getExternalStorageDirectory() + "/TrainingCalTri.csv");
	private Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	String[] row = null;
	private Boolean writeSuccessful = false;
	private Boolean emailSuccessful = false;
	
	//TODO:
	// Check if file exists already before overwriting
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.email_archive);
		sendToEmail = (Button) findViewById(R.id.btnEmail);
		saveDataToCSV = (Button) findViewById(R.id.btnSaveCSV);
		deleteSavedData = (Button) findViewById(R.id.btnDeleteCSV);
		fileExistsNotification = (TextView) findViewById(R.id.tvTrainingFileExists);
		
		//NEED HARD CHECK FOR SD
		isSDMountedToast(isSDPresent);
		writeTrainingToSDCard();
		fileExistNotification();
		
		sendToEmail.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//second (hard) check for sd card
				Log.e("boolean writesucces is: ", String.valueOf(writeSuccessful)); 
				if (fileExistNotification()){
					Intent sendIntent = new Intent(Intent.ACTION_SEND);
			        sendIntent.setType("text/plain");
			        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Triathlon Training Log - CalTri");
			        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+ADDRESS_FILE));
			        startActivity(Intent.createChooser(sendIntent, "Email:"));
			        emailSuccessful = true;
				}
				
				if (emailSuccessful){
					//Display success message, not working correctly, displays immediately after send to email is clicked
					//Toast saveSuccessMessage = Toast.makeText(getApplicationContext(), "Save Successful", Toast.LENGTH_SHORT);
					//saveSuccessMessage.show();
				}
			}
		});
		
		saveDataToCSV.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
				//NEED HARD CHECK FOR SD
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
		dialogWrite.setMessage("Save training log to SD card? (This is required in order to email your log and will overwrite previous saved data)");
		dialogWrite.setButton("Ok", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {
				databaseHelper = new DBCalTri(EmailArchive.this);
				databaseHelper.open();
				currentData = databaseHelper.getAllEntriesArray();
				databaseHelper.close();

				CSVWriter writer;
				try {
					writer = new CSVWriter(new FileWriter(ADDRESS_FILE));
					writer.writeAll(currentData);
					writer.close();
					Toast saveSuccessMessage = Toast.makeText(getApplicationContext(), "Save Successful", Toast.LENGTH_SHORT);
					saveSuccessMessage.show();
					writeSuccessful = true;
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

package com.leslie.cal_tri;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;


public class MainMenu extends Activity {

	private ImageButton btnRecord;
	private ImageButton btnArchive;
	private ImageButton btnAbout;
	private ImageButton btnGraph;
	private ImageButton btnSync;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);

		btnRecord = (ImageButton) findViewById(R.id.bRecord);
		btnArchive = (ImageButton) findViewById(R.id.bArchive);
		btnAbout = (ImageButton) findViewById(R.id.bAbout);
		btnGraph = (ImageButton) findViewById(R.id.bGraph);
		btnSync = (ImageButton) findViewById(R.id.bSync);

		btnRecord.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intentRecord = new Intent(MainMenu.this, Record.class);
				startActivity(intentRecord);
			}
		});

		btnArchive.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intentArchive = new Intent(MainMenu.this, Archive.class);
				startActivity(intentArchive);
			}
		});

		btnAbout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intentAbout = new Intent(MainMenu.this, About.class);
				startActivity(intentAbout);
			}
		});

		btnGraph.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intentGraph = new Intent(MainMenu.this, Graph.class);
				startActivity(intentGraph);
			}
		});
		
		btnSync.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intentEmail = new Intent(MainMenu.this, EmailArchive.class);
				startActivity(intentEmail);
			}
			
		});
		
		btnSync.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intentEmail = new Intent(MainMenu.this, EmailArchive.class);
				startActivity(intentEmail);
			}
			
		});

	}
}

package com.leslie.cal_tri;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;


public class MainMenu extends Activity {

	private Button btnRecord;
	private Button btnArchive;
	private Button btnAbout;
	private Button btnGraph;
	private Button btnSync;
	private Button btnMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);

		btnRecord = (Button) findViewById(R.id.bRecord);
		btnArchive = (Button) findViewById(R.id.bArchive);
		btnAbout = (Button) findViewById(R.id.bAbout);
		btnGraph = (Button) findViewById(R.id.bGraph);
		btnSync = (Button) findViewById(R.id.bSync);
		btnMap = (Button) findViewById(R.id.bMap);

		btnRecord.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intentRecord = new Intent(MainMenu.this,
						Record.class);
				startActivity(intentRecord);
			}
		});

		btnArchive.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intentArchive = new Intent(MainMenu.this,
						Archive.class);
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
		
		btnMap.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intentMap = new Intent(MainMenu.this, MapScreen.class);
				startActivity(intentMap);
			}
			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// No menu
		// getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}

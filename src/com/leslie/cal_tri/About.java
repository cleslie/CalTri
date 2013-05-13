package com.leslie.cal_tri;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;

public class About extends Activity{
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE); //gets rid of top title bar (must be called before adding ANY content)
		setContentView(R.layout.about);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// No menu
		// getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}

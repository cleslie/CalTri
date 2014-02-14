package com.leslie.cal_tri;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Graph extends Activity {
	
	private DBCalTri databaseHelper;
	private ArrayList<String> monthlyData;
	private TextView tvTotalDistance;
	private TextView tvMonthYear;
	private TextView tvTotalActivities;
	private TextView tvMonthlyBreakdown;
	private LinearLayout currentChart;
	
	public String month = getCurrentMonth();
	public String year = getCurrentYear();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SimpleDateFormat formatReceived = new SimpleDateFormat("MMyyyy");
		SimpleDateFormat titleFormat = new SimpleDateFormat("MMMM yyyy");
		try {
			String reformattedDate = titleFormat.format(formatReceived.parse(month + year));
			setTitle("Activities for " + reformattedDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
			
		setContentView(R.layout.graph_main);
		tvTotalDistance = (TextView) findViewById(R.id.tvMonthlyTotalDistance);
		tvTotalActivities = (TextView) findViewById(R.id.tvMonthlyTotalActivities);
		tvMonthlyBreakdown = (TextView) findViewById(R.id.tvMonthlyBreakdown);
		currentChart = (LinearLayout) findViewById(R.id.mainChart);
		
		//main 
		databaseHelper = new DBCalTri(this);
		databaseHelper.open();
		updateMonthlyData();
		databaseHelper.close();
		currentChart.addView(createMonthlyPie());
	}

	public void updateMonthlyData() {

		String totalDistance = String.valueOf(databaseHelper.getMonthDistance(month, year));

		// Returns arraylist of Strings in the format:
		// [Totalactivities, swims, cycles, runs] for the current month
		monthlyData = databaseHelper.getMonthlyActivityFrequency(month, year);
		
		//updating textviews with current data
		//checking if one activity for correct grammar on textview display	
		tvTotalDistance.setText(totalDistance + " mi   ");
		if (Integer.valueOf(monthlyData.get(0))==1){
			tvTotalActivities.setText(monthlyData.get(0) + " activity");
		} else {
			tvTotalActivities.setText(monthlyData.get(0) + " activities");
		}
		tvMonthlyBreakdown.setText("Swims: " + monthlyData.get(1) + "  " + "Cycles: " + monthlyData.get(2) + "  " + "Runs: " + monthlyData.get(3));

	}

	public GraphicalView createMonthlyPie() {
		
		//Adding activity frequency values and names to pie chart series
		int[] activityFrequency = { Integer.valueOf(monthlyData.get(1)), Integer.valueOf(monthlyData.get(2)), Integer.valueOf(monthlyData.get(3)) };
		String[] activitiyName = { "Swim", "Cycle", "Run" };
			
		CategorySeries series = new CategorySeries("Pie Graph");
		int k = 0;
		for(int value : activityFrequency){
			series.add(activitiyName[k], value);
			k++;
		}
		
		//Setting pie chart colours - 
		//colours array corresponds directly to activityFrequency and activityName arrays above
		// hex values for colours - blue: #1E90FF, green #66CD00, orange #EE9A00
		int[] colours = new int[] {Color.rgb(30, 144, 255), Color.rgb(102, 205, 0), Color.rgb(238, 154, 0)}; //Blue(Swim), Green(Cycle), Orange(Run)
		DefaultRenderer renderer = new DefaultRenderer();
		for (int colour : colours){
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(colour);
			r.setDisplayChartValues(true);
			renderer.addSeriesRenderer(r);
		}
		
		renderer.setChartTitleTextSize(12);
		renderer.setLabelsTextSize(12);
		renderer.setLabelsColor(Color.BLACK);
		renderer.setZoomButtonsVisible(true);
		
		GraphicalView pieView = ChartFactory.getPieChartView(getBaseContext(), series, renderer);
		
		return pieView;
	}

	public String getCurrentMonth() {
		Calendar calendar = Calendar.getInstance();
		int monthNum = calendar.get(Calendar.MONTH) + 1;
		String month = String.valueOf(monthNum);
		if (month.length() == 1) {
			month = "0" + month;
		}
		return month;
	}

	public String getCurrentYear() {
		Calendar calendar = Calendar.getInstance();
		int yearNum = calendar.get(Calendar.YEAR);
		return String.valueOf(yearNum);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.graph_menu, menu);
		return true;
	}
}

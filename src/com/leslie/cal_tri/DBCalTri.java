package com.leslie.cal_tri;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.ArrayAdapter;

public class DBCalTri {
	public static final String KEY_ROWID = "_id";
	public static final String KEY_DATE = "date";
	public static final String KEY_ACTIVITY = "activity_type";
	public static final String KEY_DISTANCE = "distance";
	public static final String KEY_COMMENTS = "comments";

	// These columns added later, potential for them to be missing from some
	// queries
	public static final String KEY_NOTES = "notes";
	public static final String KEY_TIME = "time";
	public static final String KEY_INTENSITY = "intensity";

	private static final String DATABASE_NAME = "CalTriDB4";
	private static final String DATABASE_TABLE = "trainingTable";
	private static final int DATABASE_VERSION = 1;

	private DbHelper ourHelper;
	private final Context ourContext;
	private SQLiteDatabase ourDatabase;
	private ArrayList<String> entryArray = new ArrayList<String>();

	private static class DbHelper extends SQLiteOpenHelper {
		public DbHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// Creation of database (first run)
			db.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" + KEY_ROWID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_DATE
					+ " TEXT NOT NULL, " + KEY_ACTIVITY + " TEXT NOT NULL, "
					+ KEY_DISTANCE + " REAL, " + KEY_COMMENTS
					+ " TEXT NOT NULL, " + KEY_NOTES + " TEXT NOT NULL, "
					+ KEY_TIME + " INTEGER NOT NULL, " + KEY_INTENSITY
					+ " INTEGER NOT NULL);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// database updated/reconstructed (subsequent runs)
			db.execSQL("DROP_TABLE_IF_EXISTS " + DATABASE_TABLE);
			onCreate(db);
		}
	}

	public DBCalTri(Context c) {
		// setting context of class it is called in
		ourContext = c;
	}

	public DBCalTri open() throws SQLException {
		ourHelper = new DbHelper(ourContext);
		ourDatabase = ourHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		ourHelper.close();
	}

	public long createEntry(String strDate, String activity, Float flDistance,
			String strComments, String strNotes, int intTime, int intIntensity) {
		// data is packed into contentvalues and inserted into db table
		ContentValues cv = new ContentValues();
		cv.put(KEY_DATE, strDate);
		cv.put(KEY_ACTIVITY, activity);
		cv.put(KEY_DISTANCE, flDistance);
		cv.put(KEY_COMMENTS, strComments);
		cv.put(KEY_NOTES, strNotes);
		cv.put(KEY_TIME, intTime);
		cv.put(KEY_INTENSITY, intIntensity);

		return ourDatabase.insert(DATABASE_TABLE, null, cv);
	}

	public Cursor getTrainingRecordList() {
		return ourDatabase.rawQuery("select * from " + DATABASE_TABLE, null);
	}

	public void deleteEntry(String id) throws SQLException {
		ourDatabase.delete(DATABASE_TABLE, KEY_ROWID + "=" + id, null);
	}

	public List<String[]> getAllEntriesArray() {

		// returns arraylist of string arrays (each represnt row of database,
		// not including the id column)
		// this format is required for opencsv library to write data to .csv
		// file in EmailArchive.java

		Cursor c = ourDatabase.query(DATABASE_TABLE, new String[] { KEY_DATE,
				KEY_ACTIVITY, KEY_DISTANCE, KEY_COMMENTS, KEY_NOTES, KEY_TIME,
				KEY_INTENSITY }, null, null, null, null, null);
		List<String[]> trainingData = new ArrayList<String[]>();

		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			String tempDate = c.getString(c.getColumnIndex(KEY_DATE));
			String tempActivity = c.getString(c.getColumnIndex(KEY_ACTIVITY));
			String tempDist = c.getString(c.getColumnIndex(KEY_DISTANCE));
			String tempCom = c.getString(c.getColumnIndex(KEY_COMMENTS));
			String tempNotes = c.getString(c.getColumnIndex(KEY_NOTES));
			// time needs to be formatted here, current in total seconds
			String tempTime = c.getString(c.getColumnIndex(KEY_TIME));
			String tempInten = c.getString(c.getColumnIndex(KEY_INTENSITY));

			// Format for spreadsheet is Date, Name, Type, Distance, Time,
			// Notes, Intesity
			String[] tempRow = new String[7];
			tempRow[0] = tempDate;
			tempRow[1] = tempCom;
			tempRow[2] = tempActivity;
			tempRow[3] = tempDist;
			tempRow[4] = tempTime;
			tempRow[5] = tempNotes;
			tempRow[6] = tempInten;

			// add each row to List
			trainingData.add(tempRow);
		}

		return trainingData;
	}

	public Cursor fetchAllEntriesBasic() {
		// Return a Cursor over the list of all notes in the database
		return ourDatabase.query(DATABASE_TABLE, new String[] { KEY_ROWID,
				KEY_DATE, KEY_ACTIVITY, KEY_DISTANCE, KEY_COMMENTS }, null,
				null, null, null, null);
	}

	public Cursor fetchEntriesCurrentWeek() {
		return ourDatabase
				.rawQuery(
						"SELECT * FROM trainingTable WHERE date BETWEEN datetime('now', '-6 days') AND datetime('now', 'localtime')",
						null);
	}

	public Cursor fetchEntriesPastMonth() {
		return ourDatabase
				.rawQuery(
						"SELECT * FROM trainingTable WHERE date BETWEEN date('now', 'start of month') AND date('now', 'localtime')",
						null);
	}


	
	public Cursor fetchEntriesSpecificMonth(String monthYear) {	
		String query = "SELECT * FROM " + DATABASE_TABLE
				+ " WHERE strftime('%Y-%m', `date`) =?";
		String[] arguments = { monthYear };
		Cursor c = ourDatabase.rawQuery(query, arguments);
		return c;
	}
	
	public Cursor fetchEntry(long rowId) throws SQLException {
		// Return a Cursor positioned at the note that matches the given rowId
		Cursor mCursor = ourDatabase.query(true, DATABASE_TABLE, new String[] {
				KEY_ROWID, KEY_DATE, KEY_ACTIVITY, KEY_DISTANCE, KEY_COMMENTS,
				KEY_NOTES, KEY_TIME, KEY_INTENSITY }, KEY_ROWID + "=" + rowId,
				null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	// Sorting Queries

	public Cursor getEntriesDateRecent() {
		return ourDatabase.query(true, DATABASE_TABLE,
				new String[] { KEY_ROWID, KEY_DATE, KEY_ACTIVITY, KEY_DISTANCE,
						KEY_COMMENTS }, null, null, null, null, KEY_DATE
						+ " DESC", null);
	}

	public Cursor getEntriesDateOldest() {
		return ourDatabase.query(true, DATABASE_TABLE,
				new String[] { KEY_ROWID, KEY_DATE, KEY_ACTIVITY, KEY_DISTANCE,
						KEY_COMMENTS }, null, null, null, null, KEY_DATE
						+ " ASC", null);
	}

	public Cursor getEntriesMilesDescending() {
		return ourDatabase.query(true, DATABASE_TABLE,
				new String[] { KEY_ROWID, KEY_DATE, KEY_ACTIVITY, KEY_DISTANCE,
						KEY_COMMENTS }, null, null, null, null, KEY_DISTANCE
						+ " DESC", null);
	}

	public Cursor getEntriesMilesAscending() {
		return ourDatabase.query(true, DATABASE_TABLE,
				new String[] { KEY_ROWID, KEY_DATE, KEY_ACTIVITY, KEY_DISTANCE,
						KEY_COMMENTS }, null, null, null, null, KEY_DISTANCE
						+ " ASC", null);
	}

	public Cursor getEntriesByActivity() {
		return ourDatabase.query(true, DATABASE_TABLE,
				new String[] { KEY_ROWID, KEY_DATE, KEY_ACTIVITY, KEY_DISTANCE,
						KEY_COMMENTS }, null, null, null, null, KEY_ACTIVITY
						+ " ASC", null);
	}

	// Graph fetching Queries
	public Float getMonthDistance(String month, String year) {
		String MonthYear = year + "-" + month;
		String query = "SELECT distance FROM " + DATABASE_TABLE
				+ " WHERE strftime('%Y-%m', `date`) =?";
		String[] arguments = { MonthYear };
		Cursor c = ourDatabase.rawQuery(query, arguments);

		float totalDistance = 0;
		int iDistance = c.getColumnIndex(KEY_DISTANCE);
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			totalDistance += c.getFloat(iDistance);
		}

		return totalDistance;
	}

	public ArrayList getMonthlyActivityFrequency(String month, String year) {

		// Returns arraylist of Strings in the format:
		// [Totalactivities, swims, cycles, runs] for the current month

		int totalActivities = 0;
		int numberOfSwims = 0;
		int numberOfCycles = 0;
		int numberOfRuns = 0;

		String MonthYear = year + "-" + month;
		String query = "SELECT * FROM " + DATABASE_TABLE
				+ " WHERE strftime('%Y-%m', `date`) =?";
		String[] arguments = { MonthYear };
		Cursor c = ourDatabase.rawQuery(query, arguments);

		int iActivity = c.getColumnIndex(KEY_ACTIVITY);

		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

			String activityType = c.getString(iActivity);

			if (activityType.equals("Swim")) {
				numberOfSwims++;
				totalActivities++;
			} else if (activityType.equals("Cycle")) {
				numberOfCycles++;
				totalActivities++;
			} else if (activityType.equals("Run")) {
				numberOfRuns++;
				totalActivities++;
			}
		}
		entryArray.add(String.valueOf(totalActivities));
		entryArray.add(String.valueOf(numberOfSwims));
		entryArray.add(String.valueOf(numberOfCycles));
		entryArray.add(String.valueOf(numberOfRuns));

		return entryArray;
	}

}

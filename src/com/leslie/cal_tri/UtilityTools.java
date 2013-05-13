package com.leslie.cal_tri;

import java.util.Calendar;

public class UtilityTools {
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

}

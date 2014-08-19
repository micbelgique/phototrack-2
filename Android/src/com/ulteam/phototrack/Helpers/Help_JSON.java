package com.ulteam.phototrack.Helpers;

import java.util.Calendar;

public class Help_JSON {

	public static String getJsonDate(Calendar calendar) {
		long time = calendar.getTimeInMillis();
		return "/Date(" + time + ")/";
	}
	
}

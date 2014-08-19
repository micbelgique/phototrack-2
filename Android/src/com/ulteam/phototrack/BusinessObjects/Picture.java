package com.ulteam.phototrack.BusinessObjects;

import java.util.Calendar;
import java.util.UUID;

import android.location.Location;

public class Picture {

	/** -------------- Objects, Variables -------------- */
	/** ------------------------------------------------ */

	public UUID id;
	public Calendar date;
	public String comment;
	public Location location;

	public String imagePath;
	public String imageUrl;
	

	/** ------------------ Properties ------------------ */
	/** ------------------------------------------------ */


	/** ------------------ Constructor ----------------- */
	/** ------------------------------------------------ */

	
	public Picture(UUID id, String imageUrl, Calendar date, String comment, Location location) {
		this.id = id;
		this.imageUrl = imageUrl;
		this.date = date;
		this.comment = comment;
		this.location = location;
	}


	/** ------------------- Methods -------------------- */
	/** ------------------------------------------------ */

}
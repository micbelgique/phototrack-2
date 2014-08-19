package com.ulteam.phototrack.BusinessObjects;

import java.util.UUID;

import android.location.Location;

public class Site {

	/** -------------- Objects, Variables -------------- */
	/** ------------------------------------------------ */

	public UUID id;
	public String name;
	public String organization;
	public Location location;
	public String street, city, zip;
	public String shareCode;
	public int workCount;


	/** ------------------ Constructor ----------------- */
	/** ------------------------------------------------ */

	public Site(UUID id, String name, String organization, Location location, String street, String city, String zip, String shareCode, int workCount) {
		this.id = id;
		this.name = name;
		this.organization = organization;
		this.location = location;
		this.street = street;
		this.city = city;
		this.zip = zip;
		this.shareCode = shareCode;
		this.workCount = workCount;
	}
	
}
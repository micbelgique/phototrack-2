package com.ulteam.phototrack.BusinessObjects;

import java.util.UUID;

public class Work {

	/** -------------- Objects, Variables -------------- */
	/** ------------------------------------------------ */

	public UUID id;
	public String name;
	public int picturesCount;

	/** ------------------ Constructor ----------------- */
	/** ------------------------------------------------ */

	public Work(UUID id, String name, int picturesCount) {
		this.id = id;
		this.name = name;	
		this.picturesCount = picturesCount;
	}

}
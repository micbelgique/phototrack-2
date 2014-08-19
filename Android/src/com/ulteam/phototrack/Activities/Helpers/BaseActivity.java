package com.ulteam.phototrack.Activities.Helpers;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;

import com.ulteam.phototrack.BusinessObjects.Site;
import com.ulteam.phototrack.BusinessObjects.Work;

public class BaseActivity extends Activity {

	/** -------------- Objects, Variables -------------- */
	/** ------------------------------------------------ */

	protected static Site currentSite;
	protected static Work currentWork;


	/** --------------------- Views -------------------- */
	/** ------------------------------------------------ */


	/** ------------------ LifeCycle ------------------- */
	/** ------------------------------------------------ */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(false) ;
		actionBar.setDisplayHomeAsUpEnabled(false);
	}

	
	/** ------------------ Listeners ------------------- *
	/** ------------------------------------------------ */


	/** ------------------- Methods -------------------- */
	/** ------------------------------------------------ */


	/** -------------------- Divers -------------------- */
	/** ------------------------------------------------ */

}

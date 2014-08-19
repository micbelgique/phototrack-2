package com.ulteam.phototrack.Activities;
import android.content.Intent;
import android.os.Bundle;

import com.ulteam.phototrack.Activities.Helpers.BaseActivity;


public class SplashScreen extends BaseActivity {

	/** -------------- Objects, Variables -------------- */
	/** ------------------------------------------------ */


	/** --------------------- Views -------------------- */
	/** ------------------------------------------------ */

	
	/** ------------------ LifeCycle ------------------- */
	/** ------------------------------------------------ */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		startActivity(new Intent(SplashScreen.this, SignIn.class));
		finish();
	}

	/** ------------------ Listeners ------------------- *
	/** ------------------------------------------------ */


	/** ------------------- Methods -------------------- */
	/** ------------------------------------------------ */


	/** --------------- Gestion du Menu ---------------  */
	/** ------------------------------------------------ */


	/** ----------------- GUI Adapter ------------------ */
	/** ------------------------------------------------ */


	/** -------------------- Divers -------------------- */
	/** ------------------------------------------------ */

}

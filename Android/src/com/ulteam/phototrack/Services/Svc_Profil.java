package com.ulteam.phototrack.Services;


public class Svc_Profil {

	/** ----------- Static Objects, Variables ---------- */
	/** ------------------------------------------------ */

	private static String token;


	/** ------------------- Methods -------------------- */
	/** ------------------------------------------------ */

	public static void setToken(String token) {
		Svc_Profil.token = "Bearer " + token;
	}

	public static String getToken() {
		return token;
	}
	
}
package com.ulteam.phototrack.Helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Help_Connection {

	public static boolean isOnline(Context context) {
		NetworkInfo netInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
		return (netInfo != null && netInfo.isConnectedOrConnecting());
	}

	// Return a human-readable name describe the type of the network, for example "WIFI" or "MOBILE".
	public static String getNetworkType(Context context) {
		try {
			return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo().getTypeName();
		}
		catch(Exception e) { return "NotAvailable"; }
	}

}

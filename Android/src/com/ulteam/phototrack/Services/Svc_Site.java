package com.ulteam.phototrack.Services;

import java.util.ArrayList;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ulteam.phototrack.BusinessObjects.Site;
import com.ulteam.phototrack.Helpers.Help_Azure;
import com.ulteam.phototrack.Helpers.Help_Configuration;

public class Svc_Site {

	/** ----------- Static Objects, Variables ---------- */
	/** ------------------------------------------------ */

	private static ArrayList<Site> sites;
	private static Object lock = new Object();


	/** ------------------- Methods -------------------- */
	/** ------------------------------------------------ */

	public static ArrayList<Site> getSites(boolean canNetwork){
		if(canNetwork)
			loadSites();
		return sites;
	}

	public static void addSite(Site site, boolean canNetwork) {
		if(canNetwork)
			loadSites();
		if(!sites.contains(site))
			sites.add(site);
	}


	/** ------------ Gestion de la DataBase ------------ */
	/** ------------------------------------------------ */

	private static void loadSites() {
		synchronized(lock) {
			if(sites == null) {
				try {
					JSONObject result = Help_Azure.getResponseFromAzure(new JSONObject(), Help_Configuration.SERVICES_URL + "api/site/getsites", null, true);

					if(result != null && !result.isNull("Sites")) {
						sites = new ArrayList<Site>();
						JSONArray sitesJSON = result.getJSONArray("Sites");

						for(int i = 0; i < sitesJSON.length(); i++) {
							JSONObject siteJSON = sitesJSON.getJSONObject(i);

							sites.add(new Site(
									UUID.fromString(siteJSON.getString("SiteEntityID")), 
									siteJSON.getString("Name"), 
									siteJSON.getString("OrganizationEntityName"), 
									null, // location 
									siteJSON.getString("Street"), 
									siteJSON.getString("City"), 
									siteJSON.getString("Zip"), 
									siteJSON.getString("ShareCode"), 
									siteJSON.getInt("WorksCount")));
						}
					}
				}
				catch(Exception e) { e.printStackTrace(); }
			}
		}
	}


	/** -------------------- Divers -------------------- */
	/** ------------------------------------------------ */

	public static void invalidate() {
		sites = null;
	}

}

package com.ulteam.phototrack.Services;

import java.util.ArrayList;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ulteam.phototrack.BusinessObjects.Site;
import com.ulteam.phototrack.BusinessObjects.Work;
import com.ulteam.phototrack.Helpers.Help_Azure;
import com.ulteam.phototrack.Helpers.Help_Configuration;

public class Svc_Work {

	/** ----------- Static Objects, Variables ---------- */
	/** ------------------------------------------------ */

	private static ArrayList<Work> works;
	private static Object lock = new Object();


	/** ------------------- Methods -------------------- */
	/** ------------------------------------------------ */

	public static ArrayList<Work> getWorks(Site site, boolean canNetwork){
		if(canNetwork)
			loadWorks(site);
		return works;
	}

	public static void addWork(Site site, Work work, boolean canNetwork) {
		if(canNetwork)
			loadWorks(site);

		if(!works.contains(work)) {
			works.add(work);
			site.workCount++;
		}
	}


	/** ------------ Gestion de la DataBase ------------ */
	/** ------------------------------------------------ */

	private static void loadWorks(Site site) {
		synchronized(lock) {
			if(works == null) {
				try {
					JSONObject jsonObject = new JSONObject().put("SiteEntityID", site.id.toString());

					JSONObject result = Help_Azure.getResponseFromAzure(jsonObject, Help_Configuration.SERVICES_URL + "api/work/getworks", null, true);

					if(result != null && !result.isNull("Works")) {
						works = new ArrayList<Work>();
						JSONArray worksJSON = result.getJSONArray("Works");

						for(int i = 0; i < worksJSON.length(); i++) {
							JSONObject workJSON = worksJSON.getJSONObject(i);

							works.add(new Work(
									UUID.fromString(workJSON.getString("WorkEntityID")),
									workJSON.getString("Name"),
									workJSON.getInt("PicturesCount")));
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
		works = null;
	}

}

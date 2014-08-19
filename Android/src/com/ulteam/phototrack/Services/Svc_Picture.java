package com.ulteam.phototrack.Services;

import java.util.ArrayList;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ulteam.phototrack.BusinessObjects.Picture;
import com.ulteam.phototrack.BusinessObjects.Work;
import com.ulteam.phototrack.Helpers.Help_Azure;
import com.ulteam.phototrack.Helpers.Help_Configuration;


public class Svc_Picture {

	/** ----------- Static Objects, Variables ---------- */
	/** ------------------------------------------------ */

	private static ArrayList<Picture> pictures;
	private static Object lock = new Object();


	/** ------------------- Methods -------------------- */
	/** ------------------------------------------------ */

	public static ArrayList<Picture> getPictures(Work work, boolean canNetwork){
		if(canNetwork)
			loadPictures(work);
		return pictures;
	}


	public static void addPicture(Work work, Picture picture, boolean canNetwork) {
		if(canNetwork)
			loadPictures(work);

		if(!pictures.contains(picture)) {
			pictures.add(picture);
			work.picturesCount++;
		}
	}


	/** ------------ Gestion de la DataBase ------------ */
	/** ------------------------------------------------ */

	private static void loadPictures(Work work) {
		synchronized(lock) {
			if(pictures == null) {
				try {
					JSONObject jsonObject = new JSONObject().put("WorkEntityID", work.id.toString());

					JSONObject result = Help_Azure.getResponseFromAzure(jsonObject, Help_Configuration.SERVICES_URL + "api/picture/getpictures", null, true);

					if(result != null && !result.isNull("Pictures")) {
						pictures = new ArrayList<Picture>();
						JSONArray picturesJSON = result.getJSONArray("Pictures");

						for(int i = 0; i < picturesJSON.length(); i++) {
							JSONObject pictureJSON = picturesJSON.getJSONObject(i);

							pictures.add(new Picture(
									UUID.fromString(pictureJSON.getString("PictureEntityID")),
									pictureJSON.getString("ImageUrl"),
									null, // Date
									pictureJSON.getString("Comment"),
									null)); // Location
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
		pictures = null;
	}

}

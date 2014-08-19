package com.ulteam.phototrack.Helpers;

import java.io.InputStream;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

public class Help_Bitmap {

	/**
	 * Ouvre un Bitmap en prenant soin de le resizer s'il est trop grand (pour éviter un outOfMemory)
	 * imageMaxSize = 100
	 * @param uri Le fichier Bitmap à afficher
	 * @return Un fichier Bitmap à la bonne taille
	 */
	public static Bitmap decodeFile(Uri uri, Context context){
		return decodeFile(uri, context, 100);
	}

	public static Bitmap decodeFile(String uriString, Context context){
		if(uriString != null)
			return decodeFile(Uri.parse(uriString), context, 100);
		else 
			return null;
	}

	public static Bitmap decodeFile(Uri uri, Context context, int imageMaxSize){
		Bitmap bitmap = null;
		try {
			//Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;

			InputStream inputStream = context.getContentResolver().openInputStream(uri);
			BitmapFactory.decodeStream(inputStream, null, o);

			inputStream.close();

			int scale = 1;
			if (o.outHeight > imageMaxSize || o.outWidth > imageMaxSize)
				scale = (int) Math.pow(2, (int) Math.round(Math.log(imageMaxSize / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));

			//Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			inputStream = context.getContentResolver().openInputStream(uri);
			bitmap = BitmapFactory.decodeStream(inputStream, null, o2);

			inputStream.close();
		} 
		catch (Exception e) { e.printStackTrace(); }

		return bitmap;
	}

	/**
	 * Calcule la qualité de l'image à afficher en fonction de la quantité de mémoire disponible sur le device
	 */
	public static int getRecommandedMaxSize(Context context) {
		int memoryLimit = getMemoryLimit(context);

		if(memoryLimit >= 60)
			return 1200;
		else if(memoryLimit > 20)
			return 1000;
		else if(memoryLimit > 16)
			return 800;
		else
			return 600;
	}

	public static int getMemoryLimit(Context context) {
		/**
		 * Nexus 5: 192
		 * Galaxy Nexus: 96
		 *
		 * Samsung GS3: 64
		 * Samsung GS2: 48
		 * Samsung Rose: 64
		 * 
		 * HTC Flyer: 48
		 **/

		return ((ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE)).getMemoryClass();
	}
}

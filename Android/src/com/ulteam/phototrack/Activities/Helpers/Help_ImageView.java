package com.ulteam.phototrack.Activities.Helpers;

import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ImageView;

public class Help_ImageView {

	public static void setImage(ImageView imageView, String imagePath) {
		// Get the dimensions of the bitmap
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imagePath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		// Determine how much to scale down the image
		int scaleFactor = Math.min(photoW/imageView.getWidth(), photoH/imageView.getHeight());

		// Decode the image file into a Bitmap sized to fill the View
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		imageView.setImageBitmap(BitmapFactory.decodeFile(imagePath, bmOptions));
	}

	// Ouvre un Bitmap en prenant soin de le resizer s'il est trop grand (pour éviter un outOfMemory)
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
}
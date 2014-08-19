package com.ulteam.phototrack.Helpers.Upload;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.UUID;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.ulteam.phototrack.Helpers.Help_Bitmap;

public class Uploader {

	// Upload une image et retourne un String généré par Azure
	public static String upload(Uri imageUri, Context context) {
		byte[] data = getCompressedBitmapBytesArray(imageUri, context);
		
		if(data != null) {
			try {
				return "Upload the file and return the url";
			} 
			catch(Exception e) { e.printStackTrace(); }
		}
		
		return null;
	}
	
	// Retourne un byte[] sur base de la photo d'un ticket
	private static byte[] getCompressedBitmapBytesArray(Uri imageUri, Context context) {
		if(imageUri != null) {
			Bitmap bitmap = Help_Bitmap.decodeFile(imageUri, context, 800);

			if(bitmap != null) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();  
				bitmap.compress(Bitmap.CompressFormat.JPEG, 95, baos); 
				return baos.toByteArray();  
			}
		}
		return null;
	}


}
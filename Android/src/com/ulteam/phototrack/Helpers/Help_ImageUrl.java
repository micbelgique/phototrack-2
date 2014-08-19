package com.ulteam.phototrack.Helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

public class Help_ImageUrl {

	public static Drawable getDrawable(String imageUrlString) {
		try {
			return Drawable.createFromStream(getInputStream(imageUrlString), "name");
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Bitmap getBitmap(String imageUrlString) {
		return BitmapFactory.decodeStream(getInputStream(imageUrlString));
	}

	public static InputStream getInputStream(String imageUrlString) {
		try {
			return (InputStream) new URL(imageUrlString).getContent();
		} 
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	/** ----------- Gestion de la Cache ----------- **/

	public static enum CacheSubDirs { DEFAULT, LEAFLET_PAGES, OTHER }

	public static Bitmap getBitmapAndCache(Context context, String url) {
		return getBitmapAndCache(context, CacheSubDirs.DEFAULT, url);
	}

	public static Bitmap getBitmapAndCache(Context context, CacheSubDirs cacheSubDir, String url) {
		return getBitmapAndCache(context, CacheSubDirs.DEFAULT, url, true);
	}

	public static Bitmap getBitmapAndCache(Context context, CacheSubDirs cacheSubDir, String url, boolean returnBitmap) {
		//Long start = Calendar.getInstance().getTimeInMillis();
		File file = null;

		try {		
			File cacheDir = new File(context.getCacheDir(), cacheSubDir.toString());

			if(!cacheDir.exists())
				cacheDir.mkdir();

			file = new File(cacheDir, url.replaceAll("[|?*<\":>+\\[\\]/']", "_"));

			if(file.exists()) {

				if(returnBitmap) {

					Bitmap bitmap = null;
					int waitCount = 0;

					// Il se peut que le fichier existe mais qu'il n'ai pas encore été totalement écris dans la mémoire (parcequ'il est en cours de dl par exemple)
					// Dans ce cas, on attends un peu avant de retenter de choper l'image
					while(bitmap == null && waitCount < 10) {
						bitmap = BitmapFactory.decodeStream(new FileInputStream(file));

						if(bitmap == null) {
							Thread.sleep(50);
							waitCount++;
						}
					}
					//Log.e("getBitmapAndCache", (Calendar.getInstance().getTimeInMillis() - start) + " ms (Returned)");
					return bitmap;
				}
			}
			else {
				//Help_Dev.log("getBitmapAndCache", "Download : " + url);

				// Download image and save image to SD card
				FileOutputStream fileOutput = new FileOutputStream(file);
				InputStream inputStream = (InputStream) new URL(url).getContent();

				byte[] buffer = new byte[1024];
				int bufferLength = 0; // Used to store a temporary size of the buffer

				// Read through the input buffer and write the contents to the file
				while ((bufferLength = inputStream.read(buffer)) > 0 )
					fileOutput.write(buffer, 0, bufferLength);

				fileOutput.close();

				// Clear cacheDir if too big
				if (getDirSize(cacheDir) > getMaxSize(context))
					cleanDir(cacheDir, getMaxSize(context) / 2);

				// Read image from SD card
				if(returnBitmap)
					return BitmapFactory.decodeStream(new FileInputStream(file));
			}
		}

		catch (Exception e) { 
			e.printStackTrace();
			try {
				if(file != null) file.delete();
			} catch(Exception ex) {}
		}

		return null;
	}


	// ---------- Gestion de la cache ---------- //	

	private static Long MAX_SIZE; // 10 ou 20 Mo maximum par dossier (si smartphone ou tablette)
	private static final Object lock = new Object();

	private static void cleanDir(final File dir, final long finalSize) {
		//Help_Dev.log("getBitmapAndCache", "Clear dir : " + dir.getName());
		new Thread(new Runnable() {
			@Override
			public void run() {
				synchronized (lock) {
					try {
						File[] files = dir.listFiles();

						Arrays.sort(files, new FileDateComparator());

						for (File file : files) {
							file.delete();

							if(getDirSize(dir) < finalSize) break;
						}
					}
					catch (Exception e) { e.printStackTrace(); }
				}
			}
		}).start();
	}

	private static long getDirSize(File dir) {
		long size = 0;

		for (File file : dir.listFiles())
			if (file.isFile())
				size += file.length();
			else if(file.isDirectory())
				size += getDirSize(file);

		return size;
	}

	private static Long getMaxSize(Context context) {
		if(MAX_SIZE == null)
			MAX_SIZE = 20000000L;

		return MAX_SIZE;
	}

	private static class FileDateComparator implements Comparator<File>{
		@Override
		public int compare(File file1, File file2) {
			return Long.valueOf(file1.lastModified()).compareTo(file2.lastModified());
		}
	}


}

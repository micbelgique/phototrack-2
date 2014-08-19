package com.ulteam.phototrack.Activities.Helpers;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.ulteam.phototrack.R;
import com.ulteam.phototrack.Helpers.Help_ImageUrl;

public class AsyncImageView extends ImageView {

	/** -------------- Objects, Variables -------------- */
	/** ------------------------------------------------ */

	ButlerImageAsyncTask asyncTask;

	// Le ThreadPoolExecutor de base n'accepte que 5 asyncTasks en même temps ...
	// ... On en a bien plus ici, donc je crée un Executor qui accepte plus de tâches.
	static int corePoolSize = 60;
	static int maximumPoolSize = 80;
	static int keepAliveTime = 10;

	static final BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
	static Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);


	/** ------------------ Constructor ----------------- */
	/** ------------------------------------------------ */

	public AsyncImageView(Context context) {
		super(context);
	}

	public AsyncImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AsyncImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}


	/** ------------------- Override ------------------- */
	/** ------------------------------------------------ */

	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
		resetAsyncTask();
	}

	@Override
	public void setImageResource(int resId) {
		super.setImageResource(resId);
		resetAsyncTask();
	}

	@Override
	public void setImageDrawable(Drawable drawable) {
		super.setImageDrawable(drawable);
		resetAsyncTask();
	}

	// Reset l'url sinon lors du setImageUrl on risque de passer par ...
	// ... le "Download already in progress of finished" alors que ce n'est pas le cas
	private void resetAsyncTask() {
		try {
			if(asyncTask != null)
				asyncTask.url = null;
		}
		catch (Exception e) { /* Sécurité pour l'Async */ }
	}

	/** ------------------- Methods -------------------- */
	/** ------------------------------------------------ */

	public void setImageUrl(String url) {
		setImageUrl(url, null);
	}

	public void setImageUrl(String url, int defaultImage) {
		setImageUrl(url, null, defaultImage);
	}

	public void setImageUrl(String url, Integer threadPriority) {
		setImageUrl(url, threadPriority, R.drawable.pictures_item_background);
	}

	public void setImageUrl(String url, Integer threadPriority, int defaultImage) {

		setTag(url);

		if(asyncTask != null && asyncTask.url != null && asyncTask.url.equals(url)) {
			// Download already in progress or finished
		}
		else {
			setImageResource(defaultImage);

			if(url != null) {
				if(asyncTask != null) {
					asyncTask.cancel(false);
					asyncTask = null;	
				}

				asyncTask = new ButlerImageAsyncTask(url, threadPriority);

				if(Build.VERSION.SDK_INT >= 11) // API >= 11. Il faut préciser de lancer l'AsyncTask dans un pool plutôt qu'une queue
					asyncTask.executeOnExecutor(threadPoolExecutor);
				else
					asyncTask.execute();
			}
		}
	}


	/** -------------------- Class --------------------- */
	/** ------------------------------------------------ */

	private class ButlerImageAsyncTask extends AsyncTask<Void, Void, String> {

		String url;
		Bitmap bitmap;
		Integer threadPriority;

		public ButlerImageAsyncTask(String url, Integer threadPriority){
			super();
			this.url = url;
			this.threadPriority = threadPriority;
		}

		@Override
		protected String doInBackground(Void... params) {
			if(threadPriority != null)
				Thread.currentThread().setPriority(threadPriority);

			if(url != null)
				bitmap = Help_ImageUrl.getBitmapAndCache(getContext(), url);

			return url;
		}

		@Override
		protected void onPostExecute(String url) {
			try {
				if(getTag().equals(url) && bitmap != null)
					setImageBitmap(bitmap);
				
				setVisibility(View.VISIBLE);
			}
			catch(Exception e) { 
				e.printStackTrace();
			}

			bitmap = null;
		}

	}
}

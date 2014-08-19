package com.ulteam.phototrack.Activities;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.ulteam.phototrack.R;
import com.ulteam.phototrack.Activities.Helpers.BaseActivity;
import com.ulteam.phototrack.BusinessObjects.Picture;
import com.ulteam.phototrack.Helpers.Help_Azure;
import com.ulteam.phototrack.Helpers.Help_Configuration;
import com.ulteam.phototrack.Helpers.Help_JSON;
import com.ulteam.phototrack.Helpers.Help_String;
import com.ulteam.phototrack.Helpers.Upload.Uploader;
import com.ulteam.phototrack.Services.Svc_Picture;

public class Picture_Details extends BaseActivity implements OnClickListener, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

	/** -------------- Objects, Variables -------------- */
	/** ------------------------------------------------ */

	static final int REQUEST_IMAGE_CAPTURE = 1;

	private String currentPhotoPath;
	private int targetW, targetH;
	private boolean hasToSetPicture;

	LocationClient locationClient;
	Location location;


	/** --------------------- Views -------------------- */
	/** ------------------------------------------------ */

	ImageView imageView;
	ImageButton bt_Validate;
	EditText editText;
	ProgressDialog progressDialog;


	/** ------------------ LifeCycle ------------------- */
	/** ------------------------------------------------ */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picture_details);

		imageView = (ImageView) findViewById(R.id.imageView);
		editText = (EditText) findViewById(R.id.editText);
		bt_Validate = (ImageButton) findViewById(R.id.bt_Validate);
		bt_Validate.setOnClickListener(this);

		try {
			locationClient = new LocationClient(this, this, this);
		}
		catch(Exception e) {}

		takePicture();
	};

	@Override
	protected void onStart() {
		super.onStart();
		if(locationClient != null)
			locationClient.connect();
	}

	@Override
	protected void onStop() {
		if(locationClient != null)
			locationClient.disconnect();
		super.onStop();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus){
		targetW = imageView.getWidth();
		targetH = imageView.getHeight();

		if(hasToSetPicture) {
			setPic();
			hasToSetPicture = false;	
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
			// Full size
			if(targetW != 0 && targetW != 0)
				setPic();
			else
				hasToSetPicture = true;
		}
		else if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode != RESULT_OK) {
			finish();
		}
	}


	/** ------------------ Listeners ------------------- *
	/** ------------------------------------------------ */

	@Override
	public void onClick(View v) {
		if(v == bt_Validate) 
			upload();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) { }

	@Override
	public void onConnected(Bundle bundle) {
		location = locationClient.getLastLocation();
	}

	@Override
	public void onDisconnected() { }


	/** ------------------- Methods -------------------- */
	/** ------------------------------------------------ */

	// ----- Prise de la photo ----- //

	private void takePicture() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		if (takePictureIntent.resolveActivity(getPackageManager()) == null)
			return;

		File photoFile = createImageFile();

		if (photoFile != null) {
			takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
			startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
		}
	}

	private File createImageFile() {
		try {
			String imageFileName = "JPEG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "_"; // TimeStamp
			File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
			File image = File.createTempFile(imageFileName, ".jpg", storageDir );

			// Save a file: path for use with ACTION_VIEW intents
			currentPhotoPath = image.getAbsolutePath();
			return image;
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private void setPic() {
		// Get the dimensions of the bitmap
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		// Determine how much to scale down the image
		int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

		// Decode the image file into a Bitmap sized to fill the View
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		imageView.setImageBitmap(BitmapFactory.decodeFile(currentPhotoPath, bmOptions));
	}

	// ----- Upload de la photo ----- //

	private void upload() {
		new AsyncTask<Void, Void, String>() {

			protected void onPreExecute() {
				progressDialog = ProgressDialog.show(Picture_Details.this, null, "Transfert en cours ...", true, false);
			};

			@Override
			protected String doInBackground(Void... params) {
				return Uploader.upload(Uri.fromFile(new File(currentPhotoPath)), getBaseContext());
			}

			protected void onPostExecute(String result) {
				progressDialog.dismiss();
				if(Help_String.isNotNullNorEmpty(result))
					uploadSucceeded(result);
				else
					uploadFailed();
			};

		}.execute();
	}

	private void uploadSucceeded(final String imageUrl) {
		new AsyncTask<Void, Void, Picture>() {

			protected void onPreExecute() {
				progressDialog = ProgressDialog.show(Picture_Details.this, null, "Traitement en cours ...", true, false);
			};

			@Override
			protected Picture doInBackground(Void... params) {
				try {
					Picture picture = new Picture(null, imageUrl, Calendar.getInstance(), editText.getText().toString(), location);
					picture.imagePath = currentPhotoPath;

					JSONObject jsonObject = new JSONObject()
					.put("WorkEntityID", currentWork.id.toString())
					.put("ImageUrl", picture.imageUrl)
					.put("Comment", picture.comment)
					.put("Latitude", 0)
					.put("Longitude", 0)
					.put("Type", 0)
					.put("DatetimeCreated", Help_JSON.getJsonDate(Calendar.getInstance()));

					if(location != null)
						jsonObject
						.put("Latitude", location.getLatitude())
						.put("Longitude", location.getLongitude());

					JSONObject result = Help_Azure.getResponseFromAzure(jsonObject, Help_Configuration.SERVICES_URL + "api/picture/create", null, true);

					if(result != null && result.getBoolean("Success")) {
						picture.id = UUID.fromString(result.getString("PictureEntityID"));						
						return picture;
					}
				}
				catch(Exception e) { e.printStackTrace(); }

				return null;
			}

			protected void onPostExecute(Picture picture) {
				progressDialog.dismiss();

				if(picture != null) {
					Svc_Picture.addPicture(currentWork, picture, false);
					setResult(RESULT_OK);
					finish();
				}
				else
					Toast.makeText(getBaseContext(), "Une erreur est survenue. Veuillez réessayer.", Toast.LENGTH_SHORT).show();
			};

		}.execute();
	}

	private void uploadFailed() {
		Toast.makeText(getBaseContext(), "Une erreur est survenue. Reessayer s'il vous plait", Toast.LENGTH_SHORT).show();
	}

}
package com.ulteam.phototrack.Activities;

import java.io.File;
import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ulteam.phototrack.R;
import com.ulteam.phototrack.Activities.Helpers.AsyncImageView;
import com.ulteam.phototrack.Activities.Helpers.BaseActivity;
import com.ulteam.phototrack.Activities.Helpers.Help_ImageView;
import com.ulteam.phototrack.BusinessObjects.Picture;
import com.ulteam.phototrack.Helpers.Help_String;
import com.ulteam.phototrack.Services.Svc_Picture;

public class Pictures extends BaseActivity implements OnClickListener {

	/** -------------- Objects, Variables -------------- */
	/** ------------------------------------------------ */

	private static final int REQUEST_CREATE_PICTURE = 3663;


	/** --------------------- Views -------------------- */
	/** ------------------------------------------------ */

	ListView listView;
	LinearLayout bt_Create;
	ImageButton bt_Create2;
	ProgressDialog progressDialog;


	/** ------------------ LifeCycle ------------------- */
	/** ------------------------------------------------ */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);

		getActionBar().setTitle("Photos de " + currentWork.name);

		listView = (ListView) findViewById(R.id.listView);
		listView.setDividerHeight(0);

		bt_Create = (LinearLayout) findViewById(R.id.bt_Create);
		bt_Create.setOnClickListener(this);

		bt_Create2 = (ImageButton) findViewById(R.id.bt_Create2);
		bt_Create2.setOnClickListener(this);

		((ImageView) findViewById(R.id.imageView_Empty)).setImageResource(R.drawable.pictures_background);
		((TextView) findViewById(R.id.textView_Empty1)).setText("On y est !");
		((TextView) findViewById(R.id.textView_Empty2)).setText("Il ne vous reste plus qu'à prendre vos photos");
		((TextView) findViewById(R.id.textView_Empty3)).setText("Prendre une photo");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(requestCode == REQUEST_CREATE_PICTURE && resultCode == RESULT_OK)
			listView.setAdapter(new Adapter(Svc_Picture.getPictures(currentWork, false)));
	}


	@Override
	protected void onResume() {
		super.onResume();
		displayPictures();
	}


	/** ------------------ Listeners ------------------- *
	/** ------------------------------------------------ */

	@Override
	public void onClick(View v) {
		if(v == bt_Create || v == bt_Create2)
			startActivityForResult(new Intent(Pictures.this, Picture_Details.class), REQUEST_CREATE_PICTURE);
	}


	/** ------------------- Methods -------------------- */
	/** ------------------------------------------------ */

	private void displayPictures() {
		new AsyncTask<Void, Void, ArrayList<Picture>>() {

			protected void onPreExecute() {
				progressDialog = ProgressDialog.show(Pictures.this, null, "Traitement en cours ...", true, false);
			};

			@Override
			protected ArrayList<Picture> doInBackground(Void... params) {
				return Svc_Picture.getPictures(currentWork, true);
			}

			protected void onPostExecute(ArrayList<Picture> pictures) {
				progressDialog.dismiss();

				if(Svc_Picture.getPictures(currentWork, false) != null) {
					listView.setAdapter(new Adapter(Svc_Picture.getPictures(currentWork, false)));
					if(Svc_Picture.getPictures(currentWork, false).size() > 0)
						findViewById(R.id.layout_Empty).setVisibility(View.GONE);
				}
				else {
					findViewById(R.id.layout_Empty).setVisibility(View.GONE);
					bt_Create.setVisibility(View.GONE);
					Toast.makeText(getBaseContext(), "Une erreur est survenue. Veuillez réessayer.", Toast.LENGTH_SHORT).show();
				}
			};

		}.execute();
	}


	/** ----------------- GUI Adapter ------------------ */
	/** ------------------------------------------------ */

	private class Adapter extends BaseAdapter {

		private ArrayList<Picture> pictures;

		private class ObjectsHolder {
			// Picture picture;
			TextView textView;
			AsyncImageView imageView;
		}

		public Adapter(ArrayList<Picture> pictures) {
			this.pictures = pictures;
		}

		@Override
		public int getCount() {
			return pictures.size();
		}

		@Override
		public Picture getItem(int position) {
			return pictures.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Picture picture = getItem(position);
			ObjectsHolder objectsHolder;

			if(convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.pictures_list_item, null);
				objectsHolder = new ObjectsHolder();
				objectsHolder.textView = (TextView) convertView.findViewById(R.id.textView);
				objectsHolder.imageView = (AsyncImageView) convertView.findViewById(R.id.imageView);
				convertView.setTag(objectsHolder);
			}
			else
				objectsHolder = (ObjectsHolder) convertView.getTag();

			if(Help_String.isNotNullNorEmpty(picture.comment)) {
				objectsHolder.textView.setText(picture.comment);
				objectsHolder.textView.setVisibility(View.VISIBLE);
			}
			else
				objectsHolder.textView.setVisibility(View.INVISIBLE);

			if(picture.imagePath != null)
				objectsHolder.imageView.setImageBitmap(Help_ImageView.decodeFile(Uri.fromFile(new File(picture.imagePath)), getBaseContext(), 500));
			else
				objectsHolder.imageView.setImageUrl(picture.imageUrl);

			return convertView;
		}

	}

}

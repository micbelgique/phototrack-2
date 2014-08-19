package com.ulteam.phototrack.Activities;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.ulteam.phototrack.Activities.Helpers.BaseActivity;
import com.ulteam.phototrack.BusinessObjects.Work;
import com.ulteam.phototrack.Services.Svc_Picture;
import com.ulteam.phototrack.Services.Svc_Work;

public class Works extends BaseActivity implements OnClickListener {

	/** -------------- Objects, Variables -------------- */
	/** ------------------------------------------------ */

	private static final int REQUEST_CREATE_WORK = 6743;


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

		listView = (ListView) findViewById(R.id.listView);

		bt_Create = (LinearLayout) findViewById(R.id.bt_Create);
		bt_Create.setOnClickListener(this);

		bt_Create2 = (ImageButton) findViewById(R.id.bt_Create2);
		bt_Create2.setOnClickListener(this);

		((ImageView) findViewById(R.id.imageView_Empty)).setImageResource(R.drawable.works_background);
		((TextView) findViewById(R.id.textView_Empty1)).setText("Félicitations !");
		((TextView) findViewById(R.id.textView_Empty2)).setText("Vous avez créé votre 1er chantier.\nCréez maintenant des travaux en rapport avec ce chantier et ajoutez leurs des photos");
		((TextView) findViewById(R.id.textView_Empty3)).setText("Créer un nouveau travail");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(requestCode == REQUEST_CREATE_WORK && resultCode == RESULT_OK)
			listView.setAdapter(new Adapter(Svc_Work.getWorks(currentSite, false)));
	}

	@Override
	protected void onResume() {
		super.onResume();
		displayWorks();
	}


	/** ------------------ Listeners ------------------- *
	/** ------------------------------------------------ */

	@Override
	public void onClick(View v) {
		if(v == bt_Create || v == bt_Create2)		
			startActivityForResult(new Intent(getBaseContext(), Create_Work.class), REQUEST_CREATE_WORK);
	}


	/** ------------------- Methods -------------------- */
	/** ------------------------------------------------ */

	private void displayWorks() {
		new AsyncTask<Void, Void, ArrayList<Work>>() {

			protected void onPreExecute() {
				progressDialog = ProgressDialog.show(Works.this, null, "Traitement en cours ...", true, false);
			};

			@Override
			protected ArrayList<Work> doInBackground(Void... params) {
				return Svc_Work.getWorks(currentSite, true);
			}

			protected void onPostExecute(ArrayList<Work> sites) {
				progressDialog.dismiss();

				if(Svc_Work.getWorks(currentSite, false) != null) {
					listView.setAdapter(new Adapter(Svc_Work.getWorks(currentSite, false)));
					if(Svc_Work.getWorks(currentSite, true).size() > 0)
						findViewById(R.id.layout_Empty).setVisibility(View.GONE);
				}
				else{
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

		private ArrayList<Work> works;

		private class ObjectsHolder{
			Work work;
			TextView textView_Title, textView_SubTitle;
			TextView textView_FirstLetter;
		}

		public Adapter(ArrayList<Work> works) {
			this.works = works;
		}

		@Override
		public int getCount() {
			return works.size();
		}

		@Override
		public Work getItem(int position) {
			return works.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Work work = getItem(position);
			ObjectsHolder objectsHolder;

			if(convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.works_list_item, null);
				objectsHolder = new ObjectsHolder();
				objectsHolder.textView_Title = (TextView) convertView.findViewById(R.id.textView_Title);
				objectsHolder.textView_SubTitle = (TextView) convertView.findViewById(R.id.textView_SubTitle);
				objectsHolder.textView_FirstLetter = (TextView) convertView.findViewById(R.id.textView_FirstLetter);
				convertView.setTag(objectsHolder);
			}
			else
				objectsHolder = (ObjectsHolder) convertView.getTag();

			objectsHolder.textView_Title.setText(work.name);
			objectsHolder.textView_FirstLetter.setText(String.valueOf(work.name.charAt(0)));
			
			objectsHolder.textView_SubTitle.setText(getResources().getQuantityString(R.plurals.X_pictures, work.picturesCount, work.picturesCount));
			objectsHolder.work = work;

			convertView.setOnClickListener(itemClickListener);

			return convertView;
		}

		private OnClickListener itemClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(currentWork != ((ObjectsHolder) v.getTag()).work)
					Svc_Picture.invalidate();
				currentWork = ((ObjectsHolder) v.getTag()).work;
				startActivity(new Intent(Works.this, Pictures.class));
			}
		};

	}

}

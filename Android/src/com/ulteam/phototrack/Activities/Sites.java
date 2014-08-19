package com.ulteam.phototrack.Activities;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.ulteam.phototrack.R;
import com.ulteam.phototrack.Activities.Helpers.BaseActivity;
import com.ulteam.phototrack.BusinessObjects.Site;
import com.ulteam.phototrack.Helpers.Help_Pixel;
import com.ulteam.phototrack.Helpers.Help_String;
import com.ulteam.phototrack.Services.Svc_Site;
import com.ulteam.phototrack.Services.Svc_Work;

public class Sites extends BaseActivity implements OnClickListener {

	/** -------------- Objects, Variables -------------- */
	/** ------------------------------------------------ */

	private static final int REQUEST_CREATE_SITE = 4739;


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

		((ImageView) findViewById(R.id.imageView_Empty)).setImageResource(R.drawable.sites_background);
		((TextView) findViewById(R.id.textView_Empty1)).setText("Bienvenue !");
		((TextView) findViewById(R.id.textView_Empty2)).setText("Créez un chantier et ajoutez-lui des travaux pour suivre son évolution");
		((TextView) findViewById(R.id.textView_Empty3)).setText("Créer un nouveau chantier");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(requestCode == REQUEST_CREATE_SITE && resultCode == RESULT_OK)
			listView.setAdapter(new Adapter(Svc_Site.getSites(false)));
	}

	@Override
	protected void onResume() {
		super.onResume();
		displaySites();
	}


	/** ------------------ Listeners ------------------- *
	/** ------------------------------------------------ */

	@Override
	public void onClick(View v) {
		if(v == bt_Create || v == bt_Create2)
			startActivityForResult(new Intent(getBaseContext(), Create_Or_Join_Site.class), REQUEST_CREATE_SITE);
	}


	/** ------------------- Methods -------------------- */
	/** ------------------------------------------------ */

	private void displaySites() {
		new AsyncTask<Void, Void, ArrayList<Site>>() {

			protected void onPreExecute() {
				progressDialog = ProgressDialog.show(Sites.this, null, "Traitement en cours ...", true, false);
			};

			@Override
			protected ArrayList<Site> doInBackground(Void... params) {
				return Svc_Site.getSites(true);
			}

			protected void onPostExecute(ArrayList<Site> sites) {
				progressDialog.dismiss();

				if(Svc_Site.getSites(false) != null) {
					listView.setAdapter(new Adapter(Svc_Site.getSites(false)));
					if(Svc_Site.getSites(false).size() > 0)
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

		private ArrayList<Site> sites;

		private class ObjectsHolder {
			Site site;
			TextView textView_Title, textView_SubTitle, textView_SubSubTitle;
			TextView textView_FirstLetter;
			ImageButton bt_DotsMenu;
		}

		public Adapter(ArrayList<Site> sites) {
			this.sites = sites;
		}

		@Override
		public int getCount() {
			return sites.size();
		}

		@Override
		public Site getItem(int position) {
			return sites.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Site site = getItem(position);
			ObjectsHolder objectsHolder;

			if(convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.sites_list_item, null);
				objectsHolder = new ObjectsHolder();
				objectsHolder.textView_Title = (TextView) convertView.findViewById(R.id.textView_Title);
				objectsHolder.textView_SubTitle = (TextView) convertView.findViewById(R.id.textView_SubTitle);
				objectsHolder.textView_SubSubTitle = (TextView) convertView.findViewById(R.id.textView_SubSubTitle);
				objectsHolder.textView_FirstLetter = (TextView) convertView.findViewById(R.id.textView_FirstLetter);
				objectsHolder.bt_DotsMenu = (ImageButton) convertView.findViewById(R.id.bt_DotsMenu);
				convertView.setTag(objectsHolder);
			}
			else
				objectsHolder = (ObjectsHolder) convertView.getTag();

			objectsHolder.textView_Title.setText(site.name);
			objectsHolder.textView_FirstLetter.setText(String.valueOf(site.name.charAt(0)));

			if(Help_String.isNotNullNorEmpty(site.organization)) {
				objectsHolder.textView_SubTitle.setText(site.organization);
				objectsHolder.textView_SubTitle.setVisibility(View.VISIBLE);
			}
			else
				objectsHolder.textView_SubTitle.setVisibility(View.GONE);

			objectsHolder.textView_SubSubTitle.setText(getResources().getQuantityString(R.plurals.X_works, site.workCount, site.workCount));
			objectsHolder.site = site;

			objectsHolder.bt_DotsMenu.setTag(site);
			objectsHolder.bt_DotsMenu.setOnClickListener(dotsMenuClickListener);

			convertView.setOnClickListener(itemClickListener);

			return convertView;
		}

		private OnClickListener itemClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(currentSite != ((ObjectsHolder) v.getTag()).site)
					Svc_Work.invalidate();
				currentSite = ((ObjectsHolder) v.getTag()).site;
				startActivity(new Intent(Sites.this, Works.class));
			}
		};

		private OnClickListener dotsMenuClickListener = new OnClickListener() {
			@Override
			public void onClick(final View view1) {
				View popupView = ((LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.sites_list_item_dots_menu, null);  
				final PopupWindow popupWindow = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);  
				popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.transparent));
				popupWindow.setOutsideTouchable(true);
				
				popupView.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						Site site = ((Site) view1.getTag());
						
						Intent email = new Intent(Intent.ACTION_SEND);
						email.setType("message/rfc822");
						email.putExtra(Intent.EXTRA_SUBJECT, "Rejoins moi sur le chantier " + site.name);
						email.putExtra(Intent.EXTRA_TEXT, "Voici la clef de partage du chantier " + site.name + " : " + site.shareCode);
			 
						startActivity(Intent.createChooser(email, "Envoyer email"));
						
						popupWindow.dismiss();
					}});

				popupWindow.showAsDropDown(view1, - (int) Help_Pixel.getPixels(getResources(), 110), (int) Help_Pixel.getPixels(getResources(), -5));
			}
		};

	}

}
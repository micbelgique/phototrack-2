package com.ulteam.phototrack.Activities;

import java.util.UUID;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ulteam.phototrack.R;
import com.ulteam.phototrack.Activities.Helpers.BaseActivity;
import com.ulteam.phototrack.BusinessObjects.Site;
import com.ulteam.phototrack.Helpers.Help_Azure;
import com.ulteam.phototrack.Helpers.Help_Configuration;
import com.ulteam.phototrack.Helpers.Help_Keyboard;
import com.ulteam.phototrack.Services.Svc_Site;

public class Create_Or_Join_Site extends BaseActivity implements OnClickListener {

	/** -------------- Objects, Variables -------------- */
	/** ------------------------------------------------ */


	/** --------------------- Views -------------------- */
	/** ------------------------------------------------ */

	Button bt_Join, bt_Create;
	ImageButton bt_ValidateJoin, bt_ValidateCreate;
	EditText editText_Site, editText_Organization, editText_ShareCode;
	LinearLayout layout_CreateOrJoin;
	RelativeLayout layout_Create, layout_Join;
	ProgressDialog progressDialog;


	/** ------------------ LifeCycle ------------------- */
	/** ------------------------------------------------ */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_or_join_site);

		// ----- Create or Join ----- //
		layout_Create = (RelativeLayout) findViewById(R.id.layout_Create);
		layout_Join = (RelativeLayout) findViewById(R.id.layout_Join);
		layout_CreateOrJoin = (LinearLayout) findViewById(R.id.layout_CreateOrJoin);

		bt_Join = (Button) findViewById(R.id.bt_Join);
		bt_Join.setOnClickListener(this);

		bt_Create = (Button) findViewById(R.id.bt_Create);
		bt_Create.setOnClickListener(this);

		// ----- Create ----- //
		editText_Site = (EditText) findViewById(R.id.editText_Site);
		editText_Site.addTextChangedListener(createTextWatcherListener);

		editText_Organization = (EditText) findViewById(R.id.editText_Organization);

		bt_ValidateCreate = (ImageButton) findViewById(R.id.bt_ValidateCreate);
		bt_ValidateCreate.setEnabled(false);
		bt_ValidateCreate.setOnClickListener(this);

		// ----- Join ----- //
		editText_ShareCode = (EditText) findViewById(R.id.editText_ShareCode);
		editText_ShareCode.addTextChangedListener(joinTextWatcherListener);

		bt_ValidateJoin = (ImageButton) findViewById(R.id.bt_ValidateJoin);
		bt_ValidateJoin.setEnabled(false);
		bt_ValidateJoin.setOnClickListener(this);
	}


	/** ------------------ Listeners ------------------- */
	/** ------------------------------------------------ */

	@Override
	public void onBackPressed() {
		if(layout_CreateOrJoin.getVisibility() == View.GONE) {
			layout_CreateOrJoin.setVisibility(View.VISIBLE);
			layout_Create.setVisibility(View.GONE);
			layout_Join.setVisibility(View.GONE);
			Help_Keyboard.hideKeyboard(getBaseContext(), editText_Site);
		}
		else
			super.onBackPressed();
	}

	@Override
	public void onClick(View v) {
		if(v == bt_Join)
			join();
		else if(v == bt_Create)
			create();
		else if(v == bt_ValidateJoin)
			validateJoin();
		else if(v == bt_ValidateCreate)
			validateCreate();
	}

	TextWatcher createTextWatcherListener = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }
		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }
		@Override
		public void afterTextChanged(Editable s) {
			bt_ValidateCreate.setEnabled(editText_Site.length() > 0);
		}
	};

	TextWatcher joinTextWatcherListener = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }
		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }
		@Override
		public void afterTextChanged(Editable s) {
			bt_ValidateJoin.setEnabled(editText_ShareCode.length() > 0);
		}
	};


	/** ------------------- Methods -------------------- */
	/** ------------------------------------------------ */

	private void join() {
		layout_CreateOrJoin.setVisibility(View.GONE);
		layout_Join.setVisibility(View.VISIBLE);
	}

	private void create() {
		layout_CreateOrJoin.setVisibility(View.GONE);
		layout_Create.setVisibility(View.VISIBLE);
	}

	private void validateJoin() {
		new AsyncTask<Void, Void, Boolean>() {

			protected void onPreExecute() {
				progressDialog = ProgressDialog.show(Create_Or_Join_Site.this, null, "Traitement en cours ...", true, false);
			};

			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					JSONObject jsonObject = new JSONObject()
					.put("ShareCode", editText_ShareCode.getText().toString());

					JSONObject result = Help_Azure.getResponseFromAzure(jsonObject, Help_Configuration.SERVICES_URL + "api/site/join", null, true);

					return result != null && result.getBoolean("Success");
				}
				catch(Exception e) { e.printStackTrace(); }

				return false;
			}

			protected void onPostExecute(Boolean success) {
				progressDialog.dismiss();

				if(success) {
					Svc_Site.invalidate();
					setResult(RESULT_OK);
					finish();
				}
				else
					Toast.makeText(getBaseContext(), "Une erreur est survenue. Veuillez réessayer.", Toast.LENGTH_SHORT).show();
			};

		}.execute();
	}

	private void validateCreate() {
		new AsyncTask<Void, Void, Site>() {

			protected void onPreExecute() {
				progressDialog = ProgressDialog.show(Create_Or_Join_Site.this, null, "Traitement en cours ...", true, false);
			};

			@Override
			protected Site doInBackground(Void... params) {
				try {
					Site site = new Site(							
							null,
							editText_Site.getText().toString(), 
							null, // Organization
							null, // Location
							null, // Street
							null, // City
							null, // Zip
							null, // ShareCode
							0); // workCount

					JSONObject jsonObject = new JSONObject()
					.put("Name", editText_Site.getText().toString())
					.put("Latitude", 50.4790217)
					.put("Longitude", 6.1087749);

					JSONObject result = Help_Azure.getResponseFromAzure(jsonObject, Help_Configuration.SERVICES_URL + "api/site/create", null, true);

					if(result != null && result.getBoolean("Success")) {
						site.id = UUID.fromString(result.getString("SiteEntityID"));
						site.shareCode = result.getString("ShareCode");
						return site;
					}
				}
				catch(Exception e) { e.printStackTrace(); }

				return null;
			}

			protected void onPostExecute(Site site) {
				progressDialog.dismiss();

				if(site != null) {
					Svc_Site.addSite(site, false);
					setResult(RESULT_OK);
					finish();
				}
				else
					Toast.makeText(getBaseContext(), "Une erreur est survenue. Veuillez réessayer.", Toast.LENGTH_SHORT).show();
			};

		}.execute();
	}

}

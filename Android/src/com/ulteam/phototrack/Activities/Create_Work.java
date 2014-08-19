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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ulteam.phototrack.R;
import com.ulteam.phototrack.Activities.Helpers.BaseActivity;
import com.ulteam.phototrack.BusinessObjects.Work;
import com.ulteam.phototrack.Helpers.Help_Azure;
import com.ulteam.phototrack.Helpers.Help_Configuration;
import com.ulteam.phototrack.Services.Svc_Work;

public class Create_Work extends BaseActivity implements OnClickListener {

	/** -------------- Objects, Variables -------------- */
	/** ------------------------------------------------ */


	/** --------------------- Views -------------------- */
	/** ------------------------------------------------ */

	ImageButton bt_Create;
	EditText editText;
	ProgressDialog progressDialog;


	/** ------------------ LifeCycle ------------------- */
	/** ------------------------------------------------ */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_work);

		bt_Create = (ImageButton) findViewById(R.id.bt_Create);
		bt_Create.setEnabled(false);
		bt_Create.setOnClickListener(this);

		editText = (EditText) findViewById(R.id.editText);
		editText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }
			@Override
			public void afterTextChanged(Editable s) { 
				bt_Create.setEnabled(s.length() > 0);
			}
		});
	}


	/** ------------------ Listeners ------------------- *
	/** ------------------------------------------------ */

	@Override
	public void onClick(View v) {
		if(v == bt_Create)
			createWork();
	}


	/** ------------------- Methods -------------------- */
	/** ------------------------------------------------ */

	private void createWork() {
		new AsyncTask<Void, Void, Work>() {

			protected void onPreExecute() {
				progressDialog = ProgressDialog.show(Create_Work.this, null, "Traitement en cours ...", true, false);
			};

			@Override
			protected Work doInBackground(Void... params) {
				try {
					Work work = new Work(null, editText.getText().toString(), 0);
					
					JSONObject jsonObject = new JSONObject()
					.put("Name", editText.getText().toString())
					.put("SiteEntityID", currentSite.id.toString());
					
					JSONObject result = Help_Azure.getResponseFromAzure(jsonObject, Help_Configuration.SERVICES_URL + "api/work/create", null, true);

					if(result != null && result.getBoolean("Success")) {
						work.id = UUID.fromString(result.getString("WorkEntityID"));
						return work;
					}
				}
				catch(Exception e) { e.printStackTrace(); }
				
				return null;
			}

			protected void onPostExecute(Work work) {
				progressDialog.dismiss();

				if(work != null) {
					Svc_Work.addWork(currentSite, work, false);
					setResult(RESULT_OK);
					finish();
				}
				else
					Toast.makeText(getBaseContext(), "Une erreur est survenue. Veuillez réessayer.", Toast.LENGTH_SHORT).show();
			};

		}.execute();
	}

}
package com.ulteam.phototrack.Activities;

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
import com.ulteam.phototrack.Helpers.Help_Azure;
import com.ulteam.phototrack.Helpers.Help_Configuration;
import com.ulteam.phototrack.Helpers.Help_Connection;
import com.ulteam.phototrack.Helpers.Help_String;
import com.ulteam.phototrack.Services.Svc_Profil;

public class SignUp extends BaseActivity implements OnClickListener, TextWatcher {

	/** -------------- Objects, Variables -------------- */
	/** ------------------------------------------------ */


	//	new AsyncTask<Void, Void, Void>() {
	//
	//		protected void onPreExecute() {
	//			progressDialog = ProgressDialog.show(Activity.this, null, "Traitement en cours ...", true, false);
	//		};
	//		
	//		@Override
	//		protected Void doInBackground(Void... params) {
	//			return null;
	//		}
	//		
	//		protected void onPostExecute(Void result) {
	//			progressDialog.dismiss();
	//			
	//			if(result){}
	//			else
	//				Toast.makeText(getBaseContext(), "Une erreur est survenue. Veuillez réessayer.", Toast.LENGTH_SHORT).show();
	//		};
	//		
	//	}.execute();

	/** --------------------- Views -------------------- */
	/** ------------------------------------------------ */

	ImageButton bt_Validate;
	EditText editText_FirstName, editText_LastName, editText_Phone, editText_Company, editText_Email, editText_Password;
	ProgressDialog progressDialog;


	/** ------------------ LifeCycle ------------------- */
	/** ------------------------------------------------ */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);

		editText_FirstName = (EditText) findViewById(R.id.editText_FirstName);
		editText_FirstName.addTextChangedListener(this);
		editText_LastName = (EditText) findViewById(R.id.editText_LastName);
		editText_LastName.addTextChangedListener(this);
		editText_Phone = (EditText) findViewById(R.id.editText_Phone);
		editText_Phone.addTextChangedListener(this);
		editText_Company = (EditText) findViewById(R.id.editText_Company);
		editText_Company.addTextChangedListener(this);
		editText_Email = (EditText) findViewById(R.id.editText_Email);
		editText_Email.addTextChangedListener(this);
		editText_Password = (EditText) findViewById(R.id.editText_Password);
		editText_Password.addTextChangedListener(this);

		bt_Validate = (ImageButton) findViewById(R.id.bt_Validate);
		bt_Validate.setEnabled(false);
		bt_Validate.setOnClickListener(this);
	}


	/** ------------------ Listeners ------------------- *
	/** ------------------------------------------------ */

	@Override
	public void onClick(View v) {
		if(v == bt_Validate)
			signUp();
	}

	@Override
	public void afterTextChanged(Editable s) {
		bt_Validate.setEnabled(
				// editText_FirstName.getText().toString().length() > 0
				// && editText_LastName.getText().toString().length() > 0
				// && editText_Phone.getText().toString().length() > 0
				// && editText_Company.getText().toString().length() > 0
				editText_Email.getText().toString().length() > 0
				&& Help_String.isEmail(editText_Email.getText().toString())
				&& editText_Password.getText().toString().length() > 5);
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }


	/** ------------------- Methods -------------------- */
	/** ------------------------------------------------ */

	private void signUp() {
		new AsyncTask<Void, Void, String>() {

			protected void onPreExecute() {
				if(Help_Connection.isOnline(getBaseContext()))
					progressDialog = ProgressDialog.show(SignUp.this, null, "Traitement en cours ...", true, false);
				else
					Toast.makeText(getBaseContext(), "Veuillez vous à internet", Toast.LENGTH_SHORT).show();
			};

			@Override
			protected String doInBackground(Void... params) {
				try {
					// ----- SignUp ----- //
					JSONObject json = new JSONObject()
					.put("Email", editText_Email.getText().toString())
					.put("Password", editText_Password.getText().toString())
					.put("ConfirmPassword", editText_Password.getText().toString());

					JSONObject result = Help_Azure.getResponseFromAzure(json, Help_Configuration.SERVICES_URL + "api/Account/Register", null);

					// ----- SignIn ----- //
					if(result != null)
						return SignIn.signInRequest(editText_Email.getText().toString(), editText_Password.getText().toString());
				}
				catch(Exception e) { e.printStackTrace(); }

				return null;
			}

			protected void onPostExecute(String token) {
				progressDialog.dismiss();
				if(token != null) {
					Svc_Profil.setToken(token);
					setResult(RESULT_OK);
					finish();
				}
				else
					Toast.makeText(getBaseContext(), "Une erreur est survenue. Réessayez s'il vous plait", Toast.LENGTH_SHORT).show();
			};

		}.execute();
	}

}

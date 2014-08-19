package com.ulteam.phototrack.Activities;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ulteam.phototrack.R;
import com.ulteam.phototrack.Activities.Helpers.BaseActivity;
import com.ulteam.phototrack.Helpers.Help_Azure;
import com.ulteam.phototrack.Services.Svc_Profil;

public class SignIn extends BaseActivity implements OnClickListener {

	/** -------------- Objects, Variables -------------- */
	/** ------------------------------------------------ */

	private static final int REQUEST_SIGNUP = 3754;


	/** --------------------- Views -------------------- */
	/** ------------------------------------------------ */

	Button bt_SignIn, bt_SignUp;
	EditText editText_LogIn, editText_Password;

	ProgressDialog progressDialog;


	/** ------------------ LifeCycle ------------------- */
	/** ------------------------------------------------ */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_in);

		getActionBar().hide();

		bt_SignIn = (Button) findViewById(R.id.bt_SignIn);
		bt_SignIn.setOnClickListener(this);

		bt_SignUp = (Button) findViewById(R.id.bt_SignUp);
		bt_SignUp.setOnClickListener(this);

		editText_LogIn = (EditText) findViewById(R.id.editText_Login);
		editText_Password = (EditText) findViewById(R.id.editText_Password);
		
		editText_LogIn.setText("valentin@live.com");
		editText_Password.setText("123456");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(requestCode == REQUEST_SIGNUP && resultCode == RESULT_OK)
			goToSites();
	}


	/** ------------------ Listeners ------------------- *
	/** ------------------------------------------------ */

	@Override
	public void onClick(View v) {
		if(v == bt_SignIn)
			signIn();

		else if(v == bt_SignUp)
			startActivityForResult(new Intent(SignIn.this, SignUp.class), REQUEST_SIGNUP);
	}


	/** ------------------- Methods -------------------- */
	/** ------------------------------------------------ */

	private void goToSites() {
		startActivity(new Intent(SignIn.this, Sites.class));
		finish();
	}

	public void signIn() {
		new AsyncTask<Void, Void, String>(){

			protected void onPreExecute() {
				progressDialog = ProgressDialog.show(SignIn.this, null, "Traitement en cours ...", true, false);
			};

			protected String doInBackground(Void... params) {
				return signInRequest(editText_LogIn.getText().toString(), editText_Password.getText().toString());
			}

			protected void onPostExecute(String token) {
				progressDialog.dismiss();

				if(token != null) {
					Svc_Profil.setToken(token);
					goToSites();
				}
				else
					Toast.makeText(getBaseContext(), "Une erreur est survenue. Réessayez s'il vous plait", Toast.LENGTH_SHORT).show();
			};
		}.execute();
	}


	/** ------------------- Divers --------------------- */
	/** ------------------------------------------------ */

	public static String signInRequest(String userName, String password) {
		try {
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("grant_type", "password"));
			nameValuePairs.add(new BasicNameValuePair("username", userName));
			nameValuePairs.add(new BasicNameValuePair("password", password));

			JSONObject result = Help_Azure.getResponseFromAzure(nameValuePairs, Help_Configuration.SERVICES_URL + "/Token");

			return result.getString("access_token");
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}

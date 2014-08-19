package com.ulteam.phototrack.Helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.ulteam.phototrack.Services.Svc_Profil;

public class Help_Azure {

	// ----------- Azure ------------ //
	// ------------------------------ //

	public static JSONObject getResponseFromAzure(JSONObject jsonObject, String url){
		return getResponseFromAzure(jsonObject, url, null);
	}

	public static JSONObject getResponseFromAzure(JSONObject jsonObject, String url, String requestString){
		return getResponseFromAzure(jsonObject, url, requestString, false);
	}

	public static JSONObject getResponseFromAzure(JSONObject jsonObject, String url, String requestString, boolean putTokenInHeader){
		try {		
			HttpClient client = getNewHttpClient();

			JSONObject json = new JSONObject();
			if(requestString != null) 
				json.put(requestString, jsonObject);
			else
				json = jsonObject;

			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(new StringEntity(json.toString(), "UTF-8"));
			httpPost.setHeader("Content-Type", "application/json");
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Accept-Encoding", "gzip");

			if(putTokenInHeader) 
				httpPost.setHeader("Authorization", Svc_Profil.getToken());

			client.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
			client.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);

			HttpResponse response = client.execute(httpPost);

			if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
				return null;
			else
				return getJSONObject(response);

		} 
		catch (JSONException e) { e.printStackTrace(); } 
		catch (IllegalStateException e) { e.printStackTrace(); }
		catch (IOException e) { e.printStackTrace(); }

		return null;
	} 

	public static JSONObject getResponseFromAzure(ArrayList<NameValuePair> nameValuePairs, String url){
		try {		
			HttpClient httpClient = getNewHttpClient();
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Accept-Encoding", "gzip");

			httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
			httpClient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);

			HttpResponse response = httpClient.execute(httpPost);

			if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
				return null;
			else
				return getJSONObject(response);
		} 
		catch (Exception e) { e.printStackTrace(); }

		return null;
	} 

	private static HttpClient getNewHttpClient() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			HttpConnectionParams.setConnectionTimeout(params, 8000);
			HttpConnectionParams.setSoTimeout(params, 10000);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

			return new DefaultHttpClient(ccm, params);
		} 
		catch (Exception e) {
			Log.e("getNewHttpClient", "Exception", e);
			return new DefaultHttpClient();
		}
	}

	public static JSONObject getJSONObject(HttpResponse response) {
		if(response != null){
			try {
				// Decode GZIP
				InputStream instream = response.getEntity().getContent();
				Header contentEncoding = response.getFirstHeader("Content-Encoding");
				if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip"))
					instream = new GZIPInputStream(instream);

				String string = convertStreamToString(instream);

				JSONObject result = null;
				try {
					result = new JSONObject(string);
				}
				catch(JSONException jsonE) {
					// Le Json reçu est peut être un JsonArray plutot qu'un JsonObject
					// Du coup, on place le tout dans un JsonObject
					result = new JSONObject();
				}

				instream.close();
				return result;
			} 
			catch (Exception e) { Log.w("getJSONObject", "Exception", e); }
		}
		return null;
	}

	// Convert the Stream into readable String
	public static String convertStreamToString(InputStream is) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		while ((line = reader.readLine()) != null)
			sb.append(line + "\n");

		reader.close();

		return sb.toString();
	}

}
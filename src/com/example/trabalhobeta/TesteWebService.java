package com.example.trabalhobeta;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class TesteWebService extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.webservice);

		final Button GetServerData = (Button) findViewById(R.id.GetServerData);

		GetServerData.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				// WebServer Request URL
				// 172.16.176.246
				// zecoxao.no-ip.org HOME
				String serverURL = "http://172.16.176.246/webservice/index.php";

				// Use AsyncTask execute Method To Prevent ANR Problem
				new LongOperation().execute(serverURL);
			}
		});

	}
	
	@Override
	public void onBackPressed() {
		Intent i = new Intent(TesteWebService.this, MainActivity.class);
		startActivity(i);
		finish();
	}
	

	// Class with extends AsyncTask class

	private class LongOperation extends AsyncTask<String, Void, Void> {

		// Required initialization

		private String Content;
		private String Error = null;
		private ProgressDialog Dialog = new ProgressDialog(TesteWebService.this);
		String data = "";
		TextView jsonParsed = (TextView) findViewById(R.id.jsonParsed);

		protected void onPreExecute() {
			// NOTE: You can call UI Element here.

			// Start Progress Dialog (Message)

			Dialog.setMessage("Please wait..");
			Dialog.show();

			try {
				// Set Request parameter
				data += "&" + URLEncoder.encode("data", "UTF-8");

			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		// Call after onPreExecute method
		protected Void doInBackground(String... urls) {

			/************ Make Post Call To Web Server ***********/
			BufferedReader reader = null;

			// Send data
			try {

				// Defined URL where to send data
				URL url = new URL(urls[0]);

				// Send POST data request

				URLConnection conn = url.openConnection();
				conn.setDoOutput(true);
				OutputStreamWriter wr = new OutputStreamWriter(
						conn.getOutputStream());
				wr.write(data);
				wr.flush();

				// Get the server response

				reader = new BufferedReader(new InputStreamReader(
						conn.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String line = null;

				// Read Server Response
				while ((line = reader.readLine()) != null) {
					// Append server response in string
					sb.append(line + "");
				}

				// Append Server Response To Content String
				Content = sb.toString();
			} catch (Exception ex) {
				Error = ex.getMessage();
			} finally {
				try {

					reader.close();
				}

				catch (Exception ex) {
				}
			}

			/*****************************************************/
			return null;
		}

		protected void onPostExecute(Void unused) {
			// NOTE: You can call UI Element here.

			// Close progress dialog
			Dialog.dismiss();

			if (Error != null) {

				Log.i("Error","Output : " + Error);

			} else {

				

				/****************** Start Parse Response JSON Data *************/

				String OutputData = "";
				JSONObject jsonResponse;

				try {

					/******
					 * Creates a new JSONObject with name/value mappings from
					 * the JSON string.
					 ********/
					jsonResponse = new JSONObject(Content);

					/*****
					 * Returns the value mapped by name if it exists and is a
					 * JSONArray.
					 ***/
					/******* Returns null otherwise. *******/
					JSONArray jsonMainNode = jsonResponse
							.optJSONArray("Categorias");
					// JSONArray jsonMainNode = jsonResponse.optJSONArray("");

					/*********** Process each JSON Node ************/

					int lengthJsonArr = jsonMainNode.length();

					
					for (int i = 0; i < lengthJsonArr; i++) {
						/****** Get Object for each JSON node. ***********/
						JSONObject jsonChildNode = jsonMainNode
								.getJSONObject(i);

						/******* Fetch node values **********/
						String descricao = jsonChildNode.optString("Descricao")
								.toString();

						OutputData += "Descrição: " + descricao + "\n";

						DBAccess db = new DBAccess(getApplicationContext());
						DBAccess.getInstance(getApplicationContext());
						db.insereTipo(descricao);
						db.close();
					}
					
					/****************** End Parse Response JSON Data *************/

					// Show Parsed Output on screen (activity)
					jsonParsed.setText(OutputData);

				} catch (JSONException e) {

					e.printStackTrace();
				}

			}
		}

	}

}
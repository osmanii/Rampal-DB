package com.osmani.rampal.upazillasurvey;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.amadergram.rampal.survey.R;
import com.osmani.location.LocationTrackerService;
import com.osmani.rampal.household.HHRootMenuActivity;
import com.osmani.utils.FieldValidationUtils;
import com.osmani.utils.UserUtils;
import com.osmani.utils.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class Login extends Activity  implements OnClickListener, LocationListener{
	
	private EditText userName, password;
	private Button login;
	boolean flag;
	private LocationManager locationManager;
	private boolean gpsAvailable;


	 // Progress Dialog
    private ProgressDialog processDialog;
 
    // JSON parser class
    JSONParser jsonParser;

    private static final String LOGIN_URL = "http://www.ag-climatedata.net/ws_rhdp/login_rhdp.php";
    
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);   
        
        Intent serviceIntent = new Intent(this,LocationTrackerService.class);
        startService(serviceIntent);
        
		userName = (EditText)findViewById(R.id.username);
		password = (EditText)findViewById(R.id.password);
		password.setTypeface(Typeface.SERIF);
		
		login = (Button)findViewById(R.id.login);
		
		login.setOnClickListener(this);
        
        jsonParser = new JSONParser();    
        
		gpsAvailable = false;
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10f, this);

    }

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.login:
			if(validateData())
			{
				new AttemptLogin().execute();
			}
			else
			{ 
				Toast.makeText(getApplicationContext(), "Please enter user name and/or password", Toast.LENGTH_LONG).show();
			}
			break;

		default:
			break;
		}

	}
	
	private boolean validateData(){
		
		if (FieldValidationUtils.validateEdittextValueForText(userName) &&
				FieldValidationUtils.validateEdittextValueForText(password))		
			return true;
		return false;
	}
	
	class AttemptLogin extends AsyncTask<String, String, String> {

		 /**
        * Before starting background thread Show Progress Dialog
        * */
		boolean failure = false;
		boolean processDialogShown = false;
		
       @Override
       protected void onPreExecute() {
           super.onPreExecute();
           try
           {
	           processDialog = new ProgressDialog(Login.this);
	           processDialog.setMessage("Attempting login...");
	           processDialog.setIndeterminate(false);
	           processDialog.setCancelable(true);
	           processDialog.show();
	           processDialogShown = true;
           }
           catch(Exception ex)
           {
        	   ex.printStackTrace();
        	   Toast.makeText(getApplicationContext(), "Please check internet connection!", Toast.LENGTH_LONG).show();
           }
       }
		
		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			 // Check for success tag
           int success;
           String username = userName.getText().toString();
           String passwordText = password.getText().toString();

           if(!Utils.hasActiveInternetConnection(getApplicationContext()))
           {
        	   if(processDialogShown)
        		   processDialog.dismiss();
  				//cancel(true);
  				return null;
           }

           try 
           {
               // Building Parameters
               List<NameValuePair> params = new ArrayList<NameValuePair>();
               params.add(new BasicNameValuePair("name", username));
               //params.add(new BasicNameValuePair("password", passwordText));
               // need to send MD5 value of password now
			   params.add(new BasicNameValuePair("password", UserUtils.getMD5(passwordText)));
               //Log.d("request!", UserUtils.getMD5(passwordText));

               Log.d("request!", "starting");

               JSONObject json = jsonParser.makeHttpRequest(
                      LOGIN_URL, "POST", params);

               // check your log for json response
               Log.d("Login attempt", json.toString());

               // json success tag
               success = json.getInt(TAG_SUCCESS);
               Log.d("<x_x>", "Success: " + success);
               if (success == 1) 
               {
            	   	String userId = json.getString("userId");
            	    Log.d("<x_x>", "id: " + userId);
            	    
            	    UserUtils.setUserId(getApplicationContext(), userId);	
    				Log.d("Login Successful!", json.toString());
	               	flag = true;
	               	return json.getString(TAG_MESSAGE);
               }
               else
               {
	               	Log.d("Login Failure!", json.getString(TAG_MESSAGE));
	               	return json.getString(TAG_MESSAGE);               	
               }
           }
           catch (JSONException e) 
           {
               e.printStackTrace();
           }

           return null;
			
		}
		/**
        * After completing background task Dismiss the progress dialog
        * **/
       protected void onPostExecute(String file_url) {
           // dismiss the dialog once product deleted
           if(processDialog!=null)	
        	   if(processDialog.isShowing())
        		   processDialog.dismiss();
           if(file_url == null)
           {
				Toast.makeText(getApplicationContext(), "Please check internet connection!", Toast.LENGTH_LONG).show();
           }
           
           if (file_url != null)
           {
        	   Toast.makeText(Login.this, file_url, Toast.LENGTH_SHORT).show();
           }
           //flag = true;
           if (flag == true)
           {
	           	
	           	Intent i = new Intent(Login.this, HHRootMenuActivity.class);
	           	i.putExtra("householdId", "House Hold ID");
				startActivity(i);
				finish();
           }
           else
           {
           	
           }

       }
		
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
	}




	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		/* this is called if/when the GPS is disabled in settings */
		Log.v("<x_x>", "Disabled");

		/* bring up the GPS settings */
		Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivity(intent);				
	}




	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		Log.v("<x_x>", "Enabled");
		Toast.makeText(this, "GPS Enabled", Toast.LENGTH_SHORT).show();				
	}




	@Override
	public void onStatusChanged(String arg0, int status, Bundle arg2) {
		// TODO Auto-generated method stub
		/* This is called when the GPS status alters */
		switch (status) {
		case LocationProvider.OUT_OF_SERVICE:
			Log.v("x_x", "Status Changed: Out of Service");
//			Toast.makeText(this, "Status Changed: Out of Service",
//					Toast.LENGTH_SHORT).show();
			break;
		case LocationProvider.TEMPORARILY_UNAVAILABLE:
			Log.v("x_x", "Status Changed: Temporarily Unavailable");
//			Toast.makeText(this, "Status Changed: Temporarily Unavailable",
//					Toast.LENGTH_SHORT).show();
			break;
		case LocationProvider.AVAILABLE:
			Log.v("x_x", "Status Changed: Available");
//			Toast.makeText(this, "Status Changed: Available",
//					Toast.LENGTH_SHORT).show();
			break;
		}		
	}

}
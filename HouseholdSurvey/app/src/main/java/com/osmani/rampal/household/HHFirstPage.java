
package com.osmani.rampal.household;




import org.json.JSONObject;


import com.osmani.location.LocationTrackerService;
import com.osmani.model.HouseholdModel;

import com.osmani.rampal.upazillasurvey.JSONParser;
import com.amadergram.rampal.survey.R;
import com.osmani.utils.Constants;
import com.osmani.utils.FieldValidationUtils;
import com.osmani.utils.UserUtils;
import com.osmani.utils.Utils;
import com.osmani.utils.ViewUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.AsyncTask.Status;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class HHFirstPage extends Activity implements OnClickListener, OnItemSelectedListener, LocationListener{

	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor spEditor;
	private String latitudeString = " ...", longitudeString = " ...", electricity = " ";
	private LocationManager locationManager;
	
	private TextView latitudeTV, longitudeTV;
	
	private Button getGPSLocation;
	private boolean gpsAvailable;
	
	private String householdId;
	
	private Button nextButton;
	private Button backButton;
	private Button draftButton;
	
	private RadioButton electricityYes, electricityNo;
	
	private Spinner spinnerSaferWater;
	private Spinner spinnerReligion;
	private Switch switchLiveStock,switchBuffaloes,switchCows,switchGoats,switchSheeps,switchChickens,switchDucks;

	private Switch switchHandWashCleansingAvailable;
	private Switch switchHandWashPlace;
	private Switch switchHandWashWaterAvailable;
	
	private HouseholdHttpAsyncTask submittingTask;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hh_activity_first_page);
//		Log.d("<x_x>", Utils.getHouseHoldId(getApplicationContext()) + "----");
		setTitle("Household Page 1 ID:" + Utils.getHouseHoldId(getApplicationContext()));
		
		setDraftStatus();
		initLocationService();
		
		gpsAvailable = false;
		
		Bundle bundle = getIntent().getExtras();
		if(bundle!=null){
			householdId = bundle.getString("householdId", "sample");
			//getActionBar().setTitle(householdId);
		}
	    
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10f, this);
	    
	    latitudeTV = (TextView)findViewById(R.id.latitude);
	    longitudeTV = (TextView)findViewById(R.id.longitude);   
		
		getGPSLocation = (Button)findViewById(R.id.getGPS);
		getGPSLocation.setOnClickListener(this);
		
		electricityYes = (RadioButton)findViewById(R.id.electricityYes);
		electricityNo = (RadioButton)findViewById(R.id.electricityNo);
		
		
		nextButton = (Button)findViewById(R.id.nextButton);
	    nextButton.setOnClickListener(this);
	    
	    backButton = (Button)findViewById(R.id.backButton);
	    backButton.setOnClickListener(this);
	    
	    draftButton = (Button)findViewById(R.id.draftButton);
	    draftButton.setOnClickListener(this);
	    
	    spinnerSaferWater = (Spinner) findViewById(R.id.spinnerSaferWater);
	    spinnerReligion = (Spinner) findViewById(R.id.spinnerReligion);
	    
	    
	    switchLiveStock = (Switch) findViewById(R.id.switchLiveStock);
	    switchBuffaloes = (Switch) findViewById(R.id.switchBuffaloes);
	    switchCows = (Switch) findViewById(R.id.switchCows);
	    switchGoats = (Switch) findViewById(R.id.switchGoats);
	    switchSheeps = (Switch) findViewById(R.id.switchSheeps);
	    switchChickens = (Switch) findViewById(R.id.switchChickens);
	    switchDucks = (Switch) findViewById(R.id.switchDucks);

		switchHandWashPlace = (Switch)findViewById(R.id.switchHandWashPlace);
		switchHandWashWaterAvailable = (Switch)findViewById(R.id.switchHandWashWaterAvailable);
		switchHandWashCleansingAvailable = (Switch)findViewById(R.id.switchHandWashCleansingAvailable);

	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		householdId = Utils.getHouseHoldId(getApplicationContext());
		sharedPreferences =  getSharedPreferences("householdInformation", 0);
		if(sharedPreferences.getString("latitude", "").equals("")==false && sharedPreferences.getString("latitude", "").equalsIgnoreCase("null")==false)
		{
			latitudeTV.setText("Latitude:" + sharedPreferences.getString("latitude", ""));
			latitudeString = sharedPreferences.getString("latitude", "");
		}
		if(sharedPreferences.getString("longitude", "").equals("")==false && sharedPreferences.getString("longitude", "").equalsIgnoreCase("null")==false)		
		{
			longitudeTV.setText("Longitude:" + sharedPreferences.getString("longitude", ""));
			longitudeString = sharedPreferences.getString("longitude", "");
		}
		if(sharedPreferences.getString("electricity", "").equals("")==false && sharedPreferences.getString("electricity", "").equalsIgnoreCase("null")==false)		
		{
			if(sharedPreferences.getString("electricity", "").equalsIgnoreCase("Yes"))
			{
				electricityYes.setChecked(true);
				electricity = "Yes";
			}
			else if(sharedPreferences.getString("electricity", "").equalsIgnoreCase("No"))
			{
				electricityNo.setChecked(true);
				electricity = "No";
			}
		}
		if(sharedPreferences.getString("safeWaterMethod", "").equals("")==false && sharedPreferences.getString("safeWaterMethod", "").equalsIgnoreCase("null")==false)
		{
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
			        R.array.saferWaterSources, android.R.layout.simple_spinner_item);
			spinnerSaferWater.setSelection(adapter.getPosition(sharedPreferences.getString("safeWaterMethod", ""))); 
		}
		if(sharedPreferences.getString("religion", "").equals("")==false && sharedPreferences.getString("religion", "").equalsIgnoreCase("null")==false)
		{
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
			        R.array.religionNames, android.R.layout.simple_spinner_item);
			spinnerReligion.setSelection(adapter.getPosition(sharedPreferences.getString("religion", "")));
//			Log.d("<x_x>", "religion" + adapter.getPosition(sharedPreferences.getString("religion", "")));
		}
		if(sharedPreferences.getString("hasLivestock", "").equals("")==false && sharedPreferences.getString("hasLivestock", "").equalsIgnoreCase("null")==false)		
		{
			if(sharedPreferences.getString("hasLivestock", "").equalsIgnoreCase("Yes"))
				switchLiveStock.setChecked(true);
			else switchLiveStock.setChecked(false);			
		}
		if(sharedPreferences.getString("hasBuffaloes", "").equals("")==false && sharedPreferences.getString("hasBuffaloes", "").equalsIgnoreCase("null")==false)		
		{
			if(sharedPreferences.getString("hasBuffaloes", "").equalsIgnoreCase("Yes"))
				switchBuffaloes.setChecked(true);
			else switchBuffaloes.setChecked(false);			
		}
		if(sharedPreferences.getString("hasCows", "").equals("")==false && sharedPreferences.getString("hasCows", "").equalsIgnoreCase("null")==false)		
		{
			if(sharedPreferences.getString("hasCows", "").equalsIgnoreCase("Yes"))
				switchCows.setChecked(true);
			else switchCows.setChecked(false);			
		}
		if(sharedPreferences.getString("hasGoats", "").equals("")==false && sharedPreferences.getString("hasGoats", "").equalsIgnoreCase("null")==false)		
		{
			if(sharedPreferences.getString("hasGoats", "").equalsIgnoreCase("Yes"))
				switchGoats.setChecked(true);
			else switchGoats.setChecked(false);			
		}
		if(sharedPreferences.getString("hasSheeps", "").equals("")==false && sharedPreferences.getString("hasSheeps", "").equalsIgnoreCase("null")==false)		
		{
			if(sharedPreferences.getString("hasSheeps", "").equalsIgnoreCase("Yes"))
				switchSheeps.setChecked(true);
			else switchSheeps.setChecked(false);			
		}
		if(sharedPreferences.getString("hasChickens", "").equals("")==false && sharedPreferences.getString("hasChickens", "").equalsIgnoreCase("null")==false)		
		{
			if(sharedPreferences.getString("hasChickens", "").equalsIgnoreCase("Yes"))
				switchChickens.setChecked(true);
			else switchChickens.setChecked(false);			
		}
		if(sharedPreferences.getString("hasDucks", "").equals("")==false && sharedPreferences.getString("hasDucks", "").equalsIgnoreCase("null")==false)		
		{
			if(sharedPreferences.getString("hasDucks", "").equalsIgnoreCase("Yes"))
				switchDucks.setChecked(true);
			else switchDucks.setChecked(false);			
		}
		if (!sharedPreferences.getString("handWashPlace", "").equals("") && !sharedPreferences.getString("handWashPlace", "").equalsIgnoreCase("null"))
		{
			if (sharedPreferences.getString("handWashPlace", "").compareToIgnoreCase("Yes")==0)
			{
				switchHandWashPlace.setChecked(true);
			} else
			{
				switchHandWashPlace.setChecked(false);
			}
		}
		if (!sharedPreferences.getString("handWashWaterAvailable", "").equals("") && !sharedPreferences.getString("handWashWaterAvailable", "").equalsIgnoreCase("null"))
		{
			if (sharedPreferences.getString("handWashWaterAvailable", "").compareToIgnoreCase("Yes")==0)
			{
				switchHandWashWaterAvailable.setChecked(true);
			} else
			{
				switchHandWashWaterAvailable.setChecked(false);
			}
		}
		if (!sharedPreferences.getString("handWashCleansingAvailable", "").equals("") && !sharedPreferences.getString("handWashCleansingAvailable", "").equalsIgnoreCase("null"))
		{
			if (sharedPreferences.getString("handWashCleansingAvailable", "").compareToIgnoreCase("Yes")==0)
			{
				switchHandWashCleansingAvailable.setChecked(true);
			}else{
				switchHandWashCleansingAvailable.setChecked(false);
			}
		}
	}
	
	private void setDraftStatus(){
		
		sharedPreferences =  getSharedPreferences("householdInformation", 0);
		spEditor = sharedPreferences.edit();
		spEditor.putString("householdDraftStatus", "draft");
		spEditor.putString("householdDraftWhere",  Constants.HHFirstPage_DRAFT_WHERE);
		spEditor.commit();
	}
	private void initLocationService(){
		Intent serviceIntent = new Intent(this,LocationTrackerService.class);
        startService(serviceIntent);
	}
	
	@Override
	public void onClick(View view) {
	
		switch (view.getId()) {
		case R.id.nextButton:
			
			if(validateData()){
				updateSharedPreferrence();
				Intent i = new Intent(HHFirstPage.this, HHSecondPage.class);
				startActivity(i);
				finish();
			}

			break;
		case R.id.backButton:
			super.onBackPressed();

			break;
		case R.id.draftButton:
			//updateSharedPreferrence();
			if(submittingTask==null){
				submittingTask= new HouseholdHttpAsyncTask();
				submittingTask.execute();
			}else{
				if(submittingTask.getStatus()==Status.RUNNING ||submittingTask.getStatus()== Status.PENDING ){
					Toast.makeText(getApplicationContext(), "Already submitting data", Toast.LENGTH_LONG).show();
				}else{
					submittingTask= new HouseholdHttpAsyncTask();
					submittingTask.execute();
				}
			}
			
			break;
		case R.id.getGPS:
			gpsAvailable = true;
			latitudeString = UserUtils.getUserLatitude(getApplicationContext());
			longitudeString = UserUtils.getUserLongitude(getApplicationContext());
			latitudeTV.setText("Latitude:" + latitudeString);
			longitudeTV.setText("Longitude:" + longitudeString);	
			
			// probablly you are not going to get any location data from GPS provider inside of a room.
			//So we just used our location service's best location implementation
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10f, this);
			//latitudeTV.setText("Latitude:" + latitudeString);
			//longitudeTV.setText("Longitude:" + longitudeString);
			break;
		}	
	}
	private void updateSharedPreferrence(){
		
		sharedPreferences =  getSharedPreferences("householdInformation", 0);
		spEditor = sharedPreferences.edit();
	    
		spEditor.putString("householdId",  householdId);
		spEditor.putString("latitude",  latitudeString);
		spEditor.putString("longitude",  longitudeString);
		spEditor.putString("electricity",  electricity);
		spEditor.putString("safeWaterMethod", ViewUtils.getSpinnerSelectedValue(spinnerSaferWater));
		spEditor.putString("religion", ViewUtils.getSpinnerSelectedValue(spinnerReligion));
		spEditor.putString("hasLivestock",  ViewUtils.getSwitchValue(switchLiveStock));
		
		
		spEditor.putString("hasBuffaloes",  ViewUtils.getSwitchValue(switchBuffaloes));
		spEditor.putString("hasCows",  ViewUtils.getSwitchValue(switchCows));
		spEditor.putString("hasGoats",  ViewUtils.getSwitchValue(switchGoats));
		spEditor.putString("hasSheeps",  ViewUtils.getSwitchValue(switchSheeps));
		spEditor.putString("hasChickens",  ViewUtils.getSwitchValue(switchChickens));
		spEditor.putString("hasDucks",  ViewUtils.getSwitchValue(switchDucks));

		spEditor.putString("handWashPlace", ViewUtils.getSwitchValue(switchHandWashPlace));
		spEditor.putString("handWashWaterAvailable", ViewUtils.getSwitchValue(switchHandWashWaterAvailable));
		spEditor.putString("handWashCleansingAvailable", ViewUtils.getSwitchValue(switchHandWashCleansingAvailable));
		
		spEditor.commit();	

	}

	
	private boolean validateData(){
		
		if(!gpsAvailable)
		{
			Toast.makeText(getApplicationContext(), "GPS coordinates are unavailable!", Toast.LENGTH_LONG).show();
			return false;
		}
		if(electricity.equalsIgnoreCase(" "))
		{
			Toast.makeText(getApplicationContext(), "Please answer question 2!", Toast.LENGTH_LONG).show();
			return false;			
		}
/*		if( !FieldValidationUtils.validateSpinnerValue(spinnerSaferWater) )
		{
			Toast.makeText(getApplicationContext(), "Please answer question 3!", Toast.LENGTH_LONG).show();
			return false;						
		}*/
		if( !FieldValidationUtils.validateSpinnerValue(spinnerReligion) )
		{
			Toast.makeText(getApplicationContext(), "Please answer question 4!", Toast.LENGTH_LONG).show();
			return false;						
		}
		
		return true;		
		
	}
	private void showResultDialog(boolean result){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);


    	if(result){
    		builder.setMessage("Drafted Household Id :"+Utils.getHouseHoldId(getApplicationContext()))
			.setTitle("Success!");
    	}else{
    		builder.setMessage("Problem occurred, Please draft again.")
			.setTitle("Failed!");
    	}
    	builder.setNeutralButton("OK.",new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
				
			}
		});
    	AlertDialog dialog = builder.create();
    	dialog.setCanceledOnTouchOutside(false);
    	dialog.show();
	}
	private class HouseholdHttpAsyncTask extends AsyncTask<Void, Void, Boolean> {
		
		private HouseholdModel model ;
		private String SERVER_POSTING_URL="http://www.ag-climatedata.net/ws_rhdp/update_household.php";
		// Progress Dialog
		private ProgressDialog progressDialog;
		
		      
		public HouseholdHttpAsyncTask() {
			super();
			this.model = Utils.getHouseholdModelObject(getApplicationContext());
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(HHFirstPage.this);
			progressDialog.setMessage("Uploading Data to Server...");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}
		@Override
		protected Boolean doInBackground(Void... paramss) {
			// TODO Auto-generated method stub
		
			try {
				JSONParser jsonParser = new JSONParser();
				//Posting user data to script 
				JSONObject json2 = jsonParser.makeHttpRequest(SERVER_POSTING_URL, "POST", Utils.getNameValuePairFromModel(getApplicationContext(),this.model));

				// full JSON response
				Log.d("Upload attempt!", json2.toString());
				return json2.getBoolean("success");           
	 
	        } catch (Exception e) {
	            Log.d("InputStream", e.getLocalizedMessage());
	            return false;
	        }
		}
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Boolean result) {
        	
        	if(progressDialog!=null && progressDialog.isShowing()){
        		progressDialog.dismiss();
        	}
        	showResultDialog(result);
       }
	}



	

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onRadioButtonClicked(View view) {
		// TODO Auto-generated method stub
	    boolean checked = ((RadioButton) view).isChecked();
	    

	    switch(view.getId()) {
	        case R.id.electricityYes:
	            if (checked)
	            	electricity = "Yes";
	            Log.d("<x_x>", "electricity: " + electricity);
	            break;
	        case R.id.electricityNo:
	            if (checked)
	            	electricity = "No";
	            Log.d("<x_x>", "electricity: " + electricity);
	            break;
	    }
		
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		if(!Double.toString(location.getLatitude()).equals("") && !Double.toString(location.getLongitude()).equals(""))
		{
			latitudeString = Double.toString(location.getLatitude());
			longitudeString = Double.toString(location.getLongitude());
			gpsAvailable = true;
			
			latitudeTV.setText("Latitude:" + latitudeString);
			longitudeTV.setText("Longitude:" + longitudeString);						
		}
		Log.d("<x_x>", "Latitude: " + latitudeString);
		Log.d("<x_x>", "Longitude: " + longitudeString);
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		/* this is called if/when the GPS is disabled in settings */
		Log.v("<x_x>", "Disabled");

		/* bring up the GPS settings */
		Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivity(intent);		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		Log.v("<x_x>", "Enabled");
//		Toast.makeText(this, "GPS Enabled", Toast.LENGTH_SHORT).show();		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
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

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		
	}
}

package com.osmani.rampal.household;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.osmani.location.LocationTrackerService;
import com.osmani.model.HouseholdModel;
import com.osmani.rampal.personal.PPFirstPage;
import com.osmani.rampal.upazillasurvey.JSONParser;
import com.osmani.rampal.upazillasurvey.PPRootMenuActivity;
import com.amadergram.rampal.survey.R;
import com.osmani.utils.Constants;
import com.osmani.utils.FieldValidationUtils;
import com.osmani.utils.Utils;
import com.osmani.utils.ViewUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View.OnClickListener;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.AsyncTask.Status;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

public class HHFifthPage extends Activity implements OnClickListener {

	SharedPreferences sharedPreferences;
	SharedPreferences.Editor spEditor;

	Switch television, radio, tabla, flute, harmonium;
	Switch switchBicycle,switchMotorcycle,switchIndependentPowerSource;
	boolean dataUploaded = false;
	// JSON parser class
	JSONParser jsonParser = new JSONParser();
	
	private boolean submitted = false;
	
	
	private Button nextButton;
	private Button backButton;
	private Button draftButton;
	
	private Spinner spinnerLandOwnership,spinnerSourceKitchenFuel,spinnerImportantConcern;

	private EditText maleChildET,femaleChildET;
	
	private HouseholdHttpAsyncTask submittingTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hh_activity_fifth_page);
		
		setTitle("Household Page 5 ID:" + Utils.getHouseHoldId(getApplicationContext()));

		setDraftStatus();
		initLocationService();

		
		television = (Switch)findViewById(R.id.tvSwitch);
		radio = (Switch)findViewById(R.id.radioSwitch);
		harmonium = (Switch)findViewById(R.id.harmoniumSwitch);
		tabla = (Switch)findViewById(R.id.tablaSwitch);
		flute = (Switch)findViewById(R.id.fluteSwitch);


		switchBicycle = (Switch)findViewById(R.id.switchBicycle);
		switchMotorcycle = (Switch)findViewById(R.id.switchMotorcycle);
		switchIndependentPowerSource = (Switch)findViewById(R.id.switchIndependentPowerSource);

		spinnerImportantConcern = (Spinner) findViewById(R.id.spinnerImportantConcern);
		spinnerLandOwnership = (Spinner) findViewById(R.id.spinnerLandOwnership);
		spinnerSourceKitchenFuel = (Spinner) findViewById(R.id.spinnerSourceKitchenFuel);


		maleChildET = (EditText) findViewById(R.id.maleChildET);
		femaleChildET = (EditText) findViewById(R.id.femaleChildET);

		nextButton = (Button)findViewById(R.id.nextButton);
	    nextButton.setOnClickListener(this);
	    
	    backButton = (Button)findViewById(R.id.backButton);
	    backButton.setOnClickListener(this);
	    
	    draftButton = (Button)findViewById(R.id.draftButton);
	    draftButton.setOnClickListener(this);
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		submitted = false;
		
		sharedPreferences =  getSharedPreferences("householdInformation", 0);
		if(sharedPreferences.getString("landOwnerShip", "").equals("")==false && sharedPreferences.getString("landOwnerShip", "").equalsIgnoreCase("null")==false)
		{
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
			        R.array.landOwnershipRange, android.R.layout.simple_spinner_item);
			spinnerLandOwnership.setSelection(adapter.getPosition(sharedPreferences.getString("landOwnerShip", ""))); 
		}
		if(sharedPreferences.getString("television", "").equals("")==false && sharedPreferences.getString("television", "").equalsIgnoreCase("null")==false)		
		{
			if(sharedPreferences.getString("television", "").equalsIgnoreCase("Yes"))
				television.setChecked(true);
			else television.setChecked(false);			
		}
		if(sharedPreferences.getString("radio", "").equals("")==false && sharedPreferences.getString("radio", "").equalsIgnoreCase("null")==false)		
		{
			if(sharedPreferences.getString("radio", "").equalsIgnoreCase("Yes"))
				radio.setChecked(true);
			else radio.setChecked(false);			
		}
		if(sharedPreferences.getString("harmonium", "").equals("")==false && sharedPreferences.getString("harmonium", "").equalsIgnoreCase("null")==false)		
		{
			if(sharedPreferences.getString("harmonium", "").equalsIgnoreCase("Yes"))
				harmonium.setChecked(true);
			else harmonium.setChecked(false);			
		}
		if(sharedPreferences.getString("tabla", "").equals("")==false && sharedPreferences.getString("tabla", "").equalsIgnoreCase("null")==false)		
		{
			if(sharedPreferences.getString("tabla", "").equalsIgnoreCase("Yes"))
				tabla.setChecked(true);
			else tabla.setChecked(false);			
		}
		if(sharedPreferences.getString("flute", "").equals("")==false && sharedPreferences.getString("flute", "").equalsIgnoreCase("null")==false)		
		{
			if(sharedPreferences.getString("flute", "").equalsIgnoreCase("Yes"))
				flute.setChecked(true);
			else flute.setChecked(false);			
		}
		if(sharedPreferences.getString("kitchenFuel", "").equals("")==false && sharedPreferences.getString("kitchenFuel", "").equalsIgnoreCase("null")==false)
		{
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
			        R.array.sourcesKitchenFuel, android.R.layout.simple_spinner_item);
			spinnerSourceKitchenFuel.setSelection(adapter.getPosition(sharedPreferences.getString("kitchenFuel", ""))); 
		}
		if(sharedPreferences.getString("importantConcern", "").equals("")==false && sharedPreferences.getString("importantConcern", "").equalsIgnoreCase("null")==false)
		{
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
			        R.array.importantConcerns, android.R.layout.simple_spinner_item);
			spinnerImportantConcern.setSelection(adapter.getPosition(sharedPreferences.getString("importantConcern", ""))); 
		}
		if(sharedPreferences.getString("hasCycle", "").equals("")==false && sharedPreferences.getString("hasCycle", "").equalsIgnoreCase("null")==false)
		{
			if(sharedPreferences.getString("hasCycle", "").equalsIgnoreCase("Yes"))
				switchBicycle.setChecked(true);
			else
				switchBicycle.setChecked(false);
		}
		if(sharedPreferences.getString("hasMotorCycle", "").equals("")==false && sharedPreferences.getString("hasMotorCycle", "").equalsIgnoreCase("null")==false)
		{
			if(sharedPreferences.getString("hasMotorCycle", "").equalsIgnoreCase("Yes"))
				switchMotorcycle.setChecked(true);
			else
				switchMotorcycle.setChecked(false);
		}
		if(sharedPreferences.getString("hasIndependentPowerSource", "").equals("")==false && sharedPreferences.getString("hasIndependentPowerSource", "").equalsIgnoreCase("null")==false)
		{
			if(sharedPreferences.getString("hasIndependentPowerSource", "").equalsIgnoreCase("Yes"))
				switchIndependentPowerSource.setChecked(true);
			else
				switchIndependentPowerSource.setChecked(false);
		}
		if(sharedPreferences.getString("maleChild", "").equals("")==false && sharedPreferences.getString("maleChild", "").equalsIgnoreCase("null")==false)
		{
			maleChildET.setText(sharedPreferences.getString("maleChild", ""));
		}
		if(sharedPreferences.getString("femaleChild", "").equals("")==false && sharedPreferences.getString("femaleChild", "").equalsIgnoreCase("null")==false)
		{
			maleChildET.setText(sharedPreferences.getString("femaleChild", ""));
		}


	}


	
	private void setDraftStatus(){
		
		sharedPreferences =  getSharedPreferences("householdInformation", 0);
		spEditor = sharedPreferences.edit();
		spEditor.putString("householdDraftStatus", "draft");
		spEditor.putString("householdDraftWhere",  Constants.HHFifthPage_DRAFT_WHERE);
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
				if(submittingTask==null){
					submitted = true;
					submittingTask= new HouseholdHttpAsyncTask();
					submittingTask.execute();
				}else{
					if(submittingTask.getStatus()==Status.RUNNING ||submittingTask.getStatus()== Status.PENDING ){
						Toast.makeText(getApplicationContext(), "Already submitting data", Toast.LENGTH_LONG).show();
					}else{
						submitted = true;
						submittingTask= new HouseholdHttpAsyncTask();
						submittingTask.execute();
					}
				}
			}
			

			break;
		case R.id.backButton:
			Intent i = new Intent(HHFifthPage.this, HHFourthPage.class);
			startActivity(i);
			finish();
			break;
		case R.id.draftButton:			
			if(submittingTask==null){
				submitted = false;
				submittingTask= new HouseholdHttpAsyncTask();
				submittingTask.execute();
			}else{
				if(submittingTask.getStatus()==Status.RUNNING ||submittingTask.getStatus()== Status.PENDING ){
					Toast.makeText(getApplicationContext(), "Already submitting data", Toast.LENGTH_LONG).show();
				}else{
					submitted = false;
					submittingTask= new HouseholdHttpAsyncTask();
					submittingTask.execute();
				}
			}
			
			break;

		}	
	}
	private void updateSharedPreferrence(){
		sharedPreferences =  getSharedPreferences("householdInformation", 0);
		spEditor = sharedPreferences.edit();

		spEditor.putString("landOwnerShip",  ViewUtils.getSpinnerSelectedValue(spinnerLandOwnership));
		spEditor.putString("television",  ViewUtils.getSwitchValue(television));
		spEditor.putString("radio",  ViewUtils.getSwitchValue(radio));
		spEditor.putString("harmonium",  ViewUtils.getSwitchValue(harmonium));
		spEditor.putString("tabla",  ViewUtils.getSwitchValue(tabla));
		spEditor.putString("flute",  ViewUtils.getSwitchValue(flute));
		spEditor.putString("kitchenFuel",  ViewUtils.getSpinnerSelectedValue(spinnerSourceKitchenFuel));
		spEditor.putString("importantConcern",  ViewUtils.getSpinnerSelectedValue(spinnerImportantConcern));

		spEditor.putString("hasCycle",  ViewUtils.getSwitchValue(switchBicycle));
		spEditor.putString("hasMotorCycle",  ViewUtils.getSwitchValue(switchMotorcycle));
		spEditor.putString("hasIndependentPowerSource",  ViewUtils.getSwitchValue(switchIndependentPowerSource));

		spEditor.putString("maleChild",  ViewUtils.getEditTextInput(maleChildET));
		spEditor.putString("femaleChild",  ViewUtils.getEditTextInput(femaleChildET));


		spEditor.putString("householdDraftStatus", "completed");
		spEditor.putString("householdDraftWhere",  Constants.PPRootMenu_DRAFT_WHERE);

		spEditor.commit();		
	}
	
	private boolean validateData(){
		
		if(!FieldValidationUtils.validateSpinnerValue(spinnerLandOwnership))
		{
			Toast.makeText(this, "Please select land ownership!", Toast.LENGTH_SHORT).show();
			return false;								
		}
		else if(!FieldValidationUtils.validateSpinnerValue(spinnerSourceKitchenFuel))
		{
			Toast.makeText(this, "Please select source of kitchen fuel!", Toast.LENGTH_SHORT).show();
			return false;								
		}
		else if(!FieldValidationUtils.validateSpinnerValue(spinnerImportantConcern))
		{
			Toast.makeText(this, "Please select most important concern of the household!", Toast.LENGTH_SHORT).show();
			return false;								
		}
		else if(!FieldValidationUtils.validateEdittextValueForNumber(maleChildET)){
			Toast.makeText(this, "Male child count must be a numeric value!", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if(!FieldValidationUtils.validateEdittextValueForNumber(femaleChildET)){
			Toast.makeText(this, "Female child count must be a numeric value!", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}
	
	private void showResultDialog(boolean result){
		
    	if(result){
    		Intent i = new Intent(HHFifthPage.this, PPRootMenuActivity.class);				
			startActivity(i);
			finish();
			
			
    	}else{
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		builder.setMessage("Problem occurred, Please draft again.")
			.setTitle("Failed!");
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
    	
	}
	
	private void showDraftResultDialog(boolean result){
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
			progressDialog = new ProgressDialog(HHFifthPage.this);
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
        	
        	if(submitted)
        		showResultDialog(result);
        	else showDraftResultDialog(result);
       }
	}
	

}

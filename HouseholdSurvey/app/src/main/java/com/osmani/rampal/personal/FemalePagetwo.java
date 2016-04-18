package com.osmani.rampal.personal;

import org.json.JSONObject;

import com.osmani.location.LocationTrackerService;
import com.osmani.model.FemalePersonalModel;
import com.osmani.rampal.upazillasurvey.JSONParser;
import com.amadergram.rampal.survey.R;
import com.osmani.utils.Constants;
import com.osmani.utils.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.AsyncTask.Status;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

public class FemalePagetwo extends Activity implements OnClickListener {
	
	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor spEditor;
	private Button nextButton;
	private Button backButton;
	private Button draftButton;
	
	// switches
	Switch switchSmoking,switchBetelNut,switchCancerfamilyHistory,switchMalaria,switchMalariaTreatment,switchProductiveCough,switchProductiveCoughTreatment;
	Switch switchHusbandRelationship;
	
	private FemaleHttpAsyncTask submittingTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pp_activity_female_page_two);
		
		setTitle("Female Page 2 ID:" + Utils.getPersonId(getApplicationContext()));
				
		setDraftStatus();
		initLocationService();
		
		switchSmoking = (Switch) findViewById(R.id.switchSmoking);
		switchBetelNut = (Switch) findViewById(R.id.switchBetelNut);
		switchCancerfamilyHistory = (Switch) findViewById(R.id.switchCancerfamilyHistory);
		switchMalaria = (Switch) findViewById(R.id.switchMalaria);
		switchMalariaTreatment = (Switch) findViewById(R.id.switchMalariaTreatment);
		switchProductiveCough = (Switch) findViewById(R.id.switchProductiveCough);
		switchProductiveCoughTreatment = (Switch) findViewById(R.id.switchProductiveCoughTreatment);
		switchHusbandRelationship = (Switch)findViewById(R.id.switchHusbandRelationship);
		
		nextButton = (Button)findViewById(R.id.nextButton);
	    nextButton.setOnClickListener(this);
	    
	    backButton = (Button)findViewById(R.id.backButton);
	    backButton.setOnClickListener(this);
	    
	    draftButton = (Button)findViewById(R.id.draftButton);
	    draftButton.setOnClickListener(this);
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

	
	@Override
	public void onResume()
	{
		super.onResume();
		
		sharedPreferences =  getSharedPreferences("personalInformation", 0);
		if(sharedPreferences.getString("smoking", "").equals("")==false && sharedPreferences.getString("smoking", "").equalsIgnoreCase("null")==false)		
		{
			if(sharedPreferences.getString("smoking", "").equalsIgnoreCase("Yes"))
				switchSmoking.setChecked(true);
			else switchSmoking.setChecked(false);			
		}
		if(sharedPreferences.getString("betelNutChewing", "").equals("")==false && sharedPreferences.getString("betelNutChewing", "").equalsIgnoreCase("null")==false)		
		{
			if(sharedPreferences.getString("betelNutChewing", "").equalsIgnoreCase("Yes"))
				switchBetelNut.setChecked(true);
			else switchBetelNut.setChecked(false);			
		}
		if(sharedPreferences.getString("familyHistoryCancer", "").equals("")==false && sharedPreferences.getString("familyHistoryCancer", "").equalsIgnoreCase("null")==false)		
		{
			if(sharedPreferences.getString("familyHistoryCancer", "").equalsIgnoreCase("Yes"))
				switchCancerfamilyHistory.setChecked(true);
			else switchCancerfamilyHistory.setChecked(false);			
		}
		if(sharedPreferences.getString("isLastYearClinicalMalaria", "").equals("")==false && sharedPreferences.getString("isLastYearClinicalMalaria", "").equalsIgnoreCase("null")==false)		
		{
			if(sharedPreferences.getString("isLastYearClinicalMalaria", "").equalsIgnoreCase("Yes"))
				switchMalaria.setChecked(true);
			else switchMalaria.setChecked(false);			
		}
		if(sharedPreferences.getString("treatedLastYearClinicalMalaria", "").equals("")==false && sharedPreferences.getString("treatedLastYearClinicalMalaria", "").equalsIgnoreCase("null")==false)		
		{
			if(sharedPreferences.getString("treatedLastYearClinicalMalaria", "").equalsIgnoreCase("Yes"))
				switchMalariaTreatment.setChecked(true);
			else switchMalariaTreatment.setChecked(false);			
		}
		if(sharedPreferences.getString("isProductiveCough", "").equals("")==false && sharedPreferences.getString("isProductiveCough", "").equalsIgnoreCase("null")==false)		
		{
			if(sharedPreferences.getString("isProductiveCough", "").equalsIgnoreCase("Yes"))
				switchProductiveCough.setChecked(true);
			else switchProductiveCough.setChecked(false);			
		}
		if(sharedPreferences.getString("treatedProductiveCough", "").equals("")==false && sharedPreferences.getString("treatedProductiveCough", "").equalsIgnoreCase("null")==false)		
		{
			if(sharedPreferences.getString("treatedProductiveCough", "").equalsIgnoreCase("Yes"))
				switchProductiveCoughTreatment.setChecked(true);
			else switchProductiveCoughTreatment.setChecked(false);			
		}
		if(sharedPreferences.getString("husbandRelationship", "").equals("")==false && sharedPreferences.getString("husbandRelationship", "").equalsIgnoreCase("null")==false)
		{
			if(sharedPreferences.getString("husbandRelationship", "").equalsIgnoreCase("Yes"))
				switchHusbandRelationship.setChecked(true);
			else switchHusbandRelationship.setChecked(false);
		}
	}

	
	private void setDraftStatus(){
		sharedPreferences =  getSharedPreferences("personalInformation", 0);
		spEditor = sharedPreferences.edit();
		spEditor.putString("personDraftStatus", "draft");
		spEditor.putString("personDraftWhere",  Constants.FemalePageTwo_DRAFT_WHERE);
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
				
			
				Intent i = new Intent(FemalePagetwo.this, FemalePageThree.class);
				startActivity(i);
				finish();
			}

			break;
		case R.id.backButton:
			Intent i = new Intent(FemalePagetwo.this, FemalePageOne.class);
			startActivity(i);
			finish();

			break;
		case R.id.draftButton:
			if(submittingTask==null){
				submittingTask= new FemaleHttpAsyncTask();
				submittingTask.execute();
			}else{
				if(submittingTask.getStatus()==Status.RUNNING ||submittingTask.getStatus()== Status.PENDING ){
					Toast.makeText(getApplicationContext(), "Already submitting data", Toast.LENGTH_LONG).show();
				}else{
					submittingTask= new FemaleHttpAsyncTask();
					submittingTask.execute();
				}
			}
			break;
		}	
	}
	
	private void updateSharedPreferrence(){
		sharedPreferences =  getSharedPreferences("personalInformation", 0);
		spEditor = sharedPreferences.edit();
	    
		spEditor.putString("smoking",getSwitchValue(switchSmoking) );
		spEditor.putString("betelNutChewing",  getSwitchValue(switchBetelNut));
		spEditor.putString("familyHistoryCancer", getSwitchValue(switchCancerfamilyHistory));
		spEditor.putString("isLastYearClinicalMalaria",  getSwitchValue(switchMalaria));
		spEditor.putString("treatedLastYearClinicalMalaria", getSwitchValue(switchMalariaTreatment) );
		spEditor.putString("isProductiveCough",  getSwitchValue(switchProductiveCough));
		spEditor.putString("treatedProductiveCough",  getSwitchValue(switchProductiveCoughTreatment));
		// v4 diff
		spEditor.putString("husbandRelationship", getSwitchValue(switchHusbandRelationship));
		
		spEditor.commit();
	}
	private String getSwitchValue(Switch switchView){
		return (String)(switchView.isChecked()?switchView.getTextOn():switchView.getTextOff());
	}
	
	private boolean validateData(){
		
		return true;
		
		/*if(familyHead.getText().toString().trim().equals(""))
		{
			Toast.makeText(this, "Please enter name of head of the family!", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if(name.getText().toString().equals(""))
		{
			Toast.makeText(this, "Please enter name!", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if(mothersName.getText().toString().equals(""))
		{
			Toast.makeText(this, "Please enter mother's name!", Toast.LENGTH_SHORT).show();
			return false;
		}			
		else if(fathersName.getText().toString().equals(""))
		{
			Toast.makeText(this, "Please enter father's name!", Toast.LENGTH_SHORT).show();
			return false;
		}		
		else if(dob.getText().toString().equals(""))
		{
			Toast.makeText(this, "Please enter date of birth!", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if(age.getText().toString().equals(""))
		{
			Toast.makeText(this, "Please enter age!", Toast.LENGTH_SHORT).show();
			return false;
		}			
		else if(voterBirth.getText().toString().equals(""))
		{
			Toast.makeText(this, "Please enter voter id or birth registration no!", Toast.LENGTH_SHORT).show();
			return false;
		}		
		else
		{
			return true;			
		}	*/	
		
	}
private class FemaleHttpAsyncTask extends AsyncTask<Void, Void, Boolean> {
		
		private FemalePersonalModel model ;
		private String SERVER_POSTING_URL="http://www.ag-climatedata.net/ws_rhdp/update_female.php";
		// Progress Dialog
		private ProgressDialog progressDialog;
		
		      
		public FemaleHttpAsyncTask() {
			super();
			this.model = Utils.getFemalePersonalModelObject(getApplicationContext());
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(FemalePagetwo.this);
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
	


}

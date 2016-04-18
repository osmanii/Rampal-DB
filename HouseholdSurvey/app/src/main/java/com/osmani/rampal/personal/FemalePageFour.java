package com.osmani.rampal.personal;

import org.json.JSONObject;

import com.osmani.location.LocationTrackerService;
import com.osmani.model.FemalePersonalModel;
import com.osmani.rampal.household.HHRootMenuActivity;
import com.osmani.rampal.upazillasurvey.JSONParser;
import com.osmani.rampal.upazillasurvey.PPRootMenuActivity;
import com.amadergram.rampal.survey.R;
import com.osmani.utils.Constants;
import com.osmani.utils.FieldValidationUtils;
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
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

public class FemalePageFour extends Activity implements OnClickListener{
	
	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor spEditor;
	private Button nextButton;
	private Button backButton;
	private Button draftButton;
	
	Switch switchAgeMenopause,switchAgeFirstPregnency,switchCurrentOralContraceptives,switchCurrentInjectableContraceptives,switchEverOralContraceptives;
	
	EditText edittextAgeMenarcheYear,edittextAgeMenarcheMonth,edittextAgeMenopauseYears,edittextAgeFirstPregnencyYear,edittextAgeFirstPregnencyMonth,editextFullTermPregnency,editextFullBreastFeedingMonths;
	
	private FemaleHttpAsyncTask submittingTask;
	
	private boolean submitted = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pp_activity_female_page_three);
		
		setTitle("Female Page 4 ID:" + Utils.getPersonId(getApplicationContext()));
				
		setDraftStatus();
		initLocationService();
		
		switchAgeMenopause = (Switch)findViewById(R.id.switchAgeMenopause);
		switchAgeFirstPregnency = (Switch)findViewById(R.id.switchAgeFirstPregnency);
		switchCurrentOralContraceptives = (Switch)findViewById(R.id.switchCurrentOralContraceptives);
        switchCurrentInjectableContraceptives = (Switch)findViewById(R.id.switchCurrentInjectableContraceptives);
		switchEverOralContraceptives = (Switch)findViewById(R.id.switchEverOralContraceptives);
		
		edittextAgeMenarcheYear = (EditText) findViewById(R.id.edittextAgeMenarcheYear);
		edittextAgeMenarcheMonth = (EditText) findViewById(R.id.edittextAgeMenarcheMonth);
		edittextAgeMenopauseYears = (EditText) findViewById(R.id.edittextAgeMenopauseYears);
		edittextAgeFirstPregnencyYear = (EditText) findViewById(R.id.edittextAgeFirstPregnencyYear);
		edittextAgeFirstPregnencyMonth = (EditText) findViewById(R.id.edittextAgeFirstPregnencyMonth);
		editextFullTermPregnency = (EditText) findViewById(R.id.editextFullTermPregnency);
		editextFullBreastFeedingMonths = (EditText) findViewById(R.id.editextFullBreastFeedingMonths);
		
		
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
		if(sharedPreferences.getString("ageMenarcheYears", "").equals("")==false && sharedPreferences.getString("ageMenarcheYears", "").equalsIgnoreCase("null")==false)
		{
			edittextAgeMenarcheYear.setText(sharedPreferences.getString("ageMenarcheYears", ""));
		}
		if(sharedPreferences.getString("ageMennopauseYears", "").equals("")==false && sharedPreferences.getString("ageMennopauseYears", "").equalsIgnoreCase("null")==false)		
		{
			edittextAgeMenopauseYears.setText(sharedPreferences.getString("ageMennopauseYears", ""));
		}
        if(sharedPreferences.getString("isFirstPregnencyApplicable", "").equals("")==false && sharedPreferences.getString("isFirstPregnencyApplicable", "").equalsIgnoreCase("null")==false)
        {
            if(sharedPreferences.getString("isFirstPregnencyApplicable", "").equalsIgnoreCase("Yes"))
                switchAgeFirstPregnency.setChecked(true);
            else switchAgeFirstPregnency.setChecked(false);
        }
		if(sharedPreferences.getString("ageFirstPregnencyYears", "").equals("")==false && sharedPreferences.getString("ageFirstPregnencyYears", "").equalsIgnoreCase("null")==false)		
		{
			edittextAgeFirstPregnencyYear.setText(sharedPreferences.getString("ageFirstPregnencyYears", ""));
		}
		if(sharedPreferences.getString("ageFirstPregnencyMonths", "").equals("")==false && sharedPreferences.getString("ageFirstPregnencyMonths", "").equalsIgnoreCase("null")==false)
		{
			edittextAgeFirstPregnencyMonth.setText(sharedPreferences.getString("ageFirstPregnencyMonths", ""));
		}
		if(sharedPreferences.getString("noFullTermPregnency", "").equals("")==false && sharedPreferences.getString("noFullTermPregnency", "").equalsIgnoreCase("null")==false)		
		{
			editextFullTermPregnency.setText(sharedPreferences.getString("noFullTermPregnency", ""));
		}
		if(sharedPreferences.getString("noMonthsBreastFeeding", "").equals("")==false && sharedPreferences.getString("noMonthsBreastFeeding", "").equalsIgnoreCase("null")==false)		
		{
			editextFullBreastFeedingMonths.setText(sharedPreferences.getString("noMonthsBreastFeeding", ""));
		}
		if(sharedPreferences.getString("isCurrentOralContraceptives", "").equals("")==false && sharedPreferences.getString("isCurrentOralContraceptives", "").equalsIgnoreCase("null")==false)		
		{
			if(sharedPreferences.getString("isCurrentOralContraceptives", "").equalsIgnoreCase("Yes"))
				switchCurrentOralContraceptives.setChecked(true);
			else switchCurrentOralContraceptives.setChecked(false);			
		}
        if(sharedPreferences.getString("isCurrentInjectableContraceptives", "").equals("")==false && sharedPreferences.getString("isCurrentInjectableContraceptives", "").equalsIgnoreCase("null")==false)
        {
            if(sharedPreferences.getString("isCurrentInjectableContraceptives", "").equalsIgnoreCase("Yes"))
                switchCurrentInjectableContraceptives.setChecked(true);
            else switchCurrentInjectableContraceptives.setChecked(false);
        }
		if(sharedPreferences.getString("isEverOralContraceptives", "").equals("")==false && sharedPreferences.getString("isEverOralContraceptives", "").equalsIgnoreCase("null")==false)		
		{
			if(sharedPreferences.getString("isEverOralContraceptives", "").equalsIgnoreCase("Yes"))
				switchEverOralContraceptives.setChecked(true);
			else switchEverOralContraceptives.setChecked(false);
		}
	}

	
	@Override
	public void onClick(View view) {
	
		switch (view.getId()) {
		case R.id.nextButton:

			//initAddMoreDialog();				
			
			if(validateData()){
				updateSharedPreferrence();				
				if(submittingTask==null){					
					submitted = true;
					submittingTask= new FemaleHttpAsyncTask();
					submittingTask.execute();
				}else{
					if(submittingTask.getStatus()==Status.RUNNING ||submittingTask.getStatus()== Status.PENDING ){
						Toast.makeText(getApplicationContext(), "Already submitting data", Toast.LENGTH_LONG).show();
					}else{
						submitted = true;
						submittingTask= new FemaleHttpAsyncTask();
						submittingTask.execute();
					}
				}
			}

			break;
		case R.id.backButton:
			Intent i = new Intent(FemalePageFour.this, FemalePageThree.class);
			startActivity(i);
			finish();

			break;
		case R.id.draftButton:
			if(submittingTask==null){
				submitted = false;
				submittingTask= new FemaleHttpAsyncTask();
				submittingTask.execute();
			}else{
				if(submittingTask.getStatus()==Status.RUNNING ||submittingTask.getStatus()== Status.PENDING ){
					Toast.makeText(getApplicationContext(), "Already submitting data", Toast.LENGTH_LONG).show();
				}else{
					submitted = false;
					submittingTask= new FemaleHttpAsyncTask();
					submittingTask.execute();
				}
			}
			break;
		}	
	}
	
	private void initAddMoreDialog(boolean result){
		AlertDialog.Builder builder = new AlertDialog.Builder(FemalePageFour.this);


    	if(result){
    		builder.setMessage("Do you want to add another person?")
			.setTitle("Success!");
    		builder.setNegativeButton("No,Thanks",new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Utils.clearAllHouseholdInfo(getApplicationContext());
					Utils.clearAllPersonalInfo(getApplicationContext());
					Intent intent = new Intent(FemalePageFour.this,HHRootMenuActivity.class);
					startActivity(intent);
					finish();
				}
			});
    		builder.setPositiveButton("Yes,lets do.",new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Utils.clearAllPersonalInfo(getApplicationContext());
					Intent intent = new Intent(FemalePageFour.this,PPRootMenuActivity.class);
					startActivity(intent);
					finish();
				}
			});
    	}else{
    		builder.setMessage("Error on Data uploading.")
			.setTitle("Failed!");
    	}
    	AlertDialog dialog = builder.create();
    	dialog.show();
	}
	
	private void setDraftStatus(){
		sharedPreferences =  getSharedPreferences("personalInformation", 0);
		spEditor = sharedPreferences.edit();
		spEditor.putString("personDraftStatus", "draft");
		spEditor.putString("personDraftWhere",  Constants.FemalePageFour_DRAFT_WHERE);
		spEditor.commit();
	}
	private void initLocationService(){
		Intent serviceIntent = new Intent(this,LocationTrackerService.class);
        startService(serviceIntent);
	}

	
	private void updateSharedPreferrence(){
		sharedPreferences =  getSharedPreferences("personalInformation", 0);
		spEditor = sharedPreferences.edit();
	    
		spEditor.putString("ageMenarcheYears",edittextAgeMenarcheYear.getText().toString() );
		spEditor.putString("ageMenarcheMonths",  edittextAgeMenarcheMonth.getText().toString() );
		spEditor.putString("isMennopauseApplicable", getSwitchValue(switchAgeMenopause));
		spEditor.putString("ageMennopauseYears",  edittextAgeMenopauseYears.getText().toString() );
		spEditor.putString("isFirstPregnencyApplicable", getSwitchValue(switchAgeFirstPregnency) );
		spEditor.putString("ageFirstPregnencyYears", edittextAgeFirstPregnencyYear.getText().toString() );
		spEditor.putString("ageFirstPregnencyMonths", edittextAgeFirstPregnencyMonth.getText().toString() );
		spEditor.putString("noFullTermPregnency", editextFullTermPregnency.getText().toString());
		
		spEditor.putString("noMonthsBreastFeeding", editextFullBreastFeedingMonths.getText().toString());
		spEditor.putString("isCurrentOralContraceptives", getSwitchValue(switchCurrentOralContraceptives));
		spEditor.putString("isCurrentInjectableContraceptives", getSwitchValue(switchCurrentInjectableContraceptives));
		spEditor.putString("isEverOralContraceptives", getSwitchValue(switchEverOralContraceptives));
		spEditor.putString("personDraftStatus", "finished");
		
		
		spEditor.commit();
	}
	private String getSwitchValue(Switch switchView){
		return (String)(switchView.isChecked()?switchView.getTextOn():switchView.getTextOff());
	}
	
	private boolean validateData(){
		
		if(!FieldValidationUtils.validateEdittextValueForNumber(edittextAgeMenarcheYear))
		{
			Toast.makeText(this, "Please enter age at menarche!", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(getSwitchValue(switchAgeMenopause).equalsIgnoreCase("Yes") && !FieldValidationUtils.validateEdittextValueForText(edittextAgeMenopauseYears))
		{
			Toast.makeText(this, "Please enter age at menopause!", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(getSwitchValue(switchAgeFirstPregnency).equalsIgnoreCase("Yes"))
		{
            if(!FieldValidationUtils.validateEdittextValueForNumber(edittextAgeFirstPregnencyYear)) {
                Toast.makeText(this, "Please enter year at first full term pregnancy!", Toast.LENGTH_SHORT).show();
                return false;
            }
            if(!FieldValidationUtils.validateEdittextValueForNumber(edittextAgeFirstPregnencyMonth)) {
                Toast.makeText(this, "Please enter month at first full term pregnancy!", Toast.LENGTH_SHORT).show();
                return false;
            }
		}			
		if(!FieldValidationUtils.validateEdittextValueForText(editextFullTermPregnency))
		{
			Toast.makeText(this, "Please enter number of full term pregnancies!", Toast.LENGTH_SHORT).show();
			return false;
		}		
		if(!FieldValidationUtils.validateEdittextValueForText(editextFullBreastFeedingMonths))
		{
			Toast.makeText(this, "Please enter total number of months breast-feeding!", Toast.LENGTH_SHORT).show();
			return false;
		}

		return true;			
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
			progressDialog = new ProgressDialog(FemalePageFour.this);
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
        	
        	if(submitted){
        		initAddMoreDialog(result);
        	}
       }
    }


}

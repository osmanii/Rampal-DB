package com.osmani.rampal.personal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.amadergram.rampal.survey.R;
import com.osmani.location.LocationTrackerService;
import com.osmani.model.FemalePersonalModel;
import com.osmani.rampal.upazillasurvey.JSONParser;
import com.osmani.utils.Constants;
import com.osmani.utils.FieldValidationUtils;
import com.osmani.utils.Utils;
import com.osmani.utils.ViewUtils;

import org.json.JSONObject;

public class FemalePageThree extends Activity implements OnClickListener {
	
	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor spEditor;
	private Button nextButton;
	private Button backButton;
	private Button draftButton;
	
	// switches
	Switch switchHeartDisease,switchMajorDisability,switchHepA,switchHepB,switchHPV,switchTetanus;
	EditText editTextHeight,editTextWeight,editTextSystolic1,editTextDiastolic1,editTextSystolic2,editTextDiastolic2;

	EditText typeOfDisabilityET;
	
	private FemaleHttpAsyncTask submittingTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pp_activity_female_page_two_2);
		
		setTitle("Female Page 3 ID:" + Utils.getPersonId(getApplicationContext()));
				
		setDraftStatus();
		initLocationService();

		switchHeartDisease = (Switch) findViewById(R.id.switchHeartDisease);
		switchMajorDisability = (Switch) findViewById(R.id.switchMajorDisability);
		switchHepA = (Switch) findViewById(R.id.switchHepA);
		switchHepB = (Switch) findViewById(R.id.switchHepB);
		switchHPV = (Switch) findViewById(R.id.switchHPV);
		switchTetanus = (Switch) findViewById(R.id.switchTetanus);

		editTextHeight = (EditText) findViewById(R.id.editTextHeight);
		editTextWeight = (EditText) findViewById(R.id.editTextWeight);
		editTextSystolic1 = (EditText) findViewById(R.id.editTextSystolic1);
		editTextDiastolic1 = (EditText) findViewById(R.id.editTextDiastolic1);
		editTextSystolic2 = (EditText) findViewById(R.id.editTextSystolic2);
		editTextDiastolic2 = (EditText) findViewById(R.id.editTextDiastolic2);

		typeOfDisabilityET = (EditText) findViewById(R.id.typeOfDisabilityET);
		
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
		if(sharedPreferences.getString("isHeartDisease", "").equals("")==false && sharedPreferences.getString("isHeartDisease", "").equalsIgnoreCase("null")==false)
		{
			if(sharedPreferences.getString("isHeartDisease", "").equalsIgnoreCase("Yes"))
				switchHeartDisease.setChecked(true);
			else switchHeartDisease.setChecked(false);
		}
		if(sharedPreferences.getString("isMajorDisability", "").equals("")==false && sharedPreferences.getString("isMajorDisability", "").equalsIgnoreCase("null")==false)
		{
			if(sharedPreferences.getString("isMajorDisability", "").equalsIgnoreCase("Yes"))
				switchMajorDisability.setChecked(true);
			else switchMajorDisability.setChecked(false);
		}
		if(sharedPreferences.getString("isHepA", "").equals("")==false && sharedPreferences.getString("isHepA", "").equalsIgnoreCase("null")==false)
		{
			if(sharedPreferences.getString("isHepA", "").equalsIgnoreCase("Yes"))
				switchHepA.setChecked(true);
			else switchHepA.setChecked(false);
		}
		if(sharedPreferences.getString("isHepB", "").equals("")==false && sharedPreferences.getString("isHepB", "").equalsIgnoreCase("null")==false)
		{
			if(sharedPreferences.getString("isHepB", "").equalsIgnoreCase("Yes"))
				switchHepB.setChecked(true);
			else switchHepB.setChecked(false);
		}
		if(sharedPreferences.getString("isHPV", "").equals("")==false && sharedPreferences.getString("isHPV", "").equalsIgnoreCase("null")==false)
		{
			if(sharedPreferences.getString("isHPV", "").equalsIgnoreCase("Yes"))
				switchHPV.setChecked(true);
			else switchHPV.setChecked(false);
		}
		if(sharedPreferences.getString("isTetanus", "").equals("")==false && sharedPreferences.getString("isTetanus", "").equalsIgnoreCase("null")==false)
		{
			if(sharedPreferences.getString("isTetanus", "").equalsIgnoreCase("Yes"))
				switchTetanus.setChecked(true);
			else switchTetanus.setChecked(false);
		}
		if(sharedPreferences.getString("heightFemale", "").equals("")==false && sharedPreferences.getString("heightFemale", "").equalsIgnoreCase("null")==false)
		{
			editTextHeight.setText(sharedPreferences.getString("heightFemale", ""));
		}

		if(sharedPreferences.getString("weightFemale", "").equals("")==false && sharedPreferences.getString("weightFemale", "").equalsIgnoreCase("null")==false)
		{
			editTextWeight.setText(sharedPreferences.getString("weightFemale", ""));
		}
		if(sharedPreferences.getString("systolic1", "").equals("")==false && sharedPreferences.getString("systolic1", "").equalsIgnoreCase("null")==false)
		{
			editTextSystolic1.setText(sharedPreferences.getString("systolic1", ""));
		}
		if(sharedPreferences.getString("diastolic1", "").equals("")==false && sharedPreferences.getString("diastolic1", "").equalsIgnoreCase("null")==false)
		{
			editTextDiastolic1.setText(sharedPreferences.getString("diastolic1", ""));
		}
		if(sharedPreferences.getString("systolic2", "").equals("")==false && sharedPreferences.getString("systolic2", "").equalsIgnoreCase("null")==false)
		{
			editTextSystolic2.setText(sharedPreferences.getString("systolic2", ""));
		}
		if(sharedPreferences.getString("diastolic2", "").equals("")==false && sharedPreferences.getString("diastolic2", "").equalsIgnoreCase("null")==false)
		{
			editTextDiastolic2.setText(sharedPreferences.getString("diastolic2", ""));
		}
		if(sharedPreferences.getString("typeOfDisability", "").equals("")==false && sharedPreferences.getString("typeOfDisability", "").equalsIgnoreCase("null")==false)
		{
			typeOfDisabilityET.setText(sharedPreferences.getString("typeOfDisability", ""));
		}
	}

	
	private void setDraftStatus(){
		sharedPreferences =  getSharedPreferences("personalInformation", 0);
		spEditor = sharedPreferences.edit();
		spEditor.putString("personDraftStatus", "draft");
		spEditor.putString("personDraftWhere",  Constants.FemalePageThree_DRAFT_WHERE);
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
				
			
				Intent i = new Intent(FemalePageThree.this, FemalePageFour.class);
				startActivity(i);
				finish();
			}

			break;
		case R.id.backButton:
			Intent i = new Intent(FemalePageThree.this, FemalePagetwo.class);
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

		spEditor.putString("isHeartDisease",getSwitchValue(switchHeartDisease) );
		spEditor.putString("isMajorDisability",  getSwitchValue(switchMajorDisability));

		spEditor.putString("typeOfDisability",  typeOfDisabilityET.getText().toString());

		spEditor.putString("isHepA", getSwitchValue(switchHepA));
		spEditor.putString("isHepB",  getSwitchValue(switchHepB));
		spEditor.putString("isHPV",  getSwitchValue(switchHPV));
		spEditor.putString("isTetanus",  getSwitchValue(switchTetanus));


		spEditor.putString("heightFemale",  editTextHeight.getText().toString());
		spEditor.putString("weightFemale",  editTextWeight.getText().toString());
		spEditor.putString("systolic1",  editTextSystolic1.getText().toString());
		spEditor.putString("diastolic1",  editTextDiastolic1.getText().toString());
		spEditor.putString("systolic2",  editTextSystolic2.getText().toString());
		spEditor.putString("diastolic2",  editTextDiastolic2.getText().toString());
		
		spEditor.commit();
	}
	private String getSwitchValue(Switch switchView){
		return (String)(switchView.isChecked()?switchView.getTextOn():switchView.getTextOff());
	}

	private boolean validateData(){

		if(!FieldValidationUtils.validateEdittextValueForNumber(editTextHeight))
		{
			Toast.makeText(this, "Please enter height!", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(!FieldValidationUtils.validateEdittextValueForNumber(editTextWeight))
		{
			Toast.makeText(this, "Please enter weight!", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(!FieldValidationUtils.validateEdittextValueForNumber(editTextSystolic1))
		{
			Toast.makeText(this, "Please enter systolic 1st value!", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(!FieldValidationUtils.validateEdittextValueForNumber(editTextDiastolic1))
		{
			Toast.makeText(this, "Please enter diastolic 1st value!", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(!FieldValidationUtils.validateEdittextValueForNumber(editTextSystolic2))
		{
			Toast.makeText(this, "Please enter systolic 2nd value!", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(!FieldValidationUtils.validateEdittextValueForNumber(editTextDiastolic2))
		{
			Toast.makeText(this, "Please enter diastolic 2nd value!", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(ViewUtils.getSwitchValue(switchMajorDisability).compareToIgnoreCase("Yes")==0 && !FieldValidationUtils.validateEdittextValueForText(typeOfDisabilityET)){
			Toast.makeText(this, "Please enter the type of disability!", Toast.LENGTH_SHORT).show();
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
			progressDialog = new ProgressDialog(FemalePageThree.this);
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

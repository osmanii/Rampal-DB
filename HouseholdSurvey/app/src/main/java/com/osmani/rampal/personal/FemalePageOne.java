package com.osmani.rampal.personal;

import org.json.JSONObject;

import com.osmani.location.LocationTrackerService;
import com.osmani.model.FemalePersonalModel;
import com.osmani.model.MalePersonalModel;
import com.osmani.rampal.upazillasurvey.JSONParser;
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
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.AsyncTask.Status;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

public class FemalePageOne extends Activity implements OnClickListener {

	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor spEditor;
	private Button nextButton;
	private Button backButton;
	private Button draftButton;
	
	// switches
	Switch switchHyperTension,switchDiabetes,switchIHD,switchCancer,switchAsthma,switchOtherMajor,switchChronicPain;
	EditText edittextCancerType,edittextOtherMajorEt,edittextChronicPainMajorSite,edittextChronicPainCurrentPainLevel,edittextChronicPainWorstPainLevel;
	
	private FemaleHttpAsyncTask submittingTask;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pp_activity_male_page_one);
		
		setTitle("Female Page 1 ID:" + Utils.getPersonId(getApplicationContext()));
				
		setDraftStatus();
		initLocationService();
		
		switchHyperTension = (Switch) findViewById(R.id.switchHyperTension);
		switchDiabetes = (Switch) findViewById(R.id.switchDiabetes);
		switchIHD = (Switch) findViewById(R.id.switchIHD);
		switchCancer = (Switch) findViewById(R.id.switchCancer);
		switchAsthma = (Switch) findViewById(R.id.switchAsthma);
		switchOtherMajor = (Switch) findViewById(R.id.switchOtherMajor);
		switchChronicPain = (Switch) findViewById(R.id.switchChronicPain);
		
		edittextCancerType = (EditText) findViewById(R.id.edittextCancerType);
		edittextOtherMajorEt = (EditText) findViewById(R.id.edittextOtherMajorEt);
		edittextChronicPainMajorSite = (EditText) findViewById(R.id.edittextChronicPainMajorSite);
		edittextChronicPainCurrentPainLevel = (EditText) findViewById(R.id.edittextChronicPainCurrentPainLevel);
		edittextChronicPainWorstPainLevel = (EditText) findViewById(R.id.edittextChronicPainWorstPainLevel);
		
		
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
		
		sharedPreferences =  getSharedPreferences("personalInformation", 0);
		if(sharedPreferences.getString("hypertension", "").equals("")==false && sharedPreferences.getString("hypertension", "").equalsIgnoreCase("null")==false)		
		{
			if(sharedPreferences.getString("hypertension", "").equalsIgnoreCase("Yes"))
				switchHyperTension.setChecked(true);
			else switchHyperTension.setChecked(false);			
		}
		if(sharedPreferences.getString("diabetes", "").equals("")==false && sharedPreferences.getString("diabetes", "").equalsIgnoreCase("null")==false)		
		{
			if(sharedPreferences.getString("diabetes", "").equalsIgnoreCase("Yes"))
				switchDiabetes.setChecked(true);
			else switchDiabetes.setChecked(false);			
		}
		if(sharedPreferences.getString("chestPainOnExertion", "").equals("")==false && sharedPreferences.getString("chestPainOnExertion", "").equalsIgnoreCase("null")==false)		
		{
			if(sharedPreferences.getString("chestPainOnExertion", "").equalsIgnoreCase("Yes"))
				switchIHD.setChecked(true);
			else switchIHD.setChecked(false);			
		}
		if(sharedPreferences.getString("isCancer", "").equals("")==false && sharedPreferences.getString("isCancer", "").equalsIgnoreCase("null")==false)		
		{
			if(sharedPreferences.getString("isCancer", "").equalsIgnoreCase("Yes"))
				switchCancer.setChecked(true);
			else switchCancer.setChecked(false);			
		}
		if(sharedPreferences.getString("cancerType", "").equals("")==false && sharedPreferences.getString("cancerType", "").equalsIgnoreCase("null")==false)
		{
			edittextCancerType.setText(sharedPreferences.getString("cancerType", ""));
		}
		if(sharedPreferences.getString("asthma", "").equals("")==false && sharedPreferences.getString("asthma", "").equalsIgnoreCase("null")==false)		
		{
			if(sharedPreferences.getString("asthma", "").equalsIgnoreCase("Yes"))
				switchAsthma.setChecked(true);
			else switchAsthma.setChecked(false);			
		}
		if(sharedPreferences.getString("isActiveMajorCondition", "").equals("")==false && sharedPreferences.getString("isActiveMajorCondition", "").equalsIgnoreCase("null")==false)		
		{
			if(sharedPreferences.getString("isActiveMajorCondition", "").equalsIgnoreCase("Yes"))
				switchOtherMajor.setChecked(true);
			else switchOtherMajor.setChecked(false);			
		}
		if(sharedPreferences.getString("activeMajorConditionName", "").equals("")==false && sharedPreferences.getString("activeMajorConditionName", "").equalsIgnoreCase("null")==false)		
		{
			edittextOtherMajorEt.setText(sharedPreferences.getString("activeMajorConditionName", ""));
		}
		if(sharedPreferences.getString("isChronicPain", "").equals("")==false && sharedPreferences.getString("isChronicPain", "").equalsIgnoreCase("null")==false)		
		{
			if(sharedPreferences.getString("isChronicPain", "").equalsIgnoreCase("Yes"))
				switchChronicPain.setChecked(true);
			else switchChronicPain.setChecked(false);			
		}
		if(sharedPreferences.getString("chronicPainMajorSite", "").equals("")==false && sharedPreferences.getString("chronicPainMajorSite", "").equalsIgnoreCase("null")==false)		
		{
			edittextChronicPainMajorSite.setText(sharedPreferences.getString("chronicPainMajorSite", ""));
		}
		if(sharedPreferences.getString("chronicPainCurrentLevel", "").equals("")==false && sharedPreferences.getString("chronicPainCurrentLevel", "").equalsIgnoreCase("null")==false)		
		{
			edittextChronicPainCurrentPainLevel.setText(sharedPreferences.getString("chronicPainCurrentLevel", ""));
		}
		if(sharedPreferences.getString("chronicPainWorstLevel", "").equals("")==false && sharedPreferences.getString("chronicPainWorstLevel", "").equalsIgnoreCase("null")==false)		
		{
			edittextChronicPainWorstPainLevel.setText(sharedPreferences.getString("chronicPainWorstLevel", ""));
		}
	}
	
	private void setDraftStatus(){
		sharedPreferences =  getSharedPreferences("personalInformation", 0);
		spEditor = sharedPreferences.edit();
		spEditor.putString("personDraftStatus", "draft");
		spEditor.putString("personDraftWhere",  Constants.FemalePageOne_DRAFT_WHERE);
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
				Intent i = new Intent(FemalePageOne.this, FemalePagetwo.class);
				startActivity(i);
				finish();
			}

			break;
		case R.id.backButton:
			Intent i = new Intent(FemalePageOne.this, PPFifthPage.class);
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

	
	private void updateSharedPreferrence(){
		spEditor = sharedPreferences.edit();
	    
		spEditor.putString("hypertension",getSwitchValue(switchHyperTension) );
		spEditor.putString("diabetes",  getSwitchValue(switchDiabetes));
		spEditor.putString("chestPainOnExertion", getSwitchValue(switchIHD));
		spEditor.putString("isCancer",  getSwitchValue(switchCancer));
		spEditor.putString("cancerType", ViewUtils.getEditTextInput(edittextCancerType));
		spEditor.putString("asthma",  getSwitchValue(switchAsthma));
		spEditor.putString("isActiveMajorCondition",  getSwitchValue(switchOtherMajor));
		spEditor.putString("activeMajorConditionName",  ViewUtils.getEditTextInput(edittextOtherMajorEt));
		spEditor.putString("isChronicPain",  getSwitchValue(switchChronicPain));
		
		spEditor.putString("chronicPainMajorSite",  ViewUtils.getEditTextInput(edittextChronicPainMajorSite));
		spEditor.putString("chronicPainCurrentLevel",  ViewUtils.getEditTextInput(edittextChronicPainCurrentPainLevel));
		spEditor.putString("chronicPainWorstLevel",  ViewUtils.getEditTextInput(edittextChronicPainWorstPainLevel));
		
		
		spEditor.commit();
	}
	private String getSwitchValue(Switch switchView){
		return (String)(switchView.isChecked()?switchView.getTextOn():switchView.getTextOff());
	}
	
	private boolean validateData(){
		
		if(getSwitchValue(switchCancer).equalsIgnoreCase("Yes") && !FieldValidationUtils.validateEdittextValueForText(edittextCancerType))
		{
			Toast.makeText(this, "Please enter type of the cancer!", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if(getSwitchValue(switchOtherMajor).equalsIgnoreCase("Yes") && !FieldValidationUtils.validateEdittextValueForText(edittextOtherMajorEt))
		{
			Toast.makeText(this, "Please enter name of active major health condition!", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if(getSwitchValue(switchChronicPain).equalsIgnoreCase("Yes") && !FieldValidationUtils.validateEdittextValueForText(edittextChronicPainMajorSite))
		{
			Toast.makeText(this, "Please enter major site of pain!", Toast.LENGTH_SHORT).show();
			return false;
		}			
		else if(getSwitchValue(switchChronicPain).equalsIgnoreCase("Yes") && !FieldValidationUtils.validateEdittextValueForText(edittextChronicPainCurrentPainLevel))
		{
			Toast.makeText(this, "Please enter current pain level!", Toast.LENGTH_SHORT).show();
			return false;
		}		
		else if(getSwitchValue(switchChronicPain).equalsIgnoreCase("Yes") && !FieldValidationUtils.validateEdittextValueForText(edittextChronicPainWorstPainLevel))
		{
			Toast.makeText(this, "Please enter worst pain level!", Toast.LENGTH_SHORT).show();
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
			progressDialog = new ProgressDialog(FemalePageOne.this);
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

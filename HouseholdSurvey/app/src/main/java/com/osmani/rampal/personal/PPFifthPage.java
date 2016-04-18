package com.osmani.rampal.personal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.osmani.location.LocationTrackerService;
import com.osmani.model.CommonPersonalModel;
import com.osmani.rampal.upazillasurvey.JSONParser;
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
import android.widget.RadioButton;
import android.widget.Toast;

public class PPFifthPage extends Activity implements OnClickListener {

	SharedPreferences sharedPreferences;
	SharedPreferences.Editor spEditor;

	String diedAny = " ", diedAnyCancer = " ";

	EditText reason_death, type_cancer;


	boolean dataUploaded = false;

	private RadioButton diedAnyYes, diedAnyNo, diedAnyCancerYes, diedAnyCancerNo;
	private Button nextButton;
	private Button backButton;
	private Button draftButton;
	
	private String chosenGender=null;
	
	private CommonPersonHttpAsyncTask submittingTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pp_activity_fifth_page);
		
		setTitle("Personal Page 5 ID:" + Utils.getPersonId(getApplicationContext()));
				
		setDraftStatus();
		initLocationService();


		reason_death = (EditText) findViewById(R.id.diedDiseaseET);
		type_cancer = (EditText) findViewById(R.id.cancerET);
		
		diedAnyYes = (RadioButton)findViewById(R.id.diedYes);
		diedAnyNo = (RadioButton)findViewById(R.id.diedNo);
		diedAnyCancerYes = (RadioButton)findViewById(R.id.cancerYes);
		diedAnyCancerNo = (RadioButton)findViewById(R.id.cancerNo);

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
		if(sharedPreferences.getString("diedAnyByDisease", "").equals("")==false && sharedPreferences.getString("diedAnyByDisease", "").equalsIgnoreCase("null")==false)		
		{
			if(sharedPreferences.getString("diedAnyByDisease", "").equalsIgnoreCase("Yes"))
			{
				diedAnyYes.setChecked(true);
				diedAny = "Yes";
			}
			else if(sharedPreferences.getString("diedAnyByDisease", "").equalsIgnoreCase("No"))
			{
				diedAnyNo.setChecked(true);
				diedAny = "No";
			}
		}
		if(sharedPreferences.getString("reasonForDeath", "").equals("")==false && sharedPreferences.getString("reasonForDeath", "").equalsIgnoreCase("null")==false)
		{
			reason_death.setText(sharedPreferences.getString("reasonForDeath", ""));
		}
		if(sharedPreferences.getString("diedAnyByCancer", "").equals("")==false && sharedPreferences.getString("diedAnyByCancer", "").equalsIgnoreCase("null")==false)		
		{
			if(sharedPreferences.getString("diedAnyByCancer", "").equalsIgnoreCase("Yes"))
			{
				diedAnyCancerYes.setChecked(true);
				diedAnyCancer = "Yes";
			}
			else if(sharedPreferences.getString("diedAnyByCancer", "").equalsIgnoreCase("No"))
			{
				diedAnyCancerNo.setChecked(true);
				diedAnyCancer = "No";
			}
		}
		if(sharedPreferences.getString("typeOfCancer", "").equals("")==false && sharedPreferences.getString("typeOfCancer", "").equalsIgnoreCase("null")==false)
		{
			type_cancer.setText(sharedPreferences.getString("typeOfCancer", ""));
		}

	}
	
	public void onRadioButtonClicked(View view) {
		// TODO Auto-generated method stub
	    boolean checked = ((RadioButton) view).isChecked();
	    

	    switch(view.getId()) {
	        case R.id.diedYes:
	            if (checked)
	            	diedAny = "Yes";
	            Log.d("<x_x>", "Died any: " + diedAny);
	            break;
	        case R.id.diedNo:
	            if (checked)
	            	diedAny = "No";
	            Log.d("<x_x>", "Died any: " + diedAny);
	            break;
	        case R.id.cancerYes:
	            if (checked)
	            	diedAnyCancer = "Yes";
	            Log.d("<x_x>", "Died any caner: " + diedAnyCancer);
	            break;
	        case R.id.cancerNo:
	            if (checked)
	            	diedAnyCancer = "No";
	            Log.d("<x_x>", "Died any caner: " + diedAnyCancer);
	            break;
	    }
	}
	
	@Override
	public void onClick(View view) {
	
		switch (view.getId()) {
		case R.id.nextButton:
			
			if(validateData()){
				
				updateSharedPreferrence();
				initGenderChoiceDialog();
			}

			break;
		case R.id.backButton:
			Intent i = new Intent(PPFifthPage.this, PPFourthPage.class);
			startActivity(i);
			finish();

			break;
		case R.id.draftButton:
			if(submittingTask==null){
				submittingTask= new CommonPersonHttpAsyncTask();
				submittingTask.execute();
			}else{
				if(submittingTask.getStatus()==Status.RUNNING ||submittingTask.getStatus()== Status.PENDING ){
					Toast.makeText(getApplicationContext(), "Already submitting data", Toast.LENGTH_LONG).show();
				}else{
					submittingTask= new CommonPersonHttpAsyncTask();
					submittingTask.execute();
				}
			}
			break;
		}	
	}
	
	
	private void initGenderChoiceDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Gender Choice");
		builder.setMessage("Please select gender");
		builder.setPositiveButton("Male", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				chosenGender = "Male";
				updateSharedPreferrence();
				Intent i = new Intent(PPFifthPage.this, MalePageOne.class);
				startActivity(i);
				finish();
				
			}
		});
		builder.setNegativeButton("Female", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				chosenGender = "Female";
				updateSharedPreferrence();
				Intent i = new Intent(PPFifthPage.this, FemalePageOne.class);
				startActivity(i);
				finish();
			}
		});
		
		AlertDialog dialog = builder.create();
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}
	
	private void showResultDialog(boolean result){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);


    	if(result){
    		builder.setMessage("Drafted Household Id :"+Utils.getHouseHoldId(getApplicationContext()))
			.setTitle("Success!");
    	}else{
    		builder.setMessage("Problem occurred, Please submit again.")
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

	private void setDraftStatus(){
		
		sharedPreferences =  getSharedPreferences("personalInformation", 0);
		spEditor = sharedPreferences.edit();
		spEditor.putString("personDraftStatus", "draft");
		spEditor.putString("personDraftWhere",  Constants.PPFifthPage_DRAFT_WHERE);
		spEditor.commit();
	}
	private void initLocationService(){
		Intent serviceIntent = new Intent(this,LocationTrackerService.class);
        startService(serviceIntent);
	}
	
	
	private void updateSharedPreferrence(){
	
		sharedPreferences =  getSharedPreferences("personalInformation", 0);
		spEditor = sharedPreferences.edit();
		
		spEditor.putString("chosenGender",  chosenGender);
		spEditor.putString("diedAnyByDisease",  diedAny);
		spEditor.putString("reasonForDeath",  reason_death.getText().toString());
		spEditor.putString("diedAnyByCancer",  diedAnyCancer);
		spEditor.putString("typeOfCancer",  type_cancer.getText().toString());
		spEditor.commit();	

	}
	
	private boolean validateData(){
		
		if(diedAny.equals(" "))
		{
			Toast.makeText(this, "Please select \"Died any member in last six months?\"!", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if(diedAny.equals("Yes") && !FieldValidationUtils.validateEdittextValueForText(reason_death))
		{
			Toast.makeText(this, "Please enter the reason for death!", Toast.LENGTH_SHORT).show();
			return false;
		}			
		else if(diedAnyCancer.equals(" "))
		{
			Toast.makeText(this, "Please select \"Died any member by cancer disease?\"!", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if(diedAnyCancer.equals("Yes") && !FieldValidationUtils.validateEdittextValueForText(type_cancer))
		{
			Toast.makeText(this, "Please enter type of cancer!", Toast.LENGTH_SHORT).show();
			return false;
		}			

		return true;				
		
	}
	
	
	private class CommonPersonHttpAsyncTask extends AsyncTask<Void, Void, Boolean> {
		
		private CommonPersonalModel model ;
		private String SERVER_POSTING_URL="http://www.ag-climatedata.net/ws_rhdp/update_person.php";
		// Progress Dialog
		private ProgressDialog progressDialog;
		
		      
		public CommonPersonHttpAsyncTask() {
			super();
			this.model = Utils.getCommonPersonalModelObject(getApplicationContext());
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(PPFifthPage.this);
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
        	
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}

			showResultDialog(result);
       }
    }

}

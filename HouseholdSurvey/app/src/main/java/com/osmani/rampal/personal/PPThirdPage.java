package com.osmani.rampal.personal;

import org.json.JSONObject;

import com.osmani.location.LocationTrackerService;
import com.osmani.model.CommonPersonalModel;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;


public class PPThirdPage extends Activity implements OnClickListener{
	
	SharedPreferences sharedPreferences;
	SharedPreferences.Editor spEditor;
	
	Switch symptom, lump, doctor;
	 
	Spinner education, mainIncomeSource;
	String symptom_str = "No", lump_str = "No", doctor_str = "No";
	
	EditText symptomET, diseaseET, illnessET, medicineET;
	
	private Button nextButton;
	private Button backButton;
	private Button draftButton;

	private CommonPersonHttpAsyncTask submittingTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pp_activity_third_page);
		
		setTitle("Personal Page 3 ID:" + Utils.getPersonId(getApplicationContext()));
		
		setDraftStatus();
		initLocationService();
		
	    sharedPreferences =  getSharedPreferences("personalInformation", 0);
	    
	    symptom = (Switch) findViewById(R.id.symptomSwitch);
	    lump = (Switch) findViewById(R.id.lumpSwitch);
	    doctor = (Switch) findViewById(R.id.doctorSwitch);
		
	    symptomET = (EditText) findViewById(R.id.symptomET);
	    diseaseET = (EditText) findViewById(R.id.diseaseNameET);
	    illnessET = (EditText) findViewById(R.id.illnessET);
	    medicineET = (EditText) findViewById(R.id.medicineET);

		
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
		if(sharedPreferences.getString("isSymptom", "").equals("")==false && sharedPreferences.getString("isSymptom", "").equalsIgnoreCase("null")==false)		
		{
			if(sharedPreferences.getString("isSymptom", "").equalsIgnoreCase("Yes"))
			{
				symptom.setChecked(true);
				symptom_str = "Yes";
			}
			else
			{
				symptom.setChecked(false);
				symptom_str = "No";
			}			
		}
		if(sharedPreferences.getString("symptom", "").equals("")==false && sharedPreferences.getString("symptom", "").equalsIgnoreCase("null")==false)
		{
			symptomET.setText(sharedPreferences.getString("symptom", ""));
		}
		if(sharedPreferences.getString("lump", "").equals("")==false && sharedPreferences.getString("lump", "").equalsIgnoreCase("null")==false)		
		{
			if(sharedPreferences.getString("lump", "").equalsIgnoreCase("Yes"))
			{
				lump.setChecked(true);
				lump_str = "Yes";
			}
			else 
			{
				lump.setChecked(false);
				lump_str = "No";
			}			
		}
		if(sharedPreferences.getString("illness", "").equals("")==false && sharedPreferences.getString("illness", "").equalsIgnoreCase("null")==false)		
		{
			illnessET.setText(sharedPreferences.getString("illness", ""));
		}
		if(sharedPreferences.getString("doctorCheckUp", "").equals("")==false && sharedPreferences.getString("doctorCheckUp", "").equalsIgnoreCase("null")==false)		
		{
			if(sharedPreferences.getString("doctorCheckUp", "").equalsIgnoreCase("Yes"))
			{
				doctor.setChecked(true);
				doctor_str = "Yes";
			}
			else 
			{
				doctor.setChecked(false);
				doctor_str = "No";
			}			
		}
		if(sharedPreferences.getString("diseaseDoctorCheckUp", "").equals("")==false && sharedPreferences.getString("diseaseDoctorCheckUp", "").equalsIgnoreCase("null")==false)		
		{
			diseaseET.setText(sharedPreferences.getString("diseaseDoctorCheckUp", ""));
		}
		if(sharedPreferences.getString("medicine", "").equals("")==false && sharedPreferences.getString("medicine", "").equalsIgnoreCase("null")==false)		
		{
			medicineET.setText(sharedPreferences.getString("medicine", ""));
		}
	}
	
	public void onSwitchClicked(View view) {
	    // Is the toggle on?
	    boolean on = ((Switch) view).isChecked();
	    
	    if(view == symptom)
	    {	    
	    	if (on) {
	    		symptom_str = "Yes";
	    	} else {
	    		symptom_str = "No";
	    	}
	    }
	    else if(view == lump)
	    {	    
	    	if (on) {
	    		lump_str = "Yes";
	    	} else {
	    		lump_str = "No";
	    	}
	    }
	    else if(view == doctor)
	    {	    
	    	if (on) {
	    		doctor_str = "Yes";
	    	} else {
	    		doctor_str = "No";
	    	}
	    }
	}
	
	private void setDraftStatus(){
		
		sharedPreferences =  getSharedPreferences("personalInformation", 0);
		spEditor = sharedPreferences.edit();
		spEditor.putString("personDraftStatus", "draft");
		spEditor.putString("personDraftWhere",  Constants.PPThirdPage_DRAFT_WHERE);
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
				Intent i = new Intent(PPThirdPage.this, PPFourthPage.class);
				startActivity(i);
				finish();
			}

			break;
		case R.id.backButton:
			Intent i = new Intent(PPThirdPage.this, PPSecondPage.class);
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
	private void updateSharedPreferrence(){
		spEditor = sharedPreferences.edit();
	    
		spEditor.putString("isSymptom",  symptom_str);
		spEditor.putString("symptom", ViewUtils.getEditTextInput(symptomET));
		spEditor.putString("lump",  lump_str);
		spEditor.putString("illness", ViewUtils.getEditTextInput(illnessET));
		spEditor.putString("doctorCheckUp", doctor_str);
		spEditor.putString("diseaseDoctorCheckUp", ViewUtils.getEditTextInput(diseaseET));
		spEditor.putString("medicine", ViewUtils.getEditTextInput(medicineET));
		
		spEditor.commit();	
	}
	
	private boolean validateData(){
	
		if(symptom_str.equals("Yes") && !FieldValidationUtils.validateEdittextValueForText(symptomET))
		{
			Toast.makeText(this, "Please enter visible health problem!", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if(!FieldValidationUtils.validateEdittextValueForText(illnessET))
		{
			Toast.makeText(this, "Please enter cause of illness!", Toast.LENGTH_SHORT).show();
			return false;
		}	
		else if(doctor_str.equals("Yes") && !FieldValidationUtils.validateEdittextValueForText(diseaseET))
		{
			Toast.makeText(this, "Please enter disease name!", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if(doctor_str.equals("Yes") && !FieldValidationUtils.validateEdittextValueForText(medicineET))
		{
			Toast.makeText(this, "Please enter name of the medicine!", Toast.LENGTH_SHORT).show();
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
			progressDialog = new ProgressDialog(PPThirdPage.this);
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

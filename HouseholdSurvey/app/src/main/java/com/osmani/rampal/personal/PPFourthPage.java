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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

public class PPFourthPage extends Activity implements OnClickListener, OnItemSelectedListener{

	SharedPreferences sharedPreferences;
	SharedPreferences.Editor spEditor;
	
	Spinner treatment;
	
	String currentMedicine_str = " ", immunization_str = " ", medicalCheckUp_str = " ", treatment_str = " ";

	EditText currentMedicine, prescribedBy;
	
	private RadioButton medicineYes, medicineNo, immunizationYes, immunizationNo, checkupYes, checkupNo;
	private Button nextButton;
	private Button backButton;
	private Button draftButton;
	
	private CommonPersonHttpAsyncTask submittingTask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setTitle("Personal Page 4 ID:" + Utils.getPersonId(getApplicationContext()));
		
		setContentView(R.layout.pp_activity_fourth_page);
		setDraftStatus();
		initLocationService();
		
	    sharedPreferences =  getSharedPreferences("personalInformation", 0);
		
		treatment = (Spinner) findViewById(R.id.treatmentSpinner);
		ArrayAdapter<CharSequence> treatment_adapter = ArrayAdapter.createFromResource(this,
		        R.array.treatment, android.R.layout.simple_spinner_item);
		treatment_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		
		treatment.setAdapter(treatment_adapter);		
		treatment.setOnItemSelectedListener(this);
						
		currentMedicine = (EditText)findViewById(R.id.currentMedicineET);
		prescribedBy = (EditText)findViewById(R.id.prescribedET);

		medicineYes = (RadioButton)findViewById(R.id.medicineYes);
		medicineNo = (RadioButton)findViewById(R.id.medicineNo);
		immunizationYes = (RadioButton)findViewById(R.id.immunizationYes);
		immunizationNo = (RadioButton)findViewById(R.id.immunizationNo);
		checkupYes = (RadioButton)findViewById(R.id.medicalCheckUpYes);
		checkupNo = (RadioButton)findViewById(R.id.medicalCheckUpNo);
		
		
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
		if(sharedPreferences.getString("anyCurrentMedicine", "").equals("")==false && sharedPreferences.getString("anyCurrentMedicine", "").equalsIgnoreCase("null")==false)		
		{
			if(sharedPreferences.getString("anyCurrentMedicine", "").equalsIgnoreCase("Yes"))
			{
				medicineYes.setChecked(true);
				currentMedicine_str = "Yes";
			}
			else if(sharedPreferences.getString("anyCurrentMedicine", "").equalsIgnoreCase("No"))
			{
				medicineNo.setChecked(true);
				currentMedicine_str = "No";
			}
		}
		if(sharedPreferences.getString("currentMedicine", "").equals("")==false && sharedPreferences.getString("currentMedicine", "").equalsIgnoreCase("null")==false)
		{
			currentMedicine.setText(sharedPreferences.getString("currentMedicine", ""));
		}
		if(sharedPreferences.getString("prescribedBy", "").equals("")==false && sharedPreferences.getString("prescribedBy", "").equalsIgnoreCase("null")==false)		
		{
			prescribedBy.setText(sharedPreferences.getString("prescribedBy", ""));
		}
		if(sharedPreferences.getString("treatment", "").equals("")==false && sharedPreferences.getString("treatment", "").equalsIgnoreCase("null")==false)
		{
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
			        R.array.treatment, android.R.layout.simple_spinner_item);
			treatment.setSelection(adapter.getPosition(sharedPreferences.getString("treatment", ""))); 
		}
		if(sharedPreferences.getString("immunization", "").equals("")==false && sharedPreferences.getString("immunization", "").equalsIgnoreCase("null")==false)		
		{
			if(sharedPreferences.getString("immunization", "").equalsIgnoreCase("Yes"))
			{
				immunizationYes.setChecked(true);
				immunization_str = "Yes";
			}
			else if(sharedPreferences.getString("immunization", "").equalsIgnoreCase("No"))
			{
				immunizationNo.setChecked(true);
				immunization_str = "No";
			}
		}
		if(sharedPreferences.getString("medicalCheckUp", "").equals("")==false && sharedPreferences.getString("medicalCheckUp", "").equalsIgnoreCase("null")==false)		
		{
			if(sharedPreferences.getString("medicalCheckUp", "").equalsIgnoreCase("Yes"))
			{
				checkupYes.setChecked(true);
				medicalCheckUp_str = "Yes";
			}
			else if(sharedPreferences.getString("medicalCheckUp", "").equalsIgnoreCase("No"))
			{
				checkupNo.setChecked(true);
				medicalCheckUp_str = "No";
			}
		}

	}
	
	private void setDraftStatus(){
		
		sharedPreferences =  getSharedPreferences("personalInformation", 0);
		spEditor = sharedPreferences.edit();
		spEditor.putString("personDraftStatus", "draft");
		spEditor.putString("personDraftWhere",  Constants.PPFourthPage_DRAFT_WHERE);
		spEditor.commit();
	}
	private void initLocationService(){
		Intent serviceIntent = new Intent(this,LocationTrackerService.class);
        startService(serviceIntent);
	}

	@Override
	public void onItemSelected(AdapterView<?> spinner, View arg1, int position,
			long id) {
		// TODO Auto-generated method stub
		if(spinner.getId() == treatment.getId())
		{
			treatment_str = spinner.getItemAtPosition(position).toString();
			Log.d("<x_x>",  treatment_str);
		}
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void onRadioButtonClicked(View view)
	{
		// TODO Auto-generated method stub
	    boolean checked = ((RadioButton) view).isChecked();
	    
	    switch(view.getId()) {
	        case R.id.medicineYes:
	            if (checked)
	            	currentMedicine_str = "Yes";
	            Log.d("<x_x>", "Current medicine: " + currentMedicine_str);
	            break;
	        case R.id.medicineNo:
	            if (checked)
	            	currentMedicine_str = "No";
	            Log.d("<x_x>", "Current medicine: " + currentMedicine_str);
	            break;
	        case R.id.immunizationYes:
	            if (checked)
	            	immunization_str = "Yes";
	            Log.d("<x_x>", "Immunization: " + immunization_str);
	            break;
	        case R.id.immunizationNo:
	            if (checked)
	            	immunization_str = "No";
	            Log.d("<x_x>", "Immunization: " + immunization_str);
	            break;
	        case R.id.medicalCheckUpYes:
	            if (checked)
	            	medicalCheckUp_str = "Yes";
	            Log.d("<x_x>", "Medical Check-up: " + medicalCheckUp_str);
	            break;
	        case R.id.medicalCheckUpNo:
	            if (checked)
	            	medicalCheckUp_str = "No";
	            Log.d("<x_x>", "Medical Check-up: " + medicalCheckUp_str);
	            break;
	    }
	}
	
	@Override
	public void onClick(View view) {
	
		switch (view.getId()) {
		case R.id.nextButton:
			
			if(validateData()){
				updateSharedPreferrence();
				Intent i = new Intent(PPFourthPage.this, PPFifthPage.class);
				startActivity(i);
				finish();
			}

			break;
		case R.id.backButton:
			Intent i = new Intent(PPFourthPage.this, PPThirdPage.class);
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
	    
		spEditor.putString("anyCurrentMedicine",  currentMedicine_str);
		spEditor.putString("currentMedicine", ViewUtils.getEditTextInput(currentMedicine));
		spEditor.putString("prescribedBy",  ViewUtils.getEditTextInput(prescribedBy));
		spEditor.putString("treatment",  treatment_str);
		spEditor.putString("immunization",  immunization_str);
		spEditor.putString("medicalCheckUp",  medicalCheckUp_str);
		
		spEditor.commit();
	}
	
	private boolean validateData(){
		
		if(currentMedicine_str.equals(" "))
		{
			Toast.makeText(this, "Please answer \"Are you using the medicine now?\" !", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if(currentMedicine_str.equals("Yes") && !FieldValidationUtils.validateEdittextValueForText(currentMedicine))
		{
			Toast.makeText(this, "Please enter name of the current medicine!", Toast.LENGTH_SHORT).show();	
			return false;
		}	
		else if(currentMedicine_str.equals("Yes") && !FieldValidationUtils.validateEdittextValueForText(prescribedBy))
		{
			Toast.makeText(this, "Please answer \"who prescribed it?\" !", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if(!FieldValidationUtils.validateSpinnerValue(treatment))
		{
			Toast.makeText(this, "Please select where you go for treatment!", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if(immunization_str.equals(" "))
		{
			Toast.makeText(this, "Please select immnunization for >5 children!", Toast.LENGTH_SHORT).show();
			return false;
		}			
		else if(medicalCheckUp_str.equals(" "))
		{
			Toast.makeText(this, "Please select medical checkup/gynecologic examination during pregnancy!", Toast.LENGTH_SHORT).show();
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
			progressDialog = new ProgressDialog(PPFourthPage.this);
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

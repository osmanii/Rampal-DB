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
import android.widget.Switch;
import android.widget.Toast;

public class PPSecondPage extends Activity implements OnClickListener, OnItemSelectedListener{
	
	SharedPreferences sharedPreferences;
	SharedPreferences.Editor spEditor;

	private Switch switchBloodGroup;
	private Button nextButton;
	private Button backButton;
	private Button draftButton;
	
	
	Spinner education, profession, maritalStatus, healthCondition; 	
	EditText height, relation;
	Spinner bloodGroup ;
	String isBloodGroup = "No", education_str = " ", profession_str = " ", maritalStatus_str = " ", healthCondition_str = " ";
	
	private CommonPersonHttpAsyncTask submittingTask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pp_activity_second_page);		
		
		setTitle("Personal Page 2 ID:" + Utils.getPersonId(getApplicationContext()));
		
	    setDraftStatus();
	    initLocationService();
		
		height = (EditText) findViewById(R.id.heightET);
		relation = (EditText) findViewById(R.id.relationET);
		bloodGroup = (Spinner) findViewById(R.id.spinnerBloodGroup);

		education = (Spinner) findViewById(R.id.educationSpinner);
		ArrayAdapter<CharSequence> education_adapter = ArrayAdapter.createFromResource(this,
		        R.array.lastEducation, android.R.layout.simple_spinner_item);
		education_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		
		education.setAdapter(education_adapter);		
		education.setOnItemSelectedListener(this);

		profession = (Spinner) findViewById(R.id.professionSpinner);
		ArrayAdapter<CharSequence> profession_adapter = ArrayAdapter.createFromResource(this,
		        R.array.profession, android.R.layout.simple_spinner_item);
		profession_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		
		profession.setAdapter(profession_adapter);		
		profession.setOnItemSelectedListener(this);

		maritalStatus = (Spinner) findViewById(R.id.maritalSpinner);
		ArrayAdapter<CharSequence> maritalStatus_adapter = ArrayAdapter.createFromResource(this,
		        R.array.maritalStatus, android.R.layout.simple_spinner_item);
		maritalStatus_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		
		maritalStatus.setAdapter(maritalStatus_adapter);		
		maritalStatus.setOnItemSelectedListener(this);

		healthCondition = (Spinner) findViewById(R.id.healthSpinner);
		ArrayAdapter<CharSequence> healthCondition_adapter = ArrayAdapter.createFromResource(this,
		        R.array.healthCondition, android.R.layout.simple_spinner_item);
		healthCondition_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		
		healthCondition.setAdapter(healthCondition_adapter);		
		healthCondition.setOnItemSelectedListener(this);

		switchBloodGroup = (Switch) findViewById(R.id.bloodSwitch);
		
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
		if(sharedPreferences.getString("education", "").equals("")==false && sharedPreferences.getString("education", "").equalsIgnoreCase("null")==false)
		{
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
			        R.array.lastEducation, android.R.layout.simple_spinner_item);
			education.setSelection(adapter.getPosition(sharedPreferences.getString("education", ""))); 
		}
		if(sharedPreferences.getString("height", "").equals("")==false && sharedPreferences.getString("height", "").equalsIgnoreCase("null")==false)
		{
			height.setText(sharedPreferences.getString("height", ""));
		}
		if(sharedPreferences.getString("profession", "").equals("")==false && sharedPreferences.getString("profession", "").equalsIgnoreCase("null")==false)
		{
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
			        R.array.profession, android.R.layout.simple_spinner_item);
			profession.setSelection(adapter.getPosition(sharedPreferences.getString("profession", ""))); 
		}
		if(sharedPreferences.getString("relationWithFamilyHead", "").equals("")==false && sharedPreferences.getString("relationWithFamilyHead", "").equalsIgnoreCase("null")==false)
		{
			relation.setText(sharedPreferences.getString("relationWithFamilyHead", ""));
		}
		if(sharedPreferences.getString("isBloodGroup", "").equals("")==false && sharedPreferences.getString("isBloodGroup", "").equalsIgnoreCase("null")==false)		
		{
			if(sharedPreferences.getString("isBloodGroup", "").equalsIgnoreCase("Yes"))
				switchBloodGroup.setChecked(true);
			else switchBloodGroup.setChecked(false);			
		}

		if(sharedPreferences.getString("bloodGroup", "").equals("")==false && sharedPreferences.getString("bloodGroup", "").equalsIgnoreCase("null")==false)
		{
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
			        R.array.bloodGroups, android.R.layout.simple_spinner_item);
			bloodGroup.setSelection(adapter.getPosition(sharedPreferences.getString("bloodGroup", ""))); 
		}
		if(sharedPreferences.getString("maritalStatus", "").equals("")==false && sharedPreferences.getString("maritalStatus", "").equalsIgnoreCase("null")==false)
		{
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
			        R.array.maritalStatus, android.R.layout.simple_spinner_item);
			maritalStatus.setSelection(adapter.getPosition(sharedPreferences.getString("maritalStatus", ""))); 
		}
		if(sharedPreferences.getString("healthCondition", "").equals("")==false && sharedPreferences.getString("healthCondition", "").equalsIgnoreCase("null")==false)
		{
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
			        R.array.healthCondition, android.R.layout.simple_spinner_item);
			healthCondition.setSelection(adapter.getPosition(sharedPreferences.getString("healthCondition", ""))); 
		}
		
	}


	
	public void onSwitchClicked(View view) 
	{
	    // Is the toggle on?
	    boolean on = ((Switch) view).isChecked();
	    
	    if (on) {
	    	isBloodGroup = "Yes";
	    } else {
	    	isBloodGroup = "No";
	    }
	}
	
	@Override
	public void onClick(View view) {
	
		switch (view.getId()) {
		case R.id.nextButton:
			
			if(validateData()){
				updateSharedPreferrence();
				Intent i = new Intent(PPSecondPage.this, PPThirdPage.class);
				startActivity(i);
				finish();
			}

			break;
		case R.id.backButton:
			Intent i = new Intent(PPSecondPage.this, PPFirstPage.class);
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
	private void setDraftStatus(){
		
		sharedPreferences =  getSharedPreferences("personalInformation", 0);
		spEditor = sharedPreferences.edit();
		spEditor.putString("personDraftStatus", "draft");
		spEditor.putString("personDraftWhere",  Constants.PPSecondPage_DRAFT_WHERE);
		spEditor.commit();
	}
	private void initLocationService(){
		Intent serviceIntent = new Intent(this,LocationTrackerService.class);
        startService(serviceIntent);
	}
	private void updateSharedPreferrence(){
		sharedPreferences =  getSharedPreferences("personalInformation", 0);
		spEditor = sharedPreferences.edit();
	    
		spEditor.putString("education",  education_str);
		spEditor.putString("height", ViewUtils.getEditTextInput(height));
		spEditor.putString("profession",  profession_str);
		spEditor.putString("relationWithFamilyHead",  ViewUtils.getEditTextInput(relation));
		spEditor.putString("isBloodGroup",  isBloodGroup);
		spEditor.putString("bloodGroup",  bloodGroup.getSelectedItem().toString());
		spEditor.putString("maritalStatus",  maritalStatus_str);
		spEditor.putString("healthCondition",  healthCondition_str);
		
		spEditor.commit();
	}
	
	private boolean validateData(){
		
		if(!FieldValidationUtils.validateSpinnerValue(education))
		{
			Toast.makeText(this, "Please select last education!", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if(!FieldValidationUtils.validateEdittextValueForNumber(height))
		{
			Toast.makeText(this, "Please enter height!", Toast.LENGTH_SHORT).show();
			return false;
		}	
		else if(!FieldValidationUtils.validateSpinnerValue(profession))
		{
			Toast.makeText(this, "Please select profession!", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if(!FieldValidationUtils.validateEdittextValueForText(relation))
		{
			Toast.makeText(this, "Please enter relation with head of the family!", Toast.LENGTH_SHORT).show();
			return false;
		}	
		else if(isBloodGroup.equals("Yes") && !FieldValidationUtils.validateSpinnerValue(bloodGroup))
		{
			Toast.makeText(this, "Please select blood group!", Toast.LENGTH_SHORT).show();
			return false;
		}			
		else if(!FieldValidationUtils.validateSpinnerValue(maritalStatus))
		{
			Toast.makeText(this, "Please select marital status!", Toast.LENGTH_SHORT).show();
			return false;
		}		
		else if(!FieldValidationUtils.validateSpinnerValue(healthCondition))
		{
			Toast.makeText(this, "Please select present health condition!", Toast.LENGTH_SHORT).show();
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
			progressDialog = new ProgressDialog(PPSecondPage.this);
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
	public void onItemSelected(AdapterView<?> spinner, View arg1, int position,
			long id) {
		// TODO Auto-generated method stub
		if(spinner.getId() == education.getId())
		{
			education_str = spinner.getItemAtPosition(position).toString();
			Log.d("x_x",  education_str);
		}
		else if(spinner.getId() == profession.getId())
		{
			profession_str = spinner.getItemAtPosition(position).toString();
			Log.d("x_x",  profession_str);
		}
		else if(spinner.getId() == maritalStatus.getId())
		{
			maritalStatus_str = spinner.getItemAtPosition(position).toString();
			Log.d("x_x",  maritalStatus_str);
		}
		else if(spinner.getId() == healthCondition.getId())
		{
			healthCondition_str = spinner.getItemAtPosition(position).toString();
			Log.d("x_x",  healthCondition_str);
		}
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
}

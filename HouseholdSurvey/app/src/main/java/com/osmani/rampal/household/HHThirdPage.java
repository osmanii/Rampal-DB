package com.osmani.rampal.household;

import org.json.JSONObject;

import com.osmani.location.LocationTrackerService;
import com.osmani.model.HouseholdModel;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.view.View.OnClickListener;


public class HHThirdPage extends Activity implements OnClickListener, OnItemSelectedListener{
	
	SharedPreferences sharedPreferences;
	SharedPreferences.Editor spEditor;
	
	Spinner education, mainIncomeSource;
	String education_Str = " ", mainIncomeSource_Str = " ", majorDisability = " ";
	
	EditText total, male, female, years0to5, years5to11, years11to19, years19to49, years49to60, years60Plus;
	

	private RadioButton disabilityYes, disabilityNo;
	private Button nextButton;
	private Button backButton;
	private Button draftButton;
	
	// clarification?
	int totalMembers = 0, maleMembers = 0, femaleMembers = 0, y0to5Members = 0, y5to11Members = 0, y11to19Members = 0, y19to49Members = 0, y49to60Members = 0, y60PlusMembers = 0;
	
	private HouseholdHttpAsyncTask submittingTask;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hh_activity_third_page);
		
		setTitle("Household Page 3 ID:" + Utils.getHouseHoldId(getApplicationContext()));

		setDraftStatus();
		initLocationService();		
	    
		
		education = (Spinner) findViewById(R.id.education);
		ArrayAdapter<CharSequence> education_adapter = ArrayAdapter.createFromResource(this,
		        R.array.lastEducation, android.R.layout.simple_spinner_item);
		education_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		
		education.setAdapter(education_adapter);		
		education.setOnItemSelectedListener(this);

		mainIncomeSource = (Spinner) findViewById(R.id.spinnerPrimaryIncome);
		ArrayAdapter<CharSequence> mainIncomeSource_adapter = ArrayAdapter.createFromResource(this,
		        R.array.incomeSource, android.R.layout.simple_spinner_item);
		mainIncomeSource_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		
		mainIncomeSource.setAdapter(mainIncomeSource_adapter);		
		mainIncomeSource.setOnItemSelectedListener(this);
		
		total = (EditText)findViewById(R.id.totalFamilyMembersET);
		male = (EditText)findViewById(R.id.maleMembersET);
		female = (EditText)findViewById(R.id.femaleMembersET);
		years0to5 = (EditText)findViewById(R.id.years0to5ET);
		years5to11 = (EditText)findViewById(R.id.years5to11ET);
		years11to19 = (EditText)findViewById(R.id.years11to19ET);
		years19to49 = (EditText)findViewById(R.id.years19to49ET);
		years49to60 = (EditText)findViewById(R.id.years49to60ET);
		years60Plus = (EditText)findViewById(R.id.years60PlusET);
		
		disabilityYes = (RadioButton) findViewById(R.id.disabilityYes);
		disabilityNo = (RadioButton) findViewById(R.id.disabilityNo);
		
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

		sharedPreferences =  getSharedPreferences("householdInformation", 0);
		if(sharedPreferences.getString("lastEducation", "").equals("")==false && sharedPreferences.getString("lastEducation", "").equalsIgnoreCase("null")==false)
		{
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
			        R.array.lastEducation, android.R.layout.simple_spinner_item);
			education.setSelection(adapter.getPosition(sharedPreferences.getString("lastEducation", ""))); 
		}
		if(sharedPreferences.getString("fmTotal", "").equals("")==false && sharedPreferences.getString("fmTotal", "").equalsIgnoreCase("null")==false)
		{
			total.setText(sharedPreferences.getString("fmTotal", ""));
		}
		if(sharedPreferences.getString("fmMale", "").equals("")==false && sharedPreferences.getString("fmMale", "").equalsIgnoreCase("null")==false)		
		{
			male.setText(sharedPreferences.getString("fmMale", ""));
		}
		if(sharedPreferences.getString("fmFemale", "").equals("")==false && sharedPreferences.getString("fmFemale", "").equalsIgnoreCase("null")==false)
		{
			female.setText(sharedPreferences.getString("fmFemale", ""));
		}
		if(sharedPreferences.getString("fm0to5", "").equals("")==false && sharedPreferences.getString("fm0to5", "").equalsIgnoreCase("null")==false)		
		{
			years0to5.setText(sharedPreferences.getString("fm0to5", ""));
		}
		if(sharedPreferences.getString("fm5to11", "").equals("")==false && sharedPreferences.getString("fm5to11", "").equalsIgnoreCase("null")==false)
		{
			years5to11.setText(sharedPreferences.getString("fm5to11", ""));
		}
		if(sharedPreferences.getString("fm11to19", "").equals("")==false && sharedPreferences.getString("fm11to19", "").equalsIgnoreCase("null")==false)		
		{
			years11to19.setText(sharedPreferences.getString("fm11to19", ""));
		}
		if(sharedPreferences.getString("fm19to49", "").equals("")==false && sharedPreferences.getString("fm19to49", "").equalsIgnoreCase("null")==false)
		{
			years19to49.setText(sharedPreferences.getString("fm19to49", ""));
		}
		if(sharedPreferences.getString("fm49to60", "").equals("")==false && sharedPreferences.getString("fm49to60", "").equalsIgnoreCase("null")==false)		
		{
			years49to60.setText(sharedPreferences.getString("fm49to60", ""));
		}
		if(sharedPreferences.getString("fm60Above", "").equals("")==false && sharedPreferences.getString("fm60Above", "").equalsIgnoreCase("null")==false)		
		{
			years60Plus.setText(sharedPreferences.getString("fm60Above", ""));
		}
		if(sharedPreferences.getString("disability", "").equals("")==false && sharedPreferences.getString("disability", "").equalsIgnoreCase("null")==false)		
		{
			if(sharedPreferences.getString("disability", "").equalsIgnoreCase("Yes"))
			{
				disabilityYes.setChecked(true);
				majorDisability = "Yes";
			}
			else if(sharedPreferences.getString("disability", "").equalsIgnoreCase("No"))
			{
				disabilityNo.setChecked(true);
				majorDisability = "No";
			}
		}
		if(sharedPreferences.getString("mainIncome", "").equals("")==false && sharedPreferences.getString("mainIncome", "").equalsIgnoreCase("null")==false)
		{
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
			        R.array.incomeSource, android.R.layout.simple_spinner_item);
			mainIncomeSource.setSelection(adapter.getPosition(sharedPreferences.getString("mainIncome", ""))); 
		}
	}

	
	private void setDraftStatus(){
		
		sharedPreferences =  getSharedPreferences("householdInformation", 0);
		spEditor = sharedPreferences.edit();
		spEditor.putString("householdDraftStatus", "draft");
		spEditor.putString("householdDraftWhere",  Constants.HHThirdPage_DRAFT_WHERE);
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
				Intent i = new Intent(HHThirdPage.this, HHFourthPage.class);
				startActivity(i);
				finish();
			}

			break;
		case R.id.backButton:
			Intent i = new Intent(HHThirdPage.this, HHSecondPage.class);
			startActivity(i);
			finish();

			break;
		case R.id.draftButton:
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

		}	
	}
	private void updateSharedPreferrence(){
		Log.d("<x_x>", "Male members: " + maleMembers + "\n" + y5to11Members);
		sharedPreferences =  getSharedPreferences(Constants.SHAREDPREFERRENCE_TAG_HOUSEHOLD, 0);
	    spEditor = sharedPreferences.edit();
	    
		spEditor.putString("lastEducation",  education_Str);
		spEditor.putString("fmTotal",  totalMembers + "");
		spEditor.putString("fmMale",  maleMembers + "");
		spEditor.putString("fmFemale",  femaleMembers + "");
		spEditor.putString("fm0to5",  y0to5Members + "");
		spEditor.putString("fm5to11",  y5to11Members + "");
		spEditor.putString("fm11to19",  y11to19Members + "");
		spEditor.putString("fm19to49",  y19to49Members + "");
		spEditor.putString("fm49to60",  y49to60Members + "");
		spEditor.putString("fm60Above",  y60PlusMembers + "");
		spEditor.putString("disability",  majorDisability);
		spEditor.putString("mainIncome",  mainIncomeSource_Str);
		
		spEditor.commit();	
	}
	
	private boolean validateData(){

		boolean wrongTotal = false;

		try {
			if (FieldValidationUtils.validateEdittextValueForNumber(total))
				totalMembers = Integer.parseInt(total.getText().toString());

			if (FieldValidationUtils.validateEdittextValueForNumber(male))
				maleMembers = Integer.parseInt(male.getText().toString());
			if (FieldValidationUtils.validateEdittextValueForNumber(female))
				femaleMembers = Integer.parseInt(female.getText().toString());
			
			if(totalMembers != (maleMembers + femaleMembers))
				wrongTotal = true;
			
			if (FieldValidationUtils.validateEdittextValueForNumber(years0to5))
				y0to5Members = Integer.parseInt(years0to5.getText().toString());
			if (FieldValidationUtils.validateEdittextValueForNumber(years5to11))
				y5to11Members = Integer.parseInt(years5to11.getText().toString());
			if (FieldValidationUtils.validateEdittextValueForNumber(years11to19))
				y11to19Members = Integer.parseInt(years11to19.getText().toString());
			if (FieldValidationUtils.validateEdittextValueForNumber(years19to49))
				y19to49Members = Integer.parseInt(years19to49.getText().toString());
			if (FieldValidationUtils.validateEdittextValueForNumber(years49to60))
				y49to60Members = Integer.parseInt(years49to60.getText().toString());
			if (FieldValidationUtils.validateEdittextValueForNumber(years60Plus))
				y60PlusMembers = Integer.parseInt(years60Plus.getText().toString());

			if (totalMembers != (y0to5Members + y5to11Members + y11to19Members + y19to49Members
					+ y49to60Members + y60PlusMembers)) 
			{
				wrongTotal = true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Toast.makeText(this, "Invalid family number!",Toast.LENGTH_SHORT).show();
			return false;
		}

		if (!FieldValidationUtils.validateSpinnerValue(education)) {
			Toast.makeText(this, "Please select last education!", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if(totalMembers == 0)
		{
			Toast.makeText(this, "Number of family members can't be zero!",	Toast.LENGTH_SHORT).show();
			return false;			
		}
		else if (!FieldValidationUtils.validateEdittextValueForNumber(total)) {
			Toast.makeText(this, "Enter number of family members!",	Toast.LENGTH_SHORT).show();
			return false;
		} 
		else if (wrongTotal) {
			Toast.makeText(this, "Total number of family member is wrong!",	Toast.LENGTH_SHORT).show();
			return false;
		} 
		else if (majorDisability.equals(" ")) {
			Toast.makeText(this, "Please select major disability!",	Toast.LENGTH_SHORT).show();
			return false;
		}
		else if (!FieldValidationUtils.validateSpinnerValue(mainIncomeSource)) {
			Toast.makeText(this, "Please select main source of income!", Toast.LENGTH_SHORT).show();
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
			progressDialog = new ProgressDialog(HHThirdPage.this);
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
			education_Str = spinner.getItemAtPosition(position).toString();
			Log.d("x_x",  education_Str);
		}
		else if(spinner.getId() == mainIncomeSource.getId())
		{
			mainIncomeSource_Str = spinner.getItemAtPosition(position).toString();
			Log.d("x_x",  education_Str);
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
	        case R.id.disabilityYes:
	            if (checked)
	            	majorDisability = "Yes";
	            Log.d("<x_x>", "Major Diasbility: " + majorDisability);
	            break;
	        case R.id.disabilityNo:
	            if (checked)
	            	majorDisability = "No";
	            Log.d("<x_x>", "Major Diasbility: " + majorDisability);
	            break;
	    }
	}
}

package com.osmani.rampal.personal;

import java.util.Calendar;

import org.joda.time.LocalDate;
import org.joda.time.Years;
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
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.AsyncTask.Status;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

public class PPFirstPage extends Activity implements OnClickListener{

	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor spEditor;
	private EditText familyHead, name, mothersName, fathersName, dob, age, voterBirth;
	private Button nextButton;
	private Button backButton;
	private Button draftButton;
	
	
	private String personalId;
	private CommonPersonHttpAsyncTask submittingTask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pp_activity_first_page);
		
		setTitle("Personal Page 1 ID:" + Utils.getPersonId(getApplicationContext()));
		
		setDraftStatus();
		initLocationService();
		
		
		Bundle bundle = getIntent().getExtras();
		if(bundle!=null){
			personalId = bundle.getString("personalId", "sample");
			//getActionBar().setTitle(personalId);
		}
	    
	    familyHead = (EditText) findViewById(R.id.nameHeadET);
	    name = (EditText) findViewById(R.id.nameET);
	    mothersName = (EditText) findViewById(R.id.motherNameET);
	    fathersName = (EditText) findViewById(R.id.fatherNameET);
	    dob = (EditText) findViewById(R.id.dateOfBirthET);
	    
//		dob.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            showStartDateDialog();
//        }
//	});
	
	// i have modified this to see whether calender pop-up on touch--------Osmani
	// if it doesn't work comment the follwing block and uncomment block above.
	dob.setOnTouchListener(new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if (event.getAction() == MotionEvent.ACTION_DOWN)
			{
				showStartDateDialog();
			}			
			return false;
		}
	});

	    
	    age = (EditText) findViewById(R.id.ageET);
	    voterBirth = (EditText) findViewById(R.id.voterIdET);
	    	    	    
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
		
		personalId = Utils.getPersonId(getApplicationContext());
		
		sharedPreferences =  getSharedPreferences("personalInformation", 0);
		if(sharedPreferences.getString("nameFamilyHead", "").equals("")==false && sharedPreferences.getString("nameFamilyHead", "").equalsIgnoreCase("null")==false)
		{
			familyHead.setText(sharedPreferences.getString("nameFamilyHead", ""));
		}
		if(sharedPreferences.getString("name", "").equals("")==false && sharedPreferences.getString("name", "").equalsIgnoreCase("null")==false)
		{
			name.setText(sharedPreferences.getString("name", ""));
		}
		if(sharedPreferences.getString("mothersName", "").equals("")==false && sharedPreferences.getString("mothersName", "").equalsIgnoreCase("null")==false)		
		{
			mothersName.setText(sharedPreferences.getString("mothersName", ""));
		}
		if(sharedPreferences.getString("fathersName", "").equals("")==false && sharedPreferences.getString("fathersName", "").equalsIgnoreCase("null")==false)		
		{
			fathersName.setText(sharedPreferences.getString("fathersName", ""));
		}
		if(sharedPreferences.getString("dob", "").equals("")==false && sharedPreferences.getString("dob", "").equalsIgnoreCase("null")==false)		
		{
			dob.setText(sharedPreferences.getString("dob", ""));
		}
		if(sharedPreferences.getString("age", "").equals("")==false && sharedPreferences.getString("age", "").equalsIgnoreCase("null")==false)		
		{
			age.setText(sharedPreferences.getString("age", ""));
			age.setEnabled(false);
		}
		if(sharedPreferences.getString("voterOrBirth", "").equals("")==false && sharedPreferences.getString("voterOrBirth", "").equalsIgnoreCase("null")==false)		
		{
			voterBirth.setText(sharedPreferences.getString("voterOrBirth", ""));
		}
	}


	@Override
	public void onClick(View view) {
	
		switch (view.getId()) {
		case R.id.nextButton:
			
			if(validateData()){
				updateSharedPreferrence();
				Intent i = new Intent(PPFirstPage.this, PPSecondPage.class);
				startActivity(i);
			}

			break;
		case R.id.backButton:
			super.onBackPressed();

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
		spEditor.putString("personDraftWhere",  Constants.PPFirstPage_DRAFT_WHERE);
		spEditor.commit();
	}
	private void initLocationService(){
		Intent serviceIntent = new Intent(this,LocationTrackerService.class);
        startService(serviceIntent);
	}
	
	
	private void updateSharedPreferrence(){
		
	    sharedPreferences =  getSharedPreferences("personalInformation", 0);
		spEditor = sharedPreferences.edit();
	    
		spEditor.putString("personId",  personalId);
		spEditor.putString("nameFamilyHead", ViewUtils.getEditTextInput(familyHead));
		spEditor.putString("name",  ViewUtils.getEditTextInput(name));
		spEditor.putString("mothersName",  ViewUtils.getEditTextInput(mothersName));
		spEditor.putString("fathersName",  ViewUtils.getEditTextInput(fathersName));
		spEditor.putString("dob",  ViewUtils.getEditTextInput(dob));
		spEditor.putString("age",  ViewUtils.getEditTextInput(age));
		spEditor.putString("voterOrBirth",  ViewUtils.getEditTextInput(voterBirth));
		
		spEditor.commit();
	}
	
	private boolean validateData(){
		
		if(!FieldValidationUtils.validateEdittextValueForText(familyHead))
		{
			Toast.makeText(this, "Please enter name of head of the family!", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if(!FieldValidationUtils.validateEdittextValueForText(name))
		{
			Toast.makeText(this, "Please enter name!", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if(!FieldValidationUtils.validateEdittextValueForText(mothersName))
		{
			Toast.makeText(this, "Please enter mother's name!", Toast.LENGTH_SHORT).show();
			return false;
		}			
		else if(!FieldValidationUtils.validateEdittextValueForText(fathersName))
		{
			Toast.makeText(this, "Please enter father's name!", Toast.LENGTH_SHORT).show();
			return false;
		}		
		else if(!FieldValidationUtils.validateEdittextValueForText(dob))
		{
			Toast.makeText(this, "Please enter date of birth!", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if(!FieldValidationUtils.validateEdittextValueForText(age))
		{
			Toast.makeText(this, "Please enter age!", Toast.LENGTH_SHORT).show();
			return false;
		}			
		else if(!FieldValidationUtils.validateEdittextValueForText(voterBirth))
		{
			Toast.makeText(this, "Please enter voter id or birth registration no!", Toast.LENGTH_SHORT).show();
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
			progressDialog = new ProgressDialog(PPFirstPage.this);
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
	
	
	public void showStartDateDialog(){
        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        System.out.println("the selected " + mDay);
        DatePickerDialog dialog = new DatePickerDialog(PPFirstPage.this,
                new mDateSetListener(), mYear, mMonth, mDay);

        dialog.setTitle("Set Date of Birth");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.show();
    }
// an inner class

class mDateSetListener implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            // getCalender();
            int mYear = year;
            int mMonth = monthOfYear;
            int mDay = dayOfMonth;
            dob.setText(new StringBuilder()
                    // Month is 0 based so add 1
                    .append(mYear).append("-")
                    .append(mMonth + 1).append("-")
                    .append(mDay).append(""));
            
            LocalDate birthdate = new LocalDate (mYear, mMonth+1, mDay);
            LocalDate now = new LocalDate();
            Years ageYears = Years.yearsBetween(birthdate, now);
            
            age.setText(ageYears.getYears() + "");
            age.setEnabled(false);
        }
    }


}

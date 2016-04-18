package com.osmani.rampal.household;

import java.util.Calendar;

import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.json.JSONObject;

import com.osmani.location.LocationTrackerService;
import com.osmani.model.HouseholdModel;
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
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.AsyncTask.Status;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

public class HHSecondPage extends Activity implements OnClickListener{
	
	SharedPreferences sharedPreferences;
	SharedPreferences.Editor spEditor;

	Button next;
	Button back;
	
	EditText nameHead, mothersName, fathersName, mobileNo, dob, age;
	String isMobile = "No"; 
	
	private Switch switchMobile,switchSafewWaterActionBoil, switchSafewWaterActionFilter,switchSafewWaterActionOther,switchSafewWaterActionNothing;
	private Button nextButton;
	private Button backButton;
	private Button draftButton;
	
	private HouseholdHttpAsyncTask submittingTask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hh_activity_second_page);
		
		setTitle("Household Page 2 ID:" + Utils.getHouseHoldId(getApplicationContext()));

		setDraftStatus();
		initLocationService();
		
	    		
		
		nameHead = (EditText) findViewById(R.id.nameHeadET);
		mothersName = (EditText) findViewById(R.id.mothersNameET);
		fathersName = (EditText) findViewById(R.id.fatherNameET);
		switchMobile = (Switch) findViewById(R.id.mobileSwitch);
		mobileNo = (EditText) findViewById(R.id.mobileNoET);
		dob = (EditText) findViewById(R.id.dateOfBirthET);
		age = (EditText) findViewById(R.id.ageET);
		age.setEnabled(false);


		switchSafewWaterActionBoil = (Switch) findViewById(R.id.switchSafewWaterActionBoil);
		switchSafewWaterActionFilter = (Switch) findViewById(R.id.switchSafewWaterActionFilter);
		switchSafewWaterActionOther = (Switch) findViewById(R.id.switchSafewWaterActionOther);
		switchSafewWaterActionNothing = (Switch) findViewById(R.id.switchSafewWaterActionNothing);

//		dob.setOnClickListener(new View.OnClickListener() {
//	        @Override
//	        public void onClick(View view) {
//	            showStartDateDialog();
//	        }
//		});
		
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
		if(sharedPreferences.getString("nameFamilyHead", "").equals("")==false && sharedPreferences.getString("nameFamilyHead", "").equalsIgnoreCase("null")==false)
		{
			nameHead.setText(sharedPreferences.getString("nameFamilyHead", ""));
		}
		if(sharedPreferences.getString("mothersName", "").equals("")==false && sharedPreferences.getString("mothersName", "").equalsIgnoreCase("null")==false)		
		{
			mothersName.setText(sharedPreferences.getString("mothersName", ""));
		}
		if(sharedPreferences.getString("fathersName", "").equals("")==false && sharedPreferences.getString("fathersName", "").equalsIgnoreCase("null")==false)		
		{
			fathersName.setText(sharedPreferences.getString("fathersName", ""));
		}
		if(sharedPreferences.getString("mobile", "").equals("")==false && sharedPreferences.getString("mobile", "").equalsIgnoreCase("null")==false)		
		{
			if(sharedPreferences.getString("mobile", "").equalsIgnoreCase("Yes"))
				switchMobile.setChecked(true);
			else switchMobile.setChecked(false);			
		}
		if(sharedPreferences.getString("mobileNo", "").equals("")==false && sharedPreferences.getString("mobileNo", "").equalsIgnoreCase("null")==false)		
		{
			mobileNo.setText(sharedPreferences.getString("mobileNo", ""));
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
		if(sharedPreferences.getString("waterBoil", "").equalsIgnoreCase("Yes"))
			switchSafewWaterActionBoil.setChecked(true);
		else
			switchSafewWaterActionBoil.setChecked(false);

		if(sharedPreferences.getString("waterFilter", "").equalsIgnoreCase("Yes"))
			switchSafewWaterActionFilter.setChecked(true);
		else
			switchSafewWaterActionFilter.setChecked(false);

		if(sharedPreferences.getString("waterOther", "").equalsIgnoreCase("Yes"))
			switchSafewWaterActionOther.setChecked(true);
		else
			switchSafewWaterActionOther.setChecked(false);

		if(sharedPreferences.getString("waterNone", "").equalsIgnoreCase("Yes"))
			switchSafewWaterActionNothing.setChecked(true);
		else
			switchSafewWaterActionNothing.setChecked(false);

	}

	
	private void setDraftStatus(){
		
		sharedPreferences =  getSharedPreferences("householdInformation", 0);
		spEditor = sharedPreferences.edit();
		spEditor.putString("householdDraftStatus", "draft");
		spEditor.putString("householdDraftWhere",  Constants.HHSecondPage_DRAFT_WHERE);
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
				Intent i = new Intent(HHSecondPage.this, HHThirdPage.class);
				startActivity(i);
				finish();
			}

			break;
		case R.id.backButton:
			Intent i = new Intent(HHSecondPage.this, HHFirstPage.class);
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
		sharedPreferences =  getSharedPreferences("householdInformation", 0);
		spEditor = sharedPreferences.edit();
	    
		spEditor.putString("nameFamilyHead",  nameHead.getText().toString());
		spEditor.putString("mothersName",  mothersName.getText().toString());
		spEditor.putString("fathersName",  fathersName.getText().toString());
		spEditor.putString("mobile",  isMobile);
		spEditor.putString("mobileNo",  mobileNo.getText().toString());
		spEditor.putString("dob",  dob.getText().toString());
		spEditor.putString("age",  age.getText().toString());


		spEditor.putString("waterBoil",  ViewUtils.getSwitchValue(switchSafewWaterActionBoil));
		spEditor.putString("waterFilter",  ViewUtils.getSwitchValue(switchSafewWaterActionFilter));
		spEditor.putString("waterOther",  ViewUtils.getSwitchValue(switchSafewWaterActionOther));
		spEditor.putString("waterNone",  ViewUtils.getSwitchValue(switchSafewWaterActionNothing));

		spEditor.commit();	
	}
	
	private boolean validateData(){
		
		if(!FieldValidationUtils.validateEdittextValueForText(nameHead))
		{
			Toast.makeText(this, "Please enter name of Head of the family!", Toast.LENGTH_SHORT).show();
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
		else if(isMobile.equals(""))
		{
			Toast.makeText(this, "Please select whether there is any mobile no!", Toast.LENGTH_SHORT).show();
			return false;												
		}
		else if(isMobile.equals("Yes") && !FieldValidationUtils.validateEdittextValueForText(mobileNo))
		{
			Toast.makeText(this, "Please enter mobile no!", Toast.LENGTH_SHORT).show();
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
			progressDialog = new ProgressDialog(HHSecondPage.this);
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
        DatePickerDialog dialog = new DatePickerDialog(HHSecondPage.this,
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
            
            LocalDate birthdate = new LocalDate (mYear, (mMonth+1), mDay);
            LocalDate now = new LocalDate();
            Years ageYears = Years.yearsBetween(birthdate, now);
            
            age.setText(ageYears.getYears() + "");
            age.setEnabled(false);
        }
    }

	
	public void onSwitchClicked(View view) {
	    // Is the toggle on?
	    boolean on = ((Switch) view).isChecked();
	    
	    if (on) {
	    	isMobile = "Yes";
	    } else {
	    	isMobile = "No";
	    }
	    Log.d("<x_x>", isMobile);
	}

}

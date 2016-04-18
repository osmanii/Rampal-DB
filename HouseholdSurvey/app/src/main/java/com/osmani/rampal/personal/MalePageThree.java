package com.osmani.rampal.personal;

import org.json.JSONObject;

import com.osmani.location.LocationTrackerService;
import com.osmani.model.MalePersonalModel;
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

public class MalePageThree extends Activity implements OnClickListener{
	
	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor spEditor;
	private Button nextButton;
	private Button backButton;
	private Button draftButton;
	
	// switches
	Switch switchSmokingNow,switchSmokingRegular,switchBetelNut,switchCancerfamilyHistory,switchMalaria,switchMalariaTreatment,switchProductiveCough,switchProductiveCoughTreatment;
	
	EditText editextOtherMedicine,edittextSmokingStartingAge,edittextSmokingCount;
	private MaleHttpAsyncTask submittingTask;
	
	private boolean submitted = false;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pp_activity_male_page_two);
		
		setTitle("Male Page 3 ID:" + Utils.getPersonId(getApplicationContext()));
		
		setDraftStatus();
		initLocationService();
		
		switchSmokingNow = (Switch) findViewById(R.id.switchSmokingNow);
		edittextSmokingStartingAge = (EditText) findViewById(R.id.edittextSmokingStartingAge);
		edittextSmokingCount = (EditText) findViewById(R.id.edittextSmokingCount);
		switchSmokingRegular = (Switch) findViewById(R.id.switchSmokingRegular);

		switchBetelNut = (Switch) findViewById(R.id.switchBetelNut);
		switchCancerfamilyHistory = (Switch) findViewById(R.id.switchCancerfamilyHistory);
		switchMalaria = (Switch) findViewById(R.id.switchMalaria);
		switchMalariaTreatment = (Switch) findViewById(R.id.switchMalariaTreatment);
		switchProductiveCough = (Switch) findViewById(R.id.switchProductiveCough);
		switchProductiveCoughTreatment = (Switch) findViewById(R.id.switchProductiveCoughTreatment);
		
		editextOtherMedicine = (EditText) findViewById(R.id.editextOtherMedicine);
		
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
		if(sharedPreferences.getString("smokingNow", "").equals("")==false && sharedPreferences.getString("smokingNow", "").equalsIgnoreCase("null")==false)
		{
			if(sharedPreferences.getString("smokingNow", "").equalsIgnoreCase("Yes"))
				switchSmokingNow.setChecked(true);
			else switchSmokingNow.setChecked(false);
		}
        if(sharedPreferences.getString("smokingStartingAge", "").equals("")==false && sharedPreferences.getString("smokingStartingAge", "").equalsIgnoreCase("null")==false)
        {
            edittextSmokingStartingAge.setText(sharedPreferences.getString("smokingStartingAge", ""));
        }
        if(sharedPreferences.getString("smokingCount", "").equals("")==false && sharedPreferences.getString("smokingCount", "").equalsIgnoreCase("null")==false)
        {
            edittextSmokingCount.setText(sharedPreferences.getString("smokingCount", ""));
        }
        if(sharedPreferences.getString("isEverSmoking", "").equals("")==false && sharedPreferences.getString("isEverSmoking", "").equalsIgnoreCase("null")==false)
        {
            if(sharedPreferences.getString("isEverSmoking", "").equalsIgnoreCase("Yes"))
                switchSmokingRegular.setChecked(true);
            else switchSmokingRegular.setChecked(false);
        }
		if(sharedPreferences.getString("betelNutChewing", "").equals("")==false && sharedPreferences.getString("betelNutChewing", "").equalsIgnoreCase("null")==false)		
		{
			if(sharedPreferences.getString("betelNutChewing", "").equalsIgnoreCase("Yes"))
				switchBetelNut.setChecked(true);
			else switchBetelNut.setChecked(false);			
		}
		if(sharedPreferences.getString("familyHistoryCancer", "").equals("")==false && sharedPreferences.getString("familyHistoryCancer", "").equalsIgnoreCase("null")==false)		
		{
			if(sharedPreferences.getString("familyHistoryCancer", "").equalsIgnoreCase("Yes"))
				switchCancerfamilyHistory.setChecked(true);
			else switchCancerfamilyHistory.setChecked(false);			
		}
		if(sharedPreferences.getString("isLastYearClinicalMalaria", "").equals("")==false && sharedPreferences.getString("isLastYearClinicalMalaria", "").equalsIgnoreCase("null")==false)		
		{
			if(sharedPreferences.getString("isLastYearClinicalMalaria", "").equalsIgnoreCase("Yes"))
				switchMalaria.setChecked(true);
			else switchMalaria.setChecked(false);			
		}
		if(sharedPreferences.getString("treatedLastYearClinicalMalaria", "").equals("")==false && sharedPreferences.getString("treatedLastYearClinicalMalaria", "").equalsIgnoreCase("null")==false)		
		{
			if(sharedPreferences.getString("treatedLastYearClinicalMalaria", "").equalsIgnoreCase("Yes"))
				switchMalariaTreatment.setChecked(true);
			else switchMalariaTreatment.setChecked(false);			
		}
		if(sharedPreferences.getString("isProductiveCough", "").equals("")==false && sharedPreferences.getString("isProductiveCough", "").equalsIgnoreCase("null")==false)		
		{
			if(sharedPreferences.getString("isProductiveCough", "").equalsIgnoreCase("Yes"))
				switchProductiveCough.setChecked(true);
			else switchProductiveCough.setChecked(false);			
		}
		if(sharedPreferences.getString("treatedProductiveCough", "").equals("")==false && sharedPreferences.getString("treatedProductiveCough", "").equalsIgnoreCase("null")==false)		
		{
			if(sharedPreferences.getString("treatedProductiveCough", "").equalsIgnoreCase("Yes"))
				switchProductiveCoughTreatment.setChecked(true);
			else switchProductiveCoughTreatment.setChecked(false);			
		}
		if(sharedPreferences.getString("regularMedicines", "").equals("")==false && sharedPreferences.getString("regularMedicines", "").equalsIgnoreCase("null")==false)
		{
			editextOtherMedicine.setText(sharedPreferences.getString("regularMedicines", ""));
		}
	}

	
	private void setDraftStatus(){
		sharedPreferences =  getSharedPreferences("personalInformation", 0);
		spEditor = sharedPreferences.edit();
		spEditor.putString("personDraftStatus", "draft");
		spEditor.putString("personDraftWhere",  Constants.MalePageThree_DRAFT_WHERE);
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
							
				if(submittingTask==null){
					submitted = true;
					submittingTask= new MaleHttpAsyncTask();
					submittingTask.execute();
				}else{
					if(submittingTask.getStatus()==Status.RUNNING ||submittingTask.getStatus()== Status.PENDING ){
						Toast.makeText(getApplicationContext(), "Already submitting data", Toast.LENGTH_LONG).show();
					}else{
						submitted = true;
						submittingTask= new MaleHttpAsyncTask();
						submittingTask.execute();
					}
				}
				
				//Intent i = new Intent(MalePageOne.this, MalePageThree.class);
				//startActivity(i);
			}

			break;
		case R.id.backButton:
			Intent i = new Intent(MalePageThree.this, MalePageTwo.class);
			startActivity(i);
			finish();

			break;
		case R.id.draftButton:
			if(submittingTask==null){
				submitted = false;
				submittingTask= new MaleHttpAsyncTask();
				submittingTask.execute();
			}else{
				if(submittingTask.getStatus()==Status.RUNNING ||submittingTask.getStatus()== Status.PENDING ){
					Toast.makeText(getApplicationContext(), "Already submitting data", Toast.LENGTH_LONG).show();
				}else{
					submitted = false;
					submittingTask= new MaleHttpAsyncTask();
					submittingTask.execute();
				}
			}
			
			break;
		}	
	}
	
	private void initAddMoreDialog(boolean result){
		AlertDialog.Builder builder = new AlertDialog.Builder(MalePageThree.this);


    	if(result){
    		builder.setMessage("Do you want to add another person?")
			.setTitle("Success!");
    		builder.setNegativeButton("No,Thanks",new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Utils.clearAllHouseholdInfo(getApplicationContext());
					Utils.clearAllPersonalInfo(getApplicationContext());
					Intent intent = new Intent(MalePageThree.this,HHRootMenuActivity.class);
					startActivity(intent);
					finish();
				}
			});
    		builder.setPositiveButton("Yes,lets do.",new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Utils.clearAllPersonalInfo(getApplicationContext());
					Intent intent = new Intent(MalePageThree.this,PPRootMenuActivity.class);
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

	
	private void updateSharedPreferrence(){
		sharedPreferences =  getSharedPreferences("personalInformation", 0);
		spEditor = sharedPreferences.edit();
	    
		spEditor.putString("smokingNow",getSwitchValue(switchSmokingNow) );
        spEditor.putString("smokingStartingAge",  edittextSmokingStartingAge.getText().toString());
        spEditor.putString("smokingCount",  edittextSmokingCount.getText().toString());
        spEditor.putString("isEverSmoking",getSwitchValue(switchSmokingRegular) );

		spEditor.putString("betelNutChewing",  getSwitchValue(switchBetelNut));
		spEditor.putString("familyHistoryCancer", getSwitchValue(switchCancerfamilyHistory));
		spEditor.putString("isLastYearClinicalMalaria",  getSwitchValue(switchMalaria));
		spEditor.putString("treatedLastYearClinicalMalaria", getSwitchValue(switchMalariaTreatment) );
		spEditor.putString("isProductiveCough",  getSwitchValue(switchProductiveCough));
		spEditor.putString("treatedProductiveCough",  getSwitchValue(switchProductiveCoughTreatment));
		spEditor.putString("regularMedicines",  editextOtherMedicine.getText().toString());
		
		spEditor.putString("personDraftStatus", "finished");
		spEditor.putString("personDraftWhere",  "");		
		spEditor.commit();
	}
	private String getSwitchValue(Switch switchView){
		return (String)(switchView.isChecked()?switchView.getTextOn():switchView.getTextOff());
	}
	
	private boolean validateData(){
		
		if(!FieldValidationUtils.validateEdittextValueForText(editextOtherMedicine))
		{
			Toast.makeText(this, "Please enter regular medicines!", Toast.LENGTH_SHORT).show();
			return false;
		}

        if(switchSmokingNow.isChecked()){
            if(!FieldValidationUtils.validateEdittextValueForNumber(edittextSmokingStartingAge)){
                Toast.makeText(this, "Please enter smoking startinga age (years)", Toast.LENGTH_SHORT).show();
                return false;
            }
            if(!FieldValidationUtils.validateEdittextValueForNumber(edittextSmokingCount)){
                Toast.makeText(this, "Please enter number of smokes", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

		return true;			
		
	}
	
	private class MaleHttpAsyncTask extends AsyncTask<Void, Void, Boolean> {
		
		private MalePersonalModel model ;
		private String SERVER_POSTING_URL="http://www.ag-climatedata.net/ws_rhdp/update_male.php";
		// Progress Dialog
		private ProgressDialog progressDialog;
		
		      
		public MaleHttpAsyncTask() {
			super();
			this.model = Utils.getMalePersonalModelObject(getApplicationContext());
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(MalePageThree.this);
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

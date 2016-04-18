package com.osmani.rampal.household;

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
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.AvoidXfermode;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.AsyncTask.Status;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

public class HHFourthPage extends Activity implements OnClickListener, OnItemSelectedListener{

	SharedPreferences sharedPreferences;
	SharedPreferences.Editor spEditor;
	
	Spinner secondaryIncomeSource;
	Spinner drinkingWater;
	Spinner toiletFacility;
	Spinner numberOfHouses;
	
	String secondaryIncomeSource_Str = " ", drinkingWater_Str = " ", toiletFacility_Str = " ", numberOfHouses_Str = " ";

	EditText houses;

	int noOfHouses = 0;
	
	private Button nextButton;
	private Button backButton;
	private Button draftButton;
	

	private Spinner spinnerAvgSpend;
	private Switch switchRefrigerator;

	private Switch switchFishpond;
	private Switch switchShrimppond;

	private HouseholdHttpAsyncTask submittingTask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hh_activity_fourth_page);
		
		setTitle("Household Page 4 ID:" + Utils.getHouseHoldId(getApplicationContext()));

		setDraftStatus();
		initLocationService();
	    
		
		secondaryIncomeSource = (Spinner) findViewById(R.id.spinnerSecondaryIncome);		
		secondaryIncomeSource.setOnItemSelectedListener(this);
		
		numberOfHouses = (Spinner) findViewById(R.id.noOfHousesSpinner);	
		numberOfHouses.setOnItemSelectedListener(this);

		drinkingWater = (Spinner) findViewById(R.id.drinkingWaterSpinner);		
		drinkingWater.setOnItemSelectedListener(this);
		
		toiletFacility = (Spinner) findViewById(R.id.toiletFacilitySpinner);
		toiletFacility.setOnItemSelectedListener(this);
				
		houses = (EditText)findViewById(R.id.housesET);
		
		nextButton = (Button)findViewById(R.id.nextButton);
	    nextButton.setOnClickListener(this);
	    
	    backButton = (Button)findViewById(R.id.backButton);
	    backButton.setOnClickListener(this);
	    
	    draftButton = (Button)findViewById(R.id.draftButton);
	    draftButton.setOnClickListener(this);
	    
	    
	    spinnerAvgSpend = (Spinner) findViewById(R.id.spinnerAvgSpend);
	    switchRefrigerator = (Switch) findViewById(R.id.switchRefrigerator);

		switchFishpond = (Switch) findViewById(R.id.switchFishpond);
		switchShrimppond = (Switch) findViewById(R.id.switchShrimppond);
	}
	
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		sharedPreferences =  getSharedPreferences("householdInformation", 0);
		if(sharedPreferences.getString("secondaryIncome", "").equals("")==false && sharedPreferences.getString("secondaryIncome", "").equalsIgnoreCase("null")==false)
		{
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
			        R.array.incomeSource, android.R.layout.simple_spinner_item);
			secondaryIncomeSource.setSelection(adapter.getPosition(sharedPreferences.getString("secondaryIncome", ""))); 
		}
		if(sharedPreferences.getString("typeOfHouse", "").equals("")==false && sharedPreferences.getString("typeOfHouse", "").equalsIgnoreCase("null")==false)
		{
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
			        R.array.house, android.R.layout.simple_spinner_item);
			numberOfHouses.setSelection(adapter.getPosition(sharedPreferences.getString("typeOfHouse", ""))); 
		}

		if(sharedPreferences.getString("noOfHouses", "").equals("")==false && sharedPreferences.getString("noOfHouses", "").equalsIgnoreCase("null")==false)
		{
			houses.setText(sharedPreferences.getString("noOfHouses", ""));
		}
		if(sharedPreferences.getString("drinkingWater", "").equals("")==false && sharedPreferences.getString("drinkingWater", "").equalsIgnoreCase("null")==false)
		{
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
			        R.array.drinkingWaterSource, android.R.layout.simple_spinner_item);
			drinkingWater.setSelection(adapter.getPosition(sharedPreferences.getString("drinkingWater", ""))); 
		}
		if(sharedPreferences.getString("toiletFacility", "").equals("")==false && sharedPreferences.getString("toiletFacility", "").equalsIgnoreCase("null")==false)
		{
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
			        R.array.toiletFacility, android.R.layout.simple_spinner_item);
			toiletFacility.setSelection(adapter.getPosition(sharedPreferences.getString("toiletFacility", ""))); 
		}
		if(sharedPreferences.getString("avgHouseExpense", "").equals("")==false && sharedPreferences.getString("avgHouseExpense", "").equalsIgnoreCase("null")==false)
		{
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
			        R.array.avgSpendRange, android.R.layout.simple_spinner_item);
			spinnerAvgSpend.setSelection(adapter.getPosition(sharedPreferences.getString("avgHouseExpense", ""))); 
		}
		if(sharedPreferences.getString("hasRefrigerator", "").equals("")==false && sharedPreferences.getString("hasRefrigerator", "").equalsIgnoreCase("null")==false)		
		{
			if(sharedPreferences.getString("hasRefrigerator", "").equalsIgnoreCase("Yes"))
				switchRefrigerator.setChecked(true);
			else
				switchRefrigerator.setChecked(false);
		}
		if(sharedPreferences.getString("hasFishPond", "").equals("")==false && sharedPreferences.getString("hasFishPond", "").equalsIgnoreCase("null")==false)
		{
			if(sharedPreferences.getString("hasFishPond", "").equalsIgnoreCase("Yes"))
				switchFishpond.setChecked(true);
			else
				switchFishpond.setChecked(false);
		}
		if(sharedPreferences.getString("hasShrimpPond", "").equals("")==false && sharedPreferences.getString("hasShrimpPond", "").equalsIgnoreCase("null")==false)
		{
			if(sharedPreferences.getString("hasShrimpPond", "").equalsIgnoreCase("Yes"))
				switchShrimppond.setChecked(true);
			else
				switchShrimppond.setChecked(false);
		}
		
	}

	
	private void setDraftStatus(){
		
		sharedPreferences =  getSharedPreferences("householdInformation", 0);
		spEditor = sharedPreferences.edit();
		spEditor.putString("householdDraftStatus", "draft");
		spEditor.putString("householdDraftWhere",  Constants.HHFourthPage_DRAFT_WHERE);
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
				Intent i = new Intent(HHFourthPage.this, HHFifthPage.class);
				startActivity(i);
				finish();
			}

			break;
		case R.id.backButton:
			Intent i = new Intent(HHFourthPage.this, HHThirdPage.class);
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
	    
		spEditor.putString("secondaryIncome",  secondaryIncomeSource_Str);
		spEditor.putString("noOfHouses",  noOfHouses + "");
		spEditor.putString("typeOfHouse",  numberOfHouses_Str);
		spEditor.putString("drinkingWater",  drinkingWater_Str);
		spEditor.putString("toiletFacility",  toiletFacility_Str);
		spEditor.putString("avgHouseExpense",  ViewUtils.getSpinnerSelectedValue(spinnerAvgSpend));
		spEditor.putString("hasRefrigerator",  ViewUtils.getSwitchValue(switchRefrigerator));

		spEditor.putString("hasFishPond",  ViewUtils.getSwitchValue(switchFishpond));
		spEditor.putString("hasShrimpPond",  ViewUtils.getSwitchValue(switchShrimppond));

		spEditor.commit();	
	}
	
	private boolean validateData(){
		
		try 
		{
			if (FieldValidationUtils.validateEdittextValueForNumber(houses))
				noOfHouses = Integer.parseInt(houses.getText().toString());
		} 
		catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}

		if (!FieldValidationUtils.validateSpinnerValue(secondaryIncomeSource)) {
			Toast.makeText(this, "Please select secondary income source!",	Toast.LENGTH_SHORT).show();
			return false;
		} else if (!FieldValidationUtils.validateSpinnerValue(numberOfHouses)) {
			Toast.makeText(this, "Please select type of house!", Toast.LENGTH_SHORT).show();
			return false;
		} else if (!FieldValidationUtils.validateEdittextValueForNumber(houses)) {
			Toast.makeText(this, "Please enter number of houses!",	Toast.LENGTH_SHORT).show();
			return false;
		} else if (!FieldValidationUtils.validateSpinnerValue(drinkingWater)) {
			Toast.makeText(this, "Please select source of safe drinking water!", Toast.LENGTH_SHORT).show();
			return false;
		} else if (!FieldValidationUtils.validateSpinnerValue(toiletFacility)) {
			Toast.makeText(this, "Please select toilet facility!", Toast.LENGTH_SHORT).show();
			return false;
		} else if (!FieldValidationUtils.validateSpinnerValue(spinnerAvgSpend)) {
			Toast.makeText(this, "Please select household expense in an average month!", Toast.LENGTH_SHORT).show();
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
			progressDialog = new ProgressDialog(HHFourthPage.this);
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
		if(spinner.getId() == secondaryIncomeSource.getId())
		{
			secondaryIncomeSource_Str = spinner.getItemAtPosition(position).toString();
			Log.d("x_x",  secondaryIncomeSource_Str);
		}
		else if(spinner.getId() == numberOfHouses.getId())
		{
			numberOfHouses_Str = spinner.getItemAtPosition(position).toString();
			Log.d("x_x",  numberOfHouses_Str);
		}
		else if(spinner.getId() == drinkingWater.getId())
		{
			drinkingWater_Str = spinner.getItemAtPosition(position).toString();
			Log.d("x_x",  drinkingWater_Str);
		}
		else if(spinner.getId() == toiletFacility.getId())
		{
			toiletFacility_Str = spinner.getItemAtPosition(position).toString();
			Log.d("x_x",  toiletFacility_Str);
		}
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}

}

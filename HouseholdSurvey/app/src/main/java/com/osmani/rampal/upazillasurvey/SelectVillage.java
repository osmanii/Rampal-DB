package com.osmani.rampal.upazillasurvey;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.amadergram.rampal.survey.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.osmani.apimodels.NewHouseholdFeed;
import com.osmani.apimodels.VillageFeed;
import com.osmani.apimodels.VillageModel;
import com.osmani.rampal.household.HHFirstPage;
import com.osmani.utils.FieldValidationUtils;
import com.osmani.utils.UserUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class SelectVillage extends Activity implements OnClickListener {
	
	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor spEditor;
	private Button buttonOk;
	private Spinner spinnerVillage;
	
	private ArrayList<VillageModel> villageModelList = new ArrayList<VillageModel>();
	private ArrayList<String> villageNameList = new ArrayList<String>();
	private ArrayAdapter<String> spinnerArrayAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_village);
		buttonOk = (Button) findViewById(R.id.ok_village);
		buttonOk.setOnClickListener(this);
		
		spinnerVillage = (Spinner)findViewById(R.id.village_spinner);
		
		villageNameList.add("Please Select");
		
		new VillageListFetcher().execute();
		resetSpinner();
		
	}
	private void resetSpinner(){
		spinnerArrayAdapter = new ArrayAdapter<String>(
		         this, android.R.layout.simple_spinner_item, villageNameList);
		spinnerArrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
		spinnerVillage.setAdapter(spinnerArrayAdapter);
	}
	private void prepareDataForSpinner(){
		villageNameList.clear();
		villageNameList.add("Please Select");
		
		for(VillageModel temp : villageModelList){
			villageNameList.add(temp.villageName);
		}
		resetSpinner();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch (v.getId()) {
		case R.id.ok_village:
			
			if(FieldValidationUtils.validateSpinnerValue(spinnerVillage)){
				VillageModel model =  villageModelList.get(spinnerVillage.getSelectedItemPosition()-1);
				Toast.makeText(getApplicationContext(), model.villageId + "user id:"+UserUtils.getUserId(getApplicationContext()), Toast.LENGTH_SHORT).show();
				new HouseHoldCreatorTask(model.villageId).execute();
			}
			else
			{ 
				Toast.makeText(getApplicationContext(), "Please select a village name!", Toast.LENGTH_LONG).show();
			}
			
			break;

		default:
			break;
		}
		
	}
	
	 private class VillageListFetcher extends AsyncTask<Void, Void, VillageFeed> {
	        private  String TAG = "VillageListFetcher";
	        private  String SERVER_URL = "http://www.ag-climatedata.net/ws_rhdp/village.php?userId=###";
	        private ProgressDialog processDialog;


	        @Override
	        protected void onPreExecute() {	        	
	            super.onPreExecute();

	        	
	        	SERVER_URL = SERVER_URL.replace("###", UserUtils.getUserId(getApplicationContext()));
	            processDialog = new ProgressDialog(SelectVillage.this);
	            processDialog.setMessage("Fetching villages...");
	            processDialog.setIndeterminate(true);
	            processDialog.setCancelable(false);
	            processDialog.show();

	        }

	        @Override
	        protected VillageFeed doInBackground(Void... params) {
	            try {

	                HttpClient client = new DefaultHttpClient();
         
	                HttpGet get = new HttpGet(SERVER_URL);

	                //Perform the request and check the status code
	                HttpResponse response = client.execute(get);
	                StatusLine statusLine = response.getStatusLine();
	                if(statusLine.getStatusCode() == 200) {
	                    HttpEntity entity = response.getEntity();
	                    InputStream content = entity.getContent();

	                    if(isCancelled()){
	                        return null;
	                    }
	                    try {
	                        //Read the server response and attempt to parse it as JSON
	                        Reader reader = new InputStreamReader(content);

	                        GsonBuilder gsonBuilder = new GsonBuilder();

	                        Gson gson = gsonBuilder.create();
	                        VillageFeed temp;

	                        temp = gson.fromJson(reader, VillageFeed.class);
	                        content.close();
	                        return temp;
	                    } catch (Exception ex) {
	                        Log.e(TAG, TAG + ":Failed to parse JSON due to: " + ex);

	                        return null;
	                    }
	                } else {
	                    Log.e(TAG, TAG+"Server responded with status code: " + statusLine.getStatusCode());
	                    return null;
	                }
	            } catch(Exception ex) {
	                ex.printStackTrace();
	                Log.e(TAG, TAG+":Failed to send HTTP GET request due to: " + ex.getCause());

	                return null;
	            }

	        }

	        @Override
	        protected void onPostExecute(VillageFeed feed) {
	        	
	        	if(processDialog!=null && processDialog.isShowing()){
	        		processDialog.dismiss();
	        	}


	            if(feed==null || isCancelled()){

	                Toast.makeText(getApplicationContext(),"Internet Connection not available",Toast.LENGTH_LONG).show();

	            }else{

	                if(feed.success){	                	
	                	villageModelList.clear();
	                	villageModelList.addAll(feed.posts);
	                	prepareDataForSpinner();
	                }else{
	                    Toast.makeText(getApplicationContext(),"Failed to load Village List from server.",Toast.LENGTH_LONG).show();

	                    Log.e(TAG, "Failed to load Village List from server.");
	                }

	            }
	          
	        }

	    }

	 
	 private class HouseHoldCreatorTask extends AsyncTask<Void, Void, NewHouseholdFeed> {
	        private  String TAG = "HouseHoldCreator";
	        private  String SERVER_URL = "http://www.ag-climatedata.net/ws_rhdp/new_household.php?userId=###&villageId=$$$";
	        private ProgressDialog processDialog;
	        
	        public HouseHoldCreatorTask(String villageId){
	        	SERVER_URL = SERVER_URL.replace("###", UserUtils.getUserId(getApplicationContext()));
	        	SERVER_URL = SERVER_URL.replace("$$$", villageId);
	        }


	        @Override
	        protected void onPreExecute() {	        	
	            super.onPreExecute();

	            processDialog = new ProgressDialog(SelectVillage.this);
	            processDialog.setMessage("Creating new household");
	            processDialog.setIndeterminate(true);
	            processDialog.setCancelable(false);
	            processDialog.show();

	        }

	        @Override
	        protected NewHouseholdFeed doInBackground(Void... params) {
	            try {

	                HttpClient client = new DefaultHttpClient();
      
	                HttpGet get = new HttpGet(SERVER_URL);

	                //Perform the request and check the status code
	                HttpResponse response = client.execute(get);
	                StatusLine statusLine = response.getStatusLine();
	                if(statusLine.getStatusCode() == 200) {
	                    HttpEntity entity = response.getEntity();
	                    InputStream content = entity.getContent();

	                    if(isCancelled()){
	                        return null;
	                    }
	                    try {
	                        //Read the server response and attempt to parse it as JSON
	                        Reader reader = new InputStreamReader(content);

	                        GsonBuilder gsonBuilder = new GsonBuilder();

	                        Gson gson = gsonBuilder.create();
	                        NewHouseholdFeed temp;

	                        temp = gson.fromJson(reader, NewHouseholdFeed.class);
	                        content.close();
	                        return temp;
	                    } catch (Exception ex) {
	                        Log.e(TAG, TAG + ":Failed to parse JSON due to: " + ex);

	                        return null;
	                    }
	                } else {
	                    Log.e(TAG, TAG+"Server responded with status code: " + statusLine.getStatusCode());
	                    return null;
	                }
	            } catch(Exception ex) {
	                ex.printStackTrace();
	                Log.e(TAG, TAG+":Failed to send HTTP GET request due to: " + ex.getCause());

	                return null;
	            }

	        }

	        @Override
	        protected void onPostExecute(NewHouseholdFeed feed) {
	        	
	        	if(processDialog!=null && processDialog.isShowing()){
	        		processDialog.dismiss();
	        	}


	            if(feed==null || isCancelled()){

	                Toast.makeText(getApplicationContext(),"Internet Connection not available",Toast.LENGTH_LONG).show();

	            }else{

	                if(feed.success){	                	
	                	Toast.makeText(getApplicationContext(),"new household id:"+feed.posts,Toast.LENGTH_SHORT).show();
	                	Intent i = new Intent(SelectVillage.this, HHFirstPage.class);

	                	sharedPreferences =  getSharedPreferences("householdInformation", 0);
	            		spEditor = sharedPreferences.edit();
	            	    
	            		spEditor.putString("householdId",  feed.posts);
	            		spEditor.commit();
	                	
	                	i.putExtra("householdId", feed.posts);
	        			startActivity(i);
	                }else{
	                    Toast.makeText(getApplicationContext(),"Failed to create new household. Please contact us.",Toast.LENGTH_LONG).show();

	                    Log.e(TAG, "Failed to create new household. Please contact us");
	                }

	            }
	          
	        }

	    }

}

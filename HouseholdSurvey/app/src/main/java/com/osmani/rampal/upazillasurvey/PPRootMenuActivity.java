package com.osmani.rampal.upazillasurvey;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

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
import com.osmani.apimodels.NewPersonFeed;
import com.osmani.rampal.household.HHFirstPage;
import com.osmani.rampal.household.HHRootMenuActivity;
import com.osmani.rampal.personal.PPFirstPage;
import com.osmani.utils.UserUtils;
import com.osmani.utils.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class PPRootMenuActivity extends Activity implements OnClickListener {
	
	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor spEditor;

	private Button buttonNewEntry; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pp_activity_root_menu);
		
		setTitle("Personal Survey ");

		buttonNewEntry = (Button) findViewById(R.id.buttonNewEntry);
		buttonNewEntry.setOnClickListener(this);
		
		Utils.resetMalePersonalModelObject(getApplicationContext());
		Utils.resetFemalePersonalModelObject(getApplicationContext());	
		
		Utils.clearAllPersonalInfo(getApplicationContext());
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch (v.getId()) {
		case R.id.buttonNewEntry:
			
			String houseHoldId = Utils.getHouseHoldId(getApplicationContext());
			if(houseHoldId!=null){
				new NewPersonCreatorTask(houseHoldId).execute();
			}else{
				Toast.makeText(getApplicationContext(), "Houeshold id not found, please contact us", Toast.LENGTH_SHORT).show();
			}

			break;

		default:
			break;
		}
		
	}
	
	 private class NewPersonCreatorTask extends AsyncTask<Void, Void, NewPersonFeed> {
	        private  String TAG = "NewPersonCreatorTask";
	        private  String SERVER_URL = "http://www.ag-climatedata.net/ws_rhdp/new_person.php?userId=###&householdId=$$$";
	        private ProgressDialog processDialog;
	        
	        public NewPersonCreatorTask(String householdId){
	        	SERVER_URL = SERVER_URL.replace("###", UserUtils.getUserId(getApplicationContext()));
	        	SERVER_URL = SERVER_URL.replace("$$$", householdId);
	        }


	        @Override
	        protected void onPreExecute() {	        	
	            super.onPreExecute();

	            processDialog = new ProgressDialog(PPRootMenuActivity.this);
	            processDialog.setMessage("Creating new Person");
	            processDialog.setIndeterminate(true);
	            processDialog.setCancelable(false);
	            processDialog.show();

	        }

	        @Override
	        protected NewPersonFeed doInBackground(Void... params) {
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
	                        NewPersonFeed temp;

	                        temp = gson.fromJson(reader, NewPersonFeed.class);
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
	        protected void onPostExecute(NewPersonFeed feed) {
	        	
	        	if(processDialog!=null && processDialog.isShowing()){
	        		processDialog.dismiss();
	        	}


	            if(feed==null || isCancelled()){

	                Toast.makeText(getApplicationContext(),"Internet Connection not available",Toast.LENGTH_LONG).show();

	            }else{

	                if(feed.success){	                	
	                	Toast.makeText(getApplicationContext(),"new person id:"+feed.posts,Toast.LENGTH_SHORT).show();
	        			Intent i = new Intent(PPRootMenuActivity.this, PPFirstPage.class);

	        			sharedPreferences =  getSharedPreferences("personalInformation", 0);
	        			spEditor = sharedPreferences.edit();
	        		    
	        			spEditor.putString("personId",  feed.posts);
	        			
	        			spEditor.commit();
	        			
	        			i.putExtra("personalId", feed.posts);
	        			startActivity(i);
	        			finish();
	                }else{
	                    Toast.makeText(getApplicationContext(),"Failed to create new person id. Please try again.",Toast.LENGTH_LONG).show();

	                    Log.e(TAG, "Failed to load Village List from server.");
	                }

	            }
	          
	        }

	    }

}
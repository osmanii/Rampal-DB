package com.osmani.rampal.household;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.osmani.apimodels.RetreiveFemaleFeed;
import com.osmani.apimodels.RetreiveHouseholdFeed;
import com.osmani.apimodels.RetreiveMaleFeed;
import com.osmani.apimodels.RetreiveSurveyFeed;
import com.osmani.rampal.personal.FemalePageOne;
import com.osmani.rampal.personal.FemalePageFour;
import com.osmani.rampal.personal.FemalePageThree;
import com.osmani.rampal.personal.FemalePagetwo;
import com.osmani.rampal.personal.MalePageOne;
import com.osmani.rampal.personal.MalePageThree;
import com.osmani.rampal.personal.MalePageTwo;
import com.osmani.rampal.personal.PPFifthPage;
import com.osmani.rampal.personal.PPFirstPage;
import com.osmani.rampal.personal.PPFourthPage;
import com.osmani.rampal.personal.PPSecondPage;
import com.osmani.rampal.personal.PPThirdPage;
import com.osmani.rampal.upazillasurvey.PPRootMenuActivity;
import com.amadergram.rampal.survey.R;
import com.osmani.rampal.upazillasurvey.SelectVillage;
import com.osmani.utils.Constants;
import com.osmani.utils.UserUtils;
import com.osmani.utils.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class HHRootMenuActivity extends Activity implements OnClickListener {
	
	
	private Button buttonNewEntry;
	private Button buttonRetrieve;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hh_activity_root_menu);
		buttonNewEntry = (Button) findViewById(R.id.buttonNewEntry);
		buttonNewEntry.setOnClickListener(this);
		
		buttonRetrieve = (Button) findViewById(R.id.buttonRetrieve);
		buttonRetrieve.setOnClickListener(this);
		
		Utils.clearAllHouseholdInfo(getApplicationContext());		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch (v.getId()) {
		case R.id.buttonNewEntry:
			Intent i = new Intent(HHRootMenuActivity.this, SelectVillage.class);
			startActivity(i);
			break;
		case R.id.buttonRetrieve:
			showInputDialog();
			break;
		default:
			break;
		}
		
	}
	
	private void showInputDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Retrieve Household");
        builder.setMessage("Type the household id:");
 
         // Use an EditText view to get user input.
         final EditText input = new EditText(this);
         builder.setView(input);
 
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
 
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                Log.d("Rampal", "household id: " + value);
                
                if(!value.trim().isEmpty()){
                	RetreiveSurveyFetcher retreiveSurveyFetcher = new RetreiveSurveyFetcher(value);
                    retreiveSurveyFetcher.execute();
                }
                
                return;
            }
        });
 
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
 
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        builder.setCancelable(false);
        AlertDialog dialog= builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
	}
	
	private class RetreiveSurveyFetcher extends AsyncTask<Void, Void, RetreiveSurveyFeed>{
		
		private String SERVER_URL= "http://www.ag-climatedata.net/ws_rhdp/retrieve_survey.php";
		private ProgressDialog progressDialog;
		private  String TAG = "RetreiveSurveyFetcher";
		private String inputHouseholdId;
		
		public RetreiveSurveyFetcher(String inputHouseholdId) {
			// TODO Auto-generated constructor stub
			this.inputHouseholdId = inputHouseholdId;
			SERVER_URL = SERVER_URL+"/?userId="+UserUtils.getUserId(getApplicationContext())+"&householdId="+inputHouseholdId;
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog = new ProgressDialog(HHRootMenuActivity.this);
			progressDialog.setMessage("Retrieiving data from server...");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected RetreiveSurveyFeed doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			try {
                //Create an HTTP client
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
                        RetreiveSurveyFeed feed;

                        feed = gson.fromJson(reader,RetreiveSurveyFeed.class);
                        content.close();
                       
                        return feed;
                    } catch (Exception ex) {
                        Log.e(TAG, "RetreiveSurveyFetcher to parse JSON due to: " + ex);

                        return null;
                    }
                } else {
                    Log.e(TAG, "RetreiveSurveyFetcher responded with status code: " + statusLine.getStatusCode());
                    return null;
                }
            } catch(Exception ex) {
                ex.printStackTrace();
                Log.e(TAG, "RetreiveSurveyFetcher:Failed to send HTTP POST request due to: " + ex.getCause());

                return null;
            }
		}
		@Override
		protected void onPostExecute(RetreiveSurveyFeed resultFeed) {
			// TODO Auto-generated method stub
			super.onPostExecute(resultFeed);
			if(progressDialog!=null && progressDialog.isShowing()){
				progressDialog.dismiss();
			}
			
			if(resultFeed==null || isCancelled()){				
                Toast.makeText(getApplicationContext(),"Internet Connection Problem",Toast.LENGTH_SHORT).show();
                Log.e(TAG, "RetreiveSurveyFetcher:Failed to load Posts. Have a look at LogCat.");         

            }
			if(resultFeed.success==false){
				Toast.makeText(getApplicationContext(), resultFeed.message, Toast.LENGTH_LONG).show();
			}			
			else{
				if(resultFeed.module!=null && resultFeed.module.isEmpty()){
					// No draft available, so lets take him to PpRootMenuActivity
					SharedPreferences sharedPreferences =  getSharedPreferences(Constants.SHAREDPREFERRENCE_TAG_HOUSEHOLD, 0);
				     SharedPreferences.Editor spEditor= sharedPreferences.edit();    
				        
				     spEditor.putString("householdId",  inputHouseholdId);
				     spEditor.commit();
				     Intent intent = new Intent(HHRootMenuActivity.this,PPRootMenuActivity.class);
				     startActivity(intent);
				     finish();
				}else if(resultFeed.module!=null && resultFeed.module.compareToIgnoreCase("household")==0){
					// Need to fetch household details
					RetreiveHouseholdFetcher householdFetcher = new RetreiveHouseholdFetcher(inputHouseholdId);
					householdFetcher.execute();
					
				}else if(resultFeed.module!=null && resultFeed.module.compareToIgnoreCase("personal")==0){
					// Need to fetch person details
					SharedPreferences sharedPreferences =  getSharedPreferences(Constants.SHAREDPREFERRENCE_TAG_HOUSEHOLD, 0);
				     SharedPreferences.Editor spEditor= sharedPreferences.edit();    
				        
				     spEditor.putString("householdId",  inputHouseholdId);
				     spEditor.commit();
				     
					if(resultFeed.message.compareToIgnoreCase("male")==0){
						RetreiveMaleFetcher maleFetcher = new RetreiveMaleFetcher(resultFeed.personId);
						maleFetcher.execute();
					}else{
						RetreiveFemaleFetcher femaleFetcher = new RetreiveFemaleFetcher(resultFeed.personId);
						femaleFetcher.execute();
					}
				}else{
					Toast.makeText(getApplicationContext(), "There is something wrong, please contact suppport team", Toast.LENGTH_LONG).show();
				}
			}
			
		}
		
	}
	
	private class RetreiveHouseholdFetcher extends AsyncTask<Void, Void, RetreiveHouseholdFeed>{
		
		private String SERVER_URL= "http://www.ag-climatedata.net/ws_rhdp/retrieve_household.php";
		private ProgressDialog progressDialog;
		private  String TAG = "RetreiveHouseholdFetcher";
		
		public RetreiveHouseholdFetcher(String inputHouseholdId) {
			// TODO Auto-generated constructor stub
			
			SERVER_URL = SERVER_URL+"/?userId="+UserUtils.getUserId(getApplicationContext())+"&householdId="+inputHouseholdId;
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog = new ProgressDialog(HHRootMenuActivity.this);
			progressDialog.setMessage("Draft saved for household. Retreiving previous data from server...");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected RetreiveHouseholdFeed doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			try {
                //Create an HTTP client
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
                        RetreiveHouseholdFeed feed;

                        feed = gson.fromJson(reader,RetreiveHouseholdFeed.class);
                        content.close();
                       
                        return feed;
                    } catch (Exception ex) {
                        Log.e(TAG, "RetreiveHouseholdFetcher to parse JSON due to: " + ex);

                        return null;
                    }
                } else {
                    Log.e(TAG, "RetreiveHouseholdFetcher responded with status code: " + statusLine.getStatusCode());
                    return null;
                }
            } catch(Exception ex) {
                ex.printStackTrace();
                Log.e(TAG, "RetreiveHouseholdFetcher: Failed to send HTTP POST request due to: " + ex.getCause());

                return null;
            }
		}
		@Override
		protected void onPostExecute(RetreiveHouseholdFeed resultFeed) {
			// TODO Auto-generated method stub
			super.onPostExecute(resultFeed);
			if(progressDialog!=null && progressDialog.isShowing()){
				progressDialog.dismiss();
			}
			
			if(resultFeed==null || isCancelled()){				
                Toast.makeText(getApplicationContext(),"Internet Connection Problem",Toast.LENGTH_SHORT).show();
                Log.e(TAG, "RetreiveSurveyFetcher:Failed to load Posts. Have a look at LogCat.");         

            }
			if(resultFeed.success==false){
				Toast.makeText(getApplicationContext(), resultFeed.message, Toast.LENGTH_LONG).show();
			}else{
				// Need to save into sharedpreferrence and navigate to corresponding activity
				
				//saving part
				Utils.updateSharedPreferencesFromModel(getApplicationContext(), resultFeed.posts);
				
				// navigating part
				Intent intent=null;
				String draftWhere =resultFeed.posts.householdDraftWhere;
				if(draftWhere.compareToIgnoreCase(Constants.HHFirstPage_DRAFT_WHERE)==0){
					intent = new Intent(HHRootMenuActivity.this,HHFirstPage.class);
				}else if(draftWhere.compareToIgnoreCase(Constants.HHSecondPage_DRAFT_WHERE)==0){
					intent = new Intent(HHRootMenuActivity.this,HHSecondPage.class);
				}else if(draftWhere.compareToIgnoreCase(Constants.HHThirdPage_DRAFT_WHERE)==0){
					intent = new Intent(HHRootMenuActivity.this,HHThirdPage.class);
				}else if(draftWhere.compareToIgnoreCase(Constants.HHFourthPage_DRAFT_WHERE)==0){
					intent = new Intent(HHRootMenuActivity.this,HHFourthPage.class);
				}else if(draftWhere.compareToIgnoreCase(Constants.HHFifthPage_DRAFT_WHERE)==0){
					intent = new Intent(HHRootMenuActivity.this,HHFifthPage.class);
				}else{
					Toast.makeText(getApplicationContext(), "Draft position unknown or invalid. Please contact support team", Toast.LENGTH_LONG).show();
				}
				
				if(intent!=null){
					startActivity(intent);
				}
				
			}			
		}
		
	}
	
	private class RetreiveMaleFetcher extends AsyncTask<Void, Void, RetreiveMaleFeed>{
		
		private String SERVER_URL= "http://www.ag-climatedata.net/ws_rhdp/retrieve_person.php";
		private ProgressDialog progressDialog;
		private  String TAG = "RetreiveMaleFetcher";

		
		public RetreiveMaleFetcher(String inputPersonId) {
			// TODO Auto-generated constructor stub

			SERVER_URL = SERVER_URL+"/?userId="+UserUtils.getUserId(getApplicationContext())+"&personId="+inputPersonId;
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog = new ProgressDialog(HHRootMenuActivity.this);
			progressDialog.setMessage("Draft saved for Male Person. Retreiving previous data from server...");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected RetreiveMaleFeed doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			try {
                //Create an HTTP client
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
                        RetreiveMaleFeed feed;

                        feed = gson.fromJson(reader,RetreiveMaleFeed.class);
                        content.close();
                       
                        return feed;
                    } catch (Exception ex) {
                        Log.e(TAG, "RetreiveMaleFetcher: failed to parse JSON due to: " + ex);

                        return null;
                    }
                } else {
                    Log.e(TAG, "RetreiveMaleFetcher: Server responded with status code: " + statusLine.getStatusCode());
                    return null;
                }
            } catch(Exception ex) {
                ex.printStackTrace();
                Log.e(TAG, "RetreiveMaleFetcher: Failed to send HTTP POST request due to: " + ex.getCause());

                return null;
            }
		}
		@Override
		protected void onPostExecute(RetreiveMaleFeed resultFeed) {
			// TODO Auto-generated method stub
			super.onPostExecute(resultFeed);
			if(progressDialog!=null && progressDialog.isShowing()){
				progressDialog.dismiss();
			}
			
			if(resultFeed==null || isCancelled()){				
                Toast.makeText(getApplicationContext(),"Internet Connection Problem",Toast.LENGTH_SHORT).show();
                Log.e(TAG, "RetreiveMaleFetcher: Failed to load Posts. Have a look at LogCat.");         

            }
			if(resultFeed.success==false){
				Toast.makeText(getApplicationContext(), resultFeed.message, Toast.LENGTH_LONG).show();
			}else{
				// Need to save into sharedpreferrence and navigate to corresponding activity
				
				//saving part
				Utils.updateSharedPreferencesFromModel(getApplicationContext(), resultFeed.posts);
				

				// navigating part
				Intent intent=null;
				String draftWhere =resultFeed.posts.personDraftWhere;
				if(draftWhere.compareToIgnoreCase(Constants.PPFirstPage_DRAFT_WHERE)==0){
					intent = new Intent(HHRootMenuActivity.this,PPFirstPage.class);
				}else if(draftWhere.compareToIgnoreCase(Constants.PPSecondPage_DRAFT_WHERE)==0){
					intent = new Intent(HHRootMenuActivity.this,PPSecondPage.class);
				}else if(draftWhere.compareToIgnoreCase(Constants.PPThirdPage_DRAFT_WHERE)==0){
					intent = new Intent(HHRootMenuActivity.this,PPThirdPage.class);
				}else if(draftWhere.compareToIgnoreCase(Constants.PPFourthPage_DRAFT_WHERE)==0){
					intent = new Intent(HHRootMenuActivity.this,PPFourthPage.class);
				}else if(draftWhere.compareToIgnoreCase(Constants.PPFifthPage_DRAFT_WHERE)==0){
					intent = new Intent(HHRootMenuActivity.this,PPFifthPage.class);
				}else if(draftWhere.compareToIgnoreCase(Constants.MalePageOne_DRAFT_WHERE)==0){
					intent = new Intent(HHRootMenuActivity.this,MalePageOne.class);
				}else if(draftWhere.compareToIgnoreCase(Constants.MalePageTwo_DRAFT_WHERE)==0){
					intent = new Intent(HHRootMenuActivity.this,MalePageTwo.class);
				}else if(draftWhere.compareToIgnoreCase(Constants.MalePageThree_DRAFT_WHERE)==0){
					intent = new Intent(HHRootMenuActivity.this,MalePageThree.class);
				}else if(draftWhere.compareToIgnoreCase(Constants.FemalePageOne_DRAFT_WHERE)==0){
					intent = new Intent(HHRootMenuActivity.this,FemalePageOne.class);
				}else if(draftWhere.compareToIgnoreCase(Constants.FemalePageTwo_DRAFT_WHERE)==0){
					intent = new Intent(HHRootMenuActivity.this,FemalePagetwo.class);
				}else if(draftWhere.compareToIgnoreCase(Constants.FemalePageThree_DRAFT_WHERE)==0){
					intent = new Intent(HHRootMenuActivity.this,FemalePageThree.class);
				}else if(draftWhere.compareToIgnoreCase(Constants.FemalePageFour_DRAFT_WHERE)==0){
					intent = new Intent(HHRootMenuActivity.this,FemalePageFour.class);
				}else{
					Toast.makeText(getApplicationContext(), "Draft position unknown or invalid. Please contact support team", Toast.LENGTH_LONG).show();
				}
				
				if(intent!=null){
					startActivity(intent);
				}
			}			
		}
		
	}


	private class RetreiveFemaleFetcher extends AsyncTask<Void, Void, RetreiveFemaleFeed>{
		
		private String SERVER_URL= "http://www.ag-climatedata.net/ws_rhdp/retrieve_person.php";
		private ProgressDialog progressDialog;
		private  String TAG = "RetreiveFemaleFetcher";

		
		public RetreiveFemaleFetcher(String inputPersonId) {
			// TODO Auto-generated constructor stub

			SERVER_URL = SERVER_URL+"/?userId="+UserUtils.getUserId(getApplicationContext())+"&personId="+inputPersonId;
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog = new ProgressDialog(HHRootMenuActivity.this);
			progressDialog.setMessage("Draft saved for Female Person. Retreiving previous data from server...");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected RetreiveFemaleFeed doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			try {
                //Create an HTTP client
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
                        RetreiveFemaleFeed feed;

                        feed = gson.fromJson(reader,RetreiveFemaleFeed.class);
                        content.close();
                       
                        return feed;
                    } catch (Exception ex) {
                        Log.e(TAG, "RetreiveFemaleFetcher: failed to parse JSON due to: " + ex);

                        return null;
                    }
                } else {
                    Log.e(TAG, "RetreiveFemaleFetcher: Server responded with status code: " + statusLine.getStatusCode());
                    return null;
                }
            } catch(Exception ex) {
                ex.printStackTrace();
                Log.e(TAG, "RetreiveFemaleFetcher: Failed to send HTTP POST request due to: " + ex.getCause());

                return null;
            }
		}
		@Override
		protected void onPostExecute(RetreiveFemaleFeed resultFeed) {
			// TODO Auto-generated method stub
			super.onPostExecute(resultFeed);
			if(progressDialog!=null && progressDialog.isShowing()){
				progressDialog.dismiss();
			}
			
			if(resultFeed==null || isCancelled()){				
                Toast.makeText(getApplicationContext(),"Internet Connection Problem",Toast.LENGTH_SHORT).show();
                Log.e(TAG, "RetreiveFemaleFetcher: Failed to load Posts. Have a look at LogCat.");         

            }
			if(resultFeed.success==false){
				Toast.makeText(getApplicationContext(), resultFeed.message, Toast.LENGTH_LONG).show();
			}else{
				// Need to save into sharedpreferrence and navigate to corresponding activity
				//saving part
				Utils.updateSharedPreferencesFromModel(getApplicationContext(), resultFeed.posts);								

				// navigating part
				Intent intent=null;
				String draftWhere =resultFeed.posts.personDraftWhere;
				if(draftWhere.compareToIgnoreCase(Constants.PPFirstPage_DRAFT_WHERE)==0){
					intent = new Intent(HHRootMenuActivity.this,PPFirstPage.class);
				}else if(draftWhere.compareToIgnoreCase(Constants.PPSecondPage_DRAFT_WHERE)==0){
					intent = new Intent(HHRootMenuActivity.this,PPSecondPage.class);
				}else if(draftWhere.compareToIgnoreCase(Constants.PPThirdPage_DRAFT_WHERE)==0){
					intent = new Intent(HHRootMenuActivity.this,PPThirdPage.class);
				}else if(draftWhere.compareToIgnoreCase(Constants.PPFourthPage_DRAFT_WHERE)==0){
					intent = new Intent(HHRootMenuActivity.this,PPFourthPage.class);
				}else if(draftWhere.compareToIgnoreCase(Constants.PPFifthPage_DRAFT_WHERE)==0){
					intent = new Intent(HHRootMenuActivity.this,PPFifthPage.class);
				}else if(draftWhere.compareToIgnoreCase(Constants.MalePageOne_DRAFT_WHERE)==0){
					intent = new Intent(HHRootMenuActivity.this,MalePageOne.class);
				}else if(draftWhere.compareToIgnoreCase(Constants.MalePageTwo_DRAFT_WHERE)==0){
					intent = new Intent(HHRootMenuActivity.this,MalePageTwo.class);
				}else if(draftWhere.compareToIgnoreCase(Constants.MalePageThree_DRAFT_WHERE)==0){
					intent = new Intent(HHRootMenuActivity.this,MalePageThree.class);
				}else if(draftWhere.compareToIgnoreCase(Constants.FemalePageOne_DRAFT_WHERE)==0){
					intent = new Intent(HHRootMenuActivity.this,FemalePageOne.class);
				}else if(draftWhere.compareToIgnoreCase(Constants.FemalePageTwo_DRAFT_WHERE)==0){
					intent = new Intent(HHRootMenuActivity.this,FemalePagetwo.class);
				}else if(draftWhere.compareToIgnoreCase(Constants.FemalePageThree_DRAFT_WHERE)==0){
					intent = new Intent(HHRootMenuActivity.this,FemalePageThree.class);
				}else if(draftWhere.compareToIgnoreCase(Constants.FemalePageFour_DRAFT_WHERE)==0){
					intent = new Intent(HHRootMenuActivity.this,FemalePageFour.class);
				}else{
					Toast.makeText(getApplicationContext(), "Draft position unknown or invalid. Please contact support team", Toast.LENGTH_LONG).show();
				}
				
				if(intent!=null){
					startActivity(intent);
				}
				
			}			
		}
		
	}


}

package com.osmani.utils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.osmani.model.CommonPersonalModel;
import com.osmani.model.FemalePersonalModel;
import com.osmani.model.HouseholdModel;
import com.osmani.model.MalePersonalModel;

public class Utils {
	
	public static void clearAllPersonalInfo(Context appContext){
		SharedPreferences sharedPreferences = appContext.getSharedPreferences(Constants.SHAREDPREFERRENCE_TAG_PERSONAL, 0);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.clear();
		editor.commit();
	}
	public static void clearAllHouseholdInfo(Context appContext){
		SharedPreferences sharedPreferences = appContext.getSharedPreferences("householdInformation", 0);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.clear();
		editor.commit();
	}
	
	public static boolean hasActiveInternetConnection(Context context) {
	    if (isNetworkAvailable(context)) {
	        try {
	            HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
	            urlc.setRequestProperty("User-Agent", "Test");
	            urlc.setRequestProperty("Connection", "close");
	            urlc.setConnectTimeout(1500); 
	            urlc.connect();
	            return (urlc.getResponseCode() == 200);
	        } catch (IOException e) {
	            Log.e("<x_x>", "Error checking internet connection", e);
	        }
	    } else {
	        Log.d("<x_x>", "No network available!");
	    }
	    return false;
	}
	
	public static boolean isNetworkAvailable(Context context) 
	{
	    ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    if (connectivity == null) 
	    {
	      return false;
	    }
	    else
	    {
	      NetworkInfo[] info = connectivity.getAllNetworkInfo();
	      if (info != null) 
	      {
	        for (int i = 0; i < info.length; i++) 
	        {
	          if (info[i].getState() == NetworkInfo.State.CONNECTED) 
	          {
	            return true;
	          }
	        }
	      }
	    }
	    return false;
	  }

	
	/**
	 * 
	 * @param appContext
	 * @return householdId, "" if there none.
	 */
	public static String getHouseHoldId(Context appContext){
		SharedPreferences sharedPreferences = appContext.getSharedPreferences("householdInformation", 0);
		
		return sharedPreferences.getString("householdId",  "");
	}
	
	public static String getPersonId(Context appContext){
		SharedPreferences sharedPreferences = appContext.getSharedPreferences("personalInformation", 0);
		
		return sharedPreferences.getString("personId",  "");
	}
	
	public static HouseholdModel getHouseholdModelObject(Context appContext){
		
		HouseholdModel model = new HouseholdModel();
		
		SharedPreferences sharedPreferences = appContext.getSharedPreferences("householdInformation", 0);
		
		//firstPage
		model.householdId = sharedPreferences.getString("householdId",  "");
		model.latitude = sharedPreferences.getString("latitude",  "");
		model.longitude = sharedPreferences.getString("longitude",  "");
		model.electricity = sharedPreferences.getString("electricity",  "");
		model. safeWaterMethod= sharedPreferences.getString("safeWaterMethod", "");
		model.religion = sharedPreferences.getString("religion",  "");
		model.hasLivestock = sharedPreferences.getString("hasLivestock", "");
		
		
		model.hasBuffaloes = sharedPreferences.getString("hasBuffaloes",  "");
		model.hasCows = sharedPreferences.getString("hasCows",  "");
		model.hasGoats = sharedPreferences.getString("hasGoats",  "");
		model.hasSheeps = sharedPreferences.getString("hasSheeps",  "");
		model.hasChickens = sharedPreferences.getString("hasChickens",  "");
		model.hasDucks = sharedPreferences.getString("hasDucks",  "");

		//v4 change start
		model.handWashPlace = sharedPreferences.getString("handWashPlace", "");
		model.handWashWaterAvailable = sharedPreferences.getString("handWashWaterAvailable", "");
		model.handWashCleansingAvailable = sharedPreferences.getString("handWashCleansingAvailable", "");
		//v4 change end


		//secondPage
		model.nameFamilyHead= sharedPreferences.getString("nameFamilyHead", "");
		model.mothersName = sharedPreferences.getString("mothersName",  "");
		model.fathersName = sharedPreferences.getString("fathersName",  "");
		model.mobile = sharedPreferences.getString("mobile",  "");
		model.mobileNo = sharedPreferences.getString("mobileNo", "");
		model.dob = sharedPreferences.getString("dob", "");
		model.age = sharedPreferences.getString("age", "");

		model.waterBoil = sharedPreferences.getString("waterBoil",  "");
		model.waterFilter = sharedPreferences.getString("waterFilter",  "");
		model.waterOther = sharedPreferences.getString("waterOther",  "");
		model.waterNone = sharedPreferences.getString("waterNone",  "");
		
		//thirdPage
		
		model.lastEducation = sharedPreferences.getString("lastEducation",  "");
		model.fmTotal = sharedPreferences.getString("fmTotal", "");
		model.fmMale = sharedPreferences.getString("fmMale", "");
		model.fmFemale = sharedPreferences.getString("fmFemale", "");
		model.fm0to5 = sharedPreferences.getString("fm0to5", "");
		model.fm5to11 = sharedPreferences.getString("fm5to11",  "");
		model.fm11to19 = sharedPreferences.getString("fm11to19",  "");
		model.fm19to49 = sharedPreferences.getString("fm19to49",  "");
		model.fm49to60 = sharedPreferences.getString("fm49to60",  "");
		model.fm60Above = sharedPreferences.getString("fm60Above",  "");
		model.disability = sharedPreferences.getString("disability",  "");
		model.mainIncome = sharedPreferences.getString("mainIncome",  "");
		
		//fourthPage
		model.secondaryIncome = sharedPreferences.getString("secondaryIncome",  "");
		model.noOfHouses = sharedPreferences.getString("noOfHouses",  "");
		model.typeOfHouse = sharedPreferences.getString("typeOfHouse",  "");
		model.drinkingWater = sharedPreferences.getString("drinkingWater",  "");
		model.toiletFacility = sharedPreferences.getString("toiletFacility",  "");
		model.avgHouseExpense = sharedPreferences.getString("avgHouseExpense",  "");
		model.hasRefrigerator = sharedPreferences.getString("hasRefrigerator",  "");

		model.hasFishPond = sharedPreferences.getString("hasFishPond",  "");
		model.hasShrimpPond = sharedPreferences.getString("hasShrimpPond",  "");

		//fifthPage
		model.landOwnerShip = sharedPreferences.getString("landOwnerShip",  "");
		model.television = sharedPreferences.getString("television", "");
		model.radio = sharedPreferences.getString("radio", "");
		model.harmonium = sharedPreferences.getString("harmonium", "");
		model.tabla = sharedPreferences.getString("tabla", "");
		model.flute = sharedPreferences.getString("flute",  "");
		model.kitchenFuel = sharedPreferences.getString("kitchenFuel", "");
		model.importantConcern = sharedPreferences.getString("importantConcern", "");

		model.hasCycle = sharedPreferences.getString("hasCycle", "");
		model.hasMotorCycle = sharedPreferences.getString("hasMotorCycle", "");
		model.hasIndependentPowerSource = sharedPreferences.getString("hasIndependentPowerSource", "");

		model.maleChild = sharedPreferences.getString("maleChild", "");
		model.femaleChild = sharedPreferences.getString("femaleChild", "");

		//draft status
		model.householdDraftStatus = sharedPreferences.getString("householdDraftStatus", "draft");
		model.householdDraftWhere = sharedPreferences.getString("householdDraftWhere", "unknown");
		
		
		return model;
	}
	public static void resetMalePersonalModelObject(Context appContext){
		

		
		SharedPreferences sharedPreferences = appContext.getSharedPreferences(Constants.SHAREDPREFERRENCE_TAG_PERSONAL, 0);
		SharedPreferences.Editor spEditor = sharedPreferences.edit();	

		
		//firstPage
		spEditor.putString("personId",  "");
		spEditor.putString("nameFamilyHead", "");
		spEditor.putString("name",  "");
		spEditor.putString("mothersName", "");
		spEditor.putString("fathersName", "");
		spEditor.putString("dob",  "");
		spEditor.putString("age",  "");
		spEditor.putString("voterOrBirth",  "");
		
		//secondPage
		spEditor.putString("education",  "");
		spEditor.putString("height",  "");
		spEditor.putString("profession",  "");
		spEditor.putString("relationWithFamilyHead",  "");				
		spEditor.putString("isBloodGroup",  "");
		spEditor.putString("bloodGroup",  "");
		spEditor.putString("maritalStatus",  "");
		spEditor.putString("healthCondition",  "");
		
		//thirdPage
		
		spEditor.putString("isSymptom",  "");
		spEditor.putString("symptom",  "");
		spEditor.putString("lump",  "");
		spEditor.putString("illness",  "");
		spEditor.putString("doctorCheckUp", "");
		spEditor.putString("diseaseDoctorCheckUp",  "");
		spEditor.putString("medicine", "");
		
		//fourthPage
		spEditor.putString("anyCurrentMedicine",  "");
		spEditor.putString("currentMedicine",  "");
		spEditor.putString("prescribedBy",  "");
		spEditor.putString("treatment",  "");
		spEditor.putString("immunization",  "");
		spEditor.putString("medicalCheckUp",  "");
		
		//fifthPage
		spEditor.putString("chosenGender",  "");
		spEditor.putString("diedAnyByDisease",  "");
		spEditor.putString("diedByDisease",  "");
		spEditor.putString("diedAnyByCancer",  "");
		spEditor.putString("noDiedByCancer",  "");
		
		// male page one
		spEditor.putString("hypertension","");
		spEditor.putString("diabetes", "");
		spEditor.putString("chestPainOnExertion","");
		spEditor.putString("isCancer", "");
		spEditor.putString("cancerType",  "");
		spEditor.putString("asthma", "");
		spEditor.putString("isActiveMajorCondition", "");
		spEditor.putString("activeMajorConditionName", "");
		spEditor.putString("isChronicPain",  "");
		
		spEditor.putString("chronicPainMajorSite", "");
		spEditor.putString("chronicPainCurrentlevel",  "");
		spEditor.putString("chronicPainWorstLevel",  "");

        // male page two
        spEditor.putString("isHeartDisease", "");
        spEditor.putString("isMajorDisability", "");
        spEditor.putString("isHepA", "");
        spEditor.putString("isHepB", "");
        spEditor.putString("isHPV", "");
        spEditor.putString("isTetanus", "");


        spEditor.putString("heightMale", "");
        spEditor.putString("weightMale", "");
        spEditor.putString("systolic1", "");
        spEditor.putString("diastolic1", "");
        spEditor.putString("systolic2", "");
        spEditor.putString("diastolic2", "");


        // male page three
        spEditor.putString("smokingNow", "");
        spEditor.putString("smokingStartingAge", "");
        spEditor.putString("smokingCount", "");
        spEditor.putString("isEverSmoking", "");

        spEditor.putString("betelNutChewing",  "");
		spEditor.putString("familyHistoryCancer","");
		spEditor.putString("isLastYearClinicalMalaria","");
		spEditor.putString("treatedLastYearClinicalMalaria", "");
		spEditor.putString("isProductiveCough",  "");
		spEditor.putString("treatedProductiveCough", "");
		spEditor.putString("regularMedicines", "");
		
		spEditor.putString("personDraftStatus", "draft");
		spEditor.putString("personDraftWhere", "unknown");
		
		spEditor.commit();
	}
	public static void resetFemalePersonalModelObject(Context appContext){
		
		SharedPreferences sharedPreferences = appContext.getSharedPreferences(Constants.SHAREDPREFERRENCE_TAG_PERSONAL, 0);
		SharedPreferences.Editor spEditor = sharedPreferences.edit();	
		
		//firstPage
		spEditor.putString("personId",  "");
		spEditor.putString("nameFamilyHead", "");
		spEditor.putString("name",  "");
		spEditor.putString("mothersName", "");
		spEditor.putString("fathersName", "");
		spEditor.putString("dob",  "");
		spEditor.putString("age",  "");
		spEditor.putString("voterOrBirth",  "");
		
		//secondPage
		spEditor.putString("education",  "");
		spEditor.putString("height",  "");
		spEditor.putString("profession",  "");
		spEditor.putString("relationWithFamilyHead",  "");				
		spEditor.putString("isBloodGroup",  "");
		spEditor.putString("bloodGroup",  "");
		spEditor.putString("maritalStatus",  "");
		spEditor.putString("healthCondition",  "");
		
		//thirdPage
		
		spEditor.putString("isSymptom",  "");
		spEditor.putString("symptom",  "");
		spEditor.putString("lump",  "");
		spEditor.putString("illness",  "");
		spEditor.putString("doctorCheckUp", "");
		spEditor.putString("diseaseDoctorCheckUp",  "");
		spEditor.putString("medicine", "");
		
		//fourthPage
		spEditor.putString("anyCurrentMedicine",  "");
		spEditor.putString("currentMedicine",  "");
		spEditor.putString("prescribedBy",  "");
		spEditor.putString("treatment",  "");
		spEditor.putString("immunization",  "");
		spEditor.putString("medicalCheckUp",  "");
		
		//fifthPage
		spEditor.putString("chosenGender",  "");
		spEditor.putString("diedAnyByDisease",  "");
		spEditor.putString("diedByDisease",  "");
		spEditor.putString("diedAnyByCancer",  "");
		spEditor.putString("noDiedByCancer",  "");
		
		// female page one
		spEditor.putString("hypertension","" );
		spEditor.putString("diabetes",  "");
		spEditor.putString("chestPainOnExertion", "");
		spEditor.putString("isCancer",  "");
		spEditor.putString("cancerType",  "");
		spEditor.putString("asthma",  "");
		spEditor.putString("isActiveMajorCondition", "");
		spEditor.putString("isChronicPain",  "");
		
		spEditor.putString("chronicPainMajorSite",  "");
		spEditor.putString("chronicPainCurrentlevel", "");
		spEditor.putString("chronicPainWorstLevel", "");
		
		
		// female page two
		spEditor.putString("smoking","" );
		spEditor.putString("betelNutChewing", "");
		spEditor.putString("familyHistoryCancer", "");
		spEditor.putString("isLastYearClinicalMalaria",  "");
		spEditor.putString("treatedLastYearClinicalMalaria", "" );
		spEditor.putString("isProductiveCough",  "");
		spEditor.putString("treatedProductiveCough", "");
		
		// female page three

        spEditor.putString("isHeartDisease", "");
        spEditor.putString("isMajorDisability", "");
        spEditor.putString("isHepA", "");
        spEditor.putString("isHepB", "");
        spEditor.putString("isHPV", "");
        spEditor.putString("isTetanus", "");


        spEditor.putString("heightFemale", "");
        spEditor.putString("weightFemale", "");
        spEditor.putString("systolic1", "");
        spEditor.putString("diastolic1", "");
        spEditor.putString("systolic2", "");
        spEditor.putString("diastolic2", "");


		// female page four
		spEditor.putString("ageMenarcheYears","" );
		spEditor.putString("ageMenarcheMonths",  "" );
		spEditor.putString("isMennopauseApplicable", "");
		spEditor.putString("ageMennopauseYears", "");
		spEditor.putString("isFirstPregnencyApplicable","");
		spEditor.putString("ageFirstPregnencyYears", "" );
		spEditor.putString("ageFirstPregnencyMonths", "" );
		spEditor.putString("noFullTermPregnency", "");
		
		spEditor.putString("noMonthsBreastFeeding","");
		spEditor.putString("isCurrentOralContraceptives", "");
		spEditor.putString("isCurrentInjectableContraceptives", "");
		spEditor.putString("isEverOralContraceptives", "");
		
		spEditor.putString("personDraftStatus", "");
		spEditor.putString("personDraftWhere", "");
		
		spEditor.commit();
	}
	
	
	public static List<NameValuePair> getNameValuePairFromModel(Context context, MalePersonalModel model){
		
		
		Class  aClass = model.getClass();
		Field[] members = aClass.getFields();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for(Field member :members){
			try {
				Log.e("rampal","member: "+member.getName());
				Object memberValue = member.get(model);
				Log.e("rampal","value: "+(String)memberValue);
				
				params.add(new BasicNameValuePair(member.getName(), (String)memberValue));
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(params.isEmpty()){
			return null;
		}
		else{
			params.add(new BasicNameValuePair("userId", UserUtils.getUserId(context)));
			params.add(new BasicNameValuePair("userLatitude", UserUtils.getUserLatitude(context)));
			params.add(new BasicNameValuePair("userLongitude", UserUtils.getUserLongitude(context)));
			return params;
		}
	}
	public static List<NameValuePair> getNameValuePairFromModel(Context context, FemalePersonalModel model){
		
		
		Class  aClass = model.getClass();
		Field[] members = aClass.getFields();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for(Field member :members){
			try {
				Log.e("rampal","member: "+member.getName());
				Object memberValue = member.get(model);
				Log.e("rampal","value: "+(String)memberValue);
				
				params.add(new BasicNameValuePair(member.getName(), (String)memberValue));
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(params.isEmpty()){
			return null;
		}
		else{
			params.add(new BasicNameValuePair("userId", UserUtils.getUserId(context)));
			params.add(new BasicNameValuePair("userLatitude", UserUtils.getUserLatitude(context)));
			params.add(new BasicNameValuePair("userLongitude", UserUtils.getUserLongitude(context)));
			return params;
		}
	}
	public static List<NameValuePair> getNameValuePairFromModel(Context context, CommonPersonalModel model){
		
		
		Class  aClass = model.getClass();
		Field[] members = aClass.getFields();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for(Field member :members){
			try {
				Log.e("rampal","member: "+member.getName());
				Object memberValue = member.get(model);
				Log.e("rampal","value: "+(String)memberValue);
				
				params.add(new BasicNameValuePair(member.getName(), (String)memberValue));
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(params.isEmpty()){
			return null;
		}
		else{
			params.add(new BasicNameValuePair("userId", UserUtils.getUserId(context)));
			params.add(new BasicNameValuePair("userLatitude", UserUtils.getUserLatitude(context)));
			params.add(new BasicNameValuePair("userLongitude", UserUtils.getUserLongitude(context)));
			return params;
		}
	}
 	public static List<NameValuePair> getNameValuePairFromModel(Context context, HouseholdModel model){
		
		
		Class  aClass = model.getClass();
		Field[] members = aClass.getFields();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for(Field member :members){
			try {
				Log.e("rampal","member: "+member.getName());
				Object memberValue = member.get(model);
				Log.e("rampal","value: "+(String)memberValue);
				
				params.add(new BasicNameValuePair(member.getName(), (String)memberValue));
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(params.isEmpty()){
			return null;
		}
		else{
			params.add(new BasicNameValuePair("userId", UserUtils.getUserId(context)));
			params.add(new BasicNameValuePair("userLatitude", UserUtils.getUserLatitude(context)));
			params.add(new BasicNameValuePair("userLongitude", UserUtils.getUserLongitude(context)));
			return params;
		}
	}
 	
 	
 	public static void updateSharedPreferencesFromModel(Context context, MalePersonalModel model){
 		resetMalePersonalModelObject(context);
 		
 		SharedPreferences sharedPreferences;
 		SharedPreferences.Editor spEditor;
 		
 		sharedPreferences =  context.getSharedPreferences(Constants.SHAREDPREFERRENCE_TAG_PERSONAL, 0);
		spEditor = sharedPreferences.edit();
		
		
		Class  aClass = model.getClass();
		Field[] members = aClass.getFields();
		//List<NameValuePair> params = new ArrayList<NameValuePair>();
		for(Field member :members){
			try {
				Log.e("rampal","member: "+member.getName());
				Object memberValue = member.get(model);
				Log.e("rampal","value: "+(String)memberValue);
				
				//params.add(new BasicNameValuePair(member.getName(), (String)memberValue));
				spEditor.putString(member.getName(), (String)memberValue);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		spEditor.commit();
	}
 	public static void updateSharedPreferencesFromModel(Context context, FemalePersonalModel model){
 		resetFemalePersonalModelObject(context);
 		
 		SharedPreferences sharedPreferences;
 		SharedPreferences.Editor spEditor;
 		
 		sharedPreferences =  context.getSharedPreferences(Constants.SHAREDPREFERRENCE_TAG_PERSONAL, 0);
		spEditor = sharedPreferences.edit();
		
		
		Class  aClass = model.getClass();
		Field[] members = aClass.getFields();
		//List<NameValuePair> params = new ArrayList<NameValuePair>();
		for(Field member :members){
			try {
				Log.e("rampal","member: "+member.getName());
				Object memberValue = member.get(model);
				Log.e("rampal","value: "+(String)memberValue);
				
				//params.add(new BasicNameValuePair(member.getName(), (String)memberValue));
				spEditor.putString(member.getName(), (String)memberValue);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		spEditor.commit();
	}
 	public static void updateSharedPreferencesFromModel(Context context, HouseholdModel model){
 		clearAllHouseholdInfo(context);
 		
 		SharedPreferences sharedPreferences;
 		SharedPreferences.Editor spEditor;
 		
 		sharedPreferences =  context.getSharedPreferences("householdInformation", 0);
		spEditor = sharedPreferences.edit();
		
		
		Class  aClass = model.getClass();
		Field[] members = aClass.getFields();
		for(Field member :members){
			try {
				Log.e("rampal","member: "+member.getName());
				Object memberValue = member.get(model);
				Log.e("rampal","value: "+(String)memberValue);
				spEditor.putString(member.getName(), (String)memberValue);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		spEditor.commit();
	}
 	public static CommonPersonalModel getCommonPersonalModelObject(Context appContext){
 		CommonPersonalModel model = new CommonPersonalModel();
		
		SharedPreferences sharedPreferences = appContext.getSharedPreferences(Constants.SHAREDPREFERRENCE_TAG_PERSONAL, 0);
		
		model.personId = sharedPreferences.getString("personId",  "");
		
		//firstPage
		model.nameFamilyHead = sharedPreferences.getString("nameFamilyHead",  "");
		model.name = sharedPreferences.getString("name",  "");
		model.mothersName = sharedPreferences.getString("mothersName", "");
		model.fathersName = sharedPreferences.getString("fathersName", "");
		model.dob = sharedPreferences.getString("dob",  "");
		model.age = sharedPreferences.getString("age",  "");
		model.voterOrBirth = sharedPreferences.getString("voterOrBirth", "");
		
		//secondPage
		model.education = sharedPreferences.getString("education","");
		model.height = sharedPreferences.getString("height","");
		model.profession = sharedPreferences.getString("profession","");
		model.relationWithFamilyHead = sharedPreferences.getString("relationWithFamilyHead",  "");				
		model.isBloodGroup = sharedPreferences.getString("isBloodGroup",  "");
		model.bloodGroup = sharedPreferences.getString("bloodGroup","");
		model.maritalStatus = sharedPreferences.getString("maritalStatus",  "");
		model.healthCondition = sharedPreferences.getString("healthCondition",  "");
		
		//thirdPage
		
		model.isSymptom = sharedPreferences.getString("isSymptom",  "");
		model.symptom = sharedPreferences.getString("symptom",  "");
		model.lump = sharedPreferences.getString("lump",  "");
		model.illness = sharedPreferences.getString("illness",  "");
		model.doctorCheckUp = sharedPreferences.getString("doctorCheckUp", "");
		model.diseaseDoctorCheckUp = sharedPreferences.getString("diseaseDoctorCheckUp",  "");
		model.medicine = sharedPreferences.getString("medicine", "");
		
		//fourthPage
		model.anyCurrentMedicine = sharedPreferences.getString("anyCurrentMedicine",  "");
		model.currentMedicine = sharedPreferences.getString("currentMedicine",  "");
		model.prescribedBy = sharedPreferences.getString("prescribedBy", "");
		model.treatment = sharedPreferences.getString("treatment",  "");
		model.immunization = sharedPreferences.getString("immunization",  "");
		model.medicalCheckUp = sharedPreferences.getString("medicalCheckUp",  "");
		
		//fifthPage
		model.chosenGender = sharedPreferences.getString("chosenGender",  "");
		model.diedAnyByDisease = sharedPreferences.getString("diedAnyByDisease",  "");
		model.reasonForDeath = sharedPreferences.getString("reasonForDeath",  "");
		model.diedAnyByCancer = sharedPreferences.getString("diedAnyByCancer",  "");
		model.typeOfCancer = sharedPreferences.getString("typeOfCancer",  "");
		model.isMotherFatherCousin = sharedPreferences.getString("isMotherFatherCousin",  "");

		model.personDraftStatus = sharedPreferences.getString("personDraftStatus", "");
		model.personDraftWhere = sharedPreferences.getString("personDraftWhere", "");
		
		return model;
 	}
	
		
	
	public static MalePersonalModel getMalePersonalModelObject(Context appContext){
		
		MalePersonalModel model = new MalePersonalModel();
		
		SharedPreferences sharedPreferences = appContext.getSharedPreferences(Constants.SHAREDPREFERRENCE_TAG_PERSONAL, 0);
		
		model.personId = sharedPreferences.getString("personId",  "");
		
		//firstPage
		model.nameFamilyHead = sharedPreferences.getString("nameFamilyHead",  "");
		model.name = sharedPreferences.getString("name",  "");
		model.mothersName = sharedPreferences.getString("mothersName", "");
		model.fathersName = sharedPreferences.getString("fathersName", "");
		model.dob = sharedPreferences.getString("dob",  "");
		model.age = sharedPreferences.getString("age",  "");
		model.voterOrBirth = sharedPreferences.getString("voterOrBirth", "");
		
		//secondPage
		model.education = sharedPreferences.getString("education","");
		model.height = sharedPreferences.getString("height","");
		model.profession = sharedPreferences.getString("profession","");
		model.relationWithFamilyHead = sharedPreferences.getString("relationWithFamilyHead",  "");				
		model.isBloodGroup = sharedPreferences.getString("isBloodGroup",  "");
		model.bloodGroup = sharedPreferences.getString("bloodGroup","");
		model.maritalStatus = sharedPreferences.getString("maritalStatus",  "");
		model.healthCondition = sharedPreferences.getString("healthCondition",  "");
		
		//thirdPage
		
		model.isSymptom = sharedPreferences.getString("isSymptom",  "");
		model.symptom = sharedPreferences.getString("symptom",  "");
		model.lump = sharedPreferences.getString("lump",  "");
		model.illness = sharedPreferences.getString("illness",  "");
		model.doctorCheckUp = sharedPreferences.getString("doctorCheckUp", "");
		model.diseaseDoctorCheckUp = sharedPreferences.getString("diseaseDoctorCheckUp",  "");
		model.medicine = sharedPreferences.getString("medicine", "");
		
		//fourthPage
		model.anyCurrentMedicine = sharedPreferences.getString("anyCurrentMedicine",  "");
		model.currentMedicine = sharedPreferences.getString("currentMedicine",  "");
		model.prescribedBy = sharedPreferences.getString("prescribedBy", "");
		model.treatment = sharedPreferences.getString("treatment",  "");
		model.immunization = sharedPreferences.getString("immunization",  "");
		model.medicalCheckUp = sharedPreferences.getString("medicalCheckUp",  "");
		
		//fifthPage
		model.chosenGender = sharedPreferences.getString("chosenGender",  "");
		model.diedAnyByDisease = sharedPreferences.getString("diedAnyByDisease",  "");
		model.reasonForDeath = sharedPreferences.getString("reasonForDeath",  "");
		model.diedAnyByCancer = sharedPreferences.getString("diedAnyByCancer",  "");
		model.typeOfCancer = sharedPreferences.getString("typeOfCancer",  "");
		model.isMotherFatherCousin = sharedPreferences.getString("isMotherFatherCousin",  "");
		
		// male page one
		model.hypertension = sharedPreferences.getString("hypertension","" );
		model.diabetes = sharedPreferences.getString("diabetes",  "");
		model.chestPainOnExertion = sharedPreferences.getString("chestPainOnExertion", "");
		model.isCancer = sharedPreferences.getString("isCancer",  "");
		model.cancerType = sharedPreferences.getString("cancerType",  "");
		model.asthma = sharedPreferences.getString("asthma",  "");
		model.isActiveMajorCondition = sharedPreferences.getString("isActiveMajorCondition", "");
		model.activeMajorConditionName = sharedPreferences.getString("activeMajorConditionName", "");
		model.isChronicPain = sharedPreferences.getString("isChronicPain",  "");
		
		model.chronicPainMajorSite = sharedPreferences.getString("chronicPainMajorSite",  "");
		model.chronicPainCurrentLevel = sharedPreferences.getString("chronicPainCurrentLevel", "");
		model.chronicPainWorstLevel = sharedPreferences.getString("chronicPainWorstLevel", "");

		// male page two
        model.isHeartDisease = sharedPreferences.getString("isHeartDisease", "");
        model.isMajorDisability = sharedPreferences.getString("isMajorDisability", "");
        model.typeOfDisability = sharedPreferences.getString("typeOfDisability", "");
        model.isHepA = sharedPreferences.getString("isHepA", "");
        model.isHepB = sharedPreferences.getString("isHepB", "");
        model.isHPV = sharedPreferences.getString("isHPV", "");
        model.isTetanus = sharedPreferences.getString("isTetanus", "");


        model.heightMale = sharedPreferences.getString("heightMale", "");
        model.weightMale = sharedPreferences.getString("weightMale", "");
        model.systolic1 = sharedPreferences.getString("systolic1", "");
        model.diastolic1 = sharedPreferences.getString("diastolic1", "");
        model.systolic2 = sharedPreferences.getString("systolic2", "");
        model.diastolic2 = sharedPreferences.getString("diastolic2", "");

		
		// male page three
		model.smokingNow = sharedPreferences.getString("smokingNow","" );
		model.smokingStartingAge = sharedPreferences.getString("smokingStartingAge","" );
		model.smokingCount = sharedPreferences.getString("smokingCount","" );
		model.isEverSmoking = sharedPreferences.getString("isEverSmoking","" );

		model.betelNutChewing = sharedPreferences.getString("betelNutChewing", "");
		model.familyHistoryCancer = sharedPreferences.getString("familyHistoryCancer", "");
		model.isLastYearClinicalMalaria = sharedPreferences.getString("isLastYearClinicalMalaria",  "");
		model.treatedLastYearClinicalMalaria = sharedPreferences.getString("treatedLastYearClinicalMalaria", "" );
		model.isProductiveCough = sharedPreferences.getString("isProductiveCough",  "");
		model.treatedProductiveCough = sharedPreferences.getString("treatedProductiveCough", "");
		model.regularMedicines = sharedPreferences.getString("regularMedicines",  "");
		
		model.personDraftStatus = sharedPreferences.getString("personDraftStatus", "");
		model.personDraftWhere = sharedPreferences.getString("personDraftWhere", "");
		
		
		
		return model;
	}

	public static FemalePersonalModel getFemalePersonalModelObject(Context appContext){
		
		FemalePersonalModel model = new FemalePersonalModel();
		
		SharedPreferences sharedPreferences = appContext.getSharedPreferences(Constants.SHAREDPREFERRENCE_TAG_PERSONAL, 0);
		
		model.personId = sharedPreferences.getString("personId",  "");
		//firstPage
		model.nameFamilyHead = sharedPreferences.getString("nameFamilyHead",  "");
		model.name = sharedPreferences.getString("name",  "");
		model.mothersName = sharedPreferences.getString("mothersName", "");
		model.fathersName = sharedPreferences.getString("fathersName", "");
		model.dob = sharedPreferences.getString("dob",  "");
		model.age = sharedPreferences.getString("age",  "");
		model.voterOrBirth = sharedPreferences.getString("voterOrBirth", "");
		
		//secondPage
		model.education = sharedPreferences.getString("education",  "");
		model.height = sharedPreferences.getString("height",  "");
		model.profession = sharedPreferences.getString("profession",  "");
		model.relationWithFamilyHead = sharedPreferences.getString("relationWithFamilyHead",  "");				
		model.isBloodGroup = sharedPreferences.getString("isBloodGroup",  "");
		model.bloodGroup = sharedPreferences.getString("bloodGroup",  "");
		model.maritalStatus = sharedPreferences.getString("maritalStatus",  "");
		model.healthCondition = sharedPreferences.getString("healthCondition",  "");
		
		//thirdPage
		
		model.isSymptom = sharedPreferences.getString("isSymptom",  "");
		model.symptom = sharedPreferences.getString("symptom",  "");
		model.lump = sharedPreferences.getString("lump",  "");
		model.illness = sharedPreferences.getString("illness",  "");
		model.doctorCheckUp = sharedPreferences.getString("doctorCheckUp", "");
		model.diseaseDoctorCheckUp = sharedPreferences.getString("diseaseDoctorCheckUp",  "");
		model.medicine = sharedPreferences.getString("medicine", "");
		
		//fourthPage
		model.anyCurrentMedicine = sharedPreferences.getString("anyCurrentMedicine",  "");
		model.currentMedicine = sharedPreferences.getString("currentMedicine",  "");
		model.prescribedBy = sharedPreferences.getString("prescribedBy", "");
		model.treatment = sharedPreferences.getString("treatment",  "");
		model.immunization = sharedPreferences.getString("immunization",  "");
		model.medicalCheckUp = sharedPreferences.getString("medicalCheckUp",  "");
		
		//fifthPage
		model.chosenGender = sharedPreferences.getString("chosenGender",  "");
		model.diedAnyByDisease = sharedPreferences.getString("diedAnyByDisease",  "");
		model.reasonForDeath = sharedPreferences.getString("reasonForDeath",  "");
		model.diedAnyByCancer = sharedPreferences.getString("diedAnyByCancer",  "");
		model.typeOfCancer = sharedPreferences.getString("typeOfCancer",  "");
		model.isMotherFatherCousin = sharedPreferences.getString("isMotherFatherCousin",  "");
		
		// female page one
		model.hypertension = sharedPreferences.getString("hypertension","" );
		model.diabetes = sharedPreferences.getString("diabetes",  "");
		model.chestPainOnExertion = sharedPreferences.getString("chestPainOnExertion", "");
		model.isCancer = sharedPreferences.getString("isCancer",  "");
		model.cancerType = sharedPreferences.getString("cancerType",  "");
		model.asthma = sharedPreferences.getString("asthma",  "");
		model.isActiveMajorCondition = sharedPreferences.getString("isActiveMajorCondition", "");
		model.activeMajorConditionName = sharedPreferences.getString("activeMajorConditionName", "");
		model.isChronicPain = sharedPreferences.getString("isChronicPain",  "");
		
		model.chronicPainMajorSite = sharedPreferences.getString("chronicPainMajorSite",  "");
		model.chronicPainCurrentLevel = sharedPreferences.getString("chronicPainCurrentLevel", "");
		model.chronicPainWorstLevel = sharedPreferences.getString("chronicPainWorstLevel", "");
		
		
		// female page two
		model.smoking = sharedPreferences.getString("smoking","" );
		model.betelNutChewing = sharedPreferences.getString("betelNutChewing", "");
		model.familyHistoryCancer = sharedPreferences.getString("familyHistoryCancer", "");
		model.isLastYearClinicalMalaria = sharedPreferences.getString("isLastYearClinicalMalaria",  "");
		model.treatedLastYearClinicalMalaria = sharedPreferences.getString("treatedLastYearClinicalMalaria", "" );
		model.isProductiveCough = sharedPreferences.getString("isProductiveCough",  "");
		model.treatedProductiveCough = sharedPreferences.getString("treatedProductiveCough", "");
		
		// female page three
        model.isHeartDisease = sharedPreferences.getString("isHeartDisease", "");
        model.isMajorDisability = sharedPreferences.getString("isMajorDisability", "");
        model.typeOfDisability = sharedPreferences.getString("typeOfDisability", "");
        model.isHepA = sharedPreferences.getString("isHepA", "");
        model.isHepB = sharedPreferences.getString("isHepB", "");
        model.isHPV = sharedPreferences.getString("isHPV", "");
        model.isTetanus = sharedPreferences.getString("isTetanus", "");


        model.heightFemale = sharedPreferences.getString("heightFemale", "");
        model.weightFemale = sharedPreferences.getString("weightFemale", "");
        model.systolic1 = sharedPreferences.getString("systolic1", "");
        model.diastolic1 = sharedPreferences.getString("diastolic1", "");
        model.systolic2 = sharedPreferences.getString("systolic2", "");
        model.diastolic2 = sharedPreferences.getString("diastolic2", "");

		// female page four
		model.ageMenarcheYears = sharedPreferences.getString("ageMenarcheYears","" );
		model.ageMenarcheMonths = sharedPreferences.getString("ageMenarcheMonths",  "" );
		model.isMennopauseApplicable = sharedPreferences.getString("isMennopauseApplicable", "");
		model.ageMennopauseYears = sharedPreferences.getString("ageMennopauseYears", "");
		model.isFirstPregnencyApplicable = sharedPreferences.getString("isFirstPregnencyApplicable","");
		model.ageFirstPregnencyYears = sharedPreferences.getString("ageFirstPregnencyYears", "" );
		model.ageFirstPregnencyMonths = sharedPreferences.getString("ageFirstPregnencyMonths", "" );
		model.noFullTermPregnency = sharedPreferences.getString("noFullTermPregnency", "");
		
		model.noMonthsBreastFeeding = sharedPreferences.getString("noMonthsBreastFeeding","");
		model.isCurrentOralContraceptives = sharedPreferences.getString("isCurrentOralContraceptives", "");
		model.isCurrentInjectableContraceptives = sharedPreferences.getString("isCurrentInjectableContraceptives", "");
		model.isEverOralContraceptives = sharedPreferences.getString("isEverOralContraceptives", "");
		
		model.personDraftStatus = sharedPreferences.getString("personDraftStatus", "");
		model.personDraftWhere = sharedPreferences.getString("personDraftWhere", "");
		
		
		
		return model;
	}

}

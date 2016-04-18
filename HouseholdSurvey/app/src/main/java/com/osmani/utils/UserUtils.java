package com.osmani.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.security.MessageDigest;

public class UserUtils {
	
	/**
	 * 
	 * @param context
	 * @return, if useer logged in, then userid otherwise null
	 */
	
	public static String  getUserId(Context context){
		
		SharedPreferences sharedPreferences;
		
		sharedPreferences =  context.getSharedPreferences(Constants.SHAREDPREFERRENCE_TAG_USER, 0);  	

		return sharedPreferences.getString("user_id",  null);
		
	}
	public static void  setUserId(Context context,String userId){		
		
		SharedPreferences sharedPreferences;
		SharedPreferences.Editor spEditor;
		
		sharedPreferences =  context.getSharedPreferences(Constants.SHAREDPREFERRENCE_TAG_USER, 0);  
		
		spEditor = sharedPreferences.edit();
		spEditor.putString("user_id",  userId);
		spEditor.commit();
	}
	
	public static String  getUserLatitude(Context context){
		
		SharedPreferences sharedPreferences;
		
		sharedPreferences =  context.getSharedPreferences(Constants.SHAREDPREFERRENCE_TAG_USER_LOCATION, 0);  	

		return sharedPreferences.getString("user_last_location_latitude",  null);
		
	}
	public static String  getUserLongitude(Context context){
		
		SharedPreferences sharedPreferences;
		
		sharedPreferences =  context.getSharedPreferences(Constants.SHAREDPREFERRENCE_TAG_USER_LOCATION, 0);  	

		return sharedPreferences.getString("user_last_location_longitude",  null);
		
	}

	public static final String getMD5(final String toEncrypt) {
		try {
			final MessageDigest digest = MessageDigest.getInstance("md5");
			digest.update(toEncrypt.getBytes());
			final byte[] bytes = digest.digest();
			final StringBuilder sb = new StringBuilder();
			for (int i = 0; i < bytes.length; i++) {
				sb.append(String.format("%02X", bytes[i]));
			}
			return sb.toString().toLowerCase();
		} catch (Exception exc) {
			exc.printStackTrace();
			return null; // Impossibru!
		}
	}

}

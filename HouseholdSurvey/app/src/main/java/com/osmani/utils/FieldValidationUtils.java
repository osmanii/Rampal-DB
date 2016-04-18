package com.osmani.utils;

import android.widget.EditText;
import android.widget.Spinner;

public class FieldValidationUtils {
	
	public static boolean validateSpinnerValue(Spinner spinnerView){
		
		if(spinnerView.getSelectedItemPosition()==0) // Select /Please select etc will be on top position
			return false;
		return true;
	}
	
	public static boolean validateEdittextValueForText(EditText editText){
		
		if(editText.getText().toString().trim().length()==0) // trimmed text's length 0 means actually nothing is typed
			return false;
		return true;
	}
	
	public static boolean validateEdittextValueForNumber(EditText editText){
		
		if(editText.getText().toString().trim().length()==0) // trimmed text's length 0 means actually nothing is typed
			return false;
		
		return isNumeric(editText.getText().toString().trim());
	}
	
	public static boolean isNumeric(String str)  
	{  
	  try  
	  {  
	    Integer d = Integer.parseInt(str);  
	  }  
	  catch(NumberFormatException nfe)  
	  {  
	    return false;  
	  }  
	  return true;  
	}

}

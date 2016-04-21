package com.osmani.utils;

import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

public class ViewUtils {
	
	public static String getSwitchValue(Switch switchView){
		return (switchView.isChecked()?switchView.getTextOn().toString():switchView.getTextOff().toString());
	}
	public static String getSpinnerSelectedValue(Spinner spinnerView){			
		
		return spinnerView.getSelectedItem().toString().trim();
	}
	public static String getEditTextInput(EditText editText){

		if(editText.getText().toString().trim().length()!=0)
			return editText.getText().toString().trim();
		else
			return "";
	}
}

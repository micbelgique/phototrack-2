package com.ulteam.phototrack.Helpers;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class Help_Keyboard {
	
	/**
	 * Hide the softKeyboard
	 * @param context The context
	 * @param editText The editText which has the focus
	 */
	public static void hideKeyboard(Context context, View editText){
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}
		
	/**
	 * Show the softKeyboard
	 * @param context The context
	 * @param editText The editText which request the focus
	 */
	public static void showKeyboard(Context context, View editText){
		InputMethodManager mgr = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT); // only will trigger it if no physical keyboard is open
		editText.requestFocus();
	}
	
	/**
	 * Show the softKeyboard
	 * @param activity The activity
	 * @param editText editText The editText which request the focus
	 */
	public static void showKeyboard(Activity activity, EditText editText) {
		activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		editText.requestFocus();
	}
	
}

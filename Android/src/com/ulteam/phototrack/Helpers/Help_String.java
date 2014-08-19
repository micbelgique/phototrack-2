package com.ulteam.phototrack.Helpers;

import java.text.DecimalFormat;

import android.widget.EditText;

public class Help_String {

	static final String accentValues = "ÀÁÂÃÄÅàáâãäåÒÓÔÕÖØòóôõöøÈÉÊËèéêëÌÍÎÏìíîïÙÚÛÜùúûüÿÑñÇç";
	static final String noAccentValues = "AAAAAAaaaaaaOOOOOOooooooEEEEeeeeIIIIiiiiUUUUuuuuyNnCc";

	/**
	 * Replace specials characters like ä, Ø, Ï, ... by a, O, I, ...
	 */
	public static String replaceSpecialsChars(String word){
		if(word != null)
			for(int i = 0; i < accentValues.length(); i++)
				word = word.replace(accentValues.charAt(i), noAccentValues.charAt(i));

		return word;
	}	

	/**
	 * Delete nos significant zero: Transform 5.0 -> 5
	 */
	public static String deleteNoSignificantZero(String number){
		if(number.endsWith(".0"))
			return number.substring(0, number.length() - 2);
		return number;
	}

	/**
	 * Transforme un float en String avec 2 décimal après la virgule, MEME les zéros
	 */
	public static String getStringTwoDecimal(float number) {
		return new DecimalFormat("0.00").format(number);
	}

	public static String deleteIllegalCharsForEmail(String string){
		String legalString = "";

		for(int i = 0; i < string.length(); i++){			
			if(new String("-_&@ :;()!?,.").contains(String.valueOf(string.charAt(i))) || Character.isDigit(string.charAt(i)) || Character.isLetter(string.charAt(i)))
				legalString += string.charAt(i);
			else
				legalString += " ";
		}
		return legalString;
	}


	public static Boolean isNumeric(String s) {
		return s!= null && s.matches("\\d+");
	}

	public static Boolean isWebUrl(String s) {
		return s.startsWith("http://") || s.startsWith("https://");
	}

	public static Boolean isGuid(String s) {
		// bf3b87ba-49e3-4eee-acae-e4cf72a8f090
		return s != null && s.matches("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}");
	}

	public static Boolean isEmail(String s) {
		return s!= null && s.toLowerCase().matches("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?");
		// return Patterns.EMAIL_ADDRESS.matcher(s).matches();
	}

	public static boolean isNotNullNorEmpty(String s) {
		return s != null && s.trim().length() > 0 && !s.trim().equals("null");
	}

	public static boolean isNullOrEmpty(String s) {
		return s == null || s.trim().length() == 0 || s.trim().equals("null");
	}

	public static boolean isNullOrEmpty(EditText editText) {
		return editText == null || isNullOrEmpty(editText.getText().toString());
	}
}

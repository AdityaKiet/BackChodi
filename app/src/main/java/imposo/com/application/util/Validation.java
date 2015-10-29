package imposo.com.application.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validation {
	private static Pattern pattern;
	private static Matcher matcher;
	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	public static boolean validateEmail(String email) {
		pattern = Pattern.compile(EMAIL_PATTERN);
		matcher = pattern.matcher(email);
		return matcher.matches();
	}
	
	public static boolean validatePassword(String password) {
		if(password.length() < 6)
			return false;
		else 
			return true;
	}
	
	public static boolean validatePhoneNumber(String phone)
	{	Boolean result = true;
		    for (char c : phone.toCharArray())
		    	 if (!Character.isDigit(c)){
		    		 result = false;
		    		 break;
		    	 }
	       
	    return result;
	}
}

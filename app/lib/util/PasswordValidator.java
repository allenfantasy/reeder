package lib.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Password Validator
 * 
 * Refer:
 * http://www.mkyong.com/regular-expressions/how-to-validate-password-with-regular-expression/
 * @author allen
 *
 */
// TODO: test this validator
public class PasswordValidator {
	private Pattern pattern;
	private Matcher matcher;
	
	// ^									# Start of group
	//   (?=.*\d)         #   must contain one from 0-9
	//   (?=.*[a-zA-Z])   #   must contain one lowercase/uppercase characters
	//            .       #    match anything with previous condition checking
	//             {6,20} #      length at least 6 characters and maximum of 20
	// $                  # End of group
	private static final String PASSWORD_PATTERN = "^(?=.*\\d)(?=.*[a-zA-Z]).{6,20}$";
	
	public PasswordValidator() {
		pattern = Pattern.compile(PASSWORD_PATTERN);
	}
	
	/**
	 * Validate password with regular expression
	 * @param password password for validation
	 * @return true valid password, false invalid password
	 */
	public boolean validate(final String password) {
		matcher = pattern.matcher(password);
		return matcher.matches();
	}
}
package util;

// Java built-in packages
import org.junit.*;

// 3rd Party's packages
import static org.fest.assertions.Assertions.*;
import lib.util.*;

/**
 * Password validator testing
 * 
 * Refer:
 * http://www.mkyong.com/regular-expressions/how-to-validate-password-with-regular-expression/
 * @author allen
 *
 */
public class PasswordValidatorTest {
	private static PasswordValidator pwdValidator;
	
	@BeforeClass
	public static void initData() {
		pwdValidator = new PasswordValidator();
	}
	
	@Test
	public void validateTest() {
		String[] validPasswords = {"aslgjaj931", "ADklkasjd110", "809as9d",
				"asglasi476sngl8", "fantasy236950"};
		String[] invalidPasswords = {"salgj", "ADDGKLJ", "ABCDefg", "39uF", "23456789"};
		
		for(String pwd : validPasswords) {
			assertThat(pwdValidator.validate(pwd)).isEqualTo(true);
		}
		for(String pwd : invalidPasswords) {
			assertThat(pwdValidator.validate(pwd)).isEqualTo(false);
		}
	}
}

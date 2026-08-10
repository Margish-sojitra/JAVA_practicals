public class Driver1 {
    public static void main(String[] args) {
        String[] testPasswords = { "abc", "abcd1234", "Abcd1234!" };

        for (String pw : testPasswords) {
            System.out.println("Testing password: \"" + pw + "\"");
            System.out.println(" - Min Length (>=8): " + PasswordChecker.hasMinLength(pw));
            System.out.println(" - Uppercase: " + PasswordChecker.hasUppercase(pw));
            System.out.println(" - Digit: " + PasswordChecker.hasDigit(pw));
            System.out.println(" - Special Char: " + PasswordChecker.hasSpecialChar(pw));
            System.out.println(" - Strength Label: " + PasswordChecker.strength(pw));
            System.out.println();
        }
    }
}

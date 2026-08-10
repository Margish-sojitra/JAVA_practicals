public class PasswordChecker {

    public static boolean hasMinLength(String pw) {
        return pw != null && pw.length() >= 8;
    }

    public static boolean hasUppercase(String pw) {
        return pw != null && pw.matches(".*[A-Z].*");
    }

    public static boolean hasDigit(String pw) {
        return pw != null && pw.matches(".*[0-9].*");
    }

    public static boolean hasSpecialChar(String pw) {
        return pw != null && pw.matches(".*[^a-zA-Z0-9].*");
    }

    public static int getPassedRuleCount(String pw) {
        int count = 0;
        if (hasMinLength(pw))
            count++;
        if (hasUppercase(pw))
            count++;
        if (hasDigit(pw))
            count++;
        if (hasSpecialChar(pw))
            count++;
        return count;
    }

    public static String strength(String pw) {
        int score = getPassedRuleCount(pw);
        if (score <= 1) {
            return "Weak";
        } else if (score <= 3) {
            return "Medium";
        } else {
            return "Strong";
        }
    }
}

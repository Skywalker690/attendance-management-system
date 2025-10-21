package com.company.utils;

/**
 * Utility class for input validation
 */
public class ValidationUtil {
    
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    
    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return email.matches(EMAIL_REGEX);
    }
    
    /**
     * Check if string is empty or null
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * Validate password strength (minimum 6 characters)
     */
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }
    
    /**
     * Validate that all required fields are filled
     */
    public static boolean areFieldsFilled(String... fields) {
        for (String field : fields) {
            if (isEmpty(field)) {
                return false;
            }
        }
        return true;
    }
}

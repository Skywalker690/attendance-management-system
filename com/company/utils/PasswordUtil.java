package com.company.utils;

/**
 * Utility class for password handling
 * Note: Stores passwords in plain text as requested
 */
public class PasswordUtil {
    
    /**
     * Return password as-is (no hashing)
     */
    public static String hashPassword(String password) {
        return password;
    }
    
    /**
     * Verify password by direct string comparison
     */
    public static boolean verifyPassword(String password, String storedPassword) {
        if (password == null || storedPassword == null) {
            return false;
        }
        return password.equals(storedPassword);
    }
}

package com.nexus.utils;

import org.bukkit.ChatColor;

import java.util.regex.Pattern;

/**
 * ValidationUtil - Input validation utility
 * 
 * Provides validation for common input types (money, names, etc.)
 * Helps prevent invalid data from entering the system.
 * 
 * @author Nexus Development Team
 */
public class ValidationUtil {
    
    // Name patterns
    private static final Pattern GUILD_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,16}$");
    private static final Pattern PLAYER_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,16}$");
    
    // Money limits
    private static final double MIN_MONEY = 0.01;
    private static final double MAX_MONEY = 999999999.99;
    
    /**
     * Validate a money amount
     */
    public static boolean isValidMoney(double amount) {
        return amount >= MIN_MONEY && amount <= MAX_MONEY && !Double.isNaN(amount) && !Double.isInfinite(amount);
    }
    
    /**
     * Validate a positive money amount
     */
    public static boolean isPositiveMoney(double amount) {
        return isValidMoney(amount) && amount > 0;
    }
    
    /**
     * Validate a guild name
     * - Must be 3-16 characters
     * - Alphanumeric and underscores only
     * - No color codes or special characters
     */
    public static boolean isValidGuildName(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        
        // Strip color codes
        String stripped = ChatColor.stripColor(name);
        
        return GUILD_NAME_PATTERN.matcher(stripped).matches();
    }
    
    /**
     * Validate a player name
     */
    public static boolean isValidPlayerName(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        
        return PLAYER_NAME_PATTERN.matcher(name).matches();
    }
    
    /**
     * Validate a team name (Solar or Lunar)
     */
    public static boolean isValidTeamName(String team) {
        if (team == null) {
            return false;
        }
        
        String upperTeam = team.toUpperCase();
        return upperTeam.equals("SOLAR") || upperTeam.equals("LUNAR");
    }
    
    /**
     * Sanitize input by removing potentially dangerous characters
     */
    public static String sanitize(String input) {
        if (input == null) {
            return "";
        }
        
        // Remove color codes
        String sanitized = ChatColor.stripColor(input);
        
        // Remove SQL-dangerous characters
        sanitized = sanitized.replaceAll("[';\"\\\\]", "");
        
        // Trim whitespace
        sanitized = sanitized.trim();
        
        return sanitized;
    }
    
    /**
     * Check if a string contains profanity or inappropriate words
     * (Basic implementation - should be expanded with a proper word list)
     */
    public static boolean containsProfanity(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        
        String lower = text.toLowerCase();
        
        // Basic profanity filter (expand this list as needed)
        String[] badWords = {
            // Add your server's blacklisted words here
            // This is just a placeholder example
        };
        
        for (String badWord : badWords) {
            if (lower.contains(badWord)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Validate a positive integer
     */
    public static boolean isPositiveInteger(int value) {
        return value > 0;
    }
    
    /**
     * Validate a non-negative integer
     */
    public static boolean isNonNegativeInteger(int value) {
        return value >= 0;
    }
    
    /**
     * Validate an integer within a range
     */
    public static boolean isInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }
    
    /**
     * Validate a double within a range
     */
    public static boolean isInRange(double value, double min, double max) {
        return value >= min && value <= max;
    }
    
    /**
     * Check if a string is null or empty
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * Validate UUID format
     */
    public static boolean isValidUUID(String uuid) {
        if (uuid == null) {
            return false;
        }
        
        Pattern uuidPattern = Pattern.compile(
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
        );
        
        return uuidPattern.matcher(uuid).matches();
    }
}

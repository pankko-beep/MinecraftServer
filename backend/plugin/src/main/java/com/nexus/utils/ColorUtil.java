package com.nexus.utils;

import org.bukkit.ChatColor;

/**
 * ColorUtil - Color and formatting utility
 * 
 * Provides utilities for working with Minecraft color codes and formatting.
 * 
 * @author Nexus Development Team
 */
public class ColorUtil {
    
    /**
     * Translate & color codes to § codes
     */
    public static String colorize(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
    
    /**
     * Strip all color codes from text
     */
    public static String stripColor(String text) {
        return ChatColor.stripColor(text);
    }
    
    /**
     * Get team color based on team name
     */
    public static ChatColor getTeamColor(String team) {
        if (team == null) {
            return ChatColor.WHITE;
        }
        
        switch (team.toUpperCase()) {
            case "SOLAR":
                return ChatColor.YELLOW;
            case "LUNAR":
                return ChatColor.DARK_PURPLE;
            default:
                return ChatColor.WHITE;
        }
    }
    
    /**
     * Get VIP color based on tier
     */
    public static ChatColor getVIPColor(String tier) {
        if (tier == null) {
            return ChatColor.GRAY;
        }
        
        switch (tier.toUpperCase()) {
            case "GUERREIRO":
                return ChatColor.GOLD;      // Bronze = Gold
            case "LORDE":
                return ChatColor.GRAY;      // Silver = Gray
            case "MAGO":
                return ChatColor.YELLOW;    // Gold = Yellow
            default:
                return ChatColor.WHITE;
        }
    }
    
    /**
     * Create a gradient text effect (simple implementation)
     */
    public static String gradient(String text, ChatColor startColor, ChatColor endColor) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        
        // Simple gradient: alternate between start and end colors
        StringBuilder result = new StringBuilder();
        int length = text.length();
        
        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            if (c == ' ') {
                result.append(c);
                continue;
            }
            
            // Simple alternation (could be improved with RGB interpolation)
            ChatColor color = (i % 2 == 0) ? startColor : endColor;
            result.append(color).append(c);
        }
        
        return result.toString();
    }
    
    /**
     * Create a rainbow text effect
     */
    public static String rainbow(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        
        ChatColor[] colors = {
            ChatColor.RED,
            ChatColor.GOLD,
            ChatColor.YELLOW,
            ChatColor.GREEN,
            ChatColor.AQUA,
            ChatColor.BLUE,
            ChatColor.LIGHT_PURPLE
        };
        
        StringBuilder result = new StringBuilder();
        int colorIndex = 0;
        
        for (char c : text.toCharArray()) {
            if (c == ' ') {
                result.append(c);
                continue;
            }
            
            result.append(colors[colorIndex % colors.length]).append(c);
            colorIndex++;
        }
        
        return result.toString();
    }
    
    /**
     * Format a header with borders
     */
    public static String header(String title, ChatColor color, int width) {
        String border = repeat("═", width);
        String paddedTitle = center(title, width);
        
        return color + "╔" + border + "╗\n" +
               color + "║" + ChatColor.BOLD + paddedTitle + ChatColor.RESET + color + "║\n" +
               color + "╚" + border + "╝";
    }
    
    /**
     * Format a simple header
     */
    public static String header(String title) {
        return header(title, ChatColor.GOLD, 40);
    }
    
    /**
     * Center text within a width
     */
    public static String center(String text, int width) {
        if (text == null) {
            text = "";
        }
        
        String stripped = stripColor(text);
        int padding = (width - stripped.length()) / 2;
        
        if (padding <= 0) {
            return text;
        }
        
        return repeat(" ", padding) + text + repeat(" ", width - padding - stripped.length());
    }
    
    /**
     * Repeat a string n times
     */
    public static String repeat(String str, int times) {
        if (times <= 0) {
            return "";
        }
        
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < times; i++) {
            builder.append(str);
        }
        return builder.toString();
    }
    
    /**
     * Create a line separator
     */
    public static String separator(ChatColor color, int width) {
        return color + repeat("─", width);
    }
    
    /**
     * Create a line separator with default settings
     */
    public static String separator() {
        return separator(ChatColor.GRAY, 40);
    }
    
    /**
     * Parse a color name to ChatColor
     */
    public static ChatColor parseColor(String colorName) {
        if (colorName == null) {
            return ChatColor.WHITE;
        }
        
        try {
            return ChatColor.valueOf(colorName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ChatColor.WHITE;
        }
    }
    
    /**
     * Get a color for a percentage value (0-100)
     * - 0-33%: Red
     * - 34-66%: Yellow
     * - 67-100%: Green
     */
    public static ChatColor getPercentageColor(double percentage) {
        if (percentage < 0) {
            return ChatColor.DARK_RED;
        } else if (percentage <= 33) {
            return ChatColor.RED;
        } else if (percentage <= 66) {
            return ChatColor.YELLOW;
        } else if (percentage <= 100) {
            return ChatColor.GREEN;
        } else {
            return ChatColor.DARK_GREEN;
        }
    }
}

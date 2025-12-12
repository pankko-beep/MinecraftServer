package com.nexus.utils;

import com.nexus.NexusPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * MessageUtil - Message formatting and localization utility
 * 
 * Provides consistent message formatting across the plugin.
 * Handles placeholders, color codes, and localized messages from config.
 * 
 * @author Nexus Development Team
 */
public class MessageUtil {
    
    private final NexusPlugin plugin;
    private final DecimalFormat moneyFormat;
    private String prefix;
    
    public MessageUtil(NexusPlugin plugin) {
        this.plugin = plugin;
        this.moneyFormat = new DecimalFormat("#,##0.00");
        this.prefix = ChatColor.translateAlternateColorCodes('&', 
            plugin.getConfig().getString("mensagens.prefixo", "§7[§bNexus§7] §r"));
    }
    
    /**
     * Send a message to a player with prefix
     */
    public void send(CommandSender sender, String messageKey, Map<String, String> placeholders) {
        String message = getMessage(messageKey, placeholders);
        if (message != null && !message.isEmpty()) {
            sender.sendMessage(prefix + message);
        }
    }
    
    /**
     * Send a message to a player with prefix (no placeholders)
     */
    public void send(CommandSender sender, String messageKey) {
        send(sender, messageKey, new HashMap<>());
    }
    
    /**
     * Send a raw message without prefix
     */
    public void sendRaw(CommandSender sender, String message) {
        sender.sendMessage(colorize(message));
    }
    
    /**
     * Get a message from config with placeholders replaced
     */
    public String getMessage(String messageKey, Map<String, String> placeholders) {
        String message = plugin.getConfig().getString("mensagens." + messageKey, null);
        
        if (message == null) {
            plugin.getLogger().warning("Missing message key: mensagens." + messageKey);
            return "§c[Missing message: " + messageKey + "]";
        }
        
        // Replace placeholders
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace("%" + entry.getKey() + "%", entry.getValue());
        }
        
        return colorize(message);
    }
    
    /**
     * Get a message from config (no placeholders)
     */
    public String getMessage(String messageKey) {
        return getMessage(messageKey, new HashMap<>());
    }
    
    /**
     * Colorize a string (convert & codes to color codes)
     */
    public String colorize(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
    
    /**
     * Format a money value consistently
     */
    public String formatMoney(double amount) {
        String symbol = plugin.getConfig().getString("moeda.simbolo", "§e⛃");
        String name = plugin.getConfig().getString("moeda.nome", "moedas");
        String format = plugin.getConfig().getString("moeda.formato", "%simbolo%%valor% %nome%");
        
        return format
            .replace("%simbolo%", symbol)
            .replace("%valor%", moneyFormat.format(amount))
            .replace("%nome%", name);
    }
    
    /**
     * Format time in seconds to human-readable format
     * Example: 3665 seconds -> "1h 1m 5s"
     */
    public String formatTime(long seconds) {
        if (seconds < 60) {
            return seconds + "s";
        }
        
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;
        
        if (minutes < 60) {
            return remainingSeconds > 0 
                ? "%dm %ds".formatted(minutes, remainingSeconds)
                : "%dm".formatted(minutes);
        }
        
        long hours = minutes / 60;
        long remainingMinutes = minutes % 60;
        
        if (hours < 24) {
            return remainingMinutes > 0
                ? "%dh %dm".formatted(hours, remainingMinutes)
                : "%dh".formatted(hours);
        }
        
        long days = hours / 24;
        long remainingHours = hours % 24;
        
        return remainingHours > 0
            ? "%dd %dh".formatted(days, remainingHours)
            : "%dd".formatted(days);
    }
    
    /**
     * Format a timestamp to human-readable format
     */
    public String formatTimestamp(long timestamp) {
        long seconds = (System.currentTimeMillis() - timestamp) / 1000;
        
        if (seconds < 60) {
            return "agora mesmo";
        }
        
        if (seconds < 3600) {
            long minutes = seconds / 60;
            return minutes + " minuto" + (minutes > 1 ? "s" : "") + " atrás";
        }
        
        if (seconds < 86400) {
            long hours = seconds / 3600;
            return hours + " hora" + (hours > 1 ? "s" : "") + " atrás";
        }
        
        long days = seconds / 86400;
        return days + " dia" + (days > 1 ? "s" : "") + " atrás";
    }
    
    /**
     * Send a success message
     */
    public void success(CommandSender sender, String message) {
        sender.sendMessage(prefix + "§a✓ " + colorize(message));
    }
    
    /**
     * Send an error message
     */
    public void error(CommandSender sender, String message) {
        sender.sendMessage(prefix + "§c✗ " + colorize(message));
    }
    
    /**
     * Send a warning message
     */
    public void warn(CommandSender sender, String message) {
        sender.sendMessage(prefix + "§e⚠ " + colorize(message));
    }
    
    /**
     * Send an info message
     */
    public void info(CommandSender sender, String message) {
        sender.sendMessage(prefix + "§b§ " + colorize(message));
    }
    
    /**
     * Send "no permission" message
     */
    public void noPermission(CommandSender sender) {
        send(sender, "sem-permissao");
    }
    
    /**
     * Send "player only" message
     */
    public void playerOnly(CommandSender sender) {
        send(sender, "apenas-jogador");
    }
    
    /**
     * Send "player not found" message
     */
    public void playerNotFound(CommandSender sender, String playerName) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("jogador", playerName);
        send(sender, "jogador-nao-encontrado", placeholders);
    }
    
    /**
     * Broadcast a message to all online players
     */
    public void broadcast(String message) {
        plugin.getServer().broadcastMessage(prefix + colorize(message));
    }
    
    /**
     * Broadcast a message to all players with a specific permission
     */
    public void broadcastPermission(String permission, String message) {
        String formatted = prefix + colorize(message);
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (player.hasPermission(permission)) {
                player.sendMessage(formatted);
            }
        }
    }
    
    /**
     * Create a progress bar
     * Example: [████████░░] 80%
     */
    public String progressBar(double current, double max, int bars, char symbol, ChatColor completeColor, ChatColor incompleteColor) {
        double percent = Math.min(1.0, Math.max(0.0, current / max));
        int completeBars = (int) Math.round(bars * percent);
        int incompleteBars = bars - completeBars;
        
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        builder.append(completeColor);
        for (int i = 0; i < completeBars; i++) {
            builder.append(symbol);
        }
        builder.append(incompleteColor);
        for (int i = 0; i < incompleteBars; i++) {
            builder.append(symbol);
        }
        builder.append("§r] ");
        builder.append("%.0f%%".formatted(percent * 100));
        
        return builder.toString();
    }
    
    /**
     * Create a simple progress bar with default settings
     */
    public String progressBar(double current, double max) {
        return progressBar(current, max, 10, '█', ChatColor.GREEN, ChatColor.GRAY);
    }
    
    /**
     * Reload messages from config
     */
    public void reload() {
        this.prefix = ChatColor.translateAlternateColorCodes('&', 
            plugin.getConfig().getString("mensagens.prefixo", "§7[§bNexus§7] §r"));
    }
}

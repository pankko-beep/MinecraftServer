package com.nexus.models;

import org.bukkit.Location;

/**
 * Panel - Holographic panel/HUD model
 * 
 * Represents a holographic display panel (GLOBAL, TEAM, or GUILD).
 * 
 * @author Nexus Development Team
 */
public class Panel {
    
    public enum Type {
        GLOBAL,       // Server-wide statistics
        TEAM,         // Team-specific rankings
        GUILD         // Guild-specific information
    }
    
    private int id;
    private Type type;
    private Location location;
    private Integer guildId;   // Nullable (only for GUILD panels)
    private String team;       // Nullable (only for TEAM panels)
    private String data;       // JSON data for custom panel content
    private long createdAt;
    
    // Constructor
    public Panel(Type type, Location location) {
        this.type = type;
        this.location = location;
        this.guildId = null;
        this.team = null;
        this.data = null;
        this.createdAt = System.currentTimeMillis();
    }
    
    // Full constructor (from database)
    public Panel(int id, Type type, Location location, Integer guildId, String team, String data, long createdAt) {
        this.id = id;
        this.type = type;
        this.location = location;
        this.guildId = guildId;
        this.team = team;
        this.data = data;
        this.createdAt = createdAt;
    }
    
    // Getters
    public int getId() { return id; }
    public Type getType() { return type; }
    public Location getLocation() { return location; }
    public Integer getGuildId() { return guildId; }
    public String getTeam() { return team; }
    public String getData() { return data; }
    public long getCreatedAt() { return createdAt; }
    
    // Setters
    public void setId(int id) { this.id = id; }
    public void setLocation(Location location) { this.location = location; }
    public void setGuildId(Integer guildId) { this.guildId = guildId; }
    public void setTeam(String team) { this.team = team; }
    public void setData(String data) { this.data = data; }
    
    // Utility methods
    public boolean isGlobal() {
        return type == Type.GLOBAL;
    }
    
    public boolean isTeam() {
        return type == Type.TEAM;
    }
    
    public boolean isGuild() {
        return type == Type.GUILD;
    }
    
    public String getLocationString() {
        if (location == null) return "unknown";
        return String.format("%s,%.1f,%.1f,%.1f", 
            location.getWorld().getName(), 
            location.getX(), 
            location.getY(), 
            location.getZ());
    }
    
    @Override
    public String toString() {
        return String.format("Panel{id=%d, type=%s, location=%s}", id, type, getLocationString());
    }
}

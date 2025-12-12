package com.nexus.models;

/**
 * Team - Team data model
 * 
 * Represents a team in the Solar/Lunar system.
 * 
 * @author Nexus Development Team
 */
public class Team {
    
    private String name;           // SOLAR or LUNAR
    private int points;
    private int totalMembers;
    private long createdAt;
    
    // Constructor
    public Team(String name) {
        this.name = name;
        this.points = 0;
        this.totalMembers = 0;
        this.createdAt = System.currentTimeMillis();
    }
    
    // Full constructor
    public Team(String name, int points, int totalMembers, long createdAt) {
        this.name = name;
        this.points = points;
        this.totalMembers = totalMembers;
        this.createdAt = createdAt;
    }
    
    // Getters
    public String getName() { return name; }
    public int getPoints() { return points; }
    public int getTotalMembers() { return totalMembers; }
    public long getCreatedAt() { return createdAt; }
    
    // Setters
    public void setPoints(int points) { this.points = points; }
    public void setTotalMembers(int totalMembers) { this.totalMembers = totalMembers; }
    
    // Utility methods
    public void addPoints(int amount) {
        this.points += amount;
    }
    
    public void subtractPoints(int amount) {
        this.points = Math.max(0, this.points - amount);
    }
    
    public void incrementMembers() {
        this.totalMembers++;
    }
    
    public void decrementMembers() {
        this.totalMembers = Math.max(0, this.totalMembers - 1);
    }
    
    public boolean isSolar() {
        return "SOLAR".equalsIgnoreCase(name);
    }
    
    public boolean isLunar() {
        return "LUNAR".equalsIgnoreCase(name);
    }
    
    @Override
    public String toString() {
        return "Team{name=%s, points=%d, members=%d}".formatted(name, points, totalMembers);
    }
}

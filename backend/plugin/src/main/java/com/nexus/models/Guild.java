package com.nexus.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Guild - Guild data model
 * 
 * Represents a guild/clan with members, cofre (treasury), and points.
 * 
 * @author Nexus Development Team
 */
public class Guild {
    
    private int id;
    private String name;
    private String team;           // SOLAR or LUNAR
    private UUID leaderUUID;
    private int memberLimit;
    private double cofreBalance;
    private int points;
    private long createdAt;
    
    // In-memory member list (loaded separately)
    private List<UUID> members;
    
    // Constructor
    public Guild(int id, String name, String team, UUID leaderUUID) {
        this.id = id;
        this.name = name;
        this.team = team;
        this.leaderUUID = leaderUUID;
        this.memberLimit = 20;
        this.cofreBalance = 0.0;
        this.points = 0;
        this.createdAt = System.currentTimeMillis();
        this.members = new ArrayList<>();
    }
    
    // Full constructor
    public Guild(int id, String name, String team, UUID leaderUUID, int memberLimit, 
                 double cofreBalance, int points, long createdAt) {
        this.id = id;
        this.name = name;
        this.team = team;
        this.leaderUUID = leaderUUID;
        this.memberLimit = memberLimit;
        this.cofreBalance = cofreBalance;
        this.points = points;
        this.createdAt = createdAt;
        this.members = new ArrayList<>();
    }
    
    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getTeam() { return team; }
    public UUID getLeaderUUID() { return leaderUUID; }
    public int getMemberLimit() { return memberLimit; }
    public double getCofreBalance() { return cofreBalance; }
    public int getPoints() { return points; }
    public long getCreatedAt() { return createdAt; }
    public List<UUID> getMembers() { return members; }
    
    // Setters
    public void setLeaderUUID(UUID leaderUUID) { this.leaderUUID = leaderUUID; }
    public void setMemberLimit(int memberLimit) { this.memberLimit = memberLimit; }
    public void setCofreBalance(double balance) { this.cofreBalance = balance; }
    public void setPoints(int points) { this.points = points; }
    public void setMembers(List<UUID> members) { this.members = members; }
    
    // Utility methods
    public boolean isFull() {
        return members.size() >= memberLimit;
    }
    
    public boolean isLeader(UUID uuid) {
        return leaderUUID.equals(uuid);
    }
    
    public boolean isMember(UUID uuid) {
        return members.contains(uuid);
    }
    
    public void addMember(UUID uuid) {
        if (!members.contains(uuid)) {
            members.add(uuid);
        }
    }
    
    public void removeMember(UUID uuid) {
        members.remove(uuid);
    }
    
    public int getMemberCount() {
        return members.size();
    }
    
    public void addPoints(int amount) {
        this.points += amount;
    }
    
    public void subtractPoints(int amount) {
        this.points = Math.max(0, this.points - amount);
    }
    
    public void depositCofre(double amount) {
        this.cofreBalance += amount;
    }
    
    public void withdrawCofre(double amount) {
        this.cofreBalance -= amount;
    }
    
    public boolean canAffordCofre(double amount) {
        return cofreBalance >= amount;
    }
    
    @Override
    public String toString() {
        return String.format("Guild{id=%d, name=%s, team=%s, leader=%s, members=%d/%d, cofre=%.2f}", 
            id, name, team, leaderUUID, members.size(), memberLimit, cofreBalance);
    }
}

package com.nexus.models;

import java.util.UUID;

/**
 * NexusPlayer - Player data model
 * 
 * Represents a player in the Nexus system with team affiliation,
 * guild membership, economy, and VIP status.
 * 
 * @author Nexus Development Team
 */
public class NexusPlayer {
    
    private UUID uuid;
    private String name;
    private String team;           // SOLAR or LUNAR
    private Integer guildId;       // Nullable
    private double balance;
    private String vipTier;        // GUERREIRO, LORDE, MAGO
    private long lastLogin;
    private long lastTeamSwitch;
    private boolean economyFrozen;
    private long createdAt;
    
    // Constructor
    public NexusPlayer(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.team = null;
        this.guildId = null;
        this.balance = 0.0;
        this.vipTier = null;
        this.lastLogin = System.currentTimeMillis();
        this.lastTeamSwitch = 0;
        this.economyFrozen = false;
        this.createdAt = System.currentTimeMillis();
    }
    
    // Getters
    public UUID getUUID() { return uuid; }
    public String getName() { return name; }
    public String getTeam() { return team; }
    public Integer getGuildId() { return guildId; }
    public double getBalance() { return balance; }
    public String getVIPTier() { return vipTier; }
    public long getLastLogin() { return lastLogin; }
    public long getLastTeamSwitch() { return lastTeamSwitch; }
    public boolean isEconomyFrozen() { return economyFrozen; }
    public long getCreatedAt() { return createdAt; }
    
    // Setters
    public void setName(String name) { this.name = name; }
    public void setTeam(String team) { this.team = team; }
    public void setGuildId(Integer guildId) { this.guildId = guildId; }
    public void setBalance(double balance) { this.balance = balance; }
    public void setVIPTier(String vipTier) { this.vipTier = vipTier; }
    public void setLastLogin(long lastLogin) { this.lastLogin = lastLogin; }
    public void setLastTeamSwitch(long lastTeamSwitch) { this.lastTeamSwitch = lastTeamSwitch; }
    public void setEconomyFrozen(boolean frozen) { this.economyFrozen = frozen; }
    
    // Utility methods
    public boolean hasTeam() {
        return team != null && !team.isEmpty();
    }
    
    public boolean hasGuild() {
        return guildId != null;
    }
    
    public boolean isVIP() {
        return vipTier != null && !vipTier.isEmpty();
    }
    
    public void addBalance(double amount) {
        this.balance += amount;
    }
    
    public void subtractBalance(double amount) {
        this.balance -= amount;
    }
    
    public boolean canAfford(double amount) {
        return balance >= amount && !economyFrozen;
    }
    
    @Override
    public String toString() {
        return "NexusPlayer{uuid=%s, name=%s, team=%s, guild=%s, balance=%.2f}".formatted(
               uuid, name, team, guildId, balance);
    }
}

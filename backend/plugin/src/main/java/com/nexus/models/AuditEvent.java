package com.nexus.models;

import java.util.UUID;

/**
 * AuditEvent - Audit log event record
 * 
 * Represents a logged event for audit trails and anti-fraud analysis.
 * 
 * @author Nexus Development Team
 */
public class AuditEvent {
    
    public enum EventType {
        PLAYER_JOIN,
        PLAYER_QUIT,
        MONEY_TRANSFER,
        MONEY_DEPOSIT,
        MONEY_WITHDRAW,
        TEAM_CHOOSE,
        TEAM_SWITCH,
        GUILD_CREATE,
        GUILD_JOIN,
        GUILD_LEAVE,
        GUILD_KICK,
        GUILD_PROMOTE,
        GUILD_DEMOTE,
        NEXUS_BUILD,
        NEXUS_DESTROY,
        NEXUS_UPGRADE,
        NEXUS_DAMAGE,
        SHIELD_ACTIVATE,
        SHIELD_EXPIRE,
        OBJECTIVE_START,
        OBJECTIVE_COMPLETE,
        OBJECTIVE_FAIL,
        PANEL_CREATE,
        PANEL_DELETE,
        VIP_PURCHASE,
        VIP_REWARD_CLAIM,
        MARKET_LIST,
        MARKET_BUY,
        MARKET_SELL,
        ADMIN_COMMAND,
        SUSPICIOUS_ACTIVITY,
        ECONOMY_FREEZE,
        ECONOMY_UNFREEZE
    }
    
    private int id;
    private UUID playerUUID;
    private EventType eventType;
    private String details;
    private long timestamp;
    private String ipAddress;
    
    // Constructor
    public AuditEvent(UUID playerUUID, EventType eventType, String details) {
        this.playerUUID = playerUUID;
        this.eventType = eventType;
        this.details = details;
        this.timestamp = System.currentTimeMillis();
        this.ipAddress = null;
    }
    
    // Full constructor (from database)
    public AuditEvent(int id, UUID playerUUID, EventType eventType, String details, long timestamp, String ipAddress) {
        this.id = id;
        this.playerUUID = playerUUID;
        this.eventType = eventType;
        this.details = details;
        this.timestamp = timestamp;
        this.ipAddress = ipAddress;
    }
    
    // Getters
    public int getId() { return id; }
    public UUID getPlayerUUID() { return playerUUID; }
    public EventType getEventType() { return eventType; }
    public String getDetails() { return details; }
    public long getTimestamp() { return timestamp; }
    public String getIpAddress() { return ipAddress; }
    
    // Setters
    public void setId(int id) { this.id = id; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    
    // Utility methods
    public boolean isSuspicious() {
        return eventType == EventType.SUSPICIOUS_ACTIVITY;
    }
    
    public boolean isEconomyRelated() {
        return eventType == EventType.MONEY_TRANSFER || 
               eventType == EventType.MONEY_DEPOSIT || 
               eventType == EventType.MONEY_WITHDRAW ||
               eventType == EventType.ECONOMY_FREEZE ||
               eventType == EventType.ECONOMY_UNFREEZE;
    }
    
    public boolean isGuildRelated() {
        return eventType.name().startsWith("GUILD_");
    }
    
    @Override
    public String toString() {
        return String.format("AuditEvent{id=%d, player=%s, type=%s, time=%d}", 
            id, playerUUID, eventType, timestamp);
    }
}

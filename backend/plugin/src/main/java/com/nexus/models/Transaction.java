package com.nexus.models;

import java.util.UUID;

/**
 * Transaction - Financial transaction record
 * 
 * Represents a money transfer between players or system accounts.
 * 
 * @author Nexus Development Team
 */
public class Transaction {
    
    public enum Type {
        PLAYER_TO_PLAYER,
        PLAYER_TO_GUILD,
        GUILD_TO_PLAYER,
        SYSTEM_REWARD,
        SYSTEM_PENALTY,
        TEAM_SWITCH_FEE,
        GUILD_CREATION_FEE,
        NEXUS_BUILD_COST,
        NEXUS_UPGRADE_COST,
        SHIELD_ACTIVATION_COST,
        MARKET_LISTING_FEE,
        MARKET_SALE_TAX,
        MARKET_PURCHASE,
        VIP_DAILY_REWARD,
        OBJECTIVE_REWARD,
        OTHER
    }
    
    private int id;
    private UUID fromUUID;    // Null for system transactions
    private UUID toUUID;      // Null for guild deposits
    private double amount;
    private Type type;
    private String reason;
    private long timestamp;
    
    // Constructor
    public Transaction(UUID fromUUID, UUID toUUID, double amount, Type type, String reason) {
        this.fromUUID = fromUUID;
        this.toUUID = toUUID;
        this.amount = amount;
        this.type = type;
        this.reason = reason;
        this.timestamp = System.currentTimeMillis();
    }
    
    // Full constructor (from database)
    public Transaction(int id, UUID fromUUID, UUID toUUID, double amount, Type type, String reason, long timestamp) {
        this.id = id;
        this.fromUUID = fromUUID;
        this.toUUID = toUUID;
        this.amount = amount;
        this.type = type;
        this.reason = reason;
        this.timestamp = timestamp;
    }
    
    // Getters
    public int getId() { return id; }
    public UUID getFromUUID() { return fromUUID; }
    public UUID getToUUID() { return toUUID; }
    public double getAmount() { return amount; }
    public Type getType() { return type; }
    public String getReason() { return reason; }
    public long getTimestamp() { return timestamp; }
    
    // Setters
    public void setId(int id) { this.id = id; }
    
    // Utility methods
    public boolean isPlayerToPlayer() {
        return type == Type.PLAYER_TO_PLAYER;
    }
    
    public boolean isSystemTransaction() {
        return fromUUID == null || toUUID == null;
    }
    
    public boolean involvesPlayer(UUID uuid) {
        return (fromUUID != null && fromUUID.equals(uuid)) || 
               (toUUID != null && toUUID.equals(uuid));
    }
    
    @Override
    public String toString() {
        return "Transaction{id=%d, from=%s, to=%s, amount=%.2f, type=%s}".formatted(
               id, fromUUID, toUUID, amount, type);
    }
}

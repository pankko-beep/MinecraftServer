package com.nexus.models;

/**
 * Shield - Guild shield data model
 * 
 * Represents a guild's protective shield with states and timers.
 * 
 * @author Nexus Development Team
 */
public class Shield {
    
    public enum State {
        INACTIVE,     // Not active, can be activated
        WARMUP,       // Warming up (5 minutes)
        ACTIVE,       // Fully active, guild is protected
        EXPIRED,      // Expired, in cooldown
        COOLDOWN      // Same as EXPIRED, in cooldown
    }
    
    private int guildId;
    private State state;
    private long activatedAt;
    private long expiresAt;
    private long lastUsed;
    
    // Constructor
    public Shield(int guildId) {
        this.guildId = guildId;
        this.state = State.INACTIVE;
        this.activatedAt = 0;
        this.expiresAt = 0;
        this.lastUsed = 0;
    }
    
    // Full constructor
    public Shield(int guildId, State state, long activatedAt, long expiresAt, long lastUsed) {
        this.guildId = guildId;
        this.state = state;
        this.activatedAt = activatedAt;
        this.expiresAt = expiresAt;
        this.lastUsed = lastUsed;
    }
    
    // Getters
    public int getGuildId() { return guildId; }
    public State getState() { return state; }
    public long getActivatedAt() { return activatedAt; }
    public long getExpiresAt() { return expiresAt; }
    public long getLastUsed() { return lastUsed; }
    
    // Setters
    public void setState(State state) { this.state = state; }
    public void setActivatedAt(long activatedAt) { this.activatedAt = activatedAt; }
    public void setExpiresAt(long expiresAt) { this.expiresAt = expiresAt; }
    public void setLastUsed(long lastUsed) { this.lastUsed = lastUsed; }
    
    // Utility methods
    public boolean isActive() {
        return state == State.ACTIVE;
    }
    
    public boolean isWarmingUp() {
        return state == State.WARMUP;
    }
    
    public boolean isInactive() {
        return state == State.INACTIVE;
    }
    
    public boolean isInCooldown() {
        return state == State.COOLDOWN || state == State.EXPIRED;
    }
    
    public boolean isProtecting() {
        return state == State.ACTIVE;
    }
    
    public long getRemainingTime() {
        if (expiresAt == 0) return 0;
        return Math.max(0, expiresAt - System.currentTimeMillis());
    }
    
    public long getRemainingTimeSeconds() {
        return getRemainingTime() / 1000;
    }
    
    public boolean hasExpired() {
        if (expiresAt == 0) return false;
        return System.currentTimeMillis() >= expiresAt;
    }
    
    public void activate(int warmupSeconds, int activeDurationSeconds) {
        this.state = State.WARMUP;
        this.activatedAt = System.currentTimeMillis();
        this.expiresAt = activatedAt + ((warmupSeconds + activeDurationSeconds) * 1000L);
    }
    
    public void completeWarmup() {
        if (state == State.WARMUP) {
            this.state = State.ACTIVE;
        }
    }
    
    public void expire() {
        this.state = State.EXPIRED;
        this.lastUsed = System.currentTimeMillis();
    }
    
    public void deactivate() {
        this.state = State.INACTIVE;
        this.activatedAt = 0;
        this.expiresAt = 0;
    }
    
    @Override
    public String toString() {
        return "Shield{guild=%d, state=%s, remaining=%ds}".formatted(
               guildId, state, getRemainingTimeSeconds());
    }
}

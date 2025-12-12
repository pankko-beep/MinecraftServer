package com.nexus.models;

import org.bukkit.Location;

/**
 * Nexus - Guild heart data model
 * 
 * Represents a guild's Nexus (heart) with health, level, and state.
 * 
 * @author Nexus Development Team
 */
public class Nexus {
    
    public enum State {
        ACTIVE,          // Fully operational
        UNDER_ATTACK,    // Being sieged
        DESTROYED,       // Health reached 0
        CONSTRUCTION     // Being rebuilt
    }
    
    private int guildId;
    private int level;
    private double health;
    private double maxHealth;
    private State state;
    private Location location;
    private long lastDestroyed;
    private long createdAt;
    
    // Constructor
    public Nexus(int guildId, Location location) {
        this.guildId = guildId;
        this.level = 1;
        this.maxHealth = 10000.0;
        this.health = maxHealth;
        this.state = State.ACTIVE;
        this.location = location;
        this.lastDestroyed = 0;
        this.createdAt = System.currentTimeMillis();
    }
    
    // Full constructor
    public Nexus(int guildId, int level, double health, double maxHealth, State state, 
                 Location location, long lastDestroyed, long createdAt) {
        this.guildId = guildId;
        this.level = level;
        this.health = health;
        this.maxHealth = maxHealth;
        this.state = state;
        this.location = location;
        this.lastDestroyed = lastDestroyed;
        this.createdAt = createdAt;
    }
    
    // Getters
    public int getGuildId() { return guildId; }
    public int getLevel() { return level; }
    public double getHealth() { return health; }
    public double getMaxHealth() { return maxHealth; }
    public State getState() { return state; }
    public Location getLocation() { return location; }
    public long getLastDestroyed() { return lastDestroyed; }
    public long getCreatedAt() { return createdAt; }
    
    // Setters
    public void setLevel(int level) { this.level = level; }
    public void setHealth(double health) { this.health = health; }
    public void setMaxHealth(double maxHealth) { this.maxHealth = maxHealth; }
    public void setState(State state) { this.state = state; }
    public void setLocation(Location location) { this.location = location; }
    public void setLastDestroyed(long lastDestroyed) { this.lastDestroyed = lastDestroyed; }
    
    // Utility methods
    public boolean isActive() {
        return state == State.ACTIVE;
    }
    
    public boolean isDestroyed() {
        return state == State.DESTROYED;
    }
    
    public boolean isUnderAttack() {
        return state == State.UNDER_ATTACK;
    }
    
    public void damage(double amount) {
        this.health = Math.max(0, this.health - amount);
        if (this.health == 0) {
            this.state = State.DESTROYED;
            this.lastDestroyed = System.currentTimeMillis();
        }
    }
    
    public void heal(double amount) {
        this.health = Math.min(maxHealth, this.health + amount);
    }
    
    public void upgrade() {
        this.level++;
        this.maxHealth *= 1.2; // 20% more health per level
        this.health = maxHealth; // Fully heal on upgrade
    }
    
    public double getHealthPercentage() {
        if (maxHealth == 0) return 0;
        return (health / maxHealth) * 100.0;
    }
    
    @Override
    public String toString() {
        return "Nexus{guild=%d, level=%d, health=%.0f/%.0f (%.0f%%), state=%s}".formatted(
               guildId, level, health, maxHealth, getHealthPercentage(), state);
    }
}

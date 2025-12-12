package com.nexus.models;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Objective - Dynamic objective model
 * 
 * Represents a PVE/PVP/Exploration/Support objective with participants and progress.
 * 
 * @author Nexus Development Team
 */
public class Objective {
    
    public enum Category {
        PVE,          // Kill mobs
        PVP,          // Kill players
        EXPLORACAO,   // Explore, mine, farm
        SUPORTE       // Heal, assist teammates
    }
    
    public enum Difficulty {
        FACIL,
        MEDIO,
        DIFICIL,
        EXTREMO
    }
    
    public enum State {
        ACTIVE,
        COMPLETED,
        FAILED,
        EXPIRED
    }
    
    private int id;
    private String name;
    private String description;
    private Category category;
    private Difficulty difficulty;
    private double reward;
    private State state;
    private int progress;
    private int goal;
    private long createdAt;
    private long completedAt;
    
    // Participant contributions (UUID -> contribution amount)
    private Map<UUID, Integer> participants;
    
    // Constructor
    public Objective(String name, String description, Category category, Difficulty difficulty, int goal, double reward) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.difficulty = difficulty;
        this.goal = goal;
        this.reward = reward;
        this.state = State.ACTIVE;
        this.progress = 0;
        this.createdAt = System.currentTimeMillis();
        this.completedAt = 0;
        this.participants = new HashMap<>();
    }
    
    // Full constructor (from database)
    public Objective(int id, String name, String description, Category category, Difficulty difficulty, 
                     double reward, State state, int progress, int goal, long createdAt, long completedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.difficulty = difficulty;
        this.reward = reward;
        this.state = state;
        this.progress = progress;
        this.goal = goal;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
        this.participants = new HashMap<>();
    }
    
    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Category getCategory() { return category; }
    public Difficulty getDifficulty() { return difficulty; }
    public double getReward() { return reward; }
    public State getState() { return state; }
    public int getProgress() { return progress; }
    public int getGoal() { return goal; }
    public long getCreatedAt() { return createdAt; }
    public long getCompletedAt() { return completedAt; }
    public Map<UUID, Integer> getParticipants() { return participants; }
    
    // Setters
    public void setId(int id) { this.id = id; }
    public void setState(State state) { this.state = state; }
    public void setProgress(int progress) { this.progress = progress; }
    public void setCompletedAt(long completedAt) { this.completedAt = completedAt; }
    
    // Utility methods
    public boolean isActive() {
        return state == State.ACTIVE;
    }
    
    public boolean isCompleted() {
        return state == State.COMPLETED;
    }
    
    public void addProgress(UUID playerUUID, int amount) {
        this.progress += amount;
        participants.put(playerUUID, participants.getOrDefault(playerUUID, 0) + amount);
        
        if (progress >= goal) {
            complete();
        }
    }
    
    public void complete() {
        this.state = State.COMPLETED;
        this.completedAt = System.currentTimeMillis();
    }
    
    public void fail() {
        this.state = State.FAILED;
    }
    
    public double getProgressPercentage() {
        if (goal == 0) return 0;
        return ((double) progress / goal) * 100.0;
    }
    
    public int getTotalParticipants() {
        return participants.size();
    }
    
    public int getPlayerContribution(UUID playerUUID) {
        return participants.getOrDefault(playerUUID, 0);
    }
    
    public double getPlayerRewardShare(UUID playerUUID) {
        int contribution = getPlayerContribution(playerUUID);
        if (contribution == 0 || progress == 0) return 0;
        
        double share = (double) contribution / progress;
        return reward * share;
    }
    
    @Override
    public String toString() {
        return String.format("Objective{id=%d, name=%s, category=%s, progress=%d/%d (%.0f%%), state=%s}", 
            id, name, category, progress, goal, getProgressPercentage(), state);
    }
}

package com.nexus;

import com.nexus.services.DatabaseService;
import com.nexus.utils.ConfigManager;
import com.nexus.utils.MessageUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

/**
 * Nexus - Advanced Minecraft Server Plugin
 * 
 * Main plugin class for the Nexus server ecosystem, providing:
 * - Solar/Lunar team system with mandatory selection
 * - Guild system with Nexus (heart) and Shield mechanics
 * - Vault-integrated economy with anti-fraud measures
 * - Dynamic objectives (PVE/PVP/Exploration/Support)
 * - Holographic panel system
 * - VIP tier system with rewards
 * - Market/auction system
 * - Comprehensive audit and transaction logging
 * 
 * @author Nexus Development Team
 * @version 1.0.0
 */
public class NexusPlugin extends JavaPlugin {
    
    // ========================================
    // SINGLETON INSTANCE
    // ========================================
    private static NexusPlugin instance;
    
    // ========================================
    // SERVICE LAYER (Only DatabaseService implemented for now)
    // ========================================
    private DatabaseService databaseService;
    
    // TODO: Implement remaining services
    // private EconomyService economyService;
    // private TeamService teamService;
    // private GuildService guildService;
    // private NexusService nexusService;
    // private ShieldService shieldService;
    // private ObjectiveService objectiveService;
    // private PanelService panelService;
    // private VIPService vipService;
    // private TransactionService transactionService;
    // private AuditService auditService;
    // private MarketService marketService;
    
    // ========================================
    // UTILITIES
    // ========================================
    private ConfigManager configManager;
    private MessageUtil messageUtil;
    
    // ========================================
    // EXTERNAL DEPENDENCIES
    // ========================================
    private Economy vaultEconomy;
    
    // ========================================
    // LIFECYCLE: ENABLE
    // ========================================
    @Override
    public void onEnable() {
        instance = this;
        
        long startTime = System.currentTimeMillis();
        getLogger().info("========================================");
        getLogger().info("Nexus Plugin - Initializing...");
        getLogger().info("========================================");
        
        // Phase 1: Configuration & Utilities
        if (!initializeConfiguration()) {
            getLogger().severe("Failed to initialize configuration! Disabling plugin...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        
        // Phase 2: External Dependencies (Vault)
        if (!setupVault()) {
            getLogger().warning("Vault not found! Economy features will be limited.");
        }
        
        // Phase 3: Database & Core Services
        if (!initializeCoreServices()) {
            getLogger().severe("Failed to initialize core services! Disabling plugin...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        
        // TODO: Phase 4: Feature Services (not yet implemented)
        // initializeFeatureServices();
        
        // TODO: Phase 5: Commands (not yet implemented)
        // registerCommands();
        
        // TODO: Phase 6: Event Listeners (not yet implemented)
        // registerListeners();
        
        // TODO: Phase 7: Scheduled Tasks (not yet implemented)
        // startScheduledTasks();
        
        long loadTime = System.currentTimeMillis() - startTime;
        getLogger().info("========================================");
        getLogger().info(String.format("Nexus Plugin - Enabled (took %dms)", loadTime));
        getLogger().info("NOTE: Core infrastructure loaded. Services/Commands not yet implemented.");
        getLogger().info("========================================");
    }
    
    // ========================================
    // LIFECYCLE: DISABLE
    // ========================================
    @Override
    public void onDisable() {
        getLogger().info("========================================");
        getLogger().info("Nexus Plugin - Shutting down...");
        getLogger().info("========================================");
        
        // TODO: Save all pending data when services are implemented
        
        // Close database connections
        if (databaseService != null) {
            databaseService.close();
        }
        
        getLogger().info("Nexus Plugin - Disabled successfully.");
    }
    
    // ========================================
    // INITIALIZATION: CONFIGURATION
    // ========================================
    private boolean initializeConfiguration() {
        try {
            // Save default config if not exists
            saveDefaultConfig();
            
            // Initialize config manager
            configManager = new ConfigManager(this);
            messageUtil = new MessageUtil(this);
            
            getLogger().info("✓ Configuration loaded successfully.");
            return true;
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed to load configuration!", e);
            return false;
        }
    }
    
    // ========================================
    // INITIALIZATION: VAULT INTEGRATION
    // ========================================
    private boolean setupVault() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            getLogger().warning("Vault found but no economy provider detected!");
            return false;
        }
        
        vaultEconomy = rsp.getProvider();
        getLogger().info(String.format("✓ Vault economy integration successful: %s", vaultEconomy.getName()));
        return true;
    }
    
    // ========================================
    // INITIALIZATION: CORE SERVICES
    // ========================================
    private boolean initializeCoreServices() {
        try {
            // Database (must be first)
            getLogger().info("Initializing DatabaseService...");
            databaseService = new DatabaseService(this);
            if (!databaseService.connect()) {
                getLogger().severe("Failed to connect to database!");
                return false;
            }
            getLogger().info("✓ DatabaseService initialized.");
            
            // TODO: Initialize remaining services
            // - EconomyService (depends on Vault + Database)
            // - TransactionService (depends on Database)
            // - AuditService (depends on Database)
            // See IMPLEMENTATION_STATUS.md for implementation order
            
            return true;
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed to initialize core services!", e);
            return false;
        }
    }
    
    // ========================================
    // SINGLETON ACCESSOR
    // ========================================
    public static NexusPlugin getInstance() {
        return instance;
    }
    
    // ========================================
    // SERVICE ACCESSORS
    // ========================================
    public DatabaseService getDatabaseService() { return databaseService; }
    
    // TODO: Implement getters for remaining services when they are created
    // public EconomyService getEconomyService() { return economyService; }
    // public TeamService getTeamService() { return teamService; }
    // public GuildService getGuildService() { return guildService; }
    // ... etc
    
    // ========================================
    // UTILITY ACCESSORS
    // ========================================
    public ConfigManager getConfigManager() { return configManager; }
    public MessageUtil getMessageUtil() { return messageUtil; }
    public Economy getVaultEconomy() { return vaultEconomy; }
}

package com.minecraftserver.customplugin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("CustomPlugin has been enabled!");
        
        // Load configuration
        saveDefaultConfig();
        
        // Register commands and events here
        getCommand("hello").setExecutor(new HelloCommand());
        
        getLogger().info("CustomPlugin initialized successfully");
    }

    @Override
    public void onDisable() {
        getLogger().info("CustomPlugin has been disabled!");
    }
}

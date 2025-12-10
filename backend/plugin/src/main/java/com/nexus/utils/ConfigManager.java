package com.nexus.utils;

import com.nexus.NexusPlugin;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

/**
 * ConfigManager - Configuration access utility
 * 
 * Provides type-safe access to config.yml values with defaults.
 * Centralizes configuration logic to avoid scattered getConfig() calls.
 * 
 * @author Nexus Development Team
 */
public class ConfigManager {
    
    private final NexusPlugin plugin;
    private FileConfiguration config;
    
    public ConfigManager(NexusPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }
    
    /**
     * Reload configuration from disk
     */
    public void reload() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }
    
    // ========================================
    // MODULE TOGGLES
    // ========================================
    public boolean isModuleEnabled(String module) {
        return config.getBoolean("modulos." + module, true);
    }
    
    // ========================================
    // ECONOMY
    // ========================================
    public String getCurrencyName() {
        return config.getString("moeda.nome", "moedas");
    }
    
    public String getCurrencySymbol() {
        return config.getString("moeda.simbolo", "§e⛃");
    }
    
    public double getStartingBalance() {
        return config.getDouble("economia.saldo-inicial", 1000.0);
    }
    
    public double getMaxBalance() {
        return config.getDouble("economia.saldo-maximo", 100000000.0);
    }
    
    public double getMaxTransferAmount() {
        return config.getDouble("economia.limites.pagar.max-por-transacao", 500000);
    }
    
    public double getMaxDailyTransfer() {
        return config.getDouble("economia.limites.pagar.max-por-dia", 2000000);
    }
    
    public int getTransferCooldown() {
        return config.getInt("economia.limites.pagar.cooldown-segundos", 10);
    }
    
    public double getTransferTaxPercent() {
        return config.getDouble("economia.limites.pagar.taxa-porcentagem", 0);
    }
    
    public boolean shouldAlertSuspiciousValues() {
        return config.getBoolean("economia.anti-fraude.alertar-valores-suspeitos", true);
    }
    
    public double getSuspiciousValueThreshold() {
        return config.getDouble("economia.anti-fraude.limite-alerta", 1000000);
    }
    
    // ========================================
    // TEAMS
    // ========================================
    public boolean isTeamSelectionMandatory() {
        return config.getBoolean("times.escolha-obrigatoria", true);
    }
    
    public boolean shouldBlockMovementWithoutTeam() {
        return config.getBoolean("times.bloquear-movimento-sem-time", true);
    }
    
    public double getTeamSwitchCost() {
        return config.getDouble("times.custo-troca", 1000000);
    }
    
    public int getTeamSwitchCooldownDays() {
        return config.getInt("times.cooldown-troca-dias", 30);
    }
    
    public String getTeamPrefix(String team) {
        return config.getString("times.cores." + team.toLowerCase() + ".prefixo", "");
    }
    
    public String getTeamColor(String team) {
        return config.getString("times.cores." + team.toLowerCase() + ".cor-nome", "§f");
    }
    
    // ========================================
    // GUILDS
    // ========================================
    public int getDefaultGuildMemberLimit() {
        return config.getInt("guildas.limite-membros-padrao", 20);
    }
    
    public int getMaxGuildMemberLimit() {
        return config.getInt("guildas.limite-membros-maximo", 50);
    }
    
    public double getGuildCreationCost() {
        return config.getDouble("guildas.custo-criar", 50000);
    }
    
    public double getCostPerExtraMember() {
        return config.getDouble("guildas.custo-por-membro-extra", 10000);
    }
    
    public int getGuildLeaveCooldownDays() {
        return config.getInt("guildas.cooldown-sair-dias", 3);
    }
    
    // ========================================
    // NEXUS
    // ========================================
    public double getNexusBuildCost() {
        return config.getDouble("nexus.custo-construir", 500000);
    }
    
    public double getNexusRebuildMultiplier() {
        return config.getDouble("nexus.custo-reconstruir-multiplicador", 1.5);
    }
    
    public int getNexusRebuildCooldownHours() {
        return config.getInt("nexus.cooldown-reconstruir-horas", 72);
    }
    
    public int getNexusMinOnlineMembers() {
        return config.getInt("nexus.membros-online-minimo", 3);
    }
    
    public int getNexusMaxLevel() {
        return config.getInt("nexus.upgrades.max-nivel", 10);
    }
    
    public double getNexusUpgradeBaseCost() {
        return config.getDouble("nexus.upgrades.custo-base", 100000);
    }
    
    public double getNexusUpgradeCostMultiplier() {
        return config.getDouble("nexus.upgrades.multiplicador-por-nivel", 1.8);
    }
    
    // ========================================
    // SHIELD
    // ========================================
    public double getShieldActivationCost() {
        return config.getDouble("escudo.custo-ativar-base", 50000);
    }
    
    public int getShieldWarmupSeconds() {
        return config.getInt("escudo.duracao-warmup-segundos", 300);
    }
    
    public int getShieldActiveDuration() {
        return config.getInt("escudo.duracao-ativo-segundos", 3600);
    }
    
    public int getShieldCooldownHours() {
        return config.getInt("escudo.cooldown-entre-ativacoes-horas", 24);
    }
    
    // ========================================
    // SIEGE
    // ========================================
    public int getSiegeMinAttackers() {
        return config.getInt("cerco.atacantes-minimos", 5);
    }
    
    public int getSiegeMinDefenders() {
        return config.getInt("cerco.defensores-minimos", 3);
    }
    
    public double getSiegeDamagePerPlayer() {
        return config.getDouble("cerco.dano-por-jogador", 100);
    }
    
    public double getSiegeMaxTotalDamage() {
        return config.getDouble("cerco.dano-maximo-total", 10000);
    }
    
    // ========================================
    // OBJECTIVES
    // ========================================
    public int getMaxSimultaneousObjectives() {
        return config.getInt("objetivos.max-ativos-simultaneos", 10);
    }
    
    public int getObjectiveGenerationInterval() {
        return config.getInt("objetivos.intervalo-geracao-minutos", 30);
    }
    
    public double getObjectiveReward(String category) {
        return config.getDouble("objetivos.categorias." + category + ".recompensa-base", 5000);
    }
    
    public double getObjectiveDailyCapPerPlayer() {
        return config.getDouble("objetivos.caps.por-jogador-dia", 50000);
    }
    
    // ========================================
    // PANELS
    // ========================================
    public boolean useDecentHolograms() {
        return config.getBoolean("painel.usar-decent-holograms", true);
    }
    
    public int getPanelRefreshSeconds() {
        return config.getInt("painel.refresh-segundos", 30);
    }
    
    public int getTopPlayersCount() {
        return config.getInt("painel.metricas.top-jogadores", 6);
    }
    
    public int getTopGuildsCount() {
        return config.getInt("painel.metricas.top-guildas", 6);
    }
    
    // ========================================
    // VIP
    // ========================================
    public double getVIPXPBoost(String tier) {
        return config.getDouble("vip." + tier + ".xp-boost", 0);
    }
    
    public double getVIPMoneyBoost(String tier) {
        return config.getDouble("vip." + tier + ".moedas-boost", 0);
    }
    
    public double getVIPDailyReward(String tier) {
        return config.getDouble("vip." + tier + ".recompensa-diaria", 0);
    }
    
    public double getVIPRewardMultiplier(String tier) {
        return config.getDouble("vip.multiplicadores-recompensas." + tier, 1.0);
    }
    
    // ========================================
    // MARKET
    // ========================================
    public double getMarketListingFeePercent() {
        return config.getDouble("mercado.taxa-listagem-porcentagem", 5);
    }
    
    public double getMarketSaleTaxPercent() {
        return config.getDouble("mercado.taxa-venda-porcentagem", 10);
    }
    
    public int getDefaultMarketSlots() {
        return config.getInt("mercado.limite-listagens-padrao", 3);
    }
    
    public int getMarketExpirationDays() {
        return config.getInt("mercado.tempo-expiracao-dias", 7);
    }
    
    // ========================================
    // AUDIT
    // ========================================
    public int getAuditHistoryDays() {
        return config.getInt("auditoria.historico-por-jogador", 30);
    }
    
    public int getAuditSaveInterval() {
        return config.getInt("auditoria.salvar-intervalo-segundos", 60);
    }
    
    public List<String> getTrackedEvents() {
        return config.getStringList("auditoria.eventos-rastreados");
    }
    
    // ========================================
    // DATABASE
    // ========================================
    public String getStorageType() {
        return config.getString("storage.tipo", "sqlite");
    }
    
    public String getSQLiteFile() {
        return config.getString("storage.sqlite.arquivo", "database.db");
    }
    
    public String getMySQLHost() {
        return config.getString("storage.mysql.host", "127.0.0.1");
    }
    
    public int getMySQLPort() {
        return config.getInt("storage.mysql.porta", 3306);
    }
    
    public String getMySQLDatabase() {
        return config.getString("storage.mysql.database", "nexus");
    }
    
    public String getMySQLUsername() {
        return config.getString("storage.mysql.usuario", "root");
    }
    
    public String getMySQLPassword() {
        return config.getString("storage.mysql.senha", "");
    }
    
    public int getHikariMaxPoolSize() {
        return config.getInt("storage.hikari.maximum-pool-size", 10);
    }
    
    public int getHikariMinIdle() {
        return config.getInt("storage.hikari.minimum-idle", 2);
    }
    
    public long getHikariConnectionTimeout() {
        return config.getLong("storage.hikari.connection-timeout", 30000);
    }
    
    // ========================================
    // DEBUG
    // ========================================
    public boolean isDebugEnabled() {
        return config.getBoolean("debug.habilitado", false);
    }
    
    public boolean shouldLogTransactions() {
        return config.getBoolean("debug.log-transacoes", true);
    }
    
    public boolean shouldLogPerformance() {
        return config.getBoolean("debug.log-performance", false);
    }
}

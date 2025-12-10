package com.nexus.services;

import com.nexus.NexusPlugin;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;
import java.sql.*;
import java.util.logging.Level;

/**
 * DatabaseService - Database connection and management
 * 
 * Manages HikariCP connection pool for SQLite or MySQL.
 * Handles schema creation and database migrations.
 * 
 * @author Nexus Development Team
 */
public class DatabaseService {
    
    private final NexusPlugin plugin;
    private HikariDataSource dataSource;
    private String storageType;
    
    public DatabaseService(NexusPlugin plugin) {
        this.plugin = plugin;
        this.storageType = plugin.getConfigManager().getStorageType();
    }
    
    /**
     * Connect to the database and create tables
     */
    public boolean connect() {
        try {
            // Initialize HikariCP
            HikariConfig config = new HikariConfig();
            
            if (storageType.equalsIgnoreCase("mysql")) {
                setupMySQL(config);
            } else {
                setupSQLite(config);
            }
            
            // Create data source
            dataSource = new HikariDataSource(config);
            
            // Test connection
            try (Connection conn = dataSource.getConnection()) {
                if (conn.isValid(5)) {
                    plugin.getLogger().info("✓ Database connection established (" + storageType + ")");
                    
                    // Create tables
                    createTables();
                    
                    return true;
                } else {
                    plugin.getLogger().severe("Database connection is not valid!");
                    return false;
                }
            }
            
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to connect to database!", e);
            return false;
        }
    }
    
    /**
     * Setup SQLite configuration
     */
    private void setupSQLite(HikariConfig config) {
        String fileName = plugin.getConfigManager().getSQLiteFile();
        File dataFolder = plugin.getDataFolder();
        
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        
        File databaseFile = new File(dataFolder, fileName);
        String jdbcUrl = "jdbc:sqlite:" + databaseFile.getAbsolutePath();
        
        config.setJdbcUrl(jdbcUrl);
        config.setDriverClassName("org.sqlite.JDBC");
        config.setMaximumPoolSize(1); // SQLite only supports single connection
        config.setConnectionTestQuery("SELECT 1");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        
        plugin.getLogger().info("Using SQLite database: " + databaseFile.getAbsolutePath());
    }
    
    /**
     * Setup MySQL configuration
     */
    private void setupMySQL(HikariConfig config) {
        String host = plugin.getConfigManager().getMySQLHost();
        int port = plugin.getConfigManager().getMySQLPort();
        String database = plugin.getConfigManager().getMySQLDatabase();
        String username = plugin.getConfigManager().getMySQLUsername();
        String password = plugin.getConfigManager().getMySQLPassword();
        
        String jdbcUrl = String.format("jdbc:mysql://%s:%d/%s?useSSL=false&allowPublicKeyRetrieval=true", 
            host, port, database);
        
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        
        // HikariCP settings from config
        config.setMaximumPoolSize(plugin.getConfigManager().getHikariMaxPoolSize());
        config.setMinimumIdle(plugin.getConfigManager().getHikariMinIdle());
        config.setConnectionTimeout(plugin.getConfigManager().getHikariConnectionTimeout());
        
        // Performance optimizations
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");
        
        plugin.getLogger().info(String.format("Using MySQL database: %s@%s:%d/%s", username, host, port, database));
    }
    
    /**
     * Create all database tables
     */
    private void createTables() throws SQLException {
        try (Connection conn = getConnection()) {
            // Players table
            execute(conn, 
                "CREATE TABLE IF NOT EXISTS nexus_players (" +
                "uuid VARCHAR(36) PRIMARY KEY," +
                "name VARCHAR(16) NOT NULL," +
                "team VARCHAR(10)," +
                "guild_id INTEGER," +
                "balance DECIMAL(15,2) DEFAULT 0.00," +
                "vip_tier VARCHAR(20)," +
                "last_login BIGINT," +
                "last_team_switch BIGINT," +
                "economy_frozen BOOLEAN DEFAULT FALSE," +
                "created_at BIGINT," +
                "INDEX idx_team (team)," +
                "INDEX idx_guild (guild_id)" +
                ")"
            );
            
            // Teams table
            execute(conn,
                "CREATE TABLE IF NOT EXISTS nexus_teams (" +
                "name VARCHAR(10) PRIMARY KEY," +
                "points INTEGER DEFAULT 0," +
                "total_members INTEGER DEFAULT 0," +
                "created_at BIGINT" +
                ")"
            );
            
            // Guilds table
            execute(conn,
                "CREATE TABLE IF NOT EXISTS nexus_guilds (" +
                "id INTEGER PRIMARY KEY " + (storageType.equalsIgnoreCase("mysql") ? "AUTO_INCREMENT" : "AUTOINCREMENT") + "," +
                "name VARCHAR(32) UNIQUE NOT NULL," +
                "team VARCHAR(10) NOT NULL," +
                "leader_uuid VARCHAR(36) NOT NULL," +
                "member_limit INTEGER DEFAULT 20," +
                "cofre_balance DECIMAL(15,2) DEFAULT 0.00," +
                "points INTEGER DEFAULT 0," +
                "created_at BIGINT," +
                "INDEX idx_name (name)," +
                "INDEX idx_team (team)" +
                ")"
            );
            
            // Guild Members table
            execute(conn,
                "CREATE TABLE IF NOT EXISTS nexus_guild_members (" +
                "guild_id INTEGER NOT NULL," +
                "player_uuid VARCHAR(36) NOT NULL," +
                "role VARCHAR(20) DEFAULT 'MEMBER'," +
                "joined_at BIGINT," +
                "PRIMARY KEY (guild_id, player_uuid)," +
                "INDEX idx_player (player_uuid)" +
                ")"
            );
            
            // Nexus table
            execute(conn,
                "CREATE TABLE IF NOT EXISTS nexus_hearts (" +
                "guild_id INTEGER PRIMARY KEY," +
                "level INTEGER DEFAULT 1," +
                "health DECIMAL(10,2) DEFAULT 10000.00," +
                "max_health DECIMAL(10,2) DEFAULT 10000.00," +
                "state VARCHAR(20) DEFAULT 'ACTIVE'," +
                "location TEXT," +
                "last_destroyed BIGINT," +
                "created_at BIGINT" +
                ")"
            );
            
            // Shield table
            execute(conn,
                "CREATE TABLE IF NOT EXISTS nexus_shields (" +
                "guild_id INTEGER PRIMARY KEY," +
                "state VARCHAR(20) DEFAULT 'INACTIVE'," +
                "activated_at BIGINT," +
                "expires_at BIGINT," +
                "last_used BIGINT" +
                ")"
            );
            
            // Transactions table
            execute(conn,
                "CREATE TABLE IF NOT EXISTS nexus_transactions (" +
                "id INTEGER PRIMARY KEY " + (storageType.equalsIgnoreCase("mysql") ? "AUTO_INCREMENT" : "AUTOINCREMENT") + "," +
                "from_uuid VARCHAR(36)," +
                "to_uuid VARCHAR(36)," +
                "amount DECIMAL(15,2) NOT NULL," +
                "type VARCHAR(30) NOT NULL," +
                "reason TEXT," +
                "timestamp BIGINT," +
                "INDEX idx_from (from_uuid)," +
                "INDEX idx_to (to_uuid)," +
                "INDEX idx_timestamp (timestamp)" +
                ")"
            );
            
            // Audit log table
            execute(conn,
                "CREATE TABLE IF NOT EXISTS nexus_audit (" +
                "id INTEGER PRIMARY KEY " + (storageType.equalsIgnoreCase("mysql") ? "AUTO_INCREMENT" : "AUTOINCREMENT") + "," +
                "player_uuid VARCHAR(36)," +
                "event_type VARCHAR(50) NOT NULL," +
                "details TEXT," +
                "timestamp BIGINT," +
                "ip_address VARCHAR(45)," +
                "INDEX idx_player (player_uuid)," +
                "INDEX idx_event (event_type)," +
                "INDEX idx_timestamp (timestamp)" +
                ")"
            );
            
            // Objectives table
            execute(conn,
                "CREATE TABLE IF NOT EXISTS nexus_objectives (" +
                "id INTEGER PRIMARY KEY " + (storageType.equalsIgnoreCase("mysql") ? "AUTO_INCREMENT" : "AUTOINCREMENT") + "," +
                "name VARCHAR(100) NOT NULL," +
                "description TEXT," +
                "category VARCHAR(20)," +
                "difficulty VARCHAR(20)," +
                "reward DECIMAL(15,2)," +
                "state VARCHAR(20) DEFAULT 'ACTIVE'," +
                "progress INTEGER DEFAULT 0," +
                "goal INTEGER," +
                "created_at BIGINT," +
                "completed_at BIGINT," +
                "INDEX idx_state (state)," +
                "INDEX idx_category (category)" +
                ")"
            );
            
            // Objective Participants table
            execute(conn,
                "CREATE TABLE IF NOT EXISTS nexus_objective_participants (" +
                "objective_id INTEGER NOT NULL," +
                "player_uuid VARCHAR(36) NOT NULL," +
                "contribution INTEGER DEFAULT 0," +
                "PRIMARY KEY (objective_id, player_uuid)," +
                "INDEX idx_player (player_uuid)" +
                ")"
            );
            
            // Panels table
            execute(conn,
                "CREATE TABLE IF NOT EXISTS nexus_panels (" +
                "id INTEGER PRIMARY KEY " + (storageType.equalsIgnoreCase("mysql") ? "AUTO_INCREMENT" : "AUTOINCREMENT") + "," +
                "type VARCHAR(20) NOT NULL," +
                "location TEXT NOT NULL," +
                "guild_id INTEGER," +
                "team VARCHAR(10)," +
                "data TEXT," +
                "created_at BIGINT" +
                ")"
            );
            
            // Market listings table
            execute(conn,
                "CREATE TABLE IF NOT EXISTS nexus_market_listings (" +
                "id INTEGER PRIMARY KEY " + (storageType.equalsIgnoreCase("mysql") ? "AUTO_INCREMENT" : "AUTOINCREMENT") + "," +
                "seller_uuid VARCHAR(36) NOT NULL," +
                "item_data TEXT NOT NULL," +
                "price DECIMAL(15,2) NOT NULL," +
                "listed_at BIGINT," +
                "expires_at BIGINT," +
                "sold BOOLEAN DEFAULT FALSE," +
                "INDEX idx_seller (seller_uuid)," +
                "INDEX idx_expires (expires_at)" +
                ")"
            );
            
            // Initialize default teams if they don't exist
            execute(conn,
                "INSERT OR IGNORE INTO nexus_teams (name, points, total_members, created_at) " +
                "VALUES ('SOLAR', 0, 0, " + System.currentTimeMillis() + ")"
            );
            
            execute(conn,
                "INSERT OR IGNORE INTO nexus_teams (name, points, total_members, created_at) " +
                "VALUES ('LUNAR', 0, 0, " + System.currentTimeMillis() + ")"
            );
            
            plugin.getLogger().info("✓ Database tables created/verified");
        }
    }
    
    /**
     * Execute a SQL statement
     */
    private void execute(Connection conn, String sql) throws SQLException {
        // SQLite doesn't support IF NOT EXISTS for INSERT, use INSERT OR IGNORE
        // MySQL uses INSERT IGNORE
        if (storageType.equalsIgnoreCase("mysql")) {
            sql = sql.replace("INSERT OR IGNORE", "INSERT IGNORE");
            sql = sql.replace("AUTOINCREMENT", "AUTO_INCREMENT");
        }
        
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }
    
    /**
     * Get a connection from the pool
     */
    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource is not initialized!");
        }
        return dataSource.getConnection();
    }
    
    /**
     * Close the connection pool
     */
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            plugin.getLogger().info("Database connection closed.");
        }
    }
    
    /**
     * Check if the database is connected
     */
    public boolean isConnected() {
        if (dataSource == null) {
            return false;
        }
        
        try (Connection conn = dataSource.getConnection()) {
            return conn.isValid(5);
        } catch (SQLException e) {
            return false;
        }
    }
    
    /**
     * Get storage type (sqlite or mysql)
     */
    public String getStorageType() {
        return storageType;
    }
}

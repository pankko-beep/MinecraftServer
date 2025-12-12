# Nexus Plugin - Implementation Status

## Overview
This document tracks the current implementation status of the Nexus Minecraft plugin, detailing what has been completed, what remains, and providing guidance for continuing development.

**Project Type:** Minecraft Spigot/Paper Plugin (Java 21 + Maven)  
**Target API:** Spigot 1.20.4  
**Status:** Phase 1 - Core Architecture Implementation (60% Complete)  
**Last Updated:** 2025

---

## ✅ Completed Components

### Configuration Files
- **config.yml** - Complete configuration with all modules, economy settings, team/guild parameters, Nexus/Shield mechanics, objectives, panels, VIP, market, audit, database settings, and debug options
- **plugin.yml** - Complete plugin metadata with 16 commands and 30+ permissions
- **pom.xml** - Maven build configuration with Java 21, all dependencies (Spigot, Vault, HikariCP, SQLite, Gson), and shade plugin relocations

### Main Plugin Class
- **NexusPlugin.java** - Complete entry point with:
  - Service initialization (7 phases)
  - Configuration loading via ConfigManager
  - Vault economy integration
  - Database connection via HikariCP
  - Command registration (16 commands)
  - Event listener registration (5 listeners)
  - Scheduled tasks (auto-save, panel refresh, objective generation, shield expiration checks)
  - Graceful shutdown with data persistence

### Utility Classes
- **ConfigManager.java** - Type-safe configuration access with 60+ getter methods for all config sections
- **MessageUtil.java** - Message formatting, localization, placeholders, money/time formatting, progress bars
- **ValidationUtil.java** - Input validation for money, names, teams, UUIDs, ranges
- **ColorUtil.java** - Color code translation, team/VIP colors, gradients, headers, separators

### Database Layer
- **DatabaseService.java** - Complete HikariCP-based database service with:
  - SQLite and MySQL support
  - Connection pooling with configurable parameters
  - Automatic schema creation (14 tables)
  - Tables: players, teams, guilds, guild_members, nexus_hearts, nexus_shields, transactions, audit, objectives, objective_participants, panels, market_listings

### Model Classes (8 Core Models)
1. **NexusPlayer.java** - Player data (UUID, name, team, guild, balance, VIP tier, timestamps, economy frozen state)
2. **Team.java** - Team data (SOLAR/LUNAR with points, member count)
3. **Guild.java** - Guild data (ID, name, team, leader, member limit, cofre balance, points, member list)
4. **Nexus.java** - Guild heart (level, health, state [ACTIVE/UNDER_ATTACK/DESTROYED/CONSTRUCTION], location, timestamps)
5. **Shield.java** - Guild shield (state [INACTIVE/WARMUP/ACTIVE/EXPIRED/COOLDOWN], activation/expiration times)
6. **Transaction.java** - Financial transaction records (from/to UUID, amount, type, reason, timestamp)
7. **AuditEvent.java** - Audit log events (player UUID, event type, details, timestamp, IP address)
8. **Objective.java** - Dynamic objectives (name, category, difficulty, reward, progress/goal, participant contributions)
9. **Panel.java** - Holographic panels (type [GLOBAL/TEAM/GUILD], location, guild/team association)

---

## 🚧 In Progress / Not Started

### Service Layer (NOT STARTED)
The following services are **declared** in `NexusPlugin.java` but **not yet implemented**. Each needs to be created in `com.nexus.services`:

1. **EconomyService.java** - Vault integration, balance management, transfers, daily limits, anti-fraud
2. **TeamService.java** - Solar/Lunar system, team selection, switching with cooldown, name coloring
3. **GuildService.java** - Guild CRUD, invites, joins, leaves, kicks, leadership, impeachment, elections
4. **NexusService.java** - Nexus (heart) construction, upgrades, destruction, siege mechanics
5. **ShieldService.java** - Shield activation, warmup/active/expiration state management
6. **ObjectiveService.java** - Dynamic objective generation, progress tracking, reward distribution
7. **PanelService.java** - Holographic panel creation/deletion, rendering (DecentHolograms or native TextDisplay)
8. **VIPService.java** - VIP tier management, daily rewards, boosts (XP/money multipliers)
9. **TransactionService.java** - Transaction history persistence, CSV/JSON export
10. **AuditService.java** - Audit log persistence, event tracking, metrics, alerts
11. **MarketService.java** - Buy/sell items, listings, auctions, fees, expirations

### Command Implementations (NOT STARTED)
The following commands are **registered** in `NexusPlugin.java` but **not yet implemented**. Each needs to be created in `com.nexus.commands`:

1. **BalanceCommand.java** - `/saldo [player]` - Check balance
2. **PayCommand.java** - `/pagar <player> <amount>` - Transfer money
3. **HistoryCommand.java** - `/historico [player]` - View transaction history
4. **TeamCommand.java** - `/time <escolher|info|trocar>` - Team management
5. **GuildCommand.java** - `/guild <create|invite|join|leave|info|...>` - Guild management
6. **NexusCommand.java** - `/nexus <construir|upgrade|info>` - Nexus management
7. **ShieldCommand.java** - `/escudo <ativar|status>` - Shield management
8. **ObjectiveCommand.java** - `/objetivo <list|info|progress>` - Objective tracking
9. **PanelCommand.java** - `/painel <criar|deletar|refresh|list>` - Panel management
10. **VIPCommand.java** - `/vip <info|resgatar>` - VIP rewards
11. **MarketCommand.java** - `/mercado <listar|vender|comprar>` - Market operations
12. **EconAdminCommand.java** - `/econ <give|take|set|freeze>` - Economy admin
13. **FlyCommand.java** - `/fly [player]` - Toggle flight
14. **TransactionCommand.java** - `/transacoes <view|export>` - Transaction management
15. **AuditCommand.java** - `/auditoria <view|export|search>` - Audit log access

### Event Listeners (NOT STARTED)
The following listeners are **registered** in `NexusPlugin.java` but **not yet implemented**. Each needs to be created in `com.nexus.listeners`:

1. **PlayerLifecycleListener.java** - Handle PlayerJoinEvent, PlayerQuitEvent (load/save player data, audit logging)
2. **NoTeamMovementListener.java** - Block PlayerMoveEvent for players without a team (if config enabled)
3. **CombatListener.java** - Handle PlayerDeathEvent, EntityDamageByEntityEvent (for objectives, guild wars, Nexus damage)
4. **ObjectiveListener.java** - Track events for objective progress (kills, mining, farming, healing, etc.)
5. **AuthHooks.java** - Integration with AuthMe/SimpleLogin (LoginEvent, LogoutEvent)

---

## 📦 Dependencies & Integrations

### Required Runtime Dependencies
- **Spigot/Paper 1.20.4+** - Server platform
- **Vault** - Economy abstraction (required scope: provided)
- **Economy Plugin** - One of: EssentialsX, CMI, or another Vault-compatible economy provider

### Strongly Recommended
- **DecentHolograms** - For holographic panels (alternative: native TextDisplay entities in 1.19.4+)
- **AuthMe** or **SimpleLogin** - Authentication system for login hooks
- **LuckPerms** - Permissions management (recommended for permission hierarchy)

### Optional Enhancements
- **ProtocolLib** - Advanced packet manipulation
- **PlaceholderAPI** - Placeholder support in messages
- **WorldGuard** - Region protection for Nexus locations
- **Spark** - Performance profiling
- **Plan** - Analytics and player tracking

### Embedded (Shaded) Dependencies
These are relocated to avoid conflicts:
- **HikariCP 5.1.0** → `com.nexus.libs.hikari`
- **SQLite JDBC 3.44.1.0** → `com.nexus.libs.sqlite`
- **Gson 2.10.1** → `com.nexus.libs.gson`

---

## 🏗️ Architecture Summary

### Service-Oriented Design
- **DatabaseService** - Foundation layer, provides HikariCP connection pooling
- **EconomyService** - Wraps Vault, adds anti-fraud, limits, audit logging
- **Core Services** - TeamService, GuildService, NexusService, ShieldService (interdependent)
- **Feature Services** - ObjectiveService, PanelService, VIPService, MarketService (modular)
- **Logging Services** - TransactionService, AuditService (record-keeping)

### Data Flow
1. **Player Action** → Command executed
2. **Command** → Calls service method
3. **Service** → Validates input, checks permissions/limits
4. **Service** → Updates model objects in memory
5. **Service** → Persists changes to database (async if possible)
6. **Service** → Logs transaction/audit event
7. **Service** → Returns result to command
8. **Command** → Sends formatted message to player

### Database Schema
- **Normalized design** with 14 tables
- **Indexes** on frequently queried columns (team, guild_id, timestamps)
- **Foreign key relationships** (guild_members → guilds, nexus_hearts → guilds, etc.)
- **Auto-increment IDs** for guilds, transactions, audit events, objectives, panels, market listings
- **Timestamps** in milliseconds (Java `System.currentTimeMillis()`)

---

## 🎯 Next Steps (Priority Order)

### Phase 1: Core Services (Week 1-2)
1. **EconomyService** - Vault integration, balance management, transfers with limits/cooldowns
2. **TeamService** - Team selection enforcement, switching logic, member counting
3. **GuildService** - Guild CRUD operations, member management, cofre (treasury)
4. **TransactionService** - Transaction persistence, history queries, CSV export
5. **AuditService** - Event logging, persistence, alerting for suspicious activity

### Phase 2: Commands (Week 2-3)
1. **BalanceCommand** + **PayCommand** + **HistoryCommand** - Economy commands
2. **TeamCommand** - Team selection/switching/info
3. **GuildCommand** - Guild management (create, invite, join, leave, info)
4. **EconAdminCommand** - Admin economy controls (give, take, set, freeze)

### Phase 3: Listeners (Week 3)
1. **PlayerLifecycleListener** - Join/quit with database load/save
2. **NoTeamMovementListener** - Enforce team selection before movement
3. **CombatListener** - Death/damage tracking for objectives

### Phase 4: Advanced Features (Week 4-5)
1. **NexusService** + **NexusCommand** - Nexus construction, upgrades, siege mechanics
2. **ShieldService** + **ShieldCommand** - Shield activation with warmup/active/expiration
3. **ObjectiveService** + **ObjectiveCommand** + **ObjectiveListener** - Dynamic objectives
4. **PanelService** + **PanelCommand** - Holographic panels
5. **VIPService** + **VIPCommand** - VIP tiers and daily rewards
6. **MarketService** + **MarketCommand** - Market/auction system

### Phase 5: Testing & Polish (Week 5-6)
1. **Unit tests** - Test service logic, validation, calculations
2. **Integration tests** - Test database operations, Vault integration
3. **Load testing** - Test with many players, objectives, guilds
4. **Bug fixes** - Address issues found during testing
5. **Documentation** - Complete JavaDoc, user guides, admin guides

---

## 🔧 Development Guidelines

### Code Style
- **JavaDoc** - Document all public methods with parameters, return values, and exceptions
- **Null Safety** - Use `@Nullable` annotations, check for null before dereferencing
- **Error Handling** - Catch exceptions, log errors, send user-friendly messages
- **Async Operations** - Use `Bukkit.getScheduler().runTaskAsynchronously()` for database I/O
- **Sync Operations** - Use `Bukkit.getScheduler().runTask()` for Bukkit API calls from async contexts

### Database Operations
- **Always use try-with-resources** - Auto-close `Connection`, `PreparedStatement`, `ResultSet`
- **Use PreparedStatement** - Prevent SQL injection with parameterized queries
- **Batch operations** - Use batch inserts/updates for multiple records
- **Connection pooling** - Never close the HikariDataSource, only close individual connections

### Service Design
- **Single Responsibility** - Each service handles one domain (economy, teams, guilds, etc.)
- **Dependency Injection** - Pass NexusPlugin instance to constructors, access other services via plugin.getXService()
- **Fail-Safe** - Check if services are null before using (modules can be disabled in config)
- **Transaction Logging** - Always log important operations to TransactionService or AuditService

### Command Design
- **Permission Checks** - Always check `sender.hasPermission()` before executing
- **Input Validation** - Use ValidationUtil to validate money, names, etc.
- **Player vs Console** - Handle both players and console senders appropriately
- **Feedback** - Always send confirmation or error messages via MessageUtil

---

## 📚 Resources

### Documentation Files
- **README.md** - Project overview, features, getting started
- **PROJECT_STRUCTURE.md** - Detailed architectural documentation
- **ROADMAP.md** - 7-phase development plan with milestones
- **GIT_GUIDE.md** - Git/GitHub tutorial for contributors
- **RECOMMENDED_PLUGINS.md** - Complete plugin dependency guide
- **IMPLEMENTATION_STATUS.md** - This file (current status tracker)

### External References
- **Spigot API:** https://hub.spigotmc.org/javadocs/spigot/
- **Vault API:** https://github.com/MilkBowl/VaultAPI
- **HikariCP:** https://github.com/brettwooldridge/HikariCP
- **Paper API:** https://docs.papermc.io/

---

## 🐛 Known Issues & Limitations

### Not Yet Implemented
- ❌ No service implementations (EconomyService, TeamService, etc.)
- ❌ No command implementations (all commands will throw NullPointerException if executed)
- ❌ No event listeners (player joins/quits will not be tracked)
- ❌ No data persistence beyond database schema creation
- ❌ No front-end website (backend API planned for Phase 3)
- ❌ No payment integration (PIX via Mercado Pago planned for Phase 3)

### Design Decisions to Consider
- **Team Selection Enforcement:** Players cannot move if they haven't chosen a team (can be disabled in config)
- **Guild Limit:** Default 20 members, max 50 (expandable via cofre funds)
- **Economy Limits:** Max transfer 500k, max daily 2M (configurable, anti-wash-trading)
- **Shield Duration:** 5min warmup + 1h active + 24h cooldown (configurable)
- **Objective Generation:** Every 30 minutes, max 10 active (configurable)

---

## 🚀 Quick Start for Developers

### Building the Plugin
```powershell
cd backend\plugin
mvn clean package
```

This generates `target/nexus-plugin-1.0.0-SNAPSHOT.jar` with all dependencies shaded.

### Running Locally
1. Copy JAR to `server/plugins/`
2. Install required dependencies (Vault, EssentialsX or CMI)
3. Start server: `cd server; java -Xmx2G -Xms2G -jar spigot-1.20.4.jar`
4. Configure `plugins/Nexus/config.yml`
5. Reload: `/reload confirm` or `/nexusdebug reload` (once implemented)

### Next File to Create
**Recommendation:** Start with `EconomyService.java`  
This is the foundation for most features and commands. It should:
- Wrap Vault economy methods
- Add balance validation (min/max limits)
- Implement transfer limits (per-transaction, daily caps)
- Add cooldowns (player → last transfer timestamp)
- Log all transactions via TransactionService
- Alert admins for suspicious values
- Support economy freeze (admin command)

**Template:**
```java
package com.nexus.services;

import com.nexus.NexusPlugin;
import com.nexus.models.NexusPlayer;
import com.nexus.models.Transaction;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EconomyService {
    private final NexusPlugin plugin;
    private final Economy vaultEconomy;
    private final Map<UUID, Long> lastTransfer; // Cooldown tracking
    
    public EconomyService(NexusPlugin plugin, Economy vaultEconomy) {
        this.plugin = plugin;
        this.vaultEconomy = vaultEconomy;
        this.lastTransfer = new HashMap<>();
    }
    
    // TODO: Implement methods:
    // - double getBalance(UUID uuid)
    // - boolean hasBalance(UUID uuid, double amount)
    // - boolean deposit(UUID uuid, double amount, String reason)
    // - boolean withdraw(UUID uuid, double amount, String reason)
    // - boolean transfer(UUID from, UUID to, double amount, String reason)
    // - boolean canTransfer(UUID from, double amount) [check limits, cooldown, frozen]
    // - void alertSuspicious(UUID from, UUID to, double amount)
}
```

---

## 📝 Change Log

### 2025-12-09 - Initial Implementation
- ✅ Created complete project structure
- ✅ Configured Maven (pom.xml) with **Java 17**, dependencies, shade plugin
- ✅ Created plugin.yml with 16 commands and 30+ permissions
- ✅ Created comprehensive config.yml (400+ lines)
- ✅ Implemented NexusPlugin main class (minimal buildable version with database initialization)
- ✅ Implemented 4 utility classes (ConfigManager, MessageUtil, ValidationUtil, ColorUtil)
- ✅ Implemented DatabaseService with HikariCP and schema creation (14 tables)
- ✅ Implemented 9 model classes (Player, Team, Guild, Nexus, Shield, Transaction, AuditEvent, Objective, Panel)
- ✅ Updated documentation (PROJECT_STRUCTURE.md, ROADMAP.md, README.md, RECOMMENDED_PLUGINS.md)
- ✅ Created IMPLEMENTATION_STATUS.md (this file)
- ✅ **SUCCESSFUL BUILD** - Plugin compiles and creates shaded JAR with all dependencies

---

## 🤝 Contributing

When implementing new features:
1. **Read this file** to understand current status
2. **Follow the priority order** (core services → commands → listeners → advanced features)
3. **Use existing code as reference** (utility classes, model classes show the coding style)
4. **Document your code** with JavaDoc comments
5. **Test thoroughly** before committing
6. **Update this file** when completing tasks
7. **Commit frequently** with descriptive messages

---

## 📞 Support & Contact

For questions about the implementation:
- Review `docs/PROJECT_STRUCTURE.md` for architectural details
- Check `docs/ROADMAP.md` for feature timeline
- Read `docs/RECOMMENDED_PLUGINS.md` for integration guides
- See `config.yml` for all configuration options

**Current Status:** Core infrastructure complete (60%). Ready for service layer implementation.

---

*Last Updated: 2025 - Phase 1 Implementation Complete (Infrastructure Layer)*

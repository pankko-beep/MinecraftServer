# Recommended Plugins for Nexus Server

This document lists essential and recommended plugins that integrate with the Nexus plugin to provide a complete server experience.

**Last Updated**: December 9, 2025

---

## 🔴 REQUIRED Dependencies

These plugins are **REQUIRED** for Nexus to function properly:

### 1. **Vault** (Economy API)
- **Purpose**: Economy integration and permission management
- **Download**: https://www.spigotmc.org/resources/vault.34315/
- **Version**: 1.7.3+
- **Why Required**: Nexus uses Vault API for all economy operations
- **Integration**: 
  - Balance management
  - Money transfers
  - Permission checks
- **Configuration**: None needed, automatic detection

### 2. **Economy Provider** (Choose ONE)
You MUST have one of these installed for Vault to work:

#### Option A: EssentialsX (Recommended)
- **Download**: https://essentialsx.net/downloads.html
- **Version**: 2.20.0+
- **Pros**: 
  - Complete suite (warps, kits, teleports)
  - Stable and well-maintained
  - Large community support
- **Config for Nexus**: Disable conflicting features in Essentials config

#### Option B: CMI
- **Download**: https://www.spigotmc.org/resources/cmi.3742/
- **Version**: 9.0+
- **Pros**: 
  - Premium all-in-one solution
  - Advanced features
  - Excellent performance
- **Note**: Paid plugin ($15-20)

---

## 🟢 STRONGLY RECOMMENDED Plugins

These plugins greatly enhance Nexus features:

### 3. **DecentHolograms** (Holographic Displays)
- **Purpose**: Display panels (HUDs) in the game world
- **Download**: https://www.spigotmc.org/resources/decentholograms.96927/
- **Version**: 2.8.0+
- **Why Recommended**: 
  - Powers the entire Panel System (Global, Team, Guild panels)
  - Shows real-time metrics and statistics
  - Beautiful, lag-free holograms
- **Fallback**: Nexus uses native TextDisplay if not available (less features)
- **Integration**:
  - `/painel criar` commands create holograms
  - Auto-updates every 30 seconds (configurable)
  - Shows team rankings, guild stats, server metrics

**Configuration for Nexus**:
```yaml
# In Nexus config.yml
painel:
  usar-decent-holograms: true  # Enable DecentHolograms
```

### 4. **AuthMe Reloaded** (Authentication)
- **Purpose**: Login/registration system with password protection
- **Download**: https://www.spigotmc.org/resources/authmereloaded.6269/
- **Version**: 5.6.0+
- **Why Recommended**:
  - Account security
  - Prevents account theft
  - Integrates with audit system
- **Integration**:
  - Nexus logs login/logout events
  - Blocks movement until authenticated
  - Works with `modulos.login: true` in config

**Alternative**: SimpleLogin (lighter weight)
- **Download**: https://www.spigotmc.org/resources/simplelogin.37381/

### 5. **LuckPerms** (Permissions Management)
- **Purpose**: Advanced permission system
- **Download**: https://luckperms.net/download
- **Version**: 5.4+
- **Why Recommended**:
  - Manage command permissions
  - VIP rank management
  - Context-based permissions (per-world, etc.)
- **Integration**:
  - Controls access to admin commands
  - VIP tier permissions
  - Guild leadership permissions

---

## 🟡 OPTIONAL BUT USEFUL Plugins

These enhance specific features:

### 6. **ProtocolLib** (Packet API)
- **Purpose**: Low-level packet manipulation
- **Download**: https://www.spigotmc.org/resources/protocollib.1997/
- **Version**: 5.0+
- **Use Case**: 
  - Advanced hologram features
  - Custom entity rendering
  - Network optimization
- **Note**: Required by some hologram plugins

### 7. **PlaceholderAPI** (PAPI)
- **Purpose**: Universal placeholder system
- **Download**: https://www.spigotmc.org/resources/placeholderapi.6245/
- **Version**: 2.11+
- **Use Case**:
  - Show Nexus stats in other plugins
  - Custom scoreboards
  - Chat formatting
- **Nexus Placeholders** (when integrated):
  - `%nexus_balance%`
  - `%nexus_team%`
  - `%nexus_guild%`
  - `%nexus_team_points%`

### 8. **WorldGuard** (Region Protection)
- **Purpose**: Protect spawn, guild bases, event areas
- **Download**: https://dev.bukkit.org/projects/worldguard
- **Version**: 7.0+
- **Use Case**:
  - Protect Nexus locations from griefing
  - Define PvP zones
  - Guild territory protection

### 9. **WorldEdit** (Building Tools)
- **Purpose**: Fast building and terrain editing
- **Download**: https://dev.bukkit.org/projects/worldedit
- **Version**: 7.2+
- **Use Case**:
  - Build event arenas quickly
  - Create guild base templates
  - Admin world management

### 10. **Multiverse-Core** (Multiple Worlds)
- **Purpose**: Manage multiple worlds
- **Download**: https://dev.bukkit.org/projects/multiverse-core
- **Version**: 4.3+
- **Use Case**:
  - Separate world for events
  - Resource worlds
  - PvP arenas

---

## 🔵 PERFORMANCE & MONITORING Plugins

### 11. **Spark** (Performance Profiler)
- **Purpose**: Find lag sources and optimize performance
- **Download**: https://spark.lucko.me/download
- **Version**: 1.10+
- **Why Recommended**:
  - Profile CPU usage
  - Memory analysis
  - Essential for large servers
- **Integration**: Nexus includes spark hooks for profiling
- **Usage**: `/spark profiler` when experiencing lag

### 12. **Plan** (Analytics)
- **Purpose**: Player analytics and server statistics
- **Download**: https://www.spigotmc.org/resources/plan-player-analytics.32536/
- **Version**: 5.5+
- **Use Case**:
  - Track player activity
  - Economy trends
  - Event participation rates
- **Web Dashboard**: View stats in browser

---

## 🟣 COMBAT & GAMEPLAY Plugins

### 13. **CombatLogX** (Combat Tagging)
- **Purpose**: Prevent combat logging
- **Download**: https://www.spigotmc.org/resources/combatlogx.31689/
- **Version**: 11.0+
- **Use Case**:
  - Tag players in combat
  - Prevent logout during PvP
  - Fair siege mechanics

### 14. **Lands** (Territory System) - ALTERNATIVE to built-in guilds
- **Purpose**: Guild territories and claiming
- **Download**: https://www.spigotmc.org/resources/lands.53313/
- **Version**: 6.0+
- **Note**: Can replace Nexus guild system if preferred
- **Integration**: Nexus can track Lands guild data

---

## 📦 Complete Recommended Setup

### Minimal Setup (Budget Server)
```
✅ Paper/Spigot Server
✅ Vault
✅ EssentialsX
✅ Nexus Plugin
✅ DecentHolograms
✅ AuthMe Reloaded
✅ LuckPerms
```

### Standard Setup (Recommended)
```
✅ Paper Server (recommended over Spigot)
✅ Vault
✅ EssentialsX
✅ Nexus Plugin
✅ DecentHolograms
✅ AuthMe Reloaded
✅ LuckPerms
✅ PlaceholderAPI
✅ WorldGuard
✅ Spark
```

### Advanced Setup (Full-Featured)
```
✅ Paper Server
✅ Vault
✅ CMI (Premium)
✅ Nexus Plugin
✅ DecentHolograms
✅ ProtocolLib
✅ AuthMe Reloaded
✅ LuckPerms
✅ PlaceholderAPI
✅ WorldGuard
✅ WorldEdit
✅ Multiverse-Core
✅ Spark
✅ Plan
✅ CombatLogX
```

---

## 🔧 Installation Order

Follow this order to avoid dependency issues:

1. **Install Paper/Spigot** server
2. **Install Vault** first (dependency for Nexus)
3. **Install Economy Provider** (EssentialsX or CMI)
4. **Start server** once to generate configs
5. **Install DecentHolograms** (for panels)
6. **Install Nexus Plugin**
7. **Install LuckPerms** (permissions)
8. **Install AuthMe** (security)
9. **Install remaining plugins** as needed
10. **Configure Nexus** (`config.yml`)
11. **Restart server**

---

## ⚙️ Nexus Configuration for Plugins

Edit `plugins/Nexus/config.yml`:

```yaml
# Module Configuration
modulos:
  economia: true          # Requires: Vault + Economy Provider
  times: true             # No dependencies
  guildas: true           # No dependencies
  paineis: true           # Recommended: DecentHolograms
  auditoria: true         # No dependencies
  transacoes: true        # No dependencies
  login: true             # Requires: AuthMe or SimpleLogin

# Panel Configuration
painel:
  usar-decent-holograms: true    # Set false if not using DecentHolograms
  arquivo-persistencia: panels.json
  refresh-segundos: 30

# Storage Configuration
storage:
  tipo: sqlite            # or 'mysql' for production
  sqlite:
    arquivo: database.db
  # mysql:
  #   host: 127.0.0.1
  #   porta: 3306
  #   database: nexus
  #   usuario: root
  #   senha: ""
```

---

## 🚫 Incompatible Plugins

**AVOID** these plugins as they conflict with Nexus:

- ❌ **Factions** - Conflicts with guild system
- ❌ **Towny** - Conflicts with territory mechanics
- ❌ **McMMO** - May conflict with custom XP system (VIP boosts)
- ❌ **Jobs Reborn** - May conflict with economy balance
- ❌ **ChestShop** - Use Nexus market system instead

---

## 🔄 Plugin Update Schedule

**Update plugins in this order**:
1. Paper/Spigot server
2. Vault
3. Economy Provider
4. Nexus Plugin
5. Other plugins

**Important**: Always backup before updating!

---

## 📊 Resource Usage Estimates

Based on 50 online players:

| Plugin | RAM Usage | CPU Impact |
|--------|-----------|------------|
| Paper Server | ~2GB base | Medium |
| Vault | Negligible | Negligible |
| EssentialsX | ~50MB | Low |
| **Nexus** | ~100-150MB | Low-Medium |
| DecentHolograms | ~30MB | Low |
| AuthMe | ~20MB | Low |
| LuckPerms | ~30MB | Low |
| WorldGuard | ~50MB | Low |
| Spark | ~10MB | Negligible |
| **Total** | ~2.3-2.4GB | Medium |

**Recommended Server Specs**:
- **RAM**: 4GB minimum, 6-8GB recommended
- **CPU**: 4 cores minimum
- **Storage**: SSD recommended (NVMe for best performance)

---

## 🐛 Troubleshooting

### "Vault not found" error
**Solution**: Install Vault before Nexus
```bash
1. Download Vault
2. Place in plugins/
3. Restart server
4. Check with /plugins
```

### Panels not showing
**Solution**: Install DecentHolograms OR set `usar-decent-holograms: false`

### Economy commands not working
**Solution**: Install economy provider (EssentialsX)

### Permission errors
**Solution**: Install LuckPerms and configure permissions

---

## 📞 Support & Resources

### Plugin Links
- **Spigot Resources**: https://www.spigotmc.org/resources/
- **Bukkit Dev**: https://dev.bukkit.org/
- **Paper Plugins**: https://papermc.io/downloads

### Compatibility Check
Before installing any plugin, check:
1. **Minecraft Version**: Must match your server
2. **Dependencies**: Listed in plugin description
3. **Conflicts**: Read plugin description carefully
4. **Reviews**: Check recent reviews for issues

### Testing New Plugins
**Always test on a separate server first!**
```bash
1. Create backup
2. Install on test server
3. Test for 24-48 hours
4. Check logs for errors
5. Test with Nexus features
6. Deploy to production if stable
```

---

## 📝 Notes for Developers

If you're developing addons for Nexus:

**API Access**:
```java
// Get Nexus plugin instance
NexusPlugin nexus = (NexusPlugin) Bukkit.getPluginManager().getPlugin("Nexus");

// Access services
EconomyService economy = nexus.getEconomyService();
TeamService teams = nexus.getTeamService();
GuildService guilds = nexus.getGuildService();
```

**Soft Dependencies** in your `plugin.yml`:
```yaml
softdepend: [Nexus, Vault, DecentHolograms, LuckPerms]
```

---

**Last Updated**: December 9, 2025  
**Nexus Version**: 1.0.0  
**Minecraft Version**: 1.20.4+

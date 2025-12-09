# Project Structure Documentation

This document provides a comprehensive overview of the Minecraft Server project structure, explaining the purpose of each folder and file.

**Last Updated**: December 9, 2025  
**Version**: 1.0.0  
**Architecture Inspired By**: SVminecraft (Java 21 + Node.js Payment Integration)

---

## Core Architectural Concepts

This project follows proven patterns from production Minecraft servers:

### Key Principles:
1. **Modular Service Architecture** - Separate concerns into dedicated services
2. **Event-Driven Design** - Listeners respond to game events asynchronously
3. **Payment Integration** - Real-world economy (PIX) linked to game economy
4. **Comprehensive Auditing** - All actions logged for analytics and security
5. **Database Flexibility** - Support both SQLite (dev) and MySQL (production)
6. **External API Integration** - REST API bridges game server and website
7. **Plugin Dependency Management** - Soft dependencies with graceful fallbacks

### Technology Stack Philosophy:
- **Java 21 LTS** for plugin development (modern features + stability)
- **Node.js + Express** for payment backend (async webhooks)
- **Maven + Shade** for dependency packaging (fat JARs)
- **HikariCP** for database connection pooling (performance)
- **Vault API** for economy integration (compatibility)

---

## Root Directory Structure

```
MinecraftServer/
├── backend/              # All backend-related code
├── frontend/             # Website frontend application
├── server/               # Minecraft server runtime environment
├── config/               # Global configuration files
├── docs/                 # Project documentation
├── .gitignore           # Git ignore rules for root
└── README.md            # Project overview and quick start
```

---

## 1. Backend Directory (`/backend`)

The backend contains all server-side logic, including custom Minecraft plugins and web APIs.

### 1.1 Plugin Directory (`/backend/plugin`)

**Purpose**: Maven-based custom Minecraft plugin development

#### Structure:
```
plugin/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/yourname/minecraftplugin/
│   │   │       ├── Main.java (placeholder for main plugin class)
│   │   │       ├── commands/           (will contain command classes)
│   │   │       ├── listeners/          (will contain event listeners)
│   │   │       ├── managers/           (will contain game logic managers)
│   │   │       ├── models/             (will contain data models)
│   │   │       └── utils/              (will contain utility classes)
│   │   └── resources/
│   │       ├── plugin.yml              (plugin metadata and configuration)
│   │       └── config.yml              (plugin configuration file)
│   └── test/
│       └── java/
│           └── com/yourname/minecraftplugin/
│               └── (test classes will go here)
├── pom.xml                            (Maven project configuration)
├── .gitignore                         (Maven/Java specific ignores)
└── README.md                          (plugin-specific documentation)
```

#### Key Files:

- **`pom.xml`**: Maven Project Object Model
  - Defines project dependencies (Spigot API, libraries)
  - Configures build plugins (compiler, shade plugin for fat JAR)
  - Sets Java version and encoding
  - Specifies repositories for Minecraft APIs

- **`plugin.yml`**: Plugin Descriptor
  - Required by Bukkit/Spigot API
  - Defines plugin name, version, main class
  - Lists commands and permissions
  - Specifies API version compatibility

- **`Main.java`** (to be created): Plugin Entry Point
  - Extends JavaPlugin class
  - Contains onEnable() and onDisable() methods
  - Initializes plugin components

- **`config.yml`** (to be created): User Configuration
  - Allows server admins to customize plugin behavior
  - Contains default values for plugin settings

#### Subdirectories (to be populated):

- **`commands/`**: Command implementations (extends BukkitCommand)
  - Economy commands: `/saldo`, `/pagar`, `/historico`
  - Team commands: `/time` (choose, switch)
  - Guild commands: `/guild` (create, invite, accept, leave)
  - Admin commands: `/econ`, `/auditoria`, `/fly`
  - Panel commands: `/painel` (create, delete, refresh)

- **`listeners/`**: Event listeners for Minecraft events
  - `PlayerLifecycleListener` - Join/quit events
  - `NoTeamMovementListener` - Enforce team selection
  - `SimpleLoginHook` - Authentication integration
  - `AuthMeHook` - Alternative auth system (via reflection)

- **`services/`**: Business logic and game mechanics (core architecture)
  - `EconomyService` - Vault integration, transfers, balance
  - `TeamService` - Solar/Lunar team system
  - `GuildService` - Guild management
  - `PanelService` - Holographic displays (DecentHolograms or native)
  - `TransactionService` - Financial history tracking
  - `AuditService` - Event logging and metrics
  - `DatabaseService` - HikariCP connection pool
  - `PlayerDataService` - YAML-based player persistence

- **`panels/`**: Panel system implementation
  - Panel types: GLOBAL, TEAM, GUILD
  - Metrics calculation and display
  - Real-time updates (configurable intervals)

- **`models/`**: Data structures and POJOs
  - Player data models
  - Transaction records
  - Audit events
  - Panel configurations

- **`utils/`**: Helper classes and utilities
  - Configuration helpers
  - Color/formatting utilities
  - Validation helpers

### 1.2 API Directory (`/backend/api`)

**Purpose**: REST API for communication between Minecraft server and website

#### Planned Structure:
```
api/
├── server.js            (Express server - main entry point)
├── package.json         (Node.js dependencies)
├── .env                 (environment variables - NOT committed)
├── utils/
│   └── database.js      (JSON file persistence layer)
├── database/            (JSON data storage)
│   ├── pedidos.json     (order records)
│   ├── pagamentos.json  (processed payment IDs - anti-duplication)
│   └── vips_ativos.json (activated VIPs awaiting plugin processing)
├── logs/                (operation logs)
│   ├── webhook.log      (payment webhook events)
│   └── errors.log       (error tracking)
└── .gitignore
```

#### Core Endpoints (Planned):

**Payment System:**
- `POST /criar-pix` - Create PIX payment (Mercado Pago integration)
  - Input: `{ valor, nick, uuid, vip }`
  - Output: QR Code, payment link, order ID
- `POST /webhook` - Receive payment notifications from Mercado Pago
  - Validates payment status
  - Prevents duplicate processing
  - Saves to `vips_ativos.json` for plugin consumption

**Player Data:**
- `GET /api/players` - List all players
- `GET /api/players/:uuid` - Get player details
- `GET /api/players/:uuid/stats` - Player statistics

**Server Status:**
- `GET /api/server/status` - Server online status, player count
- `GET /api/server/online-players` - Currently online players

**Authentication:**
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration

#### Purpose:
- **Primary**: Payment processing (PIX via Mercado Pago)
- Provides player statistics to website
- Handles VIP activation workflow
- Exposes server status information
- Manages user authentication for web dashboard

#### Payment Flow:
```
Player → POST /criar-pix → Mercado Pago API → QR Code
Player pays → Mercado Pago → POST /webhook → vips_ativos.json
Plugin monitors file → Activates VIP in-game
```

---

## 2. Frontend Directory (`/frontend`)

**Purpose**: Public-facing website for the Minecraft server

### Current Structure:
```
frontend/
├── public/              (static assets)
│   ├── images/         (logos, banners, icons)
│   ├── favicon.ico     (site favicon)
│   └── robots.txt      (SEO configuration)
├── src/
│   ├── components/     (reusable UI components)
│   │   ├── Header/
│   │   ├── Footer/
│   │   ├── ServerStatus/
│   │   └── PlayerList/
│   ├── pages/          (page components)
│   │   ├── Home/
│   │   ├── About/
│   │   ├── Rules/
│   │   ├── Store/
│   │   └── Dashboard/
│   ├── styles/         (CSS/SCSS files)
│   │   ├── global.css
│   │   └── theme.css
│   ├── utils/          (helper functions)
│   │   ├── api.js      (API client)
│   │   └── formatters.js
│   ├── App.jsx         (main app component - to be created)
│   └── index.jsx       (entry point - to be created)
├── package.json        (npm dependencies and scripts)
├── .gitignore
└── README.md           (frontend documentation - to be created)
```

### Key Files:

- **`package.json`**: NPM Configuration
  - Lists frontend dependencies (React/Vue/Next.js)
  - Defines build and dev scripts
  - Specifies project metadata

- **`App.jsx`** (to be created): Main Application Component
  - Root component for the React app
  - Handles routing and global state

- **`index.jsx`** (to be created): Application Entry Point
  - Renders the React app to the DOM
  - Sets up providers and configurations

### Planned Features:
- Server status display (online players, MOTD)
- Player statistics and leaderboards
- Rule pages and guides
- Store/donation system
- Admin dashboard (with authentication)

---

## 3. Server Directory (`/server`)

**Purpose**: Minecraft server runtime environment

### Structure:
```
server/
├── plugins/            (compiled plugins go here)
│   └── YourPlugin.jar  (built from backend/plugin)
├── world/              (main world save data)
│   ├── region/
│   ├── data/
│   └── level.dat
├── world_nether/       (nether dimension)
├── world_the_end/      (end dimension)
├── logs/               (server logs)
│   └── latest.log
├── server.jar          (Spigot/Paper server - to be downloaded)
├── eula.txt            (Minecraft EULA acceptance)
├── server.properties   (server configuration)
├── ops.json            (server operators)
├── whitelist.json      (whitelisted players)
├── banned-players.json (banned players)
├── banned-ips.json     (banned IPs)
└── .gitignore
```

### Key Files:

- **`server.jar`**: Minecraft Server Software
  - Spigot or Paper recommended
  - Download from official sources
  - Version should match plugin API version

- **`server.properties`**: Server Configuration
  - World settings (difficulty, gamemode)
  - Network settings (port, IP)
  - Performance tuning
  - World generation options

- **`eula.txt`**: End User License Agreement
  - Must set `eula=true` to start server
  - Acknowledges Minecraft EULA

- **`plugins/`**: Plugin Installation Directory
  - Place compiled JAR files here
  - Server loads plugins on startup
  - Your custom plugin JAR goes here

### Purpose:
- Runs the actual Minecraft server
- Stores world data and player information
- Loads and executes plugins
- Separate from development code for clean separation

---

## 4. Config Directory (`/config`)

**Purpose**: Centralized configuration files

### Planned Contents:
```
config/
├── database.yml        (database connection settings)
├── api.yml             (API configuration)
├── security.yml        (security settings)
└── environment/
    ├── development.env
    ├── staging.env
    └── production.env
```

### Purpose:
- Centralized configuration management
- Environment-specific settings
- Shared configuration between backend components
- Security-sensitive values (should not be committed to git)

---

## 5. Docs Directory (`/docs`)

**Purpose**: Project documentation

### Current Contents:
```
docs/
├── PROJECT_STRUCTURE.md       (this file)
├── ROADMAP.md                 (project roadmap - created)
├── PLUGIN_DEVELOPMENT.md      (plugin dev guide - to be created)
├── API_DOCUMENTATION.md       (API reference - to be created)
├── DEPLOYMENT.md              (deployment guide - to be created)
├── CHANGELOG.md               (version history - to be created)
├── SERVICE_ARCHITECTURE.md    (services design patterns - to be created)
├── PAYMENT_INTEGRATION.md     (Mercado Pago integration - to be created)
└── DATABASE_SCHEMA.md         (database structure - to be created)
```

### Purpose:
- Developer guides
- API documentation
- Deployment instructions
- Architecture decisions
- Version history
- Service layer design patterns
- Payment system workflows

---

## 6. Service Architecture (Plugin Core Design)

**Purpose**: Understanding the modular service pattern used in the plugin

### Service Layer Pattern

The plugin follows a **Service-Oriented Architecture** where each major feature is encapsulated in a dedicated service class. Services are initialized once in `NexusPlugin.onEnable()` and accessed globally.

### Core Services

**1. EconomyService**
- **Purpose**: Manages all financial operations
- **Integrations**: Vault API
- **Functions**:
  - Get/set player balance
  - Transfer money between players
  - Withdraw/deposit operations
  - Freeze/unfreeze economy (admin)
  - Validate transactions (limits, cooldowns)
- **Database**: Records all transactions via TransactionService
- **Audit**: Logs all operations via AuditService

**2. TeamService**
- **Purpose**: Solar/Lunar team system
- **Persistence**: YAML files
- **Functions**:
  - Mandatory team selection on first join
  - Team switching (with cost)
  - Player name coloring (Solar = yellow, Lunar = purple)
  - Team statistics
- **Integration**: Blocks movement until team chosen

**3. GuildService**
- **Purpose**: Clan/guild management
- **Persistence**: YAML files
- **Functions**:
  - Create/disband guilds
  - Invite/accept/kick members
  - Guild leader management
  - Member listings
- **Future**: Guild banks, guild wars, territory

**4. PanelService**
- **Purpose**: Holographic stat displays
- **Integrations**: DecentHolograms (preferred) or native TextDisplay
- **Panel Types**:
  - GLOBAL - Server-wide metrics
  - TEAM - Solar/Lunar specific stats
  - GUILD - Individual guild metrics
- **Functions**:
  - Create/delete panels at locations
  - Auto-refresh at configurable intervals
  - Real-time metrics calculation
  - Persistent storage in JSON

**5. TransactionService**
- **Purpose**: Financial history tracking
- **Database**: `transactions` table (SQLite/MySQL)
- **Functions**:
  - Record all money transfers
  - Query transaction history
  - Generate reports (CSV/JSON export)
  - Filter by player, date range, amount
- **Schema**: from_uuid, to_uuid, amount, note, timestamp

**6. AuditService**
- **Purpose**: Event logging and analytics
- **Database**: `audit_events` table (SQLite/MySQL)
- **Functions**:
  - Log all significant events
  - Count events by type/player/time
  - Generate metrics for panels
  - Export audit reports
- **Event Types**: PLAYER_JOIN, MONEY_TRANSFER, TEAM_SWITCH, GUILD_CREATE, etc.
- **Schema**: type, player, target, context (JSON), timestamp

**7. DatabaseService**
- **Purpose**: Database abstraction layer
- **Technology**: HikariCP connection pooling
- **Support**: SQLite (development) or MySQL (production)
- **Functions**:
  - Initialize connection pool
  - Create tables automatically
  - Execute queries safely
  - Handle connection lifecycle
- **Configuration**: Defined in config.yml

**8. PlayerDataService**
- **Purpose**: Player data persistence
- **Persistence**: YAML files per player
- **Data Stored**:
  - UUID, name, balance
  - Team, guild membership
  - First join, last seen timestamps
  - Custom plugin data
- **Functions**:
  - Load data on join
  - Save data on quit
  - Auto-save periodically

### Service Interaction Flow

```
Command → Service → Multiple Services → Database → Audit
Example: /pagar
  PagarCommand
    → EconomyService.transfer()
      → Vault API (withdraw/deposit)
      → TransactionService.record()
      → AuditService.log(MONEY_TRANSFER)
```

### Benefits of Service Architecture

1. **Separation of Concerns** - Each service handles one domain
2. **Testability** - Services can be unit tested independently
3. **Reusability** - Commands and listeners share service logic
4. **Maintainability** - Changes isolated to specific services
5. **Scalability** - Easy to add new services without affecting existing ones

---

## 7. Payment Integration Architecture

**Purpose**: Understanding the real-money to game-currency bridge

### Payment Flow Overview

```
1. Player Request (In-Game)
   ↓
2. Backend API (POST /criar-pix)
   ↓
3. Mercado Pago API (Generate PIX)
   ↓
4. QR Code Displayed (In-Game)
   ↓
5. Player Pays (Banking App)
   ↓
6. Webhook Notification (POST /webhook)
   ↓
7. Backend Validates Payment
   ↓
8. Save to vips_ativos.json
   ↓
9. Plugin Monitors File/API
   ↓
10. VIP Activated (In-Game)
```

### Backend Components

**Files:**
- `backend/server.js` - Express server with webhook endpoint
- `backend/utils/database.js` - JSON file persistence
- `backend/database/pedidos.json` - Order history
- `backend/database/pagamentos.json` - Processed payment IDs (anti-duplication)
- `backend/database/vips_ativos.json` - Pending VIP activations

**Security Measures:**
1. **Anti-Duplication**: `jaFoiProcessado()` checks payment ID before processing
2. **Status Validation**: Only processes `approved` payments
3. **Method Validation**: Only processes PIX payments
4. **Webhook Signature**: Mercado Pago signs webhooks (can validate)

**Environment Variables (.env):**
```
MP_ACCESS_TOKEN=your_mercado_pago_token
NGROK_URL=https://your-domain.ngrok.io
PORT=3333
```

### Plugin-Backend Communication

**Method 1: File Monitoring** (Simple)
- Plugin periodically reads `vips_ativos.json`
- Processes new entries
- Removes processed entries

**Method 2: REST API** (Advanced)
- Plugin makes HTTP request to backend
- Backend returns pending activations
- More real-time, requires HTTP client in plugin

### VIP Activation Flow (Plugin Side)

```java
// Scheduled task runs every 30 seconds
VipChecker.check() {
  1. Read vips_ativos.json
  2. For each VIP:
     - Find player by UUID
     - Apply VIP permissions (LuckPerms)
     - Give VIP kit/items
     - Send confirmation message
     - Log to audit
  3. Clear processed VIPs from file
}
```

---

## 8. Database Architecture

### Database Types Supported

**SQLite (Default - Development)**
- File-based: `plugins/Nexus/database.db`
- No server required
- Easy setup
- Suitable for small/medium servers

**MySQL (Recommended - Production)**
- Client-server architecture
- Better performance at scale
- Supports replication/backups
- Industry standard

### Database Schema

**Table: `audit_events`**
```sql
CREATE TABLE audit_events (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    type VARCHAR(50) NOT NULL,           -- Event type
    player VARCHAR(36),                   -- Player UUID
    target VARCHAR(36),                   -- Target UUID (optional)
    context TEXT,                         -- JSON additional data
    timestamp BIGINT NOT NULL             -- Unix timestamp (ms)
);

-- Indexes for performance
CREATE INDEX idx_audit_timestamp ON audit_events(timestamp);
CREATE INDEX idx_audit_type ON audit_events(type);
CREATE INDEX idx_audit_player ON audit_events(player);
```

**Table: `transactions`**
```sql
CREATE TABLE transactions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    from_uuid VARCHAR(36) NOT NULL,      -- Sender UUID
    to_uuid VARCHAR(36) NOT NULL,        -- Receiver UUID
    amount DECIMAL(15,2) NOT NULL,       -- Transfer amount
    note TEXT,                            -- Optional note/reason
    timestamp BIGINT NOT NULL             -- Unix timestamp (ms)
);

-- Indexes for performance
CREATE INDEX idx_trans_from ON transactions(from_uuid);
CREATE INDEX idx_trans_to ON transactions(to_uuid);
CREATE INDEX idx_trans_timestamp ON transactions(timestamp);
```

### HikariCP Connection Pooling

**Configuration (config.yml):**
```yaml
storage:
  tipo: mysql
  mysql:
    host: 127.0.0.1
    porta: 3306
    database: nexus
    usuario: root
    senha: "secure_password"
  hikari:
    maximum-pool-size: 10
    minimum-idle: 2
    connection-timeout: 30000
```

**Benefits:**
- Reuses connections (no overhead)
- Handles connection failures gracefully
- Configurable pool size
- Industry-standard library

---

## 9. Build and Output Files

### Plugin Build Output
- **Location**: `backend/plugin/target/`
- **Contents**: 
  - `minecraft-plugin-1.0-SNAPSHOT.jar` (compiled plugin)
  - `nexus-plugin-0.1.0-SNAPSHOT-shaded.jar` (with dependencies)
  - `classes/` (compiled .class files)
  - `maven-archiver/` (Maven metadata)
  
**Build Command:**
```bash
mvn clean package
```

**Shaded JAR**: Includes HikariCP, SQLite driver, Gson (dependencies not provided by server)

### Frontend Build Output
- **Location**: `frontend/build/` or `frontend/dist/`
- **Contents**: Optimized static files for production deployment
- **Build Command**: `npm run build`

### Backend API (No Build)
- Node.js runs directly from source
- **Start Command**: `npm start` or `node server.js`
- Dependencies installed via `npm install`

### Note on Git Tracking
- Build outputs are gitignored
- Only source code is tracked
- Artifacts are generated during build process
- `.env` files never committed (security)

---

## Technology Stack

### Backend Plugin:
- **Language**: Java 17+
- **Build Tool**: Maven 3.8+
- **Framework**: Spigot/Paper API
- **Testing**: JUnit 5

### Backend API:
- **Option 1**: Node.js + Express
- **Option 2**: Java Spring Boot
- **Database**: MySQL or MongoDB (TBD)

### Frontend:
- **Framework**: React, Vue, or Next.js (TBD)
- **Styling**: CSS Modules, Tailwind, or Material-UI (TBD)
- **Build Tool**: Webpack, Vite, or Next.js built-in

### Server:
- **Platform**: Spigot or Paper
- **Java Version**: 17+ (LTS)
- **Database**: Same as backend API

---

## Development Workflow

1. **Plugin Development**:
   - Write code in `backend/plugin/src/`
   - Build with Maven: `mvn clean package`
   - Copy JAR to `server/plugins/`
   - Restart server to test

2. **API Development**:
   - Develop in `backend/api/`
   - Run locally for testing
   - Integrate with plugin for data access

3. **Frontend Development**:
   - Develop in `frontend/src/`
   - Run dev server for hot reloading
   - Connect to API for data

4. **Testing**:
   - Unit tests in respective test directories
   - Integration testing with full stack
   - Manual testing on server

---

## Future Additions

As the project evolves, this document will be updated to reflect:
- New directories and files
- Additional dependencies
- Architectural changes
- Deployment configurations
- Production setup details

---

**Note**: This is a living document. Update it whenever you make structural changes to the project.

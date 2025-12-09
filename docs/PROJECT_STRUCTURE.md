# Project Structure Documentation

This document provides a comprehensive overview of the Minecraft Server project structure, explaining the purpose of each folder and file.

**Last Updated**: December 9, 2025  
**Version**: 1.0.0

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

- **`commands/`**: Command implementations
- **`listeners/`**: Event listeners for Minecraft events
- **`managers/`**: Business logic and game mechanics
- **`models/`**: Data structures and POJOs
- **`utils/`**: Helper classes and utilities

### 1.2 API Directory (`/backend/api`)

**Purpose**: REST API for communication between Minecraft server and website

#### Planned Structure:
```
api/
├── src/
│   ├── controllers/     (API endpoint controllers)
│   ├── models/          (database models)
│   ├── routes/          (API route definitions)
│   ├── middleware/      (authentication, validation)
│   ├── services/        (business logic)
│   └── config/          (database, server config)
├── package.json         (Node.js dependencies - if using Node)
├── pom.xml              (Maven config - if using Spring Boot)
└── .gitignore
```

#### Purpose:
- Provides player statistics to website
- Handles whitelist/ban management
- Exposes server status information
- Manages user authentication for web dashboard

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
└── CHANGELOG.md               (version history - to be created)
```

### Purpose:
- Developer guides
- API documentation
- Deployment instructions
- Architecture decisions
- Version history

---

## Build and Output Files

### Plugin Build Output:
- **Location**: `backend/plugin/target/`
- **Contents**: 
  - `minecraft-plugin-1.0-SNAPSHOT.jar` (compiled plugin)
  - `classes/` (compiled .class files)
  - `maven-archiver/` (Maven metadata)

### Frontend Build Output:
- **Location**: `frontend/build/` or `frontend/dist/`
- **Contents**: Optimized static files for production deployment

### Note on Git Tracking:
- Build outputs are gitignored
- Only source code is tracked
- Artifacts are generated during build process

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

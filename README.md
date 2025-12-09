# Minecraft Server Project

A comprehensive Minecraft server project with custom plugin development and web interface.

## Project Structure

```
MinecraftServer/
├── backend/           # Backend services
│   ├── plugin/       # Custom Minecraft plugin (Maven project)
│   └── api/          # REST API for web integration
├── frontend/         # Website frontend
├── server/           # Minecraft server runtime
├── config/           # Configuration files
└── docs/             # Documentation
```

## Quick Start

### Backend (Plugin Development)
1. Navigate to `backend/plugin/`
2. Configure `pom.xml` with your project details
3. Build with Maven: `mvn clean package`
4. Plugin JAR will be in `target/` folder

### Frontend (Website)
1. Navigate to `frontend/`
2. Install dependencies: `npm install`
3. Start development server: `npm run dev`

### Server Setup
1. Navigate to `server/`
2. Download server JAR (Spigot/Paper)
3. Accept EULA
4. Start server

## Documentation

- [Project Structure Guide](./docs/PROJECT_STRUCTURE.md) - Detailed explanation of folders and files
- [Development Roadmap](./docs/ROADMAP.md) - Project milestones and timeline
- [Plugin Development](./docs/PLUGIN_DEVELOPMENT.md) - Guide for plugin creation
- [API Documentation](./docs/API_DOCUMENTATION.md) - REST API endpoints

## Technologies

### Backend Plugin
- **Language**: Java 21 LTS
- **Build Tool**: Maven 3.8+
- **Server Platform**: Spigot/Paper 1.20.4+
- **Key Libraries**:
  - Spigot API (provided)
  - Vault API (economy integration)
  - HikariCP (database connection pooling)
  - SQLite/MySQL JDBC drivers
  - Gson (JSON serialization)

### Backend API (Payment System)
- **Runtime**: Node.js 18+
- **Framework**: Express
- **Payment Gateway**: Mercado Pago (PIX)
- **Storage**: JSON files (simple) or MongoDB (advanced)
- **Key Libraries**: dotenv, node-fetch

### Frontend Website
- **Framework**: React/Vue/Next.js (TBD)
- **Styling**: Tailwind CSS/Material-UI (TBD)
- **Build Tool**: Vite/Next.js (TBD)

### Database
- **Development**: SQLite (file-based, zero-config)
- **Production**: MySQL 8.0+ (recommended for scale)
- **Connection Pooling**: HikariCP

## Core Features

### 🎮 Plugin Features (In-Game)
- **Economy System**: Vault integration with transfer, balance, history
- **Team System**: Solar/Lunar mandatory team selection with colored names
- **Guild System**: Create, join, manage clans with member tracking
- **Holographic Panels**: Real-time metrics displays (global, team, guild)
- **Transaction Tracking**: Complete financial history with exports
- **Audit System**: Comprehensive event logging and analytics
- **Admin Tools**: Economy management, flight toggle, audit queries

### 💳 Payment Integration
- **PIX Payments**: Brazilian instant payment via Mercado Pago
- **VIP Activation**: Automatic in-game perks after payment approval
- **Webhook System**: Real-time payment notifications
- **Anti-Duplication**: Prevents double-processing of payments
- **Order Tracking**: Complete payment history and status

### 🌐 Website Features (Planned)
- Server status and player count
- Player statistics and leaderboards
- VIP store with PIX checkout
- Admin dashboard
- Guild rankings

## Architecture Highlights

### Service-Oriented Design
The plugin uses a modular service architecture:
- **EconomyService** - Financial operations
- **TeamService** - Solar/Lunar management
- **GuildService** - Clan management
- **PanelService** - Holographic displays
- **TransactionService** - Financial history
- **AuditService** - Event logging
- **DatabaseService** - Data persistence
- **PlayerDataService** - Player data

### Payment Flow
```
Player (Game) → API (/criar-pix) → Mercado Pago → QR Code
Player Pays → Mercado Pago Webhook → API (/webhook) → vips_ativos.json
Plugin monitors → Activates VIP → Player receives perks
```

### Database Schema
- **audit_events**: All significant events (joins, transfers, team changes)
- **transactions**: Money transfers with timestamps and notes
- **player_data**: YAML-based per-player persistence

## Project Philosophy

This project combines proven patterns from production Minecraft servers:
1. **Modular Architecture** - Services separate concerns cleanly
2. **Real Economy Integration** - PIX payments bridge real and game money
3. **Comprehensive Auditing** - Every action logged for analytics/security
4. **Flexible Storage** - SQLite for dev, MySQL for production
5. **Graceful Degradation** - Soft dependencies with fallbacks

## License

[Add your license here]

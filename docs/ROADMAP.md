# Minecraft Server Project Roadmap

This document outlines the development roadmap from project inception to completion.

**Project Start Date**: December 9, 2025  
**Target Completion**: TBD based on scope  
**Current Phase**: Phase 0 - Project Setup

---

## Overview

The project is divided into 6 main phases, each with specific milestones and deliverables.

```
Phase 0: Project Setup (✓ Current)
    ↓
Phase 1: Backend Plugin Development
    ↓
Phase 2: Server Setup & Testing
    ↓
Phase 3: Backend API Development
    ↓
Phase 4: Frontend Website Development
    ↓
Phase 5: Integration & Testing
    ↓
Phase 6: Deployment & Launch
```

---

## Phase 0: Project Setup ✓ IN PROGRESS

**Duration**: 1 day  
**Status**: In Progress

### Objectives:
- [x] Create project structure
- [x] Initialize Git repository
- [x] Create documentation framework
- [x] Define project roadmap
- [ ] Set up development environment
- [ ] Install required tools (Maven, Java, Node.js)

### Deliverables:
- [x] Directory structure
- [x] README.md with project overview
- [x] PROJECT_STRUCTURE.md documentation
- [x] ROADMAP.md (this document)
- [ ] .gitignore files
- [ ] Initial Git commit

### Tools Setup:
- [ ] Java JDK 17+ installed
- [ ] Maven 3.8+ installed
- [ ] IntelliJ IDEA or Eclipse IDE
- [ ] Node.js and npm (for frontend)
- [ ] Git configured
- [ ] Code editor (VS Code) configured

---

## Phase 1: Backend Plugin Development

**Duration**: 4-5 weeks  
**Status**: Not Started

**Architecture**: Service-Oriented Design with modular components

### Milestone 1.1: Maven Project Setup (Week 1)

#### Tasks:
- [ ] Configure `pom.xml` with Spigot API dependency
- [ ] Add required Maven plugins (Compiler, Shade)
- [ ] Set up project build configuration
- [ ] Configure `plugin.yml` with plugin metadata
- [ ] Create Main plugin class extending JavaPlugin
- [ ] Implement basic onEnable() and onDisable()
- [ ] Test successful plugin compilation

#### Deliverables:
- [ ] Fully configured Maven project
- [ ] Plugin builds successfully
- [ ] Basic plugin loads on test server

### Milestone 1.2: Core Plugin Features (Week 2)

#### Tasks:
- [ ] Implement configuration system (config.yml)
- [ ] Create command framework
- [ ] Implement at least 3 custom commands
- [ ] Set up event listener system
- [ ] Create utility classes for common tasks
- [ ] Add logging system
- [ ] Implement basic error handling

#### Deliverables:
- [ ] Working command system
- [ ] Event listeners responding to game events
- [ ] Configuration that can be edited by admins

### Milestone 1.3: Service Layer Architecture (Week 3)

#### Tasks:
- [ ] Create EconomyService (Vault integration)
  - Balance management
  - Transfer system with validations
  - Freeze/unfreeze economy
- [ ] Create TeamService (Solar/Lunar system)
  - Mandatory team selection
  - Team switching with cost
  - Player name coloring
- [ ] Create GuildService (Clan system)
  - Guild creation/disbanding
  - Invite/accept/kick members
  - Guild data persistence
- [ ] Create PlayerDataService
  - YAML-based persistence
  - Auto-load on join, auto-save on quit
- [ ] Implement data validation across all services

#### Deliverables:
- [ ] Service layer fully functional
- [ ] YAML persistence working
- [ ] Business logic separated from commands

### Milestone 1.4: Database Integration (Week 3-4)

#### Tasks:
- [ ] Create DatabaseService with HikariCP
  - Support SQLite and MySQL
  - Connection pooling
  - Automatic table creation
- [ ] Create TransactionService
  - Record all money transfers
  - Query transaction history
  - Export to CSV/JSON
- [ ] Create AuditService
  - Log all significant events
  - Count events by type/time
  - Generate metrics for panels
- [ ] Design and create database schema
  - `transactions` table
  - `audit_events` table
  - Proper indexes for performance

#### Deliverables:
- [ ] Database connectivity (SQLite/MySQL)
- [ ] Transaction tracking system
- [ ] Comprehensive audit logging

### Milestone 1.5: Advanced Features (Week 4)

#### Tasks:
- [ ] Create PanelService (Holographic displays)
  - Support DecentHolograms and native TextDisplay
  - GLOBAL, TEAM, GUILD panel types
  - Auto-refresh with real-time metrics
  - JSON persistence
- [ ] Implement command cooldowns and limits
- [ ] Add GUI/inventory menus (optional)
- [ ] Create admin commands
  - `/econ` - Economy management
  - `/auditoria` - Event queries
  - `/_transacoes` - Transaction reports
  - `/fly` - Toggle flight
- [ ] Integration hooks for SimpleLogin/AuthMe

#### Deliverables:
- [ ] Feature-complete plugin
- [ ] Holographic panel system
- [ ] Admin management tools
- [ ] Authentication hooks

### Milestone 1.6: Testing & Polish (Week 5)

#### Tasks:
- [ ] Write unit tests for services
  - EconomyService tests
  - TransactionService tests
  - AuditService tests
- [ ] Perform integration testing
  - Test service interactions
  - Test database operations
  - Test command workflows
- [ ] Fix bugs and edge cases
- [ ] Optimize performance
  - Database query optimization
  - HikariCP tuning
  - Panel refresh optimization
- [ ] Add comprehensive logging
- [ ] Document plugin API (Javadoc)
- [ ] Create admin guide (commands, permissions, config)

#### Deliverables:
- [ ] Tested and stable plugin
- [ ] Plugin documentation (Javadoc)
- [ ] Admin configuration guide
- [ ] Performance benchmarks

---

## Phase 2: Server Setup & Testing

**Duration**: 1 week  
**Status**: Not Started  
**Dependencies**: Phase 1 Milestone 1.1 complete

### Milestone 2.1: Server Installation (Days 1-2)

#### Tasks:
- [ ] Download Paper/Spigot server JAR
- [ ] Configure server.properties
- [ ] Accept Minecraft EULA
- [ ] Set up initial world
- [ ] Configure server performance settings
- [ ] Install essential plugins (WorldEdit, etc.)
- [ ] Set up ops and permissions

#### Deliverables:
- [ ] Running Minecraft server
- [ ] Basic server configuration

### Milestone 2.2: Plugin Testing (Days 3-5)

#### Tasks:
- [ ] Deploy custom plugin to server
- [ ] Test all commands in-game
- [ ] Test event listeners
- [ ] Test configuration changes
- [ ] Perform stress testing with multiple players
- [ ] Test plugin reload functionality
- [ ] Document any issues found

#### Deliverables:
- [ ] Fully tested plugin on live server
- [ ] Bug report and fixes
- [ ] Performance benchmarks

### Milestone 2.3: Server Hardening (Days 6-7)

#### Tasks:
- [ ] Configure security settings
- [ ] Set up whitelist/ban system
- [ ] Configure anti-cheat measures
- [ ] Set up automatic backups
- [ ] Configure logging and monitoring
- [ ] Test disaster recovery procedures

#### Deliverables:
- [ ] Secure server configuration
- [ ] Backup system in place
- [ ] Server administration guide

---

## Phase 3: Backend API Development

**Duration**: 2-3 weeks  
**Status**: Not Started  
**Dependencies**: Phase 1 complete

### Milestone 3.1: Payment API Setup (Week 1)

#### Tasks:
- [ ] Initialize Node.js + Express project
- [ ] Install dependencies (express, dotenv, node-fetch)
- [ ] Create `.env` configuration file
  - Mercado Pago access token
  - Ngrok/public URL
  - Server port
- [ ] Set up project structure
  - `server.js` - Main Express server
  - `utils/database.js` - JSON file persistence
  - `database/` - JSON data storage
  - `logs/` - Operation logs
- [ ] Implement basic routing and error handling
- [ ] Set up ngrok for webhook testing

#### Deliverables:
- [ ] Node.js server running
- [ ] Environment configured
- [ ] Ngrok tunnel active
- [ ] Basic health check endpoint

### Milestone 3.2: Payment Integration (Week 1-2)

#### Tasks:
- [ ] Implement PIX payment creation endpoint
  - `POST /criar-pix`
  - Input: valor, nick, uuid, vip
  - Generate order UUID
  - Call Mercado Pago API
  - Return QR Code and payment data
- [ ] Implement webhook endpoint
  - `POST /webhook`
  - Receive payment notifications
  - Validate payment status
  - Anti-duplication check
  - Process approved payments
- [ ] Create JSON persistence utilities
  - `lerArquivo()`, `salvarArquivo()`
  - `salvarPedido()`, `jaFoiProcessado()`
  - `marcarComoProcessado()`, `ativarVipArquivo()`
- [ ] Implement logging system
  - Webhook events to `logs/webhook.log`
  - Errors to `logs/errors.log`
- [ ] Test payment flow end-to-end
  - Create PIX payment
  - Simulate webhook notification
  - Verify data in `vips_ativos.json`

#### Deliverables:
- [ ] Working payment endpoint
- [ ] Webhook receiving notifications
- [ ] Anti-duplication working
- [ ] VIP data persisted correctly
- [ ] Comprehensive logging

### Milestone 3.3: Additional API Endpoints (Week 2)

#### Tasks:
- [ ] Implement player data endpoints
  - GET /api/players
  - GET /api/players/:uuid
  - GET /api/players/:uuid/stats
- [ ] Implement server status endpoints
  - GET /api/server/status
  - GET /api/server/online-players
- [ ] Implement authentication system
  - POST /api/auth/login
  - POST /api/auth/register
- [ ] Add request validation middleware
- [ ] Implement error handling middleware
- [ ] Add API documentation (Swagger/OpenAPI)

#### Deliverables:
- [ ] Complete REST API
- [ ] API documentation
- [ ] Postman/Thunder Client collection

### Milestone 3.3: Plugin-API Integration (Week 2-3)

#### Tasks:
- [ ] Create database models for plugin data
- [ ] Implement data sync between plugin and database
- [ ] Create webhook system for real-time updates
- [ ] Add caching layer (Redis optional)
- [ ] Implement rate limiting
- [ ] Add API authentication tokens
- [ ] Test data flow plugin → database → API

#### Deliverables:
- [ ] Real-time data synchronization
- [ ] Secure API access
- [ ] Performance optimization

### Milestone 3.4: Admin API (Week 3)

#### Tasks:
- [ ] Implement admin authentication
- [ ] Create admin-only endpoints
  - POST /api/admin/whitelist
  - POST /api/admin/ban
  - POST /api/admin/kick
  - GET /api/admin/logs
- [ ] Add role-based access control
- [ ] Implement audit logging
- [ ] Create admin dashboard data endpoints

#### Deliverables:
- [ ] Admin API functionality
- [ ] Security measures in place
- [ ] Audit log system

---

## Phase 4: Frontend Website Development

**Duration**: 3-4 weeks  
**Status**: Not Started  
**Dependencies**: Phase 3 Milestone 3.2 complete

### Milestone 4.1: Frontend Project Setup (Week 1)

#### Tasks:
- [ ] Choose frontend framework (React/Vue/Next.js)
- [ ] Initialize project with create-react-app/vite
- [ ] Set up routing system
- [ ] Configure build tools
- [ ] Set up styling solution (CSS/Tailwind/MUI)
- [ ] Configure API client (axios/fetch)
- [ ] Set up development environment

#### Deliverables:
- [ ] Frontend project initialized
- [ ] Basic routing working
- [ ] API client configured

### Milestone 4.2: Core Pages (Week 1-2)

#### Tasks:
- [ ] Create Home page
  - Hero section
  - Server info display
  - Call-to-action buttons
- [ ] Create About page
  - Server description
  - Staff team
  - History
- [ ] Create Rules page
  - Server rules list
  - Guidelines
  - Consequences
- [ ] Create responsive navigation
- [ ] Create footer component
- [ ] Implement mobile responsiveness

#### Deliverables:
- [ ] Static pages complete
- [ ] Responsive design
- [ ] Navigation system

### Milestone 4.3: Dynamic Features (Week 2-3)

#### Tasks:
- [ ] Implement server status widget
  - Online/offline indicator
  - Player count
  - MOTD display
- [ ] Create player statistics page
  - Leaderboards
  - Individual player profiles
  - Statistics visualizations
- [ ] Implement live player list
  - Currently online players
  - Avatars
  - Real-time updates
- [ ] Create search functionality
- [ ] Add loading states and skeletons

#### Deliverables:
- [ ] Real-time server data display
- [ ] Interactive statistics
- [ ] Search functionality

### Milestone 4.4: User System (Week 3-4)

#### Tasks:
- [ ] Create login/register pages
- [ ] Implement authentication flow
- [ ] Create user dashboard
  - Personal statistics
  - Profile editing
  - Settings
- [ ] Implement protected routes
- [ ] Add session management
- [ ] Create logout functionality

#### Deliverables:
- [ ] User authentication system
- [ ] User dashboard
- [ ] Secure routes

### Milestone 4.5: Admin Dashboard (Week 4)

#### Tasks:
- [ ] Create admin login
- [ ] Build admin panel UI
  - Server controls
  - Player management
  - Whitelist/ban interface
  - Log viewer
- [ ] Implement admin actions
- [ ] Add confirmation dialogs
- [ ] Create admin-only routes

#### Deliverables:
- [ ] Functional admin dashboard
- [ ] Remote server management
- [ ] Security measures

---

## Phase 5: Integration & Testing

**Duration**: 2 weeks  
**Status**: Not Started  
**Dependencies**: Phases 1-4 complete

### Milestone 5.1: Full Stack Integration (Week 1)

#### Tasks:
- [ ] Test complete data flow: Plugin → Database → API → Frontend
- [ ] Verify real-time updates
- [ ] Test all API endpoints from frontend
- [ ] Fix integration bugs
- [ ] Optimize API calls
- [ ] Test error handling across stack
- [ ] Verify security measures

#### Deliverables:
- [ ] Fully integrated system
- [ ] Integration test suite
- [ ] Bug fixes

### Milestone 5.2: User Acceptance Testing (Week 1-2)

#### Tasks:
- [ ] Create test scenarios
- [ ] Perform UAT with test users
- [ ] Test on different devices (desktop, tablet, mobile)
- [ ] Test on different browsers
- [ ] Collect user feedback
- [ ] Test accessibility features
- [ ] Verify performance under load

#### Deliverables:
- [ ] UAT report
- [ ] List of improvements
- [ ] Performance metrics

### Milestone 5.3: Polish & Bug Fixes (Week 2)

#### Tasks:
- [ ] Fix reported bugs
- [ ] Improve UI/UX based on feedback
- [ ] Optimize loading times
- [ ] Add loading indicators
- [ ] Improve error messages
- [ ] Add tooltips and help text
- [ ] Final code cleanup

#### Deliverables:
- [ ] Polished user experience
- [ ] All critical bugs fixed
- [ ] Performance optimized

---

## Phase 6: Deployment & Launch

**Duration**: 1-2 weeks  
**Status**: Not Started  
**Dependencies**: Phase 5 complete

### Milestone 6.1: Deployment Preparation (Week 1)

#### Tasks:
- [ ] Choose hosting providers
  - Server hosting (Minecraft)
  - API hosting (VPS/Cloud)
  - Frontend hosting (Vercel/Netlify/VPS)
  - Database hosting
- [ ] Set up production environments
- [ ] Configure domain names
- [ ] Set up SSL certificates
- [ ] Configure environment variables
- [ ] Set up monitoring and alerting
- [ ] Create backup strategies

#### Deliverables:
- [ ] Production environments ready
- [ ] Domain configured
- [ ] SSL enabled

### Milestone 6.2: Deployment (Week 1-2)

#### Tasks:
- [ ] Deploy database
- [ ] Deploy backend API
- [ ] Deploy frontend website
- [ ] Deploy Minecraft server
- [ ] Install and configure plugin
- [ ] Run production smoke tests
- [ ] Set up monitoring dashboards
- [ ] Configure automated backups

#### Deliverables:
- [ ] All components deployed
- [ ] System monitored
- [ ] Backups configured

### Milestone 6.3: Launch (Week 2)

#### Tasks:
- [ ] Final pre-launch checks
- [ ] Create launch announcement
- [ ] Open server to public/whitelist
- [ ] Monitor for issues
- [ ] Respond to user feedback
- [ ] Document known issues
- [ ] Plan post-launch updates

#### Deliverables:
- [ ] Public server launch
- [ ] Launch announcement
- [ ] Post-launch support plan

---

## Phase 7: Post-Launch (Ongoing)

**Status**: Not Started  
**Dependencies**: Phase 6 complete

### Ongoing Tasks:

#### Maintenance:
- [ ] Monitor server performance
- [ ] Apply security updates
- [ ] Fix bugs as reported
- [ ] Update to new Minecraft versions
- [ ] Regular backups verification

#### Community Management:
- [ ] Moderate server
- [ ] Respond to player issues
- [ ] Manage whitelist/bans
- [ ] Create events
- [ ] Gather feedback

#### Development:
- [ ] Add new plugin features
- [ ] Improve website
- [ ] Add new API endpoints
- [ ] Optimize performance
- [ ] Update documentation

### Future Enhancements (Backlog):
- [ ] Mobile app
- [ ] Discord bot integration
- [ ] Mini-games system
- [ ] Economy system expansion
- [ ] Custom resource pack
- [ ] Seasonal events
- [ ] Store/donation system
- [ ] Advanced analytics
- [ ] Community forums

---

## Risk Management

### Identified Risks:

1. **Scope Creep**
   - **Mitigation**: Stick to roadmap, prioritize core features
   - **Impact**: High

2. **Technical Challenges**
   - **Mitigation**: Research before implementation, ask for help
   - **Impact**: Medium

3. **Time Constraints**
   - **Mitigation**: Set realistic deadlines, adjust scope if needed
   - **Impact**: Medium

4. **Server Costs**
   - **Mitigation**: Start with budget hosting, scale as needed
   - **Impact**: Low-Medium

5. **Player Retention**
   - **Mitigation**: Regular content updates, community engagement
   - **Impact**: High

---

## Success Metrics

### Phase Completion Criteria:
- All milestone tasks completed
- All tests passing
- Documentation updated
- Code reviewed and merged

### Project Success Criteria:
- Server stable and running 24/7
- Website live and functional
- Plugin working without critical bugs
- Positive player feedback
- Active player base (target TBD)

---

## Notes

- This roadmap is flexible and will be adjusted based on progress
- Each phase can be extended if needed
- Priorities may shift based on feedback
- Regular reviews every 2 weeks to assess progress

---

**Last Updated**: December 9, 2025  
**Next Review**: TBD


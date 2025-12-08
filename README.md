# MinecraftServer

A complete structure for a Minecraft server project with a custom plugin backend and a website frontend.

## Project Structure

```
MinecraftServer/
├── plugin/                 # Backend - Custom Minecraft Plugin
│   ├── src/
│   │   └── main/
│   │       ├── java/       # Java source code
│   │       └── resources/  # Plugin resources
│   ├── pom.xml            # Maven configuration
│   └── README.md          # Plugin documentation
│
├── frontend/              # Frontend - Server Website
│   ├── css/               # Stylesheets
│   ├── js/                # JavaScript files
│   ├── images/            # Image assets
│   ├── index.html         # Main page
│   └── README.md          # Frontend documentation
│
└── README.md              # This file
```

## Components

### Backend - Custom Plugin

The `plugin/` directory contains a Maven-based Minecraft plugin built with the Spigot API.

**Features:**
- Maven build system for easy compilation
- Custom commands (e.g., `/hello`)
- Configuration system
- Event handling support
- Compatible with Spigot/Paper 1.20.4

**Quick Start:**
```bash
cd plugin
mvn clean package
```

See [plugin/README.md](plugin/README.md) for detailed documentation.

### Frontend - Server Website

The `frontend/` directory contains a basic website for your Minecraft server.

**Features:**
- Responsive design
- Server status display
- Player count tracking
- Modern UI with animations
- Easy to customize

**Quick Start:**
```bash
cd frontend
# Open index.html in a browser or use a local server
python3 -m http.server 8000
```

See [frontend/README.md](frontend/README.md) for detailed documentation.

## Requirements

### Backend
- Java 17 or higher
- Maven 3.6 or higher
- Spigot/Paper server 1.20.4

### Frontend
- Any modern web browser
- Web server for deployment (optional for development)

## Getting Started

1. **Clone the repository:**
   ```bash
   git clone https://github.com/pankko-beep/MinecraftServer.git
   cd MinecraftServer
   ```

2. **Build the plugin:**
   ```bash
   cd plugin
   mvn clean package
   ```

3. **Deploy the plugin:**
   - Copy `plugin/target/custom-plugin-1.0.0.jar` to your server's `plugins/` folder
   - Restart your Minecraft server

4. **Setup the website:**
   - Customize `frontend/index.html` with your server details
   - Deploy to your web hosting service or use locally

## Development

### Plugin Development

The plugin follows standard Bukkit/Spigot conventions:
- Main class: `CustomPlugin.java`
- Commands are registered in `plugin.yml`
- Configuration in `config.yml`

Add new features by creating command classes and registering them in the main plugin class.

### Website Customization

Edit the files in `frontend/` to customize:
- `index.html` - Content and structure
- `css/style.css` - Styling and colors
- `js/script.js` - Functionality and interactions

## Contributing

Contributions are welcome! Please feel free to submit issues and pull requests.

## License

This project is open source. Please check individual files for specific license information.

## Support

For issues and questions:
- Open an issue on GitHub
- Check the documentation in each component's README file

---

**Note:** This project is not affiliated with Mojang or Microsoft.

# Minecraft Plugin - Backend

This directory contains the custom Minecraft server plugin built with Maven.

## Requirements

- Java 17 or higher
- Maven 3.6 or higher
- Spigot/Paper server 1.20.4

## Building the Plugin

To compile the plugin, run:

```bash
cd plugin
mvn clean package
```

The compiled plugin JAR file will be located in `target/custom-plugin-1.0.0.jar`.

**Note:** Building requires an internet connection to download dependencies from the Paper/Spigot Maven repositories.

## Installation

1. Build the plugin using the command above
2. Copy the JAR file from `target/` to your Minecraft server's `plugins/` directory
3. Restart or reload your Minecraft server
4. The plugin will be enabled automatically

## Usage

The plugin includes a basic command:

- `/hello` - Sends a welcome message to the player

## Configuration

Edit `config.yml` in the plugin's data folder to customize settings.

## Development

The plugin structure follows standard Bukkit/Spigot conventions:

- `src/main/java/` - Java source code
- `src/main/resources/` - Plugin resources (plugin.yml, config.yml)
- `pom.xml` - Maven configuration

To add new features:

1. Create new command classes in the `com.minecraftserver.customplugin` package
2. Register commands in `CustomPlugin.java`
3. Update `plugin.yml` with command definitions

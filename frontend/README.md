# Minecraft Server Website - Frontend

This directory contains the basic website setup for the Minecraft server.

## Structure

```
frontend/
├── index.html          # Main HTML page
├── css/
│   └── style.css      # Stylesheet
├── js/
│   └── script.js      # JavaScript functionality
├── images/            # Image assets directory
└── README.md          # This file
```

## Features

- **Responsive Design**: Works on desktop, tablet, and mobile devices
- **Server Status**: Displays server online/offline status
- **Player Count**: Shows current player count (demo mode)
- **Smooth Navigation**: Smooth scrolling between sections
- **Modern UI**: Clean and modern interface with animations
- **Copy Server IP**: Click to copy server IP to clipboard

## Usage

### Local Development

To view the website locally:

1. Open `index.html` in a web browser
2. Or use a local web server:

```bash
# Using Python 3
python3 -m http.server 8000

# Using Node.js (install http-server globally first)
npx http-server -p 8000
```

Then visit `http://localhost:8000` in your browser.

### Customization

1. **Update Server Information**: Edit the `index.html` file to change:
   - Server IP address
   - Server version
   - Features and descriptions

2. **Styling**: Modify `css/style.css` to change:
   - Colors and themes
   - Layout and spacing
   - Animations and effects

3. **Functionality**: Edit `js/script.js` to:
   - Implement real server status checks
   - Connect to server APIs
   - Add more interactive features

### Server Status API

To implement real server status checking:

1. Set up a server-side API endpoint that queries your Minecraft server
2. Update the `checkServerStatus()` function in `js/script.js` to call your API
3. Parse the response and update the UI accordingly

Example API call:
```javascript
fetch('/api/server-status')
    .then(response => response.json())
    .then(data => {
        // Update UI with real data
        statusIndicator.classList.add(data.online ? 'online' : 'offline');
        statusText.textContent = data.online ? 'Server is Online' : 'Server is Offline';
        document.getElementById('player-count').textContent = `${data.players}/${data.maxPlayers}`;
    });
```

## Deployment

### Static Hosting

This is a static website and can be deployed to:

- **GitHub Pages**: Free hosting for static sites
- **Netlify**: Deploy with automatic builds
- **Vercel**: Simple deployment from Git
- **AWS S3**: Static website hosting
- **Any web server**: Upload files via FTP/SFTP

### Web Server Setup

For Apache:
```apache
<VirtualHost *:80>
    ServerName example.com
    DocumentRoot /var/www/minecraft-server/frontend
    
    <Directory /var/www/minecraft-server/frontend>
        Options Indexes FollowSymLinks
        AllowOverride All
        Require all granted
    </Directory>
</VirtualHost>
```

For Nginx:
```nginx
server {
    listen 80;
    server_name example.com;
    root /var/www/minecraft-server/frontend;
    index index.html;
    
    location / {
        try_files $uri $uri/ =404;
    }
}
```

## Future Enhancements

- Add image gallery showcasing server builds
- Implement player statistics dashboard
- Add server rules and guidelines page
- Create voting system integration
- Add Discord widget integration
- Implement news/updates blog section

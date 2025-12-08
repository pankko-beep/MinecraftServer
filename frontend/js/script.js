// Minecraft Server Website JavaScript

// Server status check function
function checkServerStatus() {
    const statusIndicator = document.getElementById('status-indicator');
    const statusText = document.getElementById('status-text');
    
    // Simulate server status check
    // In a real implementation, this would make an API call to check actual server status
    setTimeout(() => {
        const isOnline = Math.random() > 0.2; // 80% chance of being online for demo
        
        if (isOnline) {
            statusIndicator.classList.add('online');
            statusText.textContent = 'Server is Online';
            updatePlayerCount();
        } else {
            statusIndicator.classList.add('offline');
            statusText.textContent = 'Server is Offline';
        }
    }, 1000);
}

// Update player count
function updatePlayerCount() {
    const playerCountElement = document.getElementById('player-count');
    
    // Simulate player count
    // In a real implementation, this would fetch from server API
    const playerCount = Math.floor(Math.random() * 50) + 10;
    const maxPlayers = 100;
    
    playerCountElement.textContent = `${playerCount}/${maxPlayers}`;
}

// Smooth scrolling for navigation links
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
        e.preventDefault();
        const targetId = this.getAttribute('href');
        const targetElement = document.querySelector(targetId);
        
        if (targetElement) {
            targetElement.scrollIntoView({
                behavior: 'smooth',
                block: 'start'
            });
        }
    });
});

// Copy server IP to clipboard
function copyServerIP() {
    const serverIP = document.getElementById('server-ip').textContent;
    
    navigator.clipboard.writeText(serverIP).then(() => {
        alert('Server IP copied to clipboard: ' + serverIP);
    }).catch(err => {
        console.error('Failed to copy: ', err);
    });
}

// Add click listener to server IP
document.addEventListener('DOMContentLoaded', () => {
    const serverIPElement = document.getElementById('server-ip');
    if (serverIPElement) {
        serverIPElement.style.cursor = 'pointer';
        serverIPElement.title = 'Click to copy';
        serverIPElement.addEventListener('click', copyServerIP);
    }
    
    // Check server status on page load
    checkServerStatus();
    
    // Refresh server status every 30 seconds
    setInterval(checkServerStatus, 30000);
});

// Add animation on scroll
const observerOptions = {
    threshold: 0.1,
    rootMargin: '0px 0px -100px 0px'
};

const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
        if (entry.isIntersecting) {
            entry.target.style.opacity = '1';
            entry.target.style.transform = 'translateY(0)';
        }
    });
}, observerOptions);

// Observe sections for animation
document.addEventListener('DOMContentLoaded', () => {
    const sections = document.querySelectorAll('section');
    sections.forEach(section => {
        section.style.opacity = '0';
        section.style.transform = 'translateY(20px)';
        section.style.transition = 'opacity 0.6s ease, transform 0.6s ease';
        observer.observe(section);
    });
});

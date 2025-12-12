# Git & GitHub Guide

**Complete guide for version control with Git and GitHub**  
**Last Updated**: December 9, 2025

---

## 📚 Table of Contents

1. [What is Git?](#what-is-git)
2. [Basic Concepts](#basic-concepts)
3. [Initial Setup](#initial-setup)
4. [Basic Workflow](#basic-workflow)
5. [Branch Management](#branch-management)
6. [Remote Repositories (GitHub)](#remote-repositories-github)
7. [Common Commands Reference](#common-commands-reference)
8. [Troubleshooting](#troubleshooting)
9. [Best Practices](#best-practices)

---

## What is Git?

**Git** is a distributed version control system that tracks changes in your code over time.

**GitHub** is a cloud platform that hosts Git repositories and adds collaboration features.

### Why Use Git?

- 📜 **History**: Track every change made to your project
- 🔄 **Collaboration**: Multiple developers can work together
- 🌿 **Branching**: Work on features without breaking main code
- ⏮️ **Undo**: Easily revert to previous versions
- 🔒 **Backup**: Code stored safely in the cloud

---

## Basic Concepts

### Repository (Repo)
A folder that Git tracks. Contains all your project files and their history.

### Commit
A snapshot of your project at a specific point in time. Like a save point in a game.

### Branch
A parallel version of your code. Allows you to work on features independently.

### Remote
A version of your repository hosted on the internet (e.g., GitHub).

### Working Directory
The files you're currently editing on your computer.

### Staging Area (Index)
A holding area for changes before committing them.

### HEAD
A pointer to your current position in the Git history.

---

## Initial Setup

### 1. Install Git

**Windows:**
Download from [git-scm.com](https://git-scm.com)

**Verify installation:**
```powershell
git --version
```

### 2. Configure Git

Set your identity (required for commits):

```powershell
git config --global user.name "Your Name"
git config --global user.email "your.email@example.com"
```

Check your configuration:
```powershell
git config --list
```

### 3. Create GitHub Account

1. Go to [github.com](https://github.com)
2. Sign up for free account
3. Verify your email

---

## Basic Workflow

### The Standard Git Workflow

```
Working Directory → Staging Area → Local Repository → Remote Repository
     (edit)      →    (add)     →    (commit)      →      (push)
```

### Step-by-Step Example

#### 1. Initialize a Repository

**Option A: Start a new project**
```powershell
# Create project folder
mkdir MyProject
cd MyProject

# Initialize Git
git init
```

**Option B: Clone existing project**
```powershell
git clone https://github.com/username/repository.git
cd repository
```

#### 2. Check Status

See what files have changed:
```powershell
git status
```

Output explains:
- **Untracked files**: New files Git doesn't know about
- **Modified files**: Files you've changed
- **Staged files**: Files ready to commit

#### 3. Stage Changes

Add specific files:
```powershell
git add filename.txt
git add src/main.java
```

Add all files in current directory:
```powershell
git add .
```

Add all files in project:
```powershell
git add -A
```

Unstage a file:
```powershell
git restore --staged filename.txt
```

#### 4. Commit Changes

Create a commit with a message:
```powershell
git commit -m "Add login feature"
```

**Good commit messages:**
- ✅ "Add user authentication system"
- ✅ "Fix bug in payment processing"
- ✅ "Update README with installation steps"

**Bad commit messages:**
- ❌ "Update"
- ❌ "Changes"
- ❌ "asdfasdf"

Commit with detailed message:
```powershell
git commit
# Opens text editor for multi-line message
```

#### 5. View History

See commit history:
```powershell
git log
```

Compact view:
```powershell
git log --oneline
```

View specific file history:
```powershell
git log filename.txt
```

#### 6. View Changes

See unstaged changes:
```powershell
git diff
```

See staged changes:
```powershell
git diff --staged
```

Compare branches:
```powershell
git diff branch1 branch2
```

---

## Branch Management

### Why Use Branches?

- Work on features without affecting main code
- Test experimental ideas safely
- Collaborate without conflicts

### Branch Commands

#### Create a Branch

```powershell
# Create new branch
git branch feature-name

# Create and switch to new branch
git checkout -b feature-name
# or (newer syntax)
git switch -c feature-name
```

#### List Branches

```powershell
# Local branches
git branch

# Remote branches
git branch -r

# All branches
git branch -a
```

#### Switch Branches

```powershell
# Old syntax
git checkout branch-name

# New syntax (Git 2.23+)
git switch branch-name
```

#### Rename Branch

```powershell
# Rename current branch
git branch -m new-name

# Rename specific branch
git branch -m old-name new-name
```

#### Delete Branch

```powershell
# Delete merged branch
git branch -d branch-name

# Force delete (even if unmerged)
git branch -D branch-name

# Delete remote branch
git push origin --delete branch-name
```

### Merging Branches

#### Basic Merge

```powershell
# Switch to branch you want to merge INTO
git checkout main

# Merge feature branch into main
git merge feature-name
```

#### Handle Merge Conflicts

When Git can't automatically merge:

1. **Git marks conflicts in files:**
```
<<<<<<< HEAD
Current branch code
=======
Incoming branch code
>>>>>>> feature-name
```

2. **Edit file to resolve conflict** (remove markers, keep desired code)

3. **Stage resolved files:**
```powershell
git add conflicted-file.txt
```

4. **Complete merge:**
```powershell
git commit -m "Merge feature-name into main"
```

#### Abort Merge

If you want to cancel:
```powershell
git merge --abort
```

---

## Remote Repositories (GitHub)

### Linking Local and Remote

#### Add Remote Repository

```powershell
# Add remote named 'origin'
git remote add origin https://github.com/username/repository.git

# Verify remote
git remote -v
```

#### Change Remote URL

```powershell
git remote set-url origin https://github.com/username/new-repository.git
```

#### Remove Remote

```powershell
git remote remove origin
```

### Push (Upload to GitHub)

#### First Push (Set Upstream)

```powershell
git push -u origin main
```
The `-u` flag sets up tracking, so future pushes can just use `git push`.

#### Regular Push

```powershell
# Push current branch
git push

# Push specific branch
git push origin branch-name

# Push all branches
git push --all
```

#### Force Push (Dangerous!)

**⚠️ Warning**: Overwrites remote history. Only use if you know what you're doing.

```powershell
git push --force
# or safer version
git push --force-with-lease
```

### Pull (Download from GitHub)

#### Pull Changes

```powershell
# Pull current branch
git pull

# Pull specific branch
git pull origin branch-name
```

**What `git pull` does:**
1. Fetches changes from remote
2. Automatically merges into your branch

#### Fetch Without Merging

```powershell
# Download changes but don't merge
git fetch

# See what changed
git log HEAD..origin/main

# Merge manually when ready
git merge origin/main
```

### Clone Repository

Download entire repository:

```powershell
# Clone via HTTPS
git clone https://github.com/username/repository.git

# Clone to specific folder
git clone https://github.com/username/repository.git MyFolder

# Clone specific branch
git clone -b branch-name https://github.com/username/repository.git
```

---

## Common Commands Reference

### Setup & Config

```powershell
git init                          # Initialize repository
git config --global user.name     # Set username
git config --global user.email    # Set email
git clone <url>                   # Clone repository
```

### Basic Operations

```powershell
git status                        # Check status
git add <file>                    # Stage file
git add .                         # Stage all changes
git commit -m "message"           # Commit changes
git log                           # View history
git log --oneline                 # Compact history
```

### Branching

```powershell
git branch                        # List branches
git branch <name>                 # Create branch
git checkout <name>               # Switch branch
git checkout -b <name>            # Create and switch
git merge <branch>                # Merge branch
git branch -d <name>              # Delete branch
```

### Remote Operations

```powershell
git remote -v                     # List remotes
git remote add origin <url>       # Add remote
git push                          # Push to remote
git push -u origin <branch>       # Push and set upstream
git pull                          # Pull from remote
git fetch                         # Fetch without merge
```

### Undoing Changes

```powershell
git restore <file>                # Discard local changes
git restore --staged <file>       # Unstage file
git reset HEAD~1                  # Undo last commit (keep changes)
git reset --hard HEAD~1           # Undo last commit (discard changes)
git revert <commit-hash>          # Create commit that undoes changes
```

### Inspection

```powershell
git diff                          # Show unstaged changes
git diff --staged                 # Show staged changes
git show <commit-hash>            # Show commit details
git blame <file>                  # See who changed each line
```

---

## Troubleshooting

### Problem: "fatal: not a git repository"

**Solution:** You're not in a Git-initialized folder.
```powershell
git init
```

### Problem: "Author identity unknown"

**Solution:** Configure your name and email.
```powershell
git config --global user.name "Your Name"
git config --global user.email "you@example.com"
```

### Problem: "Permission denied (publickey)"

**Solution:** Set up SSH keys or use HTTPS URL.
```powershell
# Switch to HTTPS
git remote set-url origin https://github.com/username/repo.git
```

### Problem: "failed to push some refs"

**Solution:** Pull changes first, then push.
```powershell
git pull --rebase
git push
```

### Problem: Merge conflict

**Solution:** 
1. Open conflicted files
2. Edit to resolve conflicts
3. `git add` resolved files
4. `git commit`

### Problem: Committed to wrong branch

**Solution:** Move commit to correct branch.
```powershell
# On wrong branch
git log  # Copy commit hash

# Switch to correct branch
git checkout correct-branch
git cherry-pick <commit-hash>

# Go back and remove from wrong branch
git checkout wrong-branch
git reset --hard HEAD~1
```

### Problem: Need to undo last commit

**Keep changes:**
```powershell
git reset HEAD~1
```

**Discard changes:**
```powershell
git reset --hard HEAD~1
```

### Problem: Accidentally deleted files

**Restore from last commit:**
```powershell
git restore <file>
# or
git checkout HEAD <file>
```

---

## Best Practices

### 1. Commit Often

- Make small, focused commits
- Each commit should be one logical change
- Easier to understand history and revert if needed

### 2. Write Good Commit Messages

**Format:**
```
Short summary (50 chars or less)

Detailed explanation if needed (wrap at 72 chars).
Explain what and why, not how.
```

**Examples:**
```
Add user authentication system

Implement JWT-based authentication for API endpoints.
Includes login, logout, and token refresh functionality.
```

### 3. Use Branches

**Common branch naming:**
- `main` or `master` - Production-ready code
- `develop` - Integration branch
- `feature/feature-name` - New features
- `bugfix/bug-name` - Bug fixes
- `hotfix/issue` - Urgent production fixes

### 4. Pull Before You Push

Always pull latest changes before pushing:
```powershell
git pull
git push
```

### 5. Don't Commit Sensitive Data

**Never commit:**
- Passwords
- API keys
- `.env` files
- Private keys

**Use `.gitignore`:**
```
.env
*.log
node_modules/
target/
*.class
```

### 6. Review Before Committing

```powershell
git status
git diff
git add -p  # Interactive staging
```

### 7. Keep Your Repository Clean

```powershell
# Remove untracked files (dry run)
git clean -n

# Remove untracked files
git clean -f

# Remove untracked files and directories
git clean -fd
```

---

## Quick Workflow Examples

### Scenario 1: Daily Development

```powershell
# Start of day - get latest code
git pull

# Create feature branch
git checkout -b feature/new-login

# Make changes to files...

# Check what changed
git status
git diff

# Stage and commit
git add .
git commit -m "Add new login form"

# Push to GitHub
git push -u origin feature/new-login

# When done, merge to main
git checkout main
git merge feature/new-login
git push
```

### Scenario 2: Fix a Bug

```powershell
# Create bugfix branch
git checkout -b bugfix/payment-error

# Fix the bug in your files...

# Commit the fix
git add .
git commit -m "Fix payment processing error"

# Push and create PR on GitHub
git push -u origin bugfix/payment-error
```

### Scenario 3: Update Your Branch

```powershell
# You're on feature branch, main has been updated
git checkout main
git pull

# Update your feature branch
git checkout feature/my-feature
git merge main

# Or use rebase (cleaner history)
git rebase main
```

### Scenario 4: Collaborate on GitHub

```powershell
# Fork repository on GitHub, then clone your fork
git clone https://github.com/YOUR-USERNAME/repository.git

# Add original repository as upstream
git remote add upstream https://github.com/ORIGINAL-OWNER/repository.git

# Keep your fork updated
git fetch upstream
git checkout main
git merge upstream/main
git push
```

---

## GitHub-Specific Features

### Pull Requests (PRs)

1. Push your branch to GitHub
2. Go to repository on GitHub
3. Click "Pull Requests" → "New Pull Request"
4. Select your branch
5. Write description of changes
6. Submit for review

### Issues

Track bugs and feature requests:
1. Go to "Issues" tab
2. Click "New Issue"
3. Describe problem or feature
4. Assign labels, assignees

### GitHub Actions

Automate workflows (CI/CD):
- Located in `.github/workflows/`
- Runs tests on every commit
- Automates deployment

---

## Git Cheat Sheet

```powershell
# SETUP
git init                          # Start tracking a folder
git clone <url>                   # Copy a repository

# CHANGES
git status                        # What's changed?
git add <file>                    # Stage specific file
git add .                         # Stage everything
git commit -m "message"           # Save changes

# BRANCHING
git branch                        # List branches
git branch <name>                 # Create branch
git checkout <name>               # Switch branch
git checkout -b <name>            # Create + switch
git merge <branch>                # Merge branch
git branch -d <name>              # Delete branch

# REMOTE
git remote add origin <url>       # Connect to GitHub
git push -u origin main           # Upload code
git pull                          # Download updates
git clone <url>                   # Copy repository

# UNDO
git restore <file>                # Discard changes
git reset HEAD~1                  # Undo commit
git revert <hash>                 # Reverse commit

# INFO
git log                           # History
git diff                          # Show changes
git show <hash>                   # Show commit
```

---

## Resources

### Official Documentation
- Git: [git-scm.com/doc](https://git-scm.com/doc)
- GitHub: [docs.github.com](https://docs.github.com)

### Interactive Learning
- [learngitbranching.js.org](https://learngitbranching.js.org) - Visual Git tutorial
- [try.github.io](https://try.github.io) - GitHub's learning resources

### GUI Tools
- GitHub Desktop - [desktop.github.com](https://desktop.github.com)
- GitKraken - [gitkraken.com](https://gitkraken.com)
- VS Code built-in Git support

---

## Summary

**Basic workflow:**
1. `git pull` - Get latest changes
2. Make changes to your files
3. `git status` - See what changed
4. `git add .` - Stage changes
5. `git commit -m "message"` - Save changes
6. `git push` - Upload to GitHub

**Remember:**
- Commit often with clear messages
- Use branches for features
- Pull before push
- Never commit sensitive data

---

**Last Updated**: December 9, 2025  
**For project-specific Git usage, see the main README.md**

# ⚔ KingdomDraft • The 4 Houses of Sepherune

[![Paper](https://img.shields.io/badge/Paper-1.21.1-blue.svg)](https://papermc.io/)
[![Spigot](https://img.shields.io/badge/Spigot-1.21.1-orange.svg)](https://spigotmc.org/)
[![Java](https://img.shields.io/badge/Java-21+-red.svg)](https://adoptium.net/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![Version](https://img.shields.io/badge/Version-1.1.0-gold.svg)](https://github.com/flowey258/KingdomDraft)

> **A Minecraft plugin that automatically drafts every new player into one of four noble houses — no choice, no escape, just fate.**

KingdomDraft randomly assigns every player to a house the moment they join your server. No kingdom creation. No manual selection. The four houses remain balanced, server-managed, and lore-driven.

---

## 📖 Table of Contents

- [Features](#features)
- [The 4 Houses](#the-4-houses)
- [Requirements](#requirements)
- [Installation](#installation)
- [Configuration](#configuration)
- [Discord Integration](#discord-integration)
- [Commands & Permissions](#commands--permissions)
- [How It Works](#how-it-works)
- [Troubleshooting](#troubleshooting)
- [FAQ](#faq)
- [Building from Source](#building-from-source)
- [Support](#support)
- [License](#license)

---

## ✨ Features

| Feature | Description |
|---------|-------------|
| 🎲 **Random Draft** | Every new player is randomly assigned to one of 4 houses |
| 🔒 **No Kingdom Creation** | Players cannot create their own kingdoms (configurable bypass) |
| 🎨 **Discord Role Sync** | Auto-assigns Discord roles when players link accounts |
| ⚡ **Zero Configuration** | Works out of the box with sensible defaults |
| 🔌 **Plugin Integration** | Seamless with Kingdoms, PlaceholderAPI, and DiscordSRV |
| 📊 **PlaceholderAPI Support** | Use `%kingdoms_has_kingdom%` and other placeholders |
| 🛡️ **Permission Based** | Fine-grained control with `kingdomdraft.bypass` |

---

## 🏰 The 4 Houses

| Emblem | House | Monarch | Biome | Color |
|:------:|-------|---------|-------|-------|
| ⚔ | **Vlossaire** | Reeval | Eastern lush forests & river valleys | 🟥 Red |
| ✦ | **Arcnaria** | Long | Western volcanic ridges & golden deserts | 🟦 Blue |
| ❄ | **Hushpierre** | Jea | Harsh mountainous northern region | 🟩 Green |
| ◆ | **Slypharis** | Coke | Southern shadowed canyons & underground networks | 🟧 Orange |

Each player is randomly assigned to **exactly one** house upon first join — no rerolls, no exceptions (without admin intervention).

---

## 📋 Requirements

### Required Plugins

| Plugin | Version | Purpose |
|--------|---------|---------|
| **Kingdoms** | Latest | Faction / kingdom management |
| **PlaceholderAPI** | 2.11+ | Read `%kingdoms_has_kingdom%` placeholder |

### Optional Plugins

| Plugin | Version | Purpose |
|--------|---------|---------|
| **DiscordSRV** | 1.27+ | Auto-assign Discord house roles |

### Server Requirements

- **Minecraft Server:** Paper or Spigot 1.21.1
- **Java:** 21 or newer
- **Maven:** 3.8+ (to compile from source)

---

## 🚀 Installation

### Quick Install (Pre-compiled)

1. Download `KingdomDraft-1.1.0.jar` from [Releases](https://github.com/flowey258/KingdomDraft/releases)
2. Place the JAR in your server's `plugins/` folder
3. Ensure **Kingdoms** and **PlaceholderAPI** are installed
4. (Optional) Install **DiscordSRV** for Discord role sync
5. Restart your server

### Verify Installation

Your console should show:

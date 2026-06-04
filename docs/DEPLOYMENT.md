# Installing Race Coordinator AI on Windows

This fork (`tjc998/racecoordinator_ai`) does **not** publish prebuilt
installers, so there is no `.exe` to download. You build it from source on a
Windows machine and then install or run it. These are the basic steps.

## Prerequisites

Install these first:

- **JDK 17** (e.g. [Adoptium Temurin](https://adoptium.net/))
- **Maven 3.x**
- **Node.js 20.x**
- **protoc** (Protocol Buffers compiler)
- **[Inno Setup 6](https://jrsoftware.org/isdl.php)** — only if you want the
  `.exe` installer (optional; see [Option B](#option-b-build-and-run-the-exe-installer))

You also need `server\src\main\resources\analytics.properties` (from the secure
vault) — the build aborts without it.

## Build

From the repo root in PowerShell:

```powershell
.\scripts\installer\create_installers.ps1
```

This builds the Angular client and the server JAR, downloads/bundles a Java
runtime and MongoDB, and writes the distribution into the **`release\`**
folder:

```
release\RaceCoordinator\            <- standard app folder
release\RaceCoordinator_Offline\    <- offline/legacy app folder
release\RaceCoordinator_Universal.zip
release\RaceCoordinator_Windows_Offline.zip
```

> **Note:** there is **no `.exe` in `release\`.** The `.exe` is only produced
> when Inno Setup is installed, and it is written to the **`Output\`** folder at
> the repo root — not `release\` (see Option B).

## Install & run

### Option A — run the folder directly (no installer)

1. Copy `release\RaceCoordinator` to wherever you want the app (e.g. your
   Desktop).
2. In that folder, double-click **`setup_windows.bat`** if you don't already
   have Java (it installs a bundled/downloaded Java runtime).
3. Double-click **`start_win.bat`** to start the server. Leave the window open.
4. Open [http://localhost:7070](http://localhost:7070) in your browser.

### Option B — build and run the `.exe` installer

1. Install [Inno Setup 6](https://jrsoftware.org/isdl.php).
2. Re-run `.\scripts\installer\create_installers.ps1` (it auto-builds the `.exe`
   when Inno Setup is present), **or** build it manually from the repo root:
   ```powershell
   iscc scripts\installer\installer_online.iss
   iscc scripts\installer\installer_offline_legacy.iss
   ```
3. The installers appear in the **`Output\`** folder:
   - `Output\RaceCoordinatorAI_Online_Setup.exe` — downloads Java 17 +
     MongoDB 6.0 during install (needs internet).
   - `Output\RaceCoordinatorAI_Offline_Legacy_Setup.exe` — bundles Java 8 +
     MongoDB 3.2 for offline/legacy Windows (XP/7/8).
4. Double-click the `.exe`, accept the admin prompts. It installs to
   `C:\Program Files\Race Coordinator AI` (data in
   `C:\ProgramData\Race Coordinator AI`) and creates desktop shortcuts for the
   **Server (Headless)** and the **Client** (which opens
   `http://localhost:7070`).

## Requirements & notes

- **Windows:** XP SP3 or newer (32-bit and 64-bit supported).
- **Legacy Windows (7 / 8 / XP)** also needs the
  [Microsoft Visual C++ 2013 Redistributable (x86)](https://www.microsoft.com/en-us/download/details.aspx?id=40784)
  so MongoDB 3.2 can start.
- Ports **7070** (web) and **27017** (database) must be free.

For macOS and Linux/Raspberry Pi, see the [README](../README.md#install).

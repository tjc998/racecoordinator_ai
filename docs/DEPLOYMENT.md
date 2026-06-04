# Installing Race Coordinator AI on Windows

Basic instructions for installing and running Race Coordinator AI on Windows.

## 1. Download

Get the latest installer from the
[Releases page](https://github.com/daufderheide/racecoordinator_ai/releases)
and pick **one**:

- **`RaceCoordinatorAI_Online_Setup.exe`** — recommended. Downloads Java 17 and
  MongoDB 6.0 during install (needs an internet connection).
- **`RaceCoordinatorAI_Offline_Legacy_Setup.exe`** — for offline machines or
  legacy Windows (XP / 7 / 8). Bundles Java 8 and MongoDB 3.2.

## 2. Install

1. Double-click the `.exe` you downloaded.
2. Accept the prompts (the installer needs administrator rights).
3. The app installs to `C:\Program Files\Race Coordinator AI`, and its data
   (database, assets) goes to `C:\ProgramData\Race Coordinator AI`.

The installer creates two desktop shortcuts:

- **Race Coordinator Server (Headless)** — starts the program.
- **Race Coordinator Client** — opens the app in your browser.

## 3. Run

1. Double-click **Race Coordinator Server (Headless)** and leave that window
   open while you use the program.
2. Double-click **Race Coordinator Client** (or open
   [http://localhost:7070](http://localhost:7070) in your browser).

## Requirements & notes

- **Windows:** XP SP3 or newer (32-bit and 64-bit supported).
- **Legacy Windows (7 / 8 / XP)** also needs the
  [Microsoft Visual C++ 2013 Redistributable (x86)](https://www.microsoft.com/en-us/download/details.aspx?id=40784)
  so MongoDB 3.2 can start.
- Ports **7070** (web) and **27017** (database) must be free.
- The bundled Java means you don't need to install Java separately.

For macOS and Linux/Raspberry Pi instructions, see the
[README](../README.md#install).

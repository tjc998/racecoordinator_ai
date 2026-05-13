$ErrorActionPreference = "Stop"

# Setup Java Environment
$TargetJavaHome = "C:\Program Files\Eclipse Adoptium\jdk-17.0.18.8-hotspot"

if (-not $env:JAVA_HOME -or -not (Test-Path $env:JAVA_HOME)) {
    if (Test-Path $TargetJavaHome) {
        $env:JAVA_HOME = $TargetJavaHome
    } else {
        # Try to find any JDK in common locations
        $PossiblePaths = @(
            "$PSScriptRoot\tools\jdk\jdk-*",
            "C:\Program Files\Eclipse Adoptium\jdk-*",
            "C:\Program Files\Microsoft\jdk-*",
            "C:\Program Files\Java\jdk-*",
            "C:\Program Files\Android\openjdk\jdk-*"
        )
        $FoundPath = Get-Item $PossiblePaths -ErrorAction SilentlyContinue | Sort-Object Name -Descending | Select-Object -First 1
        if ($FoundPath) {
            $env:JAVA_HOME = $FoundPath.FullName
        }
    }
}

if ($env:JAVA_HOME -and (Test-Path $env:JAVA_HOME)) {
    $env:Path = "$env:JAVA_HOME\bin;" + $env:Path
    Write-Host "Using JAVA_HOME: $env:JAVA_HOME" -ForegroundColor Gray
} else {
    Write-Error "JAVA_HOME not found. Please install a JDK (version 11 or higher)."
    exit 1
}

$SERVER_DIR = "$PSScriptRoot\server"
$BUILD_DIR = "target_generated"

# Run generate_protos.ps1 to handle protobuf generation (like generate_protos.sh on Unix)
# Tell it to use the same output directory as this headless build
Write-Host "Generating Protobuf files..." -ForegroundColor Cyan
Set-Location $SERVER_DIR
$env:PROTO_DEST_DIR = Join-Path $SERVER_DIR $BUILD_DIR
& powershell -ExecutionPolicy Bypass -File generate_protos.ps1

Write-Host "Starting Headless Server..." -ForegroundColor Green
Set-Location $SERVER_DIR

# Find mvn.cmd
$MvnCmd = Get-Command mvn.cmd -ErrorAction SilentlyContinue
if ($null -eq $MvnCmd) {
    # Try common installation paths if not in PATH
    $CommonPaths = @(
        "$PSScriptRoot\tools\maven\apache-maven-*\bin\mvn.cmd",
        "$PSScriptRoot\..\racecoordinator_ai\tools\maven\apache-maven-*\bin\mvn.cmd",
        "C:\Maven\apache-maven-*\bin\mvn.cmd",
        "C:\Program Files\apache-maven-*\bin\mvn.cmd",
        "C:\maven\bin\mvn.cmd"
    )
    $MvnCmd = Get-Item $CommonPaths -ErrorAction SilentlyContinue | Select-Object -First 1
}

if ($null -eq $MvnCmd) {
    Write-Warning "mvn.cmd not found in PATH or common locations. Falling back to 'mvn'."
    $MvnExecutable = "mvn"
} else {
    if ($MvnCmd -is [System.IO.FileInfo]) {
        $MvnExecutable = $MvnCmd.FullName
    } else {
        $MvnExecutable = "mvn.cmd"
    }
}

$DATA_DIR = Join-Path $PSScriptRoot "data"
# Use BUILD_DIR for both proto generation and maven build to avoid conflicts
$MvnArgs = @("compile", "exec:java", "-Dbuild.dist.dir=$BUILD_DIR", "-Dexec.mainClass=com.antigravity.App", "-Dexec.args=--headless", "-Dapp.data.dir=$DATA_DIR")
& $MvnExecutable @MvnArgs

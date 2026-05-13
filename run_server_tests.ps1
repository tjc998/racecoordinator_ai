$ErrorActionPreference = "Stop"

# Configuration
$ProjectRoot = if ($PSScriptRoot) { $PSScriptRoot } elseif ($MyInvocation.MyCommand.Definition) { Split-Path -Parent $MyInvocation.MyCommand.Definition } else { $PWD.Path }
$ServerDir = Join-Path $ProjectRoot "server"
$ServerTmp = Join-Path $ServerDir "target_tmp"
$ServerBuildDir = Join-Path $ServerTmp "target_test"

Write-Host ""
Write-Host "--- 🔹 Running Server Tests (PowerShell) 🔹 ---" -ForegroundColor Cyan

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
        $FoundPath = Get-ChildItem $PossiblePaths -ErrorAction SilentlyContinue | Sort-Object Name -Descending | Select-Object -First 1
        if ($FoundPath) {
            $env:JAVA_HOME = $FoundPath.FullName
        }
    }
}

if ($env:JAVA_HOME -and (Test-Path $env:JAVA_HOME)) {
    $env:Path = "$env:JAVA_HOME\bin;" + $env:Path
    Write-Host "Using JAVA_HOME: $env:JAVA_HOME" -ForegroundColor Gray
} else {
    Write-Warning "JAVA_HOME not found. Maven tests may fail if JDK is not in PATH."
}

if (-not (Test-Path $ServerTmp)) {
    New-Item -ItemType Directory -Path $ServerTmp -Force | Out-Null
}

# Pre-create all directories maven needs to avoid EPERM errors
$DirsToCreate = @(
    (Join-Path $ServerBuildDir "generated-sources\protobuf\java")
    (Join-Path $ServerBuildDir "classes")
    (Join-Path $ServerBuildDir "test-classes")
)

foreach ($dir in $DirsToCreate) {
    if (-not (Test-Path $dir)) {
        New-Item -ItemType Directory -Path $dir -Force | Out-Null
    }
}

Set-Location $ServerDir

# 1. Generate protobufs
Write-Host "Generating Protobufs..." -ForegroundColor Gray
.\generate_protos.ps1 --server-only

# 2. Run tests
Write-Host "Executing Maven tests..." -ForegroundColor Green

# Simple fork count for Windows
$ForkCount = "1"
$ReuseForks = "true"

$env:PROTO_DEST_DIR = $ServerBuildDir
$env:npm_config_cache = Join-Path $ServerTmp "npm_cache"
$env:EMBEDDED_MONGO_ARTIFACTS = Join-Path $ServerTmp ".embedmongo"

# Ensure all temp directories are on the same drive to avoid cross-drive file move issues
$env:TEMP = $ServerTmp
$env:TMP = $ServerTmp

# JVM optimization for faster startup in tests
$env:MAVEN_OPTS = "-XX:TieredStopAtLevel=1 -Djdk.attach.allowAttachSelf=true --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.io=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED"

$MvnArgs = @("test") + $args + @(
    "-Dbuild.dist.dir=$ServerBuildDir"
    "-DskipProtobuf=true"
    "-DforkCount=$ForkCount"
    "-DreuseForks=$ReuseForks"
    "-Djava.io.tmpdir=$ServerTmp"
    "-Dde.flapdoodle.embed.mongo.artifacts=$ServerTmp\.embedmongo"
    "-Dmaven.repo.local=$ServerDir\.m2\repository"
)

# Find mvn.cmd
$MvnCmd = Get-Command mvn.cmd -ErrorAction SilentlyContinue
if ($null -eq $MvnCmd) {
    # Try common installation paths if not in PATH
    $CommonPaths = @(
        "$ProjectRoot\tools\maven\apache-maven-*\bin\mvn.cmd",
        "$ProjectRoot\..\racecoordinator_ai\tools\maven\apache-maven-*\bin\mvn.cmd",
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

& $MvnExecutable @MvnArgs

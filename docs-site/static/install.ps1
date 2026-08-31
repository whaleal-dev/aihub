$ErrorActionPreference = "Stop"

function Write-Info {
    param([string]$Message)
    Write-Host $Message
}

function Fail {
    param([string]$Message)
    throw "aihub installer: $Message"
}

function Test-SkipPathUpdate {
    $value = $env:AIHUB_SKIP_PATH_UPDATE
    if (-not $value) {
        return $false
    }

    return @("1", "true", "yes") -contains $value.ToLowerInvariant()
}

function Resolve-Version {
    if ($env:AIHUB_VERSION) {
        return $env:AIHUB_VERSION
    }

    $repo = if ($env:AIHUB_MAVEN_REPO) { $env:AIHUB_MAVEN_REPO.TrimEnd('/') } else { "https://repo.maven.apache.org/maven2" }
    $metadataUrl = "$repo/com/whaleal/aihub-cli/maven-metadata.xml"
    [xml]$xml = (Invoke-WebRequest -UseBasicParsing -Uri $metadataUrl).Content
    $version = $xml.metadata.versioning.release
    if (-not $version) {
        $version = $xml.metadata.versioning.latest
    }
    if (-not $version) {
        Fail "unable to resolve latest aihub-cli version from Maven metadata"
    }
    return $version
}

function Ensure-Java {
    $java = Get-Command java -ErrorAction SilentlyContinue
    if (-not $java) {
        Fail "Java 8+ is required. Install Java first, then rerun this installer."
    }

    $firstLine = (& java -version 2>&1 | Select-Object -First 1)
    if ($firstLine -notmatch '"([^"]+)"') {
        Fail "unable to detect Java version"
    }

    $version = $Matches[1]
    if ($version.StartsWith("1.")) {
        $major = [int]($version.Split(".")[1])
    } else {
        $major = [int]($version.Split(".")[0])
    }

    if ($major -lt 8) {
        Fail "Java 8+ is required. Current Java major version: $major"
    }
}

function Ensure-Path {
    param([string]$BinDir)

    if (Test-SkipPathUpdate) {
        Write-Info "Skipping PATH update because AIHUB_SKIP_PATH_UPDATE is set."
        return
    }

    $currentUserPath = [Environment]::GetEnvironmentVariable("Path", "User")
    $segments = @()
    if ($currentUserPath) {
        $segments = $currentUserPath.Split(";") | Where-Object { $_ -and $_.Trim() }
    }

    $normalized = $segments | ForEach-Object { $_.TrimEnd('\') }
    if ($normalized -contains $BinDir.TrimEnd('\')) {
        if (-not (($env:Path.Split(";") | ForEach-Object { $_.TrimEnd('\') }) -contains $BinDir.TrimEnd('\'))) {
            $env:Path = "$BinDir;$env:Path"
        }
        Write-Info "aihub is already available on PATH for the current user."
        return
    }

    $newPath = if ($currentUserPath) { "$currentUserPath;$BinDir" } else { $BinDir }
    [Environment]::SetEnvironmentVariable("Path", $newPath, "User")
    $env:Path = "$BinDir;$env:Path"
    Write-Info "Added $BinDir to the user PATH."
    Write-Info "Open a new terminal if 'aihub' is not found immediately."
}

function Main {
    Ensure-Java

    $repo = if ($env:AIHUB_MAVEN_REPO) { $env:AIHUB_MAVEN_REPO.TrimEnd('/') } else { "https://repo.maven.apache.org/maven2" }
    $version = Resolve-Version
    $aihubHome = if ($env:AIHUB_HOME) { $env:AIHUB_HOME } else { Join-Path $HOME ".aihub" }
    $binDir = Join-Path $aihubHome "bin"
    $libDir = Join-Path $aihubHome "lib"
    $jarUrl = "$repo/com/whaleal/aihub-cli/$version/aihub-cli-$version-jar-with-dependencies.jar"
    $jarPath = Join-Path $libDir "aihub-cli.jar"
    $tmpJar = Join-Path $libDir "aihub-cli.jar.tmp"
    $versionFile = Join-Path $aihubHome "version.txt"
    $cmdPath = Join-Path $binDir "aihub.cmd"

    Write-Info "Installing aihub-cli $version"
    New-Item -ItemType Directory -Force -Path $binDir | Out-Null
    New-Item -ItemType Directory -Force -Path $libDir | Out-Null
    Invoke-WebRequest -UseBasicParsing -Uri $jarUrl -OutFile $tmpJar
    Move-Item -Force $tmpJar $jarPath
    Set-Content -Path $versionFile -Value $version -Encoding Ascii

    $cmdContent = @"
@echo off
setlocal
set "INSTALL_HOME=$aihubHome"
if not defined AIHUB_HOME set "AIHUB_HOME=%INSTALL_HOME%"
set "JAVA_BIN=java"
if defined AIHUB_JAVA set "JAVA_BIN=%AIHUB_JAVA%"
if not exist "%AIHUB_HOME%\lib\aihub-cli.jar" (
  echo aihub launcher: missing "%AIHUB_HOME%\lib\aihub-cli.jar" 1>&2
  exit /b 1
)
if defined AIHUB_JAVA_OPTS (
  %JAVA_BIN% %AIHUB_JAVA_OPTS% -jar "%AIHUB_HOME%\lib\aihub-cli.jar" %*
  set "EXIT_CODE=%ERRORLEVEL%"
) else (
  %JAVA_BIN% -jar "%AIHUB_HOME%\lib\aihub-cli.jar" %*
  set "EXIT_CODE=%ERRORLEVEL%"
)
endlocal & exit /b %EXIT_CODE%
"@
    Set-Content -Path $cmdPath -Value $cmdContent -Encoding Ascii

    Ensure-Path -BinDir $binDir

    Write-Info ""
    Write-Info "Installed aihub-cli $version to $aihubHome"
    Write-Info "Then run: aihub --help"
}

Main

param(
    [Parameter(ValueFromRemainingArguments = $true)]
    [string[]]$GradleArguments = @('build')
)
$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$jdkCandidates = @(Get-ChildItem -LiteralPath (Join-Path $projectRoot '.tools\jdk-21') -Directory | Where-Object { Test-Path -LiteralPath (Join-Path $_.FullName 'bin\javac.exe') })
if ($jdkCandidates.Count -ne 1) { throw 'Expected one portable JDK 21 in .tools\jdk-21.' }
$previousJavaHome = $env:JAVA_HOME
$previousGradleHome = $env:GRADLE_USER_HOME
$previousPath = $env:PATH
$previousJavaToolOptions = $env:JAVA_TOOL_OPTIONS
try {
    # Match the Windows proxy for this build process; never change machine settings.
    $probeUri = [Uri]'https://maven.neoforged.net/releases/'
    $effectiveProxy = [System.Net.WebRequest]::DefaultWebProxy.GetProxy($probeUri)
    if ($effectiveProxy.AbsoluteUri -ne $probeUri.AbsoluteUri -and $effectiveProxy.Scheme -eq 'http' -and -not $effectiveProxy.UserInfo -and $previousJavaToolOptions -notmatch '-Dhttps?\.proxyHost=') {
        $proxyOptions = "-Dhttps.proxyHost=$($effectiveProxy.Host) -Dhttps.proxyPort=$($effectiveProxy.Port) -Dhttp.proxyHost=$($effectiveProxy.Host) -Dhttp.proxyPort=$($effectiveProxy.Port) -Dhttp.nonProxyHosts=localhost|127.*|[::1]"
        $env:JAVA_TOOL_OPTIONS = ($previousJavaToolOptions + ' ' + $proxyOptions).Trim()
    }
    $env:JAVA_HOME = $jdkCandidates[0].FullName
    $env:GRADLE_USER_HOME = Join-Path $projectRoot '.gradle-user-home'
    $env:PATH = (Join-Path $env:JAVA_HOME 'bin') + [System.IO.Path]::PathSeparator + $env:PATH
    Push-Location -LiteralPath (Join-Path $projectRoot 'platform-probes\neoforge-1.21.1')
    try {
        $localGradle = Join-Path $projectRoot '.tools\gradle\gradle-9.2.1\bin\gradle.bat'
        if (Test-Path -LiteralPath $localGradle) { & $localGradle @GradleArguments } else { & .\gradlew.bat @GradleArguments }
        if ($LASTEXITCODE -ne 0) { throw ('Gradle failed with exit code ' + $LASTEXITCODE) }
    } finally { Pop-Location }
} finally {
    $env:JAVA_HOME = $previousJavaHome
    $env:GRADLE_USER_HOME = $previousGradleHome
    $env:PATH = $previousPath
    $env:JAVA_TOOL_OPTIONS = $previousJavaToolOptions
}

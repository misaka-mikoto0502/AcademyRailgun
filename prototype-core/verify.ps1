$ErrorActionPreference = 'Stop'
$frameworkRoot = $PSScriptRoot
$buildDirectory = Join-Path $frameworkRoot ('.build\' + [Guid]::NewGuid().ToString('N'))
$classesDirectory = Join-Path $buildDirectory 'classes'
New-Item -ItemType Directory -Path $classesDirectory -Force | Out-Null
$sourceFiles = @(Get-ChildItem -LiteralPath (Join-Path $frameworkRoot 'src') -Recurse -File -Filter '*.java' | ForEach-Object { $_.FullName })
& javac --release 8 -encoding UTF-8 -Xlint:all -classpath $classesDirectory -d $classesDirectory $sourceFiles
if ($LASTEXITCODE -ne 0) { throw 'Java compilation failed.' }
& java -ea -cp $classesDirectory dev.railgun.verification.FrameworkScenario
if ($LASTEXITCODE -ne 0) { throw 'Framework verification failed.' }
$frameworkJar = Join-Path $buildDirectory 'railgun-core.jar'
& jar cf $frameworkJar -C $classesDirectory dev/railgun/core
if ($LASTEXITCODE -ne 0) { throw 'Jar packaging failed.' }
Write-Output ('Core library: ' + $frameworkJar)
Write-Output 'This core library is not yet a loadable Minecraft mod.'

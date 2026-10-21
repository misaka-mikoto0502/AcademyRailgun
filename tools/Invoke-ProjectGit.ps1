param([Parameter(ValueFromRemainingArguments = $true)][string[]]$GitArguments = @('status','--short'))
$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
& git -c ('safe.directory=' + $projectRoot.Replace('\','/')) -C $projectRoot @GitArguments
if ($LASTEXITCODE -ne 0) { throw ('Git failed with exit code ' + $LASTEXITCODE) }

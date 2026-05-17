# To use: .\create_image.ps1 -ImageName <image_name>
param(
    [Parameter(Mandatory=$true)]
    [string]$ImageName
)

$ErrorActionPreference = "Stop"

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$repoRoot = (Resolve-Path (Join-Path $scriptDir "..")).Path

Write-Host "Building Docker image '$ImageName' from context '$repoRoot'..."
docker build -f (Join-Path $scriptDir "Dockerfile") -t $ImageName $repoRoot
Write-Host "Docker image '$ImageName' created successfully."
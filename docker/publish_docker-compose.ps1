# To use: .\publish_docker-compose.ps1 -DockerHubUsername <docker_hub_username>
param(
    [Parameter(Mandatory=$true)]
    [string]$DockerHubUsername
)

$ErrorActionPreference = "Stop"

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$composeTarget = "$DockerHubUsername/ladob-compose:latest"

Write-Host "Publishing docker-compose.yml to Docker Hub as OCI Artifact..."
Push-Location $scriptDir
docker compose publish $composeTarget --with-env
Pop-Location
Write-Host "docker-compose.yml published successfully as 'docker.io/$composeTarget'."
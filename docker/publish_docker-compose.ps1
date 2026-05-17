# To use: .\publish_docker-compose.ps1 -DockerHubUsername <docker_hub_username>
param(
    [Parameter(Mandatory=$true)]
    [string]$DockerHubUsername
)

$ErrorActionPreference = "Stop"

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$composeFile = Join-Path $scriptDir "docker-compose.yml"
$ociTarget = "oci://$DockerHubUsername/ladob-compose"

Write-Host "Publishing docker-compose.yml to Docker Hub as OCI Artifact..."
docker buildx imagetools create --file $composeFile $ociTarget
Write-Host "docker-compose.yml published successfully as '$ociTarget'."
#to Use: .\publish_image.ps1 -ImageName <image_name> -DockerHubUsername <docker_hub_username>
param(
    [Parameter(Mandatory=$true)]
    [string]$ImageName,
    [Parameter(Mandatory=$true)]
    [string]$DockerHubUsername
)

$ErrorActionPreference = "Stop"

$remoteTag = "$DockerHubUsername/$ImageName:latest"

Write-Host "Tagging local image '$ImageName' as '$remoteTag'..."
docker tag $ImageName $remoteTag

write-Host "Pushing image '$remoteTag' to Docker Hub..."
docker push $remoteTag
Write-Host "Image '$remoteTag' published successfully to Docker Hub."   
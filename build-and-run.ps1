# Script de PowerShell: compila, construye imagen y ejecuta contenedor
$ErrorActionPreference = 'Stop'

Write-Host "1) mvn clean package -DskipTests"
mvn clean package -DskipTests

Write-Host "2) docker build -t ladob-api:latest ."
docker build -t ladob-api:latest .

Write-Host "3) Stop y rm contenedor previo (si existe)"
if (docker ps -aq -f "name=ladob-api") {
    docker stop ladob-api | Out-Null
    docker rm ladob-api | Out-Null
}

Write-Host "4) docker run -d --name ladob-api -p 8080:8080 ladob-api:latest"
docker run -d --name ladob-api -p 8080:8080 ladob-api:latest

Write-Host "5) docker logs -f ladob-api"
docker logs -f ladob-api

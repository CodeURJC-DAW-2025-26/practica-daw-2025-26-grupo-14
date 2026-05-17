#Use with command powershell -ExecutionPolicy Bypass -File build-and-run.ps1
#$ErrorActionPreference = 'Stop' --> Error pssw injection insecure, step 4

if ($PSScriptRoot) {
    $scriptDir = $PSScriptRoot
}
elseif ($MyInvocation.MyCommand.Path) {
    $scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
}
else {
    $scriptDir = Join-Path (Get-Location) "docker"
}

$repoRoot = (Resolve-Path (Join-Path $scriptDir "..")).Path
 
$imageName = "ladob-api:latest"
$apiContainer = "ladob-api"
$dbContainer = "ladob-mysql"
$networkName = "ladob-network"
$volumeName = "ladob-mysql-data"

$dbName = "ladob"
$dbUser = "root"
$dbPassword = 'Th1$1$MyP@$$W0Rd'
$jdbcUrl = "jdbc:mysql://$($dbContainer):3306/$($dbName)?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true"

function Test-DockerResourceExists {
    param(
        [string]$ResourceType,
        [string]$Name
    )

    $result = docker $ResourceType ls --format "{{.Name}}" | Select-String -Pattern "^$Name$" -Quiet
    return $result
}

function Test-ContainerRunning {
    param(
        [string]$Name
    )

    $state = docker inspect -f "{{.State.Running}}" $Name 2>$null
    return $state -eq "true"
}

function Show-ContainerLogsAndFail {
    param(
        [string]$Name,
        [string]$Message
    )

    Write-Host ""
    Write-Host "Logs de ${Name}:"
    docker logs $Name
    throw $Message
}

Write-Host "1) Create Docker network if it doesn't exist"
if (-not (Test-DockerResourceExists "network" $networkName)) {
    docker network create $networkName | Out-Null
}

Write-Host "2) Create Docker volume if it doesn't exist"
if (-not (Test-DockerResourceExists "volume" $volumeName)) {
    docker volume create $volumeName | Out-Null
}

Write-Host "3) Build MySQL if it doesn't exist"
if (-not (docker ps -aq -f "name=$dbContainer")) {
    docker run -d `
        --name $dbContainer `
        --network $networkName `
        -e MYSQL_DATABASE=$dbName `
        -e MYSQL_ROOT_PASSWORD=$dbPassword `
        -v "${volumeName}:/var/lib/mysql" `
        mysql:8.4 | Out-Null
}
elseif (-not (docker ps -q -f "name=$dbContainer")) {
    docker start $dbContainer | Out-Null
}

if (-not (Test-ContainerRunning $dbContainer)) {
    Show-ContainerLogsAndFail $dbContainer "MySQL isn´t running."
}

Write-Host "4) Wait for MySQL to accept connections"
$maxAttempts = 30
for ($attempt = 1; $attempt -le $maxAttempts; $attempt++) {
    if (-not (Test-ContainerRunning $dbContainer)) {
        Show-ContainerLogsAndFail $dbContainer "MySQL has stopped while waiting to be ready."
    }

    docker exec $dbContainer mysqladmin ping -h 127.0.0.1 -u $dbUser "-p$dbPassword" --silent *> $null
    if ($LASTEXITCODE -eq 0) {
        break
    }

    if ($attempt -eq $maxAttempts) {
        throw "MySQL has not been ready in time."
    }

    Start-Sleep -Seconds 2
}

Write-Host "5) Build Docker image for the API"
docker build -f (Join-Path $scriptDir "Dockerfile") -t $imageName $repoRoot

Write-Host "6) Recreate previous API container if it exists"
if (docker ps -aq -f "name=$apiContainer") {
    docker stop $apiContainer | Out-Null
    docker rm $apiContainer | Out-Null
}

Write-Host "7) Start the API connected to MySQL"
docker run -d `
    --name $apiContainer `
    --network $networkName `
    -p 8080:8080 `
    -p 8443:8443 `
    -e SPRING_DATASOURCE_URL=$jdbcUrl `
    -e SPRING_DATASOURCE_USERNAME=$dbUser `
    -e SPRING_DATASOURCE_PASSWORD=$dbPassword `
    $imageName | Out-Null

Write-Host "8) docker logs -f $apiContainer"
docker logs -f $apiContainer

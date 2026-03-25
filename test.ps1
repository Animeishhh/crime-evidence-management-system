$ErrorActionPreference = "Stop"

$regBody = '{"name":"officer2", "email":"off2@police.com", "password":"pw", "role":"OFFICER"}'
Write-Output "Registering user..."
Invoke-RestMethod -Uri http://localhost:8080/auth/register -Method Post -ContentType "application/json" -Body $regBody

$loginBody = '{"username": "officer2", "password": "pw"}'
Write-Output "Logging in..."
$loginResponse = Invoke-RestMethod -Uri http://localhost:8080/auth/login -Method Post -ContentType "application/json" -Body $loginBody
$token = $loginResponse.token
Write-Output "Token acquired."

$headers = @{ Authorization = "Bearer $token" }

Write-Output "Creating dummy file..."
"dummy content" | Out-File -FilePath "dummy.txt" -Encoding utf8

$evidenceJson = '{"type":"Weapon", "description":"Uploaded via token", "caseId":1}'

Write-Output "POSTing evidence with file using curl..."
$curlOutput = curl.exe -s -X POST -H "Authorization: Bearer $token" -F "file=@dummy.txt" -F "evidence=$evidenceJson;type=application/json" http://localhost:8080/evidence

Write-Output "Evidence response: $curlOutput"

Write-Output "Querying Dashboard Stats..."
$dashOutput = Invoke-RestMethod -Uri http://localhost:8080/dashboard/stats -Method Get -Headers $headers
$dashOutput | ConvertTo-Json

Write-Output "Querying Logs filtered by user 'officer2'..."
$logsOutput = Invoke-RestMethod -Uri "http://localhost:8080/logs/filter/user?username=officer2" -Method Get -Headers $headers
$logsOutput | ConvertTo-Json

Write-Output "Testing completed!"

#!/usr/bin/env bash

curl -H "Content-Type: application/json" -X POST --data "@queries/createStation.json" http://localhost:8080/stations
curl http://localhost:8080/stations/id/43 -o responses/createdStationResponse.json


curl -H "Content-Type: application/json" -X PUT --data "@queries/updateStation.json" http://localhost:8080/stations/43/location
curl http://localhost:8080/stations/id/43 -o responses/updatedStationResponse.json

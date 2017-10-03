#!/usr/bin/env bash
curl http://localhost:8080/stations/id/38332 -o responses/stationId_38332.json
curl http://localhost:8080/stations/id/0 -o responses/stationId_missing.json

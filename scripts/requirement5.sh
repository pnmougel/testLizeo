#!/usr/bin/env bash
curl http://localhost:8080/stations/country?country=FR -o responses/stationInFR.json
curl http://localhost:8080/stations/country -o responses/stationInFRByDefault.json
curl http://localhost:8080/stations/country?country=DE -o responses/stationInDE.json
curl http://localhost:8080/stations/country?country=EN -o responses/stationInEN.json


#!/usr/bin/env bash
curl http://localhost:8080/dealers/13/stations -o responses/stationsOfDealer_13.json
curl http://localhost:8080/dealers/0/stations -o responses/stationsOfMissingDealer.json


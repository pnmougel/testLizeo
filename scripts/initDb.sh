#! /bin/sh
curl http://localhost:8080/stations/csv -F "csv=@../data/stations.csv"
curl http://localhost:8080/dealers/csv -F "csv=@../data/dealers.csv"

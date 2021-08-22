#!/bin/bash

echo "Stoping all services..."
pkill -9 -f pf-service-0.1.0.jar
pkill -9 -f ref-service-0.1.0.jar
pkill -9 -f lab-service-0.1.0.jar
pkill -9 -f nirs-service-0.1.0.jar
pkill -9 -f cell-service-0.1.0.jar
pkill -9 -f loc-service-0.1.0.jar
pkill -9 -f mgmt-0.0.1-SNAPSHOT.jar
pkill -9 -f mgmtmov-0.0.1-SNAPSHOT.jar
pkill -9 -f sp-0.0.1-SNAPSHOT.jar
pkill -9 -f neighbor-service-0.1.0.jar
pkill -9 -f server-0.0.1-SNAPSHOT.jar

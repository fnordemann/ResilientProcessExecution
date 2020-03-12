#!/bin/bash

echo "Stoping all services..."
pkill -9 -f osas-service-0.1.0.jar
pkill -9 -f ogcs-service-0.1.0.jar
pkill -9 -f nirs-service-0.1.0.jar
pkill -9 -f lgcs-service-0.1.0.jar
pkill -9 -f mgmt-0.0.1-SNAPSHOT.jar
pkill -9 -f mgmtmov-0.0.1-SNAPSHOT.jar
pkill -9 -f sp-0.0.1-SNAPSHOT.jar
pkill -9 -f neighbor-service-0.1.0.jar
pkill -9 -f server-0.0.1-SNAPSHOT.jar

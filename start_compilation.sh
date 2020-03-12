#!/bin/bash

# Stop services on ctrl_c
trap ctrl_c INT

function ctrl_c() {
		echo " "
        ../stop_services.sh
}

# Compile services
echo "Going to compile all services..."
echo " "
cd datatypes
mvn clean install
cd ../eureka-server
mvn clean install
cd ../lgcs-service
mvn clean install
cd ../mgmt
mvn clean install
cd ../mgmt-mov
mvn clean install
cd ../neighbor-service
mvn clean install
cd ../nirs-service
mvn clean install
cd ../ogcs-service
mvn clean install
cd ../osas-service
mvn clean install
cd ../sp
mvn clean install

#!/bin/bash

# Stop services on ctrl_c
#trap ctrl_c INT
#
#function ctrl_c() {
#		echo " "
#        sh stop_services.sh
#}

# Compile services
echo "Going to compile all services..."
echo " "
cd datatypes
mvn clean install
cd ../eureka-server
mvn clean install
cd ../pf-service
mvn clean install
cd ../loc-service
mvn clean install
cd ../mgmt
mvn clean install
cd ../mgmt-mov
mvn clean install
cd ../neighbor-service
mvn clean install
cd ../lab-service
mvn clean install
cd ../nirs-service
mvn clean install
cd ../cell-service
mvn clean install
cd ../ref-service
mvn clean install
cd ../sp
mvn clean install

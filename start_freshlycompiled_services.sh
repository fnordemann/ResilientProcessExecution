#!/bin/bash

# Stop services on ctrl_c
trap ctrl_c INT

function ctrl_c() {
	echo " "
    ./stop_services.sh
}

# Starting all services
echo "Starting all services in parallel, this might take a while."
echo "Following, S3 will start a slurry process and show its output log."
echo "Use the Postman-REST-Calls to:"
echo "     - restart the slurry process on S3"
echo "         - modify required min accuracy of path"
echo "         - modify allowed max total cost of path"
echo "         - modify the accuracy weight"
echo "         - modify the cost weight"
echo "         - modify the time weight"
echo "     - add/delete neighbors on S3"
echo "     - inspect Eureka servers"
echo "* During first slurry process execution on S3, not all services might be available."
echo "* This is normal behavior - the services require different amounts of time to start."
echo " "
java -jar -Dspring.profiles.active=cloud eureka-server/target/server-0.0.1-SNAPSHOT.jar > logs/eureka_cloud.txt &
java -jar -Dspring.profiles.active=sp eureka-server/target/server-0.0.1-SNAPSHOT.jar > logs/eureka_sp.txt &
java -jar -Dspring.profiles.active=nirs eureka-server/target/server-0.0.1-SNAPSHOT.jar > logs/eureka_nirs.txt &
java -jar -Dspring.profiles.active=loc eureka-server/target/server-0.0.1-SNAPSHOT.jar > logs/eureka_loc.txt &
java -jar -Dspring.profiles.active=pf pf-service/target/pf-service-0.1.0.jar > logs/pf.txt &
java -jar -Dspring.profiles.active=pfmov pf-service/target/pf-service-0.1.0.jar > logs/pfmov.txt &
java -jar -Dspring.profiles.active=ref1 ref-service/target/ref-service-0.1.0.jar > logs/ref1.txt &
java -jar -Dspring.profiles.active=refmov ref-service/target/ref-service-0.1.0.jar > logs/refmov.txt &
java -jar -Dspring.profiles.active=cell1 cell-service/target/cell-service-0.1.0.jar > logs/cell1.txt &
java -jar lab-service/target/lab-service-0.1.0.jar > logs/lab.txt &
java -jar nirs-service/target/nirs-service-0.1.0.jar > logs/nirs.txt &
java -jar loc-service/target/loc-service-0.1.0.jar > logs/loc.txt &
java -jar neighbor-service/target/neighbor-service-0.1.0.jar > logs/neighbor.txt &
java -jar sp/target/sp-0.0.1-SNAPSHOT.jar > logs/sp.txt &

# Show log of S3
tail -f logs/sp.txt

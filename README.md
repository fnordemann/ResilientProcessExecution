

# Introduction
This package provides the proof-of-concept implementation for the concepts presented in the paper "Resilient Business Process Execution using BPMN and Microservices". Since Github doesn't allow sharing private repositories with unknown collaborators/reviews, the code is hosted at GoogleDrive for the time of the review process. If accepted, the proof-of-concept implementation will be added to a public Github repository.

The README provides information to execute and manipulate the proof-of-concept implementation. Since setting up an unreliable communication environment for evaluating the proof-of-concept is cumbersome, the code was adapted to be executable on a single system. A Neighbor-Service for SP is able to add and delete neighbors, resulting in an emulated unreliable communication environment. REST-Helpercalls allow users to easily add and delete neighbors during execution. By interfacing a proactive routing protocol, the code is able to run in real-world environments.
# Run with Docker
This PoC offers a Docker integration to facilitate the initial effort of setting up every depencency. The docker container will copy all of the precompiled jar files within the "precompiled" directory to the internal file structure. After that, it will start the shell script "start_precompiled_services.sh", which has also been copied into the container namespace before! To start the system simply execute the following steps:
Build the BPMN Container:

    docker-compose build

Run the Container and expose all the necessary ports (ref. "Single system port mapping" section). Notice the "-d" option as it will detach from all output! If you want to see the logs, leave out the "-d" option

    docker-compose up -d

To shut down the system, simply run

    docker-compose down

# Single system port mapping
The port mapping for the scenario participants is illustrated below:
- Locally-emulated Cloud environment:
	- Eureka-Cloud: 8020
	- MGMT: 8025
	- OSAS: 8026/8027
	- OGCS: 8028/8029
- SP:
	- Eureka-SP: 8030
	- Neighbor-Service: 8031
	- Camunda-SP: 8035
	- MGMT-Mov: 8036
	- OSAS-Mov: 8037
- NIRS:
	- Eureka-NIRS: 8040
	- NIRS: 8045
- LGCS:
	- Eureka-LGCS: 8050
	- LGCS: 8055


# Control/manipulate scenario execution
REST-Helpercalls can be used to a start slurry process, to add/delete neighbor nodes, to inspect Eureka server instances. A collection of REST-calls can be imported into the program "Postman" (https://www.postman.com/):
	postman-rest-helpercalls -> Postman_REST_Helpercalls.postman_collection.json

Controlling/Inspecting BPMN processes running in Camunda BPM:
MGMT and SP execute BPMN processes, that can be started / monitored from Camunda tasklist / cockpit (username/password demo/demo) at:
- MGMT: http://localhost:8025
- SP: http://localhost:8035
- MGMT-MOV on SP: http://localhost:8036

Addiotionally, process / service opertion can be examined from logs at
	logs -> xyz-service.txt


# Execution of proof-of-concept (precompiled)
Users may execute precompiled Java archives or compile/adapt source files on their own (introduced later). Scripts are provided for UNIX environments.

Requirements: Java Runtime Environment (JRE, v1.8+), UNIX-based system for bash-scripts (optional!)

Automatically trigger execution: Run bash script

	./start_precompiled_services.sh

OR manually trigger execution: 
	

	java -jar -Dspring.profiles.active=cloud server-0.0.1-SNAPSHOT.jar > ../logs/eureka_cloud.txt &
    java -jar -Dspring.profiles.active=sp server-0.0.1-SNAPSHOT.jar > ../logs/eureka_sp.txt &
    java -jar -Dspring.profiles.active=nirs server-0.0.1-SNAPSHOT.jar > ../logs/eureka_nirs.txt &
    java -jar -Dspring.profiles.active=lgcs server-0.0.1-SNAPSHOT.jar > ../logs/eureka_lgcs.txt &
    java -jar -Dspring.profiles.active=osas1 osas-service-0.1.0.jar > ../logs/osas1.txt &
    java -jar -Dspring.profiles.active=osasmov osas-service-0.1.0.jar > ../logs/osas1.txt &
    java -jar -Dspring.profiles.active=ogcs1 ogcs-service-0.1.0.jar > ../logs/ogcs1.txt &
    java -jar nirs-service-0.1.0.jar > ../logs/nirs.txt &
    java -jar lgcs-service-0.1.0.jar > ../logs/lgcs.txt &
    java -jar mgmt-0.0.1-SNAPSHOT.jar > ../logs/mgmt.txt &
    java -jar mgmtmov-0.0.1-SNAPSHOT.jar > ../logs/mgmt_mov.txt &
    java -jar neighbor-service-0.1.0.jar > ../logs/neighbor.txt &
    java -jar sp-0.0.1-SNAPSHOT.jar > ../logs/sp.txt &
    tail -f ../logs/sp.txt

# Compilation and execution of proof-of-concept
Requirements: Java Development Kit (JDK, v1.8+), UNIX-based system for bash-scripts (optional!)

Automatically trigger compilation: Run bash script
	

    start_compilation.sh

OR manually compile:
	
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

Automatically trigger execution: Run bash script
	

    start_freshlycompiled_services.sh

OR manually trigger execution:
	

	java -jar -Dspring.profiles.active=cloud eureka-server/target/server-0.0.1-SNAPSHOT.jar > logs/eureka_cloud.txt &
    java -jar -Dspring.profiles.active=sp eureka-server/target/server-0.0.1-SNAPSHOT.jar > logs/eureka_sp.txt &
    java -jar -Dspring.profiles.active=nirs eureka-server/target/server-0.0.1-SNAPSHOT.jar > logs/eureka_nirs.txt &
    java -jar -Dspring.profiles.active=lgcs eureka-server/target/server-0.0.1-SNAPSHOT.jar > logs/eureka_lgcs.txt &
    java -jar -Dspring.profiles.active=osas1 osas-service/target/osas-service-0.1.0.jar > logs/osas1.txt &
    java -jar -Dspring.profiles.active=osasmov osas-service/target/osas-service-0.1.0.jar > logs/osas1.txt &
    java -jar -Dspring.profiles.active=ogcs1 ogcs-service/target/ogcs-service-0.1.0.jar > logs/ogcs1.txt &
    java -jar nirs-service/target/nirs-service-0.1.0.jar > logs/nirs.txt &
    java -jar lgcs-service/target/lgcs-service-0.1.0.jar > logs/lgcs.txt &
    java -jar mgmt/target/mgmt-0.0.1-SNAPSHOT.jar > logs/mgmt.txt &
    java -jar mgmt-mov/target/mgmtmov-0.0.1-SNAPSHOT.jar > logs/mgmt_mov.txt &
    java -jar neighbor-service/target/neighbor-service-0.1.0.jar > logs/neighbor.txt &
    java -jar sp/target/sp-0.0.1-SNAPSHOT.jar > logs/sp.txt &
    tail -f logs/sp.txt


# Adaptation of proof-of-concept implementation
- Users may find the source code commentaries helpfull for understanding/adapting the proof-of-concept.
- Real-world deployments require to adapt the network configuration of services
	xyz-service->src->main->resources->application.yml
- Service discovery and decision making is part of the SP implementation
- Multiple instances of OSAS/OGCS may be started by using profiles osas1/osas2 ogcs1/ogcs2


# Used software
- Java
- Spring Framework
- Camunda BPM
- Docker
# Introduction
This package provides the proof-of-concept implementation for the concepts presented in the paper "Resilient Business Process Execution using BPMN and Microservices". Since Github doesn't allow sharing private repositories with unknown collaborators/reviews, the code is hosted at GoogleDrive for the time of the review process. If accepted, the proof-of-concept implementation will be added to a public Github repository.

The ReadMe provides information to execute and manipulate the proof-of-concept implementation. Since setting up an unreliable communication environment for evaluating the proof-of-concept is cumbersome, the code was adapted to be executable on a single system. A Neighbor-Service for SP is able to add and delete neighbors, resulting in an emulated unreliable communication environment. REST-Helpercalls allow users to easily add and delete neighbors during execution. By interfacing a proactive routing protocol, the code is able to run in real-world environments.


# Single system port mapping
The port mapping for the scenario participants is illustrated below:
- Locally-emulated Cloud environment:
	- Eureka-Cloud: 8020
	- MGMT: 8025
	- OSAS: 8026/8027
	- OGCS: 8028/8029
- SP:
	- Eureka-SP: 8030
	- Neighbor-Service: 8031
	- Camunda-SP: 8035
	- MGMT-Mov: 8036
	- OSAS-Mov: 8037
- NIRS:
	- Eureka-NIRS: 8040
	- NIRS: 8045
- LGCS:
	- Eureka-LGCS: 8050
	- LGCS: 8055


# Control/manipulate scenario execution
REST-Helpercalls can be used to a start slurry process, to add/delete neighbor nodes, to inspect Eureka server instances. A collection of REST-calls can be imported into the program "Postman" (https://www.postman.com/):
	postman-rest-helpercalls -> Postman_REST_Helpercalls.postman_collection.json

Controlling/Inspecting BPMN processes running in Camunda BPM:
MGMT and SP execute BPMN processes, that can be started / monitored from Camunda tasklist / cockpit (username/password demo/demo) at:
- MGMT: http://localhost:8025
- SP: http://localhost:8035
- MGMT-MOV on SP: http://localhost:8036

Addiotionally, process / service opertion can be examined from logs at
	logs -> xyz-service.txt


# Execution of proof-of-concept (precompiled)
Users may execute precompiled Java archives or compile/adapt source files on their own (introduced later). Scripts are provided for UNIX environments.

**Requirements**: Java Runtime Environment (JRE, v1.8+), UNIX-based system for bash-scripts (optional!)

Automatically trigger execution: Run bash script
	

    start_precompiled_services.sh

OR manually trigger execution: 
	

	java -jar -Dspring.profiles.active=cloud server-0.0.1-SNAPSHOT.jar > ../logs/eureka_cloud.txt &
	java -jar -Dspring.profiles.active=sp server-0.0.1-SNAPSHOT.jar > ../logs/eureka_sp.txt &
    java -jar -Dspring.profiles.active=nirs server-0.0.1-SNAPSHOT.jar > ../logs/eureka_nirs.txt &
    java -jar -Dspring.profiles.active=lgcs server-0.0.1-SNAPSHOT.jar > ../logs/eureka_lgcs.txt &
    java -jar -Dspring.profiles.active=osas1 osas-service-0.1.0.jar > ../logs/osas1.txt &
    java -jar -Dspring.profiles.active=osasmov osas-service-0.1.0.jar > ../logs/osas1.txt &
    java -jar -Dspring.profiles.active=ogcs1 ogcs-service-0.1.0.jar > ../logs/ogcs1.txt &
    java -jar nirs-service-0.1.0.jar > ../logs/nirs.txt &
    java -jar lgcs-service-0.1.0.jar > ../logs/lgcs.txt &
    java -jar mgmt-0.0.1-SNAPSHOT.jar > ../logs/mgmt.txt &
    java -jar mgmtmov-0.0.1-SNAPSHOT.jar > ../logs/mgmt_mov.txt &
    java -jar neighbor-service-0.1.0.jar > ../logs/neighbor.txt &
    java -jar sp-0.0.1-SNAPSHOT.jar > ../logs/sp.txt &
    tail -f ../logs/sp.txt


# Compilation and execution of proof-of-concept
Requirements: Java Development Kit (JDK, v1.8+), UNIX-based system for bash-scripts (optional!)

Automatically trigger compilation: Run bash script
	

    start_compilation.sh

OR manually compile:
	
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

Automatically trigger execution: Run bash script

	start_freshlycompiled_services.sh

OR manually trigger execution:
	

    java -jar -Dspring.profiles.active=cloud eureka-server/target/server-0.0.1-SNAPSHOT.jar > logs/eureka_cloud.txt &
	java -jar -Dspring.profiles.active=sp eureka-server/target/server-0.0.1-SNAPSHOT.jar > logs/eureka_sp.txt &
    java -jar -Dspring.profiles.active=nirs eureka-server/target/server-0.0.1-SNAPSHOT.jar > logs/eureka_nirs.txt &
    java -jar -Dspring.profiles.active=lgcs eureka-server/target/server-0.0.1-SNAPSHOT.jar > logs/eureka_lgcs.txt &
    java -jar -Dspring.profiles.active=osas1 osas-service/target/osas-service-0.1.0.jar > logs/osas1.txt &
    java -jar -Dspring.profiles.active=osasmov osas-service/target/osas-service-0.1.0.jar > logs/osas1.txt &
    java -jar -Dspring.profiles.active=ogcs1 ogcs-service/target/ogcs-service-0.1.0.jar > logs/ogcs1.txt &
    java -jar nirs-service/target/nirs-service-0.1.0.jar > logs/nirs.txt &
    java -jar lgcs-service/target/lgcs-service-0.1.0.jar > logs/lgcs.txt &
    java -jar mgmt/target/mgmt-0.0.1-SNAPSHOT.jar > logs/mgmt.txt &
    java -jar mgmt-mov/target/mgmtmov-0.0.1-SNAPSHOT.jar > logs/mgmt_mov.txt &
    java -jar neighbor-service/target/neighbor-service-0.1.0.jar > logs/neighbor.txt &
    java -jar sp/target/sp-0.0.1-SNAPSHOT.jar > logs/sp.txt &
    tail -f logs/sp.txt


# Adaptation of proof-of-concept implementation
- Users may find the source code commentaries helpfull for understanding/adapting the proof-of-concept.
- Real-world deployments require to adapt the network configuration of services
	xyz-service->src->main->resources->application.yml
- Service discovery and decision making is part of the SP implementation
- Multiple instances of OSAS/OGCS may be started by using profiles osas1/osas2 ogcs1/ogcs2


# Used software
- Java
- Spring Framework
- Camunda BPM



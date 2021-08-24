# Resilient Business Process Execution
This repository provides the proof-of-concept implementation for the resiliency concepts and implemention strategies presented in the paper *Resilient Business Process Modeling and Execution using BPMN and Microservices*. Since setting up an unreliable communication environment for evaluating the proof-of-concept is cumbersome, the code was adapted to be executable on a single system. A Neighbor-Service for *S3* can add and delete neighbors, resulting in an emulated unreliable communication environment. By interfacing a proactive routing protocol used in the unreliable network, the code may be used for execution in real-world environments.

## Run the proof-of-concept implementation
The following three options exist to run the proof-of-concept implementation. Afterward, the execution can be controlled and manipulated by the methods presented in the next section.

### Option 1: Run with Docker
Docker integration facilitates the execution of the proof-of-concept by setting up all dependencies. **Attention**: Docker commands may take a long time to execute - it is recommended to increase the available resources in the docker settings. 

Build the BPMN Container:

    docker-compose build

Run the container and expose all necessary ports (see *Single system port mapping* section). If no output is desired, add the option *-d* to the statement.

    docker-compose up

To shut down the system, run:

    docker-compose down

### Option 2: Run precompiled .jar-files
Users may run precompiled Java archives, avoiding the need for source code compilations. Shell scripts are provided for UNIX environments.

**Requirements**: Java Runtime Environment (JRE, v1.8+)

Automatically trigger execution by running:

    start_precompiled_services.sh

OR manually trigger execution by:

    java -jar -Dspring.profiles.active=cloud server-0.0.1-SNAPSHOT.jar > ../logs/eureka_cloud.txt &
    java -jar -Dspring.profiles.active=sp server-0.0.1-SNAPSHOT.jar > ../logs/eureka_sp.txt &
    java -jar -Dspring.profiles.active=nirs server-0.0.1-SNAPSHOT.jar > ../logs/eureka_nirs.txt &
    java -jar -Dspring.profiles.active=loc server-0.0.1-SNAPSHOT.jar > ../logs/eureka_loc.txt &
    java -jar -Dspring.profiles.active=pf pf-service-0.1.0.jar > ../logs/pf1.txt &
    java -jar -Dspring.profiles.active=pfmov pf-service-0.1.0.jar > ../logs/pfmov.txt &
    java -jar -Dspring.profiles.active=ref1 ref-service-0.1.0.jar > ../logs/ref1.txt &
    java -jar -Dspring.profiles.active=refmov ref-service-0.1.0.jar > ../logs/refmov.txt &
    java -jar -Dspring.profiles.active=cell1 cell-service-0.1.0.jar > ../logs/cell1.txt &
    java -jar lab-service-0.1.0.jar > ../logs/lab.txt &
    java -jar nirs-service-0.1.0.jar > ../logs/nirs.txt &
    java -jar loc-service-0.1.0.jar > ../logs/loc.txt &
    java -jar neighbor-service-0.1.0.jar > ../logs/neighbor.txt &
    java -jar sp-0.0.1-SNAPSHOT.jar > ../logs/sp.txt &

    tail -f ../logs/sp.txt

### Option 3: Compile and run source code
**Requirements**: Java Development Kit (JDK, v1.8+), Maven 3.5.0+

#### Compilation of source code
Automatically trigger compilation by running:
    
    start_compilation.sh

OR manually compile:
    
    cd datatypes
    mvn clean install
    cd ../eureka-server
    mvn clean install
    cd ../neighbor-service
    mvn clean install
    cd ../pf-service
    mvn clean install
    cd ../lab-service
    mvn clean install
    cd ../nirs-service
    mvn clean install
    cd ../ref-service
    mvn clean install
    cd ../cell-service
    mvn clean install
    cd ../loc-service
    mvn clean install
    cd ../sp
    mvn clean install

#### Running compiled source code
Automatically trigger execution by running:

    start_freshlycompiled_services.sh

OR manually trigger execution by:    

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

    tail -f logs/sp.txt

## Controlling scenario execution
Different options to control and manipulate the slurry process execution exist.

### REST-Helpercalls in Postman
REST calls can be used to a start slurry process, to add/delete neighbor nodes and to inspect Eureka server instances. A collection of REST calls can be imported into the program *Postman* (https://www.postman.com):

    postman-rest-helpercalls -> Postman_REST_Helpercalls.postman_collection.json

### Controlling and inspecting BPMN processes running in Camunda BPM
*MGMT* and *S3* execute BPMN processes that can be started / monitored from Camunda tools (for *username/password*, use *demo/demo*). Use Camunda Tasklist to start a slurry process and Camunda Cockpit to inspect running processes at:
- MGMT: http://localhost:8025
- S3: http://localhost:8035
- MGMT(L) on S3: http://localhost:8036

### Inspecting Eureka server instances
The Eureka servers are providing information about their current status, including registered services:
- Eureka-Cloud: http://localhost:8020
- Eureka-S3: http://localhost:8030
- Eureka-NIRS: http://localhost:8040
- Eureka-LOC: http://localhost:8050

### Process and service logs
Process and service operation can be examined from logs at:

    logs -> xyz-service.txt

### Single-system port mapping
Some services also provide information by accessing their interfaces. Besides, port information is helpful to adapt scenario execution. The port mapping for the scenario participants is illustrated below:
- Locally-emulated Cloud environment:
    - Eureka-Cloud: 8020
    - PF: 8024
    - LAB: 8023
    - REF: 8026
    - CELL: 8028
- S3:
    - Eureka-S3: 8030
    - Neighbor-Service: 8031
    - Camunda-S3: 8035
    - PF(L): 8038
    - REF(L): 8037
- NIRS:
    - Eureka-NIRS: 8040
    - NIRS: 8045
- LOC:
    - Eureka-LOC: 8050
    - LOC: 8055

## Adaptation of proof-of-concept implementation
- Users may find the source code commentaries helpful for understanding/adapting the proof-of-concept.
- Real-world deployments require to adapt the network configuration of services
    xyz-service->src->main->resources->application.yml
- Service discovery and decision making is part of the *S3* implementation and may be used in other services.


## Used software
- Java
- Spring Framework
- JGraphT Java Library
- Camunda BPM
- Docker (for executing the proof-of-concept)

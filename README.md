# Resilient Business Process Execution
This repository provides the proof-of-concept implementations for the resiliency concepts and implementation strategies presented in the PhD thesis *Modeling and Execution of Resilient Business Processes in Unreliable Communication Environments*. Since setting up an unreliable communication environment for evaluating the proof-of-concept is cumbersome, the code is designed to be executable on a single system. A Neighbor-Service for the slurry spreader can add and delete neighbors, resulting in an emulated unreliable communication environment. By interfacing a proactive routing protocol used in an unreliable network, the code may be used for execution in real-world environments.

The evaluation of the thesis includes two implementations. While the *master* branch includes the graph-based multi-criteria process analysis of slurry process S3-Exe, the branch *phd-wsm* includes the second slurry process featuring an MGMT-entity and decision-making based on the Weighted Sum Model (WSM).

## Run the proof-of-concept implementations
First of all, the repository needs to be cloned to the system supposed to execute the proof-of-concept.

Clone the *master* branch for process S3-Exe (graph-based decision-making):

    git clone https://github.com/fnordemann/ResilientProcessExecution.git

or clone and checkout the *phd-wsm* branch for process S4-Exe (WSM-based decision-making):

    git clone --single-branch --branch phd-wsm https://github.com/fnordemann/ResilientProcessExecution.git

The following three options exist to run the proof-of-concept implementations. Afterwards, the execution can be controlled and manipulated by the methods presented in the following section.

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

### Option 3: Compile and run source code
**Requirements**: Java Development Kit (JDK, v1.8+), Maven 3.5.0+

#### Compilation of source code
Automatically trigger compilation by running:
    
    start_compilation.sh

#### Running compiled source code
Automatically trigger execution by running:

    start_freshlycompiled_services.sh

## Controlling scenario execution
Different options to control and manipulate the slurry process execution exist.

### REST-Calls in Postman
REST-calls can be used to start a slurry process, to add/delete neighbor nodes and to inspect Eureka server instances. A collection of REST calls can be imported into the program *Postman* (https://www.postman.com):

    postman-rest-helpercalls -> Postman_REST_Helpercalls.postman_collection.json

### Controlling and inspecting BPMN processes running in Camunda BPM
The slurry spreader and the MGMT-entity (only in process S4-Exe) execute BPMN processes that can be started/monitored from Camunda tools (for *username/password*, use *demo/demo*). Use Camunda Tasklist to start a slurry process and Camunda Cockpit to inspect running processes at:
- S3-Exe/S4-Exe: http://localhost:8035
- MGMT: http://localhost:8025
- MGMT(L) on S3: http://localhost:8036

### Inspecting Eureka server instances
The Eureka servers are providing information about their current status, including registered services:
- Eureka-Cloud: http://localhost:8020
- Eureka-S: http://localhost:8030
- Eureka-NIRS: http://localhost:8040
- Eureka-LOC: http://localhost:8050

### Process and service logs
Process and service operation can be examined from logs at:

    logs -> xyz-service.txt

### Single-system port mapping
Some services also provide information by accessing their interfaces. Besides, port information is helpful to adapt scenario execution. The port mapping for the scenario participants is illustrated below:
- Locally-emulated Cloud environment:
    - Eureka-Cloud: 8020
    - PF: 8024 (only in S3-Exe)
    - LAB: 8023 (only in S3-Exe)
    - REF: 8026
    - REF2: 8026 (only in S4-Exe)
    - CELL: 8028
    - CELL2: 8029 (only in S4-Exe)
- S3-Exe/S4-Exe:
    - Eureka: 8030
    - Neighbor-Service: 8031
    - Camunda: 8035
    - PF(L): 8038 (only in S3-Exe)
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
- Service discovery and decision-making is part of the slurry spreader implementation and may be used in other services.


## Used software
- Java
- Spring Framework
- JGraphT Java Library
- Camunda BPM
- Docker (for executing the proof-of-concept)

FROM maven:3.6.3-jdk-8-openj9

WORKDIR /

COPY datatypes/ /datatypes/
COPY eureka-server/ /eureka-server/
COPY loc-service/ /loc-service/
COPY mgmt/ /mgmt/
COPY mgmt-mov/ /mgmt-mov/
COPY neighbor-service/ /neighbor-service/
COPY nirs-service/ /nirs-service/
COPY cell-service/ /cell-service/
COPY ref-service/ /ref-service/
COPY postman-rest-helpercalls/ /postman-rest-helpercalls/
COPY sp/ /sp/
COPY *.sh /

EXPOSE 8020 8025 8026 8027 8028 8029 8030 8031 8035 8036 8037 8040 8041 8045 8050 8055
RUN bash start_compilation.sh
CMD bash start_freshlycompiled_services.sh

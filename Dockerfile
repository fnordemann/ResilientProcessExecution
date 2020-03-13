FROM openjdk:8-jre-alpine

WORKDIR /

COPY precompiled/* /precompiled/
COPY *.sh /

EXPOSE 8020 8025 8026 8027 8028 8029 8030 8031 8035 8036 8037 8040 8041 8045 8050 8055

CMD sh start_precompiled_services.sh

/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. Camunda licenses this file to you under the Apache License,
 * Version 2.0; you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.example.mgmtmov;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.camunda.bpm.spring.boot.starter.event.PostDeployEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.event.EventListener;

import java.util.List;

@SpringBootApplication
@EnableProcessApplication
@EnableDiscoveryClient
public class MgmtMovApplication {

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private DiscoveryClient discoveryClient;

    public static void main(String... args) {
        SpringApplication.run(MgmtMovApplication.class, args);
    }

    @EventListener
    private void processPostDeploy(PostDeployEvent event) {
        if (runtimeService != null) {
            System.out.println("runtimeService in MgmtMovApplication! " + runtimeService.toString());
        }

        if (discoveryClient != null) {
            List<ServiceInstance> serviceInstanceList = discoveryClient.getInstances("ingredients-service");
            System.out.println("List elements: " + serviceInstanceList.size());
            for (int i = 0; i < serviceInstanceList.size(); i++) {
                ServiceInstance serviceInstance = serviceInstanceList.get(i);
                System.out.println("Host: " + serviceInstance.getHost());
                System.out.println("InstanceId: " + serviceInstance.getInstanceId());
                System.out.println("Metadata: " + serviceInstance.getMetadata().toString());
            }
            runtimeService.startProcessInstanceByKey("MgmtMov");
        }
    }
}
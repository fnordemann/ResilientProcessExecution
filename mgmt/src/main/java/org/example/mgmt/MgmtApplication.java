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
package org.example.mgmt;

import com.netflix.appinfo.ApplicationInfoManager;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.camunda.bpm.spring.boot.starter.event.PostDeployEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.event.EventListener;

import java.util.logging.Logger;

@SpringBootApplication
@EnableProcessApplication
@EnableDiscoveryClient
public class MgmtApplication {
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    @Qualifier("eurekaApplicationInfoManager")
    private ApplicationInfoManager applicationInfoManager;
    private final static Logger LOGGER = Logger.getLogger("MGMT");

    public static void main(String... args) {
        SpringApplication.run(MgmtApplication.class, args);
    }

    @EventListener
    private void processPostDeploy(PostDeployEvent event) {

        // Info
        LOGGER.info("Going to start MGMT process...");
        // Start MGMT process
        runtimeService.startProcessInstanceByKey("MgmtKey");
    }
}
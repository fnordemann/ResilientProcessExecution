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
package org.example.sp;

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
public class SpApplication {

    @Autowired
    private RuntimeService runtimeService;
    private final static Logger LOGGER = Logger.getLogger("SP");
    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    @Qualifier("eurekaApplicationInfoManager")
    private ApplicationInfoManager applicationInfoManager;

    public static void main(String... args) {
        SpringApplication.run(SpApplication.class, args);
    }

    @EventListener
    private void processPostDeploy(PostDeployEvent event) {

        // Start process
        LOGGER.info("");
        LOGGER.info("-------------------------------");
        LOGGER.info("Slurry process S3 started...");
        LOGGER.info("\ttaskId: 0000");
        LOGGER.info("\tdMinAccuracy: 0.3");
        LOGGER.info("\tdCostLimit: 2.0");
        LOGGER.info("\tdAccuracyWeight: 0.5");
        LOGGER.info("\tdCostWeight: 0.3");
        LOGGER.info("\tdTimeWeight: 0.2");
        LOGGER.info("* Graph-based decision making *");
        LOGGER.info("-------------------------------");
        LOGGER.info("");
        runtimeService.startProcessInstanceByKey("SpKey");
    }
}
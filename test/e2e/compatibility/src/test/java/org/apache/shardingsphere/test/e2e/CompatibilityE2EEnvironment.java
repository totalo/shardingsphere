/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.test.e2e;

import com.google.common.base.Strings;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.shardingsphere.test.e2e.container.EnvTypeEnum;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@Getter
public final class CompatibilityE2EEnvironment {
    
    private static final CompatibilityE2EEnvironment INSTANCE = new CompatibilityE2EEnvironment();
    
    private final Properties props;
    
    private final EnvTypeEnum itEnvType;
    
    private final List<String> mysqlVersions;
    
    private CompatibilityE2EEnvironment() {
        props = loadProperties();
        itEnvType = EnvTypeEnum.valueOf(props.getProperty("compatibility.it.env.type", EnvTypeEnum.NONE.name()).toUpperCase());
        mysqlVersions = Arrays.stream(props.getOrDefault("compatibility.it.docker.mysql.version", "").toString().split(",")).filter(each -> !Strings.isNullOrEmpty(each)).collect(Collectors.toList());
    }
    
    @SneakyThrows(IOException.class)
    private Properties loadProperties() {
        Properties result = new Properties();
        try (InputStream inputStream = CompatibilityE2EEnvironment.class.getClassLoader().getResourceAsStream("env/it-env.properties")) {
            result.load(inputStream);
        }
        for (String each : System.getProperties().stringPropertyNames()) {
            result.setProperty(each, System.getProperty(each));
        }
        return result;
    }
    
    /**
     * Get instance.
     *
     * @return singleton instance
     */
    public static CompatibilityE2EEnvironment getInstance() {
        return INSTANCE;
    }
}

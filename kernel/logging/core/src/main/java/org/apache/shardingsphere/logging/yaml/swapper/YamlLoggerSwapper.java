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

package org.apache.shardingsphere.logging.yaml.swapper;

import org.apache.shardingsphere.infra.util.yaml.swapper.YamlConfigurationSwapper;
import org.apache.shardingsphere.logging.logger.ShardingSphereLogger;
import org.apache.shardingsphere.logging.yaml.config.YamlLoggerConfiguration;

/**
 * YAML logger swapper.
 */
public final class YamlLoggerSwapper implements YamlConfigurationSwapper<YamlLoggerConfiguration, ShardingSphereLogger> {
    
    @Override
    public YamlLoggerConfiguration swapToYamlConfiguration(final ShardingSphereLogger data) {
        YamlLoggerConfiguration result = new YamlLoggerConfiguration();
        result.setLoggerName(data.getLoggerName());
        result.setLevel(data.getLevel());
        result.setAdditivity(data.getAdditivity());
        result.setAppenderName(data.getAppenderName());
        result.setProps(data.getProps());
        return result;
    }
    
    @Override
    public ShardingSphereLogger swapToObject(final YamlLoggerConfiguration yamlConfig) {
        ShardingSphereLogger result = new ShardingSphereLogger(yamlConfig.getLoggerName(), yamlConfig.getLevel(), yamlConfig.getAdditivity(), yamlConfig.getAppenderName());
        result.getProps().putAll(yamlConfig.getProps());
        return result;
    }
}

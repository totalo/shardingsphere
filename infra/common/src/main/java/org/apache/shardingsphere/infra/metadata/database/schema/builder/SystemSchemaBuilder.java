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

package org.apache.shardingsphere.infra.metadata.database.schema.builder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.shardingsphere.infra.config.props.ConfigurationProperties;
import org.apache.shardingsphere.infra.config.props.temporary.TemporaryConfigurationPropertyKey;
import org.apache.shardingsphere.infra.database.core.metadata.database.metadata.DialectDatabaseMetaData;
import org.apache.shardingsphere.infra.database.core.metadata.database.system.SystemDatabase;
import org.apache.shardingsphere.infra.database.core.type.DatabaseType;
import org.apache.shardingsphere.infra.database.core.type.DatabaseTypeRegistry;
import org.apache.shardingsphere.infra.metadata.database.schema.manager.SystemSchemaManager;
import org.apache.shardingsphere.infra.metadata.database.schema.model.ShardingSphereSchema;
import org.apache.shardingsphere.infra.metadata.database.schema.model.ShardingSphereTable;
import org.apache.shardingsphere.infra.yaml.schema.pojo.YamlShardingSphereTable;
import org.apache.shardingsphere.infra.yaml.schema.swapper.YamlTableSwapper;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;

/**
 * System schema builder.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SystemSchemaBuilder {
    
    /**
     * Build system schema.
     *
     * @param databaseName database name
     * @param databaseType database type
     * @param props configuration properties
     * @return ShardingSphere system schema map
     */
    public static Map<String, ShardingSphereSchema> build(final String databaseName, final DatabaseType databaseType, final ConfigurationProperties props) {
        SystemDatabase systemDatabase = new SystemDatabase(databaseType);
        Map<String, ShardingSphereSchema> result = new LinkedHashMap<>(systemDatabase.getSystemSchemas().size(), 1F);
        boolean isSystemSchemaMetaDataEnabled = isSystemSchemaMetaDataEnabled(props.getProps());
        YamlTableSwapper swapper = new YamlTableSwapper();
        for (String each : getSystemSchemas(databaseName, databaseType, systemDatabase)) {
            result.put(each.toLowerCase(), createSchema(each, SystemSchemaManager.getAllInputStreams(databaseType.getType(), each), swapper, isSystemSchemaMetaDataEnabled));
        }
        return result;
    }
    
    private static boolean isSystemSchemaMetaDataEnabled(final Properties props) {
        TemporaryConfigurationPropertyKey configKey = TemporaryConfigurationPropertyKey.SYSTEM_SCHEMA_METADATA_ASSEMBLY_ENABLED;
        return Boolean.parseBoolean(props.getOrDefault(configKey.getKey(), configKey.getDefaultValue()).toString());
    }
    
    private static Collection<String> getSystemSchemas(final String originalDatabaseName, final DatabaseType databaseType, final SystemDatabase systemDatabase) {
        DialectDatabaseMetaData dialectDatabaseMetaData = new DatabaseTypeRegistry(databaseType).getDialectDatabaseMetaData();
        String databaseName = dialectDatabaseMetaData.getSchemaOption().getDefaultSchema().isPresent() ? "postgres" : originalDatabaseName;
        return systemDatabase.getSystemDatabaseSchemaMap().getOrDefault(databaseName, Collections.emptyList());
    }
    
    private static ShardingSphereSchema createSchema(final String schemaName, final Collection<InputStream> schemaStreams, final YamlTableSwapper swapper,
                                                     final boolean isSystemSchemaMetadataEnabled) {
        Collection<ShardingSphereTable> tables = new LinkedList<>();
        for (InputStream each : schemaStreams) {
            YamlShardingSphereTable metaData = new Yaml().loadAs(each, YamlShardingSphereTable.class);
            if (isSystemSchemaMetadataEnabled || KernelSupportedSystemTables.isSupportedSystemTable(schemaName, metaData.getName())) {
                tables.add(swapper.swapToObject(metaData));
            }
        }
        return new ShardingSphereSchema(schemaName, tables, Collections.emptyList());
    }
}

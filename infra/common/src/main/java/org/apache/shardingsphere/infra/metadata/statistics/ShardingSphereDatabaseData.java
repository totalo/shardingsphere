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

package org.apache.shardingsphere.infra.metadata.statistics;

import com.cedarsoftware.util.CaseInsensitiveMap;
import lombok.Getter;

import java.util.Map;

/**
 * ShardingSphere database data.
 */
@Getter
public final class ShardingSphereDatabaseData {
    
    private final Map<String, ShardingSphereSchemaData> schemaData = new CaseInsensitiveMap<>();
    
    /**
     * Judge whether to contains schema.
     *
     * @param schemaName schema name
     * @return contains schema or not
     */
    public boolean containsSchema(final String schemaName) {
        return schemaData.containsKey(schemaName);
    }
    
    /**
     * Get ShardingSphere schema data.
     *
     * @param schemaName schema name
     * @return schema data
     */
    public ShardingSphereSchemaData getSchema(final String schemaName) {
        return schemaData.get(schemaName);
    }
    
    /**
     * Put schema data.
     *
     * @param schemaName schema name
     * @param schema schema data
     */
    public void putSchema(final String schemaName, final ShardingSphereSchemaData schema) {
        schemaData.put(schemaName, schema);
    }
    
    /**
     * Remove schema data.
     *
     * @param schemaName schema name
     */
    public void removeSchema(final String schemaName) {
        schemaData.remove(schemaName);
    }
}

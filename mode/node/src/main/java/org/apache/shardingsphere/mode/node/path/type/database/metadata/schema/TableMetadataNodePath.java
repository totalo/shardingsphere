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

package org.apache.shardingsphere.mode.node.path.type.database.metadata.schema;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.shardingsphere.mode.node.path.NodePath;
import org.apache.shardingsphere.mode.node.path.NodePathEntity;
import org.apache.shardingsphere.mode.node.path.engine.searcher.NodePathPattern;
import org.apache.shardingsphere.mode.node.path.engine.searcher.NodePathSearchCriteria;

/**
 * Table metadata node path.
 */
@NodePathEntity("/metadata/${databaseName}/schemas/${schemaName}/tables/${tableName}")
@RequiredArgsConstructor
@Getter
public final class TableMetadataNodePath implements NodePath {
    
    private final String databaseName;
    
    private final String schemaName;
    
    private final String tableName;
    
    /**
     * Create database search criteria.
     *
     * @return created search criteria
     */
    public static NodePathSearchCriteria createDatabaseSearchCriteria() {
        return new NodePathSearchCriteria(new TableMetadataNodePath(NodePathPattern.IDENTIFIER, null, null), true, true, 1);
    }
    
    /**
     * Create schema search criteria.
     *
     * @param databaseName database name
     * @param containsChildPath contains child path
     * @return created search criteria
     */
    public static NodePathSearchCriteria createSchemaSearchCriteria(final String databaseName, final boolean containsChildPath) {
        return new NodePathSearchCriteria(new TableMetadataNodePath(databaseName, NodePathPattern.IDENTIFIER, null), true, containsChildPath, 1);
    }
    
    /**
     * Create table search criteria.
     *
     * @param databaseName database name
     * @param schemaName schema name
     * @return created search criteria
     */
    public static NodePathSearchCriteria createTableSearchCriteria(final String databaseName, final String schemaName) {
        return new NodePathSearchCriteria(new TableMetadataNodePath(databaseName, schemaName, NodePathPattern.IDENTIFIER), false, true, 1);
    }
}

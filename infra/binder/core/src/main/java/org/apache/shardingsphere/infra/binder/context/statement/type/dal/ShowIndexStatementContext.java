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

package org.apache.shardingsphere.infra.binder.context.statement.type.dal;

import lombok.Getter;
import org.apache.shardingsphere.infra.binder.context.segment.table.TablesContext;
import org.apache.shardingsphere.infra.binder.context.statement.CommonSQLStatementContext;
import org.apache.shardingsphere.infra.binder.context.type.RemoveAvailable;
import org.apache.shardingsphere.infra.binder.context.type.TableContextAvailable;
import org.apache.shardingsphere.infra.database.core.type.DatabaseType;
import org.apache.shardingsphere.sql.parser.statement.core.segment.SQLSegment;
import org.apache.shardingsphere.sql.parser.statement.core.statement.dal.ShowIndexStatement;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Show index statement context.
 */
@Getter
public final class ShowIndexStatementContext extends CommonSQLStatementContext implements TableContextAvailable, RemoveAvailable {
    
    private final TablesContext tablesContext;
    
    public ShowIndexStatementContext(final DatabaseType databaseType, final ShowIndexStatement sqlStatement) {
        super(databaseType, sqlStatement);
        tablesContext = new TablesContext(sqlStatement.getTable());
    }
    
    @Override
    public ShowIndexStatement getSqlStatement() {
        return (ShowIndexStatement) super.getSqlStatement();
    }
    
    @Override
    public Collection<SQLSegment> getRemoveSegments() {
        Collection<SQLSegment> result = new LinkedList<>();
        getSqlStatement().getFromDatabase().ifPresent(result::add);
        return result;
    }
}

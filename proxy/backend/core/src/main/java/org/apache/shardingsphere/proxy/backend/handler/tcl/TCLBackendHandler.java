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

package org.apache.shardingsphere.proxy.backend.handler.tcl;

import org.apache.shardingsphere.infra.database.core.metadata.database.metadata.DialectDatabaseMetaData;
import org.apache.shardingsphere.infra.database.core.type.DatabaseTypeRegistry;
import org.apache.shardingsphere.infra.exception.core.ShardingSpherePreconditions;
import org.apache.shardingsphere.infra.exception.dialect.exception.transaction.InTransactionException;
import org.apache.shardingsphere.proxy.backend.connector.TransactionManager;
import org.apache.shardingsphere.proxy.backend.connector.jdbc.transaction.BackendTransactionManager;
import org.apache.shardingsphere.proxy.backend.handler.ProxyBackendHandler;
import org.apache.shardingsphere.proxy.backend.response.header.ResponseHeader;
import org.apache.shardingsphere.proxy.backend.response.header.update.UpdateResponseHeader;
import org.apache.shardingsphere.proxy.backend.session.ConnectionSession;
import org.apache.shardingsphere.sql.parser.statement.core.statement.SQLStatement;
import org.apache.shardingsphere.sql.parser.statement.core.statement.tcl.ReleaseSavepointStatement;
import org.apache.shardingsphere.sql.parser.statement.core.statement.tcl.RollbackStatement;
import org.apache.shardingsphere.sql.parser.statement.core.statement.tcl.SavepointStatement;
import org.apache.shardingsphere.sql.parser.statement.core.statement.tcl.SetAutoCommitStatement;
import org.apache.shardingsphere.sql.parser.statement.core.statement.tcl.TCLStatement;
import org.apache.shardingsphere.sql.parser.statement.mysql.tcl.MySQLSetAutoCommitStatement;
import org.apache.shardingsphere.sql.parser.statement.opengauss.tcl.OpenGaussCommitStatement;
import org.apache.shardingsphere.sql.parser.statement.opengauss.tcl.OpenGaussRollbackStatement;
import org.apache.shardingsphere.sql.parser.statement.postgresql.tcl.PostgreSQLCommitStatement;
import org.apache.shardingsphere.sql.parser.statement.postgresql.tcl.PostgreSQLRollbackStatement;
import org.apache.shardingsphere.transaction.core.TransactionOperationType;

import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

/**
 * TCL backend handler.
 */
public final class TCLBackendHandler implements ProxyBackendHandler {
    
    private final TCLStatement tclStatement;
    
    private final TransactionOperationType operationType;
    
    private final ConnectionSession connectionSession;
    
    private final DialectDatabaseMetaData dialectDatabaseMetaData;
    
    private final TransactionManager backendTransactionManager;
    
    public TCLBackendHandler(final TCLStatement tclStatement, final TransactionOperationType operationType, final ConnectionSession connectionSession) {
        this.tclStatement = tclStatement;
        this.operationType = operationType;
        this.connectionSession = connectionSession;
        dialectDatabaseMetaData = new DatabaseTypeRegistry(connectionSession.getProtocolType()).getDialectDatabaseMetaData();
        backendTransactionManager = new BackendTransactionManager(connectionSession.getDatabaseConnectionManager());
    }
    
    @Override
    public ResponseHeader execute() throws SQLException {
        switch (operationType) {
            case BEGIN:
                handleBegin();
                break;
            case SAVEPOINT:
                handleSavepoint();
                break;
            case ROLLBACK_TO_SAVEPOINT:
                handleRollbackToSavepoint();
                break;
            case RELEASE_SAVEPOINT:
                handleReleaseSavepoint();
                break;
            case COMMIT:
                SQLStatement sqlStatement = getSQLStatementByCommit();
                backendTransactionManager.commit();
                return new UpdateResponseHeader(sqlStatement);
            case ROLLBACK:
                backendTransactionManager.rollback();
                break;
            case SET_AUTOCOMMIT:
                handleSetAutoCommit();
                break;
            default:
                throw new SQLFeatureNotSupportedException(operationType.name());
        }
        return new UpdateResponseHeader(tclStatement);
    }
    
    private void handleBegin() throws SQLException {
        if (connectionSession.getTransactionStatus().isInTransaction()) {
            if (dialectDatabaseMetaData.getTransactionOption().isSupportAutoCommitInNestedTransaction()) {
                backendTransactionManager.commit();
            } else if (dialectDatabaseMetaData.getSchemaOption().getDefaultSchema().isPresent()) {
                throw new InTransactionException();
            }
        }
        backendTransactionManager.begin();
    }
    
    private void handleSavepoint() throws SQLException {
        ShardingSpherePreconditions.checkState(connectionSession.getTransactionStatus().isInTransaction() || !dialectDatabaseMetaData.getSchemaOption().getDefaultSchema().isPresent(),
                () -> new SQLFeatureNotSupportedException("SAVEPOINT can only be used in transaction blocks"));
        backendTransactionManager.setSavepoint(((SavepointStatement) tclStatement).getSavepointName());
    }
    
    private void handleRollbackToSavepoint() throws SQLException {
        ShardingSpherePreconditions.checkState(connectionSession.getTransactionStatus().isInTransaction() || !dialectDatabaseMetaData.getSchemaOption().getDefaultSchema().isPresent(),
                () -> new SQLFeatureNotSupportedException("ROLLBACK TO SAVEPOINT can only be used in transaction blocks"));
        backendTransactionManager.rollbackTo(((RollbackStatement) tclStatement).getSavepointName().get());
    }
    
    private void handleReleaseSavepoint() throws SQLException {
        ShardingSpherePreconditions.checkState(connectionSession.getTransactionStatus().isInTransaction() || !dialectDatabaseMetaData.getSchemaOption().getDefaultSchema().isPresent(),
                () -> new SQLFeatureNotSupportedException("RELEASE SAVEPOINT can only be used in transaction blocks"));
        backendTransactionManager.releaseSavepoint(((ReleaseSavepointStatement) tclStatement).getSavepointName());
    }
    
    private SQLStatement getSQLStatementByCommit() {
        SQLStatement result = tclStatement;
        if (connectionSession.getConnectionContext().getTransactionContext().isExceptionOccur()) {
            if (tclStatement instanceof OpenGaussCommitStatement) {
                result = new OpenGaussRollbackStatement();
            } else if (tclStatement instanceof PostgreSQLCommitStatement) {
                result = new PostgreSQLRollbackStatement();
            }
        }
        return result;
    }
    
    private void handleSetAutoCommit() throws SQLException {
        if (tclStatement instanceof MySQLSetAutoCommitStatement) {
            handleMySQLSetAutoCommit();
        }
        connectionSession.setAutoCommit(((SetAutoCommitStatement) tclStatement).isAutoCommit());
    }
    
    private void handleMySQLSetAutoCommit() throws SQLException {
        MySQLSetAutoCommitStatement statement = (MySQLSetAutoCommitStatement) tclStatement;
        if (statement.isAutoCommit() && connectionSession.getTransactionStatus().isInTransaction()) {
            backendTransactionManager.commit();
        }
    }
}

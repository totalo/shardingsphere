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

package org.apache.shardingsphere.test.e2e.engine.type.sql.dml;

import org.apache.shardingsphere.test.e2e.cases.value.SQLValue;
import org.apache.shardingsphere.test.e2e.engine.arg.E2ETestCaseArgumentsProvider;
import org.apache.shardingsphere.test.e2e.engine.arg.E2ETestCaseSettings;
import org.apache.shardingsphere.test.e2e.engine.context.E2ETestContext;
import org.apache.shardingsphere.test.e2e.env.runtime.E2ETestEnvironment;
import org.apache.shardingsphere.test.e2e.engine.framework.param.array.E2ETestParameterFactory;
import org.apache.shardingsphere.test.e2e.engine.framework.param.model.AssertionTestParameter;
import org.apache.shardingsphere.test.e2e.engine.framework.type.SQLCommandType;
import org.apache.shardingsphere.test.e2e.engine.framework.type.SQLExecuteType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertFalse;

@E2ETestCaseSettings(SQLCommandType.DML)
class AdditionalDMLE2EIT extends BaseDMLE2EIT {
    
    @ParameterizedTest(name = "{0}")
    @EnabledIf("isEnabled")
    @ArgumentsSource(E2ETestCaseArgumentsProvider.class)
    void assertExecuteUpdateWithAutoGeneratedKeys(final AssertionTestParameter testParam) throws SQLException, JAXBException, IOException {
        // TODO make sure test case can not be null
        if (null == testParam.getTestCaseContext()) {
            return;
        }
        if (isPostgreSQLOrOpenGauss(testParam.getDatabaseType().getType()) || isOracleInsertStatement(testParam.getDatabaseType().getType(), testParam.getTestCaseContext().getTestCase().getSql())) {
            return;
        }
        E2ETestContext context = new E2ETestContext(testParam);
        init(testParam);
        int actualUpdateCount;
        try (Connection connection = getEnvironmentEngine().getTargetDataSource().getConnection()) {
            actualUpdateCount = SQLExecuteType.LITERAL == context.getSqlExecuteType()
                    ? executeUpdateForStatementWithAutoGeneratedKeys(testParam, context, connection)
                    : executeUpdateForPreparedStatementWithAutoGeneratedKeys(testParam, context, connection);
        }
        assertDataSet(context, actualUpdateCount, testParam);
    }
    
    // TODO support oracle insert statement return auto generated keys
    private boolean isOracleInsertStatement(final String databaseType, final String sql) {
        return "Oracle".equals(databaseType) && sql.toUpperCase().startsWith("INSERT");
    }
    
    private boolean isPostgreSQLOrOpenGauss(final String databaseType) {
        return "PostgreSQL".equals(databaseType) || "openGauss".equals(databaseType);
    }
    
    private int executeUpdateForStatementWithAutoGeneratedKeys(final AssertionTestParameter testParam,
                                                               final E2ETestContext context, final Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            int result = statement.executeUpdate(context.getSQL(), Statement.RETURN_GENERATED_KEYS);
            assertGeneratedKeys(testParam, statement.getGeneratedKeys(), testParam.getDatabaseType());
            return result;
        }
    }
    
    private int executeUpdateForPreparedStatementWithAutoGeneratedKeys(final AssertionTestParameter testParam,
                                                                       final E2ETestContext context, final Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(context.getSQL(), Statement.RETURN_GENERATED_KEYS)) {
            for (SQLValue each : context.getAssertion().getSQLValues()) {
                preparedStatement.setObject(each.getIndex(), each.getValue());
            }
            int result = preparedStatement.executeUpdate();
            assertGeneratedKeys(testParam, preparedStatement.getGeneratedKeys(), testParam.getDatabaseType());
            return result;
        }
    }
    
    // TODO
    @Disabled("support execute update with column indexes in #23626")
    @ParameterizedTest(name = "{0}")
    @EnabledIf("isEnabled")
    @ArgumentsSource(E2ETestCaseArgumentsProvider.class)
    void assertExecuteUpdateWithColumnIndexes(final AssertionTestParameter testParam) throws SQLException, JAXBException, IOException {
        if (isPostgreSQLOrOpenGauss(testParam.getDatabaseType().getType())) {
            return;
        }
        E2ETestContext context = new E2ETestContext(testParam);
        init(testParam);
        int actualUpdateCount;
        try (Connection connection = getEnvironmentEngine().getTargetDataSource().getConnection()) {
            actualUpdateCount = SQLExecuteType.LITERAL == context.getSqlExecuteType()
                    ? executeUpdateForStatementWithColumnIndexes(context, connection)
                    : executeUpdateForPreparedStatementWithColumnIndexes(context, connection);
        }
        assertDataSet(context, actualUpdateCount, testParam);
    }
    
    private int executeUpdateForStatementWithColumnIndexes(final E2ETestContext context, final Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            return statement.executeUpdate(context.getSQL(), new int[]{1});
        }
    }
    
    private int executeUpdateForPreparedStatementWithColumnIndexes(final E2ETestContext context, final Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(context.getSQL(), new int[]{1})) {
            for (SQLValue each : context.getAssertion().getSQLValues()) {
                preparedStatement.setObject(each.getIndex(), each.getValue());
            }
            return preparedStatement.executeUpdate();
        }
    }
    
    // TODO
    @Disabled("support execute update with column names in #23626")
    @ParameterizedTest(name = "{0}")
    @EnabledIf("isEnabled")
    @ArgumentsSource(E2ETestCaseArgumentsProvider.class)
    void assertExecuteUpdateWithColumnNames(final AssertionTestParameter testParam) throws SQLException, JAXBException, IOException {
        if (isPostgreSQLOrOpenGauss(testParam.getDatabaseType().getType())) {
            return;
        }
        E2ETestContext context = new E2ETestContext(testParam);
        init(testParam);
        int actualUpdateCount;
        try (Connection connection = getEnvironmentEngine().getTargetDataSource().getConnection()) {
            actualUpdateCount = SQLExecuteType.LITERAL == context.getSqlExecuteType()
                    ? executeUpdateForStatementWithColumnNames(context, connection)
                    : executeUpdateForPreparedStatementWithColumnNames(context, connection);
        }
        assertDataSet(context, actualUpdateCount, testParam);
    }
    
    private int executeUpdateForStatementWithColumnNames(final E2ETestContext context, final Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            return statement.executeUpdate(context.getSQL());
        }
    }
    
    private int executeUpdateForPreparedStatementWithColumnNames(final E2ETestContext context, final Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(context.getSQL(), new String[]{"TODO"})) {
            for (SQLValue each : context.getAssertion().getSQLValues()) {
                preparedStatement.setObject(each.getIndex(), each.getValue());
            }
            return preparedStatement.executeUpdate();
        }
    }
    
    @ParameterizedTest(name = "{0}")
    @EnabledIf("isEnabled")
    @ArgumentsSource(E2ETestCaseArgumentsProvider.class)
    void assertExecuteWithoutAutoGeneratedKeys(final AssertionTestParameter testParam) throws SQLException, JAXBException, IOException {
        // TODO make sure test case can not be null
        if (null == testParam.getTestCaseContext()) {
            return;
        }
        if (isPostgreSQLOrOpenGauss(testParam.getDatabaseType().getType())) {
            return;
        }
        E2ETestContext context = new E2ETestContext(testParam);
        init(testParam);
        int actualUpdateCount;
        try (Connection connection = getEnvironmentEngine().getTargetDataSource().getConnection()) {
            actualUpdateCount = SQLExecuteType.LITERAL == context.getSqlExecuteType()
                    ? executeForStatementWithoutAutoGeneratedKeys(context, connection)
                    : executeForPreparedStatementWithoutAutoGeneratedKeys(context, connection);
        }
        assertDataSet(context, actualUpdateCount, testParam);
    }
    
    private int executeForStatementWithoutAutoGeneratedKeys(final E2ETestContext context, final Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            assertFalse(statement.execute(context.getSQL(), Statement.NO_GENERATED_KEYS), "Not a DML statement.");
            return statement.getUpdateCount();
        }
    }
    
    private int executeForPreparedStatementWithoutAutoGeneratedKeys(final E2ETestContext context, final Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(context.getSQL(), Statement.NO_GENERATED_KEYS)) {
            for (SQLValue each : context.getAssertion().getSQLValues()) {
                preparedStatement.setObject(each.getIndex(), each.getValue());
            }
            assertFalse(preparedStatement.execute(), "Not a DML statement.");
            return preparedStatement.getUpdateCount();
        }
    }
    
    @ParameterizedTest(name = "{0}")
    @EnabledIf("isEnabled")
    @ArgumentsSource(E2ETestCaseArgumentsProvider.class)
    void assertExecuteWithAutoGeneratedKeys(final AssertionTestParameter testParam) throws SQLException, JAXBException, IOException {
        // TODO make sure test case can not be null
        if (null == testParam.getTestCaseContext()) {
            return;
        }
        if (isPostgreSQLOrOpenGauss(testParam.getDatabaseType().getType()) || isOracleInsertStatement(testParam.getDatabaseType().getType(), testParam.getTestCaseContext().getTestCase().getSql())) {
            return;
        }
        E2ETestContext context = new E2ETestContext(testParam);
        init(testParam);
        int actualUpdateCount;
        try (Connection connection = getEnvironmentEngine().getTargetDataSource().getConnection()) {
            actualUpdateCount = SQLExecuteType.LITERAL == context.getSqlExecuteType()
                    ? executeForStatementWithAutoGeneratedKeys(testParam, context, connection)
                    : executeForPreparedStatementWithAutoGeneratedKeys(testParam, context, connection);
        }
        assertDataSet(context, actualUpdateCount, testParam);
    }
    
    private int executeForStatementWithAutoGeneratedKeys(final AssertionTestParameter testParam,
                                                         final E2ETestContext context, final Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            assertFalse(statement.execute(context.getSQL(), Statement.RETURN_GENERATED_KEYS), "Not a DML statement.");
            assertGeneratedKeys(testParam, statement.getGeneratedKeys(), testParam.getDatabaseType());
            return statement.getUpdateCount();
        }
    }
    
    private int executeForPreparedStatementWithAutoGeneratedKeys(final AssertionTestParameter testParam,
                                                                 final E2ETestContext context, final Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(context.getSQL(), Statement.RETURN_GENERATED_KEYS)) {
            for (SQLValue each : context.getAssertion().getSQLValues()) {
                preparedStatement.setObject(each.getIndex(), each.getValue());
            }
            assertFalse(preparedStatement.execute(), "Not a DML statement.");
            assertGeneratedKeys(testParam, preparedStatement.getGeneratedKeys(), testParam.getDatabaseType());
            return preparedStatement.getUpdateCount();
        }
    }
    
    // TODO
    @Disabled("support execute with column indexes in #23626")
    @ParameterizedTest(name = "{0}")
    @EnabledIf("isEnabled")
    @ArgumentsSource(E2ETestCaseArgumentsProvider.class)
    void assertExecuteWithColumnIndexes(final AssertionTestParameter testParam) throws SQLException, JAXBException, IOException {
        if (isPostgreSQLOrOpenGauss(testParam.getDatabaseType().getType())) {
            return;
        }
        E2ETestContext context = new E2ETestContext(testParam);
        init(testParam);
        int actualUpdateCount;
        try (Connection connection = getEnvironmentEngine().getTargetDataSource().getConnection()) {
            actualUpdateCount = SQLExecuteType.LITERAL == context.getSqlExecuteType()
                    ? executeForStatementWithColumnIndexes(context, connection)
                    : executeForPreparedStatementWithColumnIndexes(context, connection);
        }
        assertDataSet(context, actualUpdateCount, testParam);
    }
    
    private int executeForStatementWithColumnIndexes(final E2ETestContext context, final Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            assertFalse(statement.execute(context.getSQL(), new int[]{1}), "Not a DML statement.");
            return statement.getUpdateCount();
        }
    }
    
    private int executeForPreparedStatementWithColumnIndexes(final E2ETestContext context, final Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(context.getSQL(), new int[]{1})) {
            for (SQLValue each : context.getAssertion().getSQLValues()) {
                preparedStatement.setObject(each.getIndex(), each.getValue());
            }
            assertFalse(preparedStatement.execute(), "Not a DML statement.");
            return preparedStatement.getUpdateCount();
        }
    }
    
    // TODO
    @Disabled("support execute with column names in #23626")
    @ParameterizedTest(name = "{0}")
    @EnabledIf("isEnabled")
    @ArgumentsSource(E2ETestCaseArgumentsProvider.class)
    void assertExecuteWithColumnNames(final AssertionTestParameter testParam) throws SQLException, JAXBException, IOException {
        if (isPostgreSQLOrOpenGauss(testParam.getDatabaseType().getType())) {
            return;
        }
        E2ETestContext context = new E2ETestContext(testParam);
        init(testParam);
        int actualUpdateCount;
        try (Connection connection = getEnvironmentEngine().getTargetDataSource().getConnection()) {
            actualUpdateCount = SQLExecuteType.LITERAL == context.getSqlExecuteType()
                    ? executeForStatementWithColumnNames(context, connection)
                    : executeForPreparedStatementWithColumnNames(context, connection);
        }
        assertDataSet(context, actualUpdateCount, testParam);
    }
    
    private int executeForStatementWithColumnNames(final E2ETestContext context, final Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            assertFalse(statement.execute(context.getSQL(), new String[]{"TODO"}), "Not a DML statement.");
            return statement.getUpdateCount();
        }
    }
    
    private int executeForPreparedStatementWithColumnNames(final E2ETestContext context, final Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(context.getSQL(), new String[]{"TODO"})) {
            for (SQLValue each : context.getAssertion().getSQLValues()) {
                preparedStatement.setObject(each.getIndex(), each.getValue());
            }
            assertFalse(preparedStatement.execute(), "Not a DML statement.");
            return preparedStatement.getUpdateCount();
        }
    }
    
    private static boolean isEnabled() {
        return E2ETestParameterFactory.containsTestParameter() && E2ETestEnvironment.getInstance().isRunAdditionalTestCases();
    }
    
    @Override
    @AfterEach
    void tearDown() {
        super.tearDown();
    }
}

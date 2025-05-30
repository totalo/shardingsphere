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

package org.apache.shardingsphere.test.e2e.engine.context;

import lombok.Getter;
import org.apache.shardingsphere.test.e2e.cases.casse.assertion.E2ETestCaseAssertion;
import org.apache.shardingsphere.test.e2e.cases.dataset.DataSet;
import org.apache.shardingsphere.test.e2e.cases.dataset.DataSetLoader;
import org.apache.shardingsphere.test.e2e.cases.value.SQLValue;
import org.apache.shardingsphere.test.e2e.engine.framework.param.model.AssertionTestParameter;
import org.apache.shardingsphere.test.e2e.engine.framework.type.SQLExecuteType;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * E2E test context.
 */
@Getter
public final class E2ETestContext {
    
    public static final String NOT_VERIFY_FLAG = "NOT_VERIFY";
    
    private final String sql;
    
    private final SQLExecuteType sqlExecuteType;
    
    private final E2ETestCaseAssertion assertion;
    
    private final DataSet dataSet;
    
    public E2ETestContext(final AssertionTestParameter testParam) {
        sql = testParam.getTestCaseContext().getTestCase().getSql();
        sqlExecuteType = testParam.getSqlExecuteType();
        assertion = testParam.getAssertion();
        dataSet = null == assertion || null == assertion.getExpectedDataFile()
                ? null
                : DataSetLoader.load(testParam.getTestCaseContext().getParentPath(), testParam.getScenario(), testParam.getDatabaseType(), testParam.getMode(), assertion.getExpectedDataFile());
    }
    
    /**
     * Get SQL.
     *
     * @return SQL
     */
    public String getSQL() {
        return sqlExecuteType == SQLExecuteType.LITERAL ? getLiteralSQL(sql) : sql;
    }
    
    private String getLiteralSQL(final String sql) {
        List<Object> params = null == assertion ? Collections.emptyList() : assertion.getSQLValues().stream().map(SQLValue::toString).collect(Collectors.toList());
        return params.isEmpty() ? sql : String.format(sql.replace("%", "ÿ").replace("?", "%s"), params.toArray()).replace("ÿ", "%").replace("%%", "%").replace("'%'", "'%%'");
    }
}

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

package org.apache.shardingsphere.test.it.sql.parser.internal.asserts.segment.insert;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.shardingsphere.sql.parser.statement.core.segment.dml.assignment.ColumnAssignmentSegment;
import org.apache.shardingsphere.sql.parser.statement.core.segment.dml.column.OnDuplicateKeyColumnsSegment;
import org.apache.shardingsphere.test.it.sql.parser.internal.asserts.SQLCaseAssertContext;
import org.apache.shardingsphere.test.it.sql.parser.internal.asserts.segment.SQLSegmentAssert;
import org.apache.shardingsphere.test.it.sql.parser.internal.asserts.segment.assignment.AssignmentAssert;
import org.apache.shardingsphere.test.it.sql.parser.internal.cases.parser.jaxb.segment.impl.insert.ExpectedOnDuplicateKeyColumns;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * On duplicate key columns assert.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OnDuplicateKeyColumnsAssert {
    
    /**
     * Assert actual on duplicate key columns segment is correct with expected on duplicate key columns.
     *
     * @param assertContext assert context
     * @param actual actual on duplicate key columns segment
     * @param expected expected on duplicate key columns
     */
    public static void assertIs(final SQLCaseAssertContext assertContext, final OnDuplicateKeyColumnsSegment actual, final ExpectedOnDuplicateKeyColumns expected) {
        assertNotNull(expected, assertContext.getText("On duplicate key columns should exist."));
        assertThat(assertContext.getText("On duplicate key columns size assertion error: "), actual.getColumns().size(), is(expected.getAssignments().size()));
        int count = 0;
        for (ColumnAssignmentSegment each : actual.getColumns()) {
            AssignmentAssert.assertIs(assertContext, each, expected.getAssignments().get(count));
            count++;
        }
        SQLSegmentAssert.assertIs(assertContext, actual, expected);
    }
}

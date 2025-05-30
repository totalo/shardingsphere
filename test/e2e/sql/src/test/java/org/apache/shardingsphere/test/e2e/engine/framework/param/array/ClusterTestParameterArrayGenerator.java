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

package org.apache.shardingsphere.test.e2e.engine.framework.param.array;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.shardingsphere.test.e2e.engine.framework.type.SQLCommandType;
import org.apache.shardingsphere.test.e2e.env.container.atomic.enums.AdapterMode;
import org.apache.shardingsphere.test.e2e.env.runtime.E2ETestEnvironment;
import org.apache.shardingsphere.test.e2e.engine.framework.param.model.AssertionTestParameter;
import org.apache.shardingsphere.test.e2e.engine.framework.param.model.E2ETestParameter;

import java.util.Collection;

/**
 * Test parameter generator for cluster mode.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClusterTestParameterArrayGenerator {
    
    private static final E2ETestEnvironment ENV = E2ETestEnvironment.getInstance();
    
    /**
     * Get assertion test parameter.
     *
     * @param sqlCommandType SQL command type
     * @return assertion test parameter
     */
    public static Collection<AssertionTestParameter> getAssertionTestParameter(final SQLCommandType sqlCommandType) {
        return new E2ETestParameterGenerator(ENV.getClusterEnvironment().getAdapters(), ENV.getScenarios(), AdapterMode.CLUSTER.getValue(),
                ENV.getClusterEnvironment().getDatabaseTypes(), ENV.isSmoke()).getAssertionTestParameter(sqlCommandType);
    }
    
    /**
     * Get case test parameter.
     *
     * @param sqlCommandType SQL command type
     * @return case parameter
     */
    public static Collection<E2ETestParameter> getCaseTestParameter(final SQLCommandType sqlCommandType) {
        return new E2ETestParameterGenerator(ENV.getClusterEnvironment().getAdapters(), ENV.getScenarios(), AdapterMode.CLUSTER.getValue(),
                ENV.getClusterEnvironment().getDatabaseTypes(), ENV.isSmoke()).getCaseTestParameter(sqlCommandType);
    }
}

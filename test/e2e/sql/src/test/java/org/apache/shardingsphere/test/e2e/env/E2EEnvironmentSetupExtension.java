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

package org.apache.shardingsphere.test.e2e.env;

import org.apache.shardingsphere.test.e2e.env.container.atomic.enums.AdapterMode;
import org.apache.shardingsphere.test.e2e.env.container.atomic.enums.AdapterType;
import org.apache.shardingsphere.test.e2e.engine.framework.param.model.E2ETestParameter;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;

import java.lang.reflect.Method;

/**
 * E2E environment setup extension.
 */
public final class E2EEnvironmentSetupExtension implements InvocationInterceptor {
    
    @Override
    public void interceptTestTemplateMethod(final Invocation<Void> invocation, final ReflectiveInvocationContext<Method> invocationContext, final ExtensionContext extensionContext) throws Throwable {
        for (Object each : invocationContext.getArguments()) {
            if (each instanceof E2ETestParameter) {
                E2ETestParameter testParameter = (E2ETestParameter) each;
                // TODO make sure test case can not be null
                if (null == testParameter.getTestCaseContext()) {
                    break;
                }
                setEnvironmentEngine(extensionContext, testParameter);
            }
        }
        invocation.proceed();
    }
    
    private void setEnvironmentEngine(final ExtensionContext extensionContext, final E2ETestParameter testParam) {
        E2EEnvironmentEngine environmentEngine = new E2EEnvironmentEngine(testParam.getKey(), testParam.getScenario(), testParam.getDatabaseType(),
                AdapterMode.valueOf(testParam.getMode().toUpperCase()), AdapterType.valueOf(testParam.getAdapter().toUpperCase()));
        ((E2EEnvironmentAware) extensionContext.getRequiredTestInstance()).setEnvironmentEngine(environmentEngine);
    }
}

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

package org.apache.shardingsphere.globalclock.type.tso.provider.redis;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.shardingsphere.infra.props.TypedPropertyKey;

/**
 * Property key of redis timestamp oracle provider.
 */
@RequiredArgsConstructor
@Getter
public enum RedisTSOPropertyKey implements TypedPropertyKey {
    
    HOST("host", "127.0.0.1", String.class),
    
    PORT("port", "6379", int.class),
    
    PASSWORD("password", null, String.class),
    
    TIMEOUT_INTERVAL("timeoutInterval", "40000", int.class),
    
    MAX_IDLE("maxIdle", "8", int.class),
    
    MAX_TOTAL("maxTotal", "18", int.class);
    
    private final String key;
    
    private final String defaultValue;
    
    private final Class<?> type;
}

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

package org.apache.shardingsphere.sql.parser.statement.core.statement;

import com.cedarsoftware.util.CaseInsensitiveSet;
import lombok.Getter;
import org.apache.shardingsphere.sql.parser.statement.core.segment.generic.CommentSegment;
import org.apache.shardingsphere.sql.parser.statement.core.segment.generic.ParameterMarkerSegment;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;

/**
 * SQL statement abstract class.
 */
@Getter
public class AbstractSQLStatement implements SQLStatement {
    
    private final Collection<Integer> uniqueParameterIndexes = new LinkedHashSet<>();
    
    private final Collection<ParameterMarkerSegment> parameterMarkers = new LinkedList<>();
    
    private final Collection<String> variableNames = new CaseInsensitiveSet<>();
    
    private final Collection<CommentSegment> comments = new LinkedList<>();
    
    @Override
    public final int getParameterCount() {
        return uniqueParameterIndexes.size();
    }
    
    @Override
    public final void addParameterMarkers(final Collection<ParameterMarkerSegment> segments) {
        for (ParameterMarkerSegment each : segments) {
            parameterMarkers.add(each);
            uniqueParameterIndexes.add(each.getParameterIndex());
        }
    }
}

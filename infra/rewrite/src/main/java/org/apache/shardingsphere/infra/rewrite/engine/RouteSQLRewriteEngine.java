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

package org.apache.shardingsphere.infra.rewrite.engine;

import lombok.RequiredArgsConstructor;
import org.apache.shardingsphere.infra.annotation.HighFrequencyInvocation;
import org.apache.shardingsphere.infra.binder.context.statement.SQLStatementContext;
import org.apache.shardingsphere.infra.binder.context.statement.type.dml.SelectStatementContext;
import org.apache.shardingsphere.infra.database.core.type.DatabaseType;
import org.apache.shardingsphere.infra.datanode.DataNode;
import org.apache.shardingsphere.infra.metadata.database.ShardingSphereDatabase;
import org.apache.shardingsphere.infra.metadata.database.resource.unit.StorageUnit;
import org.apache.shardingsphere.infra.metadata.database.rule.RuleMetaData;
import org.apache.shardingsphere.infra.rewrite.context.SQLRewriteContext;
import org.apache.shardingsphere.infra.rewrite.engine.result.RouteSQLRewriteResult;
import org.apache.shardingsphere.infra.rewrite.engine.result.SQLRewriteUnit;
import org.apache.shardingsphere.infra.rewrite.parameter.builder.ParameterBuilder;
import org.apache.shardingsphere.infra.rewrite.parameter.builder.impl.GroupedParameterBuilder;
import org.apache.shardingsphere.infra.rewrite.parameter.builder.impl.StandardParameterBuilder;
import org.apache.shardingsphere.infra.rewrite.sql.impl.RouteSQLBuilder;
import org.apache.shardingsphere.infra.route.context.RouteContext;
import org.apache.shardingsphere.infra.route.context.RouteUnit;
import org.apache.shardingsphere.infra.session.query.QueryContext;
import org.apache.shardingsphere.sql.parser.statement.core.util.SQLUtils;
import org.apache.shardingsphere.sqltranslator.context.SQLTranslatorContext;
import org.apache.shardingsphere.sqltranslator.rule.SQLTranslatorRule;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Route SQL rewrite engine.
 */
@HighFrequencyInvocation
@RequiredArgsConstructor
public final class RouteSQLRewriteEngine {
    
    private final SQLTranslatorRule translatorRule;
    
    private final ShardingSphereDatabase database;
    
    private final RuleMetaData globalRuleMetaData;
    
    /**
     * Rewrite SQL and parameters.
     *
     * @param sqlRewriteContext SQL rewrite context
     * @param routeContext route context
     * @param queryContext query context
     * @return SQL rewrite result
     */
    public RouteSQLRewriteResult rewrite(final SQLRewriteContext sqlRewriteContext, final RouteContext routeContext, final QueryContext queryContext) {
        return new RouteSQLRewriteResult(translate(queryContext, createSQLRewriteUnits(sqlRewriteContext, routeContext)));
    }
    
    private Map<RouteUnit, SQLRewriteUnit> createSQLRewriteUnits(final SQLRewriteContext sqlRewriteContext, final RouteContext routeContext) {
        Map<RouteUnit, SQLRewriteUnit> result = new LinkedHashMap<>(routeContext.getRouteUnits().size(), 1F);
        for (Entry<String, Collection<RouteUnit>> entry : aggregateRouteUnitGroups(routeContext.getRouteUnits()).entrySet()) {
            Collection<RouteUnit> routeUnits = entry.getValue();
            if (isNeedAggregateRewrite(sqlRewriteContext.getSqlStatementContext(), routeUnits)) {
                result.put(routeUnits.iterator().next(), createSQLRewriteUnit(sqlRewriteContext, routeContext, routeUnits));
            } else {
                for (RouteUnit each : routeUnits) {
                    result.put(each, createSQLRewriteUnit(sqlRewriteContext, routeContext, each));
                }
            }
        }
        return result;
    }
    
    private Map<String, Collection<RouteUnit>> aggregateRouteUnitGroups(final Collection<RouteUnit> routeUnits) {
        Map<String, Collection<RouteUnit>> result = new LinkedHashMap<>(routeUnits.size(), 1F);
        for (RouteUnit each : routeUnits) {
            result.computeIfAbsent(each.getDataSourceMapper().getActualName(), unused -> new LinkedList<>()).add(each);
        }
        return result;
    }
    
    private boolean isNeedAggregateRewrite(final SQLStatementContext sqlStatementContext, final Collection<RouteUnit> routeUnits) {
        if (!(sqlStatementContext instanceof SelectStatementContext) || 1 == routeUnits.size()) {
            return false;
        }
        SelectStatementContext statementContext = (SelectStatementContext) sqlStatementContext;
        boolean containsSubqueryJoinQuery = statementContext.isContainsSubquery() || statementContext.isContainsJoinQuery();
        boolean containsOrderByLimitClause = !statementContext.getOrderByContext().getItems().isEmpty() || statementContext.getPaginationContext().isHasPagination();
        boolean containsLockClause = statementContext.getSqlStatement().getLock().isPresent();
        boolean result = !containsSubqueryJoinQuery && !containsOrderByLimitClause && !containsLockClause;
        statementContext.setNeedAggregateRewrite(result);
        return result;
    }
    
    private SQLRewriteUnit createSQLRewriteUnit(final SQLRewriteContext sqlRewriteContext, final RouteContext routeContext, final Collection<RouteUnit> routeUnits) {
        Collection<String> sql = new LinkedList<>();
        List<Object> params = new LinkedList<>();
        boolean containsDollarMarker = sqlRewriteContext.getSqlStatementContext() instanceof SelectStatementContext
                && ((SelectStatementContext) (sqlRewriteContext.getSqlStatementContext())).isContainsDollarParameterMarker();
        for (RouteUnit each : routeUnits) {
            sql.add(SQLUtils.trimSemicolon(new RouteSQLBuilder(sqlRewriteContext.getSql(), sqlRewriteContext.getSqlTokens(), each).toSQL()));
            if (containsDollarMarker && !params.isEmpty()) {
                continue;
            }
            params.addAll(getParameters(sqlRewriteContext, routeContext, each));
        }
        return new SQLRewriteUnit(String.join(" UNION ALL ", sql), params);
    }
    
    private SQLRewriteUnit createSQLRewriteUnit(final SQLRewriteContext sqlRewriteContext, final RouteContext routeContext, final RouteUnit routeUnit) {
        return new SQLRewriteUnit(getActualSQL(sqlRewriteContext, routeUnit), getParameters(sqlRewriteContext, routeContext, routeUnit));
    }
    
    private String getActualSQL(final SQLRewriteContext sqlRewriteContext, final RouteUnit routeUnit) {
        return new RouteSQLBuilder(sqlRewriteContext.getSql(), sqlRewriteContext.getSqlTokens(), routeUnit).toSQL();
    }
    
    private List<Object> getParameters(final SQLRewriteContext sqlRewriteContext, final RouteContext routeContext, final RouteUnit routeUnit) {
        ParameterBuilder parameterBuilder = sqlRewriteContext.getParameterBuilder();
        if (parameterBuilder instanceof StandardParameterBuilder) {
            return parameterBuilder.getParameters();
        }
        return routeContext.getOriginalDataNodes().isEmpty()
                ? ((GroupedParameterBuilder) parameterBuilder).getParameters()
                : buildRouteParameters((GroupedParameterBuilder) parameterBuilder, routeContext, routeUnit);
    }
    
    private List<Object> buildRouteParameters(final GroupedParameterBuilder paramBuilder, final RouteContext routeContext, final RouteUnit routeUnit) {
        List<Object> result = new LinkedList<>();
        int count = 0;
        for (Collection<DataNode> each : routeContext.getOriginalDataNodes()) {
            if (isInSameDataNode(each, routeUnit)) {
                result.addAll(paramBuilder.getParameters(count));
            }
            count++;
        }
        result.addAll(paramBuilder.getGenericParameterBuilder().getParameters());
        return result;
    }
    
    private boolean isInSameDataNode(final Collection<DataNode> dataNodes, final RouteUnit routeUnit) {
        if (dataNodes.isEmpty()) {
            return true;
        }
        for (DataNode each : dataNodes) {
            if (routeUnit.findTableMapper(each.getDataSourceName(), each.getTableName()).isPresent()) {
                return true;
            }
        }
        return false;
    }
    
    private Map<RouteUnit, SQLRewriteUnit> translate(final QueryContext queryContext, final Map<RouteUnit, SQLRewriteUnit> sqlRewriteUnits) {
        Map<RouteUnit, SQLRewriteUnit> result = new LinkedHashMap<>(sqlRewriteUnits.size(), 1F);
        Map<String, StorageUnit> storageUnits = database.getResourceMetaData().getStorageUnits();
        for (Entry<RouteUnit, SQLRewriteUnit> entry : sqlRewriteUnits.entrySet()) {
            DatabaseType storageType = storageUnits.get(entry.getKey().getDataSourceMapper().getActualName()).getStorageType();
            SQLTranslatorContext sqlTranslatorContext = translatorRule.translate(entry.getValue().getSql(), entry.getValue().getParameters(), queryContext, storageType, database, globalRuleMetaData);
            SQLRewriteUnit sqlRewriteUnit = new SQLRewriteUnit(sqlTranslatorContext.getSql(), sqlTranslatorContext.getParameters());
            result.put(entry.getKey(), sqlRewriteUnit);
        }
        return result;
    }
}

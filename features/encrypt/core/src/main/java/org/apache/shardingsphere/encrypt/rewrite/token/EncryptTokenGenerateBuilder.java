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

package org.apache.shardingsphere.encrypt.rewrite.token;

import lombok.RequiredArgsConstructor;
import org.apache.shardingsphere.encrypt.rewrite.aware.DatabaseNameAware;
import org.apache.shardingsphere.encrypt.rewrite.aware.DatabaseTypeAware;
import org.apache.shardingsphere.encrypt.rewrite.aware.EncryptConditionsAware;
import org.apache.shardingsphere.encrypt.rewrite.aware.EncryptRuleAware;
import org.apache.shardingsphere.encrypt.rewrite.condition.EncryptCondition;
import org.apache.shardingsphere.encrypt.rewrite.token.generator.EncryptAlterTableTokenGenerator;
import org.apache.shardingsphere.encrypt.rewrite.token.generator.EncryptCreateTableTokenGenerator;
import org.apache.shardingsphere.encrypt.rewrite.token.generator.EncryptGroupByItemTokenGenerator;
import org.apache.shardingsphere.encrypt.rewrite.token.generator.EncryptIndexColumnTokenGenerator;
import org.apache.shardingsphere.encrypt.rewrite.token.generator.EncryptOrderByItemTokenGenerator;
import org.apache.shardingsphere.encrypt.rewrite.token.generator.EncryptPredicateColumnTokenGenerator;
import org.apache.shardingsphere.encrypt.rewrite.token.generator.EncryptPredicateRightValueTokenGenerator;
import org.apache.shardingsphere.encrypt.rewrite.token.generator.assignment.EncryptInsertAssignmentTokenGenerator;
import org.apache.shardingsphere.encrypt.rewrite.token.generator.assignment.EncryptUpdateAssignmentTokenGenerator;
import org.apache.shardingsphere.encrypt.rewrite.token.generator.insert.EncryptInsertCipherNameTokenGenerator;
import org.apache.shardingsphere.encrypt.rewrite.token.generator.insert.EncryptInsertDefaultColumnsTokenGenerator;
import org.apache.shardingsphere.encrypt.rewrite.token.generator.insert.EncryptInsertDerivedColumnsTokenGenerator;
import org.apache.shardingsphere.encrypt.rewrite.token.generator.insert.EncryptInsertOnUpdateTokenGenerator;
import org.apache.shardingsphere.encrypt.rewrite.token.generator.insert.EncryptInsertValuesTokenGenerator;
import org.apache.shardingsphere.encrypt.rewrite.token.generator.projection.EncryptInsertSelectProjectionTokenGenerator;
import org.apache.shardingsphere.encrypt.rewrite.token.generator.projection.EncryptSelectProjectionTokenGenerator;
import org.apache.shardingsphere.encrypt.rule.EncryptRule;
import org.apache.shardingsphere.infra.binder.context.statement.SQLStatementContext;
import org.apache.shardingsphere.infra.rewrite.sql.token.generator.SQLTokenGenerator;
import org.apache.shardingsphere.infra.rewrite.sql.token.generator.builder.SQLTokenGeneratorBuilder;

import java.util.Collection;
import java.util.LinkedList;

/**
 * SQL token generator builder for encrypt.
 */
@RequiredArgsConstructor
public final class EncryptTokenGenerateBuilder implements SQLTokenGeneratorBuilder {
    
    private final EncryptRule encryptRule;
    
    private final SQLStatementContext sqlStatementContext;
    
    private final Collection<EncryptCondition> encryptConditions;
    
    private final String databaseName;
    
    @Override
    public Collection<SQLTokenGenerator> getSQLTokenGenerators() {
        Collection<SQLTokenGenerator> result = new LinkedList<>();
        addSQLTokenGenerator(result, new EncryptSelectProjectionTokenGenerator());
        addSQLTokenGenerator(result, new EncryptInsertSelectProjectionTokenGenerator());
        addSQLTokenGenerator(result, new EncryptInsertAssignmentTokenGenerator());
        addSQLTokenGenerator(result, new EncryptUpdateAssignmentTokenGenerator());
        addSQLTokenGenerator(result, new EncryptPredicateColumnTokenGenerator());
        addSQLTokenGenerator(result, new EncryptPredicateRightValueTokenGenerator());
        addSQLTokenGenerator(result, new EncryptInsertValuesTokenGenerator());
        addSQLTokenGenerator(result, new EncryptInsertDefaultColumnsTokenGenerator());
        addSQLTokenGenerator(result, new EncryptInsertCipherNameTokenGenerator());
        addSQLTokenGenerator(result, new EncryptInsertDerivedColumnsTokenGenerator());
        addSQLTokenGenerator(result, new EncryptInsertOnUpdateTokenGenerator());
        addSQLTokenGenerator(result, new EncryptCreateTableTokenGenerator());
        addSQLTokenGenerator(result, new EncryptAlterTableTokenGenerator());
        addSQLTokenGenerator(result, new EncryptOrderByItemTokenGenerator());
        addSQLTokenGenerator(result, new EncryptGroupByItemTokenGenerator());
        addSQLTokenGenerator(result, new EncryptIndexColumnTokenGenerator());
        return result;
    }
    
    private void addSQLTokenGenerator(final Collection<SQLTokenGenerator> sqlTokenGenerators, final SQLTokenGenerator toBeAddedSQLTokenGenerator) {
        setUpSQLTokenGenerator(toBeAddedSQLTokenGenerator);
        if (toBeAddedSQLTokenGenerator.isGenerateSQLToken(sqlStatementContext)) {
            sqlTokenGenerators.add(toBeAddedSQLTokenGenerator);
        }
    }
    
    private void setUpSQLTokenGenerator(final SQLTokenGenerator toBeAddedSQLTokenGenerator) {
        if (toBeAddedSQLTokenGenerator instanceof EncryptRuleAware) {
            ((EncryptRuleAware) toBeAddedSQLTokenGenerator).setEncryptRule(encryptRule);
        }
        if (toBeAddedSQLTokenGenerator instanceof EncryptConditionsAware) {
            ((EncryptConditionsAware) toBeAddedSQLTokenGenerator).setEncryptConditions(encryptConditions);
        }
        if (toBeAddedSQLTokenGenerator instanceof DatabaseNameAware) {
            ((DatabaseNameAware) toBeAddedSQLTokenGenerator).setDatabaseName(databaseName);
        }
        if (toBeAddedSQLTokenGenerator instanceof DatabaseTypeAware) {
            ((DatabaseTypeAware) toBeAddedSQLTokenGenerator).setDatabaseType(sqlStatementContext.getDatabaseType());
        }
    }
}

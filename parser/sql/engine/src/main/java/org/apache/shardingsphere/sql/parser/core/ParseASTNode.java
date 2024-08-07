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

package org.apache.shardingsphere.sql.parser.core;

import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.shardingsphere.sql.parser.api.ASTNode;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Parse AST node.
 */
@RequiredArgsConstructor
public final class ParseASTNode implements ASTNode {
    
    private final ParseTree parseTree;
    
    private final CommonTokenStream tokenStream;
    
    /**
     * Get root node.
     *
     * @return root node
     */
    public ParseTree getRootNode() {
        return parseTree.getChild(0);
    }
    
    /**
     * Get hidden tokens.
     *
     * @return hidden tokens
     */
    public Collection<Token> getHiddenTokens() {
        Collection<Token> result = new LinkedList<>();
        for (Token each : tokenStream.getTokens()) {
            if (Token.HIDDEN_CHANNEL == each.getChannel()) {
                result.add(each);
            }
        }
        return result;
    }
}

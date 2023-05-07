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

package org.apache.shardingsphere.test.e2e;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

public class ResultSetPrinter {
    
    /**
     * print sql result
     * @param rs result set
     * @throws SQLException
     */
    public static void printResultSet(ResultSet rs) throws SQLException {
        ResultSetMetaData resultSetMetaData = rs.getMetaData();
        int ColumnCount = resultSetMetaData.getColumnCount();
        int[] columnMaxLengths = new int[ColumnCount];
        ArrayList<String[]> results = new ArrayList<>();
        while (rs.next()) {
            String[] columnStr = new String[ColumnCount];
            for (int i = 0; i < ColumnCount; i++) {
                columnStr[i] = rs.getString(i + 1);
                columnMaxLengths[i] = Math.max(columnMaxLengths[i], (columnStr[i] == null) ? 0 : columnStr[i].length());
            }
            results.add(columnStr);
        }
        printSeparator(columnMaxLengths);
        printColumnName(resultSetMetaData, columnMaxLengths);
        printSeparator(columnMaxLengths);
        Iterator<String[]> iterator = results.iterator();
        String[] columnStr;
        while (iterator.hasNext()) {
            columnStr = iterator.next();
            for (int i = 0; i < ColumnCount; i++) {
                System.out.printf("|%" + (columnMaxLengths[i] + 1) + "s", columnStr[i]);
            }
            System.out.println("|");
        }
        printSeparator(columnMaxLengths);
    }
    
    /***
     * print column name
     * @param resultSetMetaData result set meta data
     * @param columnMaxLengths column max length
     * @throws SQLException SQL exception
     */
    private static void printColumnName(ResultSetMetaData resultSetMetaData, int[] columnMaxLengths) throws SQLException {
        int columnCount = resultSetMetaData.getColumnCount();
        for (int i = 0; i < columnCount; i++) {
            System.out.printf("|%" + (columnMaxLengths[i] + 1) + "s", resultSetMetaData.getColumnName(i + 1));
        }
        System.out.println("|");
    }
    
    /**
     * print separator
     * @param columnMaxLengths column max length
     */
    private static void printSeparator(int[] columnMaxLengths) {
        for (final int columnMaxLength : columnMaxLengths) {
            System.out.print("+");
            for (int j = 0; j < columnMaxLength; j++) {
                System.out.print("-");
            }
        }
        System.out.println("+");
    }
}

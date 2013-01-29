/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package groovy.sql.builder

import groovy.sql.Sql
import groovy.sql.builder.result.ResultAware
import groovy.sql.builder.result.Statement
import groovy.sql.builder.node.factory.*
import groovy.sql.builder.node.SelectClauseElements

/**
 *
 *
 * @author Benjamin Muschko
 */
class SqlSelectBuilder extends AbstractSqlFactoryBuilder {
    SqlSelectBuilder(Sql sql) {
        super(sql)
    }

    @Override
    List<NamedAbstractFactory> getNamedFactories() {
        [new SelectFactory(), new EqualsCriteriaFactory(), new NotEqualsCriteriaFactory(), new LikeCriteriaFactory(),
                new IsNullCriteriaFactory(), new IsNotNullCriteriaFactory(), new GreaterThanCriteriaFactory(), new GreaterThanEqualsCriteriaFactory(),
                new LessThanCriteriaFactory(), new LessThanEqualsCriteriaFactory(), new BetweenCriteriaFactory(), new InCriteriaFactory(),
                new AndLogicOperationFactory(), new OrLogicOperationFactory(), new NotLogicOperatorFactory(),
                new OrderCriteriaFactory(), new LimitCriteriaFactory(), new ColumnsCriteriaFactory()].asImmutable()
    }

    private class SelectFactory extends GroovySqlAbstractFactory {
        final String TABLE_ATTRIBUTE = 'table'

        @Override
        String getName() {
            'select'
        }

        @Override
        Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) {
            Select select = new Select()
            select.table = (attributes && attributes.containsKey(TABLE_ATTRIBUTE)) ? attributes[TABLE_ATTRIBUTE] : value
            select
        }

        @Override
        void onNodeCompleted(FactoryBuilderSupport builder, Object parent, Object node) {
            def statement = createStatement(node)
            node.statement = statement
            def rows = builder.sql.rows(statement.sql, statement.params)
            node.result = rows
        }

        private String createSql(String table, SelectClauseElements clauseElements) {
            def sql = new StringBuilder()

            def columns = !clauseElements.columns ? '*' : clauseElements.columns.renderExpression()
            sql <<= "SELECT $columns FROM ${table}"

            if (clauseElements.where.size() > 0) {
                sql <<= " ${getCriteriaExpression(clauseElements.where)}"
            }

            if (clauseElements.orderBy) {
                sql <<= " ${clauseElements.orderBy.renderExpression()}"
            }

            if (clauseElements.limit) {
                sql <<= " ${clauseElements.limit.renderExpression()}"
            }

            sql
        }

        private Statement createStatement(Object node) {
            String sql = createSql(node.table, node.clauseElements)
            def params = []
            collectCriteriaParams(params, node.clauseElements.where)
            new Statement(sql: sql, params: params)
        }

        @Override
        public boolean isLeaf() {
            false
        }
    }

    class Select implements ResultAware {
        String table
        def clauseElements = new SelectClauseElements()
        Statement statement
        def result

        @Override
        def getResult() {
            result
        }
    }
}
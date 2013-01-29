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
import groovy.sql.builder.result.Statement
import groovy.sql.builder.node.factory.*

/**
 * @author Benjamin Muschko
 */
class SqlDeleteBuilder extends AbstractSqlFactoryBuilder {
    SqlDeleteBuilder(Sql sql) {
        super(sql)
    }

    @Override
    List<NamedAbstractFactory> getNamedFactories() {
        [new DeleteFactory(), new EqualsCriteriaFactory(), new NotEqualsCriteriaFactory(), new LikeCriteriaFactory(),
                new IsNullCriteriaFactory(), new IsNotNullCriteriaFactory(), new GreaterThanCriteriaFactory(), new GreaterThanEqualsCriteriaFactory(),
                new LessThanCriteriaFactory(), new LessThanEqualsCriteriaFactory(), new BetweenCriteriaFactory(), new InCriteriaFactory(),
                new AndLogicOperationFactory(), new OrLogicOperationFactory(), new NotLogicOperatorFactory()].asImmutable()
    }

    private class DeleteFactory extends GroovySqlAbstractFactory {
        final String TABLE_ATTRIBUTE = 'table'

        @Override
        String getName() {
            'delete'
        }

        @Override
        Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) {
            Delete delete = new Delete()
            delete.table = (attributes && attributes.containsKey(TABLE_ATTRIBUTE)) ? attributes[TABLE_ATTRIBUTE] : value
            delete
        }

        @Override
        void onNodeCompleted(FactoryBuilderSupport builder, Object parent, Object node) {
            def statement = createStatement(node)
            node.statement = statement
            builder.sql.withTransaction {
                builder.sql.execute(statement.sql, statement.params)
            }
        }

        private String createSql(String table, criterias) {
            def sql = new StringBuilder()
            sql <<= "DELETE FROM ${table}"

            if (criterias.size > 0) {
                sql <<= " ${getCriteriaExpression(criterias)}"
            }

            sql
        }

        private Statement createStatement(Object node) {
            String sql = createSql(node.table, node.criterias)
            def params = []
            collectCriteriaParams(params, node.criterias)
            new Statement(sql: sql, params: params)
        }

        @Override
        public boolean isLeaf() {
            false
        }
    }

    private class Delete {
        String table
        def criterias = []
        Statement statement
    }
}
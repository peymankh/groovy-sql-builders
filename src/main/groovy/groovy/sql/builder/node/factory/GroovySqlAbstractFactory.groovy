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
package groovy.sql.builder.node.factory

import groovy.sql.builder.node.Criteria
import groovy.sql.builder.node.LogicOperator
import groovy.sql.builder.node.ParameterizedCriteria

/**
 *
 *
 * @author Benjamin Muschko
 */
abstract class GroovySqlAbstractFactory extends NamedAbstractFactory {
    String getCriteriaExpression(List<Criteria> criterias) {
        def expression = new StringBuilder()

        if(criterias.size() > 0) {
            criterias.eachWithIndex { criteria, index ->
                if(index == 0) {
                    expression <<= "WHERE "
                }
                else {
                    expression <<= " AND "
                }

                if(criteria instanceof Criteria) {
                    expression <<= criteria.renderExpression()
                }
            }
        }

        expression
    }

    List<Object> collectCriteriaParams(List<Object> params, List<Criteria> criterias) {
        criterias.each { criteria ->
            if(criteria instanceof ParameterizedCriteria) {
                params.addAll criteria.getParams()
            }
            else if(criteria instanceof LogicOperator) {
                collectCriteriaParams(params, criteria.criterias)
            }
        }
    }
}

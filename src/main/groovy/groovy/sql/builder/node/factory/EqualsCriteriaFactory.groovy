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

import groovy.sql.builder.node.EqualsCriteria

/**
 *
 *
 * @author Benjamin Muschko
 */
class EqualsCriteriaFactory extends KeyValuePairCriteriaAbstractFactory {
    @Override
    String getName() {
        'eq'
    }

    @Override
    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) {
        new EqualsCriteria(attributes[NAME_ATTRIBUTE], attributes[VALUE_ATTRIBUTE])
    }
}
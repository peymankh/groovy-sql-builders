
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
import java.util.logging.ConsoleHandler
import java.util.logging.Level
import java.util.logging.Logger
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass

/**
 *
 *
 * @author Benjamin Muschko
 */
class SqlBuilderFixture {
    static final String TABLE_NAME = 'city'
    Sql sql

    @BeforeClass
    public static void setUpClass() {
        def logger = Logger.getLogger('groovy.sql')
        logger.level = Level.FINE
        logger.addHandler(new ConsoleHandler(level: Level.FINE))
    }

    @Before
    public void setUp() {
        sql = SqlHandler.createDriverManagerSql()
        createCleanSchema()
    }

    @After
    public void tearDown() {
        sql = null
    }

    def createCleanSchema() {
        sql.execute """
            DROP INDEX cityIdx IF EXISTS;
            DROP TABLE city IF EXISTS;

            CREATE TABLE city (
                id INTEGER GENERATED BY DEFAULT AS IDENTITY,
                name VARCHAR(64),
                state VARCHAR(64),
                founded_year INTEGER(4)
            );

            CREATE INDEX cityIdx ON city (id);
        """
    }
}
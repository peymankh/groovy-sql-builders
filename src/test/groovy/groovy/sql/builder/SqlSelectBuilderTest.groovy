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

import org.junit.Before
import org.junit.Test

/**
 *
 *
 * @author Benjamin Muschko
 */
class SqlSelectBuilderTest extends SqlBuilderFixture {
    @Before
    @Override
    public void setUp() {
        super.setUp()
        sql.executeInsert('INSERT INTO city (name, state, founded_year) VALUES (?, ?, ?)', ['Grand Rapids', 'Michigan', 1825])
        sql.executeInsert('INSERT INTO city (name, state, founded_year) VALUES (?, ?, ?)', ['Little Rock', 'Arkansas', 1821])
        sql.executeInsert('INSERT INTO city (name, state, founded_year) VALUES (?, ?, ?)', ['Boston', 'Massachusetts', 1630])
        sql.executeInsert('INSERT INTO city (name, state, founded_year) VALUES (?, ?, ?)', ['Gulfport', 'Mississippi', 1887])
        def firstRow = sql.firstRow("SELECT * from city WHERE id = ?", [1])
        assert firstRow.name == "Grand Rapids"
        assert firstRow.state == "Michigan"
        assert firstRow.founded_year == 1825
        def secondRow = sql.firstRow("SELECT * from city WHERE id = ?", [2])
        assert secondRow.name == "Little Rock"
        assert secondRow.state == "Arkansas"
        assert secondRow.founded_year == 1821
        def thirdRow = sql.firstRow("SELECT * from city WHERE id = ?", [3])
        assert thirdRow.name == "Boston"
        assert thirdRow.state == "Massachusetts"
        assert thirdRow.founded_year == 1630
        def forthRow = sql.firstRow("SELECT * from city WHERE id = ?", [4])
        assert forthRow.name == "Gulfport"
        assert forthRow.state == "Mississippi"
        assert forthRow.founded_year == 1887
    }

    @Test
    public void testBuildingWithNoCriteriaSingleTable() {
        def builder = new SqlSelectBuilder(sql)
        def select = builder.select(TABLE_NAME)

        assert select.statement.sql == "SELECT * FROM city"
        assert select.statement.params.size() == 0
        def rows = sql.rows("SELECT * FROM city")
        assert rows.size() == 4
        assert rows.get(0).name == "Grand Rapids"
        assert rows.get(0).state == "Michigan"
        assert rows.get(0).founded_year == 1825
        assert rows.get(1).name == "Little Rock"
        assert rows.get(1).state == "Arkansas"
        assert rows.get(1).founded_year == 1821
        assert rows.get(2).name == "Boston"
        assert rows.get(2).state == "Massachusetts"
        assert rows.get(2).founded_year == 1630
        assert rows.get(3).name == "Gulfport"
        assert rows.get(3).state == "Mississippi"
        assert rows.get(3).founded_year == 1887
        println "Selected rows: ${select.result}"
    }

    @Test
    public void testBuildingWithEqualsCriteriaSingleTable() {
        def builder = new SqlSelectBuilder(sql)
        def select = builder.select(TABLE_NAME) {
            eq(name: 'name', value: 'Grand Rapids')
        }

        assert select.statement.sql == "SELECT * FROM city WHERE name = ?"
        assert select.statement.params.size() == 1
        assert select.statement.params.get(0) == "Grand Rapids"
        assert select.result.size() == 1
        assert select.result.get(0).name == "Grand Rapids"
        assert select.result.get(0).state == "Michigan"
        assert select.result.get(0).founded_year == 1825
        println "Selected rows: ${select.result}"
    }

    @Test
    public void testBuildingWithLikeCriteriaSingleTableOrderedDefault() {
        def builder = new SqlSelectBuilder(sql)
        def select = builder.select(TABLE_NAME) {
            like(name: 'name', value: 'G%')
            order(name: 'name')
        }

        assert select.statement.sql == "SELECT * FROM city WHERE name like ? ORDER BY name ASC"
        assert select.statement.params.size() == 1
        assert select.statement.params.get(0) == "G%"
        assert select.result.size() == 2
        assert select.result.get(0).name == "Grand Rapids"
        assert select.result.get(0).state == "Michigan"
        assert select.result.get(0).founded_year == 1825
        assert select.result.get(1).name == "Gulfport"
        assert select.result.get(1).state == "Mississippi"
        assert select.result.get(1).founded_year == 1887
        println "Selected rows: ${select.result}"
    }

    @Test
    public void testBuildingWithLikeCriteriaSingleTableOrderedAsc() {
        def builder = new SqlSelectBuilder(sql)
        def select = builder.select(TABLE_NAME) {
            like(name: 'name', value: 'G%')
            order(name: 'name', value: 'asc')
        }

        assert select.statement.sql == "SELECT * FROM city WHERE name like ? ORDER BY name ASC"
        assert select.statement.params.size() == 1
        assert select.statement.params.get(0) == "G%"
        assert select.result.size() == 2
        assert select.result.get(0).name == "Grand Rapids"
        assert select.result.get(0).state == "Michigan"
        assert select.result.get(0).founded_year == 1825
        assert select.result.get(1).name == "Gulfport"
        assert select.result.get(1).state == "Mississippi"
        assert select.result.get(1).founded_year == 1887
        println "Selected rows: ${select.result}"
    }

    @Test
    public void testBuildingWithLikeCriteriaSingleTableOrderedDesc() {
        def builder = new SqlSelectBuilder(sql)
        def select = builder.select(TABLE_NAME) {
            like(name: 'name', value: 'G%')
            order(name: 'name', value: 'desc')
        }

        assert select.statement.sql == "SELECT * FROM city WHERE name like ? ORDER BY name DESC"
        assert select.statement.params.size() == 1
        assert select.statement.params.get(0) == "G%"
        assert select.result.size() == 2
        assert select.result.get(0).name == "Gulfport"
        assert select.result.get(0).state == "Mississippi"
        assert select.result.get(0).founded_year == 1887
        assert select.result.get(1).name == "Grand Rapids"
        assert select.result.get(1).state == "Michigan"
        assert select.result.get(1).founded_year == 1825
        println "Selected rows: ${select.result}"
    }
}

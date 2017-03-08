/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.v7.data.util.sqlcontainer;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.vaadin.v7.data.util.sqlcontainer.connection.J2EEConnectionPoolTest;
import com.vaadin.v7.data.util.sqlcontainer.connection.SimpleJDBCConnectionPoolTest;
import com.vaadin.v7.data.util.sqlcontainer.filters.BetweenTest;
import com.vaadin.v7.data.util.sqlcontainer.filters.LikeTest;
import com.vaadin.v7.data.util.sqlcontainer.generator.SQLGeneratorsTest;
import com.vaadin.v7.data.util.sqlcontainer.query.FreeformQueryTest;
import com.vaadin.v7.data.util.sqlcontainer.query.QueryBuilderTest;
import com.vaadin.v7.data.util.sqlcontainer.query.TableQueryTest;

@RunWith(Suite.class)
@SuiteClasses({ SimpleJDBCConnectionPoolTest.class,
        J2EEConnectionPoolTest.class, LikeTest.class, QueryBuilderTest.class,
        FreeformQueryTest.class, RowIdTest.class, SQLContainerTest.class,
        SQLContainerTableQueryTest.class, ColumnPropertyTest.class,
        TableQueryTest.class, SQLGeneratorsTest.class, UtilTest.class,
        TicketTest.class, BetweenTest.class, ReadOnlyRowIdTest.class })
public class AllTests {
}

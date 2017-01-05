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

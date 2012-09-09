package com.vaadin.data.util.sqlcontainer;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.vaadin.data.util.sqlcontainer.connection.J2EEConnectionPoolTest;
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPoolTest;
import com.vaadin.data.util.sqlcontainer.filters.BetweenTest;
import com.vaadin.data.util.sqlcontainer.filters.LikeTest;
import com.vaadin.data.util.sqlcontainer.generator.SQLGeneratorsTest;
import com.vaadin.data.util.sqlcontainer.query.FreeformQueryTest;
import com.vaadin.data.util.sqlcontainer.query.QueryBuilderTest;
import com.vaadin.data.util.sqlcontainer.query.TableQueryTest;

@RunWith(Suite.class)
@SuiteClasses({ SimpleJDBCConnectionPoolTest.class,
        J2EEConnectionPoolTest.class, LikeTest.class, QueryBuilderTest.class,
        FreeformQueryTest.class, RowIdTest.class, SQLContainerTest.class,
        SQLContainerTableQueryTest.class, ColumnPropertyTest.class,
        TableQueryTest.class, SQLGeneratorsTest.class, UtilTest.class,
        TicketTests.class, BetweenTest.class, ReadOnlyRowIdTest.class })
public class AllTests {
}

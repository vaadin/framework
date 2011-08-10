package com.vaadin.tests.server.container.sqlcontainer;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.SQLContainer;
import com.vaadin.data.util.connection.SimpleJDBCConnectionPool;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.query.FreeformQuery;
import com.vaadin.data.util.query.FreeformStatementDelegate;
import com.vaadin.data.util.query.TableQuery;
import com.vaadin.data.util.query.generator.StatementHelper;
import com.vaadin.data.util.query.generator.filter.QueryBuilder;
import com.vaadin.tests.server.container.sqlcontainer.AllTests.DB;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

public class TicketTests {

    private SimpleJDBCConnectionPool connectionPool;

    @Before
    public void setUp() throws SQLException {
        connectionPool = new SimpleJDBCConnectionPool(AllTests.dbDriver,
                AllTests.dbURL, AllTests.dbUser, AllTests.dbPwd, 2, 2);
        DataGenerator.addPeopleToDatabase(connectionPool);
    }

    @Test
    public void ticket5867_throwsIllegalState_transactionAlreadyActive()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", Arrays.asList("ID"), connectionPool));
        Table table = new Table();
        Window w = new Window();
        w.addComponent(table);
        table.setContainerDataSource(container);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void ticket6136_freeform_ageIs18() throws SQLException {
        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                Arrays.asList("ID"), connectionPool);
        FreeformStatementDelegate delegate = EasyMock
                .createMock(FreeformStatementDelegate.class);
        final ArrayList<Filter> filters = new ArrayList<Filter>();
        delegate.setFilters(null);
        EasyMock.expectLastCall().anyTimes();
        delegate.setOrderBy(EasyMock.isA(List.class));
        EasyMock.expectLastCall().anyTimes();
        delegate.setOrderBy(null);
        EasyMock.expectLastCall().anyTimes();
        delegate.setFilters(EasyMock.isA(List.class));
        EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
            public Object answer() throws Throwable {
                List<Filter> orders = (List<Filter>) EasyMock
                        .getCurrentArguments()[0];
                filters.clear();
                filters.addAll(orders);
                return null;
            }
        }).anyTimes();
        EasyMock.expect(
                delegate.getQueryStatement(EasyMock.anyInt(), EasyMock.anyInt()))
                .andAnswer(new IAnswer<StatementHelper>() {
                    public StatementHelper answer() throws Throwable {
                        Object[] args = EasyMock.getCurrentArguments();
                        int offset = (Integer) (args[0]);
                        int limit = (Integer) (args[1]);
                        return FreeformQueryUtil.getQueryWithFilters(filters,
                                offset, limit);
                    }
                }).anyTimes();
        EasyMock.expect(delegate.getCountStatement())
                .andAnswer(new IAnswer<StatementHelper>() {
                    @SuppressWarnings("deprecation")
                    public StatementHelper answer() throws Throwable {
                        StatementHelper sh = new StatementHelper();
                        StringBuffer query = new StringBuffer(
                                "SELECT COUNT(*) FROM people");
                        if (!filters.isEmpty()) {
                            query.append(QueryBuilder.getWhereStringForFilters(
                                    filters, sh));
                        }
                        sh.setQueryString(query.toString());
                        return sh;
                    }
                }).anyTimes();

        EasyMock.replay(delegate);
        query.setDelegate(delegate);
        SQLContainer container = new SQLContainer(query);
        // Ville, Kalle, Pelle, Börje
        Assert.assertEquals(4, container.size());
        Assert.assertEquals("Börje",
                container.getContainerProperty(container.lastItemId(), "NAME")
                        .getValue());

        container.addContainerFilter(new Equal("AGE", 18));
        // Pelle
        Assert.assertEquals(1, container.size());
        Assert.assertEquals("Pelle",
                container.getContainerProperty(container.firstItemId(), "NAME")
                        .getValue());
        if (AllTests.db == DB.ORACLE) {
            Assert.assertEquals(new BigDecimal(18), container
                    .getContainerProperty(container.firstItemId(), "AGE")
                    .getValue());
        } else {
            Assert.assertEquals(
                    18,
                    container.getContainerProperty(container.firstItemId(),
                            "AGE").getValue());
        }

        EasyMock.verify(delegate);
    }

    @Test
    public void ticket6136_table_ageIs18() throws SQLException {
        TableQuery query = new TableQuery("people", connectionPool,
                AllTests.sqlGen);
        SQLContainer container = new SQLContainer(query);
        // Ville, Kalle, Pelle, Börje
        Assert.assertEquals(4, container.size());

        container.addContainerFilter(new Equal("AGE", 18));

        // Pelle
        Assert.assertEquals(1, container.size());
        Assert.assertEquals("Pelle",
                container.getContainerProperty(container.firstItemId(), "NAME")
                        .getValue());
        if (AllTests.db == DB.ORACLE) {
            Assert.assertEquals(new BigDecimal(18), container
                    .getContainerProperty(container.firstItemId(), "AGE")
                    .getValue());
        } else {
            Assert.assertEquals(
                    18,
                    container.getContainerProperty(container.firstItemId(),
                            "AGE").getValue());
        }
    }

}

package com.vaadin.v7.data.util.sqlcontainer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.ui.Window;
import com.vaadin.v7.data.Container.Filter;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.filter.Compare.Equal;
import com.vaadin.v7.data.util.sqlcontainer.SQLTestsConstants.DB;
import com.vaadin.v7.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.v7.data.util.sqlcontainer.query.FreeformQuery;
import com.vaadin.v7.data.util.sqlcontainer.query.FreeformStatementDelegate;
import com.vaadin.v7.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.v7.data.util.sqlcontainer.query.ValidatingSimpleJDBCConnectionPool;
import com.vaadin.v7.data.util.sqlcontainer.query.generator.StatementHelper;
import com.vaadin.v7.data.util.sqlcontainer.query.generator.filter.QueryBuilder;
import com.vaadin.v7.ui.Table;

public class TicketTest {

    private JDBCConnectionPool connectionPool;

    @Before
    public void setUp() throws SQLException {
        connectionPool = new ValidatingSimpleJDBCConnectionPool(
                SQLTestsConstants.dbDriver, SQLTestsConstants.dbURL,
                SQLTestsConstants.dbUser, SQLTestsConstants.dbPwd, 2, 2);
        DataGenerator.addPeopleToDatabase(connectionPool);
    }

    @Test
    public void ticket5867_throwsIllegalState_transactionAlreadyActive()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", Arrays.asList("ID"), connectionPool));
        Table table = new Table();
        Window w = new Window();
        w.setContent(table);
        table.setContainerDataSource(container);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void ticket6136_freeform_ageIs18() throws SQLException {
        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                Arrays.asList("ID"), connectionPool);
        FreeformStatementDelegate delegate = EasyMock
                .createMock(FreeformStatementDelegate.class);
        final List<Filter> filters = new ArrayList<Filter>();
        delegate.setFilters(null);
        EasyMock.expectLastCall().anyTimes();
        delegate.setOrderBy(EasyMock.isA(List.class));
        EasyMock.expectLastCall().anyTimes();
        delegate.setOrderBy(null);
        EasyMock.expectLastCall().anyTimes();
        delegate.setFilters(EasyMock.isA(List.class));
        EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
            @Override
            public Object answer() throws Throwable {
                List<Filter> orders = (List<Filter>) EasyMock
                        .getCurrentArguments()[0];
                filters.clear();
                filters.addAll(orders);
                return null;
            }
        }).anyTimes();
        EasyMock.expect(delegate.getQueryStatement(EasyMock.anyInt(),
                EasyMock.anyInt())).andAnswer(new IAnswer<StatementHelper>() {
                    @Override
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
                    @Override
                    public StatementHelper answer() throws Throwable {
                        StatementHelper sh = new StatementHelper();
                        StringBuilder query = new StringBuilder(
                                "SELECT COUNT(*) FROM people");
                        if (!filters.isEmpty()) {
                            query.append(QueryBuilder
                                    .getWhereStringForFilters(filters, sh));
                        }
                        sh.setQueryString(query.toString());
                        return sh;
                    }
                }).anyTimes();

        EasyMock.replay(delegate);
        query.setDelegate(delegate);
        SQLContainer container = new SQLContainer(query);
        // Ville, Kalle, Pelle, Börje
        assertEquals(4, container.size());
        assertEquals("Börje",
                container.getContainerProperty(container.lastItemId(), "NAME")
                        .getValue());

        container.addContainerFilter(new Equal("AGE", 18));
        // Pelle
        assertEquals(1, container.size());
        assertEquals("Pelle",
                container.getContainerProperty(container.firstItemId(), "NAME")
                        .getValue());
        if (SQLTestsConstants.db == DB.ORACLE) {
            assertEquals(new BigDecimal(18), container
                    .getContainerProperty(container.firstItemId(), "AGE")
                    .getValue());
        } else {
            assertEquals(18, container
                    .getContainerProperty(container.firstItemId(), "AGE")
                    .getValue());
        }

        EasyMock.verify(delegate);
    }

    @Test
    public void ticket6136_table_ageIs18() throws SQLException {
        TableQuery query = new TableQuery("people", connectionPool,
                SQLTestsConstants.sqlGen);
        SQLContainer container = new SQLContainer(query);
        // Ville, Kalle, Pelle, Börje
        assertEquals(4, container.size());

        container.addContainerFilter(new Equal("AGE", 18));

        // Pelle
        assertEquals(1, container.size());
        assertEquals("Pelle",
                container.getContainerProperty(container.firstItemId(), "NAME")
                        .getValue());
        if (SQLTestsConstants.db == DB.ORACLE) {
            assertEquals(new BigDecimal(18), container
                    .getContainerProperty(container.firstItemId(), "AGE")
                    .getValue());
        } else {
            assertEquals(18, container
                    .getContainerProperty(container.firstItemId(), "AGE")
                    .getValue());
        }
    }

    @Test
    public void ticket7434_getItem_Modified_Changed_Unchanged()
            throws SQLException {
        SQLContainer container = new SQLContainer(new TableQuery("people",
                connectionPool, SQLTestsConstants.sqlGen));

        Object id = container.firstItemId();
        Item item = container.getItem(id);
        String name = (String) item.getItemProperty("NAME").getValue();

        // set a different name
        item.getItemProperty("NAME").setValue("otherName");
        assertEquals("otherName", item.getItemProperty("NAME").getValue());

        // access the item and reset the name to its old value
        Item item2 = container.getItem(id);
        item2.getItemProperty("NAME").setValue(name);
        assertEquals(name, item2.getItemProperty("NAME").getValue());

        Item item3 = container.getItem(id);
        String name3 = (String) item3.getItemProperty("NAME").getValue();

        assertEquals(name, name3);
    }

    @Test
    public void ticket10032_empty_set_metadata_correctly_handled()
            throws SQLException {
        // If problem exists will break when method getPropertyIds()
        // is called in constructor SQLContainer(QueryDelegate delegate).
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people WHERE name='does_not_exist'",
                Arrays.asList("ID"), connectionPool));
        assertTrue("Got items while expected empty set", container.size() == 0);
    }
}

package com.vaadin.v7.data.util.sqlcontainer;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.v7.data.Container.Filter;
import com.vaadin.v7.data.Container.ItemSetChangeEvent;
import com.vaadin.v7.data.Container.ItemSetChangeListener;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.filter.Compare.Equal;
import com.vaadin.v7.data.util.filter.Like;
import com.vaadin.v7.data.util.sqlcontainer.SQLTestsConstants.DB;
import com.vaadin.v7.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.v7.data.util.sqlcontainer.query.FreeformQuery;
import com.vaadin.v7.data.util.sqlcontainer.query.FreeformQueryDelegate;
import com.vaadin.v7.data.util.sqlcontainer.query.FreeformStatementDelegate;
import com.vaadin.v7.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.v7.data.util.sqlcontainer.query.ValidatingSimpleJDBCConnectionPool;
import com.vaadin.v7.data.util.sqlcontainer.query.generator.MSSQLGenerator;
import com.vaadin.v7.data.util.sqlcontainer.query.generator.OracleGenerator;
import com.vaadin.v7.data.util.sqlcontainer.query.generator.SQLGenerator;
import com.vaadin.v7.data.util.sqlcontainer.query.generator.StatementHelper;
import com.vaadin.v7.data.util.sqlcontainer.query.generator.filter.QueryBuilder;

public class SQLContainerTest {
    private static final int offset = SQLTestsConstants.offset;
    private JDBCConnectionPool connectionPool;

    @Before
    public void setUp() throws SQLException {

        try {
            connectionPool = new ValidatingSimpleJDBCConnectionPool(
                    SQLTestsConstants.dbDriver, SQLTestsConstants.dbURL,
                    SQLTestsConstants.dbUser, SQLTestsConstants.dbPwd, 2, 2);
        } catch (SQLException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        DataGenerator.addPeopleToDatabase(connectionPool);
    }

    @After
    public void tearDown() {
        if (connectionPool != null) {
            connectionPool.destroy();
        }
    }

    @Test
    public void constructor_withFreeformQuery_shouldSucceed()
            throws SQLException {
        new SQLContainer(new FreeformQuery("SELECT * FROM people",
                connectionPool, "ID"));
    }

    @Test(expected = SQLException.class)
    public void constructor_withIllegalFreeformQuery_shouldFail()
            throws SQLException {
        SQLContainer c = new SQLContainer(
                new FreeformQuery("SELECT * FROM asdf", connectionPool, "ID"));
        c.getItem(c.firstItemId());
    }

    @Test
    public void containsId_withFreeformQueryAndExistingId_returnsTrue()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        assertTrue(container.containsId(new RowId(new Object[] { 1 })));
    }

    @Test
    public void containsId_withFreeformQueryAndNonexistingId_returnsFalse()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        assertFalse(container.containsId(new RowId(new Object[] { 1337 })));
    }

    @Test
    public void getContainerProperty_freeformExistingItemIdAndPropertyId_returnsProperty()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        if (SQLTestsConstants.db == DB.ORACLE) {
            assertEquals("Ville",
                    container.getContainerProperty(new RowId(
                            new Object[] { new BigDecimal(0 + offset) }),
                            "NAME").getValue());
        } else {
            assertEquals("Ville",
                    container.getContainerProperty(
                            new RowId(new Object[] { 0 + offset }), "NAME")
                            .getValue());
        }
    }

    @Test
    public void getContainerProperty_freeformExistingItemIdAndNonexistingPropertyId_returnsNull()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        assertNull(container.getContainerProperty(
                new RowId(new Object[] { 1 + offset }), "asdf"));
    }

    @Test
    public void getContainerProperty_freeformNonexistingItemId_returnsNull()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        assertNull(container.getContainerProperty(
                new RowId(new Object[] { 1337 + offset }), "NAME"));
    }

    @Test
    public void getContainerPropertyIds_freeform_returnsIDAndNAME()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        Collection<?> propertyIds = container.getContainerPropertyIds();
        assertEquals(3, propertyIds.size());
        assertArrayEquals(new String[] { "ID", "NAME", "AGE" },
                propertyIds.toArray());
    }

    @Test
    public void getItem_freeformExistingItemId_returnsItem()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        Item item;
        if (SQLTestsConstants.db == DB.ORACLE) {
            item = container.getItem(
                    new RowId(new Object[] { new BigDecimal(0 + offset) }));
        } else {
            item = container.getItem(new RowId(new Object[] { 0 + offset }));
        }
        assertNotNull(item);
        assertEquals("Ville", item.getItemProperty("NAME").getValue());
    }

    @Test
    public void nextItemNullAtEnd_freeformExistingItem() throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        Object lastItemId = container.lastItemId();
        Object afterLast = container.nextItemId(lastItemId);
        assertNull(afterLast);
    }

    @Test
    public void prevItemNullAtStart_freeformExistingItem() throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        Object firstItemId = container.firstItemId();
        Object beforeFirst = container.prevItemId(firstItemId);
        assertNull(beforeFirst);
    }

    @Test
    public void getItem_freeform5000RowsWithParameter1337_returnsItemWithId1337()
            throws SQLException {
        DataGenerator.addFiveThousandPeople(connectionPool);
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        Item item;
        if (SQLTestsConstants.db == DB.ORACLE) {
            item = container.getItem(
                    new RowId(new Object[] { new BigDecimal(1337 + offset) }));
            assertNotNull(item);
            assertEquals(new BigDecimal(1337 + offset),
                    item.getItemProperty("ID").getValue());
        } else {
            item = container.getItem(new RowId(new Object[] { 1337 + offset }));
            assertNotNull(item);
            assertEquals(1337 + offset, item.getItemProperty("ID").getValue());
        }
        assertEquals("Person 1337", item.getItemProperty("NAME").getValue());
    }

    @Test
    public void getItemIds_freeform_returnsItemIdsWithKeys0through3()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        Collection<?> itemIds = container.getItemIds();
        assertEquals(4, itemIds.size());
        RowId zero = new RowId(new Object[] { 0 + offset });
        RowId one = new RowId(new Object[] { 1 + offset });
        RowId two = new RowId(new Object[] { 2 + offset });
        RowId three = new RowId(new Object[] { 3 + offset });
        if (SQLTestsConstants.db == DB.ORACLE) {
            String[] correct = { "1", "2", "3", "4" };
            List<String> oracle = new ArrayList<String>();
            for (Object o : itemIds) {
                oracle.add(o.toString());
            }
            assertArrayEquals(correct, oracle.toArray());
        } else {
            assertArrayEquals(new Object[] { zero, one, two, three },
                    itemIds.toArray());
        }
    }

    @Test
    public void getType_freeformNAMEPropertyId_returnsString()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        assertEquals(String.class, container.getType("NAME"));
    }

    @Test
    public void getType_freeformIDPropertyId_returnsInteger()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        if (SQLTestsConstants.db == DB.ORACLE) {
            assertEquals(BigDecimal.class, container.getType("ID"));
        } else {
            assertEquals(Integer.class, container.getType("ID"));
        }
    }

    @Test
    public void getType_freeformNonexistingPropertyId_returnsNull()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        assertNull(container.getType("asdf"));
    }

    @Test
    public void size_freeform_returnsFour() throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        assertEquals(4, container.size());
    }

    @Test
    public void size_freeformOneAddedItem_returnsFive() throws SQLException {
        Connection conn = connectionPool.reserveConnection();
        Statement statement = conn.createStatement();
        if (SQLTestsConstants.db == DB.MSSQL) {
            statement.executeUpdate("insert into people values('Bengt', '42')");
        } else {
            statement.executeUpdate(
                    "insert into people values(default, 'Bengt', '42')");
        }
        statement.close();
        conn.commit();
        connectionPool.releaseConnection(conn);

        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        assertEquals(5, container.size());
    }

    @Test
    public void indexOfId_freeformWithParameterThree_returnsThree()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        if (SQLTestsConstants.db == DB.ORACLE) {
            assertEquals(3, container.indexOfId(
                    new RowId(new Object[] { new BigDecimal(3 + offset) })));
        } else {
            assertEquals(3, container
                    .indexOfId(new RowId(new Object[] { 3 + offset })));
        }
    }

    @Test
    public void indexOfId_freeform5000RowsWithParameter1337_returns1337()
            throws SQLException {
        DataGenerator.addFiveThousandPeople(connectionPool);
        SQLContainer container = new SQLContainer(
                new FreeformQuery("SELECT * FROM people ORDER BY \"ID\" ASC",
                        connectionPool, "ID"));
        if (SQLTestsConstants.db == DB.ORACLE) {
            container.getItem(
                    new RowId(new Object[] { new BigDecimal(1337 + offset) }));
            assertEquals(1337, container.indexOfId(
                    new RowId(new Object[] { new BigDecimal(1337 + offset) })));
        } else {
            container.getItem(new RowId(new Object[] { 1337 + offset }));
            assertEquals(1337, container
                    .indexOfId(new RowId(new Object[] { 1337 + offset })));
        }
    }

    @Test
    public void getIdByIndex_freeform5000rowsIndex1337_returnsRowId1337()
            throws SQLException {
        DataGenerator.addFiveThousandPeople(connectionPool);
        SQLContainer container = new SQLContainer(
                new FreeformQuery("SELECT * FROM people ORDER BY \"ID\" ASC",
                        connectionPool, "ID"));
        Object itemId = container.getIdByIndex(1337);
        if (SQLTestsConstants.db == DB.ORACLE) {
            assertEquals(
                    new RowId(new Object[] { new BigDecimal(1337 + offset) }),
                    itemId);
        } else {
            assertEquals(new RowId(new Object[] { 1337 + offset }), itemId);
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getIdByIndex_freeformWithPaging5000rowsIndex1337_returnsRowId1337()
            throws SQLException {
        DataGenerator.addFiveThousandPeople(connectionPool);
        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                connectionPool, "ID");
        FreeformQueryDelegate delegate = EasyMock
                .createMock(FreeformQueryDelegate.class);
        EasyMock.expect(
                delegate.getQueryString(EasyMock.anyInt(), EasyMock.anyInt()))
                .andAnswer(new IAnswer<String>() {
                    @Override
                    public String answer() throws Throwable {
                        Object[] args = EasyMock.getCurrentArguments();
                        int offset = (Integer) (args[0]);
                        int limit = (Integer) (args[1]);
                        if (SQLTestsConstants.db == DB.MSSQL) {
                            int start = offset + 1;
                            int end = offset + limit + 1;
                            String q = "SELECT * FROM (SELECT row_number() OVER"
                                    + " ( ORDER BY \"ID\" ASC) AS rownum, * FROM people)"
                                    + " AS a WHERE a.rownum BETWEEN " + start
                                    + " AND " + end;
                            return q;
                        } else if (SQLTestsConstants.db == DB.ORACLE) {
                            int start = offset + 1;
                            int end = offset + limit + 1;
                            String q = "SELECT * FROM (SELECT x.*, ROWNUM AS r FROM"
                                    + " (SELECT * FROM people ORDER BY \"ID\" ASC) x) "
                                    + " WHERE r BETWEEN " + start + " AND "
                                    + end;
                            return q;
                        } else {
                            return "SELECT * FROM people LIMIT " + limit
                                    + " OFFSET " + offset;
                        }
                    }
                }).anyTimes();
        delegate.setFilters(null);
        EasyMock.expectLastCall().anyTimes();
        delegate.setFilters(EasyMock.isA(List.class));
        EasyMock.expectLastCall().anyTimes();
        delegate.setOrderBy(null);
        EasyMock.expectLastCall().anyTimes();
        delegate.setOrderBy(EasyMock.isA(List.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(delegate.getCountQuery())
                .andThrow(new UnsupportedOperationException()).anyTimes();
        EasyMock.replay(delegate);
        query.setDelegate(delegate);
        SQLContainer container = new SQLContainer(query);
        Object itemId = container.getIdByIndex(1337);
        if (SQLTestsConstants.db == DB.ORACLE) {
            assertEquals(new RowId(new Object[] { 1337 + offset }).toString(),
                    itemId.toString());
        } else {
            assertEquals(new RowId(new Object[] { 1337 + offset }), itemId);
        }
    }

    @Test
    public void nextItemId_freeformCurrentItem1337_returnsItem1338()
            throws SQLException {
        DataGenerator.addFiveThousandPeople(connectionPool);
        SQLContainer container = new SQLContainer(
                new FreeformQuery("SELECT * FROM people ORDER BY \"ID\" ASC",
                        connectionPool, "ID"));
        Object itemId = container.getIdByIndex(1337);
        if (SQLTestsConstants.db == DB.ORACLE) {
            assertEquals(new RowId(new Object[] { 1338 + offset }).toString(),
                    container.nextItemId(itemId).toString());
        } else {
            assertEquals(new RowId(new Object[] { 1338 + offset }),
                    container.nextItemId(itemId));
        }
    }

    @Test
    public void prevItemId_freeformCurrentItem1337_returns1336()
            throws SQLException {
        DataGenerator.addFiveThousandPeople(connectionPool);
        SQLContainer container = new SQLContainer(
                new FreeformQuery("SELECT * FROM people ORDER BY \"ID\" ASC",
                        connectionPool, "ID"));
        Object itemId = container.getIdByIndex(1337);
        if (SQLTestsConstants.db == DB.ORACLE) {
            assertEquals(new RowId(new Object[] { 1336 + offset }).toString(),
                    container.prevItemId(itemId).toString());
        } else {
            assertEquals(new RowId(new Object[] { 1336 + offset }),
                    container.prevItemId(itemId));
        }
    }

    @Test
    public void firstItemId_freeform_returnsItemId0() throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        if (SQLTestsConstants.db == DB.ORACLE) {
            assertEquals(new RowId(new Object[] { 0 + offset }).toString(),
                    container.firstItemId().toString());
        } else {
            assertEquals(new RowId(new Object[] { 0 + offset }),
                    container.firstItemId());
        }
    }

    @Test
    public void lastItemId_freeform5000Rows_returnsItemId4999()
            throws SQLException {
        DataGenerator.addFiveThousandPeople(connectionPool);

        SQLContainer container = new SQLContainer(
                new FreeformQuery("SELECT * FROM people ORDER BY \"ID\" ASC",
                        connectionPool, "ID"));
        if (SQLTestsConstants.db == DB.ORACLE) {
            assertEquals(new RowId(new Object[] { 4999 + offset }).toString(),
                    container.lastItemId().toString());
        } else {
            assertEquals(new RowId(new Object[] { 4999 + offset }),
                    container.lastItemId());
        }
    }

    @Test
    public void isFirstId_freeformActualFirstId_returnsTrue()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        if (SQLTestsConstants.db == DB.ORACLE) {
            assertTrue(container.isFirstId(
                    new RowId(new Object[] { new BigDecimal(0 + offset) })));
        } else {
            assertTrue(container
                    .isFirstId(new RowId(new Object[] { 0 + offset })));
        }
    }

    @Test
    public void isFirstId_freeformSecondId_returnsFalse() throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        if (SQLTestsConstants.db == DB.ORACLE) {
            assertFalse(container.isFirstId(
                    new RowId(new Object[] { new BigDecimal(1 + offset) })));
        } else {
            assertFalse(container
                    .isFirstId(new RowId(new Object[] { 1 + offset })));
        }
    }

    @Test
    public void isLastId_freeformSecondId_returnsFalse() throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        if (SQLTestsConstants.db == DB.ORACLE) {
            assertFalse(container.isLastId(
                    new RowId(new Object[] { new BigDecimal(1 + offset) })));
        } else {
            assertFalse(
                    container.isLastId(new RowId(new Object[] { 1 + offset })));
        }
    }

    @Test
    public void isLastId_freeformLastId_returnsTrue() throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        if (SQLTestsConstants.db == DB.ORACLE) {
            assertTrue(container.isLastId(
                    new RowId(new Object[] { new BigDecimal(3 + offset) })));
        } else {
            assertTrue(
                    container.isLastId(new RowId(new Object[] { 3 + offset })));
        }
    }

    @Test
    public void isLastId_freeform5000RowsLastId_returnsTrue()
            throws SQLException {
        DataGenerator.addFiveThousandPeople(connectionPool);
        SQLContainer container = new SQLContainer(
                new FreeformQuery("SELECT * FROM people ORDER BY \"ID\" ASC",
                        connectionPool, "ID"));
        if (SQLTestsConstants.db == DB.ORACLE) {
            assertTrue(container.isLastId(
                    new RowId(new Object[] { new BigDecimal(4999 + offset) })));
        } else {
            assertTrue(container
                    .isLastId(new RowId(new Object[] { 4999 + offset })));
        }
    }

    @Test
    public void refresh_freeform_sizeShouldUpdate() throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        assertEquals(4, container.size());
        DataGenerator.addFiveThousandPeople(connectionPool);
        container.refresh();
        assertEquals(5000, container.size());
    }

    @Test
    public void refresh_freeformWithoutCallingRefresh_sizeShouldNotUpdate()
            throws SQLException {
        // Yeah, this is a weird one. We're testing that the size doesn't update
        // after adding lots of items unless we call refresh inbetween. This to
        // make sure that the refresh method actually refreshes stuff and isn't
        // a NOP.
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        assertEquals(4, container.size());
        DataGenerator.addFiveThousandPeople(connectionPool);
        assertEquals(4, container.size());
    }

    @Test
    public void setAutoCommit_freeform_shouldSucceed() throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        container.setAutoCommit(true);
        assertTrue(container.isAutoCommit());
        container.setAutoCommit(false);
        assertFalse(container.isAutoCommit());
    }

    @Test
    public void getPageLength_freeform_returnsDefault100() throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        assertEquals(100, container.getPageLength());
    }

    @Test
    public void setPageLength_freeform_shouldSucceed() throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        container.setPageLength(20);
        assertEquals(20, container.getPageLength());
        container.setPageLength(200);
        assertEquals(200, container.getPageLength());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addContainerProperty_normal_isUnsupported()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        container.addContainerProperty("asdf", String.class, "");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void removeContainerProperty_normal_isUnsupported()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        container.removeContainerProperty("asdf");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addItemObject_normal_isUnsupported() throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        container.addItem("asdf");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addItemAfterObjectObject_normal_isUnsupported()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        container.addItemAfter("asdf", "foo");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addItemAtIntObject_normal_isUnsupported() throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        container.addItemAt(2, "asdf");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addItemAtInt_normal_isUnsupported() throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        container.addItemAt(2);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addItemAfterObject_normal_isUnsupported() throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        container.addItemAfter("asdf");
    }

    @Test
    public void addItem_freeformAddOneNewItem_returnsItemId()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        Object itemId = container.addItem();
        assertNotNull(itemId);
    }

    @Test
    public void addItem_freeformAddOneNewItem_shouldChangeSize()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        int size = container.size();
        container.addItem();
        assertEquals(size + 1, container.size());
    }

    @Test
    public void addItem_freeformAddTwoNewItems_shouldChangeSize()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        int size = container.size();
        Object id1 = container.addItem();
        Object id2 = container.addItem();
        assertEquals(size + 2, container.size());
        assertNotSame(id1, id2);
        assertFalse(id1.equals(id2));
    }

    @Test
    public void nextItemId_freeformNewlyAddedItem_returnsNewlyAdded()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        Object lastId = container.lastItemId();
        Object id = container.addItem();
        assertEquals(id, container.nextItemId(lastId));
    }

    @Test
    public void lastItemId_freeformNewlyAddedItem_returnsNewlyAdded()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        Object lastId = container.lastItemId();
        Object id = container.addItem();
        assertEquals(id, container.lastItemId());
        assertNotSame(lastId, container.lastItemId());
    }

    @Test
    public void indexOfId_freeformNewlyAddedItem_returnsFour()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        Object id = container.addItem();
        assertEquals(4, container.indexOfId(id));
    }

    @Test
    public void getItem_freeformNewlyAddedItem_returnsNewlyAdded()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        Object id = container.addItem();
        assertNotNull(container.getItem(id));
    }

    @Test
    public void getItem_freeformNewlyAddedItemAndFiltered_returnsNull()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        container.addContainerFilter(new Equal("NAME", "asdf"));
        Object id = container.addItem();
        assertNull(container.getItem(id));
    }

    @Test
    public void getItemUnfiltered_freeformNewlyAddedItemAndFiltered_returnsNewlyAdded()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        container.addContainerFilter(new Equal("NAME", "asdf"));
        Object id = container.addItem();
        assertNotNull(container.getItemUnfiltered(id));
    }

    @Test
    public void getItemIds_freeformNewlyAddedItem_containsNewlyAdded()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        Object id = container.addItem();
        assertTrue(container.getItemIds().contains(id));
    }

    @Test
    public void getContainerProperty_freeformNewlyAddedItem_returnsPropertyOfNewlyAddedItem()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        Object id = container.addItem();
        Item item = container.getItem(id);
        item.getItemProperty("NAME").setValue("asdf");
        assertEquals("asdf",
                container.getContainerProperty(id, "NAME").getValue());
    }

    @Test
    public void containsId_freeformNewlyAddedItem_returnsTrue()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        Object id = container.addItem();
        assertTrue(container.containsId(id));
    }

    @Test
    public void prevItemId_freeformTwoNewlyAddedItems_returnsFirstAddedItem()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        Object id1 = container.addItem();
        Object id2 = container.addItem();
        assertEquals(id1, container.prevItemId(id2));
    }

    @Test
    public void firstItemId_freeformEmptyResultSet_returnsFirstAddedItem()
            throws SQLException {
        DataGenerator.createGarbage(connectionPool);
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM GARBAGE", connectionPool, "ID"));
        Object id = container.addItem();
        assertSame(id, container.firstItemId());
    }

    @Test
    public void isFirstId_freeformEmptyResultSet_returnsFirstAddedItem()
            throws SQLException {
        DataGenerator.createGarbage(connectionPool);
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM GARBAGE", connectionPool, "ID"));
        Object id = container.addItem();
        assertTrue(container.isFirstId(id));
    }

    @Test
    public void isLastId_freeformOneItemAdded_returnsTrueForAddedItem()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        Object id = container.addItem();
        assertTrue(container.isLastId(id));
    }

    @Test
    public void isLastId_freeformTwoItemsAdded_returnsTrueForLastAddedItem()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        container.addItem();
        Object id2 = container.addItem();
        assertTrue(container.isLastId(id2));
    }

    @Test
    public void getIdByIndex_freeformOneItemAddedLastIndexInContainer_returnsAddedItem()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        Object id = container.addItem();
        assertEquals(id, container.getIdByIndex(container.size() - 1));
    }

    @Test
    public void removeItem_freeformNoAddedItems_removesItemFromContainer()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        int size = container.size();
        Object id = container.firstItemId();
        assertTrue(container.removeItem(id));
        assertNotSame(id, container.firstItemId());
        assertEquals(size - 1, container.size());
    }

    @Test
    public void containsId_freeformRemovedItem_returnsFalse()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        Object id = container.firstItemId();
        assertTrue(container.removeItem(id));
        assertFalse(container.containsId(id));
    }

    @Test
    public void containsId_unknownObject() throws SQLException {

        Handler ensureNoLogging = new Handler() {

            @Override
            public void publish(LogRecord record) {
                fail("No messages should be logged");

            }

            @Override
            public void flush() {
            }

            @Override
            public void close() throws SecurityException {
            }
        };

        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        Logger logger = Logger.getLogger(SQLContainer.class.getName());

        logger.addHandler(ensureNoLogging);
        try {
            assertFalse(container.containsId(new Object()));
        } finally {
            logger.removeHandler(ensureNoLogging);
        }
    }

    @Test
    public void removeItem_freeformOneAddedItem_removesTheAddedItem()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        Object id = container.addItem();
        int size = container.size();
        assertTrue(container.removeItem(id));
        assertFalse(container.containsId(id));
        assertEquals(size - 1, container.size());
    }

    @Test
    public void getItem_freeformItemRemoved_returnsNull() throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        Object id = container.firstItemId();
        assertTrue(container.removeItem(id));
        assertNull(container.getItem(id));
    }

    @Test
    public void getItem_freeformAddedItemRemoved_returnsNull()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        Object id = container.addItem();
        assertNotNull(container.getItem(id));
        assertTrue(container.removeItem(id));
        assertNull(container.getItem(id));
    }

    @Test
    public void getItemIds_freeformItemRemoved_shouldNotContainRemovedItem()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        Object id = container.firstItemId();
        assertTrue(container.getItemIds().contains(id));
        assertTrue(container.removeItem(id));
        assertFalse(container.getItemIds().contains(id));
    }

    @Test
    public void getItemIds_freeformAddedItemRemoved_shouldNotContainRemovedItem()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        Object id = container.addItem();
        assertTrue(container.getItemIds().contains(id));
        assertTrue(container.removeItem(id));
        assertFalse(container.getItemIds().contains(id));
    }

    @Test
    public void containsId_freeformItemRemoved_returnsFalse()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        Object id = container.firstItemId();
        assertTrue(container.containsId(id));
        assertTrue(container.removeItem(id));
        assertFalse(container.containsId(id));
    }

    @Test
    public void containsId_freeformAddedItemRemoved_returnsFalse()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        Object id = container.addItem();
        assertTrue(container.containsId(id));
        assertTrue(container.removeItem(id));
        assertFalse(container.containsId(id));
    }

    @Test
    public void nextItemId_freeformItemRemoved_skipsRemovedItem()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        Object first = container.getIdByIndex(0);
        Object second = container.getIdByIndex(1);
        Object third = container.getIdByIndex(2);
        assertTrue(container.removeItem(second));
        assertEquals(third, container.nextItemId(first));
    }

    @Test
    public void nextItemId_freeformAddedItemRemoved_skipsRemovedItem()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        Object first = container.lastItemId();
        Object second = container.addItem();
        Object third = container.addItem();
        assertTrue(container.removeItem(second));
        assertEquals(third, container.nextItemId(first));
    }

    @Test
    public void prevItemId_freeformItemRemoved_skipsRemovedItem()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        Object first = container.getIdByIndex(0);
        Object second = container.getIdByIndex(1);
        Object third = container.getIdByIndex(2);
        assertTrue(container.removeItem(second));
        assertEquals(first, container.prevItemId(third));
    }

    @Test
    public void prevItemId_freeformAddedItemRemoved_skipsRemovedItem()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        Object first = container.lastItemId();
        Object second = container.addItem();
        Object third = container.addItem();
        assertTrue(container.removeItem(second));
        assertEquals(first, container.prevItemId(third));
    }

    @Test
    public void firstItemId_freeformFirstItemRemoved_resultChanges()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        Object first = container.firstItemId();
        assertTrue(container.removeItem(first));
        assertNotSame(first, container.firstItemId());
    }

    @Test
    public void firstItemId_freeformNewlyAddedFirstItemRemoved_resultChanges()
            throws SQLException {
        DataGenerator.createGarbage(connectionPool);
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM GARBAGE", connectionPool, "ID"));
        Object first = container.addItem();
        Object second = container.addItem();
        assertSame(first, container.firstItemId());
        assertTrue(container.removeItem(first));
        assertSame(second, container.firstItemId());
    }

    @Test
    public void lastItemId_freeformLastItemRemoved_resultChanges()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        Object last = container.lastItemId();
        assertTrue(container.removeItem(last));
        assertNotSame(last, container.lastItemId());
    }

    @Test
    public void lastItemId_freeformAddedLastItemRemoved_resultChanges()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        Object last = container.addItem();
        assertSame(last, container.lastItemId());
        assertTrue(container.removeItem(last));
        assertNotSame(last, container.lastItemId());
    }

    @Test
    public void isFirstId_freeformFirstItemRemoved_returnsFalse()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        Object first = container.firstItemId();
        assertTrue(container.removeItem(first));
        assertFalse(container.isFirstId(first));
    }

    @Test
    public void isFirstId_freeformAddedFirstItemRemoved_returnsFalse()
            throws SQLException {
        DataGenerator.createGarbage(connectionPool);
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM GARBAGE", connectionPool, "ID"));
        Object first = container.addItem();
        container.addItem();
        assertSame(first, container.firstItemId());
        assertTrue(container.removeItem(first));
        assertFalse(container.isFirstId(first));
    }

    @Test
    public void isLastId_freeformLastItemRemoved_returnsFalse()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        Object last = container.lastItemId();
        assertTrue(container.removeItem(last));
        assertFalse(container.isLastId(last));
    }

    @Test
    public void isLastId_freeformAddedLastItemRemoved_returnsFalse()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        Object last = container.addItem();
        assertSame(last, container.lastItemId());
        assertTrue(container.removeItem(last));
        assertFalse(container.isLastId(last));
    }

    @Test
    public void indexOfId_freeformItemRemoved_returnsNegOne()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        Object id = container.getIdByIndex(2);
        assertTrue(container.removeItem(id));
        assertEquals(-1, container.indexOfId(id));
    }

    @Test
    public void indexOfId_freeformAddedItemRemoved_returnsNegOne()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        Object id = container.addItem();
        assertTrue(container.indexOfId(id) != -1);
        assertTrue(container.removeItem(id));
        assertEquals(-1, container.indexOfId(id));
    }

    @Test
    public void getIdByIndex_freeformItemRemoved_resultChanges()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        Object id = container.getIdByIndex(2);
        assertTrue(container.removeItem(id));
        assertNotSame(id, container.getIdByIndex(2));
    }

    @Test
    public void getIdByIndex_freeformAddedItemRemoved_resultChanges()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        Object id = container.addItem();
        container.addItem();
        int index = container.indexOfId(id);
        assertTrue(container.removeItem(id));
        assertNotSame(id, container.getIdByIndex(index));
    }

    @Test
    public void removeAllItems_freeform_shouldSucceed() throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        assertTrue(container.removeAllItems());
        assertEquals(0, container.size());
    }

    @Test
    public void removeAllItems_freeformAddedItems_shouldSucceed()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        container.addItem();
        container.addItem();
        assertTrue(container.removeAllItems());
        assertEquals(0, container.size());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void commit_freeformAddedItem_shouldBeWrittenToDB()
            throws SQLException {
        FreeformQueryDelegate delegate = EasyMock
                .createMock(FreeformQueryDelegate.class);
        EasyMock.expect(delegate.storeRow(EasyMock.isA(Connection.class),
                EasyMock.isA(RowItem.class))).andAnswer(new IAnswer<Integer>() {
                    @Override
                    public Integer answer() throws Throwable {
                        Connection conn = (Connection) EasyMock
                                .getCurrentArguments()[0];
                        RowItem item = (RowItem) EasyMock
                                .getCurrentArguments()[1];
                        Statement statement = conn.createStatement();
                        if (SQLTestsConstants.db == DB.MSSQL) {
                            statement
                                    .executeUpdate("insert into people values('"
                                            + item.getItemProperty("NAME")
                                                    .getValue()
                                            + "', '"
                                            + item.getItemProperty("AGE")
                                                    .getValue()
                                            + "')");
                        } else {
                            statement.executeUpdate(
                                    "insert into people values(default, '"
                                            + item.getItemProperty("NAME")
                                                    .getValue()
                                            + "', '"
                                            + item.getItemProperty("AGE")
                                                    .getValue()
                                            + "')");
                        }
                        statement.close();
                        conn.commit();
                        connectionPool.releaseConnection(conn);
                        return 1;
                    }
                }).anyTimes();
        EasyMock.expect(
                delegate.getQueryString(EasyMock.anyInt(), EasyMock.anyInt()))
                .andAnswer(new IAnswer<String>() {
                    @Override
                    public String answer() throws Throwable {
                        Object[] args = EasyMock.getCurrentArguments();
                        int offset = (Integer) (args[0]);
                        int limit = (Integer) (args[1]);
                        if (SQLTestsConstants.db == DB.MSSQL) {
                            int start = offset + 1;
                            int end = offset + limit + 1;
                            String q = "SELECT * FROM (SELECT row_number() OVER"
                                    + " ( ORDER BY \"ID\" ASC) AS rownum, * FROM people)"
                                    + " AS a WHERE a.rownum BETWEEN " + start
                                    + " AND " + end;
                            return q;
                        } else if (SQLTestsConstants.db == DB.ORACLE) {
                            int start = offset + 1;
                            int end = offset + limit + 1;
                            String q = "SELECT * FROM (SELECT x.*, ROWNUM AS r FROM"
                                    + " (SELECT * FROM people ORDER BY \"ID\" ASC) x) "
                                    + " WHERE r BETWEEN " + start + " AND "
                                    + end;
                            return q;
                        } else {
                            return "SELECT * FROM people LIMIT " + limit
                                    + " OFFSET " + offset;
                        }
                    }
                }).anyTimes();
        delegate.setFilters(null);
        EasyMock.expectLastCall().anyTimes();
        delegate.setFilters(EasyMock.isA(List.class));
        EasyMock.expectLastCall().anyTimes();
        delegate.setOrderBy(null);
        EasyMock.expectLastCall().anyTimes();
        delegate.setOrderBy(EasyMock.isA(List.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(delegate.getCountQuery())
                .andThrow(new UnsupportedOperationException()).anyTimes();

        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                connectionPool, "ID");
        query.setDelegate(delegate);
        EasyMock.replay(delegate);
        SQLContainer container = new SQLContainer(query);
        Object id = container.addItem();
        container.getContainerProperty(id, "NAME").setValue("New Name");
        container.getContainerProperty(id, "AGE").setValue(30);
        assertTrue(id instanceof TemporaryRowId);
        assertSame(id, container.lastItemId());
        container.commit();
        assertFalse(container.lastItemId() instanceof TemporaryRowId);
        assertEquals("New Name",
                container.getContainerProperty(container.lastItemId(), "NAME")
                        .getValue());
        EasyMock.verify(delegate);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void commit_freeformTwoAddedItems_shouldBeWrittenToDB()
            throws SQLException {
        FreeformQueryDelegate delegate = EasyMock
                .createMock(FreeformQueryDelegate.class);
        EasyMock.expect(delegate.storeRow(EasyMock.isA(Connection.class),
                EasyMock.isA(RowItem.class))).andAnswer(new IAnswer<Integer>() {
                    @Override
                    public Integer answer() throws Throwable {
                        Connection conn = (Connection) EasyMock
                                .getCurrentArguments()[0];
                        RowItem item = (RowItem) EasyMock
                                .getCurrentArguments()[1];
                        Statement statement = conn.createStatement();
                        if (SQLTestsConstants.db == DB.MSSQL) {
                            statement
                                    .executeUpdate("insert into people values('"
                                            + item.getItemProperty("NAME")
                                                    .getValue()
                                            + "', '"
                                            + item.getItemProperty("AGE")
                                                    .getValue()
                                            + "')");
                        } else {
                            statement.executeUpdate(
                                    "insert into people values(default, '"
                                            + item.getItemProperty("NAME")
                                                    .getValue()
                                            + "', '"
                                            + item.getItemProperty("AGE")
                                                    .getValue()
                                            + "')");
                        }
                        statement.close();
                        conn.commit();
                        connectionPool.releaseConnection(conn);
                        return 1;
                    }
                }).anyTimes();
        EasyMock.expect(
                delegate.getQueryString(EasyMock.anyInt(), EasyMock.anyInt()))
                .andAnswer(new IAnswer<String>() {
                    @Override
                    public String answer() throws Throwable {
                        Object[] args = EasyMock.getCurrentArguments();
                        int offset = (Integer) (args[0]);
                        int limit = (Integer) (args[1]);
                        if (SQLTestsConstants.db == DB.MSSQL) {
                            int start = offset + 1;
                            int end = offset + limit + 1;
                            String q = "SELECT * FROM (SELECT row_number() OVER"
                                    + " ( ORDER BY \"ID\" ASC) AS rownum, * FROM people)"
                                    + " AS a WHERE a.rownum BETWEEN " + start
                                    + " AND " + end;
                            return q;
                        } else if (SQLTestsConstants.db == DB.ORACLE) {
                            int start = offset + 1;
                            int end = offset + limit + 1;
                            String q = "SELECT * FROM (SELECT x.*, ROWNUM AS r FROM"
                                    + " (SELECT * FROM people ORDER BY \"ID\" ASC) x) "
                                    + " WHERE r BETWEEN " + start + " AND "
                                    + end;
                            return q;
                        } else {
                            return "SELECT * FROM people LIMIT " + limit
                                    + " OFFSET " + offset;
                        }
                    }
                }).anyTimes();
        delegate.setFilters(null);
        EasyMock.expectLastCall().anyTimes();
        delegate.setFilters(EasyMock.isA(List.class));
        EasyMock.expectLastCall().anyTimes();
        delegate.setOrderBy(null);
        EasyMock.expectLastCall().anyTimes();
        delegate.setOrderBy(EasyMock.isA(List.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(delegate.getCountQuery())
                .andThrow(new UnsupportedOperationException()).anyTimes();

        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                connectionPool, "ID");
        query.setDelegate(delegate);
        EasyMock.replay(delegate);
        SQLContainer container = new SQLContainer(query);
        Object id = container.addItem();
        Object id2 = container.addItem();
        container.getContainerProperty(id, "NAME").setValue("Herbert");
        container.getContainerProperty(id, "AGE").setValue(30);
        container.getContainerProperty(id2, "NAME").setValue("Larry");
        container.getContainerProperty(id2, "AGE").setValue(50);
        assertTrue(id2 instanceof TemporaryRowId);
        assertSame(id2, container.lastItemId());
        container.commit();
        Object nextToLast = container.getIdByIndex(container.size() - 2);
        assertFalse(nextToLast instanceof TemporaryRowId);
        assertEquals("Herbert",
                container.getContainerProperty(nextToLast, "NAME").getValue());
        assertFalse(container.lastItemId() instanceof TemporaryRowId);
        assertEquals("Larry",
                container.getContainerProperty(container.lastItemId(), "NAME")
                        .getValue());
        EasyMock.verify(delegate);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void commit_freeformRemovedItem_shouldBeRemovedFromDB()
            throws SQLException {
        FreeformQueryDelegate delegate = EasyMock
                .createMock(FreeformQueryDelegate.class);
        EasyMock.expect(delegate.removeRow(EasyMock.isA(Connection.class),
                EasyMock.isA(RowItem.class))).andAnswer(new IAnswer<Boolean>() {
                    @Override
                    public Boolean answer() throws Throwable {
                        Connection conn = (Connection) EasyMock
                                .getCurrentArguments()[0];
                        RowItem item = (RowItem) EasyMock
                                .getCurrentArguments()[1];
                        Statement statement = conn.createStatement();
                        statement.executeUpdate(
                                "DELETE FROM people WHERE \"ID\"=" + item
                                        .getItemProperty("ID").getValue());
                        statement.close();
                        return true;
                    }
                }).anyTimes();
        EasyMock.expect(
                delegate.getQueryString(EasyMock.anyInt(), EasyMock.anyInt()))
                .andAnswer(new IAnswer<String>() {
                    @Override
                    public String answer() throws Throwable {
                        Object[] args = EasyMock.getCurrentArguments();
                        int offset = (Integer) (args[0]);
                        int limit = (Integer) (args[1]);
                        if (SQLTestsConstants.db == DB.MSSQL) {
                            int start = offset + 1;
                            int end = offset + limit + 1;
                            String q = "SELECT * FROM (SELECT row_number() OVER"
                                    + " ( ORDER BY \"ID\" ASC) AS rownum, * FROM people)"
                                    + " AS a WHERE a.rownum BETWEEN " + start
                                    + " AND " + end;
                            return q;
                        } else if (SQLTestsConstants.db == DB.ORACLE) {
                            int start = offset + 1;
                            int end = offset + limit + 1;
                            String q = "SELECT * FROM (SELECT x.*, ROWNUM AS r FROM"
                                    + " (SELECT * FROM people ORDER BY \"ID\" ASC) x) "
                                    + " WHERE r BETWEEN " + start + " AND "
                                    + end;
                            return q;
                        } else {
                            return "SELECT * FROM people LIMIT " + limit
                                    + " OFFSET " + offset;
                        }
                    }
                }).anyTimes();
        delegate.setFilters(null);
        EasyMock.expectLastCall().anyTimes();
        delegate.setFilters(EasyMock.isA(List.class));
        EasyMock.expectLastCall().anyTimes();
        delegate.setOrderBy(null);
        EasyMock.expectLastCall().anyTimes();
        delegate.setOrderBy(EasyMock.isA(List.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(delegate.getCountQuery())
                .andThrow(new UnsupportedOperationException()).anyTimes();

        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                connectionPool, "ID");
        query.setDelegate(delegate);
        EasyMock.replay(delegate);
        SQLContainer container = new SQLContainer(query);
        Object last = container.lastItemId();
        container.removeItem(last);
        container.commit();
        assertFalse(last.equals(container.lastItemId()));
        EasyMock.verify(delegate);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void commit_freeformLastItemUpdated_shouldUpdateRowInDB()
            throws SQLException {
        FreeformQueryDelegate delegate = EasyMock
                .createMock(FreeformQueryDelegate.class);
        EasyMock.expect(delegate.storeRow(EasyMock.isA(Connection.class),
                EasyMock.isA(RowItem.class))).andAnswer(new IAnswer<Integer>() {
                    @Override
                    public Integer answer() throws Throwable {
                        Connection conn = (Connection) EasyMock
                                .getCurrentArguments()[0];
                        RowItem item = (RowItem) EasyMock
                                .getCurrentArguments()[1];
                        Statement statement = conn.createStatement();
                        statement.executeUpdate("UPDATE people SET \"NAME\"='"
                                + item.getItemProperty("NAME").getValue()
                                + "' WHERE \"ID\"="
                                + item.getItemProperty("ID").getValue());
                        statement.close();
                        conn.commit();
                        connectionPool.releaseConnection(conn);
                        return 1;
                    }
                }).anyTimes();
        EasyMock.expect(
                delegate.getQueryString(EasyMock.anyInt(), EasyMock.anyInt()))
                .andAnswer(new IAnswer<String>() {
                    @Override
                    public String answer() throws Throwable {
                        Object[] args = EasyMock.getCurrentArguments();
                        int offset = (Integer) (args[0]);
                        int limit = (Integer) (args[1]);
                        if (SQLTestsConstants.db == DB.MSSQL) {
                            int start = offset + 1;
                            int end = offset + limit + 1;
                            String q = "SELECT * FROM (SELECT row_number() OVER"
                                    + " ( ORDER BY \"ID\" ASC) AS rownum, * FROM people)"
                                    + " AS a WHERE a.rownum BETWEEN " + start
                                    + " AND " + end;
                            return q;
                        } else if (SQLTestsConstants.db == DB.ORACLE) {
                            int start = offset + 1;
                            int end = offset + limit + 1;
                            String q = "SELECT * FROM (SELECT x.*, ROWNUM AS r FROM"
                                    + " (SELECT * FROM people ORDER BY \"ID\" ASC) x) "
                                    + " WHERE r BETWEEN " + start + " AND "
                                    + end;
                            return q;
                        } else {
                            return "SELECT * FROM people LIMIT " + limit
                                    + " OFFSET " + offset;
                        }
                    }
                }).anyTimes();
        delegate.setFilters(null);
        EasyMock.expectLastCall().anyTimes();
        delegate.setFilters(EasyMock.isA(List.class));
        EasyMock.expectLastCall().anyTimes();
        delegate.setOrderBy(null);
        EasyMock.expectLastCall().anyTimes();
        delegate.setOrderBy(EasyMock.isA(List.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(delegate.getCountQuery())
                .andThrow(new UnsupportedOperationException()).anyTimes();

        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                connectionPool, "ID");
        query.setDelegate(delegate);
        EasyMock.replay(delegate);
        SQLContainer container = new SQLContainer(query);
        Object last = container.lastItemId();
        container.getContainerProperty(last, "NAME").setValue("Donald");
        container.commit();
        assertEquals("Donald",
                container.getContainerProperty(container.lastItemId(), "NAME")
                        .getValue());
        EasyMock.verify(delegate);
    }

    @Test
    public void rollback_freeformItemAdded_discardsAddedItem()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        int size = container.size();
        Object id = container.addItem();
        container.getContainerProperty(id, "NAME").setValue("foo");
        assertEquals(size + 1, container.size());
        container.rollback();
        assertEquals(size, container.size());
        assertFalse("foo".equals(
                container.getContainerProperty(container.lastItemId(), "NAME")
                        .getValue()));
    }

    @Test
    public void rollback_freeformItemRemoved_restoresRemovedItem()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        int size = container.size();
        Object last = container.lastItemId();
        container.removeItem(last);
        assertEquals(size - 1, container.size());
        container.rollback();
        assertEquals(size, container.size());
        assertEquals(last, container.lastItemId());
    }

    @Test
    public void rollback_freeformItemChanged_discardsChanges()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        Object last = container.lastItemId();
        container.getContainerProperty(last, "NAME").setValue("foo");
        container.rollback();
        assertFalse("foo".equals(
                container.getContainerProperty(container.lastItemId(), "NAME")
                        .getValue()));
    }

    @Test
    public void itemChangeNotification_freeform_isModifiedReturnsTrue()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        assertFalse(container.isModified());
        RowItem last = (RowItem) container.getItem(container.lastItemId());
        container.itemChangeNotification(last);
        assertTrue(container.isModified());
    }

    @Test
    public void itemSetChangeListeners_freeform_shouldFire()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        ItemSetChangeListener listener = EasyMock
                .createMock(ItemSetChangeListener.class);
        listener.containerItemSetChange(EasyMock.isA(ItemSetChangeEvent.class));
        EasyMock.replay(listener);

        container.addListener(listener);
        container.addItem();

        EasyMock.verify(listener);
    }

    @Test
    public void itemSetChangeListeners_freeformItemRemoved_shouldFire()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        ItemSetChangeListener listener = EasyMock
                .createMock(ItemSetChangeListener.class);
        listener.containerItemSetChange(EasyMock.isA(ItemSetChangeEvent.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.replay(listener);

        container.addListener(listener);
        container.removeItem(container.lastItemId());

        EasyMock.verify(listener);
    }

    @Test
    public void removeListener_freeform_shouldNotFire() throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        ItemSetChangeListener listener = EasyMock
                .createMock(ItemSetChangeListener.class);
        EasyMock.replay(listener);

        container.addListener(listener);
        container.removeListener(listener);
        container.addItem();

        EasyMock.verify(listener);
    }

    @Test
    public void isModified_freeformRemovedItem_returnsTrue()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        assertFalse(container.isModified());
        container.removeItem(container.lastItemId());
        assertTrue(container.isModified());
    }

    @Test
    public void isModified_freeformAddedItem_returnsTrue() throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        assertFalse(container.isModified());
        container.addItem();
        assertTrue(container.isModified());
    }

    @Test
    public void isModified_freeformChangedItem_returnsTrue()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        assertFalse(container.isModified());
        container.getContainerProperty(container.lastItemId(), "NAME")
                .setValue("foo");
        assertTrue(container.isModified());
    }

    @Test
    public void getSortableContainerPropertyIds_freeform_returnsAllPropertyIds()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        Collection<?> sortableIds = container.getSortableContainerPropertyIds();
        assertTrue(sortableIds.contains("ID"));
        assertTrue(sortableIds.contains("NAME"));
        assertTrue(sortableIds.contains("AGE"));
        assertEquals(3, sortableIds.size());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void addOrderBy_freeform_shouldReorderResults() throws SQLException {
        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                connectionPool, "ID");
        FreeformQueryDelegate delegate = EasyMock
                .createMock(FreeformQueryDelegate.class);
        final List<OrderBy> orderBys = new ArrayList<OrderBy>();
        delegate.setFilters(null);
        EasyMock.expectLastCall().anyTimes();
        delegate.setFilters(EasyMock.isA(List.class));
        EasyMock.expectLastCall().anyTimes();
        delegate.setOrderBy(null);
        EasyMock.expectLastCall().anyTimes();
        delegate.setOrderBy(EasyMock.isA(List.class));
        EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
            @Override
            public Object answer() throws Throwable {
                List<OrderBy> orders = (List<OrderBy>) EasyMock
                        .getCurrentArguments()[0];
                orderBys.clear();
                orderBys.addAll(orders);
                return null;
            }
        }).anyTimes();
        EasyMock.expect(
                delegate.getQueryString(EasyMock.anyInt(), EasyMock.anyInt()))
                .andAnswer(new IAnswer<String>() {
                    @Override
                    public String answer() throws Throwable {
                        Object[] args = EasyMock.getCurrentArguments();
                        int offset = (Integer) (args[0]);
                        int limit = (Integer) (args[1]);
                        if (SQLTestsConstants.db == DB.MSSQL) {
                            SQLGenerator gen = new MSSQLGenerator();
                            if (orderBys == null || orderBys.isEmpty()) {
                                List<OrderBy> ob = new ArrayList<OrderBy>();
                                ob.add(new OrderBy("ID", true));
                                return gen
                                        .generateSelectQuery("people", null, ob,
                                                offset, limit, null)
                                        .getQueryString();
                            } else {
                                return gen
                                        .generateSelectQuery("people", null,
                                                orderBys, offset, limit, null)
                                        .getQueryString();
                            }
                        } else if (SQLTestsConstants.db == DB.ORACLE) {
                            SQLGenerator gen = new OracleGenerator();
                            if (orderBys == null || orderBys.isEmpty()) {
                                List<OrderBy> ob = new ArrayList<OrderBy>();
                                ob.add(new OrderBy("ID", true));
                                return gen
                                        .generateSelectQuery("people", null, ob,
                                                offset, limit, null)
                                        .getQueryString();
                            } else {
                                return gen
                                        .generateSelectQuery("people", null,
                                                orderBys, offset, limit, null)
                                        .getQueryString();
                            }
                        } else {
                            StringBuffer query = new StringBuffer(
                                    "SELECT * FROM people");
                            if (!orderBys.isEmpty()) {
                                query.append(" ORDER BY ");
                                for (OrderBy orderBy : orderBys) {
                                    query.append(
                                            "\"" + orderBy.getColumn() + "\"");
                                    if (orderBy.isAscending()) {
                                        query.append(" ASC");
                                    } else {
                                        query.append(" DESC");
                                    }
                                }
                            }
                            query.append(" LIMIT ").append(limit)
                                    .append(" OFFSET ").append(offset);
                            return query.toString();
                        }
                    }
                }).anyTimes();
        EasyMock.expect(delegate.getCountQuery())
                .andThrow(new UnsupportedOperationException()).anyTimes();

        EasyMock.replay(delegate);
        query.setDelegate(delegate);
        SQLContainer container = new SQLContainer(query);
        // Ville, Kalle, Pelle, Börje
        assertEquals("Ville",
                container.getContainerProperty(container.firstItemId(), "NAME")
                        .getValue());
        assertEquals("Börje",
                container.getContainerProperty(container.lastItemId(), "NAME")
                        .getValue());

        container.addOrderBy(new OrderBy("NAME", true));
        // Börje, Kalle, Pelle, Ville
        assertEquals("Börje",
                container.getContainerProperty(container.firstItemId(), "NAME")
                        .getValue());
        assertEquals("Ville",
                container.getContainerProperty(container.lastItemId(), "NAME")
                        .getValue());

        EasyMock.verify(delegate);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addOrderBy_freeformIllegalColumn_shouldFail()
            throws SQLException {
        SQLContainer container = new SQLContainer(new FreeformQuery(
                "SELECT * FROM people", connectionPool, "ID"));
        container.addOrderBy(new OrderBy("asdf", true));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void sort_freeform_sortsByName() throws SQLException {
        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                connectionPool, "ID");
        FreeformQueryDelegate delegate = EasyMock
                .createMock(FreeformQueryDelegate.class);
        final List<OrderBy> orderBys = new ArrayList<OrderBy>();
        delegate.setFilters(null);
        EasyMock.expectLastCall().anyTimes();
        delegate.setFilters(EasyMock.isA(List.class));
        EasyMock.expectLastCall().anyTimes();
        delegate.setOrderBy(null);
        EasyMock.expectLastCall().anyTimes();
        delegate.setOrderBy(EasyMock.isA(List.class));
        EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
            @Override
            public Object answer() throws Throwable {
                List<OrderBy> orders = (List<OrderBy>) EasyMock
                        .getCurrentArguments()[0];
                orderBys.clear();
                orderBys.addAll(orders);
                return null;
            }
        }).anyTimes();
        EasyMock.expect(
                delegate.getQueryString(EasyMock.anyInt(), EasyMock.anyInt()))
                .andAnswer(new IAnswer<String>() {
                    @Override
                    public String answer() throws Throwable {
                        Object[] args = EasyMock.getCurrentArguments();
                        int offset = (Integer) (args[0]);
                        int limit = (Integer) (args[1]);
                        if (SQLTestsConstants.db == DB.MSSQL) {
                            SQLGenerator gen = new MSSQLGenerator();
                            if (orderBys == null || orderBys.isEmpty()) {
                                List<OrderBy> ob = new ArrayList<OrderBy>();
                                ob.add(new OrderBy("ID", true));
                                return gen
                                        .generateSelectQuery("people", null, ob,
                                                offset, limit, null)
                                        .getQueryString();
                            } else {
                                return gen
                                        .generateSelectQuery("people", null,
                                                orderBys, offset, limit, null)
                                        .getQueryString();
                            }
                        } else if (SQLTestsConstants.db == DB.ORACLE) {
                            SQLGenerator gen = new OracleGenerator();
                            if (orderBys == null || orderBys.isEmpty()) {
                                List<OrderBy> ob = new ArrayList<OrderBy>();
                                ob.add(new OrderBy("ID", true));
                                return gen
                                        .generateSelectQuery("people", null, ob,
                                                offset, limit, null)
                                        .getQueryString();
                            } else {
                                return gen
                                        .generateSelectQuery("people", null,
                                                orderBys, offset, limit, null)
                                        .getQueryString();
                            }
                        } else {
                            StringBuffer query = new StringBuffer(
                                    "SELECT * FROM people");
                            if (!orderBys.isEmpty()) {
                                query.append(" ORDER BY ");
                                for (OrderBy orderBy : orderBys) {
                                    query.append(
                                            "\"" + orderBy.getColumn() + "\"");
                                    if (orderBy.isAscending()) {
                                        query.append(" ASC");
                                    } else {
                                        query.append(" DESC");
                                    }
                                }
                            }
                            query.append(" LIMIT ").append(limit)
                                    .append(" OFFSET ").append(offset);
                            return query.toString();
                        }
                    }
                }).anyTimes();
        EasyMock.expect(delegate.getCountQuery())
                .andThrow(new UnsupportedOperationException()).anyTimes();
        EasyMock.replay(delegate);

        query.setDelegate(delegate);
        SQLContainer container = new SQLContainer(query);
        // Ville, Kalle, Pelle, Börje
        assertEquals("Ville",
                container.getContainerProperty(container.firstItemId(), "NAME")
                        .getValue());
        assertEquals("Börje",
                container.getContainerProperty(container.lastItemId(), "NAME")
                        .getValue());

        container.sort(new Object[] { "NAME" }, new boolean[] { true });

        // Börje, Kalle, Pelle, Ville
        assertEquals("Börje",
                container.getContainerProperty(container.firstItemId(), "NAME")
                        .getValue());
        assertEquals("Ville",
                container.getContainerProperty(container.lastItemId(), "NAME")
                        .getValue());

        EasyMock.verify(delegate);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void addFilter_freeform_filtersResults() throws SQLException {
        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                connectionPool, "ID");
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
                        StringBuffer query = new StringBuffer(
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

        container.addContainerFilter(new Like("NAME", "%lle"));
        // Ville, Kalle, Pelle
        assertEquals(3, container.size());
        assertEquals("Pelle",
                container.getContainerProperty(container.lastItemId(), "NAME")
                        .getValue());

        EasyMock.verify(delegate);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void addContainerFilter_filtersResults() throws SQLException {
        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                connectionPool, "ID");
        FreeformStatementDelegate delegate = EasyMock
                .createMock(FreeformStatementDelegate.class);
        final List<Filter> filters = new ArrayList<Filter>();
        delegate.setFilters(null);
        EasyMock.expectLastCall().anyTimes();
        delegate.setOrderBy(null);
        EasyMock.expectLastCall().anyTimes();
        delegate.setOrderBy(EasyMock.isA(List.class));
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
                        StringBuffer query = new StringBuffer(
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

        container.addContainerFilter("NAME", "Vi", false, false);

        // Ville
        assertEquals(1, container.size());
        assertEquals("Ville",
                container.getContainerProperty(container.lastItemId(), "NAME")
                        .getValue());

        EasyMock.verify(delegate);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void addContainerFilter_ignoreCase_filtersResults()
            throws SQLException {
        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                connectionPool, "ID");
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
                        StringBuffer query = new StringBuffer(
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

        // FIXME LIKE %asdf% doesn't match a string that begins with asdf
        container.addContainerFilter("NAME", "vi", true, true);

        // Ville
        assertEquals(1, container.size());
        assertEquals("Ville",
                container.getContainerProperty(container.lastItemId(), "NAME")
                        .getValue());

        EasyMock.verify(delegate);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void removeAllContainerFilters_freeform_noFiltering()
            throws SQLException {
        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                connectionPool, "ID");
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
                        StringBuffer query = new StringBuffer(
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

        container.addContainerFilter("NAME", "Vi", false, false);

        // Ville
        assertEquals(1, container.size());
        assertEquals("Ville",
                container.getContainerProperty(container.lastItemId(), "NAME")
                        .getValue());

        container.removeAllContainerFilters();

        assertEquals(4, container.size());
        assertEquals("Börje",
                container.getContainerProperty(container.lastItemId(), "NAME")
                        .getValue());

        EasyMock.verify(delegate);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void removeContainerFilters_freeform_noFiltering()
            throws SQLException {
        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                connectionPool, "ID");
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
                        StringBuffer query = new StringBuffer(
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

        container.addContainerFilter("NAME", "Vi", false, true);

        // Ville
        assertEquals(1, container.size());
        assertEquals("Ville",
                container.getContainerProperty(container.lastItemId(), "NAME")
                        .getValue());

        container.removeContainerFilters("NAME");

        assertEquals(4, container.size());
        assertEquals("Börje",
                container.getContainerProperty(container.lastItemId(), "NAME")
                        .getValue());

        EasyMock.verify(delegate);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void addFilter_freeformBufferedItems_alsoFiltersBufferedItems()
            throws SQLException {
        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                connectionPool, "ID");
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
                        StringBuffer query = new StringBuffer(
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

        Object id1 = container.addItem();
        container.getContainerProperty(id1, "NAME").setValue("Palle");
        Object id2 = container.addItem();
        container.getContainerProperty(id2, "NAME").setValue("Bengt");

        container.addContainerFilter(new Like("NAME", "%lle"));

        // Ville, Kalle, Pelle, Palle
        assertEquals(4, container.size());
        assertEquals("Ville",
                container
                        .getContainerProperty(container.getIdByIndex(0), "NAME")
                        .getValue());
        assertEquals("Kalle",
                container
                        .getContainerProperty(container.getIdByIndex(1), "NAME")
                        .getValue());
        assertEquals("Pelle",
                container
                        .getContainerProperty(container.getIdByIndex(2), "NAME")
                        .getValue());
        assertEquals("Palle",
                container
                        .getContainerProperty(container.getIdByIndex(3), "NAME")
                        .getValue());

        try {
            container.getIdByIndex(4);
            fail("SQLContainer.getIdByIndex() returned a value for an index beyond the end of the container");
        } catch (IndexOutOfBoundsException e) {
            // should throw exception - item is filtered out
        }
        container.nextItemId(container.getIdByIndex(3));

        assertFalse(container.containsId(id2));
        assertFalse(container.getItemIds().contains(id2));

        assertNull(container.getItem(id2));
        assertEquals(-1, container.indexOfId(id2));

        assertNotSame(id2, container.lastItemId());
        assertSame(id1, container.lastItemId());

        EasyMock.verify(delegate);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void sort_freeformBufferedItems_sortsBufferedItemsLastInOrderAdded()
            throws SQLException {
        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                connectionPool, "ID");
        FreeformQueryDelegate delegate = EasyMock
                .createMock(FreeformQueryDelegate.class);
        final List<OrderBy> orderBys = new ArrayList<OrderBy>();
        delegate.setFilters(null);
        EasyMock.expectLastCall().anyTimes();
        delegate.setFilters(EasyMock.isA(List.class));
        EasyMock.expectLastCall().anyTimes();
        delegate.setOrderBy(null);
        EasyMock.expectLastCall().anyTimes();
        delegate.setOrderBy(EasyMock.isA(List.class));
        EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
            @Override
            public Object answer() throws Throwable {
                List<OrderBy> orders = (List<OrderBy>) EasyMock
                        .getCurrentArguments()[0];
                orderBys.clear();
                orderBys.addAll(orders);
                return null;
            }
        }).anyTimes();
        EasyMock.expect(
                delegate.getQueryString(EasyMock.anyInt(), EasyMock.anyInt()))
                .andAnswer(new IAnswer<String>() {
                    @Override
                    public String answer() throws Throwable {
                        Object[] args = EasyMock.getCurrentArguments();
                        int offset = (Integer) (args[0]);
                        int limit = (Integer) (args[1]);
                        if (SQLTestsConstants.db == DB.MSSQL) {
                            SQLGenerator gen = new MSSQLGenerator();
                            if (orderBys == null || orderBys.isEmpty()) {
                                List<OrderBy> ob = new ArrayList<OrderBy>();
                                ob.add(new OrderBy("ID", true));
                                return gen
                                        .generateSelectQuery("people", null, ob,
                                                offset, limit, null)
                                        .getQueryString();
                            } else {
                                return gen
                                        .generateSelectQuery("people", null,
                                                orderBys, offset, limit, null)
                                        .getQueryString();
                            }
                        } else if (SQLTestsConstants.db == DB.ORACLE) {
                            SQLGenerator gen = new OracleGenerator();
                            if (orderBys == null || orderBys.isEmpty()) {
                                List<OrderBy> ob = new ArrayList<OrderBy>();
                                ob.add(new OrderBy("ID", true));
                                return gen
                                        .generateSelectQuery("people", null, ob,
                                                offset, limit, null)
                                        .getQueryString();
                            } else {
                                return gen
                                        .generateSelectQuery("people", null,
                                                orderBys, offset, limit, null)
                                        .getQueryString();
                            }
                        } else {
                            StringBuffer query = new StringBuffer(
                                    "SELECT * FROM people");
                            if (!orderBys.isEmpty()) {
                                query.append(" ORDER BY ");
                                for (OrderBy orderBy : orderBys) {
                                    query.append(
                                            "\"" + orderBy.getColumn() + "\"");
                                    if (orderBy.isAscending()) {
                                        query.append(" ASC");
                                    } else {
                                        query.append(" DESC");
                                    }
                                }
                            }
                            query.append(" LIMIT ").append(limit)
                                    .append(" OFFSET ").append(offset);
                            return query.toString();
                        }
                    }
                }).anyTimes();
        EasyMock.expect(delegate.getCountQuery())
                .andThrow(new UnsupportedOperationException()).anyTimes();
        EasyMock.replay(delegate);

        query.setDelegate(delegate);
        SQLContainer container = new SQLContainer(query);
        // Ville, Kalle, Pelle, Börje
        assertEquals("Ville",
                container.getContainerProperty(container.firstItemId(), "NAME")
                        .getValue());
        assertEquals("Börje",
                container.getContainerProperty(container.lastItemId(), "NAME")
                        .getValue());

        Object id1 = container.addItem();
        container.getContainerProperty(id1, "NAME").setValue("Wilbert");
        Object id2 = container.addItem();
        container.getContainerProperty(id2, "NAME").setValue("Albert");

        container.sort(new Object[] { "NAME" }, new boolean[] { true });

        // Börje, Kalle, Pelle, Ville, Wilbert, Albert
        assertEquals("Börje",
                container.getContainerProperty(container.firstItemId(), "NAME")
                        .getValue());
        assertEquals("Wilbert",
                container.getContainerProperty(
                        container.getIdByIndex(container.size() - 2), "NAME")
                        .getValue());
        assertEquals("Albert",
                container.getContainerProperty(container.lastItemId(), "NAME")
                        .getValue());

        EasyMock.verify(delegate);
    }

}

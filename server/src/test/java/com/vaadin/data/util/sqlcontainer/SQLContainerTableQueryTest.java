package com.vaadin.data.util.sqlcontainer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.ItemSetChangeListener;
import com.vaadin.data.Item;
import com.vaadin.data.util.filter.Like;
import com.vaadin.data.util.sqlcontainer.SQLTestsConstants.DB;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.data.util.sqlcontainer.query.ValidatingSimpleJDBCConnectionPool;

public class SQLContainerTableQueryTest {

    private static final int offset = SQLTestsConstants.offset;
    private final int numberOfRowsInContainer = 4;
    private final int numberOfPropertiesInContainer = 3;
    private final String NAME = "NAME";
    private final String ID = "ID";
    private final String AGE = "AGE";
    private JDBCConnectionPool connectionPool;
    private TableQuery query;
    private SQLContainer container;
    private final RowId existingItemId = getRowId(1);
    private final RowId nonExistingItemId = getRowId(1337);

    @Before
    public void setUp() throws SQLException {

        try {
            connectionPool = new ValidatingSimpleJDBCConnectionPool(
                    SQLTestsConstants.dbDriver, SQLTestsConstants.dbURL,
                    SQLTestsConstants.dbUser, SQLTestsConstants.dbPwd, 2, 2);
        } catch (SQLException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        DataGenerator.addPeopleToDatabase(connectionPool);

        query = getTableQuery("people");
        container = new SQLContainer(query);
    }

    private TableQuery getTableQuery(String tableName) {
        return new TableQuery(tableName, connectionPool,
                SQLTestsConstants.sqlGen);
    }

    private SQLContainer getGarbageContainer() throws SQLException {
        DataGenerator.createGarbage(connectionPool);

        return new SQLContainer(getTableQuery("garbage"));
    }

    private Item getItem(Object id) {
        return container.getItem(id);
    }

    private RowId getRowId(int id) {
        return new RowId(new Object[] { id + offset });
    }

    @After
    public void tearDown() {
        if (connectionPool != null) {
            connectionPool.destroy();
        }
    }

    @Test
    public void itemWithExistingVersionColumnIsRemoved() throws SQLException {
        container.setAutoCommit(true);
        query.setVersionColumn(ID);

        assertTrue(container.removeItem(container.lastItemId()));
    }

    @Test(expected = SQLException.class)
    public void itemWithNonExistingVersionColumnCannotBeRemoved()
            throws SQLException {
        query.setVersionColumn("version");

        container.removeItem(container.lastItemId());

        container.commit();
    }

    @Test
    public void containerContainsId() {
        assertTrue(container.containsId(existingItemId));
    }

    @Test
    public void containerDoesNotContainId() {
        assertFalse(container.containsId(nonExistingItemId));
    }

    @Test
    public void idPropertyHasCorrectType() {
        if (SQLTestsConstants.db == DB.ORACLE) {
            assertEquals(container.getType(ID), BigDecimal.class);
        } else {
            assertEquals(container.getType(ID), Integer.class);
        }
    }

    @Test
    public void namePropertyHasCorrectType() {
        assertEquals(container.getType(NAME), String.class);
    }

    @Test
    public void nonExistingPropertyDoesNotHaveType() {
        assertThat(container.getType("adsf"), is(nullValue()));
    }

    @Test
    public void sizeIsReturnedCorrectly() {
        assertEquals(numberOfRowsInContainer, container.size());
    }

    @Test
    public void propertyIsFetchedForExistingItem() {
        assertThat(container.getContainerProperty(existingItemId, NAME)
                .getValue().toString(), is("Kalle"));
    }

    @Test
    public void containerDoesNotContainPropertyForExistingItem() {
        assertThat(container.getContainerProperty(existingItemId, "asdf"),
                is(nullValue()));
    }

    @Test
    public void containerDoesNotContainExistingPropertyForNonExistingItem() {
        assertThat(container.getContainerProperty(nonExistingItemId, NAME),
                is(nullValue()));
    }

    @Test
    public void propertyIdsAreFetched() {
        ArrayList<String> propertyIds = new ArrayList<String>(
                (Collection<? extends String>) container
                        .getContainerPropertyIds());

        assertThat(propertyIds.size(), is(numberOfPropertiesInContainer));
        assertThat(propertyIds, hasItems(ID, NAME, AGE));
    }

    @Test
    public void existingItemIsFetched() {
        Item item = container.getItem(existingItemId);

        assertThat(item.getItemProperty(NAME).getValue().toString(),
                is("Kalle"));
    }

    @Test
    public void newItemIsAdded() throws SQLException {
        Object id = container.addItem();
        getItem(id).getItemProperty(NAME).setValue("foo");

        container.commit();

        Item item = getItem(container.lastItemId());
        assertThat(item.getItemProperty(NAME).getValue().toString(), is("foo"));
    }

    @Test
    public void itemPropertyIsNotRevertedOnRefresh() {
        getItem(existingItemId).getItemProperty(NAME).setValue("foo");

        container.refresh();

        assertThat(getItem(existingItemId).getItemProperty(NAME).toString(),
                is("foo"));
    }

    @Test
    public void correctItemIsFetchedFromMultipleRows() throws SQLException {
        DataGenerator.addFiveThousandPeople(connectionPool);

        Item item = container.getItem(getRowId(1337));

        assertThat((Integer) item.getItemProperty(ID).getValue(),
                is(equalTo(1337 + offset)));
        assertThat(item.getItemProperty(NAME).getValue().toString(),
                is("Person 1337"));
    }

    @Test
    public void getItemIds_table_returnsItemIdsWithKeys0through3()
            throws SQLException {
        Collection<?> itemIds = container.getItemIds();
        assertEquals(4, itemIds.size());
        RowId zero = new RowId(new Object[] { 0 + offset });
        RowId one = new RowId(new Object[] { 1 + offset });
        RowId two = new RowId(new Object[] { 2 + offset });
        RowId three = new RowId(new Object[] { 3 + offset });
        if (SQLTestsConstants.db == DB.ORACLE) {
            String[] correct = new String[] { "1", "2", "3", "4" };
            List<String> oracle = new ArrayList<String>();
            for (Object o : itemIds) {
                oracle.add(o.toString());
            }
            Assert.assertArrayEquals(correct, oracle.toArray());
        } else {
            Assert.assertArrayEquals(new Object[] { zero, one, two, three },
                    itemIds.toArray());
        }
    }

    @Test
    public void size_tableOneAddedItem_returnsFive() throws SQLException {
        Connection conn = connectionPool.reserveConnection();
        Statement statement = conn.createStatement();
        if (SQLTestsConstants.db == DB.MSSQL) {
            statement.executeUpdate("insert into people values('Bengt', 30)");
        } else {
            statement
                    .executeUpdate("insert into people values(default, 'Bengt', 30)");
        }
        statement.close();
        conn.commit();
        connectionPool.releaseConnection(conn);

        assertEquals(5, container.size());
    }

    @Test
    public void indexOfId_tableWithParameterThree_returnsThree()
            throws SQLException {
        if (SQLTestsConstants.db == DB.ORACLE) {
            assertEquals(3, container.indexOfId(new RowId(
                    new Object[] { new BigDecimal(3 + offset) })));
        } else {
            assertEquals(3,
                    container.indexOfId(new RowId(new Object[] { 3 + offset })));
        }
    }

    @Test
    public void indexOfId_table5000RowsWithParameter1337_returns1337()
            throws SQLException {
        DataGenerator.addFiveThousandPeople(connectionPool);

        if (SQLTestsConstants.db == DB.ORACLE) {
            container.getItem(new RowId(new Object[] { new BigDecimal(
                    1337 + offset) }));
            assertEquals(1337, container.indexOfId(new RowId(
                    new Object[] { new BigDecimal(1337 + offset) })));
        } else {
            container.getItem(new RowId(new Object[] { 1337 + offset }));
            assertEquals(1337, container.indexOfId(new RowId(
                    new Object[] { 1337 + offset })));
        }
    }

    @Test
    public void getIdByIndex_table5000rowsIndex1337_returnsRowId1337()
            throws SQLException {
        DataGenerator.addFiveThousandPeople(connectionPool);
        Object itemId = container.getIdByIndex(1337);
        if (SQLTestsConstants.db == DB.ORACLE) {
            assertEquals(new RowId(new Object[] { 1337 + offset }).toString(),
                    itemId.toString());
        } else {
            assertEquals(new RowId(new Object[] { 1337 + offset }), itemId);
        }
    }

    @Test
    public void getIdByIndex_tableWithPaging5000rowsIndex1337_returnsRowId1337()
            throws SQLException {
        DataGenerator.addFiveThousandPeople(connectionPool);

        Object itemId = container.getIdByIndex(1337);
        if (SQLTestsConstants.db == DB.ORACLE) {
            assertEquals(new RowId(new Object[] { 1337 + offset }).toString(),
                    itemId.toString());
        } else {
            assertEquals(new RowId(new Object[] { 1337 + offset }), itemId);
        }
    }

    @Test
    public void nextItemId_tableCurrentItem1337_returnsItem1338()
            throws SQLException {
        DataGenerator.addFiveThousandPeople(connectionPool);
        SQLContainer container = new SQLContainer(new TableQuery("people",
                connectionPool, SQLTestsConstants.sqlGen));
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
    public void prevItemId_tableCurrentItem1337_returns1336()
            throws SQLException {
        DataGenerator.addFiveThousandPeople(connectionPool);
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
    public void firstItemId_table_returnsItemId0() throws SQLException {
        if (SQLTestsConstants.db == DB.ORACLE) {
            assertEquals(new RowId(new Object[] { 0 + offset }).toString(),
                    container.firstItemId().toString());
        } else {
            assertEquals(new RowId(new Object[] { 0 + offset }),
                    container.firstItemId());
        }
    }

    @Test
    public void lastItemId_table5000Rows_returnsItemId4999()
            throws SQLException {
        DataGenerator.addFiveThousandPeople(connectionPool);

        if (SQLTestsConstants.db == DB.ORACLE) {
            assertEquals(new RowId(new Object[] { 4999 + offset }).toString(),
                    container.lastItemId().toString());
        } else {
            assertEquals(new RowId(new Object[] { 4999 + offset }),
                    container.lastItemId());
        }
    }

    @Test
    public void isFirstId_tableActualFirstId_returnsTrue() throws SQLException {
        if (SQLTestsConstants.db == DB.ORACLE) {
            assertTrue(container.isFirstId(new RowId(
                    new Object[] { new BigDecimal(0 + offset) })));
        } else {
            assertTrue(container.isFirstId(new RowId(
                    new Object[] { 0 + offset })));
        }
    }

    @Test
    public void isFirstId_tableSecondId_returnsFalse() throws SQLException {
        if (SQLTestsConstants.db == DB.ORACLE) {
            Assert.assertFalse(container.isFirstId(new RowId(
                    new Object[] { new BigDecimal(1 + offset) })));
        } else {
            Assert.assertFalse(container.isFirstId(new RowId(
                    new Object[] { 1 + offset })));
        }
    }

    @Test
    public void isLastId_tableSecondId_returnsFalse() throws SQLException {
        if (SQLTestsConstants.db == DB.ORACLE) {
            Assert.assertFalse(container.isLastId(new RowId(
                    new Object[] { new BigDecimal(1 + offset) })));
        } else {
            Assert.assertFalse(container.isLastId(new RowId(
                    new Object[] { 1 + offset })));
        }
    }

    @Test
    public void isLastId_tableLastId_returnsTrue() throws SQLException {
        if (SQLTestsConstants.db == DB.ORACLE) {
            assertTrue(container.isLastId(new RowId(
                    new Object[] { new BigDecimal(3 + offset) })));
        } else {
            assertTrue(container
                    .isLastId(new RowId(new Object[] { 3 + offset })));
        }
    }

    @Test
    public void isLastId_table5000RowsLastId_returnsTrue() throws SQLException {
        DataGenerator.addFiveThousandPeople(connectionPool);
        if (SQLTestsConstants.db == DB.ORACLE) {
            assertTrue(container.isLastId(new RowId(
                    new Object[] { new BigDecimal(4999 + offset) })));
        } else {
            assertTrue(container.isLastId(new RowId(
                    new Object[] { 4999 + offset })));
        }
    }

    @Test
    public void allIdsFound_table5000RowsLastId_shouldSucceed()
            throws SQLException {
        DataGenerator.addFiveThousandPeople(connectionPool);

        for (int i = 0; i < 5000; i++) {
            assertTrue(container.containsId(container.getIdByIndex(i)));
        }
    }

    @Test
    public void allIdsFound_table5000RowsLastId_autoCommit_shouldSucceed()
            throws SQLException {
        DataGenerator.addFiveThousandPeople(connectionPool);

        container.setAutoCommit(true);
        for (int i = 0; i < 5000; i++) {
            assertTrue(container.containsId(container.getIdByIndex(i)));
        }
    }

    @Test
    public void refresh_table_sizeShouldUpdate() throws SQLException {
        assertEquals(4, container.size());
        DataGenerator.addFiveThousandPeople(connectionPool);
        container.refresh();
        assertEquals(5000, container.size());
    }

    @Test
    public void refresh_tableWithoutCallingRefresh_sizeShouldNotUpdate()
            throws SQLException {
        // Yeah, this is a weird one. We're testing that the size doesn't update
        // after adding lots of items unless we call refresh inbetween. This to
        // make sure that the refresh method actually refreshes stuff and isn't
        // a NOP.
        assertEquals(4, container.size());
        DataGenerator.addFiveThousandPeople(connectionPool);
        assertEquals(4, container.size());
    }

    @Test
    public void setAutoCommit_table_shouldSucceed() throws SQLException {
        container.setAutoCommit(true);
        assertTrue(container.isAutoCommit());
        container.setAutoCommit(false);
        Assert.assertFalse(container.isAutoCommit());
    }

    @Test
    public void getPageLength_table_returnsDefault100() throws SQLException {
        assertEquals(100, container.getPageLength());
    }

    @Test
    public void setPageLength_table_shouldSucceed() throws SQLException {
        container.setPageLength(20);
        assertEquals(20, container.getPageLength());
        container.setPageLength(200);
        assertEquals(200, container.getPageLength());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addContainerProperty_normal_isUnsupported() throws SQLException {
        container.addContainerProperty("asdf", String.class, "");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void removeContainerProperty_normal_isUnsupported()
            throws SQLException {
        container.removeContainerProperty("asdf");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addItemObject_normal_isUnsupported() throws SQLException {
        container.addItem("asdf");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addItemAfterObjectObject_normal_isUnsupported()
            throws SQLException {
        container.addItemAfter("asdf", "foo");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addItemAtIntObject_normal_isUnsupported() throws SQLException {
        container.addItemAt(2, "asdf");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addItemAtInt_normal_isUnsupported() throws SQLException {
        container.addItemAt(2);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addItemAfterObject_normal_isUnsupported() throws SQLException {
        container.addItemAfter("asdf");
    }

    @Test
    public void addItem_tableAddOneNewItem_returnsItemId() throws SQLException {
        Object itemId = container.addItem();
        Assert.assertNotNull(itemId);
    }

    @Test
    public void addItem_tableAddOneNewItem_autoCommit_returnsFinalItemId()
            throws SQLException {
        container.setAutoCommit(true);
        Object itemId = container.addItem();
        Assert.assertNotNull(itemId);
        assertTrue(itemId instanceof RowId);
        Assert.assertFalse(itemId instanceof TemporaryRowId);
    }

    @Test
    public void addItem_tableAddOneNewItem_autoCommit_sizeIsIncreased()
            throws SQLException {
        container.setAutoCommit(true);
        int originalSize = container.size();
        container.addItem();
        assertEquals(originalSize + 1, container.size());
    }

    @Test
    public void addItem_tableAddOneNewItem_shouldChangeSize()
            throws SQLException {
        int size = container.size();
        container.addItem();
        assertEquals(size + 1, container.size());
    }

    @Test
    public void addItem_tableAddTwoNewItems_shouldChangeSize()
            throws SQLException {
        int size = container.size();
        Object id1 = container.addItem();
        Object id2 = container.addItem();
        assertEquals(size + 2, container.size());
        Assert.assertNotSame(id1, id2);
        Assert.assertFalse(id1.equals(id2));
    }

    @Test
    public void nextItemId_tableNewlyAddedItem_returnsNewlyAdded()
            throws SQLException {
        Object lastId = container.lastItemId();
        Object id = container.addItem();
        assertEquals(id, container.nextItemId(lastId));
    }

    @Test
    public void lastItemId_tableNewlyAddedItem_returnsNewlyAdded()
            throws SQLException {
        Object lastId = container.lastItemId();
        Object id = container.addItem();
        assertEquals(id, container.lastItemId());
        Assert.assertNotSame(lastId, container.lastItemId());
    }

    @Test
    public void indexOfId_tableNewlyAddedItem_returnsFour() throws SQLException {
        Object id = container.addItem();
        assertEquals(4, container.indexOfId(id));
    }

    @Test
    public void getItem_tableNewlyAddedItem_returnsNewlyAdded()
            throws SQLException {
        Object id = container.addItem();
        Assert.assertNotNull(container.getItem(id));
    }

    @Test
    public void getItemIds_tableNewlyAddedItem_containsNewlyAdded()
            throws SQLException {
        Object id = container.addItem();
        assertTrue(container.getItemIds().contains(id));
    }

    @Test
    public void getContainerProperty_tableNewlyAddedItem_returnsPropertyOfNewlyAddedItem()
            throws SQLException {
        Object id = container.addItem();
        Item item = container.getItem(id);
        item.getItemProperty(NAME).setValue("asdf");
        assertEquals("asdf", container.getContainerProperty(id, NAME)
                .getValue());
    }

    @Test
    public void containsId_tableNewlyAddedItem_returnsTrue()
            throws SQLException {
        Object id = container.addItem();

        assertTrue(container.containsId(id));
    }

    @Test
    public void prevItemId_tableTwoNewlyAddedItems_returnsFirstAddedItem()
            throws SQLException {
        Object id1 = container.addItem();
        Object id2 = container.addItem();

        assertEquals(id1, container.prevItemId(id2));
    }

    @Test
    public void firstItemId_tableEmptyResultSet_returnsFirstAddedItem()
            throws SQLException {
        SQLContainer garbageContainer = getGarbageContainer();

        Object id = garbageContainer.addItem();

        Assert.assertSame(id, garbageContainer.firstItemId());
    }

    @Test
    public void isFirstId_tableEmptyResultSet_returnsFirstAddedItem()
            throws SQLException {
        SQLContainer garbageContainer = getGarbageContainer();

        Object id = garbageContainer.addItem();

        assertTrue(garbageContainer.isFirstId(id));
    }

    @Test
    public void isLastId_tableOneItemAdded_returnsTrueForAddedItem()
            throws SQLException {
        Object id = container.addItem();

        assertTrue(container.isLastId(id));
    }

    @Test
    public void isLastId_tableTwoItemsAdded_returnsTrueForLastAddedItem()
            throws SQLException {
        container.addItem();

        Object id2 = container.addItem();

        assertTrue(container.isLastId(id2));
    }

    @Test
    public void getIdByIndex_tableOneItemAddedLastIndexInContainer_returnsAddedItem()
            throws SQLException {
        Object id = container.addItem();

        assertEquals(id, container.getIdByIndex(container.size() - 1));
    }

    @Test
    public void removeItem_tableNoAddedItems_removesItemFromContainer()
            throws SQLException {
        int originalSize = container.size();
        Object id = container.firstItemId();

        assertTrue(container.removeItem(id));

        Assert.assertNotSame(id, container.firstItemId());
        assertEquals(originalSize - 1, container.size());
    }

    @Test
    public void containsId_tableRemovedItem_returnsFalse() throws SQLException {
        Object id = container.firstItemId();
        assertTrue(container.removeItem(id));
        Assert.assertFalse(container.containsId(id));
    }

    @Test
    public void removeItem_tableOneAddedItem_removesTheAddedItem()
            throws SQLException {
        Object id = container.addItem();
        int size = container.size();

        assertTrue(container.removeItem(id));
        Assert.assertFalse(container.containsId(id));
        assertEquals(size - 1, container.size());
    }

    @Test
    public void getItem_tableItemRemoved_returnsNull() throws SQLException {
        Object id = container.firstItemId();

        assertTrue(container.removeItem(id));
        Assert.assertNull(container.getItem(id));
    }

    @Test
    public void getItem_tableAddedItemRemoved_returnsNull() throws SQLException {
        Object id = container.addItem();

        Assert.assertNotNull(container.getItem(id));
        assertTrue(container.removeItem(id));
        Assert.assertNull(container.getItem(id));
    }

    @Test
    public void getItemIds_tableItemRemoved_shouldNotContainRemovedItem()
            throws SQLException {
        Object id = container.firstItemId();

        assertTrue(container.getItemIds().contains(id));
        assertTrue(container.removeItem(id));
        Assert.assertFalse(container.getItemIds().contains(id));
    }

    @Test
    public void getItemIds_tableAddedItemRemoved_shouldNotContainRemovedItem()
            throws SQLException {
        Object id = container.addItem();

        assertTrue(container.getItemIds().contains(id));
        assertTrue(container.removeItem(id));
        Assert.assertFalse(container.getItemIds().contains(id));
    }

    @Test
    public void containsId_tableItemRemoved_returnsFalse() throws SQLException {
        Object id = container.firstItemId();

        assertTrue(container.containsId(id));
        assertTrue(container.removeItem(id));
        Assert.assertFalse(container.containsId(id));
    }

    @Test
    public void containsId_tableAddedItemRemoved_returnsFalse()
            throws SQLException {
        Object id = container.addItem();

        assertTrue(container.containsId(id));
        assertTrue(container.removeItem(id));
        Assert.assertFalse(container.containsId(id));
    }

    @Test
    public void nextItemId_tableItemRemoved_skipsRemovedItem()
            throws SQLException {
        Object first = container.getIdByIndex(0);
        Object second = container.getIdByIndex(1);
        Object third = container.getIdByIndex(2);

        assertTrue(container.removeItem(second));
        assertEquals(third, container.nextItemId(first));
    }

    @Test
    public void nextItemId_tableAddedItemRemoved_skipsRemovedItem()
            throws SQLException {
        Object first = container.lastItemId();
        Object second = container.addItem();
        Object third = container.addItem();

        assertTrue(container.removeItem(second));
        assertEquals(third, container.nextItemId(first));
    }

    @Test
    public void prevItemId_tableItemRemoved_skipsRemovedItem()
            throws SQLException {
        Object first = container.getIdByIndex(0);
        Object second = container.getIdByIndex(1);
        Object third = container.getIdByIndex(2);

        assertTrue(container.removeItem(second));
        assertEquals(first, container.prevItemId(third));
    }

    @Test
    public void prevItemId_tableAddedItemRemoved_skipsRemovedItem()
            throws SQLException {
        Object first = container.lastItemId();
        Object second = container.addItem();
        Object third = container.addItem();

        assertTrue(container.removeItem(second));
        assertEquals(first, container.prevItemId(third));
    }

    @Test
    public void firstItemId_tableFirstItemRemoved_resultChanges()
            throws SQLException {
        Object first = container.firstItemId();

        assertTrue(container.removeItem(first));
        Assert.assertNotSame(first, container.firstItemId());
    }

    @Test
    public void firstItemId_tableNewlyAddedFirstItemRemoved_resultChanges()
            throws SQLException {
        SQLContainer garbageContainer = getGarbageContainer();

        Object first = garbageContainer.addItem();
        Object second = garbageContainer.addItem();

        Assert.assertSame(first, garbageContainer.firstItemId());
        assertTrue(garbageContainer.removeItem(first));
        Assert.assertSame(second, garbageContainer.firstItemId());
    }

    @Test
    public void lastItemId_tableLastItemRemoved_resultChanges()
            throws SQLException {
        Object last = container.lastItemId();

        assertTrue(container.removeItem(last));
        Assert.assertNotSame(last, container.lastItemId());
    }

    @Test
    public void lastItemId_tableAddedLastItemRemoved_resultChanges()
            throws SQLException {
        Object last = container.addItem();

        Assert.assertSame(last, container.lastItemId());
        assertTrue(container.removeItem(last));
        Assert.assertNotSame(last, container.lastItemId());
    }

    @Test
    public void isFirstId_tableFirstItemRemoved_returnsFalse()
            throws SQLException {
        Object first = container.firstItemId();

        assertTrue(container.removeItem(first));
        Assert.assertFalse(container.isFirstId(first));
    }

    @Test
    public void isFirstId_tableAddedFirstItemRemoved_returnsFalse()
            throws SQLException {
        SQLContainer garbageContainer = getGarbageContainer();

        Object first = garbageContainer.addItem();
        garbageContainer.addItem();

        Assert.assertSame(first, garbageContainer.firstItemId());
        assertTrue(garbageContainer.removeItem(first));
        Assert.assertFalse(garbageContainer.isFirstId(first));
    }

    @Test
    public void isLastId_tableLastItemRemoved_returnsFalse()
            throws SQLException {
        Object last = container.lastItemId();

        assertTrue(container.removeItem(last));
        Assert.assertFalse(container.isLastId(last));
    }

    @Test
    public void isLastId_tableAddedLastItemRemoved_returnsFalse()
            throws SQLException {
        Object last = container.addItem();

        Assert.assertSame(last, container.lastItemId());
        assertTrue(container.removeItem(last));
        Assert.assertFalse(container.isLastId(last));
    }

    @Test
    public void indexOfId_tableItemRemoved_returnsNegOne() throws SQLException {
        Object id = container.getIdByIndex(2);

        assertTrue(container.removeItem(id));
        assertEquals(-1, container.indexOfId(id));
    }

    @Test
    public void indexOfId_tableAddedItemRemoved_returnsNegOne()
            throws SQLException {
        Object id = container.addItem();

        assertTrue(container.indexOfId(id) != -1);
        assertTrue(container.removeItem(id));
        assertEquals(-1, container.indexOfId(id));
    }

    @Test
    public void getIdByIndex_tableItemRemoved_resultChanges()
            throws SQLException {
        Object id = container.getIdByIndex(2);

        assertTrue(container.removeItem(id));
        Assert.assertNotSame(id, container.getIdByIndex(2));
    }

    @Test
    public void getIdByIndex_tableAddedItemRemoved_resultChanges()
            throws SQLException {
        Object id = container.addItem();
        container.addItem();
        int index = container.indexOfId(id);

        assertTrue(container.removeItem(id));
        Assert.assertNotSame(id, container.getIdByIndex(index));
    }

    @Test
    public void removeAllItems_table_shouldSucceed() throws SQLException {
        assertTrue(container.removeAllItems());
        assertEquals(0, container.size());
    }

    @Test
    public void removeAllItems_tableAddedItems_shouldSucceed()
            throws SQLException {
        container.addItem();
        container.addItem();

        assertTrue(container.removeAllItems());
        assertEquals(0, container.size());
    }

    // Set timeout to ensure there is no infinite looping (#12882)
    @Test(timeout = 1000)
    public void removeAllItems_manyItems_commit_shouldSucceed()
            throws SQLException {
        final int itemNumber = (SQLContainer.CACHE_RATIO + 1)
                * SQLContainer.DEFAULT_PAGE_LENGTH + 1;

        container.removeAllItems();

        assertEquals(container.size(), 0);
        for (int i = 0; i < itemNumber; ++i) {
            container.addItem();
        }
        container.commit();
        assertEquals(container.size(), itemNumber);
        assertTrue(container.removeAllItems());
        container.commit();
        assertEquals(container.size(), 0);
    }

    @Test
    public void commit_tableAddedItem_shouldBeWrittenToDB() throws SQLException {
        Object id = container.addItem();
        container.getContainerProperty(id, NAME).setValue("New Name");

        assertTrue(id instanceof TemporaryRowId);
        Assert.assertSame(id, container.lastItemId());
        container.commit();
        Assert.assertFalse(container.lastItemId() instanceof TemporaryRowId);
        assertEquals("New Name",
                container.getContainerProperty(container.lastItemId(), NAME)
                        .getValue());
    }

    @Test
    public void commit_tableTwoAddedItems_shouldBeWrittenToDB()
            throws SQLException {
        Object id = container.addItem();
        Object id2 = container.addItem();
        container.getContainerProperty(id, NAME).setValue("Herbert");
        container.getContainerProperty(id2, NAME).setValue("Larry");
        assertTrue(id2 instanceof TemporaryRowId);
        Assert.assertSame(id2, container.lastItemId());
        container.commit();
        Object nextToLast = container.getIdByIndex(container.size() - 2);

        Assert.assertFalse(nextToLast instanceof TemporaryRowId);
        assertEquals("Herbert", container
                .getContainerProperty(nextToLast, NAME).getValue());
        Assert.assertFalse(container.lastItemId() instanceof TemporaryRowId);
        assertEquals("Larry",
                container.getContainerProperty(container.lastItemId(), NAME)
                        .getValue());
    }

    @Test
    public void commit_tableRemovedItem_shouldBeRemovedFromDB()
            throws SQLException {
        Object last = container.lastItemId();
        container.removeItem(last);
        container.commit();

        Assert.assertFalse(last.equals(container.lastItemId()));
    }

    @Test
    public void commit_tableLastItemUpdated_shouldUpdateRowInDB()
            throws SQLException {
        Object last = container.lastItemId();
        container.getContainerProperty(last, NAME).setValue("Donald");
        container.commit();

        assertEquals("Donald",
                container.getContainerProperty(container.lastItemId(), NAME)
                        .getValue());
    }

    @Test
    public void commit_removeModifiedItem_shouldSucceed() throws SQLException {
        int size = container.size();
        Object key = container.firstItemId();
        Item row = container.getItem(key);
        row.getItemProperty(NAME).setValue("Pekka");

        assertTrue(container.removeItem(key));
        container.commit();
        assertEquals(size - 1, container.size());
    }

    @Test
    public void rollback_tableItemAdded_discardsAddedItem() throws SQLException {
        int size = container.size();
        Object id = container.addItem();
        container.getContainerProperty(id, NAME).setValue("foo");
        assertEquals(size + 1, container.size());
        container.rollback();
        assertEquals(size, container.size());
        Assert.assertFalse("foo".equals(container.getContainerProperty(
                container.lastItemId(), NAME).getValue()));
    }

    @Test
    public void rollback_tableItemRemoved_restoresRemovedItem()
            throws SQLException {
        int size = container.size();
        Object last = container.lastItemId();
        container.removeItem(last);
        assertEquals(size - 1, container.size());
        container.rollback();
        assertEquals(size, container.size());
        assertEquals(last, container.lastItemId());
    }

    @Test
    public void rollback_tableItemChanged_discardsChanges() throws SQLException {
        Object last = container.lastItemId();
        container.getContainerProperty(last, NAME).setValue("foo");
        container.rollback();
        Assert.assertFalse("foo".equals(container.getContainerProperty(
                container.lastItemId(), NAME).getValue()));
    }

    @Test
    public void itemChangeNotification_table_isModifiedReturnsTrue()
            throws SQLException {
        Assert.assertFalse(container.isModified());
        RowItem last = (RowItem) container.getItem(container.lastItemId());
        container.itemChangeNotification(last);
        assertTrue(container.isModified());
    }

    @Test
    public void itemSetChangeListeners_table_shouldFire() throws SQLException {
        ItemSetChangeListener listener = EasyMock
                .createMock(ItemSetChangeListener.class);
        listener.containerItemSetChange(EasyMock.isA(ItemSetChangeEvent.class));
        EasyMock.replay(listener);

        container.addListener(listener);
        container.addItem();

        EasyMock.verify(listener);
    }

    @Test
    public void itemSetChangeListeners_tableItemRemoved_shouldFire()
            throws SQLException {
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
    public void removeListener_table_shouldNotFire() throws SQLException {
        ItemSetChangeListener listener = EasyMock
                .createMock(ItemSetChangeListener.class);
        EasyMock.replay(listener);

        container.addListener(listener);
        container.removeListener(listener);
        container.addItem();

        EasyMock.verify(listener);
    }

    @Test
    public void isModified_tableRemovedItem_returnsTrue() throws SQLException {
        Assert.assertFalse(container.isModified());
        container.removeItem(container.lastItemId());
        assertTrue(container.isModified());
    }

    @Test
    public void isModified_tableAddedItem_returnsTrue() throws SQLException {
        Assert.assertFalse(container.isModified());
        container.addItem();
        assertTrue(container.isModified());
    }

    @Test
    public void isModified_tableChangedItem_returnsTrue() throws SQLException {
        Assert.assertFalse(container.isModified());
        container.getContainerProperty(container.lastItemId(), NAME).setValue(
                "foo");
        assertTrue(container.isModified());
    }

    @Test
    public void getSortableContainerPropertyIds_table_returnsAllPropertyIds()
            throws SQLException {
        Collection<?> sortableIds = container.getSortableContainerPropertyIds();
        assertTrue(sortableIds.contains(ID));
        assertTrue(sortableIds.contains(NAME));
        assertTrue(sortableIds.contains("AGE"));
        assertEquals(3, sortableIds.size());
        if (SQLTestsConstants.db == DB.MSSQL
                || SQLTestsConstants.db == DB.ORACLE) {
            Assert.assertFalse(sortableIds.contains("rownum"));
        }
    }

    @Test
    public void addOrderBy_table_shouldReorderResults() throws SQLException {
        // Ville, Kalle, Pelle, Börje
        assertEquals("Ville",
                container.getContainerProperty(container.firstItemId(), NAME)
                        .getValue());
        assertEquals("Börje",
                container.getContainerProperty(container.lastItemId(), NAME)
                        .getValue());

        container.addOrderBy(new OrderBy(NAME, true));
        // Börje, Kalle, Pelle, Ville
        assertEquals("Börje",
                container.getContainerProperty(container.firstItemId(), NAME)
                        .getValue());
        assertEquals("Ville",
                container.getContainerProperty(container.lastItemId(), NAME)
                        .getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void addOrderBy_tableIllegalColumn_shouldFail() throws SQLException {
        container.addOrderBy(new OrderBy("asdf", true));
    }

    @Test
    public void sort_table_sortsByName() throws SQLException {
        // Ville, Kalle, Pelle, Börje
        assertEquals("Ville",
                container.getContainerProperty(container.firstItemId(), NAME)
                        .getValue());
        assertEquals("Börje",
                container.getContainerProperty(container.lastItemId(), NAME)
                        .getValue());

        container.sort(new Object[] { NAME }, new boolean[] { true });

        // Börje, Kalle, Pelle, Ville
        assertEquals("Börje",
                container.getContainerProperty(container.firstItemId(), NAME)
                        .getValue());
        assertEquals("Ville",
                container.getContainerProperty(container.lastItemId(), NAME)
                        .getValue());
    }

    @Test
    public void addFilter_table_filtersResults() throws SQLException {
        // Ville, Kalle, Pelle, Börje
        assertEquals(4, container.size());
        assertEquals("Börje",
                container.getContainerProperty(container.lastItemId(), NAME)
                        .getValue());

        container.addContainerFilter(new Like(NAME, "%lle"));
        // Ville, Kalle, Pelle
        assertEquals(3, container.size());
        assertEquals("Pelle",
                container.getContainerProperty(container.lastItemId(), NAME)
                        .getValue());
    }

    @Test
    public void addContainerFilter_filtersResults() throws SQLException {
        // Ville, Kalle, Pelle, Börje
        assertEquals(4, container.size());

        container.addContainerFilter(NAME, "Vi", false, false);

        // Ville
        assertEquals(1, container.size());
        assertEquals("Ville",
                container.getContainerProperty(container.lastItemId(), NAME)
                        .getValue());
    }

    @Test
    public void addContainerFilter_ignoreCase_filtersResults()
            throws SQLException {
        // Ville, Kalle, Pelle, Börje
        assertEquals(4, container.size());

        container.addContainerFilter(NAME, "vi", true, false);

        // Ville
        assertEquals(1, container.size());
        assertEquals("Ville",
                container.getContainerProperty(container.lastItemId(), NAME)
                        .getValue());
    }

    @Test
    public void removeAllContainerFilters_table_noFiltering()
            throws SQLException {
        // Ville, Kalle, Pelle, Börje
        assertEquals(4, container.size());

        container.addContainerFilter(NAME, "Vi", false, false);

        // Ville
        assertEquals(1, container.size());
        assertEquals("Ville",
                container.getContainerProperty(container.lastItemId(), NAME)
                        .getValue());

        container.removeAllContainerFilters();

        assertEquals(4, container.size());
        assertEquals("Börje",
                container.getContainerProperty(container.lastItemId(), NAME)
                        .getValue());
    }

    @Test
    public void removeContainerFilters_table_noFiltering() throws SQLException {
        // Ville, Kalle, Pelle, Börje
        assertEquals(4, container.size());

        container.addContainerFilter(NAME, "Vi", false, false);

        // Ville
        assertEquals(1, container.size());
        assertEquals("Ville",
                container.getContainerProperty(container.lastItemId(), NAME)
                        .getValue());

        container.removeContainerFilters(NAME);

        assertEquals(4, container.size());
        assertEquals("Börje",
                container.getContainerProperty(container.lastItemId(), NAME)
                        .getValue());
    }

    @Test
    public void addFilter_tableBufferedItems_alsoFiltersBufferedItems()
            throws SQLException {
        // Ville, Kalle, Pelle, Börje
        assertEquals(4, container.size());
        assertEquals("Börje",
                container.getContainerProperty(container.lastItemId(), NAME)
                        .getValue());

        Object id1 = container.addItem();
        container.getContainerProperty(id1, NAME).setValue("Palle");
        Object id2 = container.addItem();
        container.getContainerProperty(id2, NAME).setValue("Bengt");

        container.addContainerFilter(new Like(NAME, "%lle"));

        // Ville, Kalle, Pelle, Palle
        assertEquals(4, container.size());
        assertEquals("Ville",
                container.getContainerProperty(container.getIdByIndex(0), NAME)
                        .getValue());
        assertEquals("Kalle",
                container.getContainerProperty(container.getIdByIndex(1), NAME)
                        .getValue());
        assertEquals("Pelle",
                container.getContainerProperty(container.getIdByIndex(2), NAME)
                        .getValue());
        assertEquals("Palle",
                container.getContainerProperty(container.getIdByIndex(3), NAME)
                        .getValue());

        try {
            container.getIdByIndex(4);
            Assert.fail("SQLContainer.getIdByIndex() returned a value for an index beyond the end of the container");
        } catch (IndexOutOfBoundsException e) {
            // should throw exception - item is filtered out
        }
        Assert.assertNull(container.nextItemId(container.getIdByIndex(3)));

        Assert.assertFalse(container.containsId(id2));
        Assert.assertFalse(container.getItemIds().contains(id2));

        Assert.assertNull(container.getItem(id2));
        assertEquals(-1, container.indexOfId(id2));

        Assert.assertNotSame(id2, container.lastItemId());
        Assert.assertSame(id1, container.lastItemId());
    }

    @Test
    public void sort_tableBufferedItems_sortsBufferedItemsLastInOrderAdded()
            throws SQLException {
        // Ville, Kalle, Pelle, Börje
        assertEquals("Ville",
                container.getContainerProperty(container.firstItemId(), NAME)
                        .getValue());
        assertEquals("Börje",
                container.getContainerProperty(container.lastItemId(), NAME)
                        .getValue());

        Object id1 = container.addItem();
        container.getContainerProperty(id1, NAME).setValue("Wilbert");
        Object id2 = container.addItem();
        container.getContainerProperty(id2, NAME).setValue("Albert");

        container.sort(new Object[] { NAME }, new boolean[] { true });

        // Börje, Kalle, Pelle, Ville, Wilbert, Albert
        assertEquals("Börje",
                container.getContainerProperty(container.firstItemId(), NAME)
                        .getValue());
        assertEquals(
                "Wilbert",
                container.getContainerProperty(
                        container.getIdByIndex(container.size() - 2), NAME)
                        .getValue());
        assertEquals("Albert",
                container.getContainerProperty(container.lastItemId(), NAME)
                        .getValue());
    }

}

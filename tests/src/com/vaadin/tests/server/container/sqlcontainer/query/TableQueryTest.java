package com.vaadin.tests.server.container.sqlcontainer.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.OptimisticLockException;
import com.vaadin.data.util.RowItem;
import com.vaadin.data.util.SQLContainer;
import com.vaadin.data.util.connection.JDBCConnectionPool;
import com.vaadin.data.util.connection.SimpleJDBCConnectionPool;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.filter.Like;
import com.vaadin.data.util.query.OrderBy;
import com.vaadin.data.util.query.TableQuery;
import com.vaadin.data.util.query.generator.DefaultSQLGenerator;
import com.vaadin.tests.server.container.sqlcontainer.AllTests;
import com.vaadin.tests.server.container.sqlcontainer.AllTests.DB;
import com.vaadin.tests.server.container.sqlcontainer.DataGenerator;

public class TableQueryTest {
    private static final int offset = AllTests.offset;
    private JDBCConnectionPool connectionPool;

    @Before
    public void setUp() throws SQLException {

        try {
            connectionPool = new SimpleJDBCConnectionPool(AllTests.dbDriver,
                    AllTests.dbURL, AllTests.dbUser, AllTests.dbPwd, 2, 2);
        } catch (SQLException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        DataGenerator.addPeopleToDatabase(connectionPool);
    }

    @After
    public void tearDown() {
        if (connectionPool != null) {
            connectionPool.destroy();
        }
    }

    /**********************************************************************
     * TableQuery construction tests
     **********************************************************************/
    @Test
    public void construction_legalParameters_shouldSucceed() {
        TableQuery tQuery = new TableQuery("people", connectionPool,
                new DefaultSQLGenerator());
        Assert.assertArrayEquals(new Object[] { "ID" }, tQuery
                .getPrimaryKeyColumns().toArray());
        boolean correctTableName = "people".equalsIgnoreCase(tQuery
                .getTableName());
        Assert.assertTrue(correctTableName);
    }

    @Test
    public void construction_legalParameters_defaultGenerator_shouldSucceed() {
        TableQuery tQuery = new TableQuery("people", connectionPool,
                AllTests.sqlGen);
        Assert.assertArrayEquals(new Object[] { "ID" }, tQuery
                .getPrimaryKeyColumns().toArray());
        boolean correctTableName = "people".equalsIgnoreCase(tQuery
                .getTableName());
        Assert.assertTrue(correctTableName);
    }

    @Test(expected = IllegalArgumentException.class)
    public void construction_nonExistingTableName_shouldFail() {
        new TableQuery("skgwaguhsd", connectionPool, new DefaultSQLGenerator());
    }

    @Test(expected = IllegalArgumentException.class)
    public void construction_emptyTableName_shouldFail() {
        new TableQuery("", connectionPool, new DefaultSQLGenerator());
    }

    @Test(expected = IllegalArgumentException.class)
    public void construction_nullSqlGenerator_shouldFail() {
        new TableQuery("people", connectionPool, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void construction_nullConnectionPool_shouldFail() {
        new TableQuery("people", null, new DefaultSQLGenerator());
    }

    /**********************************************************************
     * TableQuery row count tests
     **********************************************************************/
    @Test
    public void getCount_simpleQuery_returnsFour() throws SQLException {
        TableQuery tQuery = new TableQuery("people", connectionPool,
                AllTests.sqlGen);
        Assert.assertEquals(4, tQuery.getCount());
    }

    @Test
    public void getCount_simpleQueryTwoMorePeopleAdded_returnsSix()
            throws SQLException {
        // Add some people
        Connection conn = connectionPool.reserveConnection();
        Statement statement = conn.createStatement();
        if (AllTests.db == DB.MSSQL) {
            statement.executeUpdate("insert into people values('Bengt', 30)");
            statement.executeUpdate("insert into people values('Ingvar', 50)");
        } else {
            statement
                    .executeUpdate("insert into people values(default, 'Bengt', 30)");
            statement
                    .executeUpdate("insert into people values(default, 'Ingvar', 50)");
        }
        statement.close();
        conn.commit();
        connectionPool.releaseConnection(conn);

        TableQuery tQuery = new TableQuery("people", connectionPool,
                AllTests.sqlGen);

        Assert.assertEquals(6, tQuery.getCount());
    }

    @Test
    public void getCount_normalState_releasesConnection() throws SQLException {
        TableQuery tQuery = new TableQuery("people", connectionPool,
                AllTests.sqlGen);
        tQuery.getCount();
        tQuery.getCount();
        Assert.assertNotNull(connectionPool.reserveConnection());
    }

    /**********************************************************************
     * TableQuery get results tests
     **********************************************************************/
    @Test
    public void getResults_simpleQuery_returnsFourRecords() throws SQLException {
        TableQuery tQuery = new TableQuery("people", connectionPool,
                AllTests.sqlGen);
        tQuery.beginTransaction();
        ResultSet rs = tQuery.getResults(0, 0);

        Assert.assertTrue(rs.next());
        Assert.assertEquals(0 + offset, rs.getInt(1));
        Assert.assertEquals("Ville", rs.getString(2));

        Assert.assertTrue(rs.next());
        Assert.assertEquals(1 + offset, rs.getInt(1));
        Assert.assertEquals("Kalle", rs.getString(2));

        Assert.assertTrue(rs.next());
        Assert.assertEquals(2 + offset, rs.getInt(1));
        Assert.assertEquals("Pelle", rs.getString(2));

        Assert.assertTrue(rs.next());
        Assert.assertEquals(3 + offset, rs.getInt(1));
        Assert.assertEquals("Börje", rs.getString(2));

        Assert.assertFalse(rs.next());
        tQuery.commit();
    }

    @Test
    public void getResults_noDelegate5000Rows_returns5000rows()
            throws SQLException {
        DataGenerator.addFiveThousandPeople(connectionPool);

        TableQuery tQuery = new TableQuery("people", connectionPool,
                AllTests.sqlGen);

        tQuery.beginTransaction();
        ResultSet rs = tQuery.getResults(0, 0);
        for (int i = 0; i < 5000; i++) {
            Assert.assertTrue(rs.next());
        }
        Assert.assertFalse(rs.next());
        tQuery.commit();
    }

    /**********************************************************************
     * TableQuery transaction management tests
     **********************************************************************/
    @Test
    public void beginTransaction_readOnly_shouldSucceed() throws SQLException {
        TableQuery tQuery = new TableQuery("people", connectionPool,
                AllTests.sqlGen);
        tQuery.beginTransaction();
    }

    @Test(expected = IllegalStateException.class)
    public void beginTransaction_transactionAlreadyActive_shouldFail()
            throws SQLException {
        TableQuery tQuery = new TableQuery("people", connectionPool,
                AllTests.sqlGen);

        tQuery.beginTransaction();
        tQuery.beginTransaction();
    }

    @Test
    public void commit_readOnly_shouldSucceed() throws SQLException {
        TableQuery tQuery = new TableQuery("people", connectionPool,
                AllTests.sqlGen);
        tQuery.beginTransaction();
        tQuery.commit();
    }

    @Test
    public void rollback_readOnly_shouldSucceed() throws SQLException {
        TableQuery tQuery = new TableQuery("people", connectionPool,
                AllTests.sqlGen);
        tQuery.beginTransaction();
        tQuery.rollback();
    }

    @Test(expected = SQLException.class)
    public void commit_noActiveTransaction_shouldFail() throws SQLException {
        TableQuery tQuery = new TableQuery("people", connectionPool,
                AllTests.sqlGen);
        tQuery.commit();
    }

    @Test(expected = SQLException.class)
    public void rollback_noActiveTransaction_shouldFail() throws SQLException {
        TableQuery tQuery = new TableQuery("people", connectionPool,
                AllTests.sqlGen);
        tQuery.rollback();
    }

    /**********************************************************************
     * TableQuery row query with given keys tests
     **********************************************************************/
    @Test
    public void containsRowWithKeys_existingKeys_returnsTrue()
            throws SQLException {
        TableQuery tQuery = new TableQuery("people", connectionPool,
                AllTests.sqlGen);
        Assert.assertTrue(tQuery.containsRowWithKey(1));
    }

    @Test
    public void containsRowWithKeys_nonexistingKeys_returnsTrue()
            throws SQLException {
        TableQuery tQuery = new TableQuery("people", connectionPool,
                AllTests.sqlGen);

        Assert.assertFalse(tQuery.containsRowWithKey(1337));
    }

    @Test
    public void containsRowWithKeys_invalidKeys_shouldFail()
            throws SQLException {
        TableQuery tQuery = new TableQuery("people", connectionPool,
                AllTests.sqlGen);
        boolean b = true;
        try {
            b = tQuery.containsRowWithKey("foo");
        } catch (SQLException se) {
            return;
        }
        Assert.assertFalse(b);
    }

    @Test
    public void containsRowWithKeys_nullKeys_shouldFailAndReleaseConnections()
            throws SQLException {
        TableQuery tQuery = new TableQuery("people", connectionPool,
                AllTests.sqlGen);
        try {
            tQuery.containsRowWithKey(new Object[] { null });
        } catch (SQLException e) {
            // We should now be able to reserve two connections
            connectionPool.reserveConnection();
            connectionPool.reserveConnection();
        }
    }

    /**********************************************************************
     * TableQuery filtering and ordering tests
     **********************************************************************/
    @Test
    public void setFilters_shouldReturnCorrectCount() throws SQLException {
        TableQuery tQuery = new TableQuery("people", connectionPool,
                AllTests.sqlGen);
        List<Filter> filters = new ArrayList<Filter>();
        filters.add(new Like("NAME", "%lle"));
        tQuery.setFilters(filters);
        Assert.assertEquals(3, tQuery.getCount());
    }

    @Test
    public void setOrderByNameAscending_shouldReturnCorrectOrder()
            throws SQLException {
        TableQuery tQuery = new TableQuery("people", connectionPool,
                AllTests.sqlGen);

        List<OrderBy> orderBys = Arrays.asList(new OrderBy("NAME", true));
        tQuery.setOrderBy(orderBys);

        tQuery.beginTransaction();
        ResultSet rs;
        rs = tQuery.getResults(0, 0);

        Assert.assertTrue(rs.next());
        Assert.assertEquals(3 + offset, rs.getInt(1));
        Assert.assertEquals("Börje", rs.getString(2));

        Assert.assertTrue(rs.next());
        Assert.assertEquals(1 + offset, rs.getInt(1));
        Assert.assertEquals("Kalle", rs.getString(2));

        Assert.assertTrue(rs.next());
        Assert.assertEquals(2 + offset, rs.getInt(1));
        Assert.assertEquals("Pelle", rs.getString(2));

        Assert.assertTrue(rs.next());
        Assert.assertEquals(0 + offset, rs.getInt(1));
        Assert.assertEquals("Ville", rs.getString(2));

        Assert.assertFalse(rs.next());
        tQuery.commit();
    }

    @Test
    public void setOrderByNameDescending_shouldReturnCorrectOrder()
            throws SQLException {
        TableQuery tQuery = new TableQuery("people", connectionPool,
                AllTests.sqlGen);

        List<OrderBy> orderBys = Arrays.asList(new OrderBy("NAME", false));
        tQuery.setOrderBy(orderBys);

        tQuery.beginTransaction();
        ResultSet rs;
        rs = tQuery.getResults(0, 0);

        Assert.assertTrue(rs.next());
        Assert.assertEquals(0 + offset, rs.getInt(1));
        Assert.assertEquals("Ville", rs.getString(2));

        Assert.assertTrue(rs.next());
        Assert.assertEquals(2 + offset, rs.getInt(1));
        Assert.assertEquals("Pelle", rs.getString(2));

        Assert.assertTrue(rs.next());
        Assert.assertEquals(1 + offset, rs.getInt(1));
        Assert.assertEquals("Kalle", rs.getString(2));

        Assert.assertTrue(rs.next());
        Assert.assertEquals(3 + offset, rs.getInt(1));
        Assert.assertEquals("Börje", rs.getString(2));

        Assert.assertFalse(rs.next());
        tQuery.commit();
    }

    @Test
    public void setFilters_nullParameter_shouldSucceed() {
        TableQuery tQuery = new TableQuery("people", connectionPool,
                AllTests.sqlGen);
        tQuery.setFilters(null);
    }

    @Test
    public void setOrderBy_nullParameter_shouldSucceed() {
        TableQuery tQuery = new TableQuery("people", connectionPool,
                AllTests.sqlGen);
        tQuery.setOrderBy(null);
    }

    /**********************************************************************
     * TableQuery row removal tests
     **********************************************************************/
    @Test
    public void removeRowThroughContainer_legalRowItem_shouldSucceed()
            throws SQLException {
        TableQuery tQuery = new TableQuery("people", connectionPool,
                AllTests.sqlGen);
        SQLContainer container = new SQLContainer(tQuery);
        container.setAutoCommit(false);
        Assert.assertTrue(container.removeItem(container.getItemIds()
                .iterator().next()));

        Assert.assertEquals(4, tQuery.getCount());
        Assert.assertEquals(3, container.size());
        container.commit();

        Assert.assertEquals(3, tQuery.getCount());
        Assert.assertEquals(3, container.size());
    }

    @Test
    public void removeRowThroughContainer_nonexistingRowId_shouldFail()
            throws SQLException {
        TableQuery tQuery = new TableQuery("people", connectionPool,
                AllTests.sqlGen);

        SQLContainer container = new SQLContainer(tQuery);
        container.setAutoCommit(true);
        Assert.assertFalse(container.removeItem("foo"));
    }

    /**********************************************************************
     * TableQuery row adding / modification tests
     **********************************************************************/
    @Test
    public void insertRowThroughContainer_shouldSucceed() throws SQLException {
        TableQuery tQuery = new TableQuery("people", connectionPool,
                AllTests.sqlGen);
        tQuery.setVersionColumn("ID");

        SQLContainer container = new SQLContainer(tQuery);
        container.setAutoCommit(false);

        Object item = container.addItem();
        Assert.assertNotNull(item);

        Assert.assertEquals(4, tQuery.getCount());
        Assert.assertEquals(5, container.size());
        container.commit();

        Assert.assertEquals(5, tQuery.getCount());
        Assert.assertEquals(5, container.size());
    }

    @Test
    public void modifyRowThroughContainer_shouldSucceed() throws SQLException {
        TableQuery tQuery = new TableQuery("people", connectionPool,
                AllTests.sqlGen);

        // In this test the primary key is used as a version column
        tQuery.setVersionColumn("ID");
        SQLContainer container = new SQLContainer(tQuery);
        container.setAutoCommit(false);

        /* Check that the container size is correct and there is no 'Viljami' */
        Assert.assertEquals(4, container.size());
        List<Filter> filters = new ArrayList<Filter>();
        filters.add(new Equal("NAME", "Viljami"));
        tQuery.setFilters(filters);
        Assert.assertEquals(0, tQuery.getCount());
        tQuery.setFilters(null);

        /* Fetch first item, modify and commit */
        Object item = container.getItem(container.getItemIds().iterator()
                .next());
        Assert.assertNotNull(item);

        RowItem ri = (RowItem) item;
        Assert.assertNotNull(ri.getItemProperty("NAME"));
        ri.getItemProperty("NAME").setValue("Viljami");

        container.commit();

        // Check that the size is still correct and only 1 'Viljami' is found
        Assert.assertEquals(4, tQuery.getCount());
        Assert.assertEquals(4, container.size());
        tQuery.setFilters(filters);
        Assert.assertEquals(1, tQuery.getCount());
    }

    @Test
    public void storeRow_noVersionColumn_shouldSucceed()
            throws UnsupportedOperationException, SQLException {
        TableQuery tQuery = new TableQuery("people", connectionPool,
                AllTests.sqlGen);
        SQLContainer container = new SQLContainer(tQuery);
        Object id = container.addItem();
        RowItem row = (RowItem) container.getItem(id);
        row.getItemProperty("NAME").setValue("R2D2");
        row.getItemProperty("AGE").setValue(123);
        tQuery.beginTransaction();
        tQuery.storeRow(row);
        tQuery.commit();

        Connection conn = connectionPool.reserveConnection();
        PreparedStatement stmt = conn
                .prepareStatement("SELECT * FROM PEOPLE WHERE \"NAME\" = ?");
        stmt.setString(1, "R2D2");
        ResultSet rs = stmt.executeQuery();
        Assert.assertTrue(rs.next());
        rs.close();
        stmt.close();
        connectionPool.releaseConnection(conn);
    }

    @Test
    public void storeRow_versionSetAndEqualToDBValue_shouldSucceed()
            throws SQLException {
        DataGenerator.addVersionedData(connectionPool);

        TableQuery tQuery = new TableQuery("versioned", connectionPool,
                AllTests.sqlGen);
        tQuery.setVersionColumn("VERSION");
        SQLContainer container = new SQLContainer(tQuery);
        RowItem row = (RowItem) container.getItem(container.firstItemId());
        Assert.assertEquals("Junk", row.getItemProperty("TEXT").getValue());

        row.getItemProperty("TEXT").setValue("asdf");
        container.commit();

        Connection conn = connectionPool.reserveConnection();
        PreparedStatement stmt = conn
                .prepareStatement("SELECT * FROM VERSIONED WHERE \"TEXT\" = ?");
        stmt.setString(1, "asdf");
        ResultSet rs = stmt.executeQuery();
        Assert.assertTrue(rs.next());
        rs.close();
        stmt.close();
        conn.commit();
        connectionPool.releaseConnection(conn);
    }

    @Test(expected = OptimisticLockException.class)
    public void storeRow_versionSetAndLessThanDBValue_shouldThrowException()
            throws SQLException {
        if (AllTests.db == DB.HSQLDB) {
            throw new OptimisticLockException(
                    "HSQLDB doesn't support row versioning for optimistic locking - don't run this test.",
                    null);
        }
        DataGenerator.addVersionedData(connectionPool);

        TableQuery tQuery = new TableQuery("versioned", connectionPool,
                AllTests.sqlGen);
        tQuery.setVersionColumn("VERSION");
        SQLContainer container = new SQLContainer(tQuery);
        RowItem row = (RowItem) container.getItem(container.firstItemId());
        Assert.assertEquals("Junk", row.getItemProperty("TEXT").getValue());

        row.getItemProperty("TEXT").setValue("asdf");

        // Update the version using another connection.
        Connection conn = connectionPool.reserveConnection();
        PreparedStatement stmt = conn
                .prepareStatement("UPDATE VERSIONED SET \"TEXT\" = ? WHERE \"ID\" = ?");
        stmt.setString(1, "foo");
        stmt.setObject(2, row.getItemProperty("ID").getValue());
        stmt.executeUpdate();
        stmt.close();
        conn.commit();
        connectionPool.releaseConnection(conn);

        container.commit();
    }

    @Test
    public void removeRow_versionSetAndEqualToDBValue_shouldSucceed()
            throws SQLException {
        DataGenerator.addVersionedData(connectionPool);

        TableQuery tQuery = new TableQuery("versioned", connectionPool,
                AllTests.sqlGen);
        tQuery.setVersionColumn("VERSION");
        SQLContainer container = new SQLContainer(tQuery);
        RowItem row = (RowItem) container.getItem(container.firstItemId());
        Assert.assertEquals("Junk", row.getItemProperty("TEXT").getValue());

        container.removeItem(container.firstItemId());
        container.commit();

        Connection conn = connectionPool.reserveConnection();
        PreparedStatement stmt = conn
                .prepareStatement("SELECT * FROM VERSIONED WHERE \"TEXT\" = ?");
        stmt.setString(1, "Junk");
        ResultSet rs = stmt.executeQuery();
        Assert.assertFalse(rs.next());
        rs.close();
        stmt.close();
        conn.commit();
        connectionPool.releaseConnection(conn);
    }

    @Test(expected = OptimisticLockException.class)
    public void removeRow_versionSetAndLessThanDBValue_shouldThrowException()
            throws SQLException {
        if (AllTests.db == AllTests.DB.HSQLDB) {
            // HSQLDB doesn't support versioning, so this is to make the test
            // green.
            throw new OptimisticLockException(null);
        }
        DataGenerator.addVersionedData(connectionPool);

        TableQuery tQuery = new TableQuery("versioned", connectionPool,
                AllTests.sqlGen);
        tQuery.setVersionColumn("VERSION");
        SQLContainer container = new SQLContainer(tQuery);
        RowItem row = (RowItem) container.getItem(container.firstItemId());
        Assert.assertEquals("Junk", row.getItemProperty("TEXT").getValue());

        // Update the version using another connection.
        Connection conn = connectionPool.reserveConnection();
        PreparedStatement stmt = conn
                .prepareStatement("UPDATE VERSIONED SET \"TEXT\" = ? WHERE \"ID\" = ?");
        stmt.setString(1, "asdf");
        stmt.setObject(2, row.getItemProperty("ID").getValue());
        stmt.executeUpdate();
        stmt.close();
        conn.commit();
        connectionPool.releaseConnection(conn);

        container.removeItem(container.firstItemId());
        container.commit();
    }

}
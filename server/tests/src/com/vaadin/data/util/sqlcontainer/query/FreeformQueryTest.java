package com.vaadin.data.util.sqlcontainer.query;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.Like;
import com.vaadin.data.util.sqlcontainer.DataGenerator;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.RowItem;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.SQLTestsConstants;
import com.vaadin.data.util.sqlcontainer.SQLTestsConstants.DB;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;

public class FreeformQueryTest {

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

    @Test
    public void construction_legalParameters_shouldSucceed() {
        FreeformQuery ffQuery = new FreeformQuery("SELECT * FROM foo",
                Arrays.asList("ID"), connectionPool);
        Assert.assertArrayEquals(new Object[] { "ID" }, ffQuery
                .getPrimaryKeyColumns().toArray());

        Assert.assertEquals("SELECT * FROM foo", ffQuery.getQueryString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void construction_emptyQueryString_shouldFail() {
        new FreeformQuery("", Arrays.asList("ID"), connectionPool);
    }

    @Test
    public void construction_nullPrimaryKeys_shouldSucceed() {
        new FreeformQuery("SELECT * FROM foo", null, connectionPool);
    }

    @Test
    public void construction_nullPrimaryKeys2_shouldSucceed() {
        new FreeformQuery("SELECT * FROM foo", connectionPool);
    }

    @Test
    public void construction_emptyPrimaryKeys_shouldSucceed() {
        new FreeformQuery("SELECT * FROM foo", connectionPool);
    }

    @Test(expected = IllegalArgumentException.class)
    public void construction_emptyStringsInPrimaryKeys_shouldFail() {
        new FreeformQuery("SELECT * FROM foo", Arrays.asList(""),
                connectionPool);
    }

    @Test(expected = IllegalArgumentException.class)
    public void construction_nullConnectionPool_shouldFail() {
        new FreeformQuery("SELECT * FROM foo", Arrays.asList("ID"), null);
    }

    @Test
    public void getCount_simpleQuery_returnsFour() throws SQLException {
        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                Arrays.asList("ID"), connectionPool);
        Assert.assertEquals(4, query.getCount());
    }

    @Test(expected = SQLException.class)
    public void getCount_illegalQuery_shouldThrowSQLException()
            throws SQLException {
        FreeformQuery query = new FreeformQuery("SELECT * FROM asdf",
                Arrays.asList("ID"), connectionPool);
        query.getResults(0, 50);
    }

    @Test
    public void getCount_simpleQueryTwoMorePeopleAdded_returnsSix()
            throws SQLException {
        // Add some people
        Connection conn = connectionPool.reserveConnection();
        Statement statement = conn.createStatement();
        if (SQLTestsConstants.db == DB.MSSQL) {
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

        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                Arrays.asList("ID"), connectionPool);

        Assert.assertEquals(6, query.getCount());
    }

    @Test
    public void getCount_moreComplexQuery_returnsThree() throws SQLException {
        FreeformQuery query = new FreeformQuery(
                "SELECT * FROM people WHERE \"NAME\" LIKE '%lle'",
                connectionPool, new String[] { "ID" });
        Assert.assertEquals(3, query.getCount());
    }

    @Test
    public void getCount_normalState_releasesConnection() throws SQLException {
        FreeformQuery query = new FreeformQuery(
                "SELECT * FROM people WHERE \"NAME\" LIKE '%lle'",
                connectionPool, "ID");
        query.getCount();
        query.getCount();
        Connection c = connectionPool.reserveConnection();
        Assert.assertNotNull(c);
        // Cleanup to make test connection pool happy
        connectionPool.releaseConnection(c);
    }

    @Test
    public void getCount_delegateRegistered_shouldUseDelegate()
            throws SQLException {
        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                Arrays.asList("ID"), connectionPool);
        FreeformQueryDelegate delegate = EasyMock
                .createMock(FreeformQueryDelegate.class);
        EasyMock.expect(delegate.getCountQuery()).andReturn(
                "SELECT COUNT(*) FROM people WHERE \"NAME\" LIKE '%lle'");
        EasyMock.replay(delegate);
        query.setDelegate(delegate);
        Assert.assertEquals(3, query.getCount());
        EasyMock.verify(delegate);
    }

    @Test
    public void getCount_delegateRegisteredZeroRows_returnsZero()
            throws SQLException {
        DataGenerator.createGarbage(connectionPool);
        FreeformQuery query = new FreeformQuery("SELECT * FROM GARBAGE",
                Arrays.asList("ID"), connectionPool);
        FreeformQueryDelegate delegate = EasyMock
                .createMock(FreeformQueryDelegate.class);
        EasyMock.expect(delegate.getCountQuery()).andReturn(
                "SELECT COUNT(*) FROM GARBAGE");
        EasyMock.replay(delegate);
        query.setDelegate(delegate);
        Assert.assertEquals(0, query.getCount());
        EasyMock.verify(delegate);
    }

    @Test
    public void getResults_simpleQuery_returnsFourRecords() throws SQLException {
        FreeformQuery query = new FreeformQuery(
                "SELECT \"ID\",\"NAME\" FROM people", Arrays.asList("ID"),
                connectionPool);
        query.beginTransaction();
        ResultSet rs = query.getResults(0, 0);

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
        query.commit();
    }

    @Test
    public void getResults_moreComplexQuery_returnsThreeRecords()
            throws SQLException {
        FreeformQuery query = new FreeformQuery(
                "SELECT * FROM people WHERE \"NAME\" LIKE '%lle'",
                Arrays.asList("ID"), connectionPool);
        query.beginTransaction();
        ResultSet rs = query.getResults(0, 0);

        Assert.assertTrue(rs.next());
        Assert.assertEquals(0 + offset, rs.getInt(1));
        Assert.assertEquals("Ville", rs.getString(2));

        Assert.assertTrue(rs.next());
        Assert.assertEquals(1 + offset, rs.getInt(1));
        Assert.assertEquals("Kalle", rs.getString(2));

        Assert.assertTrue(rs.next());
        Assert.assertEquals(2 + offset, rs.getInt(1));
        Assert.assertEquals("Pelle", rs.getString(2));

        Assert.assertFalse(rs.next());
        query.commit();
    }

    @Test
    public void getResults_noDelegate5000Rows_returns5000rows()
            throws SQLException {
        DataGenerator.addFiveThousandPeople(connectionPool);

        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                Arrays.asList("ID"), connectionPool);
        query.beginTransaction();
        ResultSet rs = query.getResults(0, 0);
        for (int i = 0; i < 5000; i++) {
            Assert.assertTrue(rs.next());
        }
        Assert.assertFalse(rs.next());
        query.commit();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setFilters_noDelegate_shouldFail() {
        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                Arrays.asList("ID"), connectionPool);
        ArrayList<Filter> filters = new ArrayList<Filter>();
        filters.add(new Like("name", "%lle"));
        query.setFilters(filters);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setOrderBy_noDelegate_shouldFail() {
        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                Arrays.asList("ID"), connectionPool);
        query.setOrderBy(Arrays.asList(new OrderBy("name", true)));
    }

    @Test(expected = IllegalStateException.class)
    public void storeRow_noDelegateNoTransactionActive_shouldFail()
            throws SQLException {
        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                Arrays.asList("ID"), connectionPool);
        query.storeRow(new RowItem(new SQLContainer(query), new RowId(
                new Object[] { 1 }), null));
    }

    @Test
    public void storeRow_noDelegate_shouldFail() throws SQLException {
        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                Arrays.asList("ID"), connectionPool);
        SQLContainer container = EasyMock.createNiceMock(SQLContainer.class);
        EasyMock.replay(container);
        query.beginTransaction();
        try {
            query.storeRow(new RowItem(container,
                    new RowId(new Object[] { 1 }), null));
            Assert.fail("storeRow should fail when there is no delegate");
        } catch (UnsupportedOperationException e) {
            // Cleanup to make test connection pool happy
            query.rollback();
        }
    }

    @Test
    public void removeRow_noDelegate_shouldFail() throws SQLException {
        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                Arrays.asList("ID"), connectionPool);
        SQLContainer container = EasyMock.createNiceMock(SQLContainer.class);
        EasyMock.replay(container);
        query.beginTransaction();
        try {
            query.removeRow(new RowItem(container,
                    new RowId(new Object[] { 1 }), null));
            Assert.fail("removeRow should fail when there is no delgate");
        } catch (UnsupportedOperationException e) {
            // Cleanup to make test connection pool happy
            query.rollback();
        }
    }

    @Test
    public void commit_readOnly_shouldSucceed() throws SQLException {
        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                Arrays.asList("ID"), connectionPool);
        query.beginTransaction();
        query.commit();
    }

    @Test
    public void rollback_readOnly_shouldSucceed() throws SQLException {
        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                Arrays.asList("ID"), connectionPool);
        query.beginTransaction();
        query.rollback();
    }

    @Test(expected = SQLException.class)
    public void commit_noActiveTransaction_shouldFail() throws SQLException {
        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                Arrays.asList("ID"), connectionPool);
        query.commit();
    }

    @Test(expected = SQLException.class)
    public void rollback_noActiveTransaction_shouldFail() throws SQLException {
        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                Arrays.asList("ID"), connectionPool);
        query.rollback();
    }

    @Test
    public void containsRowWithKeys_simpleQueryWithExistingKeys_returnsTrue()
            throws SQLException {
        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                Arrays.asList("ID"), connectionPool);
        Assert.assertTrue(query.containsRowWithKey(1));
    }

    @Test
    public void containsRowWithKeys_simpleQueryWithNonexistingKeys_returnsTrue()
            throws SQLException {
        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                Arrays.asList("ID"), connectionPool);
        Assert.assertFalse(query.containsRowWithKey(1337));
    }

    // (expected = SQLException.class)
    @Test
    public void containsRowWithKeys_simpleQueryWithInvalidKeys_shouldFail()
            throws SQLException {
        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                Arrays.asList("ID"), connectionPool);
        Assert.assertFalse(query.containsRowWithKey(38796));
    }

    @Test
    public void containsRowWithKeys_queryContainingWhereClauseAndExistingKeys_returnsTrue()
            throws SQLException {
        FreeformQuery query = new FreeformQuery(
                "SELECT * FROM people WHERE \"NAME\" LIKE '%lle'",
                Arrays.asList("ID"), connectionPool);
        Assert.assertTrue(query.containsRowWithKey(1));
    }

    @Test
    public void containsRowWithKeys_queryContainingLowercaseWhereClauseAndExistingKeys_returnsTrue()
            throws SQLException {
        FreeformQuery query = new FreeformQuery(
                "select * from people where \"NAME\" like '%lle'",
                Arrays.asList("ID"), connectionPool);
        Assert.assertTrue(query.containsRowWithKey(1));
    }

    @Test
    public void containsRowWithKeys_nullKeys_shouldFailAndReleaseConnections()
            throws SQLException {
        FreeformQuery query = new FreeformQuery(
                "select * from people where \"NAME\" like '%lle'",
                Arrays.asList("ID"), connectionPool);
        try {
            query.containsRowWithKey(new Object[] { null });
        } catch (SQLException e) {
            // We should now be able to reserve two connections
            connectionPool.reserveConnection();
            connectionPool.reserveConnection();
        }
    }

    /*
     * -------- Tests with a delegate ---------
     */

    @Test
    public void setDelegate_noExistingDelegate_shouldRegisterNewDelegate() {
        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                Arrays.asList("ID"), connectionPool);
        FreeformQueryDelegate delegate = EasyMock
                .createMock(FreeformQueryDelegate.class);
        query.setDelegate(delegate);
        Assert.assertEquals(delegate, query.getDelegate());
    }

    @Test
    public void getResults_hasDelegate_shouldCallDelegate() throws SQLException {
        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                Arrays.asList("ID"), connectionPool);
        FreeformQueryDelegate delegate = EasyMock
                .createMock(FreeformQueryDelegate.class);
        if (SQLTestsConstants.db == DB.MSSQL) {
            EasyMock.expect(delegate.getQueryString(0, 2))
                    .andReturn(
                            "SELECT * FROM (SELECT row_number()"
                                    + "OVER (ORDER BY id ASC) AS rownum, * FROM people)"
                                    + " AS a WHERE a.rownum BETWEEN 0 AND 2");
        } else if (SQLTestsConstants.db == DB.ORACLE) {
            EasyMock.expect(delegate.getQueryString(0, 2))
                    .andReturn(
                            "SELECT * FROM (SELECT  x.*, ROWNUM AS r FROM"
                                    + " (SELECT * FROM people) x) WHERE r BETWEEN 1 AND 2");
        } else {
            EasyMock.expect(delegate.getQueryString(0, 2)).andReturn(
                    "SELECT * FROM people LIMIT 2 OFFSET 0");
        }
        EasyMock.replay(delegate);

        query.setDelegate(delegate);
        query.beginTransaction();
        query.getResults(0, 2);
        EasyMock.verify(delegate);
        query.commit();
    }

    @Test
    public void getResults_delegateImplementsGetQueryString_shouldHonorOffsetAndPagelength()
            throws SQLException {
        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                Arrays.asList("ID"), connectionPool);
        FreeformQueryDelegate delegate = EasyMock
                .createMock(FreeformQueryDelegate.class);
        if (SQLTestsConstants.db == DB.MSSQL) {
            EasyMock.expect(delegate.getQueryString(0, 2))
                    .andReturn(
                            "SELECT * FROM (SELECT row_number()"
                                    + "OVER (ORDER BY id ASC) AS rownum, * FROM people)"
                                    + " AS a WHERE a.rownum BETWEEN 0 AND 2");
        } else if (SQLTestsConstants.db == DB.ORACLE) {
            EasyMock.expect(delegate.getQueryString(0, 2))
                    .andReturn(
                            "SELECT * FROM (SELECT  x.*, ROWNUM AS r FROM"
                                    + " (SELECT * FROM people) x) WHERE r BETWEEN 1 AND 2");
        } else {
            EasyMock.expect(delegate.getQueryString(0, 2)).andReturn(
                    "SELECT * FROM people LIMIT 2 OFFSET 0");
        }
        EasyMock.replay(delegate);
        query.setDelegate(delegate);

        query.beginTransaction();
        ResultSet rs = query.getResults(0, 2);
        int rsoffset = 0;
        if (SQLTestsConstants.db == DB.MSSQL) {
            rsoffset++;
        }
        Assert.assertTrue(rs.next());
        Assert.assertEquals(0 + offset, rs.getInt(1 + rsoffset));
        Assert.assertEquals("Ville", rs.getString(2 + rsoffset));

        Assert.assertTrue(rs.next());
        Assert.assertEquals(1 + offset, rs.getInt(1 + rsoffset));
        Assert.assertEquals("Kalle", rs.getString(2 + rsoffset));

        Assert.assertFalse(rs.next());

        EasyMock.verify(delegate);
        query.commit();
    }

    @Test
    public void getResults_delegateRegistered5000Rows_returns100rows()
            throws SQLException {
        DataGenerator.addFiveThousandPeople(connectionPool);
        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                Arrays.asList("ID"), connectionPool);
        FreeformQueryDelegate delegate = EasyMock
                .createMock(FreeformQueryDelegate.class);
        if (SQLTestsConstants.db == DB.MSSQL) {
            EasyMock.expect(delegate.getQueryString(200, 100))
                    .andReturn(
                            "SELECT * FROM (SELECT row_number()"
                                    + "OVER (ORDER BY id ASC) AS rownum, * FROM people)"
                                    + " AS a WHERE a.rownum BETWEEN 201 AND 300");
        } else if (SQLTestsConstants.db == DB.ORACLE) {
            EasyMock.expect(delegate.getQueryString(200, 100))
                    .andReturn(
                            "SELECT * FROM (SELECT  x.*, ROWNUM AS r FROM"
                                    + " (SELECT * FROM people ORDER BY ID ASC) x) WHERE r BETWEEN 201 AND 300");
        } else {
            EasyMock.expect(delegate.getQueryString(200, 100)).andReturn(
                    "SELECT * FROM people LIMIT 100 OFFSET 200");
        }
        EasyMock.replay(delegate);
        query.setDelegate(delegate);

        query.beginTransaction();
        ResultSet rs = query.getResults(200, 100);
        for (int i = 0; i < 100; i++) {
            Assert.assertTrue(rs.next());
            Assert.assertEquals(200 + i + offset, rs.getInt("ID"));
        }
        Assert.assertFalse(rs.next());
        query.commit();
    }

    @Test
    public void setFilters_delegateImplementsSetFilters_shouldPassFiltersToDelegate() {
        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                Arrays.asList("ID"), connectionPool);
        FreeformQueryDelegate delegate = EasyMock
                .createMock(FreeformQueryDelegate.class);
        List<Filter> filters = new ArrayList<Filter>();
        filters.add(new Like("name", "%lle"));
        delegate.setFilters(filters);

        EasyMock.replay(delegate);
        query.setDelegate(delegate);

        query.setFilters(filters);

        EasyMock.verify(delegate);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setFilters_delegateDoesNotImplementSetFilters_shouldFail() {
        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                Arrays.asList("ID"), connectionPool);
        FreeformQueryDelegate delegate = EasyMock
                .createMock(FreeformQueryDelegate.class);
        List<Filter> filters = new ArrayList<Filter>();
        filters.add(new Like("name", "%lle"));
        delegate.setFilters(filters);
        EasyMock.expectLastCall().andThrow(new UnsupportedOperationException());
        EasyMock.replay(delegate);
        query.setDelegate(delegate);

        query.setFilters(filters);

        EasyMock.verify(delegate);
    }

    @Test
    public void setOrderBy_delegateImplementsSetOrderBy_shouldPassArgumentsToDelegate() {
        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                Arrays.asList("ID"), connectionPool);
        FreeformQueryDelegate delegate = EasyMock
                .createMock(FreeformQueryDelegate.class);
        List<OrderBy> orderBys = Arrays.asList(new OrderBy("name", false));
        delegate.setOrderBy(orderBys);
        EasyMock.replay(delegate);
        query.setDelegate(delegate);

        query.setOrderBy(orderBys);

        EasyMock.verify(delegate);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setOrderBy_delegateDoesNotImplementSetOrderBy_shouldFail() {
        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                Arrays.asList("ID"), connectionPool);
        FreeformQueryDelegate delegate = EasyMock
                .createMock(FreeformQueryDelegate.class);
        List<OrderBy> orderBys = Arrays.asList(new OrderBy("name", false));
        delegate.setOrderBy(orderBys);
        EasyMock.expectLastCall().andThrow(new UnsupportedOperationException());
        EasyMock.replay(delegate);
        query.setDelegate(delegate);

        query.setOrderBy(orderBys);

        EasyMock.verify(delegate);
    }

    @Test
    public void setFilters_noDelegateAndNullParameter_shouldSucceed() {
        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                Arrays.asList("ID"), connectionPool);
        query.setFilters(null);
    }

    @Test
    public void setOrderBy_noDelegateAndNullParameter_shouldSucceed() {
        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                Arrays.asList("ID"), connectionPool);
        query.setOrderBy(null);
    }

    @Test
    public void storeRow_delegateImplementsStoreRow_shouldPassToDelegate()
            throws SQLException {
        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                Arrays.asList("ID"), connectionPool);
        FreeformQueryDelegate delegate = EasyMock
                .createMock(FreeformQueryDelegate.class);
        EasyMock.expect(
                delegate.storeRow(EasyMock.isA(Connection.class),
                        EasyMock.isA(RowItem.class))).andReturn(1);
        SQLContainer container = EasyMock.createNiceMock(SQLContainer.class);
        EasyMock.replay(delegate, container);
        query.setDelegate(delegate);

        query.beginTransaction();
        RowItem row = new RowItem(container, new RowId(new Object[] { 1 }),
                null);
        query.storeRow(row);
        query.commit();

        EasyMock.verify(delegate, container);
    }

    @Test
    public void storeRow_delegateDoesNotImplementStoreRow_shouldFail()
            throws SQLException {
        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                Arrays.asList("ID"), connectionPool);
        FreeformQueryDelegate delegate = EasyMock
                .createMock(FreeformQueryDelegate.class);
        EasyMock.expect(
                delegate.storeRow(EasyMock.isA(Connection.class),
                        EasyMock.isA(RowItem.class))).andThrow(
                new UnsupportedOperationException());
        SQLContainer container = EasyMock.createNiceMock(SQLContainer.class);
        EasyMock.replay(delegate, container);
        query.setDelegate(delegate);

        query.beginTransaction();
        RowItem row = new RowItem(container, new RowId(new Object[] { 1 }),
                null);
        try {
            query.storeRow(row);
            Assert.fail("storeRow should fail when delgate does not implement storeRow");
        } catch (UnsupportedOperationException e) {
            // Cleanup to make test connection pool happy
            query.rollback();
        }
    }

    @Test
    public void removeRow_delegateImplementsRemoveRow_shouldPassToDelegate()
            throws SQLException {
        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                Arrays.asList("ID"), connectionPool);
        FreeformQueryDelegate delegate = EasyMock
                .createMock(FreeformQueryDelegate.class);
        EasyMock.expect(
                delegate.removeRow(EasyMock.isA(Connection.class),
                        EasyMock.isA(RowItem.class))).andReturn(true);
        SQLContainer container = EasyMock.createNiceMock(SQLContainer.class);
        EasyMock.replay(delegate, container);
        query.setDelegate(delegate);

        query.beginTransaction();
        RowItem row = new RowItem(container, new RowId(new Object[] { 1 }),
                null);
        query.removeRow(row);
        query.commit();

        EasyMock.verify(delegate, container);
    }

    @Test
    public void removeRow_delegateDoesNotImplementRemoveRow_shouldFail()
            throws SQLException {
        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                Arrays.asList("ID"), connectionPool);
        FreeformQueryDelegate delegate = EasyMock
                .createMock(FreeformQueryDelegate.class);
        EasyMock.expect(
                delegate.removeRow(EasyMock.isA(Connection.class),
                        EasyMock.isA(RowItem.class))).andThrow(
                new UnsupportedOperationException());
        SQLContainer container = EasyMock.createNiceMock(SQLContainer.class);
        EasyMock.replay(delegate, container);
        query.setDelegate(delegate);

        query.beginTransaction();
        RowItem row = new RowItem(container, new RowId(new Object[] { 1 }),
                null);
        try {
            query.removeRow(row);
            Assert.fail("removeRow should fail when delegate does not implement removeRow");
        } catch (UnsupportedOperationException e) {
            // Cleanup to make test connection pool happy
            query.rollback();
        }
    }

    @Test
    public void beginTransaction_delegateRegistered_shouldSucceed()
            throws UnsupportedOperationException, SQLException {
        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                Arrays.asList("ID"), connectionPool);
        FreeformQueryDelegate delegate = EasyMock
                .createMock(FreeformQueryDelegate.class);
        EasyMock.replay(delegate);
        query.setDelegate(delegate);

        query.beginTransaction();
        // Cleanup to make test connection pool happy
        query.rollback();
    }

    @Test
    public void beginTransaction_transactionAlreadyActive_shouldFail()
            throws SQLException {
        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                Arrays.asList("ID"), connectionPool);

        query.beginTransaction();
        try {
            query.beginTransaction();
            Assert.fail("Should throw exception when starting a transaction while already in a transaction");
        } catch (IllegalStateException e) {
            // Cleanup to make test connection pool happy
            query.rollback();
        }
    }

    @Test(expected = SQLException.class)
    public void commit_delegateRegisteredNoActiveTransaction_shouldFail()
            throws UnsupportedOperationException, SQLException {
        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                Arrays.asList("ID"), connectionPool);
        FreeformQueryDelegate delegate = EasyMock
                .createMock(FreeformQueryDelegate.class);
        EasyMock.replay(delegate);
        query.setDelegate(delegate);

        query.commit();
    }

    @Test
    public void commit_delegateRegisteredActiveTransaction_shouldSucceed()
            throws UnsupportedOperationException, SQLException {
        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                Arrays.asList("ID"), connectionPool);
        FreeformQueryDelegate delegate = EasyMock
                .createMock(FreeformQueryDelegate.class);
        EasyMock.replay(delegate);
        query.setDelegate(delegate);

        query.beginTransaction();
        query.commit();
    }

    @Test(expected = SQLException.class)
    public void commit_delegateRegisteredActiveTransactionDoubleCommit_shouldFail()
            throws UnsupportedOperationException, SQLException {
        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                Arrays.asList("ID"), connectionPool);
        FreeformQueryDelegate delegate = EasyMock
                .createMock(FreeformQueryDelegate.class);
        EasyMock.replay(delegate);
        query.setDelegate(delegate);

        query.beginTransaction();
        query.commit();
        query.commit();
    }

    @Test(expected = SQLException.class)
    public void rollback_delegateRegisteredNoActiveTransaction_shouldFail()
            throws UnsupportedOperationException, SQLException {
        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                Arrays.asList("ID"), connectionPool);
        FreeformQueryDelegate delegate = EasyMock
                .createMock(FreeformQueryDelegate.class);
        EasyMock.replay(delegate);
        query.setDelegate(delegate);

        query.rollback();
    }

    @Test
    public void rollback_delegateRegisteredActiveTransaction_shouldSucceed()
            throws UnsupportedOperationException, SQLException {
        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                Arrays.asList("ID"), connectionPool);
        FreeformQueryDelegate delegate = EasyMock
                .createMock(FreeformQueryDelegate.class);
        EasyMock.replay(delegate);
        query.setDelegate(delegate);

        query.beginTransaction();
        query.rollback();
    }

    @Test(expected = SQLException.class)
    public void rollback_delegateRegisteredActiveTransactionDoubleRollback_shouldFail()
            throws UnsupportedOperationException, SQLException {
        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                Arrays.asList("ID"), connectionPool);
        FreeformQueryDelegate delegate = EasyMock
                .createMock(FreeformQueryDelegate.class);
        EasyMock.replay(delegate);
        query.setDelegate(delegate);

        query.beginTransaction();
        query.rollback();
        query.rollback();
    }

    @Test(expected = SQLException.class)
    public void rollback_delegateRegisteredCommittedTransaction_shouldFail()
            throws UnsupportedOperationException, SQLException {
        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                Arrays.asList("ID"), connectionPool);
        FreeformQueryDelegate delegate = EasyMock
                .createMock(FreeformQueryDelegate.class);
        EasyMock.replay(delegate);
        query.setDelegate(delegate);

        query.beginTransaction();
        query.commit();
        query.rollback();
    }

    @Test(expected = SQLException.class)
    public void commit_delegateRegisteredRollbackedTransaction_shouldFail()
            throws UnsupportedOperationException, SQLException {
        FreeformQuery query = new FreeformQuery("SELECT * FROM people",
                Arrays.asList("ID"), connectionPool);
        FreeformQueryDelegate delegate = EasyMock
                .createMock(FreeformQueryDelegate.class);
        EasyMock.replay(delegate);
        query.setDelegate(delegate);

        query.beginTransaction();
        query.rollback();
        query.commit();
    }

    @Test(expected = SQLException.class)
    public void containsRowWithKeys_delegateRegistered_shouldCallGetContainsRowQueryString()
            throws SQLException {
        FreeformQuery query = new FreeformQuery(
                "SELECT * FROM people WHERE name LIKE '%lle'",
                Arrays.asList("ID"), connectionPool);
        FreeformQueryDelegate delegate = EasyMock
                .createMock(FreeformQueryDelegate.class);
        EasyMock.expect(delegate.getContainsRowQueryString(1)).andReturn("");
        EasyMock.replay(delegate);
        query.setDelegate(delegate);

        query.containsRowWithKey(1);

        EasyMock.verify(delegate);
    }

    @Test
    public void containsRowWithKeys_delegateRegistered_shouldUseResultFromGetContainsRowQueryString()
            throws SQLException {
        FreeformQuery query = new FreeformQuery(
                "SELECT * FROM people WHERE \"NAME\" LIKE '%lle'",
                Arrays.asList("ID"), connectionPool);
        FreeformQueryDelegate delegate = EasyMock
                .createMock(FreeformQueryDelegate.class);
        // In order to test that this is the query that is actually used, we use
        // a non-existing id in place of the existing one.
        EasyMock.expect(delegate.getContainsRowQueryString(1))
                .andReturn(
                        "SELECT * FROM people WHERE \"NAME\" LIKE '%lle' AND \"ID\" = 1337");
        EasyMock.replay(delegate);
        query.setDelegate(delegate);

        // The id (key) used should be 1337 as above, for the call with key = 1
        Assert.assertFalse(query.containsRowWithKey(1));

        EasyMock.verify(delegate);
    }

    public static class NonMatchingDelegateWithGroupBy implements
            FreeformQueryDelegate {

        private String fromWhere = "FROM people p1 LEFT JOIN people p2 ON p2.id = p1.id WHERE p1.\"NAME\" LIKE 'notfound' GROUP BY p1.ID";

        @Override
        public int storeRow(Connection conn, RowItem row)
                throws UnsupportedOperationException, SQLException {
            // Not used in this test
            return 0;
        }

        @Override
        public void setOrderBy(List<OrderBy> orderBys)
                throws UnsupportedOperationException {
            // Not used in this test
        }

        @Override
        public void setFilters(List<Filter> filters)
                throws UnsupportedOperationException {
            // Not used in this test
        }

        @Override
        public boolean removeRow(Connection conn, RowItem row)
                throws UnsupportedOperationException, SQLException {
            // Not used in this test
            return false;
        }

        @Override
        public String getQueryString(int offset, int limit)
                throws UnsupportedOperationException {
            return "SELECT * " + fromWhere;
        }

        @Override
        public String getCountQuery() throws UnsupportedOperationException {
            return "SELECT COUNT(*) " + fromWhere;
        }

        @Override
        public String getContainsRowQueryString(Object... keys)
                throws UnsupportedOperationException {
            // Not used in this test
            return null;
        }
    }

    public static class NonMatchingStatementDelegateWithGroupBy extends
            NonMatchingDelegateWithGroupBy implements FreeformStatementDelegate {

        @Override
        public StatementHelper getQueryStatement(int offset, int limit)
                throws UnsupportedOperationException {
            StatementHelper sh = new StatementHelper();
            sh.setQueryString(getQueryString(offset, limit));
            return sh;
        }

        @Override
        public StatementHelper getCountStatement()
                throws UnsupportedOperationException {
            StatementHelper sh = new StatementHelper();
            sh.setQueryString(getCountQuery());
            return sh;
        }

        @Override
        public StatementHelper getContainsRowQueryStatement(Object... keys)
                throws UnsupportedOperationException {
            // Not used in this test
            return null;
        }
    }

    @Test
    public void containsRowWithKeys_delegateRegisteredGetContainsRowQueryStringNotImplemented_shouldBuildQueryString()
            throws SQLException {
        FreeformQuery query = new FreeformQuery(
                "SELECT * FROM people WHERE \"NAME\" LIKE '%lle'",
                Arrays.asList("ID"), connectionPool);
        FreeformQueryDelegate delegate = EasyMock
                .createMock(FreeformQueryDelegate.class);
        EasyMock.expect(delegate.getContainsRowQueryString(1)).andThrow(
                new UnsupportedOperationException());
        EasyMock.replay(delegate);
        query.setDelegate(delegate);

        Assert.assertTrue(query.containsRowWithKey(1));

        EasyMock.verify(delegate);
    }

    @Test
    public void delegateStatementCountWithGroupBy() throws SQLException {
        String dummyNotUsed = "foo";
        FreeformQuery query = new FreeformQuery(dummyNotUsed, connectionPool,
                "p1.ID");
        query.setDelegate(new NonMatchingStatementDelegateWithGroupBy());

        Assert.assertEquals(0, query.getCount());
    }

    @Test
    public void delegateCountWithGroupBy() throws SQLException {
        String dummyNotUsed = "foo";
        FreeformQuery query = new FreeformQuery(dummyNotUsed, connectionPool,
                "p1.ID");
        query.setDelegate(new NonMatchingDelegateWithGroupBy());

        Assert.assertEquals(0, query.getCount());
    }
}

package com.vaadin.data.util.sqlcontainer.generator;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.Like;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.sqlcontainer.DataGenerator;
import com.vaadin.data.util.sqlcontainer.RowItem;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.SQLTestsConstants;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.data.util.sqlcontainer.query.generator.DefaultSQLGenerator;
import com.vaadin.data.util.sqlcontainer.query.generator.MSSQLGenerator;
import com.vaadin.data.util.sqlcontainer.query.generator.OracleGenerator;
import com.vaadin.data.util.sqlcontainer.query.generator.SQLGenerator;
import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;

public class SQLGeneratorsTest {
    private JDBCConnectionPool connectionPool;

    @Before
    public void setUp() throws SQLException {

        try {
            connectionPool = new SimpleJDBCConnectionPool(
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
    public void generateSelectQuery_basicQuery_shouldSucceed() {
        SQLGenerator sg = new DefaultSQLGenerator();
        StatementHelper sh = sg.generateSelectQuery("TABLE", null, null, 0, 0,
                null);
        Assert.assertEquals(sh.getQueryString(), "SELECT * FROM TABLE");
    }

    @Test
    public void generateSelectQuery_pagingAndColumnsSet_shouldSucceed() {
        SQLGenerator sg = new DefaultSQLGenerator();
        StatementHelper sh = sg.generateSelectQuery("TABLE", null, null, 4, 8,
                "COL1, COL2, COL3");
        Assert.assertEquals(sh.getQueryString(),
                "SELECT COL1, COL2, COL3 FROM TABLE LIMIT 8 OFFSET 4");
    }

    /**
     * Note: Only tests one kind of filter and ordering.
     */
    @Test
    public void generateSelectQuery_filtersAndOrderingSet_shouldSucceed() {
        SQLGenerator sg = new DefaultSQLGenerator();
        List<com.vaadin.data.Container.Filter> f = new ArrayList<Filter>();
        f.add(new Like("name", "%lle"));
        List<OrderBy> ob = Arrays.asList(new OrderBy("name", true));
        StatementHelper sh = sg.generateSelectQuery("TABLE", f, ob, 0, 0, null);
        Assert.assertEquals(sh.getQueryString(),
                "SELECT * FROM TABLE WHERE \"name\" LIKE ? ORDER BY \"name\" ASC");
    }

    @Test
    public void generateSelectQuery_filtersAndOrderingSet_exclusiveFilteringMode_shouldSucceed() {
        SQLGenerator sg = new DefaultSQLGenerator();
        List<Filter> f = new ArrayList<Filter>();
        f.add(new Or(new Like("name", "%lle"), new Like("name", "vi%")));
        List<OrderBy> ob = Arrays.asList(new OrderBy("name", true));
        StatementHelper sh = sg.generateSelectQuery("TABLE", f, ob, 0, 0, null);
        // TODO
        Assert.assertEquals(sh.getQueryString(),
                "SELECT * FROM TABLE WHERE (\"name\" LIKE ? "
                        + "OR \"name\" LIKE ?) ORDER BY \"name\" ASC");
    }

    @Test
    public void generateDeleteQuery_basicQuery_shouldSucceed()
            throws SQLException {
        /*
         * No need to run this for Oracle/MSSQL generators since the
         * DefaultSQLGenerator method would be called anyway.
         */
        if (SQLTestsConstants.sqlGen instanceof MSSQLGenerator
                || SQLTestsConstants.sqlGen instanceof OracleGenerator) {
            return;
        }
        SQLGenerator sg = SQLTestsConstants.sqlGen;
        TableQuery query = new TableQuery("people", connectionPool,
                SQLTestsConstants.sqlGen);
        SQLContainer container = new SQLContainer(query);

        StatementHelper sh = sg.generateDeleteQuery(
                "people",
                query.getPrimaryKeyColumns(),
                null,
                (RowItem) container.getItem(container.getItemIds().iterator()
                        .next()));
        Assert.assertEquals("DELETE FROM people WHERE \"ID\" = ?",
                sh.getQueryString());
    }

    @Test
    public void generateUpdateQuery_basicQuery_shouldSucceed()
            throws SQLException {
        /*
         * No need to run this for Oracle/MSSQL generators since the
         * DefaultSQLGenerator method would be called anyway.
         */
        if (SQLTestsConstants.sqlGen instanceof MSSQLGenerator
                || SQLTestsConstants.sqlGen instanceof OracleGenerator) {
            return;
        }
        SQLGenerator sg = new DefaultSQLGenerator();
        TableQuery query = new TableQuery("people", connectionPool);
        SQLContainer container = new SQLContainer(query);

        RowItem ri = (RowItem) container.getItem(container.getItemIds()
                .iterator().next());
        ri.getItemProperty("NAME").setValue("Viljami");

        StatementHelper sh = sg.generateUpdateQuery("people", ri);
        Assert.assertTrue("UPDATE people SET \"NAME\" = ?, \"AGE\" = ? WHERE \"ID\" = ?"
                .equals(sh.getQueryString())
                || "UPDATE people SET \"AGE\" = ?, \"NAME\" = ? WHERE \"ID\" = ?"
                        .equals(sh.getQueryString()));
    }

    @Test
    public void generateInsertQuery_basicQuery_shouldSucceed()
            throws SQLException {
        /*
         * No need to run this for Oracle/MSSQL generators since the
         * DefaultSQLGenerator method would be called anyway.
         */
        if (SQLTestsConstants.sqlGen instanceof MSSQLGenerator
                || SQLTestsConstants.sqlGen instanceof OracleGenerator) {
            return;
        }
        SQLGenerator sg = new DefaultSQLGenerator();
        TableQuery query = new TableQuery("people", connectionPool);
        SQLContainer container = new SQLContainer(query);

        RowItem ri = (RowItem) container.getItem(container.addItem());
        ri.getItemProperty("NAME").setValue("Viljami");

        StatementHelper sh = sg.generateInsertQuery("people", ri);

        Assert.assertTrue("INSERT INTO people (\"NAME\", \"AGE\") VALUES (?, ?)"
                .equals(sh.getQueryString())
                || "INSERT INTO people (\"AGE\", \"NAME\") VALUES (?, ?)"
                        .equals(sh.getQueryString()));
    }

    @Test
    public void generateComplexSelectQuery_forOracle_shouldSucceed()
            throws SQLException {
        SQLGenerator sg = new OracleGenerator();
        List<Filter> f = new ArrayList<Filter>();
        f.add(new Like("name", "%lle"));
        List<OrderBy> ob = Arrays.asList(new OrderBy("name", true));
        StatementHelper sh = sg.generateSelectQuery("TABLE", f, ob, 4, 8,
                "NAME, ID");
        Assert.assertEquals(
                "SELECT * FROM (SELECT x.*, ROWNUM AS \"rownum\" FROM"
                        + " (SELECT NAME, ID FROM TABLE WHERE \"name\" LIKE ?"
                        + " ORDER BY \"name\" ASC) x) WHERE \"rownum\" BETWEEN 5 AND 12",
                sh.getQueryString());
    }

    @Test
    public void generateComplexSelectQuery_forMSSQL_shouldSucceed()
            throws SQLException {
        SQLGenerator sg = new MSSQLGenerator();
        List<Filter> f = new ArrayList<Filter>();
        f.add(new Like("name", "%lle"));
        List<OrderBy> ob = Arrays.asList(new OrderBy("name", true));
        StatementHelper sh = sg.generateSelectQuery("TABLE", f, ob, 4, 8,
                "NAME, ID");
        Assert.assertEquals(sh.getQueryString(),
                "SELECT * FROM (SELECT row_number() OVER "
                        + "( ORDER BY \"name\" ASC) AS rownum, NAME, ID "
                        + "FROM TABLE WHERE \"name\" LIKE ?) "
                        + "AS a WHERE a.rownum BETWEEN 5 AND 12");
    }

    @Test
    public void generateComplexSelectQuery_forOracle_exclusiveFilteringMode_shouldSucceed()
            throws SQLException {
        SQLGenerator sg = new OracleGenerator();
        List<Filter> f = new ArrayList<Filter>();
        f.add(new Or(new Like("name", "%lle"), new Like("name", "vi%")));
        List<OrderBy> ob = Arrays.asList(new OrderBy("name", true));
        StatementHelper sh = sg.generateSelectQuery("TABLE", f, ob, 4, 8,
                "NAME, ID");
        Assert.assertEquals(
                sh.getQueryString(),
                "SELECT * FROM (SELECT x.*, ROWNUM AS \"rownum\" FROM"
                        + " (SELECT NAME, ID FROM TABLE WHERE (\"name\" LIKE ?"
                        + " OR \"name\" LIKE ?) "
                        + "ORDER BY \"name\" ASC) x) WHERE \"rownum\" BETWEEN 5 AND 12");
    }

    @Test
    public void generateComplexSelectQuery_forMSSQL_exclusiveFilteringMode_shouldSucceed()
            throws SQLException {
        SQLGenerator sg = new MSSQLGenerator();
        List<Filter> f = new ArrayList<Filter>();
        f.add(new Or(new Like("name", "%lle"), new Like("name", "vi%")));
        List<OrderBy> ob = Arrays.asList(new OrderBy("name", true));
        StatementHelper sh = sg.generateSelectQuery("TABLE", f, ob, 4, 8,
                "NAME, ID");
        Assert.assertEquals(sh.getQueryString(),
                "SELECT * FROM (SELECT row_number() OVER "
                        + "( ORDER BY \"name\" ASC) AS rownum, NAME, ID "
                        + "FROM TABLE WHERE (\"name\" LIKE ? "
                        + "OR \"name\" LIKE ?)) "
                        + "AS a WHERE a.rownum BETWEEN 5 AND 12");
    }
}

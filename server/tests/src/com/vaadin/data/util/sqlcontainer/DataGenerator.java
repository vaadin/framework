package com.vaadin.data.util.sqlcontainer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.util.sqlcontainer.SQLTestsConstants.DB;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;

public class DataGenerator {

    @Test
    public void testDummy() {
        // Added dummy test so JUnit will not complain about
        // "No runnable methods".
    }

    public static void addPeopleToDatabase(JDBCConnectionPool connectionPool)
            throws SQLException {
        Connection conn = connectionPool.reserveConnection();
        Statement statement = conn.createStatement();
        try {
            statement.execute("drop table PEOPLE");
            if (SQLTestsConstants.db == DB.ORACLE) {
                statement.execute("drop sequence people_seq");
            }
        } catch (SQLException e) {
            // Will fail if table doesn't exist, which is OK.
            conn.rollback();
        }
        statement.execute(SQLTestsConstants.peopleFirst);
        if (SQLTestsConstants.peopleSecond != null) {
            statement.execute(SQLTestsConstants.peopleSecond);
        }
        if (SQLTestsConstants.db == DB.ORACLE) {
            statement.execute(SQLTestsConstants.peopleThird);
        }
        if (SQLTestsConstants.db == DB.MSSQL) {
            statement.executeUpdate("insert into people values('Ville', '23')");
            statement.executeUpdate("insert into people values('Kalle', '7')");
            statement.executeUpdate("insert into people values('Pelle', '18')");
            statement.executeUpdate("insert into people values('Börje', '64')");
        } else {
            statement
                    .executeUpdate("insert into people values(default, 'Ville', '23')");
            statement
                    .executeUpdate("insert into people values(default, 'Kalle', '7')");
            statement
                    .executeUpdate("insert into people values(default, 'Pelle', '18')");
            statement
                    .executeUpdate("insert into people values(default, 'Börje', '64')");
        }
        statement.close();
        statement = conn.createStatement();
        ResultSet rs = statement.executeQuery("select * from PEOPLE");
        Assert.assertTrue(rs.next());
        statement.close();
        conn.commit();
        connectionPool.releaseConnection(conn);
    }

    public static void addFiveThousandPeople(JDBCConnectionPool connectionPool)
            throws SQLException {
        Connection conn = connectionPool.reserveConnection();
        Statement statement = conn.createStatement();
        for (int i = 4; i < 5000; i++) {
            if (SQLTestsConstants.db == DB.MSSQL) {
                statement.executeUpdate("insert into people values('Person "
                        + i + "', '" + i % 99 + "')");
            } else {
                statement
                        .executeUpdate("insert into people values(default, 'Person "
                                + i + "', '" + i % 99 + "')");
            }
        }
        statement.close();
        conn.commit();
        connectionPool.releaseConnection(conn);
    }

    public static void addVersionedData(JDBCConnectionPool connectionPool)
            throws SQLException {
        Connection conn = connectionPool.reserveConnection();
        Statement statement = conn.createStatement();
        try {
            statement.execute("DROP TABLE VERSIONED");
            if (SQLTestsConstants.db == DB.ORACLE) {
                statement.execute("drop sequence versioned_seq");
                statement.execute("drop sequence versioned_version");
            }
        } catch (SQLException e) {
            // Will fail if table doesn't exist, which is OK.
            conn.rollback();
        }
        for (String stmtString : SQLTestsConstants.versionStatements) {
            statement.execute(stmtString);
        }
        if (SQLTestsConstants.db == DB.MSSQL) {
            statement
                    .executeUpdate("insert into VERSIONED values('Junk', default)");
        } else {
            statement
                    .executeUpdate("insert into VERSIONED values(default, 'Junk', default)");
        }
        statement.close();
        statement = conn.createStatement();
        ResultSet rs = statement.executeQuery("select * from VERSIONED");
        Assert.assertTrue(rs.next());
        statement.close();
        conn.commit();
        connectionPool.releaseConnection(conn);
    }

    public static void createGarbage(JDBCConnectionPool connectionPool)
            throws SQLException {
        Connection conn = connectionPool.reserveConnection();
        Statement statement = conn.createStatement();
        try {
            statement.execute("drop table GARBAGE");
            if (SQLTestsConstants.db == DB.ORACLE) {
                statement.execute("drop sequence garbage_seq");
            }
        } catch (SQLException e) {
            // Will fail if table doesn't exist, which is OK.
            conn.rollback();
        }
        statement.execute(SQLTestsConstants.createGarbage);
        if (SQLTestsConstants.db == DB.ORACLE) {
            statement.execute(SQLTestsConstants.createGarbageSecond);
            statement.execute(SQLTestsConstants.createGarbageThird);
        }
        conn.commit();
        connectionPool.releaseConnection(conn);
    }
}

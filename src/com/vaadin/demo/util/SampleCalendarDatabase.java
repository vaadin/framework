/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.demo.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

/**
 * Creates temporary database named toolkit with sample table named employee and
 * populates it with data. By default we use HSQLDB. Ensure that you have
 * hsqldb.jar under WEB-INF/lib directory. Database is will be created into
 * memory.
 * 
 * @author IT Mill Ltd.
 * 
 */
public class SampleCalendarDatabase {

    public static final int ENTRYCOUNT = 100;

    public static final String DB_TABLE_NAME = "calendar";
    public static final String PROPERTY_ID_START = "EVENTSTART";
    public static final String PROPERTY_ID_END = "EVENTEND";
    public static final String PROPERTY_ID_TITLE = "TITLE";
    public static final String PROPERTY_ID_NOTIME = "NOTIME";

    private Connection connection = null;

    private static final String[] titles = new String[] { "Meeting", "Dentist",
            "Haircut", "Bank", "Birthday", "Library", "Rent", "Acme test",
            "Party" };

    /**
     * Create temporary database.
     * 
     */
    public SampleCalendarDatabase() {
        // connect to SQL database
        connect();

        // initialize SQL database
        createTables();

        // test by executing sample JDBC query
        testDatabase();
    }

    /**
     * Creates sample table named employee and populates it with data.Use the
     * specified database connection.
     * 
     * @param connection
     */
    public SampleCalendarDatabase(Connection connection) {
        // initialize SQL database
        createTables();

        // test by executing sample JDBC query
        testDatabase();
    }

    /**
     * Connect to SQL database. In this sample we use HSQLDB and an toolkit
     * named database in implicitly created into system memory.
     * 
     */
    private void connect() {
        // use memory-Only Database
        final String url = "jdbc:hsqldb:mem:toolkit";
        try {
            Class.forName("org.hsqldb.jdbcDriver").newInstance();
            connection = DriverManager.getConnection(url, "sa", "");
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * use for SQL commands CREATE, DROP, INSERT and UPDATE
     * 
     * @param expression
     * @throws SQLException
     */
    public void update(String expression) throws SQLException {
        Statement st = null;
        st = connection.createStatement();
        final int i = st.executeUpdate(expression);
        if (i == -1) {
            System.out.println("SampleDatabase error : " + expression);
        }
        st.close();
    }

    /**
     * Create test table and few rows. Issue note: using capitalized column
     * names as HSQLDB returns column names in capitalized form with this demo.
     * 
     */
    @SuppressWarnings("deprecation")
    private void createTables() {
        try {
            String stmt = null;
            stmt = "CREATE TABLE "
                    + DB_TABLE_NAME
                    + " ( ID INTEGER IDENTITY, TITLE VARCHAR(100), "
                    + "EVENTSTART DATETIME, EVENTEND DATETIME, NOTIME BOOLEAN  )";
            update(stmt);
            for (int j = 0; j < ENTRYCOUNT; j++) {
                final Timestamp start = new Timestamp(new java.util.Date()
                        .getTime());
                start.setDate((int) ((Math.random() - 0.4) * 200));
                start.setMinutes(0);
                start.setHours(8 + (int) Math.random() * 12);
                final Timestamp end = new Timestamp(start.getTime());
                if (Math.random() < 0.7) {
                    long t = end.getTime();
                    final long hour = 60 * 60 * 1000;
                    t = t + hour + (Math.round(Math.random() * 3 * hour));
                    end.setTime(t);
                }

                stmt = "INSERT INTO "
                        + DB_TABLE_NAME
                        + "(TITLE, EVENTSTART, EVENTEND, NOTIME) VALUES ("
                        + "'"
                        + titles[(int) (Math.round(Math.random()
                                * (titles.length - 1)))] + "','" + start
                        + "','" + end + "'," + (Math.random() > 0.7) + ")";
                update(stmt);
            }
        } catch (final SQLException e) {
            if (e.toString().indexOf("Table already exists") == -1) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Test database connection with simple SELECT command.
     * 
     */
    private String testDatabase() {
        String result = null;
        try {
            final Statement stmt = connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            final ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM "
                    + DB_TABLE_NAME);
            rs.next();
            result = "rowcount for table test is " + rs.getObject(1).toString();
            stmt.close();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public Connection getConnection() {
        return connection;
    }

}

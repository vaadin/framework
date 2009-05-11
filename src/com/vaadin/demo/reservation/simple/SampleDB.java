/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.demo.reservation.simple;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.QueryContainer;
import com.vaadin.demo.reservation.ResourceNotAvailableException;

/**
 * Simplified version of Reservr's SampleDB
 * 
 * If you are going to use this application in production, make sure to modify
 * this to save data into real DB instead of in memory HSQLDB.
 */
public class SampleDB {

    public class Resource {
        public static final String TABLE = "RESOURCE";
        public static final String PROPERTY_ID_ID = TABLE + "_ID";
        public static final String PROPERTY_ID_STYLENAME = TABLE + "_STYLENAME";
        public static final String PROPERTY_ID_NAME = TABLE + "_NAME";
        public static final String PROPERTY_ID_DESCRIPTION = TABLE
                + "_DESCRIPTION";
        public static final String PROPERTY_ID_LOCATIONX = TABLE
                + "_LOCATION_X";
        public static final String PROPERTY_ID_LOCATIONY = TABLE
                + "_LOCATION_Y";
        public static final String PROPERTY_ID_CATEGORY = TABLE + "_CATEGORY";
        public static final String PROPERTY_ID_DELETED = TABLE + "_DELETED";
    }

    public class Reservation {
        public static final String TABLE = "RESERVATION";
        public static final String PROPERTY_ID_ID = TABLE + "_ID";
        public static final String PROPERTY_ID_DESCRIPTION = TABLE
                + "_DESCRIPTION";
        public static final String PROPERTY_ID_RESOURCE_ID = TABLE
                + "_RESOURCE_ID";
        public static final String PROPERTY_ID_RESERVED_BY = TABLE
                + "_RESERVED_BY_USER";
        public static final String PROPERTY_ID_RESERVED_FROM = TABLE
                + "_RESERVED_FROM";
        public static final String PROPERTY_ID_RESERVED_TO = TABLE
                + "_RESERVED_TO";
    }

    // TODO -> param
    private static final String DB_URL = "jdbc:hsqldb:file:reservationsimple.db";

    private static final String CREATE_TABLE_RESOURCE = "CREATE TABLE "
            + Resource.TABLE + " (" + " " + Resource.PROPERTY_ID_ID
            + " INTEGER IDENTITY" + ", " + Resource.PROPERTY_ID_STYLENAME
            + " VARCHAR(20) NOT NULL" + ", " + Resource.PROPERTY_ID_NAME
            + " VARCHAR(30) NOT NULL" + ", " + Resource.PROPERTY_ID_DESCRIPTION
            + " VARCHAR(100)" + ", " + Resource.PROPERTY_ID_LOCATIONX
            + " DOUBLE" + ", " + Resource.PROPERTY_ID_LOCATIONY + " DOUBLE"
            + ", " + Resource.PROPERTY_ID_CATEGORY + " VARCHAR(30)" + ", "
            + Resource.PROPERTY_ID_DELETED + " BOOLEAN DEFAULT false NOT NULL"
            + ", UNIQUE(" + Resource.PROPERTY_ID_NAME + "))";
    private static final String CREATE_TABLE_RESERVATION = "CREATE TABLE "
            + Reservation.TABLE + " (" + " " + Reservation.PROPERTY_ID_ID
            + " INTEGER IDENTITY" + ", " + Reservation.PROPERTY_ID_RESOURCE_ID
            + " INTEGER" + ", " + Reservation.PROPERTY_ID_RESERVED_FROM
            + " TIMESTAMP NOT NULL" + ", "
            + Reservation.PROPERTY_ID_RESERVED_TO + " TIMESTAMP NOT NULL"
            + ", " + Reservation.PROPERTY_ID_DESCRIPTION + " VARCHAR(100)"
            + ", " + Reservation.PROPERTY_ID_RESERVED_BY + " VARCHAR(100)"
            + ", FOREIGN KEY (" + Reservation.PROPERTY_ID_RESOURCE_ID
            + ") REFERENCES " + Resource.TABLE + "(" + Resource.PROPERTY_ID_ID
            + "))";

    private static Connection connection = null;

    /**
     * Create database.
     */
    public SampleDB() {
        // connect to SQL database
        connect();

    }

    private synchronized void dropTables() {
        try {
            update("DROP TABLE " + Reservation.TABLE);
        } catch (final SQLException IGNORED) {
            // IGNORED, assuming it was not there
        }
        try {
            update("DROP TABLE " + Resource.TABLE);
        } catch (final SQLException IGNORED) {
            // IGNORED, assuming it was not there
        }
    }

    /**
     * Connect to SQL database. In this sample we use HSQLDB and an toolkit
     * named database in implicitly created into system memory.
     * 
     */
    private synchronized void connect() {
        if (connection == null) {
            try {
                Class.forName("org.hsqldb.jdbcDriver").newInstance();
                connection = DriverManager.getConnection(DB_URL);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }

            dropTables();
            // initialize SQL database
            createTables();

            // test by executing sample JDBC query
            testDatabase();

            generateResources();
            generateReservations();

        }
    }

    /**
     * use for SQL commands CREATE, DROP, INSERT and UPDATE
     * 
     * @param expression
     * @throws SQLException
     */
    private synchronized void update(String expression) throws SQLException {
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
    private synchronized void createTables() {
        try {
            String stmt = null;
            stmt = CREATE_TABLE_RESOURCE;
            update(stmt);
        } catch (final SQLException e) {
            if (e.toString().indexOf("Table already exists") == -1) {
                throw new RuntimeException(e);
            }
        }
        try {
            String stmt = null;
            stmt = CREATE_TABLE_RESERVATION;
            update(stmt);
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
    private synchronized String testDatabase() {
        String result = null;
        try {
            final Statement stmt = connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            final ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM "
                    + Resource.TABLE);
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

    public Container getCategories() {
        // TODO where deleted=?
        final String q = "SELECT DISTINCT(" + Resource.PROPERTY_ID_CATEGORY
                + ") FROM " + Resource.TABLE + " ORDER BY "
                + Resource.PROPERTY_ID_CATEGORY;
        try {
            return new QueryContainer(q, connection,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public Container getResources(String category) {
        // TODO where deleted=?
        String q = "SELECT * FROM " + Resource.TABLE;
        if (category != null) {
            q += " WHERE " + Resource.PROPERTY_ID_CATEGORY + "='" + category
                    + "'"; // FIXME ->
            // PreparedStatement!
        }

        try {
            return new QueryContainer(q, connection,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public Container getReservations(List resources) {
        // TODO where reserved_by=?
        // TODO where from=?
        // TODO where to=?
        // TODO where deleted=?
        String q = "SELECT * FROM " + Reservation.TABLE + "," + Resource.TABLE;
        q += " WHERE " + Reservation.PROPERTY_ID_RESOURCE_ID + "="
                + Resource.PROPERTY_ID_ID;
        if (resources != null && resources.size() > 0) {
            final StringBuffer s = new StringBuffer();
            for (final Iterator it = resources.iterator(); it.hasNext();) {
                if (s.length() > 0) {
                    s.append(",");
                }
                s.append(((Item) it.next())
                        .getItemProperty(Resource.PROPERTY_ID_ID));
            }
            q += " HAVING " + Reservation.PROPERTY_ID_RESOURCE_ID + " IN (" + s
                    + ")";
        }
        q += " ORDER BY " + Reservation.PROPERTY_ID_RESERVED_FROM;
        try {
            final QueryContainer qc = new QueryContainer(q, connection,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            if (qc.size() < 1) {
                return null;
            } else {
                return qc;
            }
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void addReservation(Item resource, String reservedBy,
            Date reservedFrom, Date reservedTo, String description)
            throws ResourceNotAvailableException {
        if (reservedFrom.after(reservedTo)) {
            final Date tmp = reservedTo;
            reservedTo = reservedFrom;
            reservedFrom = tmp;
        }
        final int resourceId = ((Integer) resource.getItemProperty(
                Resource.PROPERTY_ID_ID).getValue()).intValue();
        final String q = "INSERT INTO " + Reservation.TABLE + " ("
                + Reservation.PROPERTY_ID_RESOURCE_ID + ","
                + Reservation.PROPERTY_ID_RESERVED_BY + ","
                + Reservation.PROPERTY_ID_RESERVED_FROM + ","
                + Reservation.PROPERTY_ID_RESERVED_TO + ","
                + Reservation.PROPERTY_ID_DESCRIPTION + ")"
                + "VALUES (?,?,?,?,?)";
        synchronized (DB_URL) {
            if (!isAvailableResource(resourceId, reservedFrom, reservedTo)) {
                throw new ResourceNotAvailableException(
                        "The resource is not available at that time.");
            }
            try {
                PreparedStatement p;
                p = connection.prepareStatement(q);
                p.setInt(1, resourceId);
                p.setString(2, reservedBy);
                p.setTimestamp(3,
                        new java.sql.Timestamp(reservedFrom.getTime()));
                p.setTimestamp(4, new java.sql.Timestamp(reservedTo.getTime()));
                p.setString(5, description);
                p.execute();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public boolean isAvailableResource(int resourceId, Date reservedFrom,
            Date reservedTo) {
        // TODO where deleted=?
        if (reservedFrom.after(reservedTo)) {
            final Date tmp = reservedTo;
            reservedTo = reservedFrom;
            reservedFrom = tmp;
        }
        final String checkQ = "SELECT count(*) FROM " + Reservation.TABLE
                + " WHERE " + Reservation.PROPERTY_ID_RESOURCE_ID + "=? AND (("
                + Reservation.PROPERTY_ID_RESERVED_FROM + ">=? AND "
                + Reservation.PROPERTY_ID_RESERVED_FROM + "<?) OR ("
                + Reservation.PROPERTY_ID_RESERVED_TO + ">? AND "
                + Reservation.PROPERTY_ID_RESERVED_TO + "<=?) OR ("
                + Reservation.PROPERTY_ID_RESERVED_FROM + "<=? AND "
                + Reservation.PROPERTY_ID_RESERVED_TO + ">=?)" + ")";
        try {
            final PreparedStatement p = connection.prepareStatement(checkQ);
            p.setInt(1, resourceId);
            p.setTimestamp(2, new java.sql.Timestamp(reservedFrom.getTime()));
            p.setTimestamp(3, new java.sql.Timestamp(reservedTo.getTime()));
            p.setTimestamp(4, new java.sql.Timestamp(reservedFrom.getTime()));
            p.setTimestamp(5, new java.sql.Timestamp(reservedTo.getTime()));
            p.setTimestamp(6, new java.sql.Timestamp(reservedFrom.getTime()));
            p.setTimestamp(7, new java.sql.Timestamp(reservedTo.getTime()));
            p.execute();
            final ResultSet rs = p.getResultSet();
            if (rs.next() && rs.getInt(1) > 0) {
                return false;
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    public synchronized void generateReservations() {
        final int days = 30;
        final String descriptions[] = { "Picking up guests from airport",
                "Sightseeing with the guests",
                "Moving new servers from A to B", "Shopping",
                "Customer meeting", "Guests arriving at harbour",
                "Moving furniture", "Taking guests to see town" };
        final Container cat = getCategories();
        final Collection cIds = cat.getItemIds();
        for (final Iterator it = cIds.iterator(); it.hasNext();) {
            final Object id = it.next();
            final Item ci = cat.getItem(id);
            final String c = (String) ci.getItemProperty(
                    Resource.PROPERTY_ID_CATEGORY).getValue();
            final Container resources = getResources(c);
            final Collection rIds = resources.getItemIds();
            final Calendar cal = Calendar.getInstance();
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            final int hourNow = new Date().getHours();
            // cal.add(Calendar.DAY_OF_MONTH, -days);
            for (int i = 0; i < days; i++) {
                int r = 3;
                for (final Iterator rit = rIds.iterator(); rit.hasNext()
                        && r > 0; r--) {
                    final Object rid = rit.next();
                    final Item resource = resources.getItem(rid);
                    final int s = hourNow - 6
                            + (int) Math.round(Math.random() * 6.0);
                    final int e = s + 1 + (int) Math.round(Math.random() * 4.0);
                    final Date start = new Date(cal.getTimeInMillis());
                    start.setHours(s);
                    final Date end = new Date(cal.getTimeInMillis());
                    end.setHours(e);
                    try {
                        addReservation(resource, "Demo User", start, end,
                                descriptions[(int) Math.floor(Math.random()
                                        * descriptions.length)]);
                    } catch (ResourceNotAvailableException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
                cal.add(Calendar.DATE, 1);
            }
        }

    }

    public synchronized void generateResources() {

        final Object[][] resources = {

                { "01", "Portable Video projector 1", "Small,XGA resolution",
                        "Turku", new Double(60.510857), new Double(22.275424) },
                { "02", "Auditorio", "", "Turku", new Double(60.452171),
                        new Double(22.2995) },
                { "03", "Meeting room 1", "Projector, WiFi", "Turku",
                        new Double(60.4507), new Double(22.295551) },
                { "04", "Meeting room 2", "WiFi, Blackboard", "Turku",
                        new Double(60.434722), new Double(22.224398) },
                { "05", "AV Room", "Cabrio. Keys from infodesk.", "Turku",
                        new Double(60.508970), new Double(22.264790) },
                { "06", "Video projector",
                        "Rather heavy, good luminance, WXGA", "Turku",
                        new Double(60.434722), new Double(22.224398) },

        };

        final String q = "INSERT INTO " + Resource.TABLE + "("
                + Resource.PROPERTY_ID_STYLENAME + ","
                + Resource.PROPERTY_ID_NAME + ","
                + Resource.PROPERTY_ID_DESCRIPTION + ","
                + Resource.PROPERTY_ID_CATEGORY + ","
                + Resource.PROPERTY_ID_LOCATIONX + ","
                + Resource.PROPERTY_ID_LOCATIONY + ")"
                + " VALUES (?,?,?,?,?,?)";
        try {
            final PreparedStatement stmt = connection.prepareStatement(q);
            for (int i = 0; i < resources.length; i++) {
                int j = 0;
                stmt.setString(j + 1, (String) resources[i][j++]);
                stmt.setString(j + 1, (String) resources[i][j++]);
                stmt.setString(j + 1, (String) resources[i][j++]);
                stmt.setString(j + 1, (String) resources[i][j++]);
                stmt.setDouble(j + 1, ((Double) resources[i][j++])
                        .doubleValue());
                stmt.setDouble(j + 1, ((Double) resources[i][j++])
                        .doubleValue());
                stmt.execute();
            }
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int addResource(String name, String desc) {
        final String q = "INSERT INTO " + Resource.TABLE + " ("
                + Resource.PROPERTY_ID_STYLENAME + ","
                + Resource.PROPERTY_ID_NAME + ","
                + Resource.PROPERTY_ID_DESCRIPTION + ","
                + Resource.PROPERTY_ID_CATEGORY + ","
                + Resource.PROPERTY_ID_LOCATIONX + ","
                + Resource.PROPERTY_ID_LOCATIONY + ")"
                + " VALUES (?,?,?,?,?,?)";
        synchronized (DB_URL) {
            try {
                PreparedStatement p = connection.prepareStatement(q);
                p.setString(1, "");
                p.setString(2, name);
                p.setString(3, desc);
                p.setString(4, "default");
                p.setDouble(5, 0);
                p.setDouble(6, 0);
                p.execute();

                Statement createStatement = connection.createStatement();
                ResultSet executeQuery = createStatement
                        .executeQuery("CALL IDENTITY()");
                executeQuery.next();
                return executeQuery.getInt(1);

            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return -1;
    }

    public void updateResource(Item editedItem, String name, String desc) {
        final String q = "UPDATE " + Resource.TABLE + " SET "
                + Resource.PROPERTY_ID_NAME + " = ? ,"
                + Resource.PROPERTY_ID_DESCRIPTION + " = ? " + "WHERE "
                + Resource.PROPERTY_ID_ID + " = ?";
        synchronized (DB_URL) {
            try {
                PreparedStatement p = connection.prepareStatement(q);
                p.setString(1, name);
                p.setString(2, desc);
                p.setInt(3, ((Integer) editedItem.getItemProperty(
                        Resource.PROPERTY_ID_ID).getValue()).intValue());
                p.execute();

            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }
}

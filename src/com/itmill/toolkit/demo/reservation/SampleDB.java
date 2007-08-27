package com.itmill.toolkit.demo.reservation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import com.itmill.toolkit.data.Container;
import com.itmill.toolkit.data.util.QueryContainer;

public class SampleDB {
    public class User {
	public static final String TABLE = "user";
	public static final String PROPERTY_ID_ID = "ID";
	public static final String PROPERTY_ID_FULLNAME = "FULLNAME";
	public static final String PROPERTY_ID_EMAIL = "EMAIL";
	public static final String PROPERTY_ID_PASSWORD = "PASSWORD";
	public static final String PROPERTY_ID_DELETED = "DELETED";
    }

    public class Resource {
	public static final String TABLE = "resource";
	public static final String PROPERTY_ID_ID = "ID";
	public static final String PROPERTY_ID_NAME = "NAME";
	public static final String PROPERTY_ID_DESCRIPTION = "DESCRIPTION";
	public static final String PROPERTY_ID_LOCATIONX = "LOCATION_X";
	public static final String PROPERTY_ID_LOCATIONY = "LOCATION_Y";
	public static final String PROPERTY_ID_CATEGORY = "CATEGORY";
	public static final String PROPERTY_ID_DELETED = "DELETED";
    }

    public class Reservation {
	public static final String TABLE = "reservation";
	public static final String PROPERTY_ID_ID = "ID";
	public static final String PROPERTY_ID_DESCRIPTION = "DESCRIPTION";
	public static final String PROPERTY_ID_RESOURCE_ID = "RESOURCE_ID";
	public static final String PROPERTY_ID_RESERVED_BY_ID = "RESERVED_BY_USER_ID";
	public static final String PROPERTY_ID_RESERVED_FROM = "RESERVED_FROM";
	public static final String PROPERTY_ID_RESERVED_TO = "RESERVED_TO";
    }

    // TODO -> param
    private static final String DB_URL = "jdbc:hsqldb:file:reservation.db";

    private static final String CREATE_TABLE_USER = "CREATE TABLE "
	    + User.TABLE + " (" + " " + User.PROPERTY_ID_ID
	    + " INTEGER IDENTITY" + ", " + User.PROPERTY_ID_FULLNAME
	    + " VARCHAR(100) NOT NULL" + ", " + User.PROPERTY_ID_EMAIL
	    + " VARCHAR(50) NOT NULL" + ", " + User.PROPERTY_ID_PASSWORD
	    + " VARCHAR(20) NOT NULL" + ", " + User.PROPERTY_ID_DELETED
	    + " BOOLEAN DEFAULT false NOT NULL" + ", UNIQUE("
	    + User.PROPERTY_ID_FULLNAME + "), UNIQUE(" + User.PROPERTY_ID_EMAIL
	    + ") )";
    private static final String CREATE_TABLE_RESOURCE = "CREATE TABLE "
	    + Resource.TABLE + " (" + " " + Resource.PROPERTY_ID_ID
	    + " INTEGER IDENTITY" + ", " + Resource.PROPERTY_ID_NAME
	    + " VARCHAR(30) NOT NULL" + ", " + Resource.PROPERTY_ID_DESCRIPTION
	    + " VARCHAR(100)" + ", " + Resource.PROPERTY_ID_LOCATIONX
	    + " DOUBLE" + ", " + Resource.PROPERTY_ID_LOCATIONY + " DOUBLE"
	    + ", " + Resource.PROPERTY_ID_CATEGORY + " VARCHAR(30)" + ", "
	    + Resource.PROPERTY_ID_DELETED + " BOOLEAN DEFAULT false NOT NULL"
	    + ", UNIQUE(" + Resource.PROPERTY_ID_NAME + "))";
    private static final String CREATE_TABLE_RESERVATION = "CREATE TABLE "
	    + Reservation.TABLE + " (" + " " + Reservation.PROPERTY_ID_ID
	    + " INTEGER IDENTITY" + ", " + Reservation.PROPERTY_ID_RESOURCE_ID
	    + " INTEGER" + ", " + Reservation.PROPERTY_ID_RESERVED_BY_ID
	    + " INTEGER" + ", " + Reservation.PROPERTY_ID_RESERVED_FROM
	    + " TIMESTAMP NOT NULL" + ", "
	    + Reservation.PROPERTY_ID_RESERVED_TO + " TIMESTAMP NOT NULL"
	    + ", " + Reservation.PROPERTY_ID_DESCRIPTION + " VARCHAR(100)"
	    + ", FOREIGN KEY (" + Reservation.PROPERTY_ID_RESOURCE_ID
	    + ") REFERENCES " + Resource.TABLE + "(" + Resource.PROPERTY_ID_ID
	    + "), FOREIGN KEY (" + Reservation.PROPERTY_ID_RESERVED_BY_ID
	    + ") REFERENCES " + User.TABLE + "(" + User.PROPERTY_ID_ID + "))";

    private Connection connection = null;

    /**
     * Create database.
     */
    public SampleDB() {
	this(false);
    }

    public SampleDB(boolean recreate) {
	// connect to SQL database
	connect();

	if (recreate) {
	    dropTables();
	}

	// initialize SQL database
	createTables();

	// test by executing sample JDBC query
	testDatabase();
    }

    private void dropTables() {
	try {
	    update("DROP TABLE " + Reservation.TABLE);
	} catch (SQLException IGNORED) {
	    // IGNORED, assuming it was not there
	}
	try {
	    update("DROP TABLE " + Resource.TABLE);
	} catch (SQLException IGNORED) {
	    // IGNORED, assuming it was not there
	}
	try {
	    update("DROP TABLE " + User.TABLE);
	} catch (SQLException IGNORED) {
	    // IGNORED, assuming it was not there
	}
    }

    /**
     * Connect to SQL database. In this sample we use HSQLDB and an toolkit
     * named database in implicitly created into system memory.
     * 
     */
    private void connect() {
	try {
	    Class.forName("org.hsqldb.jdbcDriver").newInstance();
	    connection = DriverManager.getConnection(DB_URL);
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * use for SQL commands CREATE, DROP, INSERT and UPDATE
     * 
     * @param expression
     * @throws SQLException
     */
    private void update(String expression) throws SQLException {
	Statement st = null;
	st = connection.createStatement();
	int i = st.executeUpdate(expression);
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
    private void createTables() {
	try {
	    String stmt = null;
	    stmt = CREATE_TABLE_RESOURCE;
	    update(stmt);
	} catch (SQLException e) {
	    if (e.toString().indexOf("Table already exists") == -1)
		throw new RuntimeException(e);
	}
	try {
	    String stmt = null;
	    stmt = CREATE_TABLE_USER;
	    update(stmt);
	} catch (SQLException e) {
	    if (e.toString().indexOf("Table already exists") == -1)
		throw new RuntimeException(e);
	}
	try {
	    String stmt = null;
	    stmt = CREATE_TABLE_RESERVATION;
	    update(stmt);
	} catch (SQLException e) {
	    if (e.toString().indexOf("Table already exists") == -1)
		throw new RuntimeException(e);
	}
    }

    /**
     * Test database connection with simple SELECT command.
     * 
     */
    private String testDatabase() {
	String result = null;
	try {
	    Statement stmt = connection.createStatement(
		    ResultSet.TYPE_SCROLL_INSENSITIVE,
		    ResultSet.CONCUR_UPDATABLE);
	    ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM "
		    + Resource.TABLE);
	    rs.next();
	    result = "rowcount for table test is " + rs.getObject(1).toString();
	    stmt.close();
	} catch (SQLException e) {
	    throw new RuntimeException(e);
	}
	return result;
    }

    public Connection getConnection() {
	return connection;
    }

    public Container getCategories() {
	String q = "SELECT DISTINCT(" + Resource.PROPERTY_ID_CATEGORY
		+ ") FROM " + Resource.TABLE + " ORDER BY "
		+ Resource.PROPERTY_ID_CATEGORY;
	try {
	    return new QueryContainer(q, connection,
		    ResultSet.TYPE_SCROLL_INSENSITIVE,
		    ResultSet.CONCUR_READ_ONLY);
	} catch (SQLException e) {
	    throw new RuntimeException(e);
	}

    }

    public Container getResources(String category) {
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
	} catch (SQLException e) {
	    throw new RuntimeException(e);
	}

    }

    public Container getReservations(int[] resourceIds) {
	String q = "SELECT * FROM " + Reservation.TABLE ;
	if (resourceIds != null && resourceIds.length > 0) {
	    StringBuilder s = new StringBuilder();
	    for (int i = 0; i < resourceIds.length; i++) {
		if (i > 0) {
		    s.append(",");
		}
		s.append(resourceIds[i]);
	    }
	    q += " HAVING " + Reservation.PROPERTY_ID_RESOURCE_ID + " IN (" + s
		    + ")";
	}
	q += " ORDER BY " + Reservation.PROPERTY_ID_RESERVED_FROM;
	try {
	    QueryContainer qc = new QueryContainer(q, connection,
		    ResultSet.TYPE_SCROLL_INSENSITIVE,
		    ResultSet.CONCUR_READ_ONLY);
	    if (qc.size() < 1) {
		return null;
	    } else {
		return qc;
	    }
	} catch (SQLException e) {
	    throw new RuntimeException(e);
	}
    }


    public void addReservation(int resourceId, int reservedById,
	    Date reservedFrom, Date reservedTo, String description) {
	// TODO swap dates if from>to
	String checkQ = "SELECT count(*) FROM " + Reservation.TABLE + " WHERE "
		+ Reservation.PROPERTY_ID_RESOURCE_ID + "=? AND (("
		+ Reservation.PROPERTY_ID_RESERVED_FROM + ">=? AND "
		+ Reservation.PROPERTY_ID_RESERVED_FROM + "<=?) OR ("
		+ Reservation.PROPERTY_ID_RESERVED_TO + ">=? AND "
		+ Reservation.PROPERTY_ID_RESERVED_TO + "<=?) OR ("
		+ Reservation.PROPERTY_ID_RESERVED_FROM + "<=? AND "
		+ Reservation.PROPERTY_ID_RESERVED_TO + ">=?)"
		+")";
	System.err.println(checkQ);
	String q = "INSERT INTO " + Reservation.TABLE + " ("
		+ Reservation.PROPERTY_ID_RESOURCE_ID + ","
		+ Reservation.PROPERTY_ID_RESERVED_BY_ID + ","
		+ Reservation.PROPERTY_ID_RESERVED_FROM + ","
		+ Reservation.PROPERTY_ID_RESERVED_TO + ","
		+ Reservation.PROPERTY_ID_DESCRIPTION + ")"
		+ "VALUES (?,?,?,?,?)";
	synchronized (DB_URL) {
	    try {
		PreparedStatement p = connection.prepareStatement(checkQ);
		p.setInt(1, resourceId);
		p.setTimestamp(2, new java.sql.Timestamp(reservedFrom.getTime()));
		p.setTimestamp(3, new java.sql.Timestamp(reservedTo.getTime()));
		p.setTimestamp(4, new java.sql.Timestamp(reservedFrom.getTime()));
		p.setTimestamp(5, new java.sql.Timestamp(reservedTo.getTime()));
		p.setTimestamp(6, new java.sql.Timestamp(reservedFrom.getTime()));
		p.setTimestamp(7, new java.sql.Timestamp(reservedTo.getTime()));
		p.execute();
		ResultSet rs = p.getResultSet();
		if (rs.next() && rs.getInt(1) > 0) {
		    // TODO custom exception
		    throw new RuntimeException("Not free!");
		}
		p = connection.prepareStatement(q);
		p.setInt(1, resourceId);
		p.setInt(2, reservedById);
		p.setTimestamp(3,
			new java.sql.Timestamp(reservedFrom.getTime()));
		p.setTimestamp(4, new java.sql.Timestamp(reservedTo.getTime()));
		p.setString(5, description);
		p.execute();
	    } catch (Exception e) {
		// TODO
		System.err.println(e);
		e.printStackTrace(System.err);
	    }
	}
    }

    public Container getUsers() {
	String q = "SELECT * FROM " + User.TABLE + " ORDER BY "
		+ User.PROPERTY_ID_FULLNAME;
	try {
	    QueryContainer qc = new QueryContainer(q, connection,
		    ResultSet.TYPE_SCROLL_INSENSITIVE,
		    ResultSet.CONCUR_READ_ONLY);
	    return qc;
	} catch (SQLException e) {
	    throw new RuntimeException(e);
	}
    }
    
    public void generateResources() {
	String[][] resources = {
		{ "IT Mill Toolkit Manual", "the manual", "Books" },
		{ "IT Mill Toolkit for Dummies", "the hardcover version", "Books" },
		{ "Sony", "Old Sony video projector", "AV equipment" },
		{ "Sanyo", "Brand new hd-ready video projector", "AV equipment" },
		{ "Room 7", "Converence room in the lobby", "Conference rooms" },
		{ "Luokkahuone", "Classroom right next to IT Mill", "Conference rooms" },
		{ "Nintendo Wii", "Teh uber fun", "Entertainment" }, 
		{ "Playstation", "We don't actually have one", "Entertainment" } 
		};

	String q = "INSERT INTO " + Resource.TABLE + "("
		+ Resource.PROPERTY_ID_NAME + ","
		+ Resource.PROPERTY_ID_DESCRIPTION + ","
		+ Resource.PROPERTY_ID_CATEGORY + ")" + " VALUES (?,?,?)";
	try {
	    PreparedStatement stmt = connection.prepareStatement(q);
	    for (int i = 0; i < resources.length; i++) {
		stmt.setString(1, resources[i][0]);
		stmt.setString(2, resources[i][1]);
		stmt.setString(3, resources[i][2]);
		stmt.execute();
	    }
	} catch (SQLException e) {
	    throw new RuntimeException(e);
	}
    }

    public void generateDemoUser() {
	String q = "INSERT INTO USER (" + User.PROPERTY_ID_FULLNAME + ","
		+ User.PROPERTY_ID_EMAIL + "," + User.PROPERTY_ID_PASSWORD
		+ ") VALUES (?,?,?)";
	try {
	    PreparedStatement stmt = connection.prepareStatement(q);
	    stmt.setString(1, "Demo User");
	    stmt.setString(2, "demo.user@itmill.com");
	    stmt.setString(3, "demo");
	    stmt.execute();
	} catch (SQLException e) {
	    throw new RuntimeException(e);
	}

    }

}

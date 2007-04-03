package com.itmill.toolkit.demo.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Creates temporary database named toolkit with sample table named employee and
 * populates it with data. By default we use HSQLDB. Ensure that you have
 * hsqldb.jar under WEB-INF/lib directory. Database is will be created into
 * memory.
 * 
 * @author IT Mill Ltd.
 * 
 */
public class SampleDatabase {

	public static final int ROWCOUNT = 1000;

	private Connection connection = null;

	private static final String[] firstnames = new String[] { "Amanda",
			"Andrew", "Bill", "Frank", "Matt", "Xavier", "John", "Mary", "Joe",
			"Gloria", "Marcus", "Belinda", "David", "Anthony", "Julian",
			"Paul", "Carrie", "Susan", "Gregg", "Michael", "William", "Ethan",
			"Thomas", "Oscar", "Norman", "Roy", "Sarah", "Jeff", "Jane",
			"Peter", "Marc", "Josie", "Linus" };

	private static final String[] lastnames = new String[] { "Torvalds",
			"Smith", "Jones", "Beck", "Burton", "Bell", "Davis", "Burke",
			"Bernard", "Hood", "Scott", "Smith", "Carter", "Roller", "Conrad",
			"Martin", "Fisher", "Martell", "Freeman", "Hackman", "Jones",
			"Harper", "Russek", "Johnson", "Sheridan", "Hill", "Parker",
			"Foster", "Moss", "Fielding" };

	private static final String[] titles = new String[] { "Project Manager",
			"Marketing Manager", "Sales Manager", "Sales", "Trainer",
			"Technical Support", "Account Manager", "Customer Support",
			"Testing Engineer", "Software Designer", "Programmer", "Consultant" };

	private static final String[] units = new String[] { "Tokyo",
			"Mexico City", "Seoul", "New York", "Sao Paulo", "Bombay", "Delhi",
			"Shanghai", "Los Angeles", "London", "Shanghai", "Sydney",
			"Bangalore", "Hong Kong", "Madrid", "Milano", "Beijing", "Paris",
			"Moscow", "Berlin", "Helsinki" };

	/**
	 * Create temporary database.
	 * 
	 */
	public SampleDatabase() {
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
	public SampleDatabase(Connection connection) {
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
		String url = "jdbc:hsqldb:mem:toolkit";
		try {
			Class.forName("org.hsqldb.jdbcDriver").newInstance();
			connection = DriverManager.getConnection(url, "sa", "");
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
	public void update(String expression) throws SQLException {
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
			stmt = "CREATE TABLE employee ( ID INTEGER IDENTITY, FIRSTNAME VARCHAR(100), "
					+ "LASTNAME VARCHAR(100), TITLE VARCHAR(100), UNIT VARCHAR(100) )";
			update(stmt);
			for (int j = 0; j < ROWCOUNT; j++) {
				stmt = "INSERT INTO employee(FIRSTNAME, LASTNAME, TITLE, UNIT) VALUES ("
						+ "'"
						+ firstnames[(int) (Math.random() * (firstnames.length - 1))]
						+ "',"
						+ "'"
						+ lastnames[(int) (Math.random() * (lastnames.length - 1))]
						+ "',"
						+ "'"
						+ titles[(int) (Math.random() * (titles.length - 1))]
						+ "',"
						+ "'"
						+ units[(int) (Math.random() * (units.length - 1))]
						+ "'" + ")";
				update(stmt);
			}
		} catch (SQLException e) {
			if (!e.toString().contains("Table already exists"))
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
			ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM employee");
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

}

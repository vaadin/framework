package com.vaadin.tests.components.table;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.QueryDelegate;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.data.util.sqlcontainer.query.generator.DefaultSQLGenerator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class TableScrollingWithSQLContainer extends UI {

    /** Table should never end up calling indexOfId in this case */
    private class LimitedSQLContainer extends SQLContainer {

        public LimitedSQLContainer(QueryDelegate delegate) throws SQLException {
            super(delegate);
        }

        @Override
        public int indexOfId(Object itemId) {
            throw new RuntimeException("This function should not be called");
        }
    }

    private void generateTestData(JDBCConnectionPool connectionPool)
            throws SQLException {
        Connection conn = connectionPool.reserveConnection();
        Statement statement = conn.createStatement();
        try {
            statement.execute("drop table PEOPLE");
        } catch (SQLException e) {
            // Will fail if table doesn't exist, which is OK.
            conn.rollback();
        }
        statement
                .execute("create table people (id integer generated always as identity,"
                        + " name varchar(32), AGE INTEGER)");
        statement.execute("alter table people add primary key (id)");
        for (int i = 0; i < 5000; i++) {
            statement
                    .executeUpdate("insert into people values(default, 'Person "
                            + i + "', '" + i % 99 + "')");
        }
        statement.close();
        conn.commit();
        connectionPool.releaseConnection(conn);
    }

    static final String TABLE = "table";

    @Override
    public void init(VaadinRequest request) {
        try {
            SimpleJDBCConnectionPool connectionPool = new SimpleJDBCConnectionPool(
                    "org.hsqldb.jdbc.JDBCDriver",
                    "jdbc:hsqldb:mem:sqlcontainer", "SA", "", 2, 20);
            generateTestData(connectionPool);

            TableQuery query = new TableQuery("people", connectionPool,
                    new DefaultSQLGenerator());

            SQLContainer container = new LimitedSQLContainer(query);

            final VerticalLayout rootLayout = new VerticalLayout();

            final Table table = new Table();
            table.setContainerDataSource(container);
            table.setCurrentPageFirstItemIndex(300);
            rootLayout.addComponent(table);

            table.setImmediate(true);

            rootLayout.addComponent(new Button("GOTO 200", new ClickListener() {

                @Override
                public void buttonClick(ClickEvent event) {
                    table.setCurrentPageFirstItemIndex(200);
                }
            }));

            setContent(rootLayout);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

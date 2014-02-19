package com.vaadin.tests.components.table;

import java.sql.SQLException;

import com.vaadin.data.util.sqlcontainer.DataGenerator;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.SQLTestsConstants;
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.QueryDelegate;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
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

    static final String TABLE = "table";

    @Override
    public void init(VaadinRequest request) {
        try {
            SimpleJDBCConnectionPool connectionPool = new SimpleJDBCConnectionPool(
                    SQLTestsConstants.dbDriver, SQLTestsConstants.dbURL,
                    SQLTestsConstants.dbUser, SQLTestsConstants.dbPwd, 2, 2);
            DataGenerator.addPeopleToDatabase(connectionPool);
            DataGenerator.addFiveThousandPeople(connectionPool);

            TableQuery query = new TableQuery("people", connectionPool,
                    SQLTestsConstants.sqlGen);

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
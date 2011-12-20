package com.vaadin.tests.containers.sqlcontainer;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.vaadin.Application;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.ItemSetChangeListener;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.sqlcontainer.AllTests;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Root.LegacyWindow;
import com.vaadin.ui.Table;

public class CheckboxUpdateProblem extends Application.LegacyApplication
        implements Property.ValueChangeListener {
    private final DatabaseHelper databaseHelper = new DatabaseHelper();
    private Table testList;
    private final HorizontalSplitPanel horizontalSplit = new HorizontalSplitPanel();

    private TestForm testForm = new TestForm();

    @Override
    public void init() {
        setMainWindow(new LegacyWindow("Test window"));
        horizontalSplit.setSizeFull();
        testList = new Table();

        horizontalSplit.setFirstComponent(testList);
        testList.setSizeFull();
        testList.setContainerDataSource(databaseHelper.getTestContainer());
        testList.setSelectable(true);
        testList.setImmediate(true);
        testList.addListener(this);

        databaseHelper.getTestContainer().addListener(
                new ItemSetChangeListener() {
                    public void containerItemSetChange(ItemSetChangeEvent event) {
                        Object selected = testList.getValue();
                        if (selected != null) {
                            testForm.setItemDataSource(testList
                                    .getItem(selected));
                        }
                    }
                });

        testForm = new TestForm();
        testForm.setItemDataSource(null);

        horizontalSplit.setSecondComponent(testForm);

        getMainWindow().setContent(horizontalSplit);
    }

    public void valueChange(ValueChangeEvent event) {

        Property<?> property = event.getProperty();
        if (property == testList) {
            Item item = testList.getItem(testList.getValue());

            if (item != testForm.getItemDataSource()) {
                testForm.setItemDataSource(item);
            }
        }

    }

    private class TestForm extends Form implements Button.ClickListener {

        private final Button save;

        private TestForm() {
            setSizeFull();
            setWriteThrough(false);
            setInvalidCommitted(false);

            save = new Button("Save", this);
            getFooter().addComponent(save);
            getFooter().setVisible(false);
        }

        public void buttonClick(ClickEvent event) {
            if (event.getSource() == save) {
                super.commit();

                try {
                    databaseHelper.getTestContainer().commit();
                    getMainWindow().showNotification("Saved");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void setItemDataSource(Item newDataSource) {
            super.setItemDataSource(newDataSource);

            if (newDataSource != null) {
                getFooter().setVisible(true);
            } else {
                getFooter().setVisible(false);
            }
        }

    }

    private class DatabaseHelper {

        private JDBCConnectionPool connectionPool = null;
        private SQLContainer testContainer = null;
        private static final String TABLENAME = "testtable";

        public DatabaseHelper() {
            initConnectionPool();
            initDatabase();
            initContainers();
        }

        private void initDatabase() {
            try {
                Connection conn = connectionPool.reserveConnection();
                Statement statement = conn.createStatement();
                try {
                    statement.execute("drop table " + TABLENAME);
                } catch (SQLException e) {
                    // Will fail if table doesn't exist, which is OK.
                    conn.rollback();
                }
                switch (AllTests.db) {
                case MYSQL:
                    statement
                            .execute("create table "
                                    + TABLENAME
                                    + " (id integer auto_increment not null, field1 varchar(100), field2 boolean, primary key(id))");
                    break;
                case POSTGRESQL:
                    statement
                            .execute("create table "
                                    + TABLENAME
                                    + " (\"id\" serial primary key, \"field1\" varchar(100), \"field2\" boolean)");
                    break;
                }
                statement.executeUpdate("insert into " + TABLENAME
                        + " values(default, 'Kalle', 'true')");
                statement.close();
                conn.commit();
                connectionPool.releaseConnection(conn);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private void initContainers() {
            try {
                TableQuery q1 = new TableQuery(TABLENAME, connectionPool);
                q1.setVersionColumn("id");
                testContainer = new SQLContainer(q1);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private void initConnectionPool() {
            try {
                connectionPool = new SimpleJDBCConnectionPool(
                        AllTests.dbDriver, AllTests.dbURL, AllTests.dbUser,
                        AllTests.dbPwd, 2, 5);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public SQLContainer getTestContainer() {
            return testContainer;
        }
    }

}

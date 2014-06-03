package com.vaadin.tests.containers.sqlcontainer;

import java.sql.SQLException;

import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.VerticalLayout;

// author table in testdb (MySQL) is set out as follows
// +-------------+-------------+------+-----+---------+----------------+
// | Field       | Type        | Null | Key | Default | Extra          |
// +-------------+-------------+------+-----+---------+----------------+
// | id          | int(11)     | NO   | PRI | NULL    | auto_increment |
// | last_name   | varchar(40) | NO   |     | NULL    |                |
// | first_names | varchar(80) | NO   |     | NULL    |                |
// +-------------+-------------+------+-----+---------+----------------+

@SuppressWarnings("serial")
public class MassInsertMemoryLeakTestApp extends LegacyApplication {

    ProgressIndicator proggress = new ProgressIndicator();
    Button process = new Button("Mass insert");

    @Override
    public void init() {
        setMainWindow(new LegacyWindow("SQLContainer Test", buildLayout()));

        process.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                MassInsert mi = new MassInsert();
                mi.start();
            }
        });
    }

    private class MassInsert extends Thread {

        @Override
        public void start() {
            getContext().lock();
            try {
                proggress.setVisible(true);
                proggress.setValue(new Float(0));
                proggress.setPollingInterval(100);
                process.setEnabled(false);
                proggress.setCaption("");
                super.start();
            } finally {
                getContext().unlock();
            }
        }

        @Override
        public void run() {
            JDBCConnectionPool pool = getConnectionPool();
            if (pool != null) {
                try {
                    int cents = 100;
                    for (int cent = 0; cent < cents; cent++) {
                        TableQuery q = new TableQuery("AUTHOR", pool);
                        q.setVersionColumn("ID");
                        SQLContainer c = new SQLContainer(q);
                        for (int i = 0; i < 100; i++) {
                            Object id = c.addItem();
                            c.getContainerProperty(id, "FIRST_NAMES").setValue(
                                    getRandonName());
                            c.getContainerProperty(id, "LAST_NAME").setValue(
                                    getRandonName());
                        }
                        c.commit();
                        getContext().lock();
                        try {
                            proggress
                                    .setValue(new Float((1.0f * cent) / cents));
                            proggress.setCaption("" + 100 * cent
                                    + " rows inserted");
                        } finally {
                            getContext().unlock();
                        }
                    }
                } catch (SQLException e) {
                    getMainWindow().showNotification(
                            "SQLException while processing",
                            e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
            getContext().lock();
            try {
                proggress.setVisible(false);
                proggress.setPollingInterval(0);
                process.setEnabled(true);
            } finally {
                getContext().unlock();
            }
        }
    }

    private ComponentContainer buildLayout() {
        VerticalLayout lo = new VerticalLayout();
        lo.setSizeFull();
        lo.addComponent(proggress);
        lo.addComponent(process);
        lo.setComponentAlignment(proggress, Alignment.BOTTOM_CENTER);
        lo.setComponentAlignment(process, Alignment.TOP_CENTER);
        lo.setSpacing(true);
        proggress.setIndeterminate(false);
        proggress.setVisible(false);
        return lo;
    }

    private String getRandonName() {
        final String[] tokens = new String[] { "sa", "len", "da", "vid", "ma",
                "ry", "an", "na", "jo", "bri", "son", "mat", "e", "ric", "ge",
                "eu", "han", "har", "ri", "ja", "lo" };
        StringBuffer sb = new StringBuffer();
        int len = (int) (Math.random() * 3 + 2);
        while (len-- > 0) {
            sb.append(tokens[(int) (Math.random() * tokens.length)]);
        }
        return Character.toUpperCase(sb.charAt(0)) + sb.toString().substring(1);
    }

    private JDBCConnectionPool getConnectionPool() {
        SimpleJDBCConnectionPool pool = null;
        try {
            pool = new SimpleJDBCConnectionPool("com.mysql.jdbc.Driver",
                    "jdbc:mysql://localhost:3306/sqlcontainer", "sqlcontainer",
                    "sqlcontainer");
        } catch (SQLException e) {
            getMainWindow().showNotification("Error connecting to database");
        }
        return pool;
    }

}

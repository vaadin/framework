package com.vaadin.tests.components.table;

import com.vaadin.Application;
import com.vaadin.data.Container;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class TableFirstRowFlicker extends Application {

    Table t;

    @Override
    public void init() {
        Window mainWindow = new Window("Table Row Flicker");
        mainWindow.getContent().setSizeFull();
        setMainWindow(mainWindow);

        t = new Table();
        t.setSizeFull();
        t.setSelectable(true);
        t.setContainerDataSource(buildContainer());
        mainWindow.addComponent(t);
        ((VerticalLayout) mainWindow.getContent()).setExpandRatio(t, 1);

        // Button button = new Button("Refresh");
        // button.addListener(new Button.ClickListener() {
        // public void buttonClick(ClickEvent event) {
        // t.refreshRowCache();
        // }
        // });
        // mainWindow.addComponent(button);

        ProgressIndicator pi = new ProgressIndicator();
        pi.setPollingInterval(1000);
        pi.setIndeterminate(true);
        mainWindow.addComponent(pi);

        Thread r = new Thread() {
            @Override
            public void run() {
                while (t != null) {
                    synchronized (t.getApplication()) {
                        int firstId = t.getCurrentPageFirstItemIndex();
                        Object selected = t.getValue();
                        t.setContainerDataSource(buildContainer());
                        t.setValue(selected);
                        t.setCurrentPageFirstItemIndex(firstId);
                        // lighter alternative for all of above
                        // t.refreshRowCache();
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("Table update thread stopped");
            }
        };
        r.start();
    }

    @Override
    public void close() {
        t = null;
        super.close();
    }

    private Container buildContainer() {
        IndexedContainer cont = new IndexedContainer();
        cont.addContainerProperty("name", Label.class, null);
        for (int i = 0; i < 10000; i++) {
            cont.addItem(i);
            Label l = new Label("Item " + i);
            l.setHeight("50px");
            cont.getContainerProperty(i, "name").setValue(l);
        }
        return cont;
    }

}
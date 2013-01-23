package com.vaadin.tests.components.table;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

public class TableInSubWindowMemoryLeak extends TestBase {

    @Override
    public void setup() {
        final Label label = new Label("Hello Vaadin user");
        addComponent(label);
        final Button openButton = new Button("open me");
        openButton.addListener(new ClickListener() {

            public void buttonClick(final ClickEvent event) {
                final Window window = new Window("Simple Window");
                window.setModal(true);
                window.setHeight("200px");
                window.setWidth("200px");
                final Table table = new Table();
                window.addComponent(table);
                getMainWindow().addWindow(window);
                window.addListener(new CloseListener() {
                    public void windowClose(final CloseEvent e) {
                        window.removeComponent(table);
                        getMainWindow().removeWindow(window);
                    }
                });
            }
        });
        addComponent(openButton);

        final Button openButton2 = new Button("open me without Table");
        openButton2.addListener(new ClickListener() {
            public void buttonClick(final ClickEvent event) {
                final Window window = new Window("Simple Window");
                window.setModal(true);
                window.setHeight("200px");
                window.setWidth("200px");
                getMainWindow().addWindow(window);
                window.addListener(new CloseListener() {
                    public void windowClose(final CloseEvent e) {
                        getMainWindow().removeWindow(window);
                    }
                });
            }
        });
        addComponent(openButton2);
    }

    @Override
    protected String getDescription() {
        return "IE 8 leaks memory with a subwindow containing a Table";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9197;
    }
}

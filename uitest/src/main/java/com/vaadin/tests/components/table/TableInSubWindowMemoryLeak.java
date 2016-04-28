package com.vaadin.tests.components.table;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

public class TableInSubWindowMemoryLeak extends TestBase {

    @Override
    public void setup() {
        final Label label = new Label("Hello Vaadin user");
        addComponent(label);
        final Button openButton = new Button("open me");
        openButton.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(final ClickEvent event) {
                final Window window = new Window("Simple Window");
                window.setModal(true);
                window.setHeight("200px");
                window.setWidth("200px");
                final Table table = new Table();
                window.setContent(table);
                UI.getCurrent().addWindow(window);
                window.addCloseListener(new CloseListener() {
                    @Override
                    public void windowClose(final CloseEvent e) {
                        window.setContent(new Label());
                        UI.getCurrent().removeWindow(window);
                    }
                });
            }
        });
        addComponent(openButton);

        final Button openButton2 = new Button("open me without Table");
        openButton2.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                final Window window = new Window("Simple Window");
                window.setModal(true);
                window.setHeight("200px");
                window.setWidth("200px");
                UI.getCurrent().addWindow(window);
                window.addCloseListener(new CloseListener() {
                    @Override
                    public void windowClose(final CloseEvent e) {
                        UI.getCurrent().removeWindow(window);
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

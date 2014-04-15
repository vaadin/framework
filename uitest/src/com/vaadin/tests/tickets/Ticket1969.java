package com.vaadin.tests.tickets;

import com.vaadin.server.UserError;
import com.vaadin.tests.TestForTablesInitialColumnWidthLogicRendering;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class Ticket1969 extends com.vaadin.server.LegacyApplication {

    @Override
    public void init() {
        final LegacyWindow main = new LegacyWindow(getClass().getName()
                .substring(getClass().getName().lastIndexOf(".") + 1));
        setMainWindow(main);

        main.getContent().setSizeFull();

        TabSheet ts = new TabSheet();
        ts.setSizeFull();

        final Table t = TestForTablesInitialColumnWidthLogicRendering
                .getTestTable(7, 2000);
        t.setSizeFull();
        ts.addTab(t, "Table, scrollins should not flash", null);

        final Label testContent = new Label(
                "TabSheet by default uses caption, icon, errors etc. from Components. ");

        testContent.setCaption("Introduction to test");

        ts.addTab(testContent);

        final VerticalLayout actions = new VerticalLayout();

        actions.setCaption("Test actions");

        ts.addTab(actions);

        Button b;

        b = new Button(
                "change introduction caption (should add * to tab name)",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        testContent.setCaption(testContent.getCaption() + "*");
                    }
                });
        actions.addComponent(b);

        b = new Button("change tab caption (should add * to tab name)",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        actions.setCaption(actions.getCaption() + "*");
                    }
                });

        actions.addComponent(b);

        final UserError e = new UserError("Test error");

        b = new Button("Toggle error", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                if (testContent.getComponentError() == null) {
                    testContent.setComponentError(e);
                } else {
                    testContent.setComponentError(null);
                }
            }
        });
        actions.addComponent(b);

        b = new Button("Change table caption", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                t.setCaption(t.getCaption() + "*");
            }
        });
        actions.addComponent(b);

        b = new Button("Toggle Table error", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                if (t.getComponentError() == null) {
                    t.setComponentError(e);
                } else {
                    t.setComponentError(null);
                }
            }
        });

        actions.addComponent(b);

        for (int i = 0; i < 20; i++) {
            Label l = new Label("Test Content");
            l.setCaption("Extra tab " + i);
            ts.addComponent(l);
        }

        main.addComponent(ts);

    }
}

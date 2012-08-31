package com.vaadin.tests.components.menubar;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.tests.components.ComponentTestCase;
import com.vaadin.ui.Component;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;

public class Menubars extends ComponentTestCase<MenuBar> {

    @Override
    protected Class<MenuBar> getTestClass() {
        return MenuBar.class;
    }

    @Override
    protected void initializeComponents() {

        MenuBar m;
        m = createMenuBar("This is an undefined wide menubar with 3 items", 3);

        m.setWidth(null);
        addTestComponent(m);

        m = createMenuBar(
                "This is an undefined wide menubar with fixed 100px height (4 items)",
                4);
        m.setWidth(null);
        m.setHeight("100px");
        addTestComponent(m);

        m = createMenuBar("This is a 200px wide menubar with 10 items", 10);
        m.setWidth("200px");
        addTestComponent(m);

        m = createMenuBar("This is a 200px wide menubar with 2 items", 2);
        m.setWidth("200px");
        addTestComponent(m);

        m = createMenuBar("This is a 100% wide menubar with 3 items ", 3);
        m.setWidth("100%");
        addTestComponent(m);

        m = createMenuBar("This is a 100% wide menubar with 40 items ", 40);
        m.setWidth("100%");
        addTestComponent(m);

        m = createMenuBar(
                "This is a 100% wide menubar with fixed 65px height (5 items). ",
                5);
        m.setWidth("100%");
        m.setHeight("65px");

        addTestComponent(m);

    }

    private MenuBar createMenuBar(String text, int items) {
        MenuBar m = new MenuBar();
        m.setCaption(text);

        for (int i = 1; i <= items; i++) {
            MenuItem mi = m.addItem("Item " + i, null);
            for (int j = 1; j <= items; j++) {
                mi.addItem("Sub menu " + i + "/" + j, null);
            }
        }

        return m;
    }

    @Override
    protected String getDescription() {
        return "A generic test for MenuBars in different configurations";
    }

    @Override
    protected List<Component> createActions() {
        ArrayList<Component> actions = new ArrayList<Component>();
        actions.add(createErrorIndicatorAction(false));
        actions.add(createEnabledAction(true));

        return actions;
    }

}

package com.itmill.toolkit.tests.components;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Layout;
import com.itmill.toolkit.ui.SplitPanel;
import com.itmill.toolkit.ui.VerticalLayout;
import com.itmill.toolkit.ui.Window;

public abstract class TestBase extends Application {

    @Override
    public final void init() {
        window = new Window(getClass().getName());
        setMainWindow(window);
        window.getLayout().setSizeFull();

        Label label = new Label(getDescription());
        label.setWidth("100%");
        window.getLayout().addComponent(label);

        layout = new VerticalLayout();
        window.getLayout().addComponent(layout);
        ((VerticalLayout) window.getLayout()).setExpandRatio(layout, 1);

        setup();
    }

    private Window window;
    private SplitPanel splitPanel;
    private Layout layout;

    public TestBase() {

    }

    protected Layout getLayout() {
        return layout;
    }

    protected abstract String getDescription();

    protected abstract void setup();

}

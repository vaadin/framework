package com.vaadin.tests.components;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.VerticalLayout;

public abstract class TestBase extends AbstractTestCase {

    @Override
    public final void init() {
        window = new LegacyWindow(getClass().getName());
        setMainWindow(window);
        window.getContent().setSizeFull();

        Label label = new Label(getDescription(), ContentMode.HTML);
        label.setWidth("100%");
        window.getContent().addComponent(label);

        layout = new VerticalLayout();
        window.getContent().addComponent(layout);
        ((VerticalLayout) window.getContent()).setExpandRatio(layout, 1);

        setup();
    }

    private LegacyWindow window;

    @Override
    public void setMainWindow(LegacyWindow mainWindow) {
        if (mainWindow != window) {
            throw new IllegalStateException(
                    "You should not set your own main window when using TestBase. If you need to use a custom Window as the main window, use AbstractTestCase instead.");
        }
        super.setMainWindow(mainWindow);
    }

    private VerticalLayout layout;

    public TestBase() {

    }

    protected VerticalLayout getLayout() {
        return layout;
    }

    protected abstract void setup();

    protected void addComponent(Component c) {
        getLayout().addComponent(c);
    }

    protected void removeComponent(Component c) {
        getLayout().removeComponent(c);
    }

    protected void replaceComponent(Component oldComponent,
            Component newComponent) {
        getLayout().replaceComponent(oldComponent, newComponent);
    }

}

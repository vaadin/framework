package com.vaadin.tests.components.abstractcomponent;

import com.vaadin.tests.components.AbstractTestCase;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.LegacyWindow;

public class EnableState extends AbstractTestCase {
    @Override
    public void init() {
        LegacyWindow mainWindow = new LegacyWindow("Helloworld Application");

        VerticalLayout panelLayout = new VerticalLayout();
        panelLayout.setMargin(true);
        final Panel panel = new Panel("Test", panelLayout);
        final Button button = new Button("ablebutton");
        panelLayout.addComponent(button);

        CheckBox enable = new CheckBox("Toggle button enabled", true);
        enable.addValueChangeListener(event -> {
            boolean enabled = event.getValue();
            button.setEnabled(enabled);
            // button.requestRepaint();
        });
        enable.setImmediate(true);

        CheckBox caption = new CheckBox("Toggle button caption", true);
        caption.addValueChangeListener(
                event -> button.setCaption(button.getCaption() + "+"));
        caption.setImmediate(true);

        CheckBox visible = new CheckBox("Toggle panel visibility", true);
        visible.addValueChangeListener(
                event -> panel.setVisible(event.getValue()));
        visible.setImmediate(true);

        CheckBox panelEnable = new CheckBox("Toggle panel enabled", true);
        panelEnable.addValueChangeListener(
                event -> panel.setEnabled(event.getValue()));
        panelEnable.setImmediate(true);

        mainWindow.addComponent(enable);
        mainWindow.addComponent(caption);
        mainWindow.addComponent(visible);
        mainWindow.addComponent(panelEnable);
        mainWindow.addComponent(panel);

        setMainWindow(mainWindow);
    }

    @Override
    protected String getDescription() {
        return "This tests the enabled/disabled propagation and that enabled/disabled state is updated"
                + " properly even when the parent is invisible. Disabling the Button while the panel is"
                + " invisible should be reflected on the screen when the panel is set visible"
                + " again.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3609;
    }
}

package com.vaadin.tests.components.abstractcomponent;

import com.vaadin.tests.components.AbstractTestCase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Window;

public class EnableState extends AbstractTestCase {
    @Override
    public void init() {
        Window mainWindow = new Window("Helloworld Application");

        final Panel panel = new Panel("Test");
        final Button button = new Button("ablebutton");
        panel.addComponent(button);

        CheckBox enable = new CheckBox("Toggle button enabled", true);
        enable.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                boolean enabled = (Boolean) event.getButton().getValue();
                button.setEnabled(enabled);
                // button.requestRepaint();
            }
        });
        enable.setImmediate(true);

        CheckBox caption = new CheckBox("Toggle button caption", true);
        caption.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                button.setCaption(button.getCaption() + "+");
            }
        });
        caption.setImmediate(true);

        CheckBox visible = new CheckBox("Toggle panel visibility", true);
        visible.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                boolean visible = (Boolean) event.getButton().getValue();

                panel.setVisible(visible);
            }
        });
        visible.setImmediate(true);

        CheckBox panelEnable = new CheckBox("Toggle panel enabled", true);
        panelEnable.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                boolean enabled = (Boolean) event.getButton().getValue();
                panel.setEnabled(enabled);
            }
        });
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

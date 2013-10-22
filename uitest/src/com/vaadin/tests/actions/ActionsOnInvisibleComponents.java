package com.vaadin.tests.actions;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

public class ActionsOnInvisibleComponents extends AbstractTestUIWithLog {

    private static final long serialVersionUID = -5993467736906948993L;

    @Override
    protected void setup(VaadinRequest request) {
        getContent().setId("test-root");
        log("'A' triggers a click on an invisible button");
        log("'B' triggers a click on a disabled button");
        log("'C' triggers a click on a visible and enabled button");

        Button invisibleButton = new Button("Invisible button with shortcut");
        invisibleButton.setClickShortcut(KeyCode.A);
        invisibleButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                log("Click event for invisible button");
            }
        });

        invisibleButton.setVisible(false);
        addComponent(invisibleButton);

        Button disabledButton = new Button("Disabled button with shortcut");
        disabledButton.setClickShortcut(KeyCode.B);
        disabledButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                log("Click event for disabled button");
            }
        });

        disabledButton.setEnabled(false);
        addComponent(disabledButton);

        Button enabledButton = new Button("Enabled button with shortcut");
        enabledButton.setClickShortcut(KeyCode.C);
        enabledButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                log("Click event for enabled button");
            }
        });

        addComponent(enabledButton);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "Test to ensure actions are not performed on disabled/invisible components";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return 12743;
    }

}

package com.vaadin.tests.components.button;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

public class TooltipForDisabledButton extends TestBase {

    @Override
    protected String getDescription() {
        return "A disabled button should show a tooltip when hovering it";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2085;
    }

    @Override
    protected void setup() {
        Button buttonEnabled = new Button("This is an enabled button");
        Button buttonDisabled = new Button("This is an disabled button");
        buttonDisabled.setEnabled(false);

        buttonEnabled.setDescription("Tooltip for enabled");
        buttonDisabled.setDescription("Tooltip for disabled");

        buttonDisabled.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                getMainWindow().showNotification("Clicked Disabled");
            }

        });

        buttonEnabled.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                getMainWindow().showNotification("Clicked Enabled");
            }

        });

        addComponent(buttonEnabled);
        addComponent(buttonDisabled);
    }

}

package com.vaadin.tests.components.checkbox;

import com.vaadin.server.ThemeResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.CheckBox;

public class CheckboxIcon extends TestBase {

    @Override
    protected String getDescription() {
        return "The icon of a Checkbox component should have the same cursor as the text and should be clickable. The tooltip should appear when hovering the checkbox, the icon or the caption.";
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void setup() {
        CheckBox checkbox = new CheckBox("A checkbox");
        checkbox.setIcon(new ThemeResource("../runo/icons/32/calendar.png"));
        checkbox.setDescription("Tooltip for checkbox");

        addComponent(checkbox);
    }

}

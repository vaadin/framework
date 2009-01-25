package com.itmill.toolkit.tests.components.checkbox;

import com.itmill.toolkit.terminal.ThemeResource;
import com.itmill.toolkit.tests.components.TestBase;
import com.itmill.toolkit.ui.CheckBox;

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
        checkbox.setIcon(new ThemeResource("icons/32/calendar.png"));
        checkbox.setDescription("Tooltip for checkbox");

        addComponent(checkbox);
    }

}

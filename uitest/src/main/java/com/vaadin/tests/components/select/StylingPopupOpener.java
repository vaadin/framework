package com.vaadin.tests.components.select;

import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.TestUtils;
import com.vaadin.ui.Button;
import com.vaadin.v7.ui.Select;

public class StylingPopupOpener extends TestBase {

    @Override
    protected void setup() {
        TestUtils.injectCSS(getMainWindow(),
                ".v-filterselect-mystyle .v-filterselect-button { width: 50px; background-color: red; } ");

        final Select select = new Select();
        addComponent(select);

        addComponent(new Button("Update style",
                event -> select.setStyleName("mystyle")));
    }

    @Override
    protected String getDescription() {
        return "ComboBox popup opener width is not updated when the style or theme changes";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8801;
    }

}

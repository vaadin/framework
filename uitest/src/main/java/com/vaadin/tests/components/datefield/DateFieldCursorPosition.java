package com.vaadin.tests.components.datefield;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Label;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class DateFieldCursorPosition extends AbstractTestUI {
    @Override
    protected void setup(VaadinRequest request) {
        Label description = new Label();
        description.setValue("<ol> <li>Open the pop-up</li>\n"
                + "      <li>Select any day. Pop-up is automatically closed</li> </ol>\n"
                + "      <b>Expected</b> : the cursor is positioned in the end of input field </br>\n"
                + "      <b>Actual</b> : the cursor is at the beginning of input</br>\n"
                + "      Reproducable on IE on first selection, on Chrome after the first selection is performed. Works correctly in FF.\n");
        description.setContentMode(ContentMode.HTML);
        addComponent(description);
        DateField df = new DateField();
        df.setDateFormat("dd/MM/yyyy");
        addComponent(df);
    }

    @Override
    protected Integer getTicketNumber() {
        return 11455;
    }
}

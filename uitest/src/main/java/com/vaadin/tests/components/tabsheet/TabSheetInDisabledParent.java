package com.vaadin.tests.components.tabsheet;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;

/**
 * Test UI to check that TabsheetBaseConnector reacts on disabling its parent.
 *
 * @author Vaadin Ltd
 */
public class TabSheetInDisabledParent extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final HorizontalLayout layout = new HorizontalLayout();
        addComponent(new Button("toggle",
                event -> layout.setEnabled(!layout.isEnabled())));
        addComponent(layout);

        TabSheet sheet = new TabSheet();
        Label label1 = new Label("Label1");
        label1.setCaption("Label 1");
        sheet.addComponent(label1);

        Label label2 = new Label("Label2");
        label2.setCaption("Label 2");
        sheet.addComponent(label2);

        Label label3 = new Label("Label3");
        label3.setCaption("Label 3");
        sheet.addComponent(label3);

        layout.addComponent(sheet);
    }

    @Override
    protected String getTestDescription() {
        return "VTabsheetBase widget should implement HasEnabled interface.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14114;
    }

}

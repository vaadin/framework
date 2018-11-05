package com.vaadin.tests.components.gridlayout;

import com.vaadin.server.UserError;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.components.TestDateField;
import com.vaadin.ui.AbstractDateField;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Panel;

public class LayoutAfterHidingError extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Panel panel = new Panel();
        panel.setWidth("300px");
        addComponent(panel);

        GridLayout gl = new GridLayout();
        gl.setWidth("100%");
        panel.setContent(gl);

        final AbstractDateField<?, ?> df = new TestDateField();
        df.setWidth("100%");
        gl.addComponent(df);

        Button err = new Button("Set error");
        err.addClickListener(
                event -> df.setComponentError(new UserError("foo")));
        gl.addComponent(err);

        err = new Button("Clear error");
        err.addClickListener(event -> df.setComponentError(null));
        gl.addComponent(err);
    }

    @Override
    protected String getTestDescription() {
        return "Setting an error icon for a component in GridLayout and then removing it should properly re-size the component";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12011;
    }

}

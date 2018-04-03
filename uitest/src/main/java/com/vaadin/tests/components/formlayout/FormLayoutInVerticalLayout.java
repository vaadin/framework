package com.vaadin.tests.components.formlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.TextField;

/**
 * Test UI for H2 label inside FormLayout.
 *
 * @author Vaadin Ltd
 */
public class FormLayoutInVerticalLayout extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        CssLayout container = new CssLayout();
        addComponent(container);

        FormLayout formLayout = new FormLayout();

        Label sectionLabel = createLabel();
        formLayout.addComponent(sectionLabel);

        TextField nameTextField = new TextField("Name");
        nameTextField.setValue("Lorem ipsum");
        nameTextField.setWidth("50%");
        formLayout.addComponent(nameTextField);

        container.addComponent(formLayout);
        container.addComponent(createLabel());
    }

    @Override
    protected Integer getTicketNumber() {
        return super.getTicketNumber();
    }

    @Override
    protected String getTestDescription() {
        return "FormLayout 'margin-top' value should take precedence over "
                + "the rule defined in any other selector.";
    }

    private Label createLabel() {
        Label sectionLabel = new Label("Personal info");
        sectionLabel.addStyleName(ValoTheme.LABEL_H2);
        sectionLabel.addStyleName(ValoTheme.LABEL_COLORED);
        return sectionLabel;
    }
}

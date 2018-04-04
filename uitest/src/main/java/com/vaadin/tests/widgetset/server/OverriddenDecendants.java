package com.vaadin.tests.widgetset.server;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.ui.TextArea;

/**
 * UI for testing that @DelegateToWidget works on derived widget states.
 *
 * @author Vaadin Ltd
 */
@Widgetset(TestingWidgetSet.NAME)
public class OverriddenDecendants extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {

        TextArea normalTextArea = new TextArea();
        normalTextArea.setRows(10);
        normalTextArea.setWordWrap(true);

        getLayout().addComponent(normalTextArea);

        // @DelegateToWidget will not work with overridden state in connector
        SuperTextArea superTextArea = new SuperTextArea();
        superTextArea.setRows(10);
        superTextArea.setWordWrap(true);

        getLayout().addComponent(superTextArea);

        // @DelegateToWidget will not work with overridden state in connector
        ExtraSuperTextArea extraSuperTextArea = new ExtraSuperTextArea();
        extraSuperTextArea.setRows(10);
        extraSuperTextArea.setWordWrap(true);

        getLayout().addComponent(extraSuperTextArea);
    }

    @Override
    protected String getTestDescription() {
        return "@DelegateToWidget does not work for widget descendants with overridden getState";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14059;
    }

}

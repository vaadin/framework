package com.vaadin.tests.components.popupview;

import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupView;

/**
 * Test UI for popup view with extension: extension is a part of getChildren()
 * collection but is not inside the getChildComponents() collection. Popup view
 * should use getChildComponents() to avoid exception when extension is returned
 * by getChildren().
 *
 * @since 7.2
 * @author Vaadin Ltd
 */
public class PopupViewWithExtension extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Label label = new Label("label");
        PopupView view = new PopupView("small", label);

        Responsive.makeResponsive(view);

        addComponent(view);
    }

    @Override
    protected String getTestDescription() {
        return "PopupView should use getChildComponents() in the connector, not getChildren()";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13503;
    }

}

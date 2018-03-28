package com.vaadin.tests.components.panel;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.ListSelect;

@SuppressWarnings("serial")
public class WebkitScrollbarTest extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Panel panel = new Panel();

        VerticalLayout content = new VerticalLayout();
        panel.setContent(content);

        GridLayout gridLayout = new GridLayout();
        gridLayout.setHeight(null);
        gridLayout.setWidth(100, Unit.PERCENTAGE);
        content.addComponent(gridLayout);

        ListSelect listSelect = new ListSelect();

        listSelect.setWidth(100, Unit.PERCENTAGE);
        listSelect.setHeight(300, Unit.PIXELS);

        gridLayout.addComponent(listSelect);

        gridLayout.setMargin(true);

        setContent(panel);
    }

    @Override
    protected String getTestDescription() {
        return "When opening the window, it should NOT contain a horizontal";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12727;
    }

}

package com.vaadin.tests.extensions;

import com.vaadin.annotations.Theme;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

@Theme("tests-responsive")
public class ResponsiveLayoutUpdate extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(false);
        layout.addStyleName("layout-update");
        layout.setWidth("100%");
        setContent(layout);
        Responsive.makeResponsive(layout);

        Label label = new Label(
                "This label changes its size between the breakpoints, allowing more space for the adjacent component.");
        label.addStyleName("change-width");
        layout.addComponent(label);

        Panel panel = new Panel("Panel");
        Label label2 = new Label(
                "This Panel should be maximized in both breakpoints.");
        label2.setWidth("100%");
        panel.setContent(label2);
        panel.setSizeFull();
        layout.addComponent(panel);
        layout.setExpandRatio(panel, 1);
    }

    @Override
    protected String getTestDescription() {
        return "A new layout phase should be requested after a new breakpoint is triggered, ensuring any style changes affecting component sizes are taken into account.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14354;
    }
}

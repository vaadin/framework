package com.vaadin.tests.themes.valo;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

public class ResponsiveStyles extends UI {

    @Override
    protected void init(VaadinRequest request) {
        ResponsiveStylesDesign design = new ResponsiveStylesDesign();
        setContent(design);

        boolean collapsed = request.getParameter("collapsed") != null;

        design.collapsed.setVisible(collapsed);
        design.narrow.setVisible(!collapsed);
        design.wide.setVisible(!collapsed);
    }

}

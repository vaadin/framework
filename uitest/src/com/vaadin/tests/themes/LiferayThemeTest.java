package com.vaadin.tests.themes;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.themes.LiferayTheme;

@Theme("liferay")
public class LiferayThemeTest extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Panel p = new Panel("Panel");
        addComponent(p);
        p.addComponent(new Label("Panel content"));

        p = new Panel("Light Panel");
        p.addStyleName(LiferayTheme.PANEL_LIGHT);
        addComponent(p);
        p.addComponent(new Label("Panel content"));
    }

    @Override
    protected String getTestDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}

package com.vaadin.tests.themes;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.LiferayTheme;

@Theme("liferay")
public class LiferayThemeTest extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout pl = new VerticalLayout();
        pl.setMargin(true);
        Panel p = new Panel("Panel", pl);
        addComponent(p);
        pl.addComponent(new Label("Panel content"));

        pl = new VerticalLayout();
        pl.setMargin(true);
        p = new Panel("Light Panel", pl);
        p.addStyleName(LiferayTheme.PANEL_LIGHT);
        addComponent(p);
        pl.addComponent(new Label("Panel content"));
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

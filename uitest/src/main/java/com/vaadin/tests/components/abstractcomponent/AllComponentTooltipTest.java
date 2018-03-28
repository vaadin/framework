package com.vaadin.tests.components.abstractcomponent;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.VaadinClasses;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.LegacyWindow;

public class AllComponentTooltipTest extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        GridLayout layout = new GridLayout(5, 5);
        setContent(layout);
        for (Class<? extends Component> cls : VaadinClasses.getComponents()) {
            try {
                AbstractComponent c = (AbstractComponent) cls.newInstance();
                if (c instanceof LegacyWindow) {
                    continue;
                }

                c.setId(cls.getName());
                c.setCaption(cls.getName());
                c.setDescription(cls.getName());
                c.setWidth("100px");
                c.setHeight("100px");
                layout.addComponent(c);
                System.out.println("Added " + cls.getName());
            } catch (Exception e) {
                System.err.println("Could not instatiate " + cls.getName());
            }
        }
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

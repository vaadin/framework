/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.tests.components.abstractcomponent;

import com.vaadin.terminal.WrappedRequest;
import com.vaadin.tests.VaadinClasses;
import com.vaadin.tests.components.AbstractTestRoot;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;

public class AllComponentTooltipTest extends AbstractTestRoot {

    @Override
    protected void setup(WrappedRequest request) {
        setContent(new GridLayout(5, 5));
        for (Class<? extends Component> cls : VaadinClasses.getComponents()) {
            try {
                AbstractComponent c = (AbstractComponent) cls.newInstance();
                if (c instanceof LegacyWindow) {
                    continue;
                }

                c.setDebugId(cls.getName());
                c.setCaption(cls.getName());
                c.setDescription(cls.getName());
                c.setWidth("100px");
                c.setHeight("100px");
                getContent().addComponent(c);
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

package com.vaadin.tests.integration;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.VerticalLayout;

/**
 * On Liferay in a freeform layout, this application should get its height from
 * the height of the portlet container in the Liferay layout.
 * 
 * See ticket #5521.
 */
public class PortletSizeInLiferayFreeformLayoutApplication extends
        LegacyApplication {
    @Override
    public void init() {
        LegacyWindow mainWindow = new LegacyWindow("Portlet5521 Application");
        ((VerticalLayout) mainWindow.getContent()).setMargin(false);
        ((VerticalLayout) mainWindow.getContent()).setSizeFull();
        // ((VerticalLayout) mainWindow.getContent()).setHeight("200px");
        Label label = new Label("Hello Vaadin user");
        mainWindow.addComponent(label);
        for (int i = 0; i < 50; ++i) {
            mainWindow.addComponent(new Label("Label " + i));
        }
        mainWindow.setSizeFull();
        setMainWindow(mainWindow);
    }

}

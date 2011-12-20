/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.tests;

import com.vaadin.ui.Label;
import com.vaadin.ui.Root.LegacyWindow;
import com.vaadin.ui.VerticalSplitPanel;

public class TestSplitPanel extends com.vaadin.Application.LegacyApplication {

    VerticalSplitPanel verticalSplit = new VerticalSplitPanel();

    @Override
    public void init() {
        final LegacyWindow mainWindow = new LegacyWindow("Feature Browser");
        setMainWindow(mainWindow);

        verticalSplit.setFirstComponent(new Label("vertical first"));
        verticalSplit.setSecondComponent(new Label("vertical second"));

        mainWindow.setContent(verticalSplit);

    }

}

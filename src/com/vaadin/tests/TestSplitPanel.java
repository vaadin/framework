/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.tests;

import com.vaadin.ui.Label;
import com.vaadin.ui.SplitPanel;
import com.vaadin.ui.Window;

public class TestSplitPanel extends com.vaadin.Application {

    // SplitPanel verticalSplit = new
    // SplitPanel(SplitPanel.ORIENTATION_HORIZONTAL);
    SplitPanel verticalSplit = new SplitPanel(SplitPanel.ORIENTATION_VERTICAL);

    @Override
    public void init() {
        final Window mainWindow = new Window("Feature Browser");
        setMainWindow(mainWindow);

        verticalSplit.setFirstComponent(new Label("vertical first"));
        verticalSplit.setSecondComponent(new Label("vertical second"));

        mainWindow.setLayout(verticalSplit);

    }

}

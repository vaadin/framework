package com.itmill.toolkit.demo.testbench;

import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.SplitPanel;
import com.itmill.toolkit.ui.Window;

public class TestSplitPanel extends com.itmill.toolkit.Application {

    SplitPanel verticalSplit = new SplitPanel(SplitPanel.ORIENTATION_VERTICAL);

    public void init() {
        Window mainWindow = new Window("Feature Browser");
        setMainWindow(mainWindow);

        verticalSplit.setFirstComponent(new Label("vertical first"));
        verticalSplit.setSecondComponent(new Label("vertical second"));

        mainWindow.setLayout(verticalSplit);

    }

}

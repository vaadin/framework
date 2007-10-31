package com.itmill.toolkit.tests;

import com.itmill.toolkit.ui.*;

public class TestSplitPanel extends com.itmill.toolkit.Application {

	// SplitPanel verticalSplit = new
	// SplitPanel(SplitPanel.ORIENTATION_HORIZONTAL);
	SplitPanel verticalSplit = new SplitPanel(SplitPanel.ORIENTATION_VERTICAL);

	public void init() {
		Window mainWindow = new Window("Feature Browser");
		setMainWindow(mainWindow);

		verticalSplit.setFirstComponent(new Label("vertical first"));
		verticalSplit.setSecondComponent(new Label("vertical second"));

		mainWindow.setLayout(verticalSplit);

	}

}

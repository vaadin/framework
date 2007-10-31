package com.itmill.toolkit.tests;

import com.itmill.toolkit.ui.*;

public class TestSplitPanel extends com.itmill.toolkit.Application {

	SplitPanel horizontalSplit = new SplitPanel(
			SplitPanel.ORIENTATION_HORIZONTAL);
	// this works
	//SplitPanel verticalSplit = new SplitPanel(SplitPanel.ORIENTATION_HORIZONTAL);
	// TODO: this does NOT work
	SplitPanel verticalSplit = new SplitPanel(SplitPanel.ORIENTATION_VERTICAL);
	
	public void init() {
		Window mainWindow = new Window("Feature Browser");
		setMainWindow(mainWindow);
		
		verticalSplit.setFirstComponent(new Label("vertical first"));
		verticalSplit.setSecondComponent(new Label("vertical second"));
		
//		horizontalSplit.setFirstComponent(new Label("horizontal first"));
//		horizontalSplit.setSecondComponent(new Label("horizontal second"));

		mainWindow.setLayout(verticalSplit);
	}

}

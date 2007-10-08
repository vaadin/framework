package com.itmill.toolkit.tests;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.*;

public class TestForApplicationLayoutThatUsesWholeBrosersSpace extends
		Application {

	Window main = new Window("Windowing test");

	ExpandLayout rootLayout;

	SplitPanel firstLevelSplit;

	public void init() {
		setMainWindow(main);

		rootLayout = new ExpandLayout();
		main.setLayout(rootLayout);

		rootLayout.addComponent(new Label("header"));

		firstLevelSplit = new SplitPanel();

		SplitPanel secondSplitPanel = new SplitPanel(
				SplitPanel.ORIENTATION_HORIZONTAL);
		secondSplitPanel.setFirstComponent(new Label("left"));
		
		ExpandLayout topRight = new ExpandLayout();
		topRight.addComponent(new Label("topright header"));
		
		Table t = TestForTablesInitialColumnWidthLogicRendering.getTestTable(4, 100);
		t.setWidth(100);
		t.setWidthUnits(Table.UNITS_PERCENTAGE);
		t.setHeight(100);
		t.setHeightUnits(Table.UNITS_PERCENTAGE);
		topRight.addComponent(t);
		topRight.expand(t);

		topRight.addComponent(new Label("topright footer"));

		secondSplitPanel.setSecondComponent(topRight);


		ExpandLayout el = new ExpandLayout();
		el.addComponent(new Label("Bšš"));

		firstLevelSplit.setFirstComponent(secondSplitPanel);
		firstLevelSplit.setSecondComponent(el);

		rootLayout.addComponent(firstLevelSplit);
		rootLayout.expand(firstLevelSplit);

		rootLayout.addComponent(new Label("footer"));

	}

}

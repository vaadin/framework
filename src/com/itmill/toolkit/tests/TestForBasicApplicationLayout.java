package com.itmill.toolkit.tests;

import java.util.Locale;

import com.itmill.toolkit.terminal.Sizeable;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.DateField;
import com.itmill.toolkit.ui.ExpandLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.SplitPanel;
import com.itmill.toolkit.ui.TabSheet;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Button.ClickListener;

public class TestForBasicApplicationLayout extends CustomComponent {

	private Button click;
	private Button click2;
	private TabSheet tab;

	public TestForBasicApplicationLayout() {
		OrderedLayout main = new OrderedLayout();
		setCompositionRoot(main);

		click = new Button("Set height -1", new ClickListener() {

			public void buttonClick(ClickEvent event) {
				tab.setHeight(-1);
			}

		});

		click2 = new Button("Set height 100%", new ClickListener() {

			public void buttonClick(ClickEvent event) {
				tab.setHeight(100);
				tab.setHeightUnits(Sizeable.UNITS_PERCENTAGE);
			}

		});

		SplitPanel sp = new SplitPanel(SplitPanel.ORIENTATION_HORIZONTAL);
		sp.setSplitPosition(290, Sizeable.UNITS_PIXELS); // Width of left
															// side area

		SplitPanel sp2 = new SplitPanel(SplitPanel.ORIENTATION_VERTICAL);
		sp2.setSplitPosition(255, Sizeable.UNITS_PIXELS); // Height of
															// right-top area

		Panel p = new Panel("Accordion Panel");
		p.setHeight(100);
		p.setHeightUnits(Panel.UNITS_PERCENTAGE);

		tab = new TabSheet();
		tab.setWidth(100);
		tab.setWidthUnits(Sizeable.UNITS_PERCENTAGE);
		tab.setHeight(740);
		tab.setHeightUnits(Sizeable.UNITS_PIXELS);

		Panel report = new Panel("Monthly Program Runs", new ExpandLayout());
		OrderedLayout controls = new OrderedLayout();
		controls.addComponent(new Label("Report tab"));
		controls.addComponent(click);
		controls.addComponent(click2);
		report.addComponent(controls);
		DateField cal = new DateField();
		cal.setResolution(DateField.RESOLUTION_DAY);
		cal.setLocale(new Locale("en", "US"));
		report.addComponent(cal);
		((ExpandLayout) report.getLayout()).expand(controls);
		report.setStyle("light");
		report.setHeight(100);
		report.setHeightUnits(Sizeable.UNITS_PERCENTAGE);

		sp2.setFirstComponent(report);

		Table table = TestForTablesInitialColumnWidthLogicRendering
				.getTestTable(5, 200);
		table.setPageLength(15);
		table.setSelectable(true);
		table.setRowHeaderMode(Table.ROW_HEADER_MODE_INDEX);
		table.setColumnCollapsingAllowed(true);
		table.setColumnReorderingAllowed(true);
		table.setSortDisabled(false);
		table.setWidth(100);
		table.setWidthUnits(Sizeable.UNITS_PERCENTAGE);
		table.setHeight(100);
		table.setHeightUnits(Sizeable.UNITS_PERCENTAGE);
		table.addStyleName("table-inline");
		sp2.setSecondComponent(table);

		tab.addTab(new Label("Tab1"), "Summary", null);
		tab.addTab(sp2, "Reports", null);
		tab.addTab(new Label("Tab 3"), "Statistics", null);
		tab.addTab(new Label("Tab 4"), "Error Tracking", null);
		tab.setSelectedTab(sp2);

		sp.setFirstComponent(p);
		sp.setSecondComponent(tab);

		main.addComponent(sp);
	}

}

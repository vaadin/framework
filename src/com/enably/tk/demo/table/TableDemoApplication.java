
package com.enably.tk.demo.table;

import com.enably.tk.Application;
import com.enably.tk.event.Action;
import com.enably.tk.terminal.ExternalResource;
import com.enably.tk.ui.Button;
import com.enably.tk.ui.DateField;
import com.enably.tk.ui.GridLayout;
import com.enably.tk.ui.Label;
import com.enably.tk.ui.Link;
import com.enably.tk.ui.Panel;
import com.enably.tk.ui.Select;
import com.enably.tk.ui.TabSheet;
import com.enably.tk.ui.Table;
import com.enably.tk.ui.TextField;
import com.enably.tk.ui.Tree;
import com.enably.tk.ui.Window;


public class TableDemoApplication extends Application implements Action.Handler {

	private Table myTable;
	private String[] texts = {"Lorem","Ipsum","Dolor","Sit","Amet"};
	private Action deleteAction = new Action("Delete row");

	public void init() {
		Window mainWindow = new Window("Millstone Example");
		setMainWindow(mainWindow);
		
		GridLayout gl1 = new GridLayout(1,2);
		GridLayout gl2 = new GridLayout(2,1);
		gl1.addComponent(gl2);
		
		myTable = new Table("Table caption");
		myTable.setMultiSelect(true);
		myTable.setPageLength(10);
		myTable.setSelectable(true);
		myTable.setColumnHeaderMode(Table.COLUMN_HEADER_MODE_ID);
		
		myTable.addContainerProperty("text", String.class, "-");
		myTable.addContainerProperty("number", Integer.class, new Integer(0));
		//myTable.addContainerProperty("date", DateField.class, "");
		myTable.setColumnReorderingAllowed(true);
		myTable.setColumnCollapsingAllowed(true);
		myTable.addActionHandler(this);
		myTable.setRowHeaderMode(Table.ROW_HEADER_MODE_INDEX);
		//myTable.setDescription("Table description text.");
		
		DateField df = new DateField();
		df.setValue(new java.util.Date());
		df.setReadOnly(true);
		df.setResolution(DateField.RESOLUTION_DAY);
		df.setStyle("text");
		
		for(int i=0; i<10000; i++) {
			myTable.addItem(
					new Object[] {
						texts[(int) (Math.random() * 5)],
						new Integer((int) (Math.random() * 80))},
					new Integer(i));
		}
		
		TabSheet ts = new TabSheet();
		
		Panel codeSamplePanel = new Panel();
		codeSamplePanel.setCaption("Example panel");
		codeSamplePanel.setDescription("A code example how to implement a Table into your application.");
		codeSamplePanel.setStyle("light");
		TextField codeSample = new TextField("Code sample");
		codeSamplePanel.addComponent(codeSample);
		ts.addTab(codeSamplePanel, "Code Sample", null);
		
		Label info = new Label();
		info.setContentMode(Label.CONTENT_XHTML);
		info.setValue("<h1>Millstone AjaxAdapter</h1><p>Examples of Millstone components.</p><h2>Table Component demo</h2><p>General info about the component and its properties (static HTML).</p><h3>Third level heading</h3><h4>Subheading</h4><h5>Paragraph heading</h5><p>Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Fusce pharetra congue nunc. Vestibulum odio metus, tristiqueeu, venenatis eget, nonummy vel, mauris.</p><p>Mauris lobortis dictum dolor. Phasellus suscipit. Nam feugiat est in risus.</p>");
		ts.addTab(myTable,"Info", null);
				
		//mainWindow.addComponent(ts);
		//mainWindow.addComponent(info);
		//mainWindow.addComponent(myTable);
				
		/* Theme testing purposes */
		Button b = new Button("Button caption");
		//b.setStyle("link");
		//b.setDescription("Button description text.");
		//mainWindow.addComponent(b);
		
		Link lnk = new Link("Link caption",new ExternalResource("http://www.itmill.com"));
		lnk.setDescription("Link description text.");
		Panel show = new Panel("Panel caption");
		show.setStyle("");
		show.addComponent(lnk);
		show.setWidth(350);
		show.setWidthUnits(Panel.UNITS_PIXELS);
		show.setHeightUnits(Panel.UNITS_PIXELS);
		//mainWindow.addComponent(show);
		
		gl2.addComponent(info);
		gl2.addComponent(show);
		gl2.addComponent(codeSamplePanel);
		gl1.addComponent(myTable);
		gl1.addComponent(b);
		mainWindow.addComponent(gl1);
		
		Select s = new Select("Select Car");
		s.addItem("Audi");
		s.addItem("BMW");
		s.addItem("Chrysler");
		s.addItem("Volvo");
		//show.addComponent(s);
		
		// Create tree
		Tree t = new Tree("Family Tree");
		for (int i = 0; i < 4; i++) {
			t.addItem(texts[i]);
			String parent = texts[(int) (Math.random() * (texts.length - 1))];
			if (t.containsId(parent)) 
				t.setParent(texts[i],parent);
		}
		
		// Forbid childless people to have children (makes them leaves)
		for (int i = 0; i < 4; i++) {
			if (!t.hasChildren(texts[i])) {
				t.setChildrenAllowed(texts[i], false);
			}
		}
		//mainWindow.addComponent(t);
		
		/*
		Upload u = new Upload("Upload a file:", new uploadReceiver());
		mainWindow.addComponent(u);
		*/
	}

	public Action[] getActions(Object arg0, Object arg1) {
		Action[] actions = {deleteAction};
		return actions;
	}

	public void handleAction(Action arg0, Object arg1, Object arg2) {
		if(arg0 != null) {
			if(arg0.getCaption() == "Delete row") {
				myTable.removeItem(arg2);
			}
		}		
	}
}

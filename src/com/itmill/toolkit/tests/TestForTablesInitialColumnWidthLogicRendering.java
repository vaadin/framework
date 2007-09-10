package com.itmill.toolkit.tests;

import java.util.Vector;

import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Table;

/**
 * 
 * This Component contains some simple test to see that component
 * updates its contents propertly.
 * 
 * @author IT Mill Ltd.
 */
public class TestForTablesInitialColumnWidthLogicRendering extends CustomComponent {
	

	private OrderedLayout main = new OrderedLayout();

	public TestForTablesInitialColumnWidthLogicRendering() {

		setCompositionRoot(main);
		createNewView();
	}
	
	public void createNewView() {
		main.removeAllComponents();
		main.addComponent(new Label("Below are same tables that all should render somewhat nice. Also when testing, you might want to try resizing window."));
		
		Table t;
		
//		t = new Table("Empty table");
//		main.addComponent(t);
		
		
		t = getTestTable(5, 0);
		t.setCaption("Table with only headers");
//		main.addComponent(t);

		t = getTestTable(5, 200);
		t.setCaption("Table with  some cols and lot of rows");
		main.addComponent(t);

		
		t = getTestTable(12, 4);
		t.setCaption("Table with  some rows and lot of columns");
		main.addComponent(t);
		
		t = getTestTable(3, 40);
		t.setCaption("Table with some columns and wide explicit width. (Ought to widen columns to use all space)");
		t.setWidth(1000);
		main.addComponent(t);


		t = getTestTable(12, 4);
		t.setCaption("Table with  some rows and lot of columns, width == 100%");
		t.setWidth(100);
		t.setWidthUnits(Table.UNITS_PERCENTAGE);
		main.addComponent(t);

		t = getTestTable(12, 100);
		t.setCaption("Table with  lot of rows and lot of columns, width == 50%");
		t.setWidth(50);
		t.setWidthUnits(Table.UNITS_PERCENTAGE);
		main.addComponent(t);

		
		t = getTestTable(5, 100);
		t.setCaption("Table with 40 rows");
//		main.addComponent(t);
		
		t = getTestTable(4, 4);
		t.setCaption("Table with some rows and width = 200px");

		t.setWidth(200);
		main.addComponent(t);
		
		Button b = new Button("refresh view", this, "createNewView");
		main.addComponent(b);

	}
	
	public Table getTestTable(int cols, int rows) {
		Table t = new Table();
		t.setColumnCollapsingAllowed(true);
		for(int i = 0; i < cols; i++) {
			t.addContainerProperty(testString[i], String.class, "");
		}
		for(int i = 0; i < rows; i++) {
			Vector content = new Vector();
			for(int j = 0; j < cols;j++) {
				content.add(rndString());
			}
			t.addItem(content.toArray(), ""+i);
		}
		return t;
	}
	
	static String[] testString = new String[] {
		"DSFdsfs",
		"böö",
		"1",
		"sdf sdfsd fsdfsdf sdf",
		"SDF SADds FASDF dsaf",
		"foo",
		"VADSFA",
		"DSFSD FS",
		"whattaa",
		" sdf sdfsd ",
		"DSf sdf sdf",
		"foods f",
		"VADsd fSFA",
		"DSFsd fSD FS",
		"wha sdf ttaa",
		" sd sdff sdfsd ",
		"DSf sdf sdf",
		"SDFsd sd fadsfadfs"
	};
	
	
	public static String rndString() {
		return testString[(int) (Math.random()*testString.length)];
	}
	
}



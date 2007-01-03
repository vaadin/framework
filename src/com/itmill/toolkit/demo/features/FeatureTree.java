/* *************************************************************************
 
                               IT Mill Toolkit 

               Development of Browser User Intarfaces Made Easy

                    Copyright (C) 2000-2006 IT Mill Ltd
                     
   *************************************************************************

   This product is distributed under commercial license that can be found
   from the product package on license/license.txt. Use of this product might 
   require purchasing a commercial license from IT Mill Ltd. For guidelines 
   on usage, see license/licensing-guidelines.html

   *************************************************************************
   
   For more information, contact:
   
   IT Mill Ltd                           phone: +358 2 4802 7180
   Ruukinkatu 2-4                        fax:   +358 2 4802 7181
   20540, Turku                          email:  info@itmill.com
   Finland                               company www: www.itmill.com
   
   Primary source for information and releases: www.itmill.com

   ********************************************************************** */

package com.itmill.toolkit.demo.features;

import java.util.Iterator;

import com.itmill.toolkit.event.Action;
import com.itmill.toolkit.ui.*;

public class FeatureTree extends Feature implements Action.Handler {

	private static final String[] firstnames =
		new String[] {
			"John",
			"Mary",
			"Joe",
			"Sarah",
			"Jeff",
			"Jane",
			"Peter",
			"Marc",
			"Josie",
			"Linus" };
	private static final String[] lastnames =
		new String[] {
			"Torvalds",
			"Smith",
			"Jones",
			"Beck",
			"Sheridan",
			"Picard",
			"Hill",
			"Fielding",
			"Einstein" };

	private Tree t;

	private boolean actionsActive = false;
	private Button actionHandlerSwitch =
		new Button("Activate actions", this, "toggleActions");

	public FeatureTree() {
		super();
	}

	public void toggleActions() {
		if (actionsActive) {
			t.removeActionHandler(this);
			actionsActive = false;
			actionHandlerSwitch.setCaption("Activate Actions");
		} else {
			t.addActionHandler(this);
			actionsActive = true;
			actionHandlerSwitch.setCaption("Deactivate Actions");
		}
	}

	public void expandAll() {
		for (Iterator i = t.rootItemIds().iterator();i.hasNext();) {
			t.expandItemsRecursively(i.next());
		}
	}
	
	public void collapseAll() {
		for (Iterator i = t.rootItemIds().iterator();i.hasNext();) {		
			t.collapseItemsRecursively(i.next());
		}		
	}
	
	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		// Create names
		Panel show = new Panel("Tree component");
		String[] names = new String[100];
		for (int i = 0; i < names.length; i++)
			names[i] =
				firstnames[(int) (Math.random() * (firstnames.length - 1))]
					+ " "
					+ lastnames[(int) (Math.random() * (lastnames.length - 1))];

		// Create tree
		t = new Tree("Family Tree");
		for (int i = 0; i < 100; i++) {
			t.addItem(names[i]);
			String parent = names[(int) (Math.random() * (names.length - 1))];
			if (t.containsId(parent))
				t.setParent(names[i], parent);
		}

		// Forbid childless people to have children (makes them leaves)
		for (int i = 0; i < 100; i++)
			if (!t.hasChildren(names[i]))
				t.setChildrenAllowed(names[i], false);

		show.addComponent(t);
		l.addComponent(show);

		// Actions
		l.addComponent(this.actionHandlerSwitch);
		
		// Expand and Collapse buttons 
		l.addComponent(new Button("Expand All",this,"expandAll"));
		l.addComponent(new Button("Collapse All",this,"collapseAll"));

		// Properties
		PropertyPanel p = new PropertyPanel(t);
		Form ap = p.createBeanPropertySet(new String[] { "selectable" });
		Select themes = (Select) p.getField("style");
		themes
			.addItem("menu")
			.getItemProperty(themes.getItemCaptionPropertyId())
			.setValue("menu");
		p.addProperties("Tree Properties", ap);
		l.addComponent(p);

		return l;
	}

	protected String getExampleSrc() {
		return "// Create tree\n"
			+ "t = new Tree(\"Family Tree\");\n"
			+ "for (int i = 0; i < 100; i++) {\n"
			+ "	t.addItem(names[i]);\n"
			+ "	String parent = names[(int) (Math.random() * (names.length - 1))];\n"
			+ "	if (t.containsId(parent)) \n"
			+ "		t.setParent(names[i],parent);\n"
			+ "}\n\n"
			+ "// Forbid childless people to have children (makes them leaves)\n"
			+ "for (int i = 0; i < 100; i++)\n"
			+ "	if (!t.hasChildren(names[i]))\n"
			+ "		t.setChildrenAllowed(names[i], false);\n";
	}

	protected String getDescriptionXHTML() {
		return "<p>A tree is a natural way to represent datasets that have"
			+ " hierarchical relationships, such as filesystems, message "
			+ "threads or... family trees. IT Mill Toolkit features a versatile "
			+ "and powerful Tree component that works much like the tree components "
			+ "of most modern operating systems. </p>"
			+ "<p>The most prominent use of the Tree component is to "
			+ "use it for displaying a hierachical menu, like the "
			+ "menu on the left side of the screen for instance "
			+ "or to display filesystems or other hierarchical datasets.</p>"
			+ "<p>The tree component uses <code>Container</code> "
			+ "datasources much like the Table component, "
			+ "with the addition that it also utilizes the hierarchy "
			+ "information maintained by the container. </p><p>On "
			+ "the demo tab you can try out how the different properties "
			+ "affect the presentation of the tree component.</p>";
	}

	protected String getImage() {
		return "tree.jpg";
	}

	protected String getTitle() {
		return "Tree";
	}

	private Action ACTION1 = new Action("Action 1");
	private Action ACTION2 = new Action("Action 2");
	private Action ACTION3 = new Action("Action 3");

	private Action[] actions = new Action[] { ACTION1, ACTION2, ACTION3 };

	public Action[] getActions(Object target, Object sender) {
		return actions;
	}

	public void handleAction(Action action, Object sender, Object target) {
		t.setDescription(
			"Last action clicked was '"
				+ action.getCaption()
				+ "' on item '"
				+ target
				+ "'");
	}
}

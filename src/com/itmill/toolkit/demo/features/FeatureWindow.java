/* *************************************************************************
 
 IT Mill Toolkit 

 Development of Browser User Interfaces Made Easy

 Copyright (C) 2000-2006 IT Mill Ltd
 
 *************************************************************************

 This product is distributed under commercial license that can be found
 from the product package on license.pdf. Use of this product might 
 require purchasing a commercial license from IT Mill Ltd. For guidelines 
 on usage, see licensing-guidelines.html

 *************************************************************************
 
 For more information, contact:
 
 IT Mill Ltd                           phone: +358 2 4802 7180
 Ruukinkatu 2-4                        fax:   +358 2 4802 7181
 20540, Turku                          email:  info@itmill.com
 Finland                               company www: www.itmill.com
 
 Primary source for information and releases: www.itmill.com

 ********************************************************************** */

package com.itmill.toolkit.demo.features;

import com.itmill.toolkit.ui.*;

public class FeatureWindow extends Feature {
	Button addButton = new Button("Add to application", this, "addWin");

	Button removeButton = new Button("Remove from application", this, "delWin");

	Window demoWindow;

	Form windowProperties;

	public FeatureWindow() {
		super();
	}

	protected Component getDemoComponent() {

		OrderedLayout layoutRoot = new OrderedLayout();
		OrderedLayout layoutUpper = new OrderedLayout();
		OrderedLayout layoutLower = new OrderedLayout();
		demoWindow = new Window("Feature Test Window");

		layoutUpper.addComponent(addButton);
		layoutUpper.addComponent(removeButton);

		layoutLower.addComponent(new Label(
				"Note: depending on your browser, you may have to "
						+ "allow popups from this web site in order"
						+ " to get this demo to work."));
		updateWinStatus();

		// Propertiesc
		propertyPanel = new PropertyPanel(demoWindow);
		propertyPanel.dependsOn(addButton);
		propertyPanel.dependsOn(removeButton);
		windowProperties = propertyPanel.createBeanPropertySet(new String[] {
				"width", "height", "name", "border", "theme", "scrollable",
				"scrollOffsetX", "scrollOffsetY" });
		windowProperties.replaceWithSelect("border", new Object[] {
				new Integer(Window.BORDER_DEFAULT),
				new Integer(Window.BORDER_NONE),
				new Integer(Window.BORDER_MINIMAL) }, new Object[] { "Default",
				"None", "Minimal" });
		propertyPanel.addProperties("Window Properties", windowProperties);

		setJavadocURL("ui/Window.html");

		layoutRoot.addComponent(layoutUpper);
		layoutRoot.addComponent(layoutLower);
		return layoutRoot;
	}

	protected String getExampleSrc() {
		return "Window win = new Window();\n"
				+ "getApplication().addWindow(win);\n";

	}

	protected String getDescriptionXHTML() {
		return "The window support in IT Mill Toolkit allows for opening and closing windows, "
				+ "refreshing one window from another (for asynchronous terminals), "
				+ "resizing windows and scrolling window content. "
				+ "There are also a number of preset window border styles defined by "
				+ "this feature.";
	}

	protected String getImage() {
		return "window.jpg";
	}

	protected String getTitle() {
		return "Window";
	}

	public void addWin() {
		getApplication().addWindow(demoWindow);

		demoWindow.removeAllComponents();
		demoWindow.setWidth(500);
		demoWindow.setHeight(200);

		// Panel panel = new Panel("New window");
		// panel.addComponent(new Label(
		// "This is a new window created by selecting <em>Add to "
		// + "application</em>.<br /><br />You can close"
		// + " this window by selecting <em>Remove from"
		// + " application</em> from the Feature Browser window.",
		// Label.CONTENT_XHTML));
		// demoWindow.addComponent(panel);

		demoWindow
				.addComponent(new Label(
						"<br /><br />This is a new window created by <em>Add to "
								+ "application</em> button's event.<br /><br />You may simply"
								+ " close this window or select <em>Remove from"
								+ " application</em> from the Feature Browser window.",
						Label.CONTENT_XHTML));

		windowProperties.getField("name").setReadOnly(true);
		updateWinStatus();
	}

	public void delWin() {
		getApplication().removeWindow(demoWindow);
		windowProperties.getField("name").setReadOnly(false);
		updateWinStatus();
	}

	private void updateWinStatus() {
		if (demoWindow.getApplication() == null) {
			addButton.setEnabled(true);
			removeButton.setEnabled(false);
		} else {
			addButton.setEnabled(false);
			removeButton.setEnabled(true);
		}
	}
}

/* *************************************************************************
 
   								Millstone(TM) 
   				   Open Sourced User Interface Library for
   		 		       Internet Development with Java

             Millstone is a registered trademark of IT Mill Ltd
                  Copyright (C) 2000-2005 IT Mill Ltd
                     
   *************************************************************************

   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   license version 2.1 as published by the Free Software Foundation.

   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

   *************************************************************************
   
   For more information, contact:
   
   IT Mill Ltd                           phone: +358 2 4802 7180
   Ruukinkatu 2-4                        fax:  +358 2 4802 7181
   20540, Turku                          email: info@itmill.com
   Finland                               company www: www.itmill.com
   
   Primary source for MillStone information and releases: www.millstone.org

   ********************************************************************** */

package com.enably.tk.demo.features;

import com.enably.tk.ui.*;

public class FeatureWindow extends Feature {
	Button addButton = new Button("Add to application", this, "addWin");
	Button removeButton = new Button("Remove from application", this, "delWin");
	Window demoWindow;
	Form windowProperties;

	public FeatureWindow() {
		super();
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();
		demoWindow = new Window("Feature Test Window");

		// Example panel
		Panel show = new Panel("Test Window Control");
		((OrderedLayout) show.getLayout()).setOrientation(
			OrderedLayout.ORIENTATION_HORIZONTAL);
		show.addComponent(addButton);
		show.addComponent(removeButton);
		updateWinStatus();
		l.addComponent(show);

		// Properties
		PropertyPanel p = new PropertyPanel(demoWindow);
		p.dependsOn(addButton);
		p.dependsOn(removeButton);
		windowProperties =
			p.createBeanPropertySet(
				new String[] {
					"width",
					"height",
					"name",
					"border",
					"theme",
					"scrollable",
					"scrollOffsetX",
					"scrollOffsetY" });
		windowProperties.replaceWithSelect(
			"border",
			new Object[] {
				new Integer(Window.BORDER_DEFAULT),
				new Integer(Window.BORDER_NONE),
				new Integer(Window.BORDER_MINIMAL)},
			new Object[] { "Default", "None", "Minimal" });
		p.addProperties("Window Properties", windowProperties);
		l.addComponent(p);

		return l;
	}

	protected String getExampleSrc() {
		return "Window win = new Window();\n"
			+ "getApplication().addWindow(win);\n";

	}

	protected String getDescriptionXHTML() {
		return "The window support of Millstone allows for opening and closing windows, "
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

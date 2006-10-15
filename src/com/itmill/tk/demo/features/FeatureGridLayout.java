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

package com.itmill.tk.demo.features;

import java.util.Date;

import com.itmill.tk.ui.*;

public class FeatureGridLayout extends Feature {

	public FeatureGridLayout() {
		super();
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		// Example panel
		Panel show = new Panel("GridLayout component");
		GridLayout gl = new GridLayout(3, 3);
		DateField cal = new DateField("Test component 1",new Date());
		cal.setStyle("calendar");
		gl.addComponent(cal, 1,0,2,1);
		for (int i = 2; i < 7; i++)
			gl.addComponent(new TextField("Test component " + i));
		show.addComponent(gl);
		l.addComponent(show);

		// Properties
		PropertyPanel p = new PropertyPanel(gl);
		Form ap = p.createBeanPropertySet(new String[] { "width", "height" });
		ap.addField("new line", new Button("New Line", gl, "newLine"));
		ap.addField("space", new Button("Space", gl, "space"));
		p.addProperties("GridLayout Features", ap);
		p.getField("height").dependsOn(p.getField("add component"));
		l.addComponent(p);

		return l;
	}

	protected String getExampleSrc() {
		return "GridLayout gl = new GridLayout(2,2);\n"
			+ "gl.addComponent(new Label(\"Label 1 in GridLayout\"));\n"
			+ "gl.addComponent(new Label(\"Label 2 in GridLayout\"));\n"
			+ "gl.addComponent(new Label(\"Label 3 in GridLayout\"));\n"
			+ "gl.addComponent(new Label(\"Label 4 in GridLayout\"));\n";
	}
	/**
	 * @see com.itmill.tk.demo.features.Feature#getDescriptionXHTML()
	 */
	protected String getDescriptionXHTML() {
		return "<p>This feature provides a container that lays out components "
			+ "into a grid of given width and height.</p>"
			+ "<p>On the demo tab you can try out how the different "
			+ "properties affect the presentation of the component.</p>";
	}

	protected String getImage() {
		return "gridlayout.jpg";
	}

	protected String getTitle() {
		return "GridLayout";
	}
}
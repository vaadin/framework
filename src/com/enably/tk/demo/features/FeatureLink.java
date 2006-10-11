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

import com.enably.tk.terminal.ExternalResource;
import com.enably.tk.ui.*;

public class FeatureLink extends Feature {

	public FeatureLink() {
		super();
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		// Example panel
		Panel show = new Panel("Link component");
		Link lnk =
			new Link(
				"Link caption",
				new ExternalResource("http://www.itmill.com"));
		show.addComponent(lnk);
		l.addComponent(show);

		// Properties
		PropertyPanel p = new PropertyPanel(lnk);
		Form ap =
			p.createBeanPropertySet(
				new String[] {
					"targetName",
					"targetWidth",
					"targetHeight",
					"targetBorder" });
		ap.replaceWithSelect(
			"targetBorder",
			new Object[] {
				new Integer(Link.TARGET_BORDER_DEFAULT),
				new Integer(Link.TARGET_BORDER_MINIMAL),
				new Integer(Link.TARGET_BORDER_NONE)},
			new Object[] { "Default", "Minimal", "None" });
		p.addProperties("Link Properties", ap);
		l.addComponent(p);

		return l;
	}

	protected String getExampleSrc() {
		return "Link lnk = new Link(\"Link caption\",new ExternalResource(\"http://www.itmill.com\"));\n";
	}

	protected String getDescriptionXHTML() {
		return "The link feature allows for making refences to both internal and external resources. "
			+ "The link can open the new resource in a new window, allowing for control of the newly "
			+ "opened windows attributes, such as size and border. "
			+ "<br /><br />"
			+ " For example you can create an application pop-up or create link to external resources.";

	}

	protected String getImage() {
		return "link.jpg";
	}

	protected String getTitle() {
		return "Link";
	}
}

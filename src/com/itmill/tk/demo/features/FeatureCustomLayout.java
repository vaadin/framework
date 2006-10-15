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

import java.net.MalformedURLException;
import java.net.URL;

import com.itmill.tk.terminal.ExternalResource;
import com.itmill.tk.ui.*;

public class FeatureCustomLayout extends Feature {

	protected String getDescriptionXHTML() {
		return "<p>A container component with freely designed layout and style. The "
			+ "container consists of items with textually represented locations. Each "
			+ "item contains one sub-component. The adapter and theme are resposible "
			+ "for rendering the layout with given style by placing the items on the "
			+ "screen in defined locations.</p>"
			+ "<p>The definition of locations is not fixed - the each style can define its "
			+ "locations in a way that is suitable for it. One typical example would be "
			+ "to create visual design for a website as a custom layout: the visual design "
			+ "could define locations for \"menu\", \"body\" and \"title\" for example. "
			+ "The layout would then be implemented as XLS-template with for given style.</p>"
			+ "<p>The default theme handles the styles that are not defined by just drawing "
			+ "the subcomponents with flowlayout.</p>";
	}

	protected String getExampleSrc() {
		return "CustomLayout c = new CustomLayout(\"style-name\");\n"
			+ "c.addComponent(new Label(\"foo\"),\"foo-location\");\n"
			+ "c.addComponent(new Label(\"bar\"),\"bar-location\");\n";
	}

	protected String getImage() {
		return "customlayout.jpg";
	}

	protected String getTitle() {
		return "CustomLayout";
	}

	protected Component getDemoComponent() {
		OrderedLayout l = new OrderedLayout();

		l.addComponent(
			new Label(
				"<p>For demonstration, see GO-Game example application. All of the "+
				"layouting done in the aplication is handled by CustomLayout with \"goroom\"-style "+
				"that is defined in \"gogame\"-theme. The theme is simply created by exteding "+
				"default theme trough theme-inheritance and adding couple of xsl-templates</p>",
				Label.CONTENT_UIDL));

		URL goUrl = null;
		try {
			goUrl = new URL(getApplication().getURL(), "../go/");
		} catch (MalformedURLException e) {
		}

		if (goUrl != null) {
			Link link = new Link("Start GO-Game", new ExternalResource(goUrl));
			link.setTargetName("gogame");
			link.setTargetBorder(Link.TARGET_BORDER_NONE);
			l.addComponent(link);
		}

		return l;
	}

}

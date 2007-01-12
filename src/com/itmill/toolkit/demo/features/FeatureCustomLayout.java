/* *************************************************************************
 
                               IT Mill Toolkit 

               Development of Browser User Interfaces Made Easy

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

import java.net.MalformedURLException;
import java.net.URL;

import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.ui.*;

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

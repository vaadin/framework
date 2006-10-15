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

import com.itmill.tk.terminal.ClassResource;
import com.itmill.tk.terminal.Sizeable;
import com.itmill.tk.ui.*;

public class FeatureEmbedded extends Feature {

	public FeatureEmbedded() {
		super();
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		// Example panel
		Panel show = new Panel("Embedded component");
		Embedded emb = new Embedded("Embedded Caption");
		emb.setClassId("clsid:F08DF954-8592-11D1-B16A-00C0F0283628");
		emb.setWidth(100);
		emb.setHeight(50);
		emb.setParameter("BorderStyle", "1");
		emb.setParameter("MousePointer", "1");
		emb.setParameter("Enabled", "1");
		emb.setParameter("Min", "1");
		emb.setParameter("Max", "10");
		show.addComponent(emb);
		l.addComponent(show);

		// Properties
		PropertyPanel p = new PropertyPanel(emb);
		Form ap =
			p.createBeanPropertySet(
				new String[] {
					"type",
					"source",
					"width",
					"height",
					"widthUnits",
					"heightUnits",
					"codebase",
					"codetype",
					"archive",
					"mimeType",
					"standby",
					"classId" });
		ap.replaceWithSelect(
			"type",
			new Object[] {
				new Integer(Embedded.TYPE_IMAGE),
				new Integer(Embedded.TYPE_OBJECT)},
			new Object[] { "Image", "Object" });
		Object[] units = new Object[Sizeable.UNIT_SYMBOLS.length];
		Object[] symbols = new Object[Sizeable.UNIT_SYMBOLS.length];
		for (int i = 0; i < units.length; i++) {
			units[i] = new Integer(i);
			symbols[i] = Sizeable.UNIT_SYMBOLS[i];
		}
		ap.replaceWithSelect("heightUnits", units, symbols);
		ap.replaceWithSelect("widthUnits", units, symbols);
		ap.replaceWithSelect(
			"source",
			new Object[] {
				null,
				new ClassResource("millstone-logo.gif", getApplication())},
			new Object[] { "null", "Millstone logo" });
		p.addProperties("Embedded Properties", ap);
		p.getField("standby").setDescription(
			"The text to display while loading the object.");
		p.getField("codebase").setDescription(
			"root-path used to access resources with relative paths.");
		p.getField("codetype").setDescription(
			"MIME-type of the code.");
		p.getField("classId").setDescription(
			"Unique object id. This can be used for example to identify windows components.");
		l.addComponent(p);

		return l;
	}

	protected String getExampleSrc() {
		return "// Load image from jpg-file, that is in the same package with the application\n"
			+ "Embedded e = new Embedded(\"Image title\",\n"
			+ "   new ClassResource(\"image.jpg\", getApplication()));";
	}

	protected String getDescriptionXHTML() {
		return "<p>The embedding feature allows for adding images, multimedia and other non-specified "
			+ "content to your application. "
			+ "The feature has provisions for embedding both applets and Active X controls. "
			+ "Actual support for embedded media types is left to the terminal.</p>";
	}

	protected String getImage() {
		return "embedded.jpg";
	}

	protected String getTitle() {
		return "Embedded";
	}

}

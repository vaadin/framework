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

import com.itmill.toolkit.terminal.ClassResource;
import com.itmill.toolkit.terminal.Sizeable;
import com.itmill.toolkit.ui.*;

public class FeatureEmbedded extends Feature {

	public FeatureEmbedded() {
		super();
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		ClassResource flashResource = new ClassResource("itmill_spin.swf", this
				.getApplication());
		Embedded emb = new Embedded("Embedded Caption", flashResource);
		emb.setType(Embedded.TYPE_OBJECT);
		emb.setMimeType("application/x-shockwave-flash");
		emb.setWidth(250);
		emb.setHeight(100);
		l.addComponent(emb);

		// Properties
		propertyPanel = null;
		if (false) {
			propertyPanel = new PropertyPanel(emb);
			Form ap = propertyPanel.createBeanPropertySet(new String[] {
					"type", "source", "width", "height", "widthUnits",
					"heightUnits", "codebase", "codetype", "archive",
					"mimeType", "standby", "classId" });
			ap.replaceWithSelect("type", new Object[] {
					new Integer(Embedded.TYPE_IMAGE),
					new Integer(Embedded.TYPE_OBJECT) }, new Object[] {
					"Image", "Object" });
			Object[] units = new Object[Sizeable.UNIT_SYMBOLS.length];
			Object[] symbols = new Object[Sizeable.UNIT_SYMBOLS.length];
			for (int i = 0; i < units.length; i++) {
				units[i] = new Integer(i);
				symbols[i] = Sizeable.UNIT_SYMBOLS[i];
			}
			ap.replaceWithSelect("heightUnits", units, symbols);
			ap.replaceWithSelect("widthUnits", units, symbols);
			ap.replaceWithSelect("source", new Object[] { null,
					new ClassResource("m-bullet-blue.gif", getApplication()) },
					new Object[] { "null", "IT Mill (m)" });
			propertyPanel.addProperties("Embedded Properties", ap);
			propertyPanel.getField("standby").setDescription(
					"The text to display while loading the object.");
			propertyPanel.getField("codebase").setDescription(
					"root-path used to access resources with relative paths.");
			propertyPanel.getField("codetype").setDescription(
					"MIME-type of the code.");
			propertyPanel
					.getField("classId")
					.setDescription(
							"Unique object id. This can be used for example to identify windows components.");
		}
		setJavadocURL("ui/Embedded.html");

		return l;
	}

	protected String getExampleSrc() {
		return "// Load image from jpg-file, that is in the same package with the application\n"
				+ "Embedded e = new Embedded(\"Image title\",\n"
				+ "   new ClassResource(\"image.jpg\", getApplication()));";
	}

	protected String getDescriptionXHTML() {
		return "The embedding feature allows for adding images, multimedia and other non-specified "
				+ "content to your application. "
				+ "The feature has provisions for embedding both applets and Active X controls. "
				+ "Actual support for embedded media types is left to the terminal.";
	}

	protected String getImage() {
		return "embedded.jpg";
	}

	protected String getTitle() {
		return "Embedded";
	}

}

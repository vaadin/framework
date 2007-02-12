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

import com.itmill.toolkit.terminal.web.ApplicationServlet;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.Form;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.Select;

public class IntroWelcome extends Feature {

	private static final String WELCOME_TEXT_UPPER = ""
			+ "This application lets you view and play with some features of "
			+ "IT Mill Toolkit. Use menu on the left to select component."
			+ "<br /><br />Note the <b>Properties selection</b> on the top "
			+ "right corner. Click it open to access component properties and"
			+ " feel free to edit properties at any time."
			+ "<br /><br />The area that you are now reading is the component"
			+ " demo area. Lower area from here contains component description, API"
			+ " documentation and optional code sample. Note that not all selections"
			+ " contain demo, only description and API documentation is shown."
			+ "<br /><br />You may also change application's theme from below the menu."
			+ " This example application is designed to work best with"
			+ " <em>Demo</em> theme, other themes are for demonstration purposes only."
			+ "<br /><br />IT Mill Toolkit enables you to construct complex Web"
			+ " applications using plain Java, no knowledge of other Web technologies"
			+ " such as XML, HTML, DOM, JavaScript or browser differences is required."
			+ "<br /><br />For more information, point your browser to"
			+ " <a href=\"http://www.itmill.com\" target=\"_new\">www.itmill.com</a>.";

	private static final String WELCOME_TEXT_LOWER = ""
			+ "This area contains the selected component's description, API documentation"
			+ " and optional code sample."
			+ "<br /><br />To see how simple it is to create IT Mill Toolkit application,"
			+ " click <em>Code Sample</em> tab."
			+ "<br /><br />Start your tour now by selecting features from the list"
			+ " on the left and remember to experiment with the <b>Properties panel</b>"
			+ " located at the top right corner area.";

	public IntroWelcome() {
		super();
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		Panel panel = new Panel();
		panel.setCaption("Welcome to the IT Mill Toolkit feature tour!");
		l.addComponent(panel);

		Label label = new Label();
		panel.addComponent(label);

		label.setContentMode(Label.CONTENT_XHTML);
		label.setValue(WELCOME_TEXT_UPPER);

		// Propertiesversion.setValue("IT Mill Toolkit version:
		// "+ApplicationServlet.VERSION);
		propertyPanel = new PropertyPanel(panel);
		Form ap = propertyPanel.createBeanPropertySet(new String[] { "width",
				"height" });
		Select themes = (Select) propertyPanel.getField("style");
		themes.addItem("light").getItemProperty(
				themes.getItemCaptionPropertyId()).setValue("light");
		themes.addItem("strong").getItemProperty(
				themes.getItemCaptionPropertyId()).setValue("strong");
		propertyPanel.addProperties("Panel Properties", ap);

		setJavadocURL("package-summary.html");

		setPropsReminder(false);

		return l;
	}

	protected String getExampleSrc() {
		return ""
				+ "package com.itmill.toolkit.demo;\n"
				+ "import com.itmill.toolkit.ui.*;\n\n"
				+ "public class HelloWorld extends com.itmill.toolkit.Application {\n"
				+ "    public void init() {\n"
				+ "        Window main = new Window(\"Hello window\");\n"
				+ "        setMainWindow(main);\n"
				+ "        main.addComponent(new Label(\"Hello World!\"));\n"
				+ "    }\n" + "}\n";
	}

	/**
	 * @see com.itmill.toolkit.demo.features.Feature#getDescriptionXHTML()
	 */
	protected String getDescriptionXHTML() {
		return WELCOME_TEXT_LOWER + "<br /><br />IT Mill Toolkit version: "
				+ ApplicationServlet.VERSION;
	}

	protected String getImage() {
		return "icon_intro.png";
	}

	protected String getTitle() {
		return "Welcome";
	}

}

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

import java.util.Iterator;
import java.util.StringTokenizer;

import com.itmill.tk.data.*;
import com.itmill.tk.terminal.ClassResource;
import com.itmill.tk.ui.*;

public class FeatureBrowser
	extends CustomComponent
	implements Property.ValueChangeListener {

	private Tree features;
	private Feature currentFeature = null;
	private GridLayout layout;
	private Component welcome;
	private boolean initialized = false;

	private static final String WELCOME_TEXT =
		"<h3>Welcome to the Millstone feature tour!</h3>"
			+ "In this Millstone application you may view a demonstration of some of its "
			+ "features.<br/>"
			+ "Most of the features can be tested online and include simple example of their "
			+ "usage associated with it.<br/><br/>"
			+ "Start your tour by selecting features from the list on the left.<br/><br/>"
			+ "For more information, point your browser to: <a href=\"http://www.millstone.org\""
			+ " target=\"_new\">www.millstone.org</a>";

	public void attach() {

		if (initialized)
			return;
		initialized = true;

		// Configure tree
		features = new Tree();
		features.setStyle("menu");
		features.addContainerProperty("name", String.class, "");
		features.addContainerProperty("feature", Feature.class, null);
		features.setItemCaptionPropertyId("name");
		features.addListener(this);
		features.setImmediate(true);

		// Configure component layout
		layout = new GridLayout(2, 1);
		setCompositionRoot(layout);
		OrderedLayout left = new OrderedLayout();
		left.addComponent(features);
		Button close = new Button("restart", getApplication(), "close");
		left.addComponent(close);
		close.setStyle("link");
		layout.addComponent(left, 0, 0, 0, 0);
		Label greeting = new Label(WELCOME_TEXT, Label.CONTENT_XHTML);
		//welcomePanel = new Panel((String) null);
		welcome =
			new Embedded(
				"",
				new ClassResource(
					getClass(),
					"millstone-logo.gif",
					getApplication()));
		//	welcomePanel.addComponent(greeting);
		layout.addComponent(welcome, 1, 0, 1, 0);

		// Test component
		registerFeature(
			"/UI Components",
			new UIComponents());
		registerFeature(
			"/UI Components/Basic/Text Field",
			new FeatureTextField());
		registerFeature(
			"/UI Components/Basic/Date Field",
			new FeatureDateField());
		registerFeature("/UI Components/Basic/Button", new FeatureButton());
		registerFeature("/UI Components/Basic/Form", new FeatureForm());
		registerFeature("/UI Components/Basic/Label", new FeatureLabel());
		registerFeature("/UI Components/Basic/Link", new FeatureLink());
		registerFeature(
			"/UI Components/Item Containers/Select",
			new FeatureSelect());
		registerFeature(
			"/UI Components/Item Containers/Table",
			new FeatureTable());
		registerFeature(
			"/UI Components/Item Containers/Tree",
			new FeatureTree());
		registerFeature(
			"/UI Components/Layouts/Ordered Layout",
			new FeatureOrderedLayout());
		registerFeature(
			"/UI Components/Layouts/Grid Layout",
			new FeatureGridLayout());
		registerFeature(
			"/UI Components/Layouts/Custom Layout",
			new FeatureCustomLayout());
		registerFeature("/UI Components/Layouts/Panel", new FeaturePanel());
		registerFeature(
			"/UI Components/Layouts/Tab Sheet",
			new FeatureTabSheet());
		registerFeature("/UI Components/Layouts/Window", new FeatureWindow());
		registerFeature(
			"/UI Components/Layouts/Frame Window",
			new FeatureFrameWindow());
		registerFeature(
			"/UI Components/Data handling/Embedded Objects",
			new FeatureEmbedded());
		registerFeature(
			"/UI Components/Data handling/Upload",
			new FeatureUpload());
		registerFeature("/Data Model/Properties", new FeatureProperties());
		registerFeature("/Data Model/Items", new FeatureItems());
		registerFeature("/Data Model/Containers", new FeatureContainers());
		registerFeature("/Data Model/Validators", new FeatureValidators());
		registerFeature("/Data Model/Buffering", new FeatureBuffering());
		registerFeature(
			"/Terminal/Server Initiated Events",
			new FeatureServerEvents());
		registerFeature(
			"/Terminal/Parameters and URI Handling",
			new FeatureParameters());

		// Pre-open all menus
		for (Iterator i=features.getItemIds().iterator(); i.hasNext();) 
			features.expandItem(i.next());
	}

	public void registerFeature(String path, Feature feature) {
		StringTokenizer st = new StringTokenizer(path, "/");
		String id = "";
		String parentId = null;
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			id += "/" + token;
			if (!features.containsId(id)) {
				features.addItem(id);
				features.setChildrenAllowed(id, false);
			}
			features.getContainerProperty(id, "name").setValue(token);
			if (parentId != null) {
				features.setChildrenAllowed(parentId, true);
				features.setParent(id, parentId);
			}
			if (!st.hasMoreTokens())
				features.getContainerProperty(id, "feature").setValue(feature);
			parentId = id;
		}
	}

	public void valueChange(Property.ValueChangeEvent event) {

		// Change feature
		if (event.getProperty() == features) {
			Object id = features.getValue();
			if (id != null) {
				if (features.areChildrenAllowed(id))
					features.expandItem(id);
				Property p = features.getContainerProperty(id, "feature");
				Feature feature = p != null ? ((Feature) p.getValue()) : null;
				if (feature != null) {
					if (currentFeature != null)
						layout.removeComponent(currentFeature);
					currentFeature = feature;
					layout.removeComponent(1, 0);
					layout.addComponent(currentFeature, 1, 0);
					getWindow().setCaption(
						"Millstone Features / "
							+ features.getContainerProperty(id, "name"));
				}
			}
		}
	}
}

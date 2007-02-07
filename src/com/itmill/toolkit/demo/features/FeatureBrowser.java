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

import java.util.Iterator;
import java.util.StringTokenizer;

import com.itmill.toolkit.data.*;
import com.itmill.toolkit.ui.*;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Button.ClickListener;

public class FeatureBrowser extends CustomComponent implements
		Property.ValueChangeListener, ClickListener {

	private Tree features;

	private Feature currentFeature = null;

	private OrderedLayout layout;

	private Button propertiesSelect;

	private OrderedLayout right;

	private PropertyPanel properties;

	private boolean initialized = false;

	private Select themeSelector = new Select();

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
		layout = new OrderedLayout(OrderedLayout.ORIENTATION_HORIZONTAL);
		layout.setStyle("featurebrowser-mainlayout");
		setCompositionRoot(layout);
		OrderedLayout left = new OrderedLayout(
				OrderedLayout.ORIENTATION_VERTICAL);
		left.addComponent(features);
		layout.addComponent(left);

		// Welcome temporarily disabled
		// Label greeting = new Label(WELCOME_TEXT, Label.CONTENT_XHTML);
		// OrderedLayout welcomePanel = new OrderedLayout();
		// welcome =
		// new Embedded(
		// "",
		// new ClassResource(
		// getClass(),
		// "itmill.gif",
		// getApplication()));
		// welcomePanel.addComponent(welcome);
		// welcomePanel.addComponent(greeting);
		// layout.addComponent(welcomePanel);

		// Theme selector
		left.addComponent(themeSelector);
		themeSelector.addItem("demo");
		themeSelector.addItem("corporate");
		themeSelector.addItem("base");
		themeSelector.addListener(this);
		themeSelector.select("demo");
		themeSelector.setImmediate(true);

		// Restart button
		Button close = new Button("restart", getApplication(), "close");
		close.setStyle("link");
		left.addComponent(close);

		// Test component
		registerFeature("/Welcome", new IntroWelcome());
		registerFeature("/UI Components", new IntroComponents());
		registerFeature("/UI Components/Basic", new IntroBasic());
		registerFeature("/UI Components/Basic/Text Field",
				new FeatureTextField());
		registerFeature("/UI Components/Basic/Date Field",
				new FeatureDateField());
		registerFeature("/UI Components/Basic/Button", new FeatureButton());
		registerFeature("/UI Components/Basic/Form", new FeatureForm());
		registerFeature("/UI Components/Basic/Label", new FeatureLabel());
		registerFeature("/UI Components/Basic/Link", new FeatureLink());
		registerFeature("/UI Components/Item Containers",
				new IntroItemContainers());
		registerFeature("/UI Components/Item Containers/Select",
				new FeatureSelect());
		registerFeature("/UI Components/Item Containers/Table",
				new FeatureTable());
		registerFeature("/UI Components/Item Containers/Tree",
				new FeatureTree());
		registerFeature("/UI Components/Layouts", new IntroLayouts());
		registerFeature("/UI Components/Layouts/Ordered Layout",
				new FeatureOrderedLayout());
		registerFeature("/UI Components/Layouts/Grid Layout",
				new FeatureGridLayout());
		registerFeature("/UI Components/Layouts/Custom Layout",
				new FeatureCustomLayout());
		registerFeature("/UI Components/Layouts/Panel", new FeaturePanel());
		registerFeature("/UI Components/Layouts/Tab Sheet",
				new FeatureTabSheet());
		registerFeature("/UI Components/Layouts/Window", new FeatureWindow());
		// Disabled for now
		// registerFeature("/UI Components/Layouts/Frame Window",
		// new FeatureFrameWindow());
		registerFeature("/UI Components/Data handling", new IntroDataHandling());
		registerFeature("/UI Components/Data handling/Embedded Objects",
				new FeatureEmbedded());
		registerFeature("/UI Components/Data handling/Upload",
				new FeatureUpload());
		registerFeature("/Data Model", new IntroDataModel());
		registerFeature("/Data Model/Properties", new FeatureProperties());
		registerFeature("/Data Model/Items", new FeatureItems());
		registerFeature("/Data Model/Containers", new FeatureContainers());
		registerFeature("/Data Model/Validators", new FeatureValidators());
		registerFeature("/Data Model/Buffering", new FeatureBuffering());
		registerFeature("/Terminal", new IntroTerminal());
		registerFeature("/Terminal/Parameters and URI Handling",
				new FeatureParameters());

		// Pre-open all menus
		for (Iterator i = features.getItemIds().iterator(); i.hasNext();)
			features.expandItem(i.next());

		// Add demo component and tabs
		currentFeature = new FeatureTable();
		layout.addComponent(currentFeature);

		// Add properties
		right = new OrderedLayout(OrderedLayout.ORIENTATION_VERTICAL);
		layout.addComponent(right);

		propertiesSelect = new Button("Show properties", this);
		propertiesSelect.setSwitchMode(true);
		right.addComponent(propertiesSelect);
		properties = currentFeature.getPropertyPanel();
		properties.setVisible(false);
		right.addComponent(properties);
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
					layout.replaceComponent(currentFeature, feature);
					currentFeature = feature;
					properties = feature.getPropertyPanel();
					if (properties != null) {
						Iterator i = right.getComponentIterator();
						i.next();
						PropertyPanel oldProps = (PropertyPanel) i.next();
						if (oldProps != null)
							right.replaceComponent(oldProps, properties);
						else
							right.addComponent(properties);
						properties.setVisible(((Boolean) propertiesSelect
								.getValue()).booleanValue());
					}
					getWindow()
							.setCaption(
									"IT Mill Toolkit Features / "
											+ features.getContainerProperty(id,
													"name"));
				}
			}
		} else if (event.getProperty() == themeSelector) {
			getApplication().setTheme(themeSelector.toString());
		}
	}

	public void buttonClick(ClickEvent event) {
		if (properties != null)
			properties.setVisible(((Boolean) propertiesSelect.getValue())
					.booleanValue());
	}
}

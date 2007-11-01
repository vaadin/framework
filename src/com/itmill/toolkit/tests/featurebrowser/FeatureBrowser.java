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

package com.itmill.toolkit.tests.featurebrowser;

import java.util.Iterator;
import java.util.StringTokenizer;

import com.itmill.toolkit.data.*;
import com.itmill.toolkit.ui.*;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Button.ClickListener;

public class FeatureBrowser extends CustomComponent implements
		Property.ValueChangeListener, ClickListener, Layout {

	private Tree features;

	private Feature currentFeature = null;

	private CustomLayout mainlayout;

	private PropertyPanel properties;

	private boolean initialized = false;

	private Select themeSelector = new Select();

	public void attach() {

		if (initialized)
			return;
		initialized = true;

		// Configure tree
		features = new Tree();
		features.addContainerProperty("name", String.class, "");
		features.addContainerProperty("feature", Feature.class, null);
		features.setItemCaptionPropertyId("name");
		features.addListener(this);
		features.setImmediate(true);
		features.setStyle("menu");

		// Configure component layout
		mainlayout = new CustomLayout("featurebrowser-mainlayout");
		setCompositionRoot(mainlayout);
		mainlayout.addComponent(features, "tree");

		// Theme selector
		mainlayout.addComponent(themeSelector, "themes");
		themeSelector.addItem("tests-featurebrowser");

		themeSelector.addListener(this);
		themeSelector.select("tests-featurebrowser");
		themeSelector.setImmediate(true);

		// Restart button
		Button close = new Button("restart", getApplication(), "close");
		close.setStyle("link");
		mainlayout.addComponent(close, "restart");

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
		// registerFeature("/Terminal", new IntroTerminal());
		// registerFeature("/Terminal/Parameters and URI Handling",
		// new FeatureParameters());

		// Pre-open all menus
		for (Iterator i = features.getItemIds().iterator(); i.hasNext();)
			features.expandItem(i.next());

		// Add demo component and tabs
		currentFeature = new IntroWelcome();
		mainlayout.addComponent(currentFeature, "demo");
		mainlayout.addComponent(currentFeature.getTabSheet(), "tabsheet");

		// Add properties
		properties = currentFeature.getPropertyPanel();
		mainlayout.addComponent(properties, "properties");
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

		// FIXME: navigation statistics
		try {
			if ((event.getProperty().toString() == null)
					&& ((AbstractComponent) event.getProperty()).getTag()
							.equals("tree")) {
				// ignore tree initialization
			} else {
				FeatureUtil.debug(getApplication().getUser().toString(),
						"valueChange "
								+ ((AbstractComponent) event.getProperty())
										.getTag() + ", " + event.getProperty());
			}
		} catch (Exception e) {
			// ignored, should never happen
		}

		// Change feature
		if (event.getProperty() == features) {
			Object id = features.getValue();
			if (id != null) {
				if (features.areChildrenAllowed(id))
					features.expandItem(id);
				Property p = features.getContainerProperty(id, "feature");
				Feature feature = p != null ? ((Feature) p.getValue()) : null;
				if (feature != null) {
					mainlayout.removeComponent(currentFeature);
					mainlayout.removeComponent(currentFeature.getTabSheet());
					mainlayout.addComponent(feature, "demo");
					mainlayout.addComponent(feature.getTabSheet(), "tabsheet");
					currentFeature = feature;
					properties = feature.getPropertyPanel();
					if (properties != null) {
						mainlayout.addComponent(properties, "properties");
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
		// FIXME: navigation statistics
		try {
			FeatureUtil.debug(getApplication().getUser().toString(),
					"buttonClick " + event.getButton().getTag() + ", "
							+ event.getButton().getCaption() + ", "
							+ event.getButton().getValue());
		} catch (Exception e) {
			// ignored, should never happen
		}

	}

	public void addComponent(Component c) {
		// TODO Auto-generated method stub

	}

	public void addListener(ComponentAttachListener listener) {
		// TODO Auto-generated method stub

	}

	public void addListener(ComponentDetachListener listener) {
		// TODO Auto-generated method stub

	}

	public Iterator getComponentIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	public void moveComponentsFrom(ComponentContainer source) {
		// TODO Auto-generated method stub

	}

	public void removeAllComponents() {
		// TODO Auto-generated method stub

	}

	public void removeComponent(Component c) {
		// TODO Auto-generated method stub

	}

	public void removeListener(ComponentAttachListener listener) {
		// TODO Auto-generated method stub

	}

	public void removeListener(ComponentDetachListener listener) {
		// TODO Auto-generated method stub

	}

	public void replaceComponent(Component oldComponent, Component newComponent) {
		// TODO Auto-generated method stub

	}

	public void setMargin(boolean enabled) {
		// TODO Auto-generated method stub
		
	}

	public void setMargin(boolean top, boolean right, boolean bottom,
			boolean left) {
		// TODO Auto-generated method stub
		
	}

	public int getHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getHeightUnits() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getWidthUnits() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setHeight(int height) {
		// TODO Auto-generated method stub
		
	}

	public void setHeightUnits(int units) {
		// TODO Auto-generated method stub
		
	}

	public void setSizeFull() {
		// TODO Auto-generated method stub
		
	}

	public void setSizeUndefined() {
		// TODO Auto-generated method stub
		
	}

	public void setWidth(int width) {
		// TODO Auto-generated method stub
		
	}

	public void setWidthUnits(int units) {
		// TODO Auto-generated method stub
		
	}
}

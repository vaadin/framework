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

import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.Form;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.Select;

public class FeatureContainers extends Feature {

    private static final String INTRO_TEXT = ""
            + "Container is the most advanced of the data "
            + "model supported by IT Mill Toolkit. It provides a very flexible "
            + "way of managing set of items that share common properties. Each "
            + "item is identified by an item id. "
            + "Properties can be requested from container with item "
            + "and property ids. Other way of accessing properties is to first "
            + "request an item from container and then request its properties "
            + "from it."
            + "<br /><br />Container interface was designed with flexibility and "
            + "efficiency in mind. It contains inner interfaces for ordering "
            + "the items sequentially, indexing the items and accessing them "
            + "hierarchically. Those ordering models provide basis for "
            + "Table, Tree and Select UI components. As with other data "
            + "models, the containers support events for notifying about the "
            + "changes."
            + "<br /><br />Set of utilities for converting between container models by "
            + "adding external indexing or hierarchy into existing containers. "
            + "In memory containers implementing indexed and hierarchical "
            + "models provide easy to use tools for setting up in memory data "
            + "storages. There is even a hierarchical container for direct "
            + "file system access.";

    public FeatureContainers() {
        super();
    }

    protected Component getDemoComponent() {

        OrderedLayout l = new OrderedLayout();

        Panel panel = new Panel();
        panel.setCaption("Containers");
        l.addComponent(panel);

        Label label = new Label();
        panel.addComponent(label);

        label.setContentMode(Label.CONTENT_XHTML);
        label.setValue(INTRO_TEXT);

        // Properties
        propertyPanel = new PropertyPanel(panel);
        Form ap = propertyPanel.createBeanPropertySet(new String[] { "width",
                "height" });
        Select themes = (Select) propertyPanel.getField("style");
        themes.addItem("light").getItemProperty(
                themes.getItemCaptionPropertyId()).setValue("light");
        themes.addItem("strong").getItemProperty(
                themes.getItemCaptionPropertyId()).setValue("strong");
        propertyPanel.addProperties("Panel Properties", ap);

        setJavadocURL("data/Container.html");

        return l;
    }

    protected String getExampleSrc() {
        return null;
    }

    protected String getDescriptionXHTML() {
        return null;
    }

    protected String getImage() {
        return null;
    }

    protected String getTitle() {
        return null;
    }

}

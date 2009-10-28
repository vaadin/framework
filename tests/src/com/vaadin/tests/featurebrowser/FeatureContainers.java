/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.featurebrowser;

import com.vaadin.ui.Component;
import com.vaadin.ui.Form;
import com.vaadin.ui.Label;
import com.vaadin.ui.OrderedLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Select;

public class FeatureContainers extends Feature {

    private static final String INTRO_TEXT = ""
            + "Container is the most advanced of the data "
            + "model supported by Vaadin. It provides a very flexible "
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

    @Override
    protected Component getDemoComponent() {

        final OrderedLayout l = new OrderedLayout();

        final Panel panel = new Panel();
        panel.setCaption("Containers");
        l.addComponent(panel);

        final Label label = new Label();
        panel.addComponent(label);

        label.setContentMode(Label.CONTENT_XHTML);
        label.setValue(INTRO_TEXT);

        // Properties
        propertyPanel = new PropertyPanel(panel);
        final Form ap = propertyPanel.createBeanPropertySet(new String[] {
                "width", "height" });
        final Select themes = (Select) propertyPanel.getField("style");
        themes.addItem("light").getItemProperty(
                themes.getItemCaptionPropertyId()).setValue("light");
        themes.addItem("strong").getItemProperty(
                themes.getItemCaptionPropertyId()).setValue("strong");
        propertyPanel.addProperties("Panel Properties", ap);

        setJavadocURL("data/Container.html");

        return l;
    }

    @Override
    protected String getExampleSrc() {
        return null;
    }

    @Override
    protected String getDescriptionXHTML() {
        return null;
    }

    @Override
    protected String getImage() {
        return null;
    }

    @Override
    protected String getTitle() {
        return null;
    }

}

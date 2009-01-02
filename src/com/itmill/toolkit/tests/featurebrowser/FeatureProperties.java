/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.tests.featurebrowser;

import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.Form;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.Select;

public class FeatureProperties extends Feature {

    private static final String INTRO_TEXT = ""
            + "IT Mill Toolkit data model is one of the core concepts "
            + "in the library and Property-interface is the base of that "
            + "model. Property provides standardized API for a single data object "
            + "that can be read (get) and written (set). A property is always typed, but can optionally "
            + "support data type conversions. Optionally properties can provide "
            + "value change events for following the state changes."
            + "<br /><br />The most important function of the Property as well as other "
            + "data models is to connect classes implementing the interface directly to "
            + "editor and viewer classes. Typically this is used to connect different "
            + "data sources to UI components for editing and viewing their contents."
            + "<br /><br />Properties can be utilized either by implementing the interface "
            + "or by using some of the existing property implementations. IT Mill Toolkit "
            + "includes Property interface implementations for "
            + "arbitrary function pairs or Bean-properties as well as simple object "
            + "properties."
            + "<br /><br />Many of the UI components also implement Property interface and allow "
            + "setting of other components as their data-source. These UI-components "
            + "include TextField, DateField, Select, Table, Button, "
            + "Label and Tree.";

    public FeatureProperties() {
        super();
    }

    @Override
    protected Component getDemoComponent() {

        final OrderedLayout l = new OrderedLayout();

        final Panel panel = new Panel();
        panel.setCaption("Data Model");
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

        setJavadocURL("data/Property.html");

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

/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.featurebrowser;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OrderedLayout;
import com.vaadin.ui.TabSheet;

public class FeatureTabSheet extends Feature {

    public FeatureTabSheet() {
        super();
    }

    @Override
    protected Component getDemoComponent() {

        final OrderedLayout l = new OrderedLayout();

        final TabSheet ts = new TabSheet();
        ts
                .addTab(
                        new Label(
                                "This is an example Label component that is added into Tab 1."),
                        "Tab 1 caption", null);
        ts
                .addTab(
                        new Label(
                                "This is an example Label component that is added into Tab 2."),
                        "Tab 2 caption", null);
        ts
                .addTab(
                        new Label(
                                "This is an example Label component that is added into Tab 3."),
                        "Tab 3 caption", null);
        l.addComponent(ts);

        // Properties
        propertyPanel = new PropertyPanel(ts);

        setJavadocURL("ui/TabSheet.html");

        return l;
    }

    @Override
    protected String getExampleSrc() {
        return "TabSheet ts = new TabSheet();\n"
                + "ts.addTab(new Label(\"This is an example Label component that is added into Tab 1.\"),\"Tab 1 caption\",null);\n"
                + "ts.addTab(new Label(\"This is an example Label component that is added into Tab 2.\"),\"Tab 2 caption\",null);\n"
                + "ts.addTab(new Label(\"This is an example Label component that is added into Tab 3.\"),\"Tab 3 caption\",null);";
    }

    @Override
    protected String getDescriptionXHTML() {
        return "A multicomponent container with tabs for switching between them.<br/>"
                + "In the normal case, one would place a layout component on each tab.<br/><br />"
                + "On the demo tab you can try out how the different properties affect "
                + "the presentation of the component.";
    }

    @Override
    protected String getImage() {
        return "icon_demo.png";
    }

    @Override
    protected String getTitle() {
        return "TabSheet";
    }

}

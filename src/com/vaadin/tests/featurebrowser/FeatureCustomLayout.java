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

public class FeatureCustomLayout extends Feature {

    private static final String INTRO_TEXT = ""
            + "A container component with freely designed layout and style. The "
            + "container consists of items with textually represented locations. Each "
            + "item contains one sub-component. The adapter and theme are resposible "
            + "for rendering the layout with given style by placing the items on the "
            + "screen in defined locations."
            + "<br /><br />The definition of locations is not fixed - the each style can define its "
            + "locations in a way that is suitable for it. One typical example would be "
            + "to create visual design for a website as a custom layout: the visual design "
            + "could define locations for \"menu\", \"body\" and \"title\" for example. "
            + "The layout would then be implemented e.g. as plain HTML file."
            + "<br /><br />The default theme handles the styles that are not defined by just drawing "
            + "the subcomponents with flowlayout.";

    @Override
    protected Component getDemoComponent() {
        final OrderedLayout l = new OrderedLayout();

        final Panel panel = new Panel();
        panel.setCaption("Custom Layout");
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

        setJavadocURL("ui/CustomLayout.html");

        return l;
    }

    @Override
    protected String getDescriptionXHTML() {
        return null;
    }

    @Override
    protected String getExampleSrc() {
        return "CustomLayout c = new CustomLayout(\"mystyle\");\n"
                + "c.addComponent(new Label(\"Example description\"),\"label1-location\");\n"
                + "c.addComponent(new Button(\"Example action\"),\"example-action-location\");\n";
    }

    @Override
    protected String getImage() {
        return null;
    }

    @Override
    protected String getTitle() {
        return "Custom Layout";
    }

}

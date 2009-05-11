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

public class FeaturePanel extends Feature {

    public FeaturePanel() {
        super();
    }

    @Override
    protected Component getDemoComponent() {

        final OrderedLayout l = new OrderedLayout();

        // Example panel
        final Panel show = new Panel("Panel caption");
        show
                .addComponent(new Label(
                        "This is an example Label component that is added into Panel."));
        l.addComponent(show);

        // Properties
        propertyPanel = new PropertyPanel(show);
        final Form ap = propertyPanel.createBeanPropertySet(new String[] {
                "width", "height" });
        final Select themes = (Select) propertyPanel.getField("style");
        themes.addItem("light").getItemProperty(
                themes.getItemCaptionPropertyId()).setValue("light");
        themes.addItem("strong").getItemProperty(
                themes.getItemCaptionPropertyId()).setValue("strong");
        propertyPanel.addProperties("Panel Properties", ap);

        setJavadocURL("ui/Panel.html");

        return l;
    }

    @Override
    protected String getExampleSrc() {
        return "Panel show = new Panel(\"Panel caption\");\n"
                + "show.addComponent(new Label(\"This is an example Label component that is added into Panel.\"));";

    }

    @Override
    protected String getDescriptionXHTML() {
        return "Panel is a container for other components, by default it draws a frame around it's "
                + "extremities and may have a caption to clarify the nature of the contained components' purpose."
                + " Panel contains an layout where the actual contained components are added, "
                + "this layout may be switched on the fly.";
    }

    @Override
    protected String getImage() {
        return "icon_demo.png";
    }

    @Override
    protected String getTitle() {
        return "Panel";
    }

}

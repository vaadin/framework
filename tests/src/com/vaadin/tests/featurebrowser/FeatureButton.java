/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.featurebrowser;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Form;
import com.vaadin.ui.OrderedLayout;
import com.vaadin.ui.Select;

public class FeatureButton extends Feature {

    public FeatureButton() {
        super();
    }

    @Override
    protected Component getDemoComponent() {

        final OrderedLayout l = new OrderedLayout();

        final Button b = new Button("Caption");
        l.addComponent(b);

        // Properties
        propertyPanel = new PropertyPanel(b);
        final Select themes = (Select) propertyPanel.getField("style");
        themes.addItem("link").getItemProperty(
                themes.getItemCaptionPropertyId()).setValue("link");
        final Form ap = propertyPanel
                .createBeanPropertySet(new String[] { "switchMode" });
        propertyPanel.addProperties("Button Properties", ap);

        setJavadocURL("ui/Button.html");

        return l;
    }

    @Override
    protected String getExampleSrc() {
        return "Button b = new Button(\"Caption\");\n";

    }

    /**
     * @see com.vaadin.tests.featurebrowser.Feature#getDescriptionXHTML()
     */
    @Override
    protected String getDescriptionXHTML() {
        return "In Vaadin, boolean input values are represented by buttons. "
                + "Buttons may function either as a push buttons or switches. (checkboxes)<br/><br/>"
                + "Button can be directly connected to any method of an object, which "
                + "is an easy way to trigger events: <code> new Button(\"Play\", myPiano \"playIt\")</code>. "
                + "Or in checkbox-mode they can be bound to a boolean proterties and create "
                + " simple selectors.<br /><br /> "
                + "See the demo and try out how the different properties affect "
                + "the presentation of the component.";
    }

    @Override
    protected String getImage() {
        return "icon_demo.png";
    }

    @Override
    protected String getTitle() {
        return "Button";
    }

}

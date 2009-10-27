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

public class IntroLayouts extends Feature {

    private static final String INTRO_TEXT = ""
            + "Layouts are required to place components to specific place in the UI."
            + " You can use plain Java to accomplish sophisticated component layouting."
            + " Other option is to use Custom Layout and let the web page designers"
            + " to take responsibility of component layouting using their own set of tools."
            + "<br /><br />See API documentation below for more information.";

    public IntroLayouts() {
        super();
    }

    @Override
    protected Component getDemoComponent() {

        final OrderedLayout l = new OrderedLayout();

        final Panel panel = new Panel();
        panel.setCaption("Layouts");
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

        setJavadocURL("ui/Layout.html");

        return l;
    }

    @Override
    protected String getExampleSrc() {
        return null;
    }

    /**
     * @see com.vaadin.tests.featurebrowser.Feature#getDescriptionXHTML()
     */
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

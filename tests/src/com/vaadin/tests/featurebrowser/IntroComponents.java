/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.featurebrowser;

import com.vaadin.terminal.ClassResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Form;
import com.vaadin.ui.Label;
import com.vaadin.ui.OrderedLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Select;

public class IntroComponents extends Feature {

    private static final String INTRO_TEXT = ""
            + "This picture summarizes the relations between different user interface (UI) components."
            + "<br /><br />See API documentation below for more information.";

    public IntroComponents() {
        super();
    }

    @Override
    protected Component getDemoComponent() {

        final OrderedLayout l = new OrderedLayout();

        final Panel panel = new Panel();
        panel.setCaption("UI component diagram");
        l.addComponent(panel);

        final Label label = new Label();
        panel.addComponent(label);

        label.setContentMode(Label.CONTENT_XHTML);
        label.setValue(INTRO_TEXT);

        panel.addComponent(new Embedded("", new ClassResource("components.png",
                getApplication())));

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

        setJavadocURL("ui/package-summary.html");

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

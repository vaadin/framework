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

public class IntroDataHandling extends Feature {

    private static final String INTRO_TEXT = ""
            + "Embedded Objects and Upload components are provided as samples"
            + " for data handling section."
            + "<br /><br />See the API documentation of respective components for more information.";

    public IntroDataHandling() {
        super();
    }

    @Override
    protected Component getDemoComponent() {

        final OrderedLayout l = new OrderedLayout();

        final Panel panel = new Panel();
        panel.setCaption("Data Handling");
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
        return "Please select <em>Embedded Objects</em> or <em>Upload</em>"
                + " from the menu for more information.";
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

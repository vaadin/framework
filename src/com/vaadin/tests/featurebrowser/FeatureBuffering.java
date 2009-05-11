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

public class FeatureBuffering extends Feature {

    private static final String INTRO_TEXT = ""
            + "IT Mill Toolkit data model provides interface for implementing "
            + "buffering in data components. The basic idea is that a component "
            + "reading their state from data source can implement "
            + "Buffered-interface, for storing the value internally. "
            + "Buffering provides transactional access "
            + "for setting data: data can be put to a component's buffer and "
            + "afterwards committed to or discarded by re-reding it from the data source. "
            + "The buffering can be used for creating interactive interfaces "
            + "as well as caching the data for performance reasons."
            + "<br /><br />Buffered interface contains methods for committing and discarding "
            + "changes to an object and support for controlling buffering mode "
            + "with read-through and write-through modes. "
            + "Read-through mode means that the value read from the buffered "
            + "object is constantly up to date with the data source. "
            + "Respectively the write-through mode means that all changes to the object are "
            + "immediately updated to the data source.";

    public FeatureBuffering() {
        super();
    }

    @Override
    protected Component getDemoComponent() {

        final OrderedLayout l = new OrderedLayout();
        final Panel panel = new Panel();
        panel.setCaption("Buffering");
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

        setJavadocURL("data/Buffered.html");

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

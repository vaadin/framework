/* *************************************************************************
 
 IT Mill Toolkit 

 Development of Browser User Interfaces Made Easy

 Copyright (C) 2000-2006 IT Mill Ltd
 
 *************************************************************************

 This product is distributed under commercial license that can be found
 from the product package on license.pdf. Use of this product might 
 require purchasing a commercial license from IT Mill Ltd. For guidelines 
 on usage, see licensing-guidelines.html

 *************************************************************************
 
 For more information, contact:
 
 IT Mill Ltd                           phone: +358 2 4802 7180
 Ruukinkatu 2-4                        fax:   +358 2 4802 7181
 20540, Turku                          email:  info@itmill.com
 Finland                               company www: www.itmill.com
 
 Primary source for information and releases: www.itmill.com

 ********************************************************************** */

package com.itmill.toolkit.tests.featurebrowser;

import com.itmill.toolkit.terminal.ClassResource;
import com.itmill.toolkit.terminal.Resource;
import com.itmill.toolkit.terminal.Sizeable;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.Embedded;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.TabSheet;

public abstract class Feature extends CustomComponent {

    private static final String PROP_REMINDER_TEXT = ""
            + "<br /><br />Note: Use <b>Properties</b> panel located at the top"
            + " right corner to try out how different properties affect"
            + " the presentation or functionality of currently selected component.";

    private boolean propsReminder = true;

    private OrderedLayout layout;

    private TabSheet ts;

    private boolean initialized = false;

    private static Resource sampleIcon;

    protected PropertyPanel propertyPanel;

    private Label javadoc;

    private Label description;

    /** Constuctor for the feature component */
    public Feature() {
        layout = new OrderedLayout(OrderedLayout.ORIENTATION_VERTICAL);
        setCompositionRoot(layout);
    }

    /**
     * Actual URL consists of "/doc/api/com/itmill/toolkit/"+url
     * 
     * @param url
     */
    public void setJavadocURL(String url) {
        javadoc
                .setValue("<iframe width=\"100%\" src=\"../doc/api/com/itmill/toolkit/"
                        + url + "\"></iframe>");
    }

    /**
     * Feature component initialization is lazily done when the feature is
     * attached to application
     */
    public void attach() {
        super.attach();

        // Check if the feature is already initialized
        if (initialized) {
            return;
        }
        initialized = true;

        // Javadoc
        javadoc = new Label();
        javadoc.setContentMode(Label.CONTENT_XHTML);

        // Demo
        Component demo = getDemoComponent();
        if (demo != null) {
            layout.addComponent(demo);
        }

        ts = new TabSheet();

        ts.setWidth(100);
        ts.setWidthUnits(Sizeable.UNITS_PERCENTAGE);
        ts.setHeight(100);
        ts.setHeightUnits(Sizeable.UNITS_PERCENTAGE);

        // Description tab
        String title = getTitle();
        if (getDescriptionXHTML() != null) {
            OrderedLayout mainLayout = new OrderedLayout(
                    OrderedLayout.ORIENTATION_VERTICAL);
            OrderedLayout layout = new OrderedLayout(
                    OrderedLayout.ORIENTATION_HORIZONTAL);
            mainLayout.addComponent(layout);
            if (getImage() != null) {
                layout.addComponent(new Embedded("", new ClassResource(
                        getImage(), getApplication())));
            }
            String label = "";
            label += getDescriptionXHTML();
            if (propsReminder) {
                label += PROP_REMINDER_TEXT;
            }
            if (title != null) {
                layout.addComponent(new Label("<h3>" + title + "</h3>",
                        Label.CONTENT_XHTML));
            }
            description = new Label(label, Label.CONTENT_XHTML);
            mainLayout.addComponent(description);

            ts.addTab(mainLayout, "Description", null);
        }

        // Properties table tab
        ts.addTab(getPropertyPanel().getAllProperties(), "Properties", null);

        // Javadoc tab
        if (!javadoc.getValue().equals("")) {
            ts.addTab(javadoc, "Javadoc", null);
        }

        // Code Sample tab
        String example = getExampleSrc();
        if (example != null) {
            OrderedLayout l = new OrderedLayout();
            if (getTitle() != null) {
                l.addComponent(new Label(
                        "<b>// " + getTitle() + " example</b>",
                        Label.CONTENT_XHTML));
            }
            l.addComponent(new Label(example, Label.CONTENT_PREFORMATTED));
            ts.addTab(l, "Code Sample", null);
        }

    }

    /** Get the desctiption of the feature as XHTML fragment */
    protected String getDescriptionXHTML() {
        return "<h2>Feature description is under construction</h2>";
    }

    /** Get the title of the feature */
    protected String getTitle() {
        return getClass().getName();
    }

    public TabSheet getTabSheet() {
        return ts;
    }

    /** Get the name of the image file that will be put on description page */
    protected String getImage() {
        return null;
    }

    /** Get the example application source code */
    protected String getExampleSrc() {
        return null;
    }

    /** Get the feature demo component */
    protected Component getDemoComponent() {
        return null;
    }

    /** Get sample icon resource */
    protected Resource getSampleIcon() {
        if (sampleIcon == null) {
            sampleIcon = new ClassResource("m.gif", getApplication());
        }
        return sampleIcon;
    }

    public PropertyPanel getPropertyPanel() {
        return propertyPanel;
    }

    public void setPropsReminder(boolean propsReminder) {
        this.propsReminder = propsReminder;
    }

    public void updateDescription() {
        String label = "";
        label += getDescriptionXHTML();
        if (propsReminder) {
            label += PROP_REMINDER_TEXT;
        }
        description.setValue(label);
    }

    // Fix for #512
    public Label getDescription() {
        return description;
    }

}
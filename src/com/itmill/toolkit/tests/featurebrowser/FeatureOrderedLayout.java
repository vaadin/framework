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

import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.Form;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Select;
import com.itmill.toolkit.ui.TextField;

public class FeatureOrderedLayout extends Feature {

    public FeatureOrderedLayout() {
        super();
    }

    protected Component getDemoComponent() {

        OrderedLayout l = new OrderedLayout();

        OrderedLayout ol = new OrderedLayout();
        for (int i = 1; i < 5; i++) {
            ol.addComponent(new TextField("Test component " + i));
        }
        l.addComponent(ol);

        // Properties
        propertyPanel = new PropertyPanel(ol);
        Form ap = propertyPanel
                .createBeanPropertySet(new String[] { "orientation" });
        ap.replaceWithSelect("orientation", new Object[] {
                new Integer(OrderedLayout.ORIENTATION_HORIZONTAL),
                new Integer(OrderedLayout.ORIENTATION_VERTICAL) },
                new Object[] { "Horizontal", "Vertical" });
        Select themes = (Select) propertyPanel.getField("style");
        themes.addItem("form").getItemProperty(
                themes.getItemCaptionPropertyId()).setValue("form");
        propertyPanel.addProperties("OrderedLayout Properties", ap);

        setJavadocURL("ui/OrderedLayout.html");

        return l;
    }

    protected String getExampleSrc() {
        return "OrderedLayout ol = new OrderedLayout(OrderedLayout.ORIENTATION_FLOW);\n"
                + "ol.addComponent(new TextField(\"Textfield caption\"));\n"
                + "ol.addComponent(new Label(\"Label\"));\n";

    }

    /**
     * @see com.itmill.toolkit.tests.featurebrowser.Feature#getDescriptionXHTML()
     */
    protected String getDescriptionXHTML() {
        return "This feature provides a container for laying out components either "
                + "vertically, horizontally or flowingly. The orientation may be changed "
                + "during runtime. It also defines a special style for themes to implement called \"form\""
                + "that is used for input forms where the components are laid-out side-by-side "
                + "with their captions."
                + "<br /><br />"
                + "On the demo tab you can try out how the different properties "
                + "affect the presentation of the component.";
    }

    protected String getImage() {
        return "icon_demo.png";
    }

    protected String getTitle() {
        return "OrderedLayout";
    }

}

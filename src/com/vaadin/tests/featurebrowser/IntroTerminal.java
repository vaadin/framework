/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.featurebrowser;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OrderedLayout;

public class IntroTerminal extends Feature {

    public IntroTerminal() {
        super();
    }

    @Override
    protected Component getDemoComponent() {

        final OrderedLayout l = new OrderedLayout();

        final Label lab = new Label();
        lab.setStyle("featurebrowser-none");
        l.addComponent(lab);

        // Properties
        propertyPanel = null;

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
        return "";
    }

    @Override
    protected String getImage() {
        return null;
    }

    @Override
    protected String getTitle() {
        return "Introduction for terminals (TODO)";
    }

}

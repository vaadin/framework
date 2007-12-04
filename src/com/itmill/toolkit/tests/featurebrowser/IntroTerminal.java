/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.tests.featurebrowser;

import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;

public class IntroTerminal extends Feature {

    public IntroTerminal() {
        super();
    }

    protected Component getDemoComponent() {

        final OrderedLayout l = new OrderedLayout();

        final Label lab = new Label();
        lab.setStyle("featurebrowser-none");
        l.addComponent(lab);

        // Properties
        propertyPanel = null;

        return l;
    }

    protected String getExampleSrc() {
        return null;
    }

    /**
     * @see com.itmill.toolkit.tests.featurebrowser.Feature#getDescriptionXHTML()
     */
    protected String getDescriptionXHTML() {
        return "";
    }

    protected String getImage() {
        return null;
    }

    protected String getTitle() {
        return "Introduction for terminals (TODO)";
    }

}

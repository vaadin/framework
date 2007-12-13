/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.tests;

import com.itmill.toolkit.terminal.Size;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.DateField;
import com.itmill.toolkit.ui.ExpandLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;

/**
 * 
 * @author IT Mill Ltd.
 */
public class TestForExpandLayout extends CustomComponent {

    ExpandLayout main = new ExpandLayout();

    DateField df;

    public TestForExpandLayout() {
        setCompositionRoot(main);
        createNewView();
    }

    public void createNewView() {
        main.removeAllComponents();
        for (int i = 0; i < 10; i++) {
            final ExpandLayout el = new ExpandLayout(
                    OrderedLayout.ORIENTATION_HORIZONTAL);
            for (int j = 0; j < 10; j++) {
                final Label l = new Label("label" + i + ":" + j);
                el.addComponent(l);
            }
            if (i > 0) {
                el.getSize().setHeight(1, Size.UNITS_EM);
            }
            main.addComponent(el);
        }

    }
}

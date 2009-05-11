/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.tests;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.ExpandLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OrderedLayout;
import com.vaadin.ui.Panel;

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
        for (int i = 0; i < 6; i++) {
            final ExpandLayout el = new ExpandLayout(
                    OrderedLayout.ORIENTATION_HORIZONTAL);
            for (int j = 0; j < i + 3; j++) {
                final Label l = new Label("label" + i + ":" + j);
                el.addComponent(l);
            }
            if (i > 0) {
                // el.setMargin(true);
                el.setSizeUndefined();
                el.setWidth("100%");
                if (i % 2 == 0) {
                    el.setHeight("8em");
                    Panel p = new Panel("tp");
                    p.addComponent(new Label("panelc"));
                    p.setHeight("100%");
                    p.setWidth("100px");
                    el.addComponent(p);
                }
            }
            main.addComponent(el);
        }

    }
}

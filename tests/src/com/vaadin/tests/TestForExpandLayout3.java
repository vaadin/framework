/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.tests;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.ExpandLayout;

/**
 * 
 * @author IT Mill Ltd.
 */
public class TestForExpandLayout3 extends CustomComponent {

    ExpandLayout main = new ExpandLayout();

    DateField df;

    public TestForExpandLayout3() {
        setCompositionRoot(main);
        createNewView();
    }

    public void createNewView() {
        main.removeAllComponents();

        ExpandLayout el;
        Button b;
        Button b2;

        el = new ExpandLayout(ExpandLayout.ORIENTATION_HORIZONTAL);

        b = new Button("SDFS");
        b2 = new Button("DSFSDFDFSSDF");

        el.addComponent(b);
        el.addComponent(b2);

        el.expand(b);
        el.setComponentAlignment(b, ExpandLayout.ALIGNMENT_RIGHT,
                ExpandLayout.ALIGNMENT_VERTICAL_CENTER);
        main.addComponent(el);

        el = new ExpandLayout(ExpandLayout.ORIENTATION_HORIZONTAL);

        b = new Button("SDFS");
        b2 = new Button("DSFSDFDFSSDF");

        el.addComponent(b);
        el.addComponent(b2);

        el.expand(b);
        el.setComponentAlignment(b, ExpandLayout.ALIGNMENT_HORIZONTAL_CENTER,
                ExpandLayout.ALIGNMENT_VERTICAL_CENTER);
        el.setHeight(60, ExpandLayout.UNITS_PIXELS);
        el.setMargin(true);
        main.addComponent(el);

        el = new ExpandLayout(ExpandLayout.ORIENTATION_HORIZONTAL);

        b = new Button("SDFS");
        b2 = new Button("DSFSDFDFSSDF");

        el.addComponent(b);
        el.addComponent(b2);

        el.expand(b);
        el.setComponentAlignment(b, ExpandLayout.ALIGNMENT_RIGHT,
                ExpandLayout.ALIGNMENT_BOTTOM);
        el.setHeight(100, ExpandLayout.UNITS_PIXELS);
        el.setSpacing(true);

        main.addComponent(el);

    }
}

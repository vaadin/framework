/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.tests;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.ExpandLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

/**
 * 
 * @author IT Mill Ltd.
 */
public class TestForExpandLayout2 extends CustomComponent {

    ExpandLayout main;

    public TestForExpandLayout2() {
        createNewView();
        setCompositionRoot(main);
    }

    public void createNewView() {
        main = new ExpandLayout(ExpandLayout.ORIENTATION_HORIZONTAL);

        Panel left = new Panel("Left column");
        left.setHeight(100, Panel.UNITS_PERCENTAGE);
        left.setWidth(150);
        main.addComponent(left);

        ExpandLayout center = new ExpandLayout();
        center.addComponent(new Label("header"));
        Panel mainContent = new Panel();
        center.addComponent(mainContent);
        center.expand(mainContent);
        mainContent.setSizeFull();

        ExpandLayout buttons = new ExpandLayout(
                ExpandLayout.ORIENTATION_HORIZONTAL);
        buttons.setHeight(30, ExpandLayout.UNITS_PIXELS);
        Button b1 = new Button("Save");
        Button b2 = new Button("Cancel");
        Button b3 = new Button("Logout");
        buttons.addComponent(b1);
        buttons.setComponentAlignment(b1, ExpandLayout.ALIGNMENT_RIGHT,
                ExpandLayout.ALIGNMENT_TOP);
        buttons.addComponent(b2);
        buttons.addComponent(b3);
        center.addComponent(buttons);

        main.addComponent(center);
        main.expand(center);

        Panel right = new Panel("Right column");
        right.setHeight(100, Panel.UNITS_PERCENTAGE);
        right.setWidth(200);

        main.addComponent(right);

    }
}

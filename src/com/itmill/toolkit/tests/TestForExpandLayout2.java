/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.tests;

import com.itmill.toolkit.terminal.Size;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.ExpandLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Panel;

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
        left.getSize().setHeight(100, Size.UNITS_PERCENTAGE);
        left.getSize().setWidth(150);
        main.addComponent(left);

        ExpandLayout center = new ExpandLayout();
        center.addComponent(new Label("header"));
        Panel mainContent = new Panel();
        center.addComponent(mainContent);
        center.expand(mainContent);
        mainContent.getSize().setSizeFull();

        ExpandLayout buttons = new ExpandLayout(
                ExpandLayout.ORIENTATION_HORIZONTAL);
        buttons.getSize().setHeight(30, Size.UNITS_PIXELS);
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
        right.getSize().setHeight(100, Size.UNITS_PERCENTAGE);
        right.getSize().setWidth(200);

        main.addComponent(right);

    }
}

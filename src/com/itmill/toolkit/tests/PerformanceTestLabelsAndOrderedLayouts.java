/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.tests;

import java.util.Date;

import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Button.ClickListener;

public class PerformanceTestLabelsAndOrderedLayouts extends CustomComponent {
    private final OrderedLayout main;

    private final OrderedLayout testContainer;

    private Date startTime;

    private final Label result;

    private static final String DESCRIPTION = "Simple test that renders n labels into ordered layout.";

    private static final int INITIAL_COMPONENTS = 1000;

    public PerformanceTestLabelsAndOrderedLayouts() {
        main = new OrderedLayout();
        setCompositionRoot(main);
        addInfo();

        result = new Label();
        main.addComponent(result);

        main.addComponent(new Button("click when rendered",
                new ClickListener() {

                    public void buttonClick(ClickEvent event) {
                        endTest();
                    }
                }));

        testContainer = new OrderedLayout();

        for (int i = 0; i < INITIAL_COMPONENTS; i++) {
            Label l = new Label("foo" + i);
            testContainer.addComponent(l);
        }

        main.addComponent(testContainer);
        startTest();
    }

    public void startTest() {
        startTime = new Date();
    }

    public void endTest() {
        final long millis = (new Date()).getTime() - startTime.getTime();
        final Float f = new Float(millis / 1000.0);
        result.setValue("Test completed in " + f + " seconds");
    }

    private void addInfo() {
        main.addComponent(new Label(DESCRIPTION, Label.CONTENT_XHTML));
    }

}

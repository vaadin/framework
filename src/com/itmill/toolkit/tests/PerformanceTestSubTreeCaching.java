/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.tests;

import java.util.Date;

import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Table;

public class PerformanceTestSubTreeCaching extends CustomComponent {

    private final OrderedLayout main;

    private final OrderedLayout testContainer;

    private Date startTime;

    private final Label result;

    private static final String DESCRIPTION = "Hyphothesis: Toolkit 4 has major architechtural problem when adding "
            + "small incrementall updates to a container which has either a lot or "
            + "some very slow components in it. Toolkit 5 has 'subtree caching' and a"
            + " small amount of logic in containers, so CommunicationManager can assure"
            + " that client do not need information about unchanged components it contains."
            + " Completing test ought to be much faster with Toolkit 5.";

    private static final int INITIAL_COMPONENTS = 40;

    public PerformanceTestSubTreeCaching() {
        main = new OrderedLayout();
        setCompositionRoot(main);
        addInfo();

        Button b = new Button("start test", this, "startTest");
        b
                .setDescription("Push this button to start test. A test label will be rendered above existing components.");
        main.addComponent(b);
        b = new Button("end test", this, "endTest");
        b
                .setDescription("Push this button as soon as test componenet is rendered.");
        main.addComponent(b);

        result = new Label();
        main.addComponent(result);

        testContainer = new OrderedLayout();
        populateContainer(testContainer, INITIAL_COMPONENTS);
        main.addComponent(testContainer);
    }

    public void startTest() {
        startTime = new Date();
        testContainer.addComponentAsFirst(new Label("Simplel Test Component"));
    }

    public void endTest() {
        final long millis = (new Date()).getTime() - startTime.getTime();
        final Float f = new Float(millis / 1000.0);
        result.setValue("Test completed in " + f + " seconds");
    }

    /**
     * Adds n Table components to given container
     * 
     * @param testContainer2
     */
    private void populateContainer(OrderedLayout container, int n) {
        for (int i = 0; i < n; i++) {
            // array_type array_element = [i];
            final Table t = TestForTablesInitialColumnWidthLogicRendering
                    .getTestTable(5, 100);
            container.addComponent(t);
        }
    }

    private void addInfo() {
        main.addComponent(new Label(DESCRIPTION, Label.CONTENT_XHTML));
    }

}

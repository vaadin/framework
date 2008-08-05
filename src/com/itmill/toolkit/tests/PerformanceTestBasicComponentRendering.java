/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.tests;

import java.util.Date;
import java.util.Map;

import com.itmill.toolkit.terminal.UserError;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.ComboBox;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.DateField;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.TabSheet;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.TextField;

public class PerformanceTestBasicComponentRendering extends CustomComponent {

    private final OrderedLayout main;

    private final OrderedLayout testContainer;

    private Date startTime;

    private final Label result;

    private static final String DESCRIPTION = "Rendering lots of differend components to stress rendering performance. Visits server after render (due table cache row fetch) and prints client round trip time to a label. More exact render time can be checked from clients debug dialog.";

    private static final int INITIAL_COMPONENTS = 10;

    public PerformanceTestBasicComponentRendering() {

        main = new OrderedLayout();
        setCompositionRoot(main);
        addInfo();

        result = new Label();
        main.addComponent(result);

        testContainer = new OrderedLayout();

        final Table t = TestForTablesInitialColumnWidthLogicRendering
                .getTestTable(5, 200);

        Table t2 = new Table("Test Table with 199 rows rendered initially") {
            public void changeVariables(Object source, Map variables) {
                super.changeVariables(source, variables);
                // end timing on cache row request
                endTest();
            }
        };
        t2.setPageLength(199); // render almost all rows at once
        t2.setContainerDataSource(t.getContainerDataSource());

        testContainer.addComponent(t2);

        for (int i = 0; i < INITIAL_COMPONENTS; i++) {
            ComboBox cb = new ComboBox("Combobox " + i);
            for (int j = 0; j < INITIAL_COMPONENTS; j++) {
                cb.addItem("option " + i + " " + j);
            }
            testContainer.addComponent(cb);

            TextField tf = new TextField("TextField " + i);
            tf.setDescription("DESC SDKJSDF");
            tf.setComponentError(new UserError("dsfjklsdf"));
            testContainer.addComponent(tf);

            testContainer.addComponent(new DateField("DateField" + i));

            testContainer.addComponent(new Button("Button" + i));

            TabSheet ts = new TabSheet();

            for (int j = 0; j < INITIAL_COMPONENTS; j++) {
                Label tab = new Label("Tab content " + i + " " + j);
                tab.setCaption("Tab " + i + " " + j);
                ts.addTab(tab);
            }
            testContainer.addComponent(ts);

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

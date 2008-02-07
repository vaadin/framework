package com.itmill.toolkit.tests.robustness;

import com.itmill.toolkit.tests.util.Log;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;

public class RobustnessSimple extends Robustness implements
        Button.ClickListener {

    /**
     * Create single orderedlayout with a label containing 1Mb of data
     */
    public void create() {
        count++;

        // remove old stressLayout, all dependant components should be now
        // allowed for garbage collection.
        if (stressLayout != null)
            main.removeComponent(stressLayout);

        // create new stress layout
        stressLayout = new OrderedLayout();

        // fill with random components
        Label label = new Label("Label " + Log.getMemoryStatistics(),
                Label.CONTENT_PREFORMATTED);
        byte[] data = new byte[1024 * 1024];
        label.setData(data);
        stressLayout.addComponent(label);

        // add new component container to main layout
        main.addComponent(stressLayout);

        System.out.println("Created " + count + " times.");
    }

}

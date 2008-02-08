package com.itmill.toolkit.tests.robustness;

import com.itmill.toolkit.tests.util.Log;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Label;

public class RobustnessComplex extends Robustness implements
        Button.ClickListener {

    /**
     * Create complex layouts with components and listeners.
     */
    public void create() {
        count++;

        // remove old stressLayout, all dependant components should be now
        // allowed for garbage collection.
        if (stressLayout != null)
            main.removeComponent(stressLayout);

        // create new stress layout
        stressLayout = randomComponents
                .getRandomComponentContainer("Component container " + count);

        Label label = new Label("Label " + Log.getMemoryStatistics(),
                Label.CONTENT_PREFORMATTED);
        stressLayout.addComponent(label);

        // fill with random components
        randomComponents.fillLayout(stressLayout, 25);

        // add new component container to main layout
        main.addComponent(stressLayout);

        // if ((count % 100) == 0) {
        System.out.println("Created " + count + " times.");
        // }
    }
}

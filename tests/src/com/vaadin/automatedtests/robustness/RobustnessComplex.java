/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.automatedtests.robustness;

import com.vaadin.automatedtests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;

public class RobustnessComplex extends Robustness implements
        Button.ClickListener {

    /**
     * Create complex layouts with components and listeners.
     */
    @Override
    public void create() {
        count++;

        // remove old stressLayout, all dependant components should be now
        // allowed for garbage collection.
        if (stressLayout != null) {
            main.removeComponent(stressLayout);
        }

        // create new stress layout
        stressLayout = randomComponents
                .getRandomComponentContainer("Component container " + count);

        Label label = new Label("Label " + Log.getMemoryStatistics(),
                Label.CONTENT_PREFORMATTED);
        stressLayout.addComponent(label);

        // fill with random components
        randomComponents.fillLayout(stressLayout, 50);

        // add new component container to main layout
        main.addComponent(stressLayout);

        // if ((count % 100) == 0) {
        System.out.println("Created " + count + " times.");
        // }
    }
}

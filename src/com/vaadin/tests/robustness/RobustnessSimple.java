package com.vaadin.tests.robustness;

import com.vaadin.automatedtests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.OrderedLayout;

public class RobustnessSimple extends Robustness implements
        Button.ClickListener {

    @Override
    public void create() {
        count++;

        // remove old stressLayout, all dependant components should be now
        // allowed for garbage collection.
        if (stressLayout != null) {
            main.removeComponent(stressLayout);
        }

        // create new stress layout
        stressLayout = new OrderedLayout();

        // CASE single orderedlayout with a label containing 1Mb of data
        // fill with random components
        Label label = new Label("Label " + Log.getMemoryStatistics(),
                Label.CONTENT_PREFORMATTED);
        byte[] data = new byte[1024 * 1024];
        label.setData(data);
        stressLayout.addComponent(label);

        // CASE simple button example
        // stressLayout.addComponent(new ButtonExample());

        // CASE #1392, this "leaks" in a way that we cannot release opened
        // windows
        // in any way (Window.open method)
        // stressLayout.addComponent(new WindowingExample());

        // CASE TableExample
        // stressLayout.addComponent(new TableExample());

        // add new component container to main layout
        main.addComponent(stressLayout);

        System.out.println("Created " + count + " times.");
    }

}

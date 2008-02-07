package com.itmill.toolkit.tests.robustness;

import com.itmill.toolkit.tests.util.RandomComponents;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.ComponentContainer;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class RobustnessComplex extends com.itmill.toolkit.Application implements
        Button.ClickListener {

    static int totalCount = 0;

    int count = 0;

    final Window main = new Window("Robustness tests by featurebrowser");

    final Button button = new Button("Create");

    final Label label = new Label();

    ComponentContainer stressLayout;

    RandomComponents randomComponents = new RandomComponents();

    public void init() {
        createNewView();
    }

    public void createNewView() {
        setMainWindow(main);
        main.removeAllComponents();

        main.addComponent(label);
        main.addComponent(button);
        button.addListener(this);

        button.setDebugId("createButton");

        create();
    }

    public void buttonClick(ClickEvent event) {
        create();
    }

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

        // fill with random components
        randomComponents.fillLayout(stressLayout, 20);

        // add new component container to main layout
        main.addComponent(stressLayout);

        // if ((count % 100) == 0) {
        System.out.println("Created " + count + " times.");
        // }
    }

}

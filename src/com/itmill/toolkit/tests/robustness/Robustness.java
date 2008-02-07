package com.itmill.toolkit.tests.robustness;

import com.itmill.toolkit.tests.util.Log;
import com.itmill.toolkit.tests.util.RandomComponents;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.ComponentContainer;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;

public abstract class Robustness extends com.itmill.toolkit.Application
        implements Button.ClickListener {

    static int totalCount = 0;

    int count = 0;

    final Window main = new Window("Robustness tests by featurebrowser");

    final Button close = new Button("Close application");

    final Button create = new Button("Create");

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
        main.addComponent(close);
        main.addComponent(create);
        close.addListener(this);
        create.addListener(this);

        create.setDebugId("createButton");
    }

    public void buttonClick(ClickEvent event) {
        if (event.getButton() == create)
            create();
        else if (event.getButton() == close) {
            System.out.println("Before close, memory statistics:");
            System.out.println(Log.getMemoryStatistics());
            close();
            System.out.println("After close, memory statistics:");
            System.out.println(Log.getMemoryStatistics());
        }
    }

    public abstract void create();
}

/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.automatedtests.robustness;

import com.vaadin.automatedtests.util.Log;
import com.vaadin.automatedtests.util.RandomComponents;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

public abstract class Robustness extends com.vaadin.Application
        implements Button.ClickListener {

    static int totalCount = 0;

    int count = 0;

    final Window main = new Window("Robustness tests by featurebrowser");

    Button close = new Button("Close application");

    Button remove = new Button("Remove all components");

    Button create = new Button("Create");

    Label label = new Label();

    ComponentContainer stressLayout;

    RandomComponents randomComponents = new RandomComponents();

    @Override
    public void init() {
        createNewView();
    }

    public void createNewView() {
        setMainWindow(main);
        main.setDebugId("MainWindow");
        main.removeAllComponents();

        main.addComponent(label);
        main.addComponent(close);
        main.addComponent(remove);
        main.addComponent(create);
        close.addListener(this);
        remove.addListener(this);
        create.addListener(this);

        remove.setDescription("After this garbage collector should"
                + " be able to collect every component"
                + " inside stressLayout.");

        close.setDebugId("close");
        remove.setDebugId("remove");
        create.setDebugId("create");

    }

    public void buttonClick(ClickEvent event) {
        if (event.getButton() == create) {
            create();
        } else if (event.getButton() == remove) {
            main.removeAllComponents();
            close.removeListener(this);
            remove.removeListener(this);
            create.removeListener(this);
            close = null;
            remove = null;
            create = null;
            label = null;
            stressLayout = null;
            System.out.println("main.getLayout()=" + main.getLayout());
            System.out.println(Log.getMemoryStatistics());
        } else if (event.getButton() == close) {
            System.out.println("Before close, memory statistics:");
            System.out.println(Log.getMemoryStatistics());
            close();
            // Still valueUnbound (session expiration) needs to occur for GC to
            // do its work
            System.out.println("After close, memory statistics:");
            System.out.println(Log.getMemoryStatistics());
        }
    }

    public abstract void create();
}

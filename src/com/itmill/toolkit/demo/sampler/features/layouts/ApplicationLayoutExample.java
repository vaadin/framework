package com.itmill.toolkit.demo.sampler.features.layouts;

import java.net.URL;

import com.itmill.toolkit.demo.sampler.ExampleUtil;
import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.ui.AbstractSelect;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.HorizontalLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.TabSheet;
import com.itmill.toolkit.ui.Tree;
import com.itmill.toolkit.ui.VerticalLayout;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class ApplicationLayoutExample extends VerticalLayout {

    private URL nativeWindowURL = null;
    private Button b1;

    public ApplicationLayoutExample() {
        setSpacing(true);

        b1 = new Button("Open a new window with Application Layout", this,
                "openButtonClick");
        addComponent(b1);

    }

    public void openButtonClick(ClickEvent event) {
        if (nativeWindowURL == null) {
            getApplication().addWindow(createWindow());
        }
        getApplication().getMainWindow().open(
                new ExternalResource(nativeWindowURL), "_blank");
    }

    /*
     * Create new window which contains the layout obtained from
     * createDemoLayout()
     */
    private Window createWindow() {
        // Create a new window and add it to the application
        final Window w = new Window("Application Layout example");
        getApplication().addWindow(w);
        // Set the window's layout to the one we get from createDemoLayout()
        w.setLayout(createDemoLayout());
        // Set the layout's size full
        w.getLayout().setSizeFull();

        nativeWindowURL = w.getURL();
        return w;
    }

    /*
     * Creates the actual Layout
     */
    private HorizontalLayout createDemoLayout() {
        // Create a new layout
        HorizontalLayout demoLayout = new HorizontalLayout();
        // Enable spacing in the layout
        demoLayout.setSpacing(true);
        // Enable margins in the layout
        demoLayout.setMargin(true);

        // Create an example tree component, set to full size
        Tree t = new Tree("Hardware Inventory", ExampleUtil
                .getHardwareContainer());
        t.setItemCaptionPropertyId(ExampleUtil.hw_PROPERTY_NAME);
        t.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
        t.setSizeFull();

        // Expand the whole tree
        int itemId = t.getContainerDataSource().size();
        for (int i = 0; i < itemId; i++) {
            t.expandItemsRecursively(i);
        }

        // Create an example tabsheet component with some content
        final TabSheet ts = new TabSheet();
        final Label l1 = new Label(
                "This window shows an example of a so called"
                        + " Application-style layout. You probably notice that the layout"
                        + " is quite similar to other examples present in the Sampler."
                        + " These examples include e.g. SplitPanel and Expanding Components."
                        + " Please select one of the other tabs to find out more about"
                        + " application-style layouting!");
        l1.setSizeFull();
        final Label l2 = new Label(
                "Typical design principles for an Application-style layout"
                        + " follow the design of traditional applications, e.g."
                        + " you should fill the whole window with your UI elements."
                        + " You should also make it dynamic, so it handles resizing events"
                        + " reasonably. In Toolkit, this is generally"
                        + " achieved by calling setSizeFull() for a Component."
                        + " In this example you may notice that sizes of all the components"
                        + " and layouts are set to full. Additionally, an expand ratio is set"
                        + " to achieve reasonable component sizes.");
        l2.setSizeFull();
        final Label l3 = new Label(
                "In some cases it is reasonable to set some of the"
                        + " sizes by hand, or at least set some limits to the sizes."
                        + " For example here an expand ratio of 1:4 has been set for the Tree"
                        + " Component and the layout containing the TabSheet component.");
        l3.setSizeFull();

        // Add tabs to TabSheet with appropriate captions
        ts.addTab(l1);
        ts.setTabCaption(l1, "Example of an Application-style layout");
        ts.addTab(l2);
        ts.setTabCaption(l2, "Principles");
        ts.addTab(l3);
        ts.setTabCaption(l3, "Exceptions");
        // Set the TabSheet to full size.
        ts.setSizeFull();

        // Create a basic label
        Label commentArea = new Label("Comment area: no comments");

        // Create an additional VerticalLayout for the TabSheet and the Label
        VerticalLayout tabsAndComments = new VerticalLayout();
        tabsAndComments.setSizeFull();
        tabsAndComments.addComponent(ts);
        tabsAndComments.addComponent(commentArea);

        // Add the components to the layout
        demoLayout.addComponent(t);
        demoLayout.addComponent(tabsAndComments);

        // Set expand ratios to the components
        demoLayout.setExpandRatio(t, 1);
        demoLayout.setExpandRatio(tabsAndComments, 3);

        return demoLayout;
    }
}
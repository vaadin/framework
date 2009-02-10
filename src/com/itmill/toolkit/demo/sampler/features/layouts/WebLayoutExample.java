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

public class WebLayoutExample extends VerticalLayout {

    private URL nativeWindowURL = null;
    private Button b1;

    public WebLayoutExample() {
        setSpacing(true);

        b1 = new Button("Open a new window with Web Layout", this,
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
        final Window w = new Window("Web Layout example");
        getApplication().addWindow(w);
        // Set the window's layout to the one we get from createDemoLayout()
        w.setLayout(createDemoLayout());
        // Set the layout's size undefined
        w.getLayout().setSizeUndefined();

        nativeWindowURL = w.getURL();
        return w;
    }

    /*
     * Creates the actual Layout
     */
    private HorizontalLayout createDemoLayout() {
        // Create a new layout
        HorizontalLayout demoLayout = new HorizontalLayout();
        // Enable spacing and margins in the layout
        demoLayout.setSpacing(true);
        demoLayout.setMargin(true);

        // Create an example tree component, set to undefined size
        Tree t = new Tree("Hardware Inventory", ExampleUtil
                .getHardwareContainer());
        t.setItemCaptionPropertyId(ExampleUtil.hw_PROPERTY_NAME);
        t.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
        t.setSizeUndefined();

        // Expand the whole tree
        int itemId = t.getContainerDataSource().size();
        for (int i = 0; i < itemId; i++) {
            t.expandItemsRecursively(i);
        }

        // Create an example tabsheet component with some content
        final TabSheet ts = new TabSheet();
        final Label l1 = new Label(
                "This window shows an example of a so called"
                        + " Web-style layout. You probably notice that the layout"
                        + " is quite different than the Application-style layout."
                        + " Please select one of the other tabs to find out more!");
        l1.setSizeUndefined();
        final Label l2 = new Label(
                "Typical design principles for a Web-style layout"
                        + " state that the content should 'push' the layout"
                        + " to reach adequate proportions. In Toolkit, this is"
                        + " achieved by calling setSizeUndefined() for a Component."
                        + " In this example you may notice that the width of the Tree"
                        + " component is set by the content of it. Also, the width of"
                        + " the Tabsheet is set by the combined width of the tab captions.");
        l2.setSizeUndefined();
        final Label l3 = new Label(
                "In some cases it is reasonable to set some of the"
                        + " sizes by hand, or at least set some limits to the sizes."
                        + " For example here the height of the TabSheet component has"
                        + " been set to a fixed 200 pixels. Also, in some cases setting"
                        + " the base layout of the window to be of undefined size does not"
                        + " produce very good results.");
        l3.setSizeUndefined();
        ts.addTab(l1);
        ts.setTabCaption(l1, "Example of a Web-style layout");
        ts.addTab(l2);
        ts.setTabCaption(l2, "Principles");
        ts.addTab(l3);
        ts.setTabCaption(l3, "Exceptions");
        // Set the TabSheet width undefined. Also set a defined height.
        ts.setSizeUndefined();
        ts.setHeight(200, UNITS_PIXELS);

        // Create a basic label
        Label commentArea = new Label("Comment area: no comments");

        // Create an additional VerticalLayout for the TabSheet and the Label
        VerticalLayout tabsAndComments = new VerticalLayout();
        tabsAndComments.setSizeUndefined();
        tabsAndComments.addComponent(ts);
        tabsAndComments.addComponent(commentArea);

        // Add the components to the layout
        demoLayout.addComponent(t);
        demoLayout.addComponent(tabsAndComments);

        return demoLayout;
    }
}
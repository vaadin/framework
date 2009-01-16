package com.itmill.toolkit.demo.sampler.features.layouts;

import java.net.URL;

import com.itmill.toolkit.demo.sampler.ExampleUtil;
import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.ui.AbstractSelect;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.HorizontalLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.TabSheet;
import com.itmill.toolkit.ui.Tree;
import com.itmill.toolkit.ui.VerticalLayout;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class ExpandingComponentExample extends VerticalLayout {

    private URL nativeWindowURL = null;
    private Button b1;

    public ExpandingComponentExample() {
        setSpacing(true);

        b1 = new Button("Open a new window with expanding components", this,
                "openButtonClick");
        addComponent(b1);

    }

    public void openButtonClick(ClickEvent event) {
        if (nativeWindowURL == null) {
            getApplication().addWindow(createWindow());
        }
        System.err.println(nativeWindowURL);
        getApplication().getMainWindow().open(
                new ExternalResource(nativeWindowURL), "_blank");
    }

    /*
     * Create new window which contains the layout obtained from
     * createDemoLayout()
     */
    private Window createWindow() {
        // Create a new window for the expanding components
        final Window w = new Window("Expanding Components Demo");
        getApplication().addWindow(w);
        w.setLayout(createDemoLayout());
        nativeWindowURL = w.getURL();
        return w;
    }

    /*
     * Creates the actual Layout with expanding components
     */
    private HorizontalLayout createDemoLayout() {
        // Create a new layout
        HorizontalLayout expandDemoLayout = new HorizontalLayout();
        // Enable spacing in the layout
        expandDemoLayout.setSpacing(true);
        expandDemoLayout.setSizeFull();

        // Create an example tree component, set to full size
        Tree t = new Tree("Hardware Inventory", ExampleUtil
                .getHardwareContainer());
        t.setItemCaptionPropertyId(ExampleUtil.hw_PROPERTY_NAME);
        t.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
        t.setSizeFull();

        // Place the Tree component inside a panel to enable scrolling if
        // necessary
        Panel p = new Panel();
        p.setSizeFull();
        p.setStyleName(Panel.STYLE_LIGHT);
        p.addComponent(t);
        p.getLayout().setSizeUndefined();

        // Add the panel component to the layout
        expandDemoLayout.addComponent(p);

        // Set an expand ratio for the Panel containing the Tree
        expandDemoLayout.setExpandRatio(p, 1.0f);

        // Create an example tabsheet component with some content
        final TabSheet ts = new TabSheet();
        final Label l1 = new Label(
                "This window shows an example of a Layout with expanding"
                        + " components. The expand ratios of the Tree and TabSheet"
                        + " are set at 1.0 : 4.0, which means that the tree component"
                        + " will occupy 1/5 of the horizontal space and the TabSheet"
                        + " component will occupy 4/5.");
        final Label l2 = new Label(
                "Try resizing the window (horizontally) and you will"
                        + " see how the expand ratios work out.");
        final Label l3 = new Label(
                "When the window gets too small for the components,"
                        + " scrollbars will be added to enable viewing the whole content.");
        ts.addTab(l1);
        ts.setTabCaption(l1, "Expanding layout demo");
        ts.addTab(l2);
        ts.setTabCaption(l2, "Resizing");
        ts.addTab(l3);
        ts.setTabCaption(l3, "Scrolling");
        ts.setSizeFull();

        // Add the component to the layout
        // No need to put the TabSheet inside a Panel, since TabSheet handles
        // scrolling automatically
        expandDemoLayout.addComponent(ts);

        // Set an expand ratio for the TabSheet
        expandDemoLayout.setExpandRatio(ts, 4.0f);

        return expandDemoLayout;
    }
}
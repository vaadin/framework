package com.itmill.toolkit.demo.sampler.features.layouts;

import java.net.URL;

import com.itmill.toolkit.demo.sampler.ExampleUtil;
import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.ui.AbstractSelect;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.SplitPanel;
import com.itmill.toolkit.ui.TabSheet;
import com.itmill.toolkit.ui.Tree;
import com.itmill.toolkit.ui.VerticalLayout;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class SplitPanelBasicExample extends VerticalLayout {

    private URL nativeWindowURL = null;
    private Button b1;

    public SplitPanelBasicExample() {
        setSpacing(true);

        b1 = new Button("Open a new window with a SplitPanel", this,
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
        // Create a new window for the SplitPanel
        final Window w = new Window("SplitPanel Demo");
        getApplication().addWindow(w);
        w.setLayout(createDemoLayout());
        nativeWindowURL = w.getURL();
        return w;
    }

    /*
     * Creates the actual Layout with two SplitPanels and some content
     */
    private SplitPanel createDemoLayout() {
        // Create a new SplitPanel
        SplitPanel demoSP = new SplitPanel();
        // Set orientation
        demoSP.setOrientation(SplitPanel.ORIENTATION_HORIZONTAL);
        // Set split position (from left edge) in pixels
        demoSP.setSplitPosition(200, UNITS_PIXELS);

        // Create an example tree component, set to full size
        Tree t = new Tree("Hardware Inventory", ExampleUtil
                .getHardwareContainer());
        t.setItemCaptionPropertyId(ExampleUtil.hw_PROPERTY_NAME);
        t.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
        t.setSizeFull();

        // Add the Tree component to the SplitPanel
        demoSP.addComponent(t);

        // Create a second SplitPanel
        SplitPanel subSP = new SplitPanel();
        // Set orientation
        subSP.setOrientation(SplitPanel.ORIENTATION_VERTICAL);
        // Set split position in percentage
        subSP.setSplitPosition(80);
        // Set the split locked (= not resizeable)
        subSP.setLocked(true);

        // Create an example tabsheet component with some content
        final TabSheet ts = new TabSheet();
        final Label l1 = new Label(
                "This window shows an example of a SplitPanel component."
                        + " A second SplitPanel is inserted into the first one"
                        + " to allow a split into three regions.");
        final Label l2 = new Label(
                "Try resizing the window (horizontally) and you will"
                        + " notice that the leftmost region does not resize because"
                        + " the split position is set in pixels."
                        + " If you resize the window vertically you will see"
                        + " how the split position ratio works.");
        final Label l3 = new Label(
                "You may resize the left and right regions by dragging the"
                        + " vertical split handle left or right. Resizing the"
                        + " upper and lower regions has been disabled.");
        ts.addTab(l1);
        ts.setTabCaption(l1, "SplitPanel demo");
        ts.addTab(l2);
        ts.setTabCaption(l2, "Resizing");
        ts.addTab(l3);
        ts.setTabCaption(l3, "Resizing the regions");
        ts.setSizeFull();

        // Add the components to the sub-SplitPanel
        subSP.addComponent(ts);
        subSP.addComponent(new Label("Comment area"));

        // Add sub-SplitPanel to main SplitPanel
        demoSP.addComponent(subSP);

        return demoSP;
    }
}
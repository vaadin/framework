package com.vaadin.ui;

import com.vaadin.terminal.gwt.client.ui.VSplitPanelVertical;

/**
 * A vertical split panel contains two components and lays them vertically. The
 * first component is above the second component.
 * 
 * <pre>
 *      +--------------------------+
 *      |                          |
 *      |  The first component     |
 *      |                          |
 *      +==========================+  <-- splitter
 *      |                          |
 *      |  The second component    |
 *      |                          |
 *      +--------------------------+
 * </pre>
 * 
 */
@ClientWidget(VSplitPanelVertical.class)
public class VerticalSplitPanel extends SplitPanel {
    @SuppressWarnings("deprecation")
    public VerticalSplitPanel() {
        super(ORIENTATION_VERTICAL);
    }
}

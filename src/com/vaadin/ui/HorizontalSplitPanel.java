package com.vaadin.ui;

import com.vaadin.terminal.gwt.client.ui.VSplitPanelHorizontal;

/**
 * A horizontal split panel contains two components and lays them horizontally.
 * The first component is on the left side.
 * 
 * <pre>
 * 
 *      +---------------------++----------------------+
 *      |                     ||                      |
 *      | The first component || The second component |
 *      |                     ||                      |
 *      +---------------------++----------------------+
 *                              
 *                            ^
 *                            |
 *                      the splitter
 * 
 * </pre>
 */
@ClientWidget(VSplitPanelHorizontal.class)
public class HorizontalSplitPanel extends SplitPanel {
    @SuppressWarnings("deprecation")
    public HorizontalSplitPanel() {
        super(ORIENTATION_HORIZONTAL);
    }
}

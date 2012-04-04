/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.ui;


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
public class VerticalSplitPanel extends AbstractSplitPanel {

    public VerticalSplitPanel() {
        super();
        setSizeFull();
    }

}

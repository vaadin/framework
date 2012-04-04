/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.ui;


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
 * 
 * @author Vaadin Ltd.
 * @version
 * @VERSION@
 * @since 6.5
 */
public class HorizontalSplitPanel extends AbstractSplitPanel {
    public HorizontalSplitPanel() {
        super();
        setSizeFull();
    }
}

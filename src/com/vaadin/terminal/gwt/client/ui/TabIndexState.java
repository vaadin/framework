package com.vaadin.terminal.gwt.client.ui;

/**
 * Interface implemented by state classes that support tab indexes.
 * 
 * @author Vaadin Ltd
 * @version @VERSION@
 * @since 7.0.0
 * 
 */
public interface TabIndexState {
    /**
     * Gets the <i>tabulator index</i> of the field.
     * 
     * @return the tab index for the Field
     */
    public int getTabIndex();

    /**
     * Sets the <i>tabulator index</i> of the field.
     * 
     * @param tabIndex
     *            the tab index to set
     */
    public void setTabIndex(int tabIndex);
}

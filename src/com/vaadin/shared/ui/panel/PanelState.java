/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.shared.ui.panel;

import com.vaadin.shared.ComponentState;

public class PanelState extends ComponentState {
    private int tabIndex;
    private int scrollLeft, scrollTop;

    public int getTabIndex() {
        return tabIndex;
    }

    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
    }

    public int getScrollLeft() {
        return scrollLeft;
    }

    public void setScrollLeft(int scrollLeft) {
        this.scrollLeft = scrollLeft;
    }

    public int getScrollTop() {
        return scrollTop;
    }

    public void setScrollTop(int scrollTop) {
        this.scrollTop = scrollTop;
    }

}
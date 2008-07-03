/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

import com.itmill.toolkit.terminal.gwt.client.ContainerResizedListener;

public class IOrderedLayoutHorizontal extends IOrderedLayout implements
        ContainerResizedListener {
    /*
     * private String height; private boolean relativeHeight; private final int
     * marginHeight = 0;
     * 
     * public IOrderedLayoutHorizontal() { super(ORIENTATION_HORIZONTAL); }
     */
    /*
     * public void setHeight(String newHeight) { super.setHeight(newHeight); if
     * (newHeight != null && !newHeight.equals("")) { if
     * (!newHeight.equals(height)) { height = newHeight; if
     * (newHeight.indexOf("%") > 0) { relativeHeight = true;
     * DOM.setStyleAttribute(getElement(), "overflow", "hidden"); } else {
     * relativeHeight = false; DOM.setStyleAttribute(getElement(), "overflow",
     * ""); } setInternalHeight(); } } else { if (newHeight != null) { // clear
     * existing height values DOM.setStyleAttribute(getElement(), "overflow",
     * ""); DOM.setStyleAttribute(DOM.getFirstChild(margin), "height", "");
     * 
     * newHeight = null; relativeHeight = false; } } }
     * 
     * protected void handleMargins(UIDL uidl) { super.handleMargins(uidl); if
     * (height != null) { marginHeight = -1; setInternalHeight(); } }
     * 
     * private void setInternalHeight() { int availSpace = DOM
     * .getElementPropertyInt(getElement(), "clientHeight"); if (marginHeight <
     * 0) { DOM.setStyleAttribute(margin, "height", height); int tmp =
     * DOM.getElementPropertyInt(margin, "offsetHeight"); marginHeight = tmp -
     * DOM.getElementPropertyInt(getElement(), "clientHeight");
     * DOM.setStyleAttribute(margin, "height", ""); }
     * 
     * availSpace -= marginHeight;
     * 
     * DOM.setStyleAttribute(DOM.getFirstChild(margin), "height", availSpace +
     * "px"); }
     * 
     * public void iLayout() { if (relativeHeight) { setInternalHeight(); }
     * Util.runDescendentsLayout(this); }
     */
}

/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.BrowserInfo;
import com.itmill.toolkit.terminal.gwt.client.Container;
import com.itmill.toolkit.terminal.gwt.client.ContainerResizedListener;
import com.itmill.toolkit.terminal.gwt.client.ICaption;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;
import com.itmill.toolkit.terminal.gwt.client.Util;
import com.itmill.toolkit.terminal.gwt.client.Util.WidgetSpaceAllocator;

/**
 * Full implementation of OrderedLayout client peer.
 * 
 * This class implements all features of OrderedLayout. It currently only
 * supports use through UIDL updates. Direct client side use is not (currently)
 * suported in all operation modes.
 * 
 * 
 * <h2>Features</h2>
 * 
 * <h3>Orientation</h3>
 * 
 * <p>
 * Orientation of the ordered layout declared whether the children are layouted
 * horizontally or vertically.
 * </p>
 * 
 * <img src="doc-files/IOrderedLayout_horizontal.png"/> <img
 * src="doc-files/IOrderedLayout_vertical.png"/>
 * 
 * <h3>Spacing</h3>
 * 
 * <p>
 * Spacing determines if there should be space between the children. Note that
 * this does not imply margin.
 * </p>
 * 
 * <img src="doc-files/IOrderedLayout_horizontal_spacing.png"/> <img
 * src="doc-files/IOrderedLayout_vertical_spacing.png"/>
 * 
 * <h3>Margin</h3>
 * 
 * <p>
 * Margin determines if there should be margin around children. Note that this
 * does not imply spacing.
 * </p>
 * 
 * <img src="doc-files/IOrderedLayout_margin.png"/>
 * 
 * <h3>Positioning the caption, icon, required indicator and error</h3>
 * 
 * <p>
 * If the child lets the layout to handle captions, by icon, caption, required
 * marker (*) and error icon are placed on top of the component area. Icon will
 * be first and is followed by the caption. Required marker is placed right
 * after the caption text and error icon is placed last. Note that all of these
 * are optional:
 * </p>
 * 
 * <img src="doc-files/IOrderedLayout_normal_caption.png"/>
 * 
 * <p>
 * If the child lets the layout to handle captions, but the caption and icon are
 * both missing, no line is reserved for the required marker (*) and error icon.
 * Instead they are placed on the right side of the top of the component area.
 * Required marker is placed right after the component text and error icon is
 * placed last. If the component is tall, the indicators are aligned along the
 * top of the component. Note that both of these indicators are optional:
 * </p>
 * 
 * <img src="doc-files/IOrderedLayout_no_caption.png"/>
 * 
 * <p>
 * In case the child want to handle the caption by itself, layout does not
 * repeat the caption.
 * </p>
 * 
 * <img src="doc-files/IOrderedLayout_component_handles_the_caption.png"/>
 * 
 * <h3>Aligning the children</h3>
 * 
 * <p>
 * The children of the layout can be aligned horizontally and vertically:
 * </p>
 * 
 * <img src="doc-files/IOrderedLayout_alignment.png"/>
 * 
 * <h3>Fixed height, width or both</h3>
 * 
 * <p>
 * When no size is explicitly specified, the size of the layout depends on the
 * size of its children. If the size if specified, either explicitly or as
 * percertages of the parent size, the size is equally divided between the
 * children. In case some children might overflow out of the given space, they
 * are cut to fit the given space. Note that the size can be independently
 * specified for horizontal and vertical dimensions and is independent of the
 * orientation. For example, layout can be horizontal and have fixed 300px
 * height, but still measure its width from the child sizes.
 * </p>
 * 
 * <p>
 * Horizontal layout with fixed width of 300px and height of 150px:
 * </p>
 * <img src="doc-files/IOrderedLayout_w300_h150.png"/>
 * 
 * <p>
 * Horizontal layout with fixed width of 300px:
 * </p>
 * <img src="doc-files/IOrderedLayout_w300.png"/>
 * 
 * <p>
 * Horizontal layout with fixed height of 150px:
 * </p>
 * <img src="doc-files/IOrderedLayout_h150.png"/>
 * 
 * 
 * <h3>CSS attributes</h3>
 * 
 * <p>
 * Sizes for marginals and spacing can be specified for the ordered layout in
 * CSS. For example, here are the defaults for OrderedLayout:
 * </p>
 * 
 * <pre>
 * .i-orderedlayout-margin-top {
 *         padding-top: 15px;
 * }
 * .i-orderedlayout-margin-right {
 *         padding-right: 18px;
 * }
 * .i-orderedlayout-margin-bottom {
 *         padding-bottom: 15px;
 * }
 * .i-orderedlayout-margin-left {
 *         padding-left: 18px;
 * }
 * 
 * .i-orderedlayout-vspacing {
 *         margin-top: 8px;
 * }
 * .i-orderedlayout-hspacing {
 *         padding-left: 8px;
 * }
 * </pre>
 * 
 * <p>
 * When a style-name is set for the layout, this name is included in the style.
 * Note that the unspecified dimensions still default to the values given for
 * the layout without style. For example, if we would like to give each layout
 * with "tested-layout" style quite a bit larger right margin:
 * </p>
 * 
 * <pre>
 * .i-orderedlayout-tested-layout-margin-right {
 *         padding-right: 100px;
 * }
 * </pre>
 * 
 * <p>
 * Here is the rendering with getMargin(true). Note that all the other margins
 * are set to the default values defined for the layout without stylename:
 * </p>
 * <img src="doc-files/IOrderedLayout_special-margin.png"/>
 * 
 * 
 * <h3>DOM-structure</h3>
 * 
 * Note that DOM-structure is an implementation specific and might change in the
 * future versions of IT Mill Toolkit. The purpose of this documentation is to
 * to ease reading of the implementation and thus to make implementation of your
 * own layouts easier.
 * 
 * <div style="border: 1px solid black; padding: 3px;">OUTERDIV
 * 
 * <div style="border: 1px solid black; padding: 3px;">Optional STRUCTURE
 * 
 * <div style="border: 1px solid black; padding: 3px;">CHILDWRAPPER (for each
 * child)
 * 
 * <div style="border: 1px solid black; padding: 3px;">Optional ALIGNMENTWRAPPER
 * 
 * <div style="border: 1px solid black; padding: 3px;">Optional CLIPPER
 * 
 * <div style="border: 1px solid black; padding: 3px;">CAPTION <span
 * style="border: 1px solid black; padding: 3px;">ICON-IMG</span> <span
 * style="border: 1px solid black; padding: 3px;">CAPTION-SPAN</span> <span
 * style="border: 1px solid black; padding: 3px;">REQUIRED-SPAN</span> <span
 * style="border: 1px solid black; padding: 3px;">ERRORINDICATOR-DIV</span>
 * </div>
 * 
 * <div style="border: 1px solid black; padding: 3px; margin-top:3px;">Widget
 * component</div>
 * 
 * </div></div></div>
 * 
 * </div></div>
 * 
 * <p>
 * Notes:
 * <ul>
 * <li>If caption and icon are missing from child, <i>Widget component</i> and
 * <i>CAPTION</i> elements are swithched</li>
 * <li>If either child manages caption, or it has no caption, icon, required or
 * error, <i>CAPTION</i> element is not needed at all</li>
 * <li>If layout is vertical and its width is specified, <i>Optional
 * STRUCTURE</i> is not present. Otherwise it looks like <div
 * style="border: 1px solid black; padding: 3px;">TABLE <div
 * style="border: 1px solid black; padding: 3px;">TBODY <div
 * style="border: 1px solid black; padding: 3px;">Optional TR only included in
 * case of horizontal layouts </div></div></div></li>
 * <li><i>CHILDWRAPPER</i> is a DIV in case of the layout is vertical and width
 * is specified. For vertical layouts with unknown width it is TR-TD. For
 * horizontal layouts, it is TR-TD.</li>
 * <li><i>Optionasl ALIGNMENTWRAPPER</i> are only used alignment is not the
 * default - top-left. Alignment wrapper structure is
 * TABLE-TBODY-TR-TD-TABLE-TBODY-TR-TD, where the outer table td is used to
 * specify the alignments and inner table td to reset the table defaults to
 * top-left.</li>
 * <li><i>Optional CLIPPERDIV</i> included in the structure only if alignment
 * structure is in place and <i>CHILDWRAPPER</i> is not a div and thus can not
 * be used for clipping</li>
 * </ul>
 * </p>
 * 
 * 
 * @author IT Mill Ltd
 */
public class IOrderedLayout extends Panel implements Container,
        ContainerResizedListener, WidgetSpaceAllocator {

    public static final String CLASSNAME = "i-orderedlayout";

    public static final int ORIENTATION_VERTICAL = 0;
    public static final int ORIENTATION_HORIZONTAL = 1;

    /**
     * If margin and spacing values has been calculated, this holds the values
     * for the given UIDL style attribute .
     */
    private static HashMap measuredMargins = new HashMap();

    /**
     * Spacing. Correct values will be set in
     * updateMarginAndSpacingFromCSS(UIDL)
     */
    private int hSpacing, vSpacing;

    /**
     * Margin. Correct values will be set in updateMarginAndSpacingFromCSS(UIDL)
     */
    private int marginTop, marginBottom, marginLeft, marginRight;

    int orientationMode = ORIENTATION_VERTICAL;

    protected ApplicationConnection client;

    /**
     * Reference to Element where wrapped childred are contained. Normally a
     * DIV, TR or a TBODY element.
     */
    private Element wrappedChildContainer;

    /**
     * List of child widgets. This is not the list of wrappers, but the actual
     * widgets
     */
    private final Vector childWidgets = new Vector();

    /**
     * In table mode, the root element is table instead of div.
     */
    private boolean tableMode = false;

    /**
     * Root element. This element points to the outmost table-element (in table
     * mode) or outmost div (in non-table-mode). This non-table-mode this equals
     * to the getElement().
     */
    private Element root = null;

    /**
     * Last set width of the component. Null if undefined (instead of being "").
     */
    private String width = null;

    /**
     * Last set height of the component. Null if undefined (instead of being
     * "").
     */
    private String height = null;
    /**
     * List of child widget wrappers. These wrappers are in exact same indexes
     * as the widgets in childWidgets list.
     */
    private final Vector<WidgetWrapper> childWidgetWrappers = new Vector<WidgetWrapper>();

    /** Whether the component has spacing enabled. */
    private boolean hasComponentSpacing;

    /** Information about margin states. */
    private MarginInfo margins = new MarginInfo(0);

    /**
     * Flag that indicates that the child layouts must be updated as soon as
     * possible. This will be done in the end of updateFromUIDL.
     */
    private boolean childLayoutsHaveChanged = false;

    private int renderedHeight;

    private int renderedWidth;

    /**
     * Construct the DOM of the orderder layout.
     * 
     * <p>
     * There are two modes - vertical and horizontal.
     * <ul>
     * <li>Vertical mode uses structure: div-root ( div-wrap ( child ) div-wrap
     * ( child ))).</li>
     * <li>Horizontal mode uses structure: table ( tbody ( tr-childcontainer (
     * td-wrap ( child ) td-wrap ( child) )) )</li>
     * </ul>
     * where root and childcontainer refer to the root element and the element
     * that contain WidgetWrappers.
     * </p>
     * 
     */
    public IOrderedLayout() {
        wrappedChildContainer = root = DOM.createDiv();
        setElement(wrappedChildContainer);
        setStyleName(CLASSNAME);
    }

    /**
     * Update orientation, if it has changed.
     * 
     * @param newOrientationMode
     */
    private void rebuildRootDomStructure(int oldOrientationMode) {

        // Should we have table as a root element?
        boolean newTableMode = !(orientationMode == ORIENTATION_VERTICAL && width != null);

        // Already in correct mode?
        if (oldOrientationMode == orientationMode && newTableMode == tableMode) {
            return;
        }
        boolean oldTableMode = tableMode;
        tableMode = newTableMode;

        /*
         * If the child are not detached before the parent is cleared with
         * setInnerHTML the children will also be cleared in IE
         */
        if (BrowserInfo.get().isIE()) {
            while (true) {
                Element child = DOM.getFirstChild(getElement());
                if (child != null) {
                    DOM.removeChild(getElement(), child);
                } else {
                    break;
                }
            }
        }

        // Constuct base DOM-structure and clean any already attached
        // widgetwrappers from DOM.
        if (tableMode) {
            String structure = "<table cellspacing=\"0\" cellpadding=\"0\"";

            if (orientationMode == ORIENTATION_HORIZONTAL) {
                // Needed for vertical alignment to work
                structure += " height=\"100%\"";
            }
            structure += "><tbody>"
                    + (orientationMode == ORIENTATION_HORIZONTAL ? "<tr valign=\"top\"></tr>"
                            : "") + "</tbody></table>";

            DOM.setInnerHTML(getElement(), structure);
            root = DOM.getFirstChild(getElement());
            // set TBODY to be the wrappedChildContainer
            wrappedChildContainer = DOM.getFirstChild(root);
            // In case of horizontal layouts, we must user TR instead of TBODY
            if (orientationMode == ORIENTATION_HORIZONTAL) {
                wrappedChildContainer = DOM
                        .getFirstChild(wrappedChildContainer);
            }
        } else {
            root = wrappedChildContainer = getElement();
            DOM.setInnerHTML(getElement(), "");
        }

        // Reinsert all widget wrappers to this container
        final int currentOrientationMode = orientationMode;
        for (int i = 0; i < childWidgetWrappers.size(); i++) {
            WidgetWrapper wr = childWidgetWrappers.get(i);
            orientationMode = oldOrientationMode;
            tableMode = oldTableMode;
            Element oldWrElement = wr.getElementWrappingWidgetAndCaption();
            orientationMode = currentOrientationMode;
            tableMode = newTableMode;
            String classe = DOM.getElementAttribute(oldWrElement, "class");
            wr.resetRootElement();
            Element newWrElement = wr.getElementWrappingWidgetAndCaption();
            if (classe != null && classe.length() > 0) {
                DOM.setElementAttribute(newWrElement, "class", classe);
            }
            while (DOM.getChildCount(oldWrElement) > 0) {
                Element c = DOM.getFirstChild(oldWrElement);
                DOM.removeChild(oldWrElement, c);
                DOM.appendChild(newWrElement, c);
            }

            DOM.appendChild(wrappedChildContainer, wr.getElement());
        }

        // Update child layouts
        childLayoutsHaveChanged = true;
    }

    /** Update the contents of the layout from UIDL. */
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {

        this.client = client;

        // Only non-cached UIDL:s can introduce changes
        if (uidl.getBooleanAttribute("cached")) {
            return;
        }

        updateMarginAndSpacingSizesFromCSS(uidl);

        // Update sizes, ...
        if (client.updateComponent(this, uidl, true)) {
            return;
        }

        // Rebuild DOM tree root if necessary
        int oldO = orientationMode;
        orientationMode = "horizontal".equals(uidl
                .getStringAttribute("orientation")) ? ORIENTATION_HORIZONTAL
                : ORIENTATION_VERTICAL;
        rebuildRootDomStructure(oldO);

        // Handle component spacing later in handleAlignments() method
        hasComponentSpacing = uidl.getBooleanAttribute("spacing");

        // Collect the list of contained widgets after this update
        final Vector newWidgets = new Vector();
        for (final Iterator it = uidl.getChildIterator(); it.hasNext();) {
            final UIDL uidlForChild = (UIDL) it.next();
            final Paintable child = client.getPaintable(uidlForChild);
            newWidgets.add(child);
        }

        // Iterator for old widgets
        final Iterator oldWidgetsIterator = (new Vector(childWidgets))
                .iterator();

        // Iterator for new widgets
        final Iterator newWidgetsIterator = newWidgets.iterator();

        // Iterator for new UIDL
        final Iterator newUIDLIterator = uidl.getChildIterator();

        // List to collect all now painted widgets to in order to remove
        // unpainted ones later
        final Vector paintedWidgets = new Vector();

        final Vector childsToPaint = new Vector();

        // Add any new widgets to the ordered layout
        Widget oldChild = null;
        while (newWidgetsIterator.hasNext()) {

            final Widget newChild = (Widget) newWidgetsIterator.next();
            final UIDL newChildUIDL = (UIDL) newUIDLIterator.next();

            // Remove any unneeded old widgets
            if (oldChild == null && oldWidgetsIterator.hasNext()) {
                // search for next old Paintable which still exists in layout
                // and delete others
                while (oldWidgetsIterator.hasNext()) {
                    oldChild = (Widget) oldWidgetsIterator.next();
                    // now oldChild is an instance of Paintable
                    if (paintedWidgets.contains(oldChild)) {
                        continue;
                    } else if (newWidgets.contains(oldChild)) {
                        break;
                    } else {
                        remove(oldChild);
                        oldChild = null;
                    }
                }
            }

            if (oldChild == null) {
                // we are adding components to the end of layout
                add(newChild);
            } else if (newChild == oldChild) {
                // child already attached in correct position
                oldChild = null;
            } else if (hasChildComponent(newChild)) {

                // current child has been moved, re-insert before current
                // oldChild
                add(newChild, childWidgets.indexOf(oldChild));

            } else {
                // insert new child before old one
                add(newChild, childWidgets.indexOf(oldChild));
            }

            // Update the child component
            childsToPaint.add(new Object[] { newChild, newChildUIDL });

            // Add this newly handled component to the list of painted
            // components
            paintedWidgets.add(newChild);
        }

        // Remove possibly remaining old widgets which were not in painted UIDL
        while (oldWidgetsIterator.hasNext()) {
            oldChild = (Widget) oldWidgetsIterator.next();
            if (!newWidgets.contains(oldChild)) {
                remove(oldChild);
            }
        }

        // Handle component alignments
        handleAlignmentsSpacingAndMargins(uidl);

        // Reset sizes for the children
        updateChildSizes(-1, -1);

        // Paint children
        for (int i = 0; i < childsToPaint.size(); i++) {
            Object[] t = (Object[]) childsToPaint.get(i);
            ((Paintable) t[0]).updateFromUIDL((UIDL) t[1], client);
        }

        // Update child layouts
        // TODO This is most probably unnecessary and should be done within
        // update Child H/W
        if (childLayoutsHaveChanged) {
            Util.runDescendentsLayout(this);
            childLayoutsHaveChanged = false;
        }

        /* Store the rendered size so we later can see if it has changed */
        renderedWidth = root.getOffsetWidth();
        renderedHeight = root.getOffsetHeight();

    }

    private void updateMarginAndSpacingSizesFromCSS(UIDL uidl) {

        // Style for this layout
        String style = uidl.getStringAttribute("style");
        if (style == null) {
            style = "";
        }

        // Try to find measured from cache
        int[] r = (int[]) measuredMargins.get(style);

        // Measure from DOM
        if (r == null) {
            r = new int[] { 0, 0, 0, 0, 0, 0 };

            // Construct DOM for measurements
            Element e1 = DOM.createTable();
            DOM.setStyleAttribute(e1, "position", "absolute");
            DOM.setElementProperty(e1, "cellSpacing", "0");
            DOM.setElementProperty(e1, "cellPadding", "0");
            Element e11 = DOM.createTBody();
            Element e12 = DOM.createTR();
            Element e13 = DOM.createTD();
            Element e2 = DOM.createDiv();
            Element e3 = DOM.createDiv();
            DOM.setStyleAttribute(e3, "width", "100px");
            DOM.setStyleAttribute(e3, "height", "100px");
            DOM.appendChild(getElement(), e1);
            DOM.appendChild(e1, e11);
            DOM.appendChild(e11, e12);
            DOM.appendChild(e12, e13);
            DOM.appendChild(e13, e2);
            DOM.appendChild(e2, e3);
            DOM.setInnerText(e3, ".");

            // Measure different properties
            final String[] classes = { "margin-top", "margin-right",
                    "margin-bottom", "margin-left", "vspacing", "hspacing" };
            for (int c = 0; c < 6; c++) {
                StringBuffer styleBuf = new StringBuffer();
                final String primaryName = getStylePrimaryName();
                styleBuf.append(primaryName + "-" + classes[c]);
                if (style.length() > 0) {
                    final String[] styles = style.split(" ");
                    for (int i = 0; i < styles.length; i++) {
                        styleBuf.append(" ");
                        styleBuf.append(primaryName);
                        styleBuf.append("-");
                        styleBuf.append(styles[i]);
                        styleBuf.append("-");
                        styleBuf.append(classes[c]);
                    }
                }
                DOM.setElementProperty(e2, "className", styleBuf.toString());

                // Measure
                r[c] = DOM.getElementPropertyInt(e1,
                        (c % 2) == 1 ? "offsetWidth" : "offsetHeight") - 100;
            }

            // Clean-up
            DOM.removeChild(getElement(), e1);

            // Cache for further use
            measuredMargins.put(style, r);
        }

        // Set the properties
        marginTop = r[0];
        marginRight = r[1];
        marginBottom = r[2];
        marginLeft = r[3];
        vSpacing = r[4];
        hSpacing = r[5];
    }

    /**
     * While setting width, ensure that margin div is also resized properly.
     * Furthermore, enable/disable fixed mode
     */
    public void setWidth(String newWidth) {

        width = newWidth == null || "".equals(newWidth) ? null : newWidth;

        // As we use divs at root - for them using 100% width should be
        // calculated with ""
        super.setWidth("");

        // Update child layouts
        childLayoutsHaveChanged = true;
    }

    /**
     * While setting height, ensure that margin div is also resized properly.
     * Furthermore, enable/disable fixed mode
     */
    public void setHeight(String newHeight) {
        super.setHeight(newHeight);
        height = newHeight == null || "".equals(newHeight) ? null : newHeight;

        // Update child layouts
        childLayoutsHaveChanged = true;
    }

    /** Recalculate and apply the space given for each child in this layout. */
    private void updateChildSizes(int renderedWidth, int renderedHeight) {

        int numChild = childWidgets.size();
        int childHeightTotal = -1;
        int childHeightDivisor = 1;
        int childWidthTotal = -1;
        int childWidthDivisor = 1;

        // Vertical layout is calculated by us
        if (height != null) {

            // Calculate the space for fixed contents minus marginals
            if (tableMode) {

                // If we know explicitly set pixel-size, use that
                if (height.endsWith("px")) {
                    try {
                        childHeightTotal = Integer.parseInt(height.substring(0,
                                height.length() - 2));

                        // For negative sizes, use measurements
                        if (childHeightTotal < 0) {
                            childHeightTotal = rootOffsetMeasure("offsetHeight");
                        }
                    } catch (NumberFormatException e) {

                        // In case of invalid number, try to measure the size;
                        childHeightTotal = rootOffsetMeasure("offsetHeight");
                    }
                } else if (height.endsWith("%") && renderedHeight >= 0) {
                    // If we have a relative height and know how large we are we
                    // can
                    // simply use that
                    childWidthTotal = renderedHeight;
                } else {
                    // If not pixels, nor percentage, try to measure the size
                    childHeightTotal = rootOffsetMeasure("offsetHeight");
                }

            } else {
                childHeightTotal = DOM.getElementPropertyInt(getElement(),
                        "offsetHeight");
            }

            childHeightTotal -= margins.hasTop() ? marginTop : 0;
            childHeightTotal -= margins.hasBottom() ? marginBottom : 0;

            // Reduce spacing from the size
            if (hasComponentSpacing) {
                childHeightTotal -= ((orientationMode == ORIENTATION_HORIZONTAL) ? hSpacing
                        : vSpacing)
                        * (numChild - 1);
            }

            // Total space is divided among the children
            if (orientationMode == ORIENTATION_VERTICAL) {
                childHeightDivisor = numChild;
            }
        }

        // layout is calculated by us
        if (width != null) {

            // Calculate the space for fixed contents minus marginals
            // If we know explicitly set pixel-size, use that
            if (width.endsWith("px")) {
                try {
                    childWidthTotal = Integer.parseInt(width.substring(0, width
                            .length() - 2));

                    // For negative sizes, use measurements
                    if (childWidthTotal < 0) {
                        childWidthTotal = rootOffsetMeasure("offsetWidth");
                    }

                } catch (NumberFormatException e) {

                    // In case of invalid number, try to measure the size;
                    childWidthTotal = rootOffsetMeasure("offsetWidth");
                }
            } else if (width.endsWith("%") && renderedWidth >= 0) {
                // If we have a relative width and know how large we are we can
                // simply use that
                childWidthTotal = renderedWidth;
            } else {
                // If not pixels, nor percentage, try to measure the size
                childWidthTotal = rootOffsetMeasure("offsetWidth");
            }

            childWidthTotal -= margins.hasLeft() ? marginLeft : 0;
            childWidthTotal -= margins.hasRight() ? marginRight : 0;

            // Reduce spacing from the size
            if (hasComponentSpacing
                    && orientationMode == ORIENTATION_HORIZONTAL) {
                childWidthTotal -= hSpacing * (numChild - 1);
            }

            // Total space is divided among the children
            if (orientationMode == ORIENTATION_HORIZONTAL) {
                childWidthDivisor = numChild;
            }
        }

        // Set the sizes for each child
        for (Iterator i = childWidgetWrappers.iterator(); i.hasNext();) {
            int w, h;
            if (childHeightDivisor > 1) {
                h = Math.round(((float) childHeightTotal)
                        / (childHeightDivisor--));
                childHeightTotal -= h;
            } else {
                h = childHeightTotal;
            }
            if (childWidthDivisor > 1) {
                w = Math.round(((float) childWidthTotal)
                        / (childWidthDivisor--));
                childWidthTotal -= w;
            } else {
                w = childWidthTotal;
            }
            WidgetWrapper ww = (WidgetWrapper) i.next();
            ww.forceSize(w, h);
        }
    }

    /**
     * Measure how much space the root element could get.
     * 
     * This measures the space allocated by the parent for the root element
     * without letting root element to affect the calculation.
     * 
     * @param offset
     *            offsetWidth or offsetHeight
     */
    private int rootOffsetMeasure(String offset) {
        // TODO This method must be optimized!
        Element measure = DOM.createDiv();
        DOM.setStyleAttribute(measure, "height", "100%");
        Element parent = DOM.getParent(getElement());
        DOM.insertBefore(parent, measure, getElement());
        // FIXME Do not detach from DOM this way. At least proper detach, attach
        // must be called. Affects odd behavior in childs, performance issues
        // and flickering. See #2102
        DOM.removeChild(parent, getElement());
        int size = DOM.getElementPropertyInt(measure, offset);
        DOM.insertBefore(parent, getElement(), measure);
        DOM.removeChild(parent, measure);
        // In case the no space would be given for this element
        // without pushing, use the current side of the root
        return size;
    }

    /** Parse alignments from UIDL and pass whem to correct widgetwrappers */
    private void handleAlignmentsSpacingAndMargins(UIDL uidl) {

        // Only update margins when they have changed
        // TODO this should be optimized to avoid reupdating these
        margins = new MarginInfo(uidl.getIntAttribute("margins"));

        // Component alignments as a comma separated list.
        // See com.itmill.toolkit.terminal.gwt.client.ui.AlignmentInfo.java for
        // possible values.
        final int[] alignments = uidl.getIntArrayAttribute("alignments");
        int alignmentIndex = 0;

        // Insert alignment attributes
        final Iterator it = childWidgetWrappers.iterator();

        while (it.hasNext()) {

            // Calculate alignment info
            final AlignmentInfo ai = new AlignmentInfo(
                    alignments[alignmentIndex++]);

            final WidgetWrapper wr = (WidgetWrapper) it.next();

            wr.setAlignment(ai.getVerticalAlignment(), ai
                    .getHorizontalAlignment());

            // Handle spacing and margins in this loop as well
            wr.setSpacingAndMargins(alignmentIndex == 1,
                    alignmentIndex == alignments.length);
        }
    }

    /**
     * Wrapper around single child in the layout.
     * 
     * This helper also manages spacing, margins and alignment for individual
     * cells handling. It also can put hard size limits for its contens by
     * clipping the content to given pixel size.
     * 
     */
    class WidgetWrapper {

        /**
         * When alignment table structure is used, these elements correspond to
         * the TD elements within the structure. If alignment is not used, these
         * are null.
         */
        Element alignmentTD, innermostTDinAlignmnetStructure;

        /**
         * When clipping must be done and the element wrapping clipped content
         * would be TD instead of DIV, this element points to additional DIV
         * that is used for clipping.
         */
        Element clipperDiv;

        /** Caption element when used. */
        ICaption caption = null;

        /**
         * Last set pixel height for the wrapper. -1 if vertical clipping is not
         * used.
         */
        int lastForcedPixelHeight = -1;

        /**
         * Last set pidel width for the wrapper. -1 if horizontal clipping is
         * not used.
         */
        int lastForcedPixelWidth = -1;

        int horizontalPadding = 0, verticalPadding = 0;

        /** Widget Wrapper root element */
        Element wrapperElement;

        /** Set the root element */
        public WidgetWrapper() {
            resetRootElement();
        }

        public Element getElement() {
            return wrapperElement;
        }

        /**
         * Set the width and height given for the wrapped widget in pixels.
         * 
         * -1 if unconstrained.
         */
        public void forceSize(int pixelWidth, int pixelHeight) {

            // If we are already at the correct size, do nothing
            if (lastForcedPixelHeight == pixelHeight
                    && lastForcedPixelWidth == pixelWidth) {
                return;
            }

            // Clipper DIV is needed?
            if (tableMode && (pixelHeight >= 0 || pixelWidth >= 0)) {
                if (clipperDiv == null) {
                    createClipperDiv();
                }
            }

            // ClipperDiv is not needed, remove if necessary
            else if (clipperDiv != null) {
                removeClipperDiv();
            }

            Element e = clipperDiv != null ? clipperDiv
                    : getElementWrappingAlignmentStructures();

            // Overflow
            DOM.setStyleAttribute(e, "overflow", pixelWidth < 0
                    && pixelHeight < 0 ? "" : "hidden");

            // Set size
            DOM.setStyleAttribute(e, "width", pixelWidth < 0 ? "" : pixelWidth
                    + "px");
            DOM.setStyleAttribute(e, "height",
                    pixelHeight < 0 ? (e == clipperDiv ? "100%" : "")
                            : pixelHeight + "px");

            // Set cached values
            lastForcedPixelWidth = pixelWidth;
            lastForcedPixelHeight = pixelHeight;
        }

        /** Create a DIV for clipping the child */
        private void createClipperDiv() {
            clipperDiv = DOM.createDiv();
            final Element e = getElementWrappingClipperDiv();
            String classe = DOM.getElementAttribute(e, "class");
            while (DOM.getChildCount(e) > 0) {
                final Element c = DOM.getFirstChild(e);
                DOM.removeChild(e, c);
                DOM.appendChild(clipperDiv, c);
            }
            if (classe != null && classe.length() > 0) {
                DOM.removeElementAttribute(e, "class");
                DOM.setElementAttribute(clipperDiv, "class", classe);
            }
            DOM.appendChild(e, clipperDiv);
        }

        /** Undo createClipperDiv() */
        private void removeClipperDiv() {
            final Element e = getElementWrappingClipperDiv();
            String classe = DOM.getElementAttribute(clipperDiv, "class");
            while (DOM.getChildCount(clipperDiv) > 0) {
                final Element c = DOM.getFirstChild(clipperDiv);
                DOM.removeChild(clipperDiv, c);
                DOM.appendChild(e, c);
            }
            DOM.removeChild(e, clipperDiv);
            clipperDiv = null;
            if (classe != null && classe.length() > 0) {
                DOM.setElementAttribute(e, "class", classe);
            }
        }

        /**
         * Get the element containing the caption and the wrapped widget.
         * Returned element can one of the following:
         * <ul>
         * <li>(a) Root DIV of the WrapperElement when not in tableMode</li>
         * <li>(b) TD in just below the root TR of the WrapperElement when in
         * tableMode</li>
         * <li>(c) clipperDiv inside the (a) or (b)</li>
         * <li>(d) The innermost TD within alignment structures located in (a),
         * (b) or (c)</li>
         * </ul>
         * 
         * @return Element described above
         */
        private Element getElementWrappingWidgetAndCaption() {

            // When alignment is used, we will can safely return the innermost
            // TD
            if (innermostTDinAlignmnetStructure != null) {
                return innermostTDinAlignmnetStructure;
            }

            // In all other cases element wrapping the potential alignment
            // structures is the correct one
            return getElementWrappingAlignmentStructures();
        }

        /**
         * Get the element where alignment structures should be placed in if
         * they are in use.
         * 
         * Returned element can one of the following:
         * <ul>
         * <li>(a) Root DIV of the WrapperElement when not in tableMode</li>
         * <li>(b) TD in just below the root TR of the WrapperElement when in
         * tableMode</li>
         * <li>(c) clipperDiv inside the (a) or (b)</li>
         * </ul>
         * 
         * @return Element described above
         */
        private Element getElementWrappingAlignmentStructures() {

            // Clipper DIV wraps the alignment structures if present
            if (clipperDiv != null) {
                return clipperDiv;
            }

            // When Clipper DIV is not used, we just give the element
            // that would wrap it if it would be used
            return getElementWrappingClipperDiv();
        }

        /**
         * Get the element where clipperDiv should be placed in if they it is in
         * use.
         * 
         * Returned element can one of the following:
         * <ul>
         * <li>(a) Root DIV of the WrapperElement when not in tableMode</li>
         * <li>(b) TD in just below the root TR of the WrapperElement when in
         * tableMode</li>
         * </ul>
         * 
         * @return Element described above
         */
        private Element getElementWrappingClipperDiv() {

            // Only vertical layouts in non-table mode use TR as root, for the
            // rest we can safely give root element
            if (!tableMode || orientationMode == ORIENTATION_HORIZONTAL) {
                return wrapperElement;
            }

            // The root is TR, we'll thus give the TD that is immediately within
            // the root
            return DOM.getFirstChild(wrapperElement);
        }

        /**
         * Create tr, td or div - depending on the orientation of the layout and
         * set it as root.
         * 
         * All contents of the wrapper are cleared. Caller is responsible for
         * preserving the contents and moving them into new root.
         * 
         * @return Previous root element.
         */
        private void resetRootElement() {
            // TODO Should we remove the existing element?
            if (tableMode) {
                if (orientationMode == ORIENTATION_HORIZONTAL) {
                    wrapperElement = DOM.createTD();
                } else {
                    wrapperElement = DOM.createTR();
                    DOM.appendChild(wrapperElement, DOM.createTD());
                }
            } else {
                wrapperElement = DOM.createDiv();
                // Apply 'hasLayout' for IE (needed to get accurate dimension
                // calculations)
                if (BrowserInfo.get().isIE()) {
                    DOM.setStyleAttribute(wrapperElement, "zoom", "1");
                }
            }

            // Clear any references to intermediate elements
            clipperDiv = alignmentTD = innermostTDinAlignmnetStructure = null;
        }

        /** Update the caption of the element contained in this wrapper. */
        public void updateCaption(UIDL uidl, Paintable paintable) {

            final Widget widget = (Widget) paintable;
            final Element captionWrapper = getElementWrappingWidgetAndCaption();

            // The widget needs caption
            if (ICaption.isNeeded(uidl)) {

                // If the caption element is missing, create it
                boolean justAdded = false;
                if (caption == null) {
                    justAdded = true;
                    caption = new ICaption(paintable, client);
                }

                // Update caption contents
                caption.updateCaption(uidl);

                final boolean after = caption.shouldBePlacedAfterComponent();
                final Element captionElement = caption.getElement();
                final Element widgetElement = widget.getElement();

                if (justAdded) {

                    // As the caption has just been created, insert it to DOM
                    if (after) {
                        DOM.appendChild(captionWrapper, captionElement);
                        DOM.setElementAttribute(captionWrapper, "class",
                                "i-orderedlayout-w");
                        caption.addStyleName("i-orderedlayout-c");
                        widget.addStyleName("i-orderedlayout-w-e");
                    } else {
                        DOM.insertChild(captionWrapper, captionElement, 0);
                    }

                } else

                // Caption exists. Move it to correct position if needed
                if (after == (DOM.getChildIndex(captionWrapper, widgetElement) > DOM
                        .getChildIndex(captionWrapper, captionElement))) {
                    Element firstElement = DOM.getChild(captionWrapper, DOM
                            .getChildCount(captionWrapper) - 2);
                    if (firstElement != null) {
                        DOM.removeChild(captionWrapper, firstElement);
                        DOM.appendChild(captionWrapper, firstElement);
                    }
                    DOM.setElementAttribute(captionWrapper, "class",
                            after ? "i-orderedlayout-w" : "");
                    if (after) {
                        caption.addStyleName("i-orderedlayout-c");
                        widget.addStyleName("i-orderedlayout-w-e");
                    } else {
                        widget.removeStyleName("i-orderedlayout-w-e");
                        caption.removeStyleName("i-orderedlayout-w-c");
                    }
                }

            }

            // Caption is not needed
            else {

                // Remove existing caption from DOM
                if (caption != null) {
                    DOM.removeChild(captionWrapper, caption.getElement());
                    caption = null;
                    DOM.setElementAttribute(captionWrapper, "class", "");
                    widget.removeStyleName("i-orderedlayout-w-e");
                }
            }
        }

        /**
         * Set alignments for this wrapper.
         */
        void setAlignment(String verticalAlignment, String horizontalAlignment) {

            // use one-cell table to implement horizontal alignments, only
            // for values other than top-left (which is default)
            if (!horizontalAlignment.equals("left")
                    || !verticalAlignment.equals("top")) {

                // The previous positioning has been left (or unspecified).
                // Thus we need to create a one-cell-table to position
                // this element.
                if (alignmentTD == null) {

                    // Store and remove the current childs (widget and caption)
                    Element c1 = DOM
                            .getFirstChild(getElementWrappingWidgetAndCaption());
                    if (c1 != null) {
                        DOM.removeChild(getElementWrappingWidgetAndCaption(),
                                c1);
                    }
                    Element c2 = DOM
                            .getFirstChild(getElementWrappingWidgetAndCaption());
                    if (c2 != null) {
                        DOM.removeChild(getElementWrappingWidgetAndCaption(),
                                c2);
                    }

                    // Construct table structure to align children
                    final String t = "<table cellpadding='0' cellspacing='0' width='100%' height='100%'><tbody><tr><td>"
                            + "<table cellpadding='0' cellspacing='0' ><tbody><tr><td align='left'>"
                            + "</td></tr></tbody></table></td></tr></tbody></table>";
                    DOM.setInnerHTML(getElementWrappingWidgetAndCaption(), t);
                    alignmentTD = DOM
                            .getFirstChild(DOM
                                    .getFirstChild(DOM
                                            .getFirstChild(DOM
                                                    .getFirstChild(getElementWrappingWidgetAndCaption()))));
                    innermostTDinAlignmnetStructure = DOM.getFirstChild(DOM
                            .getFirstChild(DOM.getFirstChild(DOM
                                    .getFirstChild(alignmentTD))));

                    // Restore children inside the
                    if (c1 != null) {
                        DOM.appendChild(innermostTDinAlignmnetStructure, c1);
                        if (c2 != null) {
                            DOM
                                    .appendChild(
                                            innermostTDinAlignmnetStructure, c2);
                        }
                    }

                } else {

                    // Go around optimization bug in WebKit and ensure repaint
                    if (BrowserInfo.get().isSafari()) {
                        String prevValue = DOM.getElementAttribute(alignmentTD,
                                "align");
                        if (!horizontalAlignment.equals(prevValue)) {
                            Element parent = DOM.getParent(alignmentTD);
                            DOM.removeChild(parent, alignmentTD);
                            DOM.appendChild(parent, alignmentTD);
                        }
                    }

                }

                // Set the alignment in td
                DOM.setElementAttribute(alignmentTD, "align",
                        horizontalAlignment);
                DOM.setElementAttribute(alignmentTD, "valign",
                        verticalAlignment);

            } else {

                // In this case we are requested to position this left
                // while as it has had some other position in the past.
                // Thus the one-cell wrapper table must be removed.
                if (alignmentTD != null) {

                    // Move content to main container
                    final Element itd = innermostTDinAlignmnetStructure;
                    final Element alignmentTable = DOM.getParent(DOM
                            .getParent(DOM.getParent(alignmentTD)));
                    final Element target = DOM.getParent(alignmentTable);
                    while (DOM.getChildCount(itd) > 0) {
                        Element content = DOM.getFirstChild(itd);
                        if (content != null) {
                            DOM.removeChild(itd, content);
                            DOM.appendChild(target, content);
                        }
                    }

                    // Remove unneeded table element
                    DOM.removeChild(target, alignmentTable);

                    alignmentTD = innermostTDinAlignmnetStructure = null;
                }
            }
        }

        /** Set class for spacing */
        void setSpacingAndMargins(boolean first, boolean last) {

            final Element e = getElementWrappingWidgetAndCaption();

            int paddingLeft = 0, paddingRight = 0, paddingTop = 0, paddingBottom = 0;

            if (orientationMode == ORIENTATION_HORIZONTAL) {
                if (first) {
                    if (margins.hasLeft()) {
                        paddingLeft = marginLeft;
                    }
                } else if (hasComponentSpacing) {
                    paddingLeft = hSpacing;
                }

                if (last) {
                    if (margins.hasRight()) {
                        paddingRight = marginRight;
                    }
                }

                if (margins.hasTop()) {
                    paddingTop = marginTop;
                }
                if (margins.hasBottom()) {
                    paddingBottom = marginBottom;
                }

            } else {
                if (margins.hasLeft()) {
                    paddingLeft = marginLeft;
                }
                if (margins.hasRight()) {
                    paddingRight = marginRight;
                }

                if (first) {
                    if (margins.hasTop()) {
                        paddingTop = marginTop;
                    }
                } else if (hasComponentSpacing) {
                    paddingTop = vSpacing;
                }
                if (last && margins.hasBottom()) {
                    paddingBottom = marginBottom;
                }

            }

            horizontalPadding = paddingLeft + paddingRight;
            verticalPadding = paddingTop + paddingBottom;

            DOM.setStyleAttribute(e, "paddingLeft", paddingLeft + "px");
            DOM.setStyleAttribute(e, "paddingRight", paddingRight + "px");

            DOM.setStyleAttribute(e, "paddingTop", paddingTop + "px");
            DOM.setStyleAttribute(e, "paddingBottom", paddingBottom + "px");
        }

        public int getAllocatedHeight() {
            if (lastForcedPixelHeight == -1) {
                if (height == null) {
                    /*
                     * We have no height specified so return the space allocated
                     * by components so far
                     */
                    return getElementWrappingClipperDiv().getOffsetHeight()
                            - horizontalPadding;
                }

                return -1;
            }

            int available = lastForcedPixelHeight;
            // Must remove caption height to report correct size to child
            if (caption != null) {
                available -= caption.getOffsetHeight();
            }
            return available;
        }

        public int getAllocatedWidth() {
            if (width == null) {
                /*
                 * We have no width specified so return the space allocated by
                 * components so far
                 */
                return getElementWrappingClipperDiv().getOffsetWidth()
                        - horizontalPadding;
            }

            return lastForcedPixelWidth;
        }
    }

    /* documented at super */
    public void add(Widget child) {
        add(child, childWidgets.size());
    }

    /**
     * Add widget to this layout at given position.
     * 
     * This methods supports reinserting exiting child into layout - it just
     * moves the position of the child in the layout.
     */
    public void add(Widget child, int atIndex) {
        /*
         * <b>Validate:</b> Perform any sanity checks to ensure the Panel can
         * accept a new Widget. Examples: checking for a valid index on
         * insertion; checking that the Panel is not full if there is a max
         * capacity.
         */
        if (atIndex < 0 || atIndex > childWidgets.size()) {
            return;
        }

        /*
         * <b>Adjust for Reinsertion:</b> Some Panels need to handle the case
         * where the Widget is already a child of this Panel. Example: when
         * performing a reinsert, the index might need to be adjusted to account
         * for the Widget's removal. See {@link ComplexPanel#adjustIndex(Widget,
         * int)}.
         */
        if (childWidgets.contains(child)) {
            if (childWidgets.indexOf(child) == atIndex) {
                return;
            }

            final int removeFromIndex = childWidgets.indexOf(child);
            final WidgetWrapper wrapper = childWidgetWrappers
                    .get(removeFromIndex);
            Element wrapperElement = wrapper.getElement();
            final int nonWidgetChildElements = DOM
                    .getChildCount(wrappedChildContainer)
                    - childWidgets.size();
            DOM.removeChild(wrappedChildContainer, wrapperElement);
            DOM.insertChild(wrappedChildContainer, wrapperElement, atIndex
                    + nonWidgetChildElements);
            childWidgets.remove(removeFromIndex);
            childWidgetWrappers.remove(removeFromIndex);
            childWidgets.insertElementAt(child, atIndex);
            childWidgetWrappers.insertElementAt(wrapper, atIndex);
            return;
        }

        /*
         * <b>Detach Child:</b> Remove the Widget from its existing parent, if
         * any. Most Panels will simply call {@link Widget#removeFromParent()}
         * on the Widget.
         */
        child.removeFromParent();

        /*
         * <b>Logical Attach:</b> Any state variables of the Panel should be
         * updated to reflect the addition of the new Widget. Example: the
         * Widget is added to the Panel's {@link WidgetCollection} at the
         * appropriate index.
         */
        childWidgets.insertElementAt(child, atIndex);

        /*
         * <b>Physical Attach:</b> The Widget's Element must be physically
         * attached to the Panel's Element, either directly or indirectly.
         */
        final WidgetWrapper wrapper = new WidgetWrapper();
        final int nonWidgetChildElements = DOM
                .getChildCount(wrappedChildContainer)
                - childWidgetWrappers.size();
        childWidgetWrappers.insertElementAt(wrapper, atIndex);
        DOM.insertChild(wrappedChildContainer, wrapper.getElement(), atIndex
                + nonWidgetChildElements);
        DOM.appendChild(wrapper.getElementWrappingWidgetAndCaption(), child
                .getElement());

        /*
         * <b>Adopt:</b> Call {@link #adopt(Widget)} to finalize the add as the
         * very last step.
         */
        adopt(child);
    }

    /* documented at super */
    public boolean remove(Widget child) {

        /*
         * <b>Validate:</b> Make sure this Panel is actually the parent of the
         * child Widget; return <code>false</code> if it is not.
         */
        if (!childWidgets.contains(child)) {
            return false;
        }

        /*
         * <b>Orphan:</b> Call {@link #orphan(Widget)} first while the child
         * Widget is still attached.
         */
        orphan(child);

        /*
         * <b>Physical Detach:</b> Adjust the DOM to account for the removal of
         * the child Widget. The Widget's Element must be physically removed
         * from the DOM.
         */
        final int index = childWidgets.indexOf(child);
        final WidgetWrapper wrapper = childWidgetWrappers.get(index);
        DOM.removeChild(wrappedChildContainer, wrapper.getElement());
        childWidgetWrappers.remove(index);

        /*
         * <b>Logical Detach:</b> Update the Panel's state variables to reflect
         * the removal of the child Widget. Example: the Widget is removed from
         * the Panel's {@link WidgetCollection}.
         */
        childWidgets.remove(index);

        if (child instanceof Paintable) {
            client.unregisterPaintable((Paintable) child);
        }

        return true;
    }

    /* documented at super */
    public boolean hasChildComponent(Widget component) {
        return childWidgets.contains(component);
    }

    /* documented at super */
    public void replaceChildComponent(Widget oldComponent, Widget newComponent) {
        final int index = childWidgets.indexOf(oldComponent);
        if (index >= 0) {
            client.unregisterPaintable((Paintable) oldComponent);
            remove(oldComponent);
            add(newComponent, index);
        }
    }

    /* documented at super */
    public void updateCaption(Paintable component, UIDL uidl) {
        final int index = childWidgets.indexOf(component);
        if (index >= 0) {
            childWidgetWrappers.get(index).updateCaption(uidl, component);
        }
    }

    /* documented at super */
    public Iterator iterator() {
        return childWidgets.iterator();
    }

    /* documented at super */
    public void iLayout(int availableWidth, int availableHeight) {
        updateChildSizes(availableWidth, availableHeight);
        Util.runDescendentsLayout(this);
        childLayoutsHaveChanged = false;
    }

    public int getAllocatedHeight(Widget child) {
        final int index = childWidgets.indexOf(child);
        if (index >= 0) {
            WidgetWrapper wrapper = childWidgetWrappers.get(index);
            return wrapper.getAllocatedHeight();
        }

        return -1;
    }

    public int getAllocatedWidth(Widget child) {
        final int index = childWidgets.indexOf(child);
        if (index >= 0) {
            WidgetWrapper wrapper = childWidgetWrappers.get(index);
            return wrapper.getAllocatedWidth();
        }

        return -1;
    }

    public boolean childComponentSizesUpdated() {
        if (height != null && width != null) {
            /*
             * If the height and width has been specified for this layout the
             * child components cannot make the size of the layout change
             */

            return true;
        }

        int currentHeight = getElement().getOffsetHeight();
        int currentWidth = getElement().getOffsetWidth();

        if (currentHeight != renderedHeight || currentWidth != renderedWidth) {
            /*
             * Size has changed so we let the child components know about the
             * new size.
             */
            iLayout(-1, -1);
            return false;
        } else {
            /*
             * Size has not changed so we do not need to propagate the event
             * further
             */
            return true;
        }

    }

}

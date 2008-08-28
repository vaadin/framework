package com.itmill.toolkit.terminal.gwt.client.ui.absolutegrid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.ICaption;
import com.itmill.toolkit.terminal.gwt.client.Container;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;
import com.itmill.toolkit.terminal.gwt.client.ui.MarginInfo;

/**
 * Proto level implementation of GridLayout.
 * 
 * All cell's will be equally sized.
 * 
 */
public class ISizeableGridLayout extends IAbsoluteGrid implements Paintable,
        Container {
    public static final String CLASSNAME = "i-gridlayout";
    private int spacing;
    private HashMap paintableToCellMap = new HashMap();
    private ApplicationConnection client;
    private MarginPixels mp;
    private String oldStyleString = "";

    public ISizeableGridLayout() {
        super();
        setStyleName(CLASSNAME);
    }

    protected int getSpacingSize() {
        return spacing;
    }

    public void updateFromUIDL(UIDL uidl, final ApplicationConnection client) {
        this.client = client;

        if (client.updateComponent(this, uidl, true)) {
            return;
        }

        // act properly in rare case where style name changes
        String newStyleString = "";
        if (uidl.hasAttribute("style")) {
            newStyleString = uidl.getStringAttribute("style");
        }
        if (!newStyleString.equals(oldStyleString)) {
            // reset detected margin values as they may change due style change
            mp = null;
            // also force extra layout phase after render (fails initially if
            // changed)
            DeferredCommand.addCommand(new Command() {
                public void execute() {
                    client.requestLayoutPhase();
                }
            });

        }
        oldStyleString = newStyleString;

        if (uidl.hasAttribute("caption")) {
            setTitle(uidl.getStringAttribute("caption"));
        }
        int row = 0, column = 0;

        final ArrayList oldCells = new ArrayList();
        for (final Iterator iterator = getCellIterator(); iterator.hasNext();) {
            oldCells.add(iterator.next());
        }
        clear();

        setCols(uidl.getIntAttribute("w"));
        setRows(uidl.getIntAttribute("h"));

        handleMargins(uidl);
        spacing = uidl.getBooleanAttribute("spacing") ? detectSpacingSize() : 0;

        final int[] alignments = uidl.getIntArrayAttribute("alignments");
        int alignmentIndex = 0;

        for (final Iterator i = uidl.getChildIterator(); i.hasNext();) {
            final UIDL r = (UIDL) i.next();
            if ("gr".equals(r.getTag())) {
                column = 0;
                for (final Iterator j = r.getChildIterator(); j.hasNext();) {
                    final UIDL c = (UIDL) j.next();
                    if ("gc".equals(c.getTag())) {

                        // Set cell width
                        int colSpan;
                        if (c.hasAttribute("w")) {
                            colSpan = c.getIntAttribute("w");
                        } else {
                            colSpan = 1;
                        }

                        // Set cell height
                        int rowSpan;
                        if (c.hasAttribute("h")) {
                            rowSpan = c.getIntAttribute("h");
                        } else {
                            rowSpan = 1;
                        }

                        final UIDL u = c.getChildUIDL(0);
                        if (u != null) {
                            final Paintable child = client.getPaintable(u);
                            IAbsoluteGridCell cell = getCell(column, row);
                            paintableToCellMap.put(child, cell);
                            cell.rowSpan = rowSpan;
                            cell.colSpan = colSpan;

                            oldCells.remove(cell);

                            cell.setAlignment(alignments[alignmentIndex++]);

                            cell.render();

                            cell.setWidget((Widget) child);

                            if (!u.getBooleanAttribute("cached")) {
                                child.updateFromUIDL(u, client);
                            }

                            cell.vAling();
                        }
                        column += colSpan;
                    }
                }
                row++;
            }
        }

        // loop oldWidgetWrappers that where not re-attached and unregister them
        for (final Iterator it = oldCells.iterator(); it.hasNext();) {
            final IAbsoluteGridCell w = (IAbsoluteGridCell) it.next();
            client.unregisterPaintable((Paintable) w.getWidget());
            w.removeFromParent();
            paintableToCellMap.remove(w.getWidget());
        }

    }

    protected void handleMargins(UIDL uidl) {
        final MarginInfo margins = new MarginInfo(uidl
                .getIntAttribute("margins"));
        if (mp == null) {
            mp = detectMargins(getElement(), CLASSNAME);
        }
        marginTop = margins.hasTop() ? mp.top : 0;
        marginRight = margins.hasRight() ? mp.right : 0;
        marginBottom = margins.hasBottom() ? mp.bottom : 0;
        marginLeft = margins.hasLeft() ? mp.left : 0;
    }

    private int detectSpacingSize() {
        if (mp == null) {
            mp = detectMargins(getElement(), CLASSNAME);
        }
        return mp.spacing;
    }

    public boolean hasChildComponent(Widget component) {
        if (paintableToCellMap.containsKey(component)) {
            return true;
        } else {
            return false;
        }
    }

    public void replaceChildComponent(Widget oldComponent, Widget newComponent) {
        // TODO Auto-generated method stub

    }

    public void updateCaption(Paintable component, UIDL uidl) {
        IAbsoluteGridCell cell = (IAbsoluteGridCell) paintableToCellMap
                .get(component);
        ICaption c = cell.getCaption();
        if (c == null) {
            c = new ICaption(component, client);
            cell.setCaption(c);
        }
        c.updateCaption(uidl);
    }

    /**
     * Helper method to detect proper sizes (set via css) for margins and
     * spacings.
     * 
     * @param baseElement
     *                measurements will be done withing this element
     * @param baseStyleName
     *                base style name
     * @return
     */
    public static MarginPixels detectMargins(Element baseElement,
            String baseStyleName) {
        Element wrap = DOM.createDiv();
        DOM.setStyleAttribute(wrap, "position", "absolute");
        DOM.setStyleAttribute(wrap, "visibility", "hidden");

        Element left = DOM.createDiv();
        DOM.setElementProperty(left, "className", baseStyleName
                + "-margin-left");
        DOM.setStyleAttribute(left, "width", "0");
        DOM.appendChild(wrap, left);
        Element right = DOM.createDiv();
        DOM.setElementProperty(right, "className", baseStyleName
                + "-margin-right");
        DOM.setStyleAttribute(right, "width", "0");
        DOM.appendChild(wrap, right);
        Element top = DOM.createDiv();
        DOM.setElementProperty(top, "className", baseStyleName + "-margin-top");
        DOM.setStyleAttribute(top, "width", "0");
        DOM.appendChild(wrap, top);
        Element bottom = DOM.createDiv();
        DOM.setElementProperty(bottom, "className", baseStyleName
                + "-margin-bottom");
        DOM.setStyleAttribute(bottom, "width", "0");
        DOM.appendChild(wrap, bottom);

        Element spacing = DOM.createDiv();
        DOM.setElementProperty(spacing, "className", baseStyleName
                + "-spacing-element");
        DOM.setStyleAttribute(spacing, "width", "0");
        DOM.appendChild(wrap, spacing);

        DOM.insertChild(baseElement, wrap, 0);

        MarginPixels marginPixels = new MarginPixels();
        marginPixels.top = DOM.getElementPropertyInt(top, "offsetHeight");
        marginPixels.right = DOM.getElementPropertyInt(right, "offsetWidth");
        marginPixels.bottom = DOM.getElementPropertyInt(bottom, "offsetHeight");
        marginPixels.left = DOM.getElementPropertyInt(left, "offsetWidth");
        marginPixels.spacing = DOM
                .getElementPropertyInt(spacing, "offsetWidth");

        DOM.removeChild(baseElement, wrap);

        return marginPixels;
    }

}

class MarginPixels {
    public int spacing;
    public int top;
    public int bottom;
    public int left;
    public int right;
}

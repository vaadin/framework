package com.itmill.toolkit.terminal.gwt.client.ui.absolutegrid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Caption;
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
public class ISizeableGridLayout extends AbsoluteGrid implements Paintable,
        Container {
    public static final String CLASSNAME = "i-gridlayout";
    private int spacing;
    private HashMap paintableToCellMap = new HashMap();
    private ApplicationConnection client;

    public ISizeableGridLayout() {
        super();
        setStyleName(CLASSNAME);
    }

    protected int getSpacingSize() {
        return spacing;
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        this.client = client;

        if (client.updateComponent(this, uidl, true)) {
            return;
        }

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
                            AbsoluteGridCell cell = getCell(column, row);
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
            final AbsoluteGridCell w = (AbsoluteGridCell) it.next();
            client.unregisterPaintable((Paintable) w.getWidget());
            w.removeFromParent();
            paintableToCellMap.remove(w.getWidget());
        }

    }

    protected void handleMargins(UIDL uidl) {
        final MarginInfo margins = new MarginInfo(uidl
                .getIntAttribute("margins"));
        // TODO build CSS detector to make margins configurable through css
        marginTop = margins.hasTop() ? 15 : 0;
        marginRight = margins.hasRight() ? 15 : 0;
        marginBottom = margins.hasBottom() ? 15 : 0;
        marginLeft = margins.hasLeft() ? 15 : 0;
    }

    private int detectSpacingSize() {
        // TODO Auto-generated method stub
        return 15;
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
        AbsoluteGridCell cell = (AbsoluteGridCell) paintableToCellMap
                .get(component);
        Caption c = cell.getCaption();
        if (c == null) {
            c = new Caption(component, client);
            cell.setCaption(c);
        }
        c.updateCaption(uidl);
    }

}

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
public class ISizeableOrderedLayout extends AbsoluteGrid implements Paintable,
        Container {
    public static final String CLASSNAME = "i-orderedlayout";
    private static final int ORIENTETION_HORIZONTAL = 1;
    private int spacing;
    private HashMap paintableToCellMap = new HashMap();
    private ApplicationConnection client;
    private int orientation;

    public ISizeableOrderedLayout() {
        super();
        setStyleName(CLASSNAME);
    }

    protected int getSpacingSize() {
        return spacing;
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        this.client = client;

        if (client.updateComponent(this, uidl, false)) {
            return;
        }

        orientation = (uidl.hasAttribute("orientation") ? ORIENTETION_HORIZONTAL
                : 0);

        if (uidl.hasAttribute("caption")) {
            setTitle(uidl.getStringAttribute("caption"));
        }

        handleMargins(uidl);
        spacing = uidl.getBooleanAttribute("spacing") ? detectSpacingSize() : 0;

        final int[] alignments = uidl.getIntArrayAttribute("alignments");
        int alignmentIndex = 0;

        // Update contained components

        final ArrayList uidlWidgets = new ArrayList();
        for (final Iterator it = uidl.getChildIterator(); it.hasNext();) {
            final UIDL uidlForChild = (UIDL) it.next();
            final Paintable child = client.getPaintable(uidlForChild);
            uidlWidgets.add(child);
        }

        if (orientation == ORIENTETION_HORIZONTAL) {
            setCols(uidlWidgets.size());
            setRows(1);
        } else {
            setCols(1);
            setRows(uidlWidgets.size());
        }

        final ArrayList oldWidgets = getPaintables();

        final HashMap oldCaptions = new HashMap();

        final Iterator newIt = uidlWidgets.iterator();
        final Iterator newUidl = uidl.getChildIterator();

        int row = 0, column = 0;
        while (newIt.hasNext()) {
            final Widget child = (Widget) newIt.next();
            final UIDL childUidl = (UIDL) newUidl.next();

            AbsoluteGridCell cell = getCell(column, row);

            Widget oldChild = cell.getWidget();
            if (oldChild != null) {
                if (oldChild != child) {
                    oldCaptions.put(oldChild, cell.getCaption());
                    cell.clear();
                    cell.setWidget(child);
                    paintableToCellMap.remove(oldChild);
                    Caption newCaption = (Caption) oldCaptions.get(child);
                    if (newCaption == null) {
                        newCaption = new Caption((Paintable) child, client);
                    }
                    cell.setCaption(newCaption);
                }
            } else {
                cell.setWidget(child);
            }

            paintableToCellMap.put(child, cell);

            cell.setAlignment(alignments[alignmentIndex++]);

            cell.render();

            ((Paintable) child).updateFromUIDL(childUidl, client);

            cell.vAling();

            if (orientation == ORIENTETION_HORIZONTAL) {
                column++;
            } else {
                row++;
            }
            oldWidgets.remove(child);
        }
        // remove possibly remaining old Paintable object which were not updated
        Iterator oldIt = oldWidgets.iterator();
        while (oldIt.hasNext()) {
            final Paintable p = (Paintable) oldIt.next();
            if (!uidlWidgets.contains(p)) {
                removePaintable(p);
            }
        }
    }

    private void removePaintable(Paintable oldChild) {
        AbsoluteGridCell cell = (AbsoluteGridCell) paintableToCellMap
                .get(oldChild);
        if (cell != null) {
            cell.clear();
        }
        client.unregisterPaintable(oldChild);
    }

    private ArrayList getPaintables() {
        ArrayList paintables = new ArrayList();
        Iterator it = paintableToCellMap.keySet().iterator();
        while (it.hasNext()) {
            Paintable p = (Paintable) it.next();
            paintables.add(p);
        }
        return paintables;
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

package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.CaptionWrapper;
import com.itmill.toolkit.terminal.gwt.client.Container;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class IGridLayout extends FlexTable implements Paintable, Container {

    public static final String CLASSNAME = "i-gridlayout";

    /** Widget to captionwrapper map */
    private HashMap widgetToCaptionWrapper = new HashMap();

    public IGridLayout() {
        super();
        setStyleName(CLASSNAME);
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {

        if (client.updateComponent(this, uidl, false)) {
            return;
        }

        if (uidl.hasAttribute("caption")) {
            setTitle(uidl.getStringAttribute("caption"));
        }
        int row = 0, column = 0;

        ArrayList detachdedPaintables = new ArrayList();

        for (Iterator i = uidl.getChildIterator(); i.hasNext();) {
            UIDL r = (UIDL) i.next();
            if ("gr".equals(r.getTag())) {
                column = 0;
                for (Iterator j = r.getChildIterator(); j.hasNext();) {
                    UIDL c = (UIDL) j.next();
                    if ("gc".equals(c.getTag())) {
                        // Set cell width
                        int w;
                        if (c.hasAttribute("w")) {
                            w = c.getIntAttribute("w");
                        } else {
                            w = 1;
                        }
                        ((FlexCellFormatter) getCellFormatter()).setColSpan(
                                row, column, w);

                        // Set cell height
                        int h;
                        if (c.hasAttribute("h")) {
                            h = c.getIntAttribute("h");
                        } else {
                            h = 1;
                        }
                        ((FlexCellFormatter) getCellFormatter()).setRowSpan(
                                row, column, h);

                        UIDL u = c.getChildUIDL(0);
                        if (u != null) {
                            Widget child = client.getWidget(u);
                            prepareCell(row, column);
                            Widget oldChild = getWidget(row, column);
                            if (oldChild instanceof CaptionWrapper) {
                                CaptionWrapper new_name = (CaptionWrapper) oldChild;
                                oldChild = (Widget) new_name.getPaintable();
                            }
                            if (child != oldChild) {
                                if (oldChild != null) {
                                    detachdedPaintables.add(oldChild);
                                    CaptionWrapper cw = (CaptionWrapper) widgetToCaptionWrapper
                                            .get(oldChild);
                                    cw.removeFromParent();
                                    widgetToCaptionWrapper.remove(oldChild);
                                }
                                CaptionWrapper wrapper = new CaptionWrapper(
                                        (Paintable) child, client);
                                setWidget(row, column, wrapper);
                                widgetToCaptionWrapper.put(child, wrapper);
                            }
                            if (!u.getBooleanAttribute("cached")) {
                                ((Paintable) child).updateFromUIDL(u, client);
                            }
                        }
                        column += w;
                    }
                }
                row++;
            }
        }

        // for loop detached widgets and unregister them unless they are
        // attached (case of widget which is moved to another cell)
        for (Iterator it = detachdedPaintables.iterator(); it.hasNext();) {
            Widget w = (Widget) it.next();
            if (!w.isAttached()) {
                client.unregisterPaintable((Paintable) w);
            }
        }
    }

    public boolean hasChildComponent(Widget component) {
        if (widgetToCaptionWrapper.containsKey(component)) {
            return true;
        }
        return false;
    }

    public void replaceChildComponent(Widget oldComponent, Widget newComponent) {
        // TODO Auto-generated method stub

    }

    public void updateCaption(Paintable component, UIDL uidl) {
        CaptionWrapper wrapper = (CaptionWrapper) widgetToCaptionWrapper
                .get(component);
        wrapper.updateCaption(uidl);
    }

}

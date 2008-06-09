/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.CaptionWrapper;
import com.itmill.toolkit.terminal.gwt.client.Container;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.StyleConstants;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class IGridLayout extends SimplePanel implements Paintable, Container {

    public static final String CLASSNAME = "i-gridlayout";

    private Grid grid = new Grid();

    public IGridLayout() {
        super();
        setStyleName(CLASSNAME);
        setWidget(grid);
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {

        if (client.updateComponent(this, uidl, false)) {
            return;
        }

        final MarginInfo margins = new MarginInfo(uidl
                .getIntAttribute("margins"));

        Element margin = getElement();
        setStyleName(margin, CLASSNAME + "-" + StyleConstants.MARGIN_TOP,
                margins.hasTop());
        setStyleName(margin, CLASSNAME + "-" + StyleConstants.MARGIN_RIGHT,
                margins.hasRight());
        setStyleName(margin, CLASSNAME + "-" + StyleConstants.MARGIN_BOTTOM,
                margins.hasBottom());
        setStyleName(margin, CLASSNAME + "-" + StyleConstants.MARGIN_LEFT,
                margins.hasLeft());

        setStyleName(margin, CLASSNAME + "-" + "spacing", uidl
                .hasAttribute("spacing"));

        grid.updateFromUIDL(uidl, client);
    }

    public boolean hasChildComponent(Widget component) {
        return grid.hasChildComponent(component);
    }

    public void replaceChildComponent(Widget oldComponent, Widget newComponent) {
        grid.replaceChildComponent(oldComponent, newComponent);
    }

    public void updateCaption(Paintable component, UIDL uidl) {
        grid.updateCaption(component, uidl);
    }

    public class Grid extends FlexTable implements Paintable, Container {

        /** Widget to captionwrapper map */
        private final HashMap widgetToCaptionWrapper = new HashMap();

        public Grid() {
            super();
            setStyleName(CLASSNAME + "-grid");
        }

        public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {

            int row = 0, column = 0;

            final ArrayList oldWidgetWrappers = new ArrayList();
            for (final Iterator iterator = iterator(); iterator.hasNext();) {
                oldWidgetWrappers.add(iterator.next());
            }
            clear();

            final int[] alignments = uidl.getIntArrayAttribute("alignments");
            int alignmentIndex = 0;

            for (final Iterator i = uidl.getChildIterator(); i.hasNext();) {
                final UIDL r = (UIDL) i.next();
                if ("gr".equals(r.getTag())) {
                    column = 0;
                    for (final Iterator j = r.getChildIterator(); j.hasNext();) {
                        final UIDL c = (UIDL) j.next();
                        if ("gc".equals(c.getTag())) {
                            prepareCell(row, column);

                            // Set cell width
                            int w;
                            if (c.hasAttribute("w")) {
                                w = c.getIntAttribute("w");
                            } else {
                                w = 1;
                            }
                            AlignmentInfo alignmentInfo = new AlignmentInfo(
                                    alignments[alignmentIndex++]);

                            VerticalAlignmentConstant va;
                            if (alignmentInfo.isBottom()) {
                                va = HasVerticalAlignment.ALIGN_BOTTOM;
                            } else if (alignmentInfo.isTop()) {
                                va = HasVerticalAlignment.ALIGN_TOP;
                            } else {
                                va = HasVerticalAlignment.ALIGN_MIDDLE;
                            }

                            HorizontalAlignmentConstant ha;

                            if (alignmentInfo.isLeft()) {
                                ha = HasHorizontalAlignment.ALIGN_LEFT;
                            } else if (alignmentInfo.isHorizontalCenter()) {
                                ha = HasHorizontalAlignment.ALIGN_CENTER;
                            } else {
                                ha = HasHorizontalAlignment.ALIGN_RIGHT;
                            }

                            FlexCellFormatter formatter = (FlexCellFormatter) getCellFormatter();

                            formatter.setAlignment(row, column, ha, va);

                            // set col span
                            formatter.setColSpan(row, column, w);

                            String styleNames = CLASSNAME + "-cell";
                            if (column == 0) {
                                styleNames += " " + CLASSNAME + "-firstcol";
                            }
                            if (row == 0) {
                                styleNames += " " + CLASSNAME + "-firstrow";
                            }
                            formatter.setStyleName(row, column, styleNames);

                            // Set cell height
                            int h;
                            if (c.hasAttribute("h")) {
                                h = c.getIntAttribute("h");
                            } else {
                                h = 1;
                            }
                            ((FlexCellFormatter) getCellFormatter())
                                    .setRowSpan(row, column, h);

                            final UIDL u = c.getChildUIDL(0);
                            if (u != null) {
                                final Paintable child = client.getPaintable(u);
                                CaptionWrapper wr;
                                if (widgetToCaptionWrapper.containsKey(child)) {
                                    wr = (CaptionWrapper) widgetToCaptionWrapper
                                            .get(child);
                                    oldWidgetWrappers.remove(wr);
                                } else {
                                    wr = new CaptionWrapper(child, client);
                                    widgetToCaptionWrapper.put(child, wr);
                                }

                                setWidget(row, column, wr);

                                DOM.setStyleAttribute(wr.getElement(),
                                        "textAlign", alignmentInfo
                                                .getHorizontalAlignment());

                                if (!u.getBooleanAttribute("cached")) {
                                    child.updateFromUIDL(u, client);
                                }
                            }
                            column += w;
                        }
                    }
                    row++;
                }
            }

            // loop oldWidgetWrappers that where not re-attached and unregister
            // them
            for (final Iterator it = oldWidgetWrappers.iterator(); it.hasNext();) {
                final CaptionWrapper w = (CaptionWrapper) it.next();
                client.unregisterPaintable(w.getPaintable());
                widgetToCaptionWrapper.remove(w.getPaintable());
            }
        }

        public boolean hasChildComponent(Widget component) {
            if (widgetToCaptionWrapper.containsKey(component)) {
                return true;
            }
            return false;
        }

        public void replaceChildComponent(Widget oldComponent,
                Widget newComponent) {
            // TODO Auto-generated method stub

        }

        public void updateCaption(Paintable component, UIDL uidl) {
            final CaptionWrapper wrapper = (CaptionWrapper) widgetToCaptionWrapper
                    .get(component);
            wrapper.updateCaption(uidl);
        }

    }

}

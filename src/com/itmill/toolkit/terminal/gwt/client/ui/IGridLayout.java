/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.BrowserInfo;
import com.itmill.toolkit.terminal.gwt.client.Container;
import com.itmill.toolkit.terminal.gwt.client.ContainerResizedListener;
import com.itmill.toolkit.terminal.gwt.client.ICaptionWrapper;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.StyleConstants;
import com.itmill.toolkit.terminal.gwt.client.UIDL;
import com.itmill.toolkit.terminal.gwt.client.RenderInformation.Size;

public class IGridLayout extends SimplePanel implements Paintable, Container,
        ContainerResizedListener {

    public static final String CLASSNAME = "i-gridlayout";

    private Grid grid = new Grid();

    private boolean needsLayout = false;

    private boolean needsFF2Hack = BrowserInfo.get().isFF2();

    private Element margin = DOM.createDiv();

    private Element meterElement;

    private String width;

    private ApplicationConnection client;

    public IGridLayout() {
        super();
        DOM.appendChild(getElement(), margin);
        DOM.setStyleAttribute(getElement(), "overflow", "hidden");
        setStyleName(CLASSNAME);
        setWidget(grid);
    }

    protected Element getContainerElement() {
        return margin;
    }

    public void setWidth(String width) {
        this.width = width;
        if (width != null && !width.equals("")) {
            needsLayout = true;
        } else {
            needsLayout = false;
            grid.setWidth("");
        }
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        this.client = client;

        if (client.updateComponent(this, uidl, true)) {
            return;
        }
        final MarginInfo margins = new MarginInfo(uidl
                .getIntAttribute("margins"));

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
        iLayout();
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

            /* Clear() removes all widgets but leaves the tr and td tags */
            clear();

            boolean structuralChange = uidl
                    .getBooleanAttribute("structuralChange");

            /*
             * If a row has been inserted or removed at the middle of the table
             * we need to remove all old tr and td tags.
             */
            if (structuralChange) {
                while (getRowCount() > 0) {
                    removeRow(0);
                }
            }

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

                            FlexCellFormatter formatter = (FlexCellFormatter) getCellFormatter();

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

                                formatter.setAlignment(row, column, ha, va);

                                final Paintable child = client.getPaintable(u);
                                ICaptionWrapper wr;
                                if (widgetToCaptionWrapper.containsKey(child)) {
                                    wr = (ICaptionWrapper) widgetToCaptionWrapper
                                            .get(child);
                                    oldWidgetWrappers.remove(wr);
                                } else {
                                    wr = new ICaptionWrapper(child, client);
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
                            column++;
                        }
                    }
                    row++;
                }
            }

            // loop oldWidgetWrappers that where not re-attached and unregister
            // them
            for (final Iterator it = oldWidgetWrappers.iterator(); it.hasNext();) {
                final ICaptionWrapper w = (ICaptionWrapper) it.next();
                client.unregisterPaintable(w.getPaintable());
                widgetToCaptionWrapper.remove(w.getPaintable());
            }
            // fix rendering bug on FF2 (#1838)
            if (needsFF2Hack) {
                DeferredCommand.addCommand(new Command() {
                    public void execute() {
                        Element firstcell = getCellFormatter().getElement(0, 0);
                        if (firstcell != null) {
                            String styleAttribute = DOM.getStyleAttribute(
                                    firstcell, "verticalAlign");
                            DOM.setStyleAttribute(firstcell, "verticalAlign",
                                    "");
                            int elementPropertyInt = DOM.getElementPropertyInt(
                                    firstcell, "offsetWidth");
                            DOM.setStyleAttribute(firstcell, "verticalAlign",
                                    styleAttribute);
                            if (elementPropertyInt > 0) {
                                needsFF2Hack = false;
                            }
                        }
                    }
                });
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
            final ICaptionWrapper wrapper = (ICaptionWrapper) widgetToCaptionWrapper
                    .get(component);
            wrapper.updateCaption(uidl);
        }

        public boolean requestLayout(Set<Paintable> child) {
            // TODO Auto-generated method stub
            return false;
        }

        public Size getAllocatedSpace(Widget child) {
            // TODO Auto-generated method stub
            return null;
        }

    }

    public void iLayout() {
        if (needsLayout) {
            super.setWidth(width);
            if (meterElement == null) {
                meterElement = DOM.createDiv();
                DOM.setStyleAttribute(meterElement, "overflow", "hidden");
                DOM.setStyleAttribute(meterElement, "height", "0");
                DOM.appendChild(getContainerElement(), meterElement);
            }
            int contentWidth = DOM.getElementPropertyInt(meterElement,
                    "offsetWidth");
            int offsetWidth = getOffsetWidth();

            grid.setWidth((offsetWidth - (offsetWidth - contentWidth)) + "px");
        } else {
            grid.setWidth("");
        }
        client.runDescendentsLayout(this);
    }

    public boolean requestLayout(Set<Paintable> child) {
        // TODO Auto-generated method stub
        return false;
    }

    public Size getAllocatedSpace(Widget child) {
        // TODO Auto-generated method stub
        return null;
    }

}

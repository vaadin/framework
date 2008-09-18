package com.itmill.toolkit.terminal.gwt.client.ui.absolutegrid;

import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.BrowserInfo;
import com.itmill.toolkit.terminal.gwt.client.ContainerResizedListener;
import com.itmill.toolkit.terminal.gwt.client.ICaption;
import com.itmill.toolkit.terminal.gwt.client.Util;
import com.itmill.toolkit.terminal.gwt.client.ui.AlignmentInfo;

/**
 * Prototype helper widget to implement complex sized Toolkit layouts like
 * GridLayout and OrderedLayout. Supports size, margins, spacing, but has bit
 * expensive layout function.
 */
public class IAbsoluteGrid extends Composite implements
        ContainerResizedListener {

    protected HashMap cells = new HashMap();

    private int cols = 1;
    private int rows = 1;

    private AbsolutePanel ap;

    protected int marginTop;
    protected int marginBottom;
    protected int marginLeft;
    protected int marginRight;

    private int offsetWidth;

    private int offsetHeight;

    public IAbsoluteGrid() {
        ap = new AbsolutePanel();
        initWidget(ap);
    }

    public IAbsoluteGridCell getCell(int col, int row) {
        IAbsoluteGridCell p = (IAbsoluteGridCell) cells.get(col + "." + row);
        if (p == null) {
            p = new IAbsoluteGridCell(col, row);
            cells.put(col + "." + row, p);
            ap.add(p);
        }
        return p;
    }

    public void clear() {
        ap.clear();
        cells.clear();
    }

    public Iterator getCellIterator() {
        return cells.values().iterator();
    }

    private float getCellWidth(int colspan) {
        int total = ap.getOffsetWidth();
        total -= getMarginWidth();
        total -= getSpacingSize() * (cols - colspan);
        if (total < 0) {
            return 0;
        }
        return total * colspan / (float) cols;
    }

    /**
     * 
     * @return space used by left and right margin
     */
    private int getMarginWidth() {
        return marginLeft + marginRight;
    }

    /**
     * @return pixels reserved for space between components
     */
    protected int getSpacingSize() {
        return 0;
    }

    private float getCellHeight(int rowspan) {
        int total = ap.getOffsetHeight();
        total -= getMarginHeight();
        total -= getSpacingSize() * (rows - rowspan);
        if (total < 0) {
            return 0;
        }
        return total * rowspan / (float) rows;
    }

    /**
     * 
     * @return space used by top and bottom margin
     */
    private int getMarginHeight() {
        return marginBottom + marginTop;
    }

    /**
     * TODO contains Caption (which is a widget) in a very bad way, cannot be
     * simple panel
     */
    public class IAbsoluteGridCell extends SimplePanel {

        int rowIndex;
        int colIndex;
        int colSpan = 1;
        int rowSpan = 1;
        private Element container = DOM.createDiv();

        private ICaption caption;
        private AlignmentInfo alignmentInfo = new AlignmentInfo(
                AlignmentInfo.ALIGNMENT_TOP + AlignmentInfo.ALIGNMENT_LEFT);

        IAbsoluteGridCell(int colIndex, int rowIndex) {
            super();
            if (BrowserInfo.get().isIE6()) {
                DOM.setStyleAttribute(getElement(), "overflow", "hidden");
            }
            DOM.appendChild(getElement(), container);
            this.rowIndex = rowIndex;
            this.colIndex = colIndex;
        }

        public void clear() {
            super.clear();
            if (caption != null) {
                DOM.removeChild(getElement(), caption.getElement());
                caption = null;
            }
        }

        protected Element getContainerElement() {
            return container;
        }

        void setColSpan(int s) {
            // TODO Should remove possibly collapsing cells
            colSpan = s;
        }

        void setRowSpan(int s) {
            // TODO Should remove possibly collapsing cells
            rowSpan = s;
        }

        private int getLeft() {
            int left = marginLeft;
            left += colIndex * getCellWidth(1);
            left += getSpacingSize() * colIndex;
            return left;
        }

        private int getTop() {
            int top = marginTop;
            top += rowIndex * getCellHeight(1);
            top += getSpacingSize() * rowIndex;
            return top;
        }

        public void render() {
            setPixelSize((int) getCellWidth(colSpan),
                    (int) getCellHeight(rowSpan));
            ap.setWidgetPosition(this, getLeft(), getTop());
        }

        /**
         * Does vertical positioning based on DOM values
         */
        public void vAling() {
            DOM.setStyleAttribute(getElement(), "paddingTop", "0");
            if (!alignmentInfo.isTop()) {
                Widget c = getWidget();
                if (c != null) {

                    int oh = getOffsetHeight();
                    int wt = DOM.getElementPropertyInt(container, "offsetTop");
                    int wh = c.getOffsetHeight();

                    int freeSpace = getOffsetHeight()
                            - (DOM
                                    .getElementPropertyInt(container,
                                            "offsetTop") + c.getOffsetHeight());
                    if (Util.isIE()) {
                        freeSpace -= DOM.getElementPropertyInt(c.getElement(),
                                "offsetTop");
                    }
                    if (freeSpace < 0) {
                        freeSpace = 0; // clipping rest of contents when object
                        // larger than reserved area
                    }
                    if (alignmentInfo.isVerticalCenter()) {
                        DOM.setStyleAttribute(getElement(), "paddingTop",
                                (freeSpace / 2) + "px");
                    } else {
                        DOM.setStyleAttribute(getElement(), "paddingTop",
                                (freeSpace) + "px");
                    }
                }
            }
        }

        public void setPixelSize(int width, int height) {
            super.setPixelSize(width, height);
            DOM.setStyleAttribute(container, "width", width + "px");
            int contHeight = height - getCaptionHeight();
            if (contHeight < 0) {
                contHeight = 0;
            }
            DOM.setStyleAttribute(container, "height", contHeight + "px");
        }

        private int getCaptionHeight() {
            // remove hard coded caption height
            return (caption == null) ? 0 : caption.getOffsetHeight();
        }

        public ICaption getCaption() {
            return caption;
        }

        public void setCaption(ICaption newCaption) {
            // TODO check for existing, shouldn't happen though
            caption = newCaption;
            DOM.insertChild(getElement(), caption.getElement(), 0);
        }

        public void setAlignment(int bitmask) {
            if (alignmentInfo.getBitMask() != bitmask) {
                alignmentInfo = new AlignmentInfo(bitmask);
                setHorizontalAling();
                // vertical align is set in render() method
            }
        }

        private void setHorizontalAling() {
            DOM.setStyleAttribute(getElement(), "textAlign", alignmentInfo
                    .getHorizontalAlignment());
            if (getWidget() != null) {
                Element el = getWidget().getElement();
                if (alignmentInfo.isHorizontalCenter()
                        || alignmentInfo.isRight()) {
                    DOM.setStyleAttribute(el, "marginLeft", "auto");
                } else {
                    DOM.setStyleAttribute(el, "marginLeft", "");
                }
                if (alignmentInfo.isHorizontalCenter()
                        || alignmentInfo.isLeft()) {
                    DOM.setStyleAttribute(el, "marginRight", "auto");
                } else {
                    DOM.setStyleAttribute(el, "marginRight", "");
                }
            }
        }
    }

    public void iLayout(int availableWidth, int availableHeight) {
        boolean sizeChanged = false;
        int newWidth = getOffsetWidth();
        if (offsetWidth != newWidth) {
            offsetWidth = newWidth;
            sizeChanged = true;
        }
        int newHeight = getOffsetHeight();
        if (offsetHeight != newHeight) {
            offsetHeight = newHeight;
            sizeChanged = true;
        }
        if (sizeChanged) {
            for (Iterator it = cells.values().iterator(); it.hasNext();) {
                IAbsoluteGridCell cell = (IAbsoluteGridCell) it.next();
                cell.render();
                cell.vAling();
            }
            Util.runDescendentsLayout(ap);
        }
    }

    public int getCols() {
        return cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
        // force relayout
        offsetHeight = 0;
        offsetWidth = 0;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
        // force relayout
        offsetHeight = 0;
        offsetWidth = 0;
    }
}

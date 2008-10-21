package com.itmill.toolkit.terminal.gwt.client.ui.layout;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.BrowserInfo;
import com.itmill.toolkit.terminal.gwt.client.ICaption;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;
import com.itmill.toolkit.terminal.gwt.client.Util;
import com.itmill.toolkit.terminal.gwt.client.RenderInformation.FloatSize;
import com.itmill.toolkit.terminal.gwt.client.RenderInformation.Size;
import com.itmill.toolkit.terminal.gwt.client.ui.AlignmentInfo;

public class ChildComponentContainer extends Panel {

    /**
     * Size of the container DIV excluding any margins and also excluding the
     * expansion amount (containerExpansion)
     */
    private Size contSize = new Size(0, 0);

    /**
     * Size of the widget inside the container DIV
     */
    private Size widgetSize = new Size(0, 0);
    /**
     * Size of the caption
     */
    private Size captionSize = new Size(0, 0);

    /**
     * Padding added to the container when it is larger than the component.
     */
    private Size containerExpansion = new Size(0, 0);

    private float expandRatio;

    private int containerMarginLeft = 0;
    private int containerMarginTop = 0;

    AlignmentInfo alignment = new AlignmentInfo(AlignmentInfo.ALIGNMENT_LEFT,
            AlignmentInfo.ALIGNMENT_TOP);
    private int alignmentLeftOffsetForWidget = 0;
    private int alignmentLeftOffsetForCaption = 0;
    /**
     * Top offset for implementing alignment. Top offset is set to the container
     * DIV as it otherwise would have to be set to either the Caption or the
     * Widget depending on whether there is a caption and where the caption is
     * located.
     */
    private int alignmentTopOffset = 0;

    // private Margins alignmentOffset = new Margins(0, 0, 0, 0);
    private ICaption caption = null;
    private Element containerDIV;
    private Element widgetDIV;
    private Widget widget;
    private FloatSize relativeSize = null;

    public ChildComponentContainer(Widget widget, int orientation) {
        super();

        containerDIV = DOM.createDiv();
        setElement(containerDIV);

        DOM.setStyleAttribute(containerDIV, "height", "0px");
        // DOM.setStyleAttribute(containerDIV, "width", "0px");
        DOM.setStyleAttribute(containerDIV, "overflow", "hidden");

        widgetDIV = DOM.createDiv();
        setFloat(widgetDIV, "left");

        containerDIV.appendChild(widgetDIV);

        setOrientation(orientation);

        setWidget(widget);

    }

    private void setWidget(Widget w) {
        // Validate
        if (w == widget) {
            return;
        }

        // Detach new child.
        if (w != null) {
            w.removeFromParent();
        }

        // Remove old child.
        if (widget != null) {
            remove(widget);
        }

        // Logical attach.
        widget = w;

        if (w != null) {
            // Physical attach.
            DOM.appendChild(widgetDIV, widget.getElement());

            adopt(w);
        }
    }

    private static void setFloat(Element e, String floatString) {
        Util.setFloat(e, floatString);
        if (BrowserInfo.get().isIE()) {
            // IE requires display:inline for margin-left to work together
            // with float:left
            if (floatString.equals("left")) {
                DOM.setStyleAttribute(e, "display", "inline");
            } else {
                DOM.setStyleAttribute(e, "display", "block");
            }

        }
    }

    public void setOrientation(int orientation) {
        if (orientation == CellBasedLayout.ORIENTATION_HORIZONTAL) {
            setFloat(containerDIV, "left");
        } else {
            setFloat(containerDIV, "");
        }
        setHeight("0px");
        // setWidth("0px");
        contSize.setHeight(0);
        contSize.setWidth(0);
        containerMarginLeft = 0;
        containerMarginTop = 0;
        DOM.setStyleAttribute(getElement(), "paddingLeft", "0px");
        DOM.setStyleAttribute(getElement(), "paddingTop", "0px");

        containerExpansion.setHeight(0);
        containerExpansion.setWidth(0);

        // Clear old alignments
        clearAlignments();

    }

    public void renderChild(UIDL childUIDL, ApplicationConnection client) {
        /*
         * Must remove width specification from container before rendering to
         * allow components to grow in horizontal direction
         */
        DOM.setStyleAttribute(containerDIV, "width", "");
        ((Paintable) widget).updateFromUIDL(childUIDL, client);
    }

    public void updateWidgetSize() {
        int w = widget.getOffsetWidth();
        int h = widget.getOffsetHeight();

        widgetSize.setHeight(h);
        widgetSize.setWidth(w);
    }

    public void setMarginLeft(int marginLeft) {
        containerMarginLeft = marginLeft;
        DOM.setStyleAttribute(getElement(), "paddingLeft", marginLeft + "px");
    }

    public void setMarginTop(int marginTop) {
        containerMarginTop = marginTop;
        DOM.setStyleAttribute(getElement(), "paddingTop", marginTop
                + alignmentTopOffset + "px");

        updateContainerDOMSize();
    }

    public void updateAlignments(int parentWidth, int parentHeight) {
        if (parentHeight == -1) {
            parentHeight = contSize.getHeight();
        }
        if (parentWidth == -1) {
            parentWidth = contSize.getWidth();
        }

        alignmentTopOffset = calculateVerticalAlignmentTopOffset(parentHeight);

        calculateHorizontalAlignment(parentWidth);

        applyAlignments();

    }

    private void applyAlignments() {

        // Update top margin to take alignment into account
        setMarginTop(containerMarginTop);

        if (caption != null) {
            DOM.setStyleAttribute(caption.getElement(), "marginLeft",
                    alignmentLeftOffsetForCaption + "px");
        }
        DOM.setStyleAttribute(widgetDIV, "marginLeft",
                alignmentLeftOffsetForWidget + "px");
    }

    public int getCaptionWidth() {
        if (caption == null) {
            return 0;
        }

        return captionSize.getWidth();
    }

    public int getCaptionHeight() {
        if (caption == null) {
            return 0;
        }

        return captionSize.getHeight();
    }

    public int getCaptionWidthAfterComponent() {
        if (caption == null || !caption.shouldBePlacedAfterComponent()) {
            return 0;
        }

        return getCaptionWidth();
    }

    public int getCaptionHeightAboveComponent() {
        if (caption == null || caption.shouldBePlacedAfterComponent()) {
            return 0;
        }

        return getCaptionHeight();
    }

    private int calculateVerticalAlignmentTopOffset(int emptySpace) {
        if (alignment.isTop()) {
            return 0;
        }

        if (caption != null) {
            if (caption.shouldBePlacedAfterComponent()) {
                /*
                 * Take into account the rare case that the caption on the right
                 * side of the component AND is higher than the component
                 */
                emptySpace -= Math.max(widgetSize.getHeight(), caption
                        .getHeight());
            } else {
                emptySpace -= widgetSize.getHeight();
                emptySpace -= getCaptionHeight();
            }
        } else {
            /*
             * There is no caption and thus we do not need to take anything but
             * the widget into account
             */
            emptySpace -= widgetSize.getHeight();
        }

        int top = 0;
        if (alignment.isVerticalCenter()) {
            top = emptySpace / 2;
        } else if (alignment.isBottom()) {
            top = emptySpace;
        }

        if (top < 0) {
            top = 0;
        }
        return top;
    }

    private void calculateHorizontalAlignment(int emptySpace) {
        alignmentLeftOffsetForCaption = 0;
        alignmentLeftOffsetForWidget = 0;

        if (alignment.isLeft()) {
            return;
        }

        int captionSpace = emptySpace;
        int widgetSpace = emptySpace;

        if (caption != null) {
            // There is a caption
            if (caption.shouldBePlacedAfterComponent()) {
                /*
                 * The caption is after component. In this case the caption
                 * needs no alignment.
                 */
                captionSpace = 0;
                widgetSpace -= widgetSize.getWidth();
                widgetSpace -= getCaptionWidth();
            } else {
                /*
                 * The caption is above the component. Caption and widget needs
                 * separate alignment offsets.
                 */
                widgetSpace -= widgetSize.getWidth();
                captionSpace -= getCaptionWidth();
            }
        } else {
            /*
             * There is no caption and thus we do not need to take anything but
             * the widget into account
             */
            captionSpace = 0;
            widgetSpace -= widgetSize.getWidth();
        }

        if (alignment.isHorizontalCenter()) {
            alignmentLeftOffsetForCaption = captionSpace / 2;
            alignmentLeftOffsetForWidget = widgetSpace / 2;
        } else if (alignment.isRight()) {
            alignmentLeftOffsetForCaption = captionSpace;
            alignmentLeftOffsetForWidget = widgetSpace;
        }

        if (alignmentLeftOffsetForCaption < 0) {
            alignmentLeftOffsetForCaption = 0;
        }
        if (alignmentLeftOffsetForWidget < 0) {
            alignmentLeftOffsetForWidget = 0;
        }

    }

    public void setAlignment(AlignmentInfo alignmentInfo) {
        alignment = alignmentInfo;

    }

    public Size getWidgetSize() {
        return widgetSize;
    }

    public void updateCaption(UIDL uidl, ApplicationConnection client) {
        if (ICaption.isNeeded(uidl)) {
            // We need a caption

            ICaption newCaption = caption;

            if (newCaption == null) {
                newCaption = new ICaption((Paintable) widget, client);
            }

            boolean positionChanged = newCaption.updateCaption(uidl);

            if (newCaption != caption || positionChanged) {
                setCaption(newCaption);
            }

        } else {
            // Caption is not needed
            if (caption != null) {
                remove(caption);
            }

        }

        int w = 0;
        int h = 0;

        if (caption != null) {
            w = caption.getWidth();
            h = caption.getHeight();
        }

        captionSize.setWidth(w);
        captionSize.setHeight(h);
    }

    private void setCaption(ICaption newCaption) {
        // Validate
        // if (newCaption == caption) {
        // return;
        // }

        // Detach new child.
        if (newCaption != null) {
            newCaption.removeFromParent();
        }

        // Remove old child.
        if (caption != null && newCaption != caption) {
            remove(caption);
        }

        // Logical attach.
        caption = newCaption;

        if (caption != null) {
            // Physical attach.
            if (caption.shouldBePlacedAfterComponent()) {
                Util.setFloat(caption.getElement(), "left");
                containerDIV.appendChild(caption.getElement());
            } else {
                Util.setFloat(caption.getElement(), "");
                containerDIV.insertBefore(caption.getElement(), widgetDIV);
            }

            adopt(caption);
        }

    }

    @Override
    public boolean remove(Widget child) {
        // Validate
        if (child != caption && child != widget) {
            return false;
        }

        // Orphan
        orphan(child);

        // Physical && Logical Detach
        if (child == caption) {
            containerDIV.removeChild(caption.getElement());
            caption = null;
        } else {
            containerDIV.removeChild(widget.getElement());
            widget = null;
        }

        return true;
    }

    public Iterator<Widget> iterator() {
        return new ChildComponentContainerIterator<Widget>();
    }

    public class ChildComponentContainerIterator<T> implements Iterator<Widget> {
        private int id = 0;

        public boolean hasNext() {
            return (id < size());
        }

        public Widget next() {
            Widget w = get(id);
            id++;
            return w;
        }

        private Widget get(int i) {
            if (i == 0) {
                if (widget != null) {
                    return widget;
                } else if (caption != null) {
                    return caption;
                } else {
                    throw new NoSuchElementException();
                }
            } else if (i == 1) {
                if (widget != null && caption != null) {
                    return caption;
                } else {
                    throw new NoSuchElementException();
                }
            } else {
                throw new NoSuchElementException();
            }
        }

        public void remove() {
            int toRemove = id - 1;
            if (toRemove == 0) {
                if (widget != null) {
                    ChildComponentContainer.this.remove(widget);
                } else if (caption != null) {
                    ChildComponentContainer.this.remove(caption);
                } else {
                    throw new IllegalStateException();
                }

            } else if (toRemove == 1) {
                if (widget != null && caption != null) {
                    ChildComponentContainer.this.remove(caption);
                } else {
                    throw new IllegalStateException();
                }
            } else {
                throw new IllegalStateException();
            }

            id--;
        }
    }

    public int size() {
        if (widget != null) {
            if (caption != null) {
                return 2;
            } else {
                return 1;
            }
        } else {
            if (caption != null) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public Widget getWidget() {
        return widget;
    }

    /**
     * Return true if the size of the widget has been specified in the selected
     * orientation.
     * 
     * @return
     */
    public boolean widgetHasSizeSpecified(int orientation) {
        String size;
        if (orientation == CellBasedLayout.ORIENTATION_HORIZONTAL) {
            size = widget.getElement().getStyle().getProperty("width");
        } else {
            size = widget.getElement().getStyle().getProperty("height");
        }
        return (size != null && !size.equals(""));
    }

    public boolean isComponentRelativeSized(int orientation) {
        if (relativeSize == null) {
            return false;
        }
        if (orientation == CellBasedLayout.ORIENTATION_HORIZONTAL) {
            return relativeSize.getWidth() >= 0;
        } else {
            return relativeSize.getHeight() >= 0;
        }
    }

    public void setRelativeSize(FloatSize relativeSize) {
        this.relativeSize = relativeSize;
    }

    public Size getContSize() {
        return contSize;
    }

    public void clearAlignments() {
        alignmentLeftOffsetForCaption = 0;
        alignmentLeftOffsetForWidget = 0;
        alignmentTopOffset = 0;
        applyAlignments();

    }

    public void setExpandRatio(int expandRatio) {
        this.expandRatio = (expandRatio / 1000.0f);
    }

    public int expand(int orientation, int spaceForExpansion) {
        int expansionAmount = (int) ((double) spaceForExpansion * expandRatio);

        if (orientation == CellBasedLayout.ORIENTATION_HORIZONTAL) {
            // HORIZONTAL
            containerExpansion.setWidth(expansionAmount);
        } else {
            // VERTICAL
            containerExpansion.setHeight(expansionAmount);
        }

        return expansionAmount;
    }

    public void expandExtra(int orientation, int extra) {
        if (orientation == CellBasedLayout.ORIENTATION_HORIZONTAL) {
            // HORIZONTAL
            containerExpansion.setWidth(containerExpansion.getWidth() + extra);
        } else {
            // VERTICAL
            containerExpansion
                    .setHeight(containerExpansion.getHeight() + extra);
        }

    }

    public void setContainerSize(int widgetAndCaptionWidth,
            int widgetAndCaptionHeight) {

        int containerWidth = widgetAndCaptionWidth;
        containerWidth += containerExpansion.getWidth();

        int containerHeight = widgetAndCaptionHeight;
        containerHeight += containerExpansion.getHeight();

        ApplicationConnection.getConsole().log(
                "Setting container size for " + Util.getSimpleName(widget)
                        + " to " + containerWidth + "," + containerHeight);

        if (containerWidth < 0) {
            ApplicationConnection.getConsole().error(
                    "containerWidth should never be negative: "
                            + containerWidth);
            containerWidth = 0;
        }
        if (containerHeight < 0) {
            ApplicationConnection.getConsole().error(
                    "containerHeight should never be negative: "
                            + containerHeight);
            containerHeight = 0;
        }

        contSize.setWidth(containerWidth);
        contSize.setHeight(containerHeight);

        updateContainerDOMSize();
    }

    public void updateContainerDOMSize() {
        int width = contSize.getWidth();
        int height = contSize.getHeight() - alignmentTopOffset;
        if (width < 0) {
            width = 0;
        }
        if (height < 0) {
            height = 0;
        }

        setWidth(width + "px");
        setHeight(height + "px");

        // Also update caption max width
        if (caption != null) {
            caption.setMaxWidth(width);

            captionSize.setWidth(caption.getWidth());
        }

    }

}

package com.itmill.toolkit.terminal.gwt.client.ui.layout;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
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
    private int captionRequiredWidth = 0;
    private int captionWidth = 0;
    private int captionHeight = 0;

    /**
     * Padding added to the container when it is larger than the component.
     */
    private Size containerExpansion = new Size(0, 0);

    private float expandRatio;

    private int containerMarginLeft = 0;
    private int containerMarginTop = 0;

    AlignmentInfo alignment = AlignmentInfo.TOP_LEFT;

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
    private DivElement containerDIV;
    private DivElement widgetDIV;
    private Widget widget;
    private FloatSize relativeSize = null;

    public ChildComponentContainer(Widget widget, int orientation) {
        super();

        containerDIV = Document.get().createDivElement();
        setElement(containerDIV);

        containerDIV.getStyle().setProperty("height", "0");
        containerDIV.getStyle().setProperty("width", "0px");
        containerDIV.getStyle().setProperty("overflow", "hidden");

        widgetDIV = Document.get().createDivElement();
        if (BrowserInfo.get().isFF2()) {
            Style style = widgetDIV.getStyle();
            style.setProperty("display", "table-cell");
        } else {
            setFloat(widgetDIV, "left");
        }

        if (BrowserInfo.get().isIE()) {
            /*
             * IE requires position: relative on overflow:hidden elements if
             * they should hide position:relative elements. Without this e.g. a
             * 1000x1000 Panel inside an 500x500 OrderedLayout will not be
             * clipped but fully shown.
             */
            containerDIV.getStyle().setProperty("position", "relative");
            widgetDIV.getStyle().setProperty("position", "relative");
        }

        containerDIV.appendChild(widgetDIV);

        setOrientation(orientation);

        setWidget(widget);

    }

    public void setWidget(Widget w) {
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
            widgetDIV.appendChild(widget.getElement());
            adopt(w);
        }
    }

    private static void setFloat(DivElement div, String floatString) {
        if (BrowserInfo.get().isIE()) {
            div.getStyle().setProperty("styleFloat", floatString);
            // IE requires display:inline for margin-left to work together
            // with float:left
            if (floatString.equals("left")) {
                div.getStyle().setProperty("display", "inline");
            } else {
                div.getStyle().setProperty("display", "block");
            }

        } else {
            div.getStyle().setProperty("cssFloat", floatString);
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
        getElement().getStyle().setProperty("paddingLeft", "0");
        getElement().getStyle().setProperty("paddingTop", "0");

        containerExpansion.setHeight(0);
        containerExpansion.setWidth(0);

        // Clear old alignments
        clearAlignments();

    }

    public void renderChild(UIDL childUIDL, ApplicationConnection client,
            int fixedWidth) {
        /*
         * Must remove width specification from container before rendering to
         * allow components to grow in horizontal direction.
         * 
         * For fixed width layouts we specify the width directly so that height
         * is automatically calculated correctly (e.g. for Labels).
         */
        /*
         * This should no longer be needed (after #2563) as all components are
         * such that they can be rendered inside a 0x0 DIV.
         */
        // if (fixedWidth > 0) {
        // setLimitedContainerWidth(fixedWidth);
        // } else {
        // setUnlimitedContainerWidth();
        // }
        ((Paintable) widget).updateFromUIDL(childUIDL, client);
    }

    public void setUnlimitedContainerWidth() {
        setLimitedContainerWidth(1000000);
    }

    public void setLimitedContainerWidth(int width) {
        containerDIV.getStyle().setProperty("width", width + "px");
    }

    public void updateWidgetSize() {
        /*
         * Widget wrapper includes margin which the widget offsetWidth/Height
         * does not include
         */
        int w = Util.getRequiredWidth(widgetDIV);
        int h = Util.getRequiredHeight(widgetDIV);

        widgetSize.setWidth(w);
        widgetSize.setHeight(h);

        // ApplicationConnection.getConsole().log(
        // Util.getSimpleName(widget) + " size is " + w + "," + h);

    }

    public void setMarginLeft(int marginLeft) {
        containerMarginLeft = marginLeft;
        getElement().getStyle().setPropertyPx("paddingLeft", marginLeft);
    }

    public void setMarginTop(int marginTop) {
        containerMarginTop = marginTop;
        getElement().getStyle().setPropertyPx("paddingTop",
                marginTop + alignmentTopOffset);

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
            caption.getElement().getStyle().setPropertyPx("marginLeft",
                    alignmentLeftOffsetForCaption);
        }
        widgetDIV.getStyle().setPropertyPx("marginLeft",
                alignmentLeftOffsetForWidget);
    }

    public int getCaptionRequiredWidth() {
        if (caption == null) {
            return 0;
        }

        return captionRequiredWidth;
    }

    public int getCaptionWidth() {
        if (caption == null) {
            return 0;
        }

        return captionWidth;
    }

    public int getCaptionHeight() {
        if (caption == null) {
            return 0;
        }

        return captionHeight;
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
                // Set initial height to avoid Safari flicker
                newCaption.setHeight("18px");
                // newCaption.setHeight(newCaption.getHeight()); // This might
                // be better... ??
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

        updateCaptionSize();
    }

    public void updateCaptionSize() {
        captionWidth = 0;
        captionHeight = 0;

        if (caption != null) {
            captionWidth = caption.getRenderedWidth();
            captionHeight = caption.getHeight();
            captionRequiredWidth = caption.getRequiredWidth();

            /*
             * ApplicationConnection.getConsole().log(
             * "Caption rendered width: " + captionWidth +
             * ", caption required width: " + captionRequiredWidth +
             * ", caption height: " + captionHeight);
             */
        }

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
            containerDIV.removeChild(child.getElement());
            caption = null;
        } else {
            widgetDIV.removeChild(child.getElement());
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

        // ApplicationConnection.getConsole().log(
        // "Setting container size for " + Util.getSimpleName(widget)
        // + " to " + containerWidth + "," + containerHeight);

        if (containerWidth < 0) {
            ApplicationConnection.getConsole().log(
                    "containerWidth should never be negative: "
                            + containerWidth);
            containerWidth = 0;
        }
        if (containerHeight < 0) {
            ApplicationConnection.getConsole().log(
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
            if (caption.shouldBePlacedAfterComponent()) {
                caption.setMaxWidth(captionWidth);
            } else {
                caption.setMaxWidth(width);
            }
            captionWidth = caption.getRenderedWidth();

            // Remove initial height
            caption.setHeight("");
        }

    }

}

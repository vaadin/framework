package com.vaadin.terminal.gwt.client.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.VConsole;

public class VBoxLayout extends FlowPanel {

    protected boolean spacing = false;

    protected boolean vertical = true;

    private Map<Widget, Slot> widgetToSlot = new HashMap<Widget, Slot>();

    public VBoxLayout() {
        setStylePrimaryName("v-layout");
        setVertical(true);
    }

    public void setVertical(boolean isVertical) {
        vertical = isVertical;
        addStyleName("v-box");
        if (vertical) {
            addStyleName("v-vertical");
            removeStyleName("v-horizontal");
        } else {
            addStyleName("v-horizontal");
            removeStyleName("v-vertical");
        }
    }

    public void addOrMoveSlot(Slot slot, int index) {
        if (slot.getParent() == this) {
            int currentIndex = getWidgetIndex(slot);
            if (index == currentIndex) {
                return;
            }
        }
        insert(slot, index);
    }

    @Override
    protected void insert(Widget child, Element container, int beforeIndex,
            boolean domInsert) {
        // Validate index; adjust if the widget is already a child of this
        // panel.
        beforeIndex = adjustIndex(child, beforeIndex);

        // Detach new child.
        child.removeFromParent();

        // Logical attach.
        getChildren().insert(child, beforeIndex);

        // Physical attach.
        container = expandWrapper != null ? expandWrapper : getElement();
        if (domInsert) {
            DOM.insertChild(container, child.getElement(),
                    spacing ? beforeIndex * 2 : beforeIndex);
        } else {
            DOM.appendChild(container, child.getElement());
        }

        // Adopt.
        adopt(child);
    }

    public Slot removeSlot(Widget widget) {
        Slot slot = getSlot(widget);
        remove(slot);
        widgetToSlot.remove(widget);
        return slot;
    }

    public Slot getSlot(Widget widget) {
        Slot slot = widgetToSlot.get(widget);
        if (slot == null) {
            slot = new Slot(widget);
            widgetToSlot.put(widget, slot);
        }
        return slot;
    }

    protected static class Slot extends SimplePanel {

        public enum CaptionPosition {
            TOP, RIGHT, BOTTOM, LEFT
        }

        private static final String ALIGN_CLASS_PREFIX = "v-align-";

        private DivElement spacer;

        private Element caption;
        private Element captionText;
        private Element captionWrap;
        private Element icon;
        private CaptionPosition captionPosition = CaptionPosition.TOP;

        private AlignmentInfo alignment;
        private double expandRatio = -1;

        public Slot(Widget widget) {
            setWidget(widget);
            setStylePrimaryName("v-slot");
        }

        public AlignmentInfo getAlignment() {
            return alignment;
        }

        public void setAlignment(AlignmentInfo alignment) {
            this.alignment = alignment;

            if (alignment.isHorizontalCenter()) {
                addStyleName(ALIGN_CLASS_PREFIX + "center");
                removeStyleName(ALIGN_CLASS_PREFIX + "right");
            } else if (alignment.isRight()) {
                addStyleName(ALIGN_CLASS_PREFIX + "right");
                removeStyleName(ALIGN_CLASS_PREFIX + "center");
            } else {
                removeStyleName(ALIGN_CLASS_PREFIX + "right");
                removeStyleName(ALIGN_CLASS_PREFIX + "center");
            }
            if (alignment.isVerticalCenter()) {
                addStyleName(ALIGN_CLASS_PREFIX + "middle");
                removeStyleName(ALIGN_CLASS_PREFIX + "bottom");
            } else if (alignment.isBottom()) {
                addStyleName(ALIGN_CLASS_PREFIX + "bottom");
                removeStyleName(ALIGN_CLASS_PREFIX + "middle");
            } else {
                removeStyleName(ALIGN_CLASS_PREFIX + "middle");
                removeStyleName(ALIGN_CLASS_PREFIX + "bottom");
            }
        }

        public void setExpandRatio(double expandRatio) {
            this.expandRatio = expandRatio;
        }

        public double getExpandRatio() {
            return expandRatio;
        }

        public void setSpacing(boolean spacing) {
            if (spacing && spacer == null) {
                spacer = Document.get().createDivElement();
                spacer.addClassName("v-spacing");
                getElement().getParentElement().insertBefore(spacer,
                        getElement());
            } else if (!spacing && spacer != null) {
                spacer.removeFromParent();
                spacer = null;
            }
        }

        protected int getSpacingSize(boolean vertical) {
            if (spacer == null) {
                return 0;
            }
            if (vertical) {
                return spacer.getOffsetHeight();
            } else {
                return spacer.getOffsetWidth();
            }
        }

        public void setCaptionPosition(CaptionPosition captionPosition) {
            this.captionPosition = captionPosition;
            if (caption == null) {
                return;
            }
            if (captionPosition == CaptionPosition.BOTTOM
                    || captionPosition == CaptionPosition.RIGHT) {
                captionWrap.appendChild(caption);
            } else {
                captionWrap.insertFirst(caption);
            }
            captionWrap.addClassName("v-caption-on-"
                    + captionPosition.name().toLowerCase());
        }

        public void setCaption(String captionText, String iconUrl,
                List<String> styles) {

            // TODO place for optimization: check if any of these have changed
            // since last time, and only run those changes

            // Caption wrappers
            if (captionText != null || iconUrl != null) {
                if (caption == null) {
                    caption = DOM.createDiv();
                    captionWrap = DOM.createDiv();
                    captionWrap.addClassName("v-paintable");
                    captionWrap.addClassName("v-has-caption");
                    getElement().appendChild(captionWrap);
                    captionWrap.appendChild(getWidget().getElement());
                    setCaptionPosition(captionPosition);
                }
            } else if (caption != null) {
                getElement().appendChild(getWidget().getElement());
                captionWrap.removeFromParent();
                caption = null;
                captionWrap = null;
            }

            // Caption text
            if (captionText != null) {
                if (this.captionText == null) {
                    this.captionText = DOM.createSpan();
                    this.captionText.addClassName("v-captiontext");
                    caption.appendChild(this.captionText);
                }
                this.captionText.setInnerText(captionText);
            } else if (this.captionText != null) {
                this.captionText.removeFromParent();
                this.captionText = null;
            }

            // Icon
            if (iconUrl != null) {
                if (icon == null) {
                    icon = DOM.createImg();
                    icon.setClassName("v-icon");
                    caption.insertFirst(icon);
                }
                icon.setAttribute("src", iconUrl);
            } else if (icon != null) {
                icon.removeFromParent();
                icon = null;
            }

            // Styles
            caption.setClassName("v-caption");
            for (String style : styles) {
                caption.addClassName("v-caption-" + style);
            }

            // TODO add extra styles to captionWrap as well?

            // updateSize();

        }

        public void updateSize() {
            if (caption == null) {
                return;
            }

            VConsole.log("####################### updateSize");

            Style style = captionWrap.getStyle();

            style.clearWidth();
            style.clearHeight();
            style.clearPaddingTop();
            style.clearPaddingRight();
            style.clearPaddingBottom();
            style.clearPaddingLeft();

            caption.getStyle().clearMarginTop();
            caption.getStyle().clearMarginRight();
            caption.getStyle().clearMarginBottom();
            caption.getStyle().clearMarginLeft();

            captionWrap.removeClassName("v-has-width");
            captionWrap.removeClassName("v-has-height");

            // Relative sized widgets need extra calculations
            if (getWidget().getElement().getStyle().getWidth().endsWith("%")) {
                if (captionPosition == CaptionPosition.LEFT) {
                    int offset = caption.getOffsetWidth();
                    style.setPaddingLeft(offset, Unit.PX);
                    caption.getStyle().setMarginLeft(-offset, Unit.PX);
                } else if (captionPosition == CaptionPosition.RIGHT) {
                    int offset = caption.getOffsetWidth();
                    style.setPaddingRight(offset, Unit.PX);
                    caption.getStyle().setMarginRight(-offset, Unit.PX);
                }
                captionWrap.addClassName("v-has-width");
                style.setProperty("width", getWidget().getElement().getStyle()
                        .getWidth());
            }
            if (getWidget().getElement().getStyle().getHeight().endsWith("%")) {
                if (captionPosition == CaptionPosition.TOP) {
                    int offset = caption.getOffsetHeight();
                    style.setPaddingTop(offset, Unit.PX);
                    caption.getStyle().setMarginTop(-offset, Unit.PX);
                } else if (captionPosition == CaptionPosition.BOTTOM) {
                    int offset = caption.getOffsetHeight();
                    style.setPaddingBottom(offset, Unit.PX);
                    caption.getStyle().setMarginBottom(-offset, Unit.PX);
                }
                captionWrap.addClassName("v-has-height");
                style.setProperty("height", getWidget().getElement().getStyle()
                        .getHeight());
            }
        }

        @Override
        protected void onDetach() {
            if (spacer != null) {
                spacer.removeFromParent();
            }
            super.onDetach();
        }

    }

    private void toggleStyleName(String name, boolean enabled) {
        if (enabled) {
            addStyleName(name);
        } else {
            removeStyleName(name);
        }
    }

    void setMargin(VMarginInfo marginInfo) {
        toggleStyleName("v-margin-top", marginInfo.hasTop());
        toggleStyleName("v-margin-right", marginInfo.hasRight());
        toggleStyleName("v-margin-bottom", marginInfo.hasBottom());
        toggleStyleName("v-margin-left", marginInfo.hasLeft());
    }

    protected void setSpacing(boolean spacingEnabled) {
        spacing = spacingEnabled;
        for (Slot slot : widgetToSlot.values()) {
            if (getWidgetIndex(slot) > 0) {
                slot.setSpacing(spacingEnabled);
            }
        }
    }

    private boolean recalculateExpandsScheduled = false;

    public void recalculateExpands() {
        if (!recalculateExpandsScheduled) {
            Scheduler.get().scheduleDeferred(calculateExpands);
            recalculateExpandsScheduled = true;
        }
    }

    private ScheduledCommand calculateExpands = new ScheduledCommand() {
        public void execute() {
            double total = 0;
            for (Slot slot : widgetToSlot.values()) {
                if (slot.getExpandRatio() > -1) {
                    total += slot.getExpandRatio();
                } else {
                    if (vertical) {
                        slot.getElement().getStyle().clearHeight();
                    } else {
                        slot.getElement().getStyle().clearWidth();
                    }
                }
            }
            for (Slot slot : widgetToSlot.values()) {
                if (slot.getExpandRatio() > -1) {
                    if (vertical) {
                        slot.setHeight((100 * (slot.getExpandRatio() / total))
                                + "%");
                    } else {
                        slot.setWidth((100 * (slot.getExpandRatio() / total))
                                + "%");
                    }
                }
            }
            recalculateExpandsScheduled = false;
        }
    };

    private Element expandWrapper;

    private boolean recalculateUsedSpaceScheduled = false;

    public void recalculateUsedSpace() {
        if (!recalculateUsedSpaceScheduled) {
            Scheduler.get().scheduleDeferred(updateExpandSlotSize);
            recalculateUsedSpaceScheduled = true;
        }
    }

    private ScheduledCommand updateExpandSlotSize = new ScheduledCommand() {
        public void execute() {
            boolean isExpanding = false;
            for (Widget w : getChildren()) {
                if (((Slot) w).getExpandRatio() > -1) {
                    isExpanding = true;
                } else {
                    if (vertical) {
                        w.getElement().getStyle().clearHeight();
                    } else {
                        w.getElement().getStyle().clearWidth();
                    }
                }
                w.getElement().getStyle().clearMarginLeft();
                w.getElement().getStyle().clearMarginTop();
            }
            if (isExpanding) {
                if (expandWrapper == null) {
                    expandWrapper = DOM.createDiv();
                    expandWrapper.setClassName("v-box-expand");
                    for (; getElement().getChildCount() > 0;) {
                        Node el = getElement().getChild(0);
                        expandWrapper.appendChild(el);
                    }
                    getElement().appendChild(expandWrapper);
                }

                int totalSize = 0;
                for (Widget w : getChildren()) {
                    Slot slot = (Slot) w;
                    if (slot.getExpandRatio() == -1) {
                        totalSize += vertical ? slot.getOffsetHeight() : slot
                                .getOffsetWidth();
                    }
                    totalSize += slot.getSpacingSize(vertical);
                }

                // When we set the margin to the first child, we don't need
                // overflow:hidden in the layout root element, since the wrapper
                // would otherwise be placed outside of the layout root element
                // and block events on elements below it.
                if (vertical) {
                    expandWrapper.getStyle().setPaddingTop(totalSize, Unit.PX);
                    expandWrapper.getFirstChildElement().getStyle()
                            .setMarginTop(-totalSize, Unit.PX);
                } else {
                    expandWrapper.getStyle().setPaddingLeft(totalSize, Unit.PX);
                    expandWrapper.getFirstChildElement().getStyle()
                            .setMarginLeft(-totalSize, Unit.PX);
                }
                calculateExpands.execute();

            } else if (expandWrapper != null) {
                for (; expandWrapper.getChildCount() > 0;) {
                    Node el = expandWrapper.getChild(0);
                    getElement().appendChild(el);
                    if (vertical) {
                        ((Element) el.cast()).getStyle().clearHeight();
                    } else {
                        ((Element) el.cast()).getStyle().clearWidth();
                    }
                }
                expandWrapper.removeFromParent();
                expandWrapper = null;
            }

            recalculateUsedSpaceScheduled = false;
        }
    };

    private boolean recalculateLayoutHeightScheduled = false;

    public void recalculateLayoutHeight() {
        if (vertical || getStyleName().contains("v-has-height")) {
            return;
        }
        if (!recalculateLayoutHeightScheduled) {
            recalculateLayoutHeightScheduled = true;
            Scheduler.get().scheduleDeferred(calculateLayoutHeight);
        }
    }

    private ScheduledCommand calculateLayoutHeight = new ScheduledCommand() {
        public void execute() {
            // Clear previous height
            getElement().getStyle().clearHeight();

            boolean hasRelativeHeight = false;
            boolean hasVAlign = false;

            for (Widget slot : getChildren()) {
                Widget widget = ((Slot) slot).getWidget();
                String h = widget.getElement().getStyle().getHeight();
                if (h != null && h.indexOf("%") > -1) {
                    hasRelativeHeight = true;
                }
                AlignmentInfo a = ((Slot) slot).getAlignment();
                if (a.isVerticalCenter() || a.isBottom()) {
                    hasVAlign = true;
                }
            }

            if (hasRelativeHeight || hasVAlign) {
                int newHeight = getOffsetHeight();
                VBoxLayout.this.getElement().getStyle()
                        .setHeight(newHeight, Unit.PX);
            }

            recalculateLayoutHeightScheduled = false;
        }
    };
}

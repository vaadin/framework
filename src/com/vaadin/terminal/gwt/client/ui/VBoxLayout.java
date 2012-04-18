/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.LayoutManager;

public class VBoxLayout extends FlowPanel {

    private static final String ALIGN_CLASS_PREFIX = "v-align-";

    protected boolean spacing = false;

    protected boolean vertical = true;

    protected boolean definedHeight = false;

    private Map<Widget, Slot> widgetToSlot = new HashMap<Widget, Slot>();

    private LayoutManager layoutManager;

    public VBoxLayout() {
        setStylePrimaryName("v-boxlayout");
        setVertical(true);
    }

    public void setVertical(boolean isVertical) {
        vertical = isVertical;
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

    public enum CaptionPosition {
        TOP, RIGHT, BOTTOM, LEFT
    }

    protected class Slot extends SimplePanel {

        private Element spacer;

        private Element captionWrap;
        private Element caption;
        private Element captionText;
        private Icon icon;
        private Element errorIcon;
        private Element requiredIcon;

        // Caption is placed after component unless there is some part which
        // moves it above.
        private CaptionPosition captionPosition = CaptionPosition.RIGHT;

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
                spacer = DOM.createDiv();
                spacer.addClassName("v-spacing");
                getElement().getParentElement().insertBefore(spacer,
                        getElement());
            } else if (!spacing && spacer != null) {
                spacer.removeFromParent();
                spacer = null;
            }
        }

        public Element getSpacingElement() {
            return spacer;
        }

        protected int getSpacingSize(boolean vertical) {
            if (spacer == null) {
                return 0;
            }

            if (layoutManager != null) {
                if (vertical) {
                    return layoutManager.getOuterHeight(spacer);
                } else {
                    return layoutManager.getOuterWidth(spacer);
                }
            }
            // TODO place for optimization (in expense of theme
            // flexibility): only measure one of the elements and cache the
            // value
            return vertical ? spacer.getOffsetHeight() : spacer
                    .getOffsetWidth();
            // }
        }

        public void setCaptionPosition(CaptionPosition captionPosition) {
            if (caption == null) {
                return;
            }

            captionWrap.removeClassName("v-caption-on-"
                    + this.captionPosition.name().toLowerCase());

            this.captionPosition = captionPosition;
            if (captionPosition == CaptionPosition.BOTTOM
                    || captionPosition == CaptionPosition.RIGHT) {
                captionWrap.appendChild(caption);
            } else {
                captionWrap.insertFirst(caption);
            }

            captionWrap.addClassName("v-caption-on-"
                    + captionPosition.name().toLowerCase());
        }

        public CaptionPosition getCaptionPosition() {
            return captionPosition;
        }

        // TODO refactor VCaption and use that instead: creates a tight coupling
        // between this layout and Vaadin, but it's already coupled
        public void setCaption(String captionText, String iconUrl,
                List<String> styles, String error, boolean showError,
                boolean required, boolean enabled) {

            // TODO place for optimization: check if any of these have changed
            // since last time, and only run those changes

            // Caption wrappers
            if (captionText != null || iconUrl != null || error != null
                    || required) {
                if (caption == null) {
                    caption = DOM.createDiv();
                    captionWrap = DOM.createDiv();
                    captionWrap.addClassName("v-connector");
                    captionWrap.addClassName("v-has-caption");
                    getElement().appendChild(captionWrap);
                    captionWrap.appendChild(getWidget().getElement());
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
                if (captionText.equals("")) {
                    this.captionText.setInnerHTML("&nbsp;");
                } else {
                    this.captionText.setInnerText(captionText);
                }
            } else if (this.captionText != null) {
                this.captionText.removeFromParent();
                this.captionText = null;
            }

            // Icon
            if (iconUrl != null) {
                if (icon == null) {
                    icon = new Icon();
                    // icon = DOM.createImg();
                    // icon.setClassName("v-icon");
                    caption.insertFirst(icon.getElement());
                }
                // icon.setAttribute("src", iconUrl);
                icon.setUri(iconUrl);
            } else if (icon != null) {
                icon.getElement().removeFromParent();
                icon = null;
            }

            // Required
            if (required) {
                if (requiredIcon == null) {
                    requiredIcon = DOM.createSpan();
                    // TODO decide something better
                    requiredIcon.setInnerHTML("*");
                    requiredIcon.setClassName("v-required-field-indicator");
                }
                caption.appendChild(requiredIcon);
            } else if (requiredIcon != null) {
                requiredIcon.removeFromParent();
                requiredIcon = null;
            }

            // Error
            if (error != null && showError) {
                if (errorIcon == null) {
                    errorIcon = DOM.createSpan();
                    errorIcon.setClassName("v-errorindicator");
                }
                caption.appendChild(errorIcon);
            } else if (errorIcon != null) {
                errorIcon.removeFromParent();
                errorIcon = null;
            }

            if (caption != null) {
                // Styles
                caption.setClassName("v-caption");

                if (styles != null) {
                    for (String style : styles) {
                        caption.addClassName("v-caption-" + style);
                    }
                }

                if (enabled) {
                    caption.removeClassName("v-disabled");
                } else {
                    caption.addClassName("v-disabled");
                }

                // Caption position
                if (captionText != null || iconUrl != null) {
                    setCaptionPosition(CaptionPosition.TOP);
                } else {
                    setCaptionPosition(CaptionPosition.RIGHT);
                }
            }

            // TODO theme flexibility: add extra styles to captionWrap as well?

        }

        public boolean hasCaption() {
            return caption != null;
        }

        public Element getCaptionElement() {
            return caption;
        }

        public void setRelativeWidth(boolean relativeWidth) {
            updateRelativeSize(relativeWidth, "width");
        }

        public void setRelativeHeight(boolean relativeHeight) {
            updateRelativeSize(relativeHeight, "height");
        }

        private void updateRelativeSize(boolean isRelativeSize, String direction) {
            if (isRelativeSize && hasCaption()) {
                captionWrap.getStyle().setProperty(
                        direction,
                        getWidget().getElement().getStyle()
                                .getProperty(direction));
                captionWrap.addClassName("v-has-" + direction);
            } else if (hasCaption()) {
                captionWrap.getStyle().clearHeight();
                captionWrap.removeClassName("v-has-" + direction);
                captionWrap.getStyle().clearPaddingTop();
                captionWrap.getStyle().clearPaddingRight();
                captionWrap.getStyle().clearPaddingBottom();
                captionWrap.getStyle().clearPaddingLeft();
                caption.getStyle().clearMarginTop();
                caption.getStyle().clearMarginRight();
                caption.getStyle().clearMarginBottom();
                caption.getStyle().clearMarginLeft();
            }
        }

        @Override
        public void onBrowserEvent(Event event) {
            super.onBrowserEvent(event);
            if (DOM.eventGetType(event) == Event.ONLOAD
                    && icon.getElement() == DOM.eventGetTarget(event)) {
                if (layoutManager != null) {
                    layoutManager.layoutLater();
                } else {
                    updateCaptionOffset(caption);
                }
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

    protected class Icon extends UIObject {
        public static final String CLASSNAME = "v-icon";
        private String myUrl;

        public Icon() {
            setElement(DOM.createImg());
            DOM.setElementProperty(getElement(), "alt", "");
            setStyleName(CLASSNAME);
        }

        public void setUri(String url) {
            if (!url.equals(myUrl)) {
                /*
                 * Start sinking onload events, widgets responsibility to react.
                 * We must do this BEFORE we set src as IE fires the event
                 * immediately if the image is found in cache (#2592).
                 */
                sinkEvents(Event.ONLOAD);

                DOM.setElementProperty(getElement(), "src", url);
                myUrl = url;
            }
        }

    }

    void setLayoutManager(LayoutManager manager) {
        layoutManager = manager;
    }

    private static final RegExp captionPositionRegexp = RegExp
            .compile("v-caption-on-(\\S+)");

    CaptionPosition getCaptionPositionFromElement(Element captionWrap) {
        // Get caption position from the classname
        MatchResult matcher = captionPositionRegexp.exec(captionWrap
                .getClassName());
        if (matcher == null || matcher.getGroupCount() < 2) {
            return CaptionPosition.TOP;
        }
        String captionClass = matcher.getGroup(1);
        CaptionPosition captionPosition = CaptionPosition.valueOf(
                CaptionPosition.class, captionClass.toUpperCase());
        return captionPosition;
    }

    void updateCaptionOffset(Element caption) {

        Element captionWrap = caption.getParentElement().cast();

        Style captionWrapStyle = captionWrap.getStyle();
        captionWrapStyle.clearPaddingTop();
        captionWrapStyle.clearPaddingRight();
        captionWrapStyle.clearPaddingBottom();
        captionWrapStyle.clearPaddingLeft();

        Style captionStyle = caption.getStyle();
        captionStyle.clearMarginTop();
        captionStyle.clearMarginRight();
        captionStyle.clearMarginBottom();
        captionStyle.clearMarginLeft();

        // Get caption position from the classname
        CaptionPosition captionPosition = getCaptionPositionFromElement(captionWrap);

        if (captionPosition == CaptionPosition.LEFT
                || captionPosition == CaptionPosition.RIGHT) {
            int captionWidth;
            if (layoutManager != null) {
                captionWidth = layoutManager.getOuterWidth(caption)
                        - layoutManager.getMarginWidth(caption);
            } else {
                captionWidth = caption.getOffsetWidth();
            }
            if (captionWidth > 0) {
                if (captionPosition == CaptionPosition.LEFT) {
                    captionWrapStyle.setPaddingLeft(captionWidth, Unit.PX);
                    captionStyle.setMarginLeft(-captionWidth, Unit.PX);
                } else {
                    captionWrapStyle.setPaddingRight(captionWidth, Unit.PX);
                    captionStyle.setMarginRight(-captionWidth, Unit.PX);
                }
            }
        }
        if (captionPosition == CaptionPosition.TOP
                || captionPosition == CaptionPosition.BOTTOM) {
            int captionHeight;
            if (layoutManager != null) {
                captionHeight = layoutManager.getOuterHeight(caption)
                        - layoutManager.getMarginHeight(caption);
            } else {
                captionHeight = caption.getOffsetHeight();
            }
            if (captionHeight > 0) {
                if (captionPosition == CaptionPosition.TOP) {
                    captionWrapStyle.setPaddingTop(captionHeight, Unit.PX);
                    captionStyle.setMarginTop(-captionHeight, Unit.PX);
                } else {
                    captionWrapStyle.setPaddingBottom(captionHeight, Unit.PX);
                    captionStyle.setMarginBottom(-captionHeight, Unit.PX);
                }
            }
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

    private void recalculateExpands() {
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
                    slot.setWidth((100 * (slot.getExpandRatio() / total)) + "%");
                }
            }
        }
    }

    private Element expandWrapper;

    void clearExpand() {
        if (expandWrapper != null) {
            for (; expandWrapper.getChildCount() > 0;) {
                Element el = expandWrapper.getChild(0).cast();
                getElement().appendChild(el);
                if (vertical) {
                    el.getStyle().clearHeight();
                    el.getStyle().clearMarginTop();
                } else {
                    el.getStyle().clearWidth();
                    el.getStyle().clearMarginLeft();
                }
            }
            expandWrapper.removeFromParent();
            expandWrapper = null;
        }
    }

    public void updateExpand() {
        boolean isExpanding = false;
        for (Widget slot : getChildren()) {
            if (((Slot) slot).getExpandRatio() > -1) {
                isExpanding = true;
            } else {
                if (vertical) {
                    slot.getElement().getStyle().clearHeight();
                } else {
                    slot.getElement().getStyle().clearWidth();
                }
            }
            slot.getElement().getStyle().clearMarginLeft();
            slot.getElement().getStyle().clearMarginTop();
        }

        if (isExpanding) {
            if (expandWrapper == null) {
                expandWrapper = DOM.createDiv();
                expandWrapper.setClassName("v-expand");
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
                    if (layoutManager != null) {
                        // TODO check caption position
                        if (vertical) {
                            int size = layoutManager.getOuterHeight(slot
                                    .getWidget().getElement())
                                    - layoutManager.getMarginHeight(slot
                                            .getWidget().getElement());
                            if (slot.hasCaption()) {
                                size += layoutManager.getOuterHeight(slot
                                        .getCaptionElement())
                                        - layoutManager.getMarginHeight(slot
                                                .getCaptionElement());
                            }
                            if (size > 0) {
                                totalSize += size;
                            }
                        } else {
                            int max = -1;
                            max = layoutManager.getOuterWidth(slot.getWidget()
                                    .getElement())
                                    - layoutManager.getMarginWidth(slot
                                            .getWidget().getElement());
                            if (slot.hasCaption()) {
                                int max2 = layoutManager.getOuterWidth(slot
                                        .getCaptionElement())
                                        - layoutManager.getMarginWidth(slot
                                                .getCaptionElement());
                                max = Math.max(max, max2);
                            }
                            if (max > 0) {
                                totalSize += max;
                            }
                        }
                    } else {
                        totalSize += vertical ? slot.getOffsetHeight() : slot
                                .getOffsetWidth();
                    }
                }
                // TODO fails in Opera, always returns 0
                int spacingSize = slot.getSpacingSize(vertical);
                if (spacingSize > 0) {
                    totalSize += spacingSize;
                }
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

            recalculateExpands();
        }
    }

    public void recalculateLayoutHeight() {
        // Only needed if a horizontal layout is undefined high, and contains
        // relative height children or vertical alignments
        if (vertical || definedHeight) {
            return;
        }

        boolean hasRelativeHeightChildren = false;
        boolean hasVAlign = false;

        for (Widget slot : getChildren()) {
            Widget widget = ((Slot) slot).getWidget();
            String h = widget.getElement().getStyle().getHeight();
            if (h != null && h.indexOf("%") > -1) {
                hasRelativeHeightChildren = true;
            }
            AlignmentInfo a = ((Slot) slot).getAlignment();
            if (a != null && (a.isVerticalCenter() || a.isBottom())) {
                hasVAlign = true;
            }
        }

        if (hasRelativeHeightChildren || hasVAlign) {
            int newHeight;
            if (layoutManager != null) {
                newHeight = layoutManager.getOuterHeight(getElement())
                        - layoutManager.getMarginHeight(getElement());
            } else {
                newHeight = getElement().getOffsetHeight();
            }
            VBoxLayout.this.getElement().getStyle()
                    .setHeight(newHeight, Unit.PX);
        }

    }

    void clearHeight() {
        getElement().getStyle().clearHeight();
    }

    @Override
    public void setHeight(String height) {
        super.setHeight(height);
        definedHeight = (height != null && !"".equals(height));
    }
}

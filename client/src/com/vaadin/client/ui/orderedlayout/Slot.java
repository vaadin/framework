package com.vaadin.client.ui.orderedlayout;

import java.util.List;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.LayoutManager;
import com.vaadin.client.StyleConstants;
import com.vaadin.client.ui.layout.ElementResizeListener;
import com.vaadin.client.ui.orderedlayout.VAbstractOrderedLayout.CaptionPosition;
import com.vaadin.client.ui.orderedlayout.VAbstractOrderedLayout.Icon;
import com.vaadin.shared.ui.AlignmentInfo;

/**
 * Represents a slot which contains the actual widget in the layout.
 */
public final class Slot extends SimplePanel {

    private final VAbstractOrderedLayout layout;

    public static final String SLOT_CLASSNAME = "v-slot";

    private Element spacer;
    private Element captionWrap;
    private Element caption;
    private Element captionText;
    private Icon icon;
    private Element errorIcon;
    private Element requiredIcon;

    private ElementResizeListener captionResizeListener;

    private ElementResizeListener widgetResizeListener;

    private ElementResizeListener spacingResizeListener;

    // Caption is placed after component unless there is some part which
    // moves it above.
    private CaptionPosition captionPosition = CaptionPosition.RIGHT;

    private AlignmentInfo alignment;

    private double expandRatio = -1;

    private boolean relativeWidth = false;

    private boolean relativeHeight = false;

    /**
     * Constructor
     * 
     * @param widget
     *            The widget to put in the slot
     * @param layout
     *            The layout the slot is attached to
     * 
     * @param layoutManager
     *            The layout manager used by the layout
     */
    public Slot(VAbstractOrderedLayout layout, Widget widget) {
        this.layout = layout;
        setStyleName(SLOT_CLASSNAME);
        setWidget(widget);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.user.client.ui.SimplePanel#remove(com.google.gwt.user
     * .client.ui.Widget)
     */
    @Override
    public boolean remove(Widget w) {
        detachListeners();
        return super.remove(w);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.user.client.ui.SimplePanel#setWidget(com.google.gwt
     * .user.client.ui.Widget)
     */
    @Override
    public void setWidget(Widget w) {
        detachListeners();
        super.setWidget(w);
        attachListeners();
    }

    /**
     * Attached resize listeners to the widget, caption and spacing elements
     */
    private void attachListeners() {
        if (getWidget() != null && layout.getLayoutManager() != null) {
            LayoutManager lm = layout.getLayoutManager();
            if (getCaptionElement() != null
                    && captionResizeListener != null) {
                lm.addElementResizeListener(getCaptionElement(),
                        captionResizeListener);
            }
            if (widgetResizeListener != null) {
                lm.addElementResizeListener(getWidget().getElement(),
                        widgetResizeListener);
            }
            if (getSpacingElement() != null
                    && spacingResizeListener != null) {
                lm.addElementResizeListener(getSpacingElement(),
                        spacingResizeListener);
            }
        }
    }

    /**
     * Detaches resize listeners from the widget, caption and spacing
     * elements
     */
    protected void detachListeners() {
        if (getWidget() != null && layout.getLayoutManager() != null) {
            LayoutManager lm = layout.getLayoutManager();
            if (getCaptionElement() != null
                    && captionResizeListener != null) {
                lm.removeElementResizeListener(getCaptionElement(),
                        captionResizeListener);
            }
            if (widgetResizeListener != null) {
                lm.removeElementResizeListener(getWidget().getElement(),
                        widgetResizeListener);
            }
            if (getSpacingElement() != null
                    && spacingResizeListener != null) {
                lm.removeElementResizeListener(getSpacingElement(),
                        spacingResizeListener);
            }
        }
    }

    /**
     * Returns the caption resize listener that monitors changes in slot
     * captions
     */
    public ElementResizeListener getCaptionResizeListener() {
        return captionResizeListener;
    }

    /**
     * Set the caption resize listener that monitors changes in slot
     * captions
     * 
     * @param captionResizeListener
     *            The listener to use or null to remove listener
     */
    public void setCaptionResizeListener(
            ElementResizeListener captionResizeListener) {
        detachListeners();
        this.captionResizeListener = captionResizeListener;
        attachListeners();
    }

    /**
     * Get the resize listener which monitors changes in size of the widgets
     * contained in the slots
     */
    public ElementResizeListener getWidgetResizeListener() {
        return widgetResizeListener;
    }

    /**
     * Set the resize listener that monitors size changes in the widgets
     * 
     * @param widgetResizeListener
     *            The listener to set or null to remove the listener
     */
    public void setWidgetResizeListener(
            ElementResizeListener widgetResizeListener) {
        detachListeners();
        this.widgetResizeListener = widgetResizeListener;
        attachListeners();
    }

    /**
     * Get the listener that monitors size changes in the spacing
     */
    public ElementResizeListener getSpacingResizeListener() {
        return spacingResizeListener;
    }

    /**
     * Set the listener that monitors size changes in the spacing
     * 
     * @param spacingResizeListener
     *            The listener to set or null to remove the listener
     */
    public void setSpacingResizeListener(
            ElementResizeListener spacingResizeListener) {
        detachListeners();
        this.spacingResizeListener = spacingResizeListener;
        attachListeners();
    }

    /**
     * Returns the alignment for the slot.
     * 
     */
    public AlignmentInfo getAlignment() {
        return alignment;
    }

    /**
     * Sets the style names for the slot containing the widget
     * 
     * @param stylenames
     *            The style names for the slot
     */
    protected void setStyleNames(String... stylenames) {
        setStyleName(SLOT_CLASSNAME);
        if (stylenames != null) {
            for (String stylename : stylenames) {
                addStyleDependentName(stylename);
            }
        }

        // Ensure alignment style names are correct
        setAlignment(alignment);
    }

    /**
     * Sets how the widget is aligned inside the slot
     * 
     * @param alignment
     *            The alignment inside the slot
     */
    public void setAlignment(AlignmentInfo alignment) {
        this.alignment = alignment;

        if (alignment != null && alignment.isHorizontalCenter()) {
            addStyleName(VAbstractOrderedLayout.ALIGN_CLASS_PREFIX + "center");
            removeStyleName(VAbstractOrderedLayout.ALIGN_CLASS_PREFIX + "right");
        } else if (alignment != null && alignment.isRight()) {
            addStyleName(VAbstractOrderedLayout.ALIGN_CLASS_PREFIX + "right");
            removeStyleName(VAbstractOrderedLayout.ALIGN_CLASS_PREFIX + "center");
        } else {
            removeStyleName(VAbstractOrderedLayout.ALIGN_CLASS_PREFIX + "right");
            removeStyleName(VAbstractOrderedLayout.ALIGN_CLASS_PREFIX + "center");
        }

        if (alignment != null && alignment.isVerticalCenter()) {
            addStyleName(VAbstractOrderedLayout.ALIGN_CLASS_PREFIX + "middle");
            removeStyleName(VAbstractOrderedLayout.ALIGN_CLASS_PREFIX + "bottom");
        } else if (alignment != null && alignment.isBottom()) {
            addStyleName(VAbstractOrderedLayout.ALIGN_CLASS_PREFIX + "bottom");
            removeStyleName(VAbstractOrderedLayout.ALIGN_CLASS_PREFIX + "middle");
        } else {
            removeStyleName(VAbstractOrderedLayout.ALIGN_CLASS_PREFIX + "middle");
            removeStyleName(VAbstractOrderedLayout.ALIGN_CLASS_PREFIX + "bottom");
        }
    }

    /**
     * Set how the slot should be expanded relative to the other slots
     * 
     * @param expandRatio
     *            The ratio of the space the slot should occupy
     * 
     */
    public void setExpandRatio(double expandRatio) {
        this.expandRatio = expandRatio;
    }

    /**
     * Get the expand ratio for the slot. The expand ratio describes how the
     * slot should be resized compared to other slots in the layout
     * 
     * @return
     */
    public double getExpandRatio() {
        return expandRatio;
    }

    /**
     * Set the spacing for the slot. The spacing determines if there should
     * be empty space around the slot when the slot.
     * 
     * @param spacing
     *            Should spacing be enabled
     */
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

    /**
     * Get the element which is added to make the spacing
     * 
     * @return
     */
    public Element getSpacingElement() {
        return spacer;
    }

    /**
     * Does the slot have spacing
     */
    public boolean hasSpacing() {
        return getSpacingElement() != null;
    }

    /**
     * Get the vertical amount in pixels of the spacing
     */
    public int getVerticalSpacing() {
        if (spacer == null) {
            return 0;
        } else if (layout.getLayoutManager() != null) {
            return layout.getLayoutManager().getOuterHeight(spacer);
        }
        return spacer.getOffsetHeight();
    }

    /**
     * Get the horizontal amount of pixels of the spacing
     * 
     * @return
     */
    public int getHorizontalSpacing() {
        if (spacer == null) {
            return 0;
        } else if (layout.getLayoutManager() != null) {
            return layout.getLayoutManager().getOuterWidth(spacer);
        }
        return spacer.getOffsetWidth();
    }

    /**
     * Set the position of the caption relative to the slot
     * 
     * @param captionPosition
     *            The position of the caption
     */
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

    /**
     * Get the position of the caption relative to the slot
     */
    public CaptionPosition getCaptionPosition() {
        return captionPosition;
    }

    /**
     * Set the caption of the slot
     * 
     * @param captionText
     *            The text of the caption
     * @param iconUrl
     *            The icon URL
     * @param styles
     *            The style names
     * @param error
     *            The error message
     * @param showError
     *            Should the error message be shown
     * @param required
     *            Is the (field) required
     * @param enabled
     *            Is the component enabled
     */
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
                captionWrap.addClassName(StyleConstants.UI_WIDGET);
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
            if (captionText.trim().equals("")) {
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
                icon = new VAbstractOrderedLayout.Icon();
                caption.insertFirst(icon.getElement());
            }
            icon.setUri(iconUrl);
        } else if (icon != null) {
            icon.getElement().removeFromParent();
            icon = null;
        }

        // Required
        if (required) {
            if (requiredIcon == null) {
                requiredIcon = DOM.createSpan();
                // TODO decide something better (e.g. use CSS to insert the
                // character)
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
    }

    /**
     * Does the slot have a caption
     */
    public boolean hasCaption() {
        return caption != null;
    }

    /**
     * Get the slots caption element
     */
    public Element getCaptionElement() {
        return caption;
    }

    /**
     * Set if the slot has a relative width
     * 
     * @param relativeWidth
     *            True if slot uses relative width, false if the slot has a
     *            static width
     */
    public void setRelativeWidth(boolean relativeWidth) {
        this.relativeWidth = relativeWidth;
        updateRelativeSize(relativeWidth, "width");
    }

    /**
     * Slot has a relative width
     */
    public boolean isRelativeWidth() {
        return relativeWidth;
    }

    /**
     * Set if the slot has a relative height
     * 
     * @param relativeHeight
     *            True if the slot uses a relative height, false if the slot
     *            has a static height
     */
    public void setRelativeHeight(boolean relativeHeight) {
        this.relativeHeight = relativeHeight;
        updateRelativeSize(relativeHeight, "height");
    }

    /**
     * Slot has a relative height
     * 
     * @return
     */
    public boolean isRelativeHeight() {
        return relativeHeight;
    }

    /**
     * Updates the captions size if the slot is relative
     * 
     * @param isRelativeSize
     *            Is the slot relatived sized
     * @param direction
     *            The directorion of the caption
     */
    private void updateRelativeSize(boolean isRelativeSize, String direction) {
        if (isRelativeSize && hasCaption()) {
            captionWrap.getStyle().setProperty(
                    direction,
                    getWidget().getElement().getStyle()
                            .getProperty(direction));
            captionWrap.addClassName("v-has-" + direction);
        } else if (hasCaption()) {
            if (direction.equals("height")) {
                captionWrap.getStyle().clearHeight();
            } else {
                captionWrap.getStyle().clearWidth();
            }
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.user.client.ui.Widget#onBrowserEvent(com.google.gwt
     * .user.client.Event)
     */
    @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        if (DOM.eventGetType(event) == Event.ONLOAD
                && icon.getElement() == DOM.eventGetTarget(event)) {
            if (layout.getLayoutManager() != null) {
                layout.getLayoutManager().layoutLater();
            } else {
                layout.updateCaptionOffset(caption);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.user.client.ui.SimplePanel#getContainerElement()
     */
    @Override
    protected Element getContainerElement() {
        if (captionWrap == null) {
            return getElement();
        } else {
            return captionWrap;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.user.client.ui.Widget#onDetach()
     */
    @Override
    protected void onDetach() {
        if (spacer != null) {
            spacer.removeFromParent();
        }
        super.onDetach();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.user.client.ui.Widget#onAttach()
     */
    @Override
    protected void onAttach() {
        super.onAttach();
        if (spacer != null) {
            getElement().getParentElement().insertBefore(spacer,
                    getElement());
        }
    }
}
package com.vaadin.client.ui.colorpicker;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;

/**
 * Client side implementation for ColorPickerGradient.
 * 
 * @since 7.0.0
 * 
 */
public class VColorPickerGradient extends FocusPanel implements
        MouseDownHandler, MouseUpHandler, MouseMoveHandler {

    /** Set the CSS class name to allow styling. */
    public static final String CLASSNAME = "v-colorpicker-gradient";
    public static final String CLASSNAME_BACKGROUND = CLASSNAME + "-background";
    public static final String CLASSNAME_FOREGROUND = CLASSNAME + "-foreground";
    public static final String CLASSNAME_LOWERBOX = CLASSNAME + "-lowerbox";
    public static final String CLASSNAME_HIGHERBOX = CLASSNAME + "-higherbox";
    public static final String CLASSNAME_CONTAINER = CLASSNAME + "-container";
    public static final String CLASSNAME_CLICKLAYER = CLASSNAME + "-clicklayer";

    private final HTML background;
    private final HTML foreground;
    private final HTML lowercross;
    private final HTML highercross;
    private final HTML clicklayer;
    private final AbsolutePanel container;

    private boolean mouseIsDown = false;

    private int cursorX;
    private int cursorY;

    /**
     * Instantiates the client side component for a color picker gradient.
     */
    public VColorPickerGradient() {
        super();

        setStyleName(CLASSNAME);

        int width = 220;
        int height = 220;

        background = new HTML();
        background.setStyleName(CLASSNAME_BACKGROUND);
        background.setPixelSize(width, height);

        foreground = new HTML();
        foreground.setStyleName(CLASSNAME_FOREGROUND);
        foreground.setPixelSize(width, height);

        clicklayer = new HTML();
        clicklayer.setStyleName(CLASSNAME_CLICKLAYER);
        clicklayer.setPixelSize(width, height);
        clicklayer.addMouseDownHandler(this);
        clicklayer.addMouseUpHandler(this);
        clicklayer.addMouseMoveHandler(this);

        lowercross = new HTML();
        lowercross.setPixelSize(width / 2, height / 2);
        lowercross.setStyleName(CLASSNAME_LOWERBOX);

        highercross = new HTML();
        highercross.setPixelSize(width / 2, height / 2);
        highercross.setStyleName(CLASSNAME_HIGHERBOX);

        container = new AbsolutePanel();
        container.setStyleName(CLASSNAME_CONTAINER);
        container.setPixelSize(width, height);
        container.add(background, 0, 0);
        container.add(foreground, 0, 0);
        container.add(lowercross, 0, height / 2);
        container.add(highercross, width / 2, 0);
        container.add(clicklayer, 0, 0);

        add(container);
    }

    /**
     * Returns the latest x-coordinate for pressed-down mouse cursor.
     */
    protected int getCursorX() {
        return cursorX;
    }

    /**
     * Returns the latest y-coordinate for pressed-down mouse cursor.
     */
    protected int getCursorY() {
        return cursorY;
    }

    /**
     * Sets the given css color as the background.
     * 
     * @param bgColor
     */
    protected void setBGColor(String bgColor) {
        background.getElement().getStyle().setProperty("background", bgColor);
    }

    @Override
    public void onMouseDown(MouseDownEvent event) {
        event.preventDefault();

        mouseIsDown = true;
        setCursor(event.getX(), event.getY());
    }

    @Override
    public void onMouseUp(MouseUpEvent event) {
        event.preventDefault();
        mouseIsDown = false;
        setCursor(event.getX(), event.getY());

        cursorX = event.getX();
        cursorY = event.getY();
    }

    @Override
    public void onMouseMove(MouseMoveEvent event) {
        event.preventDefault();

        if (mouseIsDown) {
            setCursor(event.getX(), event.getY());
        }
    }

    /**
     * Sets the latest coordinates for pressed-down mouse cursor and updates the
     * cross elements.
     * 
     * @param x
     * @param y
     */
    public void setCursor(int x, int y) {
        cursorX = x;
        cursorY = y;
        if (x >= 0) {
            DOM.setStyleAttribute(lowercross.getElement(), "width",
                    String.valueOf(x) + "px");
        }
        if (y >= 0) {
            DOM.setStyleAttribute(lowercross.getElement(), "top",
                    String.valueOf(y) + "px");
        }
        if (y >= 0) {
            DOM.setStyleAttribute(lowercross.getElement(), "height",
                    String.valueOf((background.getOffsetHeight() - y)) + "px");
        }

        if (x >= 0) {
            DOM.setStyleAttribute(highercross.getElement(), "width",
                    String.valueOf((background.getOffsetWidth() - x)) + "px");
        }
        if (x >= 0) {
            DOM.setStyleAttribute(highercross.getElement(), "left",
                    String.valueOf(x) + "px");
        }
        if (y >= 0) {
            DOM.setStyleAttribute(highercross.getElement(), "height",
                    String.valueOf((y)) + "px");
        }
    }
}

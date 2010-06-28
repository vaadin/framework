/*
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;

/**
 * Represents a date selection component with a text field and a popup date
 * selector.
 * 
 * <b>Note:</b> To change the keyboard assignments used in the popup dialog you
 * should extend <code>com.vaadin.terminal.gwt.client.ui.VCalendarPanel</code>
 * and then pass set it by calling the
 * <code>setCalendarPanel(VCalendarPanel panel)</code> method.
 * 
 */
public class VPopupCalendar extends VTextualDate implements Paintable, Field,
        ClickHandler, CloseHandler<PopupPanel> {

    private final Button calendarToggle;

    private VCalendarPanel calendar;

    private final VOverlay popup;
    private boolean open = false;
    private boolean parsable = true;

    public VPopupCalendar() {
        super();

        calendarToggle = new Button();
        calendarToggle.setStyleName(CLASSNAME + "-button");
        calendarToggle.setText("");
        calendarToggle.addClickHandler(this);
        calendarToggle.getElement().setTabIndex(-1);
        add(calendarToggle);

        calendar = new VCalendarPanel(this);

        popup = new VOverlay(true, true, true);
        popup.setStyleName(VDateField.CLASSNAME + "-popup");
        popup.setWidget(calendar);
        popup.addCloseHandler(this);

        DOM.setElementProperty(calendar.getElement(), "id",
                "PID_VAADIN_POPUPCAL");

        sinkEvents(Event.ONKEYDOWN);

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.terminal.gwt.client.ui.VTextualDate#updateFromUIDL(com.vaadin
     * .terminal.gwt.client.UIDL,
     * com.vaadin.terminal.gwt.client.ApplicationConnection)
     */
    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        boolean lastReadOnlyState = readonly;
        parsable = uidl.getBooleanAttribute("parsable");

        super.updateFromUIDL(uidl, client);

        popup.setStyleName(VDateField.CLASSNAME + "-popup "
                + VDateField.CLASSNAME + "-"
                + resolutionToString(currentResolution));
        if (date != null) {
            calendar.updateCalendar();
        }
        calendarToggle.setEnabled(enabled);

        if (readonly) {
            calendarToggle.addStyleName(CLASSNAME + "-button-readonly");
        } else {
            calendarToggle.removeStyleName(CLASSNAME + "-button-readonly");
        }

        if (lastReadOnlyState != readonly) {
            updateWidth();
        }

        calendarToggle.setEnabled(true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.user.client.ui.UIObject#setStyleName(java.lang.String)
     */
    @Override
    public void setStyleName(String style) {
        // make sure the style is there before size calculation
        super.setStyleName(style + " " + CLASSNAME + "-popupcalendar");
    }

    /**
     * Set the popup panel to be displayed when clicking the calendar button.
     * This is usually used when we want to extend the VCalendarPanel and use
     * new keyboard bindings.
     * 
     * @param panel
     *            The custom calendar panel
     */
    public void setCalendarPanel(VCalendarPanel panel) {
        if (panel != null) {
            calendar = panel;
            calendar.updateCalendar();
        }
    }

    /**
     * Opens the calendar panel popup
     */
    public void openCalendarPanel() {

        if (!open && !readonly) {
            open = true;
            calendar.updateCalendar();

            // clear previous values
            popup.setWidth("");
            popup.setHeight("");
            popup.setPopupPositionAndShow(new PositionCallback() {
                public void setPosition(int offsetWidth, int offsetHeight) {
                    final int w = offsetWidth;
                    final int h = offsetHeight;
                    final int browserWindowWidth = Window.getClientWidth()
                            + Window.getScrollLeft();
                    final int browserWindowHeight = Window.getClientHeight()
                            + Window.getScrollTop();
                    int t = calendarToggle.getAbsoluteTop();
                    int l = calendarToggle.getAbsoluteLeft();

                    // Add a little extra space to the right to avoid
                    // problems with IE6/IE7 scrollbars and to make it look
                    // nicer.
                    int extraSpace = 30;

                    boolean overflowRight = false;
                    if (l + +w + extraSpace > browserWindowWidth) {
                        overflowRight = true;
                        // Part of the popup is outside the browser window
                        // (to the right)
                        l = browserWindowWidth - w - extraSpace;
                    }

                    if (t + h + calendarToggle.getOffsetHeight() + 30 > browserWindowHeight) {
                        // Part of the popup is outside the browser window
                        // (below)
                        t = browserWindowHeight - h
                                - calendarToggle.getOffsetHeight() - 30;
                        if (!overflowRight) {
                            // Show to the right of the popup button unless we
                            // are in the lower right corner of the screen
                            l += calendarToggle.getOffsetWidth();
                        }
                    }

                    // fix size
                    popup.setWidth(w + "px");
                    popup.setHeight(h + "px");

                    popup.setPopupPosition(l, t
                            + calendarToggle.getOffsetHeight() + 2);

                    /*
                     * We have to wait a while before focusing since the popup
                     * needs to be opened before we can focus
                     */
                    Timer focusTimer = new Timer() {
                        @Override
                        public void run() {
                            setFocus(true);
                        }
                    };

                    focusTimer.schedule(100);
                }
            });
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event
     * .dom.client.ClickEvent)
     */
    public void onClick(ClickEvent event) {
        if (event.getSource() == calendarToggle) {
            openCalendarPanel();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.event.logical.shared.CloseHandler#onClose(com.google.gwt
     * .event.logical.shared.CloseEvent)
     */
    public void onClose(CloseEvent<PopupPanel> event) {
        if (event.getSource() == popup) {
            buildDate();
            // Sigh.
            Timer t = new Timer() {
                @Override
                public void run() {
                    open = false;
                }
            };
            t.schedule(100);
        }
    }

    /**
     * Sets focus to Calendar panel.
     * 
     * @param focus
     */
    public void setFocus(boolean focus) {
        calendar.setFocus(focus);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.gwt.client.ui.VTextualDate#getFieldExtraWidth()
     */
    @Override
    protected int getFieldExtraWidth() {
        if (fieldExtraWidth < 0) {
            fieldExtraWidth = super.getFieldExtraWidth();
            fieldExtraWidth += calendarToggle.getOffsetWidth();
        }
        return fieldExtraWidth;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.gwt.client.ui.VTextualDate#buildDate()
     */
    @Override
    protected void buildDate() {
        // Save previous value
        String previousValue = getText();
        super.buildDate();

        // Restore previous value if the input could not be parsed
        if (!parsable) {
            setText(previousValue);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.terminal.gwt.client.ui.VDateField#onBrowserEvent(com.google
     * .gwt.user.client.Event)
     */
    @Override
    public void onBrowserEvent(com.google.gwt.user.client.Event event) {
        super.onBrowserEvent(event);
        if (DOM.eventGetType(event) == Event.ONKEYDOWN
                && event.getKeyCode() == getOpenCalenderPanelKey()) {
            openCalendarPanel();
            event.preventDefault();
        }
    }

    /**
     * Get the key code that opens the calendar panel. By default it is the down
     * key but you can override this to be whatever you like
     * 
     * @return
     */
    protected int getOpenCalenderPanelKey() {
        return KeyCodes.KEY_DOWN;
    }

    /**
     * Closes the open popup panel
     */
    public void closeCalendarPanel() {
        if (open) {
            popup.hide(true);
        }
    }

}

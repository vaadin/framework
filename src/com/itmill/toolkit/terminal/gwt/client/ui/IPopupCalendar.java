/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.PopupListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class IPopupCalendar extends ITextualDate implements Paintable,
        ClickListener, PopupListener {

    private final IButton calendarToggle;

    private final CalendarPanel calendar;

    private final ToolkitOverlay popup;
    private boolean open = false;

    public IPopupCalendar() {
        super();

        calendarToggle = new IButton();
        calendarToggle.setText("...");
        calendarToggle.addClickListener(this);
        add(calendarToggle);

        calendar = new CalendarPanel(this);
        popup = new ToolkitOverlay(true, true);
        popup.setStyleName(IDateField.CLASSNAME + "-popup");
        popup.setWidget(calendar);
        popup.addPopupListener(this);

        DOM.setElementProperty(calendar.getElement(), "id",
                "PID_TOOLKIT_POPUPCAL");

    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        super.updateFromUIDL(uidl, client);
        if (date != null) {
            calendar.updateCalendar();
        }
        calendarToggle.setEnabled(enabled);
    }

    public void onClick(Widget sender) {
        if (sender == calendarToggle && !open) {
            open = true;
            calendar.updateCalendar();
            // clear previous values
            popup.setWidth("");
            popup.setHeight("");
            popup.setPopupPositionAndShow(new PositionCallback() {
                public void setPosition(int offsetWidth, int offsetHeight) {
                    final int w = offsetWidth;
                    final int h = offsetHeight;
                    int t = calendarToggle.getAbsoluteTop();
                    int l = calendarToggle.getAbsoluteLeft();
                    if (l + w > Window.getClientWidth()
                            + Window.getScrollLeft()) {
                        l = Window.getClientWidth() + Window.getScrollLeft()
                                - w;
                    }
                    if (t + h > Window.getClientHeight()
                            + Window.getScrollTop()) {
                        t = Window.getClientHeight() + Window.getScrollTop()
                                - h - calendarToggle.getOffsetHeight() - 30;
                        l += calendarToggle.getOffsetWidth();
                    }
                    popup.setPopupPosition(l, t
                            + calendarToggle.getOffsetHeight() + 2);

                    // fix size
                    popup.setWidth(w + "px");
                    popup.setHeight(h + "px");
                    setFocus(true);
                }
            });
        }
    }

    public void onPopupClosed(PopupPanel sender, boolean autoClosed) {
        if (sender == popup) {
            buildDate();
            // Sigh.
            Timer t = new Timer() {
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

}

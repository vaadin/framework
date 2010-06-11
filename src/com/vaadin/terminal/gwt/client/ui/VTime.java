/*
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import java.util.Date;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.ListBox;
import com.vaadin.terminal.gwt.client.BrowserInfo;

public class VTime extends FocusableFlowPanel implements ChangeHandler,
        KeyPressHandler, KeyDownHandler, FocusHandler {

    private final VDateField datefield;

    private final VCalendarPanel calendar;

    private ListBox hours;

    private ListBox mins;

    private ListBox sec;

    private ListBox msec;

    private AMPMListBox ampm;

    private int resolution = VDateField.RESOLUTION_HOUR;

    private boolean readonly;

    /**
     * The AM/PM Listbox yields the keyboard focus to the calendar
     */
    private class AMPMListBox extends ListBox {
        public AMPMListBox() {
            super();
            sinkEvents(Event.ONKEYDOWN);
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

            if (event.getKeyCode() == KeyCodes.KEY_TAB) {
                event.preventDefault();

                /*
                 * Wait until the current event has been processed. If the timer
                 * is left out the focus will move to the VTime-class not the
                 * panel itself. Weird.
                 */
                Timer t = new Timer() {
                    @Override
                    public void run() {
                        calendar.setFocus(true);
                    }
                };
                t.schedule(1);
            }
        }
    }

    /**
     * Constructor
     * 
     * @param parent
     *            The DateField related to this instance
     * @param panel
     *            The panel where this this instance is embedded
     */
    public VTime(VDateField parent, VCalendarPanel panel) {
        super();
        datefield = parent;
        calendar = panel;
        setStyleName(VDateField.CLASSNAME + "-time");

        /*
         * Firefox auto-repeat works correctly only if we use a key press
         * handler, other browsers handle it correctly when using a key down
         * handler
         */
        if (BrowserInfo.get().isGecko()) {
            addKeyPressHandler(this);
        } else {
            addKeyDownHandler(this);
        }

        addFocusHandler(this);
    }

    /**
     * Constructs the ListBoxes and updates their value
     * 
     * @param redraw
     *            Should new instances of the listboxes be created
     */
    private void buildTime(boolean redraw) {
        final boolean thc = datefield.getDateTimeService().isTwelveHourClock();
        if (redraw) {
            clear();
            final int numHours = thc ? 12 : 24;
            hours = new ListBox();
            hours.setStyleName(VNativeSelect.CLASSNAME);
            for (int i = 0; i < numHours; i++) {
                hours.addItem((i < 10) ? "0" + i : "" + i);
            }
            hours.addChangeHandler(this);
            if (thc) {
                ampm = new AMPMListBox();
                ampm.setStyleName(VNativeSelect.CLASSNAME);
                final String[] ampmText = datefield.getDateTimeService()
                        .getAmPmStrings();
                calendar.setFocus(true);
                ampm.addItem(ampmText[0]);
                ampm.addItem(ampmText[1]);
                ampm.addChangeHandler(this);
            }

            if (datefield.getCurrentResolution() >= VDateField.RESOLUTION_MIN) {
                mins = new ListBox();
                mins.setStyleName(VNativeSelect.CLASSNAME);
                for (int i = 0; i < 60; i++) {
                    mins.addItem((i < 10) ? "0" + i : "" + i);
                }
                mins.addChangeHandler(this);
            }
            if (datefield.getCurrentResolution() >= VDateField.RESOLUTION_SEC) {
                sec = new ListBox();
                sec.setStyleName(VNativeSelect.CLASSNAME);
                for (int i = 0; i < 60; i++) {
                    sec.addItem((i < 10) ? "0" + i : "" + i);
                }
                sec.addChangeHandler(this);
            }
            if (datefield.getCurrentResolution() == VDateField.RESOLUTION_MSEC) {
                msec = new ListBox();
                msec.setStyleName(VNativeSelect.CLASSNAME);
                for (int i = 0; i < 1000; i++) {
                    if (i < 10) {
                        msec.addItem("00" + i);
                    } else if (i < 100) {
                        msec.addItem("0" + i);
                    } else {
                        msec.addItem("" + i);
                    }
                }
                msec.addChangeHandler(this);
            }

            final String delimiter = datefield.getDateTimeService()
                    .getClockDelimeter();
            final boolean ro = datefield.isReadonly();

            if (ro) {
                int h = 0;
                if (datefield.getCurrentDate() != null) {
                    h = datefield.getCurrentDate().getHours();
                }
                if (thc) {
                    h -= h < 12 ? 0 : 12;
                }
                add(new VLabel(h < 10 ? "0" + h : "" + h));
            } else {
                add(hours);
            }

            if (datefield.getCurrentResolution() >= VDateField.RESOLUTION_MIN) {
                add(new VLabel(delimiter));
                if (ro) {
                    final int m = mins.getSelectedIndex();
                    add(new VLabel(m < 10 ? "0" + m : "" + m));
                } else {
                    add(mins);
                }
            }
            if (datefield.getCurrentResolution() >= VDateField.RESOLUTION_SEC) {
                add(new VLabel(delimiter));
                if (ro) {
                    final int s = sec.getSelectedIndex();
                    add(new VLabel(s < 10 ? "0" + s : "" + s));
                } else {
                    add(sec);
                }
            }
            if (datefield.getCurrentResolution() == VDateField.RESOLUTION_MSEC) {
                add(new VLabel("."));
                if (ro) {
                    final int m = datefield.getMilliseconds();
                    final String ms = m < 100 ? "0" + m : "" + m;
                    add(new VLabel(m < 10 ? "0" + ms : ms));
                } else {
                    add(msec);
                }
            }
            if (datefield.getCurrentResolution() == VDateField.RESOLUTION_HOUR) {
                add(new VLabel(delimiter + "00")); // o'clock
            }
            if (thc) {
                add(new VLabel("&nbsp;"));
                if (ro) {
                    add(new VLabel(ampm.getItemText(datefield.getCurrentDate()
                            .getHours() < 12 ? 0 : 1)));
                } else {
                    add(ampm);
                }
            }

            if (ro) {
                return;
            }
        }

        // Update times
        Date cdate = datefield.getShowingDate();
        boolean selected = true;
        if (cdate == null) {
            cdate = new Date();
            selected = false;
        }
        if (thc) {
            int h = cdate.getHours();
            ampm.setSelectedIndex(h < 12 ? 0 : 1);
            h -= ampm.getSelectedIndex() * 12;
            hours.setSelectedIndex(h);
        } else {
            hours.setSelectedIndex(cdate.getHours());
        }
        if (datefield.getCurrentResolution() >= VDateField.RESOLUTION_MIN) {
            mins.setSelectedIndex(cdate.getMinutes());
        }
        if (datefield.getCurrentResolution() >= VDateField.RESOLUTION_SEC) {
            sec.setSelectedIndex(cdate.getSeconds());
        }
        if (datefield.getCurrentResolution() == VDateField.RESOLUTION_MSEC) {
            if (selected) {
                msec.setSelectedIndex(datefield.getMilliseconds());
            } else {
                msec.setSelectedIndex(0);
            }
        }
        if (thc) {
            ampm.setSelectedIndex(cdate.getHours() < 12 ? 0 : 1);
        }

        if (datefield.isReadonly() && !redraw) {
            // Do complete redraw when in read-only status
            clear();
            final String delimiter = datefield.getDateTimeService()
                    .getClockDelimeter();

            int h = cdate.getHours();
            if (thc) {
                h -= h < 12 ? 0 : 12;
            }
            add(new VLabel(h < 10 ? "0" + h : "" + h));

            if (datefield.getCurrentResolution() >= VDateField.RESOLUTION_MIN) {
                add(new VLabel(delimiter));
                final int m = mins.getSelectedIndex();
                add(new VLabel(m < 10 ? "0" + m : "" + m));
            }
            if (datefield.getCurrentResolution() >= VDateField.RESOLUTION_SEC) {
                add(new VLabel(delimiter));
                final int s = sec.getSelectedIndex();
                add(new VLabel(s < 10 ? "0" + s : "" + s));
            }
            if (datefield.getCurrentResolution() == VDateField.RESOLUTION_MSEC) {
                add(new VLabel("."));
                final int m = datefield.getMilliseconds();
                final String ms = m < 100 ? "0" + m : "" + m;
                add(new VLabel(m < 10 ? "0" + ms : ms));
            }
            if (datefield.getCurrentResolution() == VDateField.RESOLUTION_HOUR) {
                add(new VLabel(delimiter + "00")); // o'clock
            }
            if (thc) {
                add(new VLabel("&nbsp;"));
                add(new VLabel(ampm.getItemText(cdate.getHours() < 12 ? 0 : 1)));
            }
        }

        final boolean enabled = datefield.isEnabled();
        hours.setEnabled(enabled);
        if (mins != null) {
            mins.setEnabled(enabled);
        }
        if (sec != null) {
            sec.setEnabled(enabled);
        }
        if (msec != null) {
            msec.setEnabled(enabled);
        }
        if (ampm != null) {
            ampm.setEnabled(enabled);
        }

    }

    /**
     * Update the time ListBoxes
     * 
     * @param redraw
     *            Should new instances of the listboxes be created
     */
    public void updateTime(boolean redraw) {
        buildTime(redraw || resolution != datefield.getCurrentResolution()
                || readonly != datefield.isReadonly());
        if (datefield instanceof VTextualDate) {
            ((VTextualDate) datefield).buildDate();
        }
        resolution = datefield.getCurrentResolution();
        readonly = datefield.isReadonly();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.event.dom.client.ChangeHandler#onChange(com.google.gwt
     * .event.dom.client.ChangeEvent)
     */
    public void onChange(ChangeEvent event) {
        if (datefield.getCurrentDate() == null) {
            // was null on server, need to set
            Date now = datefield.getShowingDate();
            if (now == null) {
                now = new Date();
                datefield.setShowingDate(now);
            }
            datefield.setCurrentDate(new Date(now.getTime()));

            // Init variables with current time
            notifyServerOfChanges();
        }
        if (event.getSource() == hours) {
            int h = hours.getSelectedIndex();
            if (datefield.getDateTimeService().isTwelveHourClock()) {
                h = h + ampm.getSelectedIndex() * 12;
            }
            datefield.getShowingDate().setHours(h);
            updateTime(false);
        } else if (event.getSource() == mins) {
            final int m = mins.getSelectedIndex();
            datefield.getShowingDate().setMinutes(m);
            updateTime(false);
        } else if (event.getSource() == sec) {
            final int s = sec.getSelectedIndex();
            datefield.getShowingDate().setSeconds(s);
            updateTime(false);
        } else if (event.getSource() == msec) {
            final int ms = msec.getSelectedIndex();
            datefield.setShowingMilliseconds(ms);
            updateTime(false);
        } else if (event.getSource() == ampm) {
            final int h = hours.getSelectedIndex()
                    + (ampm.getSelectedIndex() * 12);
            datefield.getShowingDate().setHours(h);
            updateTime(false);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.event.dom.client.KeyPressHandler#onKeyPress(com.google
     * .gwt.event.dom.client.KeyPressEvent)
     */
    public void onKeyPress(KeyPressEvent event) {
        int keycode = event.getNativeEvent().getKeyCode();

        if (calendar != null) {
            if (keycode == calendar.getSelectKey()
                    || keycode == calendar.getCloseKey()) {
                if (keycode == calendar.getSelectKey()) {
                    notifyServerOfChanges();
                }

                calendar.handleNavigation(keycode, event.getNativeEvent()
                        .getCtrlKey()
                        || event.getNativeEvent().getMetaKey(), event
                        .getNativeEvent().getShiftKey());
                return;
            }
        }

        event.stopPropagation();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.event.dom.client.KeyDownHandler#onKeyDown(com.google.gwt
     * .event.dom.client.KeyDownEvent)
     */
    public void onKeyDown(KeyDownEvent event) {
        int keycode = event.getNativeEvent().getKeyCode();
        if (keycode != calendar.getCloseKey()
                && keycode != calendar.getSelectKey()) {
            event.stopPropagation();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.event.dom.client.FocusHandler#onFocus(com.google.gwt.event
     * .dom.client.FocusEvent)
     */
    public void onFocus(FocusEvent event) {
        event.preventDefault();

        // Delegate focus to the hour select
        hours.setFocus(true);
    }

    /**
     * Update the variables server side
     */
    public void notifyServerOfChanges() {
        /*
         * Just update the variables, don't send any thing. The calendar panel
         * will make the request when the panel is closed.
         */
        Date now = datefield.getCurrentDate();
        datefield.getClient().updateVariable(datefield.getId(), "year",
                now.getYear() + 1900, false);
        datefield.getClient().updateVariable(datefield.getId(), "month",
                now.getMonth() + 1, false);
        datefield.getClient().updateVariable(datefield.getId(), "day",
                now.getDate(), false);
        datefield.getClient().updateVariable(datefield.getId(), "hour",
                now.getHours(), false);
        datefield.getClient().updateVariable(datefield.getId(), "min",
                now.getMinutes(), false);
        datefield.getClient().updateVariable(datefield.getId(), "sec",
                now.getSeconds(), false);
        datefield.getClient().updateVariable(datefield.getId(), "msec",
                datefield.getShowingMilliseconds(), false);
    }

}

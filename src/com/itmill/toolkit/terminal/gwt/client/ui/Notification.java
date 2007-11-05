package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventPreview;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class Notification extends PopupPanel {

	public static final int CENTERED = 1;
	public static final int CENTERED_TOP = 2;
	public static final int CENTERED_BOTTOM = 3;
	public static final int TOP_LEFT = 4;
	public static final int TOP_RIGHT = 5;
	public static final int BOTTOM_LEFT = 6;
	public static final int BOTTOM_RIGHT = 7;

	public static final int DELAY_FOREVER = -1;
	public static final int DELAY_NONE = 0;

	private static final String STYLENAME = "i-Notification";
	private static final int mouseMoveThreshold = 7;
	private static final int Z_INDEX_BASE = 20000;

	private int startOpacity = 90;
	private int fadeMsec = 400;
	private int delayMsec = 1000;

	private Timer fader;
	private Timer delay;
	private EventPreview eventPreview;

	private String temporaryStyle;

	public Notification() {
		setStylePrimaryName(STYLENAME);
		sinkEvents(Event.ONCLICK);
		DOM.setStyleAttribute(getElement(), "zIndex", "" + Z_INDEX_BASE);
	}

	public Notification(int delayMsec) {
		this();
		this.delayMsec = delayMsec;
	}

	public Notification(int delayMsec, int fadeMsec, int startOpacity) {
		this(delayMsec);
		this.fadeMsec = fadeMsec;
		this.startOpacity = startOpacity;
	}

	public void startDelay() {
		DOM.removeEventPreview(eventPreview);
		if (delayMsec > 0) {
			delay = new Timer() {
				public void run() {
					fade();
				}
			};
			delay.scheduleRepeating(delayMsec);
		} else if (delayMsec == 0) {
			fade();
		}
	}

	public void show() {
		show(CENTERED);
	}

	public void show(String style) {
		show(CENTERED, style);
	}

	public void show(int position) {
		show(position, null);
	}

	public void show(Widget widget, int position, String style) {
		setWidget(widget);
		show(position, style);
	}

	public void show(String html, int position, String style) {
		setWidget(new HTML(html));
		show(position, style);
	}

	public void show(int position, String style) {
		hide();
		setOpacity(getElement(), startOpacity);
		if (style != null) {
			this.temporaryStyle = style;
			addStyleName(style);
		}
		super.show();
		setPosition(position);

		if (eventPreview == null) {
			eventPreview = new EventPreview() {
				int x = -1;
				int y = -1;

				public boolean onEventPreview(Event event) {
					switch (DOM.eventGetType(event)) {
					case Event.ONMOUSEMOVE:
						if (x < 0) {
							x = DOM.eventGetClientX(event);
							y = DOM.eventGetClientY(event);
						} else if (Math.abs(DOM.eventGetClientX(event) - x) > mouseMoveThreshold
								|| Math.abs(DOM.eventGetClientY(event) - y) > mouseMoveThreshold) {
							startDelay();
						}
						break;
					case Event.KEYEVENTS:
					case Event.ONCLICK:
					case Event.ONDBLCLICK:
					case Event.ONSCROLL:
					default:
						startDelay();
					}
					return true;
				}
			};
		}

		DOM.addEventPreview(eventPreview);
	}

	public void hide() {
		DOM.removeEventPreview(eventPreview);
		cancelDelay();
		cancelFade();
		if (this.temporaryStyle != null) {
			removeStyleName(this.temporaryStyle);
			this.temporaryStyle = null;
		}
		super.hide();
	}

	public void fade() {
		cancelDelay();
		fader = new Timer() {
			int opacity = startOpacity;

			public void run() {
				opacity -= 5;
				setOpacity(getElement(), opacity);
				if (opacity <= 0) {
					cancel();
					hide();
				}
			}
		};
		int msec = fadeMsec / (startOpacity / 5);
		fader.scheduleRepeating(msec);
	}

	public void setPosition(int position) {
		Element el = getElement();
		DOM.setStyleAttribute(el, "top", null);
		DOM.setStyleAttribute(el, "left", null);
		DOM.setStyleAttribute(el, "bottom", null);
		DOM.setStyleAttribute(el, "right", null);
		switch (position) {
		case TOP_LEFT:
			DOM.setStyleAttribute(el, "top", "0px");
			DOM.setStyleAttribute(el, "left", "0px");
			break;
		case TOP_RIGHT:
			DOM.setStyleAttribute(el, "top", "0px");
			DOM.setStyleAttribute(el, "right", "0px");
			break;
		case BOTTOM_RIGHT:
			DOM.setStyleAttribute(el, "position", "absolute");
			DOM.setStyleAttribute(el, "bottom", "0px");
			DOM.setStyleAttribute(el, "right", "0px");
			break;
		case BOTTOM_LEFT:
			DOM.setStyleAttribute(el, "bottom", "0px");
			DOM.setStyleAttribute(el, "left", "0px");
			break;
		case CENTERED_TOP:
			center();
			DOM.setStyleAttribute(el, "top", "0px");
			break;
		case CENTERED_BOTTOM:
			center();
			DOM.setStyleAttribute(el, "top", null);
			DOM.setStyleAttribute(el, "bottom", "0px");
			break;
		default:
		case CENTERED:
			center();
			break;
		}
	}

	private void cancelFade() {
		if (fader != null) {
			fader.cancel();
			fader = null;
		}
	}

	private void cancelDelay() {
		if (delay != null) {
			delay.cancel();
			delay = null;
		}
	}

	private void setOpacity(Element el, int opacity) {
		DOM.setStyleAttribute(el, "opacity", "" + (opacity / 100.0));
		DOM.setStyleAttribute(el, "filter", "Alpha(opacity=" + opacity + ")");

	}

	public void onBrowserEvent(Event event) {
		DOM.removeEventPreview(eventPreview);
		if (fader == null) {
			fade();
		}
	}

}

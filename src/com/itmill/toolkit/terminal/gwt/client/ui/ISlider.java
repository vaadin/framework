package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.HashSet;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.SourcesMouseEvents;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class ISlider extends Widget implements Paintable {
	
	public static final String CLASSNAME = "i-slider";
	
	ApplicationConnection client;
	
	String id;
	
	private boolean immediate;
	private boolean disabled;
	private boolean readonly;
	
	private int handleSize;
	private float min;
	private float max;
	private int resolution;
	private Object value;
	private HashSet values;
	private boolean vertical;
	private int size = -1;
	
	/* DOM element for slider's base */
	private Element base;
	
	/* DOM element for slider's handle */
	private Element handle;
	
	private boolean dragging;
	
	public ISlider() {
		super();
		setElement(DOM.createElement("div"));
		base = DOM.createElement("div");
		DOM.appendChild(getElement(), base);
		handle = DOM.createElement("div");
		DOM.appendChild(base, handle);
		setStyleName(CLASSNAME);
		DOM.setAttribute(base, "className", CLASSNAME+"-base");
		DOM.setAttribute(handle, "className", CLASSNAME+"-handle");
		
		DOM.sinkEvents(base, Event.MOUSEEVENTS);
		DOM.sinkEvents(handle, Event.MOUSEEVENTS);
	}

	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		
		this.client = client;
		
		// Ensure correct implementation (handle own caption)
		if (client.updateComponent(this, uidl, false))
			return;
		
		immediate = uidl.getBooleanAttribute("immediate");
		disabled = uidl.getBooleanAttribute("disabled");
		readonly = uidl.getBooleanAttribute("readonly");
		
		vertical = uidl.hasAttribute("vertical");
		
		if(vertical)
			addStyleName(CLASSNAME+"-vertical");
		else
			removeStyleName(CLASSNAME+"-vertical");
		
		if(uidl.hasAttribute("values")) {
			values = uidl.getStringArrayAttributeAsSet("values");
			value = uidl.getStringVariable("value");
		} else {
			min = uidl.getLongAttribute("min");
			max = uidl.getLongAttribute("max");
			resolution = uidl.getIntAttribute("resolution");
			value = new Float(uidl.getFloatVariable("value"));
			values = null;
		}
		
		handleSize = uidl.getIntAttribute("hsize");
		
		if(uidl.hasAttribute("size"))
			size = uidl.getIntAttribute("size");
		
		buildBase();
		
		if(!vertical) {
			// Draw handle with a delay to allow base to gain maximum width
			Timer delay = new Timer() {
				public void run() {
					buildHandle();
					setHandlePosition(value);
				}
			};
			delay.schedule(100);
		} else {
			buildHandle();
			setHandlePosition(value);
		}
	}

	private void buildBase() {
		if(vertical) {
			if(size > -1)
				DOM.setStyleAttribute(base, "height", size + "px");
			else DOM.setStyleAttribute(base, "height", "120px");
		} else {
			if(size > -1)
				DOM.setStyleAttribute(base, "width", size + "px");
			else DOM.setStyleAttribute(base, "width", "100%");
		}
		// Allow absolute positioning of handle
		DOM.setStyleAttribute(base, "position", "relative");
		
		// TODO attach listeners for clicking on base, focusing and arrow keys
	}
	
	private void buildHandle() {
		// Allow absolute positioning
		DOM.setStyleAttribute(handle, "position", "absolute");
		
		if(vertical) {
			// TODO
		} else {
			int t = Integer.parseInt(DOM.getAttribute(base, "offsetHeight")) - Integer.parseInt(DOM.getAttribute(handle, "offsetHeight"));
			DOM.setStyleAttribute(handle, "top", (t/2)+"px");
			DOM.setStyleAttribute(handle, "left", "0px");
			int w = (int) (Float.parseFloat(DOM.getAttribute(base, "offsetWidth")) / 100 * handleSize);
			DOM.setStyleAttribute(handle, "width", w+"px");
		}
		
	}
	
	private void setHandlePosition(Object value) {
		if(vertical) {
			// TODO
		} else {
			if(values == null) {
				int handleWidth = Integer.parseInt(DOM.getAttribute(handle, "offsetWidth"));
				int baseWidth = Integer.parseInt(DOM.getAttribute(base, "offsetWidth"));
				int range = baseWidth - handleWidth;
				float v = ((Float)value).floatValue();
				float valueRange = max - min;
				float pos = range * ((v - min) / valueRange);
				DOM.setStyleAttribute(handle, "left", pos+"px");
				DOM.setAttribute(handle, "title", ""+v);
			}
		}
		this.value = value;
	}

	public void onBrowserEvent(Event event) {
		if(DOM.compare(DOM.eventGetTarget(event), handle))
			processHandleEvent(event);
		else
			processBaseEvent(event);
		
		super.onBrowserEvent(event);
	}
	
	private void processHandleEvent(Event event) {
		switch (DOM.eventGetType(event)) {
		case Event.ONMOUSEDOWN:
			client.console.log("Slider handle: mousedown");
			if(!disabled && !readonly) {
				dragging = true;
				DOM.setCapture(handle);
				DOM.eventPreventDefault(event); // prevent selecting text
			}
			break;
		case Event.ONMOUSEMOVE:
			if (dragging) {
				client.console.log("Slider handle: dragging...");
				int x = DOM.eventGetClientX(event);
				int y = DOM.eventGetClientY(event);
				if(vertical) {
					// TODO
				} else {
					if(values == null) {
						float handleW = Integer.parseInt(DOM.getAttribute(handle, "offsetWidth"));
						float baseX = DOM.getAbsoluteLeft(base);
						float baseW = Integer.parseInt(DOM.getAttribute(base, "offsetWidth"));
						float v = ((x-baseX)/(baseW-baseX)) * (max-min) + min;
						if(resolution > 0) {
							setHandlePosition(new Float(v));
						} else
							setHandlePosition(new Float((int)v));
					} else {
						// TODO
					}
				}
			}
			break;
		case Event.ONMOUSEUP:
			dragging = false;
			DOM.releaseCapture(handle);
			break;
		default:
			break;
		}
	}
		
	private void processBaseEvent(Event event) {
		// TODO
		super.onBrowserEvent(event);
	}

}
